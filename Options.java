import java.io.File;

public class Options {

    final static String logFile = "./cleanup.log";

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
        String osuExePath = "";
        String[] splitted = path.split("/");
        //-2 because the path is of this format: D:/osu!/Songs/, we want D:/osu!/
        for (int i = 0; i < splitted.length - 1; i++) {
            osuExePath += splitted[i] + "/";
        }
        osuExePath += "osu!.exe";
        File f = new File(osuExePath);
        return f.exists() ? 0 : 1;
    }

    public void log() {
        Logger.log("Path: " + path);
        Logger.log("Testrun: " + testrun);
        Logger.log("Keep hitsounds: " + keepHitsounds);
        Logger.log("Keep standard: " + !gamemodesToRemove[0]);
        Logger.log("Keep taiko: " + !gamemodesToRemove[1]);
        Logger.log("Keep ctb: " + !gamemodesToRemove[2]);
        Logger.log("Keep mania: " + !gamemodesToRemove[3]);
        Logger.log("");
    }
}