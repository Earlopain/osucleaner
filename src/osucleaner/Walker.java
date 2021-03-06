package osucleaner;

import osucleaner.parser.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JProgressBar;

public class Walker {
    private static int counter = 0;

    public static void start(Options options, JProgressBar progressBar) {
        options.log();
        Logger.log("Starting", 1);
        Logger.log("Getting mapsets");
        progressBar.setStringPainted(true);
        progressBar.setString("Getting mapsets...");
        String[] filesAndFolders = options.root.list();
        Logger.log("Finished!");
        Logger.log("Sorting out files");
        ArrayList<File> songs = new ArrayList<File>();
        for (String element : filesAndFolders) {
            File f = new File(options.root.getPath() + File.separator + element);
            if (f.isDirectory()) {
                songs.add(f);
            } else {
                Logger.log("Not a mapset: " + element);
            }
        }

        Logger.log("Finished!");

        int totalFiles = 0;
        int deletedFiles = 0;
        long spaceSaved = 0l;
        long startTime = new Date().getTime() / 1000;
        //used to see if you need to update the progress indicator, if different from the new one write it

        String previousWrite = "";
        Logger.log("Start itterating over the mapsets...", 2);
        progressBar.setMaximum(songs.size());
        try {
            //matches something in quotes ending with picture ext, like 0,0,"background.jpg"
            //only picture, because mp4 etc also get startet like this 
            String regex = "\"[^\"]*\\.jpg\"|\"[^\"]*\\.jpeg\"|\"[^\"]*\\.png\"";
            ArrayList<File> filesToDelete;
            ArrayList<File> dotOsuFiles;
            ArrayList<File> uniqueSoundFiles;
            ArrayList<File> uniqueBackgrounds;
            ArrayList<File> unwishedGamemodeFiles;
            ArrayList<File> filesInDirWithoutSubdirs;

            for (File folder : songs) {
                //if trying to read a file fails, it's probably that the path is to long
                Logger.log("Parsing " + folder.getName(), 1);
                progressBar.setValue(counter);
                String writing = "Parsing files: " + Util.progressbarString(startTime, counter, songs.size());
                if (!writing.equals(previousWrite))
                    progressBar.setString(writing);
                counter++;

                filesInDirWithoutSubdirs = Util.getAllFilesInFolder(folder);
                //Maps like https://osu.ppy.sh/s/73 are packaged in a extra folder, the program only looks in the first dir for 
                //osu files, make sure maps like that survive
                if (filesInDirWithoutSubdirs.size() == 0 && folder.listFiles().length == 1)
                    folder = folder.listFiles()[0];

                filesToDelete = Util.getAllFilesInFolderRecursive(folder);
                totalFiles += filesToDelete.size();
                dotOsuFiles = Util.getOsuFiles(Util.getAllFilesInFolder(folder));

                uniqueSoundFiles = new ArrayList<File>();
                uniqueBackgrounds = new ArrayList<File>();
                unwishedGamemodeFiles = new ArrayList<File>();
                boolean pathsToLong = false;

                for (File osuFile : dotOsuFiles) {
                    if (pathsToLong)
                        continue;
                    try {
                        Parameter paras = new Parameter();
                        paras.add("General", "Mode");
                        paras.add("Events", regex, true);
                        paras.add("General", "AudioFilename");
                        Results results = Parser.get(osuFile, paras);

                        String gm = results.getFirst(0);
                        String backgroundImageFilename = results.getFirst(1);
                        String soundFilename = results.getFirst(2);

                        if (options.removeGamemodes) {
                            //early maps didn't have mode property
                            if (gm != null) {
                                int gamemode = Integer.parseInt(gm);
                                //did the user selecte to delete this gamemode?
                                if (options.gamemodesToRemove[gamemode]) {
                                    Logger.log(Options.validGamemodesString[gamemode] + ": " + osuFile.getName()
                                            + ", removing");
                                    unwishedGamemodeFiles.add(osuFile);
                                    //stop the current loop, so you don't add files referenced in the to be deleted .osu
                                    continue;
                                }
                            }

                        }
                        if (backgroundImageFilename != null) {
                            File backgroundImage = new File(folder.getPath() + File.separator//files can be in nested folders, so escape seperators
                                    + backgroundImageFilename.substring(1, backgroundImageFilename.length() - 1)
                                            .replace("\\", "\\\\"));
                            if (Options.caseSensitive)
                                backgroundImage = Util.checkCapitalization(backgroundImage, filesToDelete);

                            if (uniqueBackgrounds.indexOf(backgroundImage) == -1) {
                                uniqueBackgrounds.add(backgroundImage);
                                Logger.log("Found background " + backgroundImageFilename);
                            }
                        }
                        if (soundFilename != null) {
                            File soundFile = new File(
                                    folder.getPath() + File.separator + soundFilename.replace("\\", "\\\\"));
                            if (Options.caseSensitive)
                                soundFile = Util.checkCapitalization(soundFile, filesToDelete);
                            if (uniqueSoundFiles.indexOf(soundFile) == -1) {
                                uniqueSoundFiles.add(soundFile);
                                Logger.log("Found soundfile " + soundFilename);
                            }
                        }
                    } catch (FileNotFoundException e) {
                        Logger.log("Aborting, paths are too long");
                        pathsToLong = true;
                        continue;
                    }

                }
                if (!pathsToLong) {
                    if (uniqueBackgrounds.size() == 0)
                        Logger.log("No background found");
                    if (uniqueSoundFiles.size() == 0)
                        Logger.log("No sound found");

                    filesToDelete.removeAll(uniqueBackgrounds);
                    filesToDelete.removeAll(uniqueSoundFiles);
                    dotOsuFiles.removeAll(unwishedGamemodeFiles);
                    filesToDelete.removeAll(dotOsuFiles);
                    if (dotOsuFiles.size() == 0)
                        Logger.log("All difficulties removed");
                    if (options.keepHitsounds) {
                        ArrayList<File> hitsounds = Util.getHitsounds(filesToDelete, folder);
                        filesToDelete.removeAll(hitsounds);
                    }

                    for (File delete : filesToDelete) {
                        String fullPath = delete.getPath();
                        Logger.log("Deleting " + fullPath.replace(folder.getPath() + File.separatorChar, ""));
                        spaceSaved += delete.length();
                        if (!options.testrun)
                            delete.delete();
                    }

                    if (options.replaceAllBackgrounds && !options.testrun) {
                        for (File f : uniqueBackgrounds) {
                            spaceSaved += f.length() - (options.image == null ? 0 : options.image.length());
                            f.delete();
                            Files.copy(options.image.toPath(), f.toPath());
                        }
                    }

                    deletedFiles += filesToDelete.size();
                }

            }
            if (!options.testrun) {
                ArrayList<File> emptyFolders = Util.getEmptyFolders(options.root, progressBar, startTime);
                for (File folder : emptyFolders) {
                    if (!options.testrun)
                        folder.delete();
                    Logger.log("Deleting empty folder " + folder.getName());
                }
            }

            progressBar.setString("Finished!");
            Logger.log(
                    "Total: " + totalFiles + ", deleted " + deletedFiles + ", saved: " + Util.humanFileSize(spaceSaved)
                            + ", runtime: " + Math.round(new Date().getTime() / 1000 - startTime) + "s",
                    1);
            Util.notice("Everything is finished\nFrom a total of " + totalFiles + " you deleted " + deletedFiles
                    + ", saving you " + Util.humanFileSize(spaceSaved) + "\nAnd it only took "
                    + Math.round(new Date().getTime() / 1000 - startTime)
                    + " seconds!\nClicking in the button will close this program. Thanks for using it :)");
            Logger.end();
            System.exit(0);
        } catch (Exception e) {
            Util.throwException(e);
        }
    }
}