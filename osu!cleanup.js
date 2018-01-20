const fs = require("fs");
const readlineSync = require("readline-sync");
const deleteEmpty = require("delete-empty");
const logFile = "./cleanup.log";

const filesInExeDir = fs.readdirSync("./");
let logFileAlreadyExists = false;
let logFiles = [];

//if cleanup.log already exists, name it log1. If log1 exists, name it log2 etc
for (let i = 0; i < filesInExeDir.length; i++) {
    if (filesInExeDir[i].startsWith(logFile.slice(2))) {
        logFiles.push(filesInExeDir[i]);
        //only necessary to move, if we would overwrite
        if (filesInExeDir[i] === logFile.slice(2))
            logFileAlreadyExists = true;
    }

}
if (logFileAlreadyExists) {
    //start with the last one, so we don't overwrite files
    for (let i = logFiles.length - 1; i >= 0; i--) {
        fs.renameSync(logFiles[i], logFile + (i + 1));
    }
}
var logger = fs.createWriteStream(logFile);

process.stdout.write("Programm written by earlopain. It should probalby work," +
    "but there is always a chance that something breaks. I recommend to " +
    "make a copy of your songs folder, just to be sure. Kind of defeats the" +
    " purpose of this tool, but at least I warned you. If you have many beatmaps, " +
    "this will probably take a while. Some stats:\n" +
    "Different mapsets:     15,457    Time:         91s (on an ssd)\n" +
    "Files before:          356,741   Size before:  137GB\n" +
    "Files after:           93,497    Size after:   68.27GB\n" +
    "Difference:            263,244   Difference:   68.73GB\n\n\n");
//Do not delete files if true
let dryRun = true;
if (readlineSync.keyInYN("The program defaults to not deleting files. It will only show you what would have happened. Press y if you want to delete the files\n")) {
    dryRun = false;
}
process.stdout.write("You selected " + (dryRun ? "only watch" : "to clean up your songs folder"));

let validFolder = false;
while (!validFolder) {
    rootFolder = readlineSync.question("\nPlease enter you osu! songs folder\n");
    //replaces \ with / to easier work with the path later
    rootFolder = rootFolder.replace(/\\/g, "/");
    try {
        //don't let users input the working directory with this shortcut, let them tpye out the directory
        if (rootFolder === ".")
            throw new Error();
        //errors if not a dir, retry
        if (fs.statSync(rootFolder).isDirectory)
            validFolder = true;
    } catch (error) {
        process.stdout.write("This is either not a valid folder or you don't have permissions\n");
    }
}
//append / if not already there
if (!rootFolder.endsWith("/"))
    rootFolder = rootFolder + "/";

try {
    //check if parent dir contains osu.exe, if not notice the user of it
    let osuExePath = "";
    const splitted = rootFolder.split("/");
    //-2 because the path is of this format: D:/osu!/Songs/, we want D:/osu!/
    for (let i = 0; i < splitted.length - 2; i++) {
        osuExePath += splitted[i] + "/";
    }
    osuExePath += "osu!.exe";
    fs.existsSync(osuExePath);
} catch (error) {
    process.stdout.write("osu!.exe was not found one directory lower in your folder. This is ok if your songs folder is not in your osu! folder");
    if (!dryRun)
        process.stdout.write("\nAre you ABSOLUTLY sure you got the right folder?\n" +
            "You may loose important files, they will not apear in your recycle bin");

    if (!readlineSync.keyInYN("\nDid you select the right folder?")) {
        process.exit(1);
    }
}
//only notice if it is not a test run
if (!dryRun && !readlineSync.keyInYN("\n\nAre you sure you want to do this? There may be errors. Consider making a backup\n")) {
    process.exit(1);
}

logger.write("root folder: " + rootFolder);
logger.write("\r\nGetting mapsets...");

let foldersAndFiles = fs.readdirSync(rootFolder);
logger.write("\r\nFinished!");
logger.write("\r\nSorting out files...");
let songs = [];
//remove everything which is not a folder
foldersAndFiles.forEach(element => {
    if (fs.statSync(rootFolder + element).isDirectory())
        songs.push(rootFolder + element);
    else {
        logger.write("\r\nNot a mapset: " + rootFolder + element);
    }
});
logger.write("\r\nFinished!");
let percentage = 0;
let counter = 0;

