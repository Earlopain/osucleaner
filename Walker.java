import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JProgressBar;

import com.sun.deploy.jcp.controller.DeleteFiles;

public class Walker {
    public static void start(Options options, JProgressBar progressBar) {
        options.log();
        Logger.log("Starting", 1);

        Logger.log("Getting mapsets");
        String[] filesAndFolders = new File(options.path).list();
        Logger.log("Finished!");
        Logger.log("Sorting out files");
        ArrayList<File> songs = new ArrayList<File>();
        for (String element : filesAndFolders) {
            File f = new File(options.path + "/" + element);
            if (f.isDirectory()) {
                songs.add(f);
            } else {
                Logger.log("Not a mapset: " + options.path + element);
            }
        }

        Logger.log("Finished!");
        int counter = 0;

        int totalFiles = 0;
        int deletedFiles = 0;
        int spaceSaved = 0;
        long startTime = new Date().getTime() / 1000;
        //used to see if you need to update the progress indicator, if different from the new one write it
        String previousWrite = "";
        Logger.log("Start itterating over the mapsets...", 2);
        progressBar.setMaximum(songs.size());
        progressBar.setStringPainted(true);
        try {
            //matches something in quotes ending with picture ext, like 0,0,"background.jpg"
            //only picture, because mp4 etc also get startet like this 
            String regex = "\"[^\"]*\\.jpg\"|\"[^\"]*\\.jpeg\"|\"[^\"]*\\.png\"";
            ArrayList<File> filesToDelete;
            ArrayList<File> dotOsuFiles;
            ArrayList<File> uniqueSoundFiles;
            ArrayList<File> uniqueBackgrounds;
            ArrayList<File> unwishedGamemodeFiles;

            for (File folder : songs) {
                if (counter == 943) {
                    System.out.println("");
                }
                System.out.println(counter);
                Logger.log("Parsing " + folder, 1);
                progressBar.setValue(counter);
                String writing = "" + (Math.round(counter / songs.size() * 10) / 10) + "% "
                        + (Math.round(new Date().getTime() / 1000 - startTime)) + "s";
                if (!writing.equals(previousWrite))
                    progressBar.setString(writing);
                counter++;

                filesToDelete = Util.getAllFilesInFolder(folder);
                totalFiles += filesToDelete.size();
                dotOsuFiles = Util.getOsuFiles(filesToDelete);

                uniqueSoundFiles = new ArrayList<File>();
                uniqueBackgrounds = new ArrayList<File>();
                unwishedGamemodeFiles = new ArrayList<File>();

                for (File osuFile : dotOsuFiles) {
                    if (options.removeGamemodes) {
                        String gm = Parser.get(osuFile, "General", "Mode", false);
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

                    String backgroundImageFilename = Parser.get(osuFile, "Events", regex, true);
                    if (backgroundImageFilename != null) {
                        File backgroundImage = new File(folder.getPath() + "/"
                                + backgroundImageFilename.substring(1, backgroundImageFilename.length() - 1));

                        if (uniqueBackgrounds.indexOf(backgroundImage) == -1) {
                            uniqueBackgrounds.add(backgroundImage);
                            Logger.log("Found background " + backgroundImageFilename);
                        }
                    }
                    String soundFilename = Parser.get(osuFile, "General", "AudioFilename", false);
                    if (soundFilename != null) {
                        File soundFile = new File(folder.getPath() + "/" + soundFilename);
                        if (uniqueSoundFiles.indexOf(soundFile) == -1) {
                            uniqueSoundFiles.add(soundFile);
                            Logger.log("Found soundfile " + soundFilename);
                        }
                    }
                }
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
                    Logger.log("Deleting " + delete.getName());
                    if (!options.testrun)
                        delete.delete();
                    spaceSaved += delete.length();
                }
                deletedFiles += filesToDelete.size();
            }
            progressBar.setIndeterminate(true);
            ArrayList<File> emptyFolders = Util.getEmptyFolders(new File(options.path));
            for (File folder : emptyFolders) {
                if (!options.testrun)
                    folder.delete();
                Logger.log("Deleting empty folder " + folder.getName());
            }
            progressBar.setIndeterminate(false);
            progressBar.setValue(progressBar.getMaximum());
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