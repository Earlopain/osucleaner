package osucleaner;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

public class Util {
    public static ArrayList<File> getHitsounds(ArrayList<File> files, File folder) {
        ArrayList<File> hitsounds = new ArrayList<File>();
        for (int i = 0; i < files.size(); i++) {
            String filename = files.get(i).getName();
            String prefix = filename.split("-")[0];
            String ext = getExtension(files.get(i));
            if ((prefix.equals("normal") || prefix.equals("soft") || prefix.equals("drum")) && ext.equals("wav")) {
                hitsounds.add(files.get(i));
                Logger.log("Keeping hitsound " + filename);
            }
        }
        return hitsounds;
    }

    public static String humanFileSize(long size) {
        if (size == 0)
            return "nothing";
        boolean negative = size < 0 ? true : false;
        if (size < 0)
            size *= -1;
        int i = (int) Math.floor(Math.log(size) / Math.log(1024));
        String[] values = { "B", "kB", "MB", "GB", "TB", "PB" };
        String result =  String.format("%.2f", size / Math.pow(1024, i)) + " " + values[i];
        return negative ? "-" + result : result;
    }

    public static ArrayList<File> getOsuFiles(ArrayList<File> files) {
        ArrayList<File> results = new ArrayList<File>();
        for (int i = 0; i < files.size(); i++) {
            if (getExtension(files.get(i)).equals("osu")) {
                results.add(files.get(i));
            }
        }
        return results;
    }

    public static ArrayList<File> getAllFilesInFolderRecursive(File dir) {
        ArrayList<File> results = new ArrayList<File>();
        for (File fileEntry : dir.listFiles()) {
            if (fileEntry.isDirectory()) {
                results.addAll(getAllFilesInFolderRecursive(fileEntry));
            } else {
                results.add(fileEntry);
            }
        }
        return results;
    }

    public static ArrayList<File> getAllFilesInFolder(File dir) {
        ArrayList<File> results = new ArrayList<File>();
        for (File fileEntry : dir.listFiles()) {
            if (!fileEntry.isDirectory()) {
                results.add(fileEntry);
            }
        }
        return results;
    }

    public static ArrayList<File> getEmptyFolders(File dir, JProgressBar bar, long startTime) {
        ArrayList<File> results = new ArrayList<File>();
        File[] list = dir.listFiles();
        int counter = 0;
        bar.setMaximum(list.length);
        String previous = "";
        for (File fileEntry : list) {
            bar.setValue(counter);
            String writing = "Deleting empty folders: " + Util.progressbarString(startTime, counter, list.length);
            if (!previous.equals(writing)) {
                previous = writing;
                bar.setString(writing);
            }
            if (fileEntry.isDirectory() && fileEntry.listFiles().length == 0) {
                results.add(fileEntry);
            } else if (fileEntry.isDirectory()) {
                results.addAll(getEmptyFolders(fileEntry));
            }
            counter++;
        }
        return results;
    }

    public static ArrayList<File> getEmptyFolders(File dir) {
        ArrayList<File> results = new ArrayList<File>();
        for (File fileEntry : dir.listFiles()) {
            if (fileEntry.isDirectory() && fileEntry.listFiles().length == 0) {
                results.add(fileEntry);
            } else if (fileEntry.isDirectory()) {
                results.addAll(getEmptyFolders(fileEntry));
            }
        }
        return results;
    }

    public static void throwException(Exception e) {
        Logger.log(e.toString(), 2);
        e.printStackTrace();
        for (StackTraceElement stack : e.getStackTrace()) {
            Logger.log("    at " + stack.toString());
        }
        Logger.end();
        JOptionPane.showMessageDialog(null, e.toString()
                + "\n\nSorry about that, please send me the logfile so I can fix the problem.\nThe program will now exit",
                "Something doesn't look right", JOptionPane.OK_OPTION);
        System.exit(0);
    }

    public static void notice(String s) {
        JOptionPane.showMessageDialog(null, s);
    }

    public static String getExtension(File f) {
        int i = f.getName().lastIndexOf('.');
        if (i >= 0) {
            return f.getName().substring(i + 1);
        } else
            return "";
    }

    public static File checkCapitalization(File check, ArrayList<File> files) {
        String lowercase = check.getName().toLowerCase();
        for (File f : files) {
            if (lowercase.equalsIgnoreCase(f.getName()) && !check.getName().equals(f.getName())) {
                Logger.log("Capitalization inconsistent: " + check.getName() + " vs " + f.getName());
                return f;
            }
        }
        return check;
    }

    public static void moveOldConfigs() {
        File[] folderFiles = new File("./").listFiles();
        String[] logFiles = new String[folderFiles.length];
        boolean logFileAlreadyExists = false;
        int counter = 0;
        for (File file : folderFiles) {
            if (file.isFile()) {
                if (file.getName().startsWith(Options.logFile.substring(2))) {
                    logFiles[counter] = (file.getAbsolutePath());
                    counter++;
                }
                if (file.getName().equals(Options.logFile.substring(2)))
                    logFileAlreadyExists = true;
            }
        }
        if (logFileAlreadyExists) {
            //start with the last one, so we don't overwrite files
            for (int i = counter - 1; i >= 0; i--) {
                new File(logFiles[i]).renameTo(new File(Options.logFile + (i + 1)));
            }
        }
    }

    public static String progressbarString(long startTime, int now, int max) {
        return (String.format("%.1f", (double) now / max * 100)) + "% "
                + (Math.round(new Date().getTime() / 1000 - startTime)) + "s";
    }

    public static String getUserDataFolder(){
        if(true){}
        return "";
    }
}