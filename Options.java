import java.io.File;

public class Options {

    final static String logFile = "./cleanup.log";

    String path = null;
    boolean testrun = true;
    boolean keepHitsounds = false;
    boolean removeGamemodes = false;
    //standart, taiko, ctb, mania
    boolean gamemodesToRemove[] = { false, false, false, false };

    public boolean verifyPath() {
        //check if parent dir contains osu.exe, if not notice the user of it
        String osuExePath = "";
        String[] splitted = path.split("/");
        //-2 because the path is of this format: D:/osu!/Songs/, we want D:/osu!/
        for (int i = 0; i < splitted.length - 1; i++) {
            osuExePath += splitted[i] + "/";
        }
        osuExePath += "osu!.exe";
        File f = new File(osuExePath);
        return f.exists();
    }

    public String toString() {
        return "Path: " + path + "\nTestrun: " + testrun + "\nKeep hitsounds: " + keepHitsounds + "\nKeep standard:  "
                + !gamemodesToRemove[0] + "\nKeep taiko:     " + !gamemodesToRemove[1] + "\nKeep ctb:       "
                + !gamemodesToRemove[2] + "\nKepp mania:     " + !gamemodesToRemove[3];
    }

}