import java.io.File;
import java.util.ArrayList;

import javax.swing.JOptionPane;

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

    public static String humanFileSize(int size) {
        int i = (int) Math.floor(Math.log(size) / Math.log(1024));
        String[] values = { "B", "kB", "MB", "GB", "TB" };
        return String.format("%.2f", size / Math.pow(1024, i)) + " " + values[i];
    }

    public static ArrayList<File> getOsuFiles(ArrayList<File> files) {
        ArrayList<File> results = new ArrayList<File>();
        for (int i = 0; i < files.size(); i++) {
            String[] splitted = files.get(i).getName().split("\\.");
            if (getExtension(files.get(i)).equals("osu")) {
                results.add(files.get(i));
            }
        }
        return results;
    }

    public static ArrayList<File> getAllFilesInFolder(File dir) {
        ArrayList<File> results = new ArrayList<File>();
        for (File fileEntry : dir.listFiles()) {
            if (fileEntry.isDirectory()) {
                results.addAll(getAllFilesInFolder(fileEntry));
            } else {
                results.add(fileEntry);
            }
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
}