let totalFiles = 0;
let deletedFiles = 0;
let spaceSaved = 0;

const startTime =
    Date.now() / 1000;
let previousWrite = "";

logger.write("\r\n\r\nStart itterating over the mapsets...");
try {
    songs.forEach(folder => {
        logger.write("\r\n\r\nParsing " + folder);
        counter++;
        //percentage with one decimal and seconds since start, if something changes, update progress
        const writing = "" + Number.parseFloat(counter / songs.length * 100).toFixed(1) + "% " + (Math.round(Date.now() / 1000 - startTime)) + "s";
        if (writing !== previousWrite) {
            process.stdout.clearLine();
            process.stdout.cursorTo(0);
            process.stdout.write(writing);
            previousWrite = writing;
        }
        let filesToDelete = getAllFilesInFolder(folder);
        totalFiles += filesToDelete.length;
        const dotOsuFiles = getOsuFiles(filesToDelete)

        //Files as they appear in the .osu file
        let originalSoundFiles = [];
        let originalBackgrounds = [];


        //files as they are one the disk
        let uniqueSoundFiles = [];
        let uniqueBackgrounds = [];

        for (let i = 0; i < dotOsuFiles.length; i++) {
            const content = fs.readFileSync(dotOsuFiles[i], "utf8");
            const lines = content.split("\r\n");

            let backgroundImage;
            let soundFile;
            //are you in the general area? Contains music file
            let inGeneral = false;
            //are you in the events area? Contains background file
            let inEvents = false;
            //did the file get found?
            let generalFinished = false;
            let eventsFinsished = false;
            //matches something in quotes ending with picture ext, like 0,0,"background.jpg"
            //only picture, because mp4 etc also get startet like this 
            const regex = /"([^"])*\.jpg"|"([^"])*\.jpeg"|"([^"])*\.png"/g;
            //look through every line
            for (let j = 0; j < lines.length; j++) {
                //if both sound and background are found, no need to check further
                if (eventsFinsished && generalFinished) {
                    break;
                }

                if (inEvents) {
                    //did not find anything, stop looking
                    if (lines[j].charAt[0] === "[") {
                        inEvents = false;
                        eventsFinsished = true;
                    }

                    let m;
                    while ((m = regex.exec(lines[j])) !== null) {
                        if (m.index === regex.lastIndex) {
                            regex.lastIndex++;
                        }
                        //remove the quotes
                        backgroundImage = folder + "/" + m[0].slice(1, -1);

                        //did it already get added to the list? Do not add it a second time
                        if (originalBackgrounds.indexOf(backgroundImage) === -1) {
                            originalBackgrounds.push(backgroundImage);
                            backgroundImage = checkCapitalization(backgroundImage, filesToDelete);
                            uniqueBackgrounds.push(backgroundImage);
                            logger.write("\r\nFound background " + m[0].slice(1, -1));
                        }
                        inEvents = false;
                    }
                }

                if (inGeneral) {
                    //not found, stop looking
                    if (lines[j].charAt[0] === "[") {
                        inGeneral = false;
                        generalFinished = true;
                    }
                    //splits string into two parts, one everyting before the first :, the second everything after
                    const splitted = lines[j].split(/:(.+)/);
                    if (splitted[0] === "AudioFilename") {
                        //trimm first space
                        soundFile = folder + "/" + splitted[1].substr(1);
                        //already in it, don't add again
                        if (originalSoundFiles.indexOf(soundFile) === -1) {
                            originalSoundFiles.push(soundFile);
                            soundFile = checkCapitalization(soundFile, filesToDelete);
                            uniqueSoundFiles.push(soundFile);
                            logger.write("\r\nFound soundFile " + splitted[1].substr(1));
                        }
                        inGeneral = false;
                    }
                }

                if (lines[j] === "[General]")
                    inGeneral = true;
                if (lines[j] === "[Events]")
                    inEvents = true;
            }
        }
        //check for *.osu from one folder finished
        if (uniqueBackgrounds.length === 0) {
            logger.write("\r\nNo background found");
        }

        if (uniqueSoundFiles.length === 0) {
            logger.write("\r\nNot deleting, no sound found");
        } else {
            //removes the content of the second from the first
            filesToDelete = filterArray(filesToDelete, uniqueBackgrounds);
            filesToDelete = filterArray(filesToDelete, uniqueSoundFiles);
            filesToDelete = filterArray(filesToDelete, dotOsuFiles);
            //delete files, if mode !testrun
            for (let i = 0; i < filesToDelete.length; i++) {
                const stats = fs.statSync(filesToDelete[i]);
                logger.write("\r\nDeleting " + filesToDelete[i].split(folder + "/")[1]);
                if (!dryRun)
                    fs.unlinkSync(filesToDelete[i]);
                spaceSaved += stats.size;
            }

            deletedFiles += filesToDelete.length;
        }
    });
    //split in two functions, first contains error prone code, so I can easily wrap it in try catch
    nextStep();
} catch (error) {
    logger.write("\r\n\r\n\r\n" + error.stack);

    logger.on("close", function () {
        process.stdout.write("\r\nPress any key to exit");
        process.stdin.setRawMode(true);
        process.stdin.resume();
        process.stdin.on("data", process.exit.bind(process, 0));
    });

    process.stdout.write("\r\n" + error.stack);
    process.stdout.write("\r\n\r\nSorry about that, please send me the logfile so I can fix the problem");
    process.stdout.write("\r\n\r\nWriting logfile...\n");
    logger.end();
}

