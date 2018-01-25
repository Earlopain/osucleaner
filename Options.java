import java.io.File;

public class Options {

    final static String logFile = "./cleanup.log";
    final static boolean caseSensitive = !new File("a").equals(new File("A"));

    String path = null;
    boolean testrun = true;
    boolean keepHitsounds = false;
    boolean removeGamemodes = false;
    //standart, taiko, ctb, mania
    static String validGamemodesString[] = { "standart", "taiko", "ctb", "mania" };
    boolean gamemodesToRemove[] = { false, false, false, false };

    public int verify() {
        File folder = new File(path);
        if (!folder.exists())
            return -1;
        boolean deleteAll = true;
        for (int i = 0; i < gamemodesToRemove.length; i++) {
            if (gamemodesToRemove[i] == false)
                deleteAll = false;
        }
        if (deleteAll)
            return 2;

        //check if parent dir contains osu.exe, if not notice the user of it
        File parent = folder.getParentFile();
        if (parent != null) {
            return new File(parent.getPath() + File.separator + "osu!.exe").exists() ? 0 : 1;
        } else
            return 1;
    }

    public void log() {
        Logger.log("Path: " + path);
        Logger.log("Path case-sensitive: " + caseSensitive);
        Logger.log("Testrun: " + testrun);
        Logger.log("Keep hitsounds: " + keepHitsounds);
        Logger.log("Keep standard: " + !gamemodesToRemove[0]);
        Logger.log("Keep taiko: " + !gamemodesToRemove[1]);
        Logger.log("Keep ctb: " + !gamemodesToRemove[2]);
        Logger.log("Keep mania: " + !gamemodesToRemove[3]);
        Logger.log("");
    }
}