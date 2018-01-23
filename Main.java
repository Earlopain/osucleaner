import java.awt.EventQueue;
import java.io.File;
import java.util.List;

import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        moveOldConfigs();
        GUI window = new GUI();
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
}