function nextStep() {
    process.stdout.write("\nDeleting empty folders...\n");
    if (!dryRun) {
        const orphaned = deleteEmpty.sync(rootFolder, { verbose: false });
        let temp = "";
        for (let i = 0; i < orphaned.length; i++) {
            temp += "\r\nRemoved empty dir " + orphaned[i];
        }
        logger.write(temp);
    }


    logger.write("\r\n\r\nTotal: " + totalFiles + ", deleted " + deletedFiles + ", saved: " + humanFileSize(spaceSaved) + ", runtime: " + Math.round(Date.now() / 1000 - startTime) + "s");
    process.stdout.write("\n\nFinished! From a total of " + totalFiles + " files you deleted " + deletedFiles + ", saving you " + humanFileSize(spaceSaved) + ".\n");
    process.stdout.write("And it only took you " + (Math.round(Date.now() / 1000 - startTime) < 180 ? Math.round(Date.now() / 1000 - startTime) + " seconds" : (Math.round(Math.round(Date.now() / 1000 - startTime) / 60)) + " minutes"));
    process.stdout.write("\nThanks for using this tool.");
    process.stdout.write("\nWriting logfile...");

    logger.on("close", function () {
        process.stdout.write("\n\nFinished. Press any key to exit\n");
        process.stdin.setRawMode(true);
        process.stdin.resume();
        process.stdin.on("data", process.exit.bind(process, 0));
    });
    logger.end();
}

//fs.unlink doesn't care about capitalization, while fs.exists does, which means every file need to be checked
function checkCapitalization(file, filesToDelete) {
    const fileLowerCase = file.toLowerCase();
    //make both lowercase, if the same take the one with the name from disk
    for (let i = 0; i < filesToDelete.length; i++) {
        if (fileLowerCase === filesToDelete[i].toLowerCase() && file !== filesToDelete[i]) {
            logger.write("\nCapitalzisation inconsistent: " + filesToDelete[i]);
            return filesToDelete[i];
        }
    }
    return file;
}

function humanFileSize(size) {
    var i = Math.floor(Math.log(size) / Math.log(1024));
    return (size / Math.pow(1024, i)).toFixed(2) * 1 + " " + ["B", "kB", "MB", "GB", "TB"][i];
};

function getOsuFiles(files) {
    let results = [];
    for (let i = 0; i < files.length; i++) {
        //add files ending in .osu to results
        if (files[i].split(".")[files[i].split(".").length - 1] === "osu") {
            results.push(files[i]);
        }
    }
    return results;
}
//remove content of second from first
function filterArray(original, remove) {
    return original.filter(function (x) {
        return remove.indexOf(x) < 0;
    })
}

//Doesn't push dirs to results
function getAllFilesInFolder(dir) {
    let results = [];
    let list = fs.readdirSync(dir);
    list.forEach(function (file) {
        file = dir + "/" + file;
        const stat = fs.statSync(file);
        if (stat && stat.isDirectory()) results = results.concat(getAllFilesInFolder(file));
        else results.push(file);
    })
    return results;
}