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

process.stdout.write("v1.2 21.1.18 06:48PM\n" +
    "Programm written by earlopain. It should probalby work," +
    "but there is always a chance that something breaks. I recommend to " +
    "make a copy of your songs folder, just to be sure. Kind of defeats the" +
    " purpose of this tool, but at least I warned you. If you have many beatmaps, " +
    "this will probably take a while. Some stats:\n" +
    "Different mapsets:     15,457    Time:         91s (on an ssd)\n" +
    "Files before:          356,741   Size before:  137GB\n" +
    "Files after:           93,497    Size after:   68.27GB\n" +
    "Difference:            263,244   Difference:   68.73GB\n\n");
//Do not delete files if true
let dryRun = true;
if (readlineSync.keyInYN("\nThe program defaults to not deleting files. It will only show you what would have happened. Press y if you want to delete the files")) {
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
        process.stdout.write("\nThis is either not a valid folder or you don't have permissions");
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
    fs.statSync(osuExePath);
} catch (error) {
    process.stdout.write("\nosu!.exe was not found one directory lower in your folder. This is ok if your songs folder is not in your osu! folder");
    if (!dryRun)
        process.stdout.write("\nAre you ABSOLUTLY sure you got the right folder?\n" +
            "You may loose important files, they will not apear in your recycle bin");

    if (!readlineSync.keyInYN("\nDid you select the right folder?")) {
        process.exit(1);
    }
}
let gamemodesToDelete = [];
const validGamemodesString = ["standart", "taiko", "ctb", "mania"];
if (readlineSync.keyInYN("Do you want to also remove gamemode specific maps?")) {
    let validGamemodes = false;
    let first = true;
    while (!validGamemodes) {
        gamemodesToDelete = [];
        let toDelete;
        //if else , so you don't always see the question if you mistyped or something
        if (first)
            toDelete = readlineSync.question("Please write either standart, ctb, taiko or mania.\nIf you want to delete more than one, divide them like ctb;taiko;mania\n");
        else
            toDelete = readlineSync.question("");
        if (toDelete.endsWith(";"))
            toDelete = toDelete.slice(0, -1);

        const gamemodes = toDelete.split(";");
        for (let i = 0; i < gamemodes.length; i++) {
            if (validGamemodesString.indexOf(gamemodes[i]) === -1) {
                process.stdout.write(gamemodes[i] + " doesn't seem to be a valid gamemode, please try again");
                validGamemodes = false;
                first = false;
                break;
            }
            gamemodesToDelete.push(gamemodes[i]);
            validGamemodes = true;
        }
        if (gamemodesToDelete.length >= validGamemodesString.length) {
            process.stdout.write("\nDeleting all gamemodes seems a bit overkill, I'm mean you can just delete the whole folder, what do you need this tool for?");
            break;
        }
        log("Chose to delete gamemodes " + gamemodesToDelete);
    }
}

let keepHitsounds = false;
if (readlineSync.keyInYN("Do you want to keep hitsounds?")) {
    process.stdout.write("You chose to keep hitsounds\n");
    keepHitsounds = true;
    log("User chose to keep hitsounds");
}
//only notice if it is not a test run
if (!dryRun && !readlineSync.keyInYN("\n\nAre you sure you want to do this? There may be errors. Consider making a backup")) {
    process.exit(1);
}

log("root folder: " + rootFolder);
log("Getting mapsets...");

let foldersAndFiles = fs.readdirSync(rootFolder);
log("Finished!");
log("Sorting out files...");
let songs = [];
//remove everything which is not a folder
foldersAndFiles.forEach(element => {
    if (fs.statSync(rootFolder + element).isDirectory())
        songs.push(rootFolder + element);
    else {
        log("Not a mapset: " + rootFolder + element);
    }
});
log("Finished!");
let percentage = 0;
let counter = 0;

let totalFiles = 0;
let deletedFiles = 0;
let spaceSaved = 0;

const startTime = Date.now() / 1000;
//used to see if you need to update the progress indicator, if different from the new one write it
let previousWrite = "";

log("Start itterating over the mapsets...", 2);
try {
    songs.forEach(folder => {
        log("Parsing " + folder, 1);
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
        let unwishedGamemodeFiles = [];

        for (let i = 0; i < dotOsuFiles.length; i++) {
            const lines = fs.readFileSync(dotOsuFiles[i], "utf8").split("\r\n");

            let gamemode;
            //did the user selecte to delete gamemodes?
            if (gamemodesToDelete.length > 0) {
                gamemode = getPropertyFormDotOsu(dotOsuFiles[i], "General", "Mode");
                if (gamemodesToDelete.indexOf(validGamemodesString[gamemode]) !== -1) {
                    log(validGamemodesString[gamemode] + ": " + dotOsuFiles[i] + ", removing");
                    unwishedGamemodeFiles.push(dotOsuFiles[i]);
                    //stop the current loop, so you don't add files referenced in the to be deleted .osu
                    continue;
                }
            }

            //matches something in quotes ending with picture ext, like 0,0,"background.jpg"
            //only picture, because mp4 etc also get startet like this 
            const regex = /"[^"]*\.jpg"|"[^"]*\.jpeg"|"[^"]*\.png"/g;

            const backgroundImageFilename = getPropertyFormDotOsu(dotOsuFiles[i], "Events", regex);
            if (backgroundImageFilename !== undefined) {
                const backgroundImagePath = folder + "/" + backgroundImageFilename.slice(1, -1);
                //did it already get added to the list? Do not add it a second time
                if (originalBackgrounds.indexOf(backgroundImagePath) === -1) {
                    originalBackgrounds.push(backgroundImagePath);
                    const backgroundImagePathCapitalized = checkCapitalization(backgroundImagePath, filesToDelete);
                    uniqueBackgrounds.push(backgroundImagePathCapitalized);
                    log("Found background " + backgroundImageFilename.slice(1, -1));
                }
            }
            const soundFilename = getPropertyFormDotOsu(dotOsuFiles[i], "General", "AudioFilename");
            if (soundFilename !== undefined) {
                const soundFilePath = folder + "/" + soundFilename;
                //already in it, don't add again
                if (originalSoundFiles.indexOf(soundFilePath) === -1) {
                    originalSoundFiles.push(soundFilePath);
                    const soundFilePathCapitalized = checkCapitalization(soundFilePath, filesToDelete);
                    uniqueSoundFiles.push(soundFilePathCapitalized);
                    log("Found soundFile " + soundFilename);
                }
            }

        }

        if (uniqueBackgrounds.length === 0)
            log("No background found");

        if (uniqueSoundFiles.length === 0)
            log("No sound found");

        //removes the content of the second from the first
        filesToDelete = filterArray(filesToDelete, uniqueBackgrounds);
        filesToDelete = filterArray(filesToDelete, uniqueSoundFiles);
        filesToDelete = filterArray(filesToDelete, filterArray(dotOsuFiles, unwishedGamemodeFiles));
        if (dotOsuFiles.length === unwishedGamemodeFiles.length)
            log("All difficulties deleted");
        if (keepHitsounds) {
            const hitsounds = getHitsounds(filesToDelete, folder);
            filesToDelete = filterArray(filesToDelete, hitsounds);
        }
        //delete files, if mode !testrun
        for (let i = 0; i < filesToDelete.length; i++) {
            const stats = fs.statSync(filesToDelete[i]);
            log("Deleting " + filesToDelete[i].split(folder + "/")[1]);
            if (!dryRun)
                fs.unlinkSync(filesToDelete[i]);
            spaceSaved += stats.size;
        }

        deletedFiles += filesToDelete.length;

    });
    //split in two functions, first contains error prone code, so I can easily wrap it in try catch
    nextStep();
} catch (error) {
    log(error.stack, 2);

    logger.on("close", function () {
        process.stdout.write("\nPress any key to exit");
        process.stdin.setRawMode(true);
        process.stdin.resume();
        process.stdin.on("data", process.exit.bind(process, 0));
    });

    process.stdout.write("\n" + error.stack);
    process.stdout.write("\n\nSorry about that, please send me the logfile so I can fix the problem");
    process.stdout.write("\n\nWriting logfile...\n");
    logger.end();
}

function nextStep() {
    process.stdout.write("\nDeleting empty folders, this might take a while...\n");
    if (!dryRun) {
        const orphaned = deleteEmpty.sync(rootFolder, { verbose: false });
        let temp = "";
        for (let i = 0; i < orphaned.length; i++) {
            temp += "\r\nRemoved empty dir " + orphaned[i];
        }
        log(temp);
    }


    log("Total: " + totalFiles + ", deleted " + deletedFiles + ", saved: " + humanFileSize(spaceSaved) + ", runtime: " + Math.round(Date.now() / 1000 - startTime) + "s", 1);
    process.stdout.write("\n\nFinished! From a total of " + totalFiles + " files you deleted " + deletedFiles + ", saving you " + humanFileSize(spaceSaved) + ".");
    process.stdout.write("\nAnd it only took you " + (Math.round(Date.now() / 1000 - startTime) < 180 ? Math.round(Date.now() / 1000 - startTime) + " seconds" : (Math.round(Math.round(Date.now() / 1000 - startTime) / 60)) + " minutes"));
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

function getHitsounds(files, folder) {
    let hitsounds = [];
    for (let i = 0; i < files.length; i++) {
        const filename = files[i].split(folder + "/")[1];
        const prefix = filename.split("-")[0];
        const ext = filename.split(".")[filename.split(".").length - 1];

        if ((prefix === "normal" || prefix === "soft" || prefix === "drum") && ext === "wav") {
            hitsounds.push(files[i]);
            log("\nKeeping hitsound " + filename);
        }
    }
    return hitsounds;
}

//fs.unlink doesn't care about capitalization, while fs.exists does, which means every file need to be checked
function checkCapitalization(file, filesToDelete) {
    const fileLowerCase = file.toLowerCase();
    //make both lowercase, if the same take the one with the name from disk
    for (let i = 0; i < filesToDelete.length; i++) {
        if (fileLowerCase === filesToDelete[i].toLowerCase() && file !== filesToDelete[i]) {
            log("Capitalzisation inconsistent: " + filesToDelete[i]);
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
//you can either find via a key or use regex, regex will only return the full capture
function getPropertyFormDotOsu(file, property, findThis) {
    let regex = false;
    if (typeof findThis === "object")
        regex = true;

    const lines = fs.readFileSync(file, "utf8").split("\r\n");
    let result;
    let inProperty = false;
    for (let i = 0; i < lines.length; i++) {
        if (inProperty) {
            //not found, stop looking, next category reached
            if (lines[i].charAt[0] === "[") {
                return undefined;
            }

            if (regex) {
                let m;
                //returns the first match, if existing
                while ((m = findThis.exec(lines[i])) !== null) {
                    if (m.index === regex.lastIndex) {
                        regex.lastIndex++;
                    }
                    return m[0];
                }
            }
            else {
                //splits string into two parts, one everyting before the first :, the second everything after
                const splitted = lines[i].split(/:(.+)/);
                if (splitted[0] === findThis) {
                    return splitted[1].substr(1);
                }
            }
        }
        //reached the desired category, start looking
        if (lines[i] === "[" + property + "]")
            inProperty = true;
    }
}
//defaults to adding a newline
function log(text, newLines) {
    if (newLines === undefined) {
        logger.write("\r\n" + text);
        return;
    }
    let whitespace = "";
    for (let i = 0; i < newLines + 1; i++) {
        whitespace += "\r\n";
    }
    logger.write(whitespace + text);
}