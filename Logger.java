import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class Logger {
    private static Writer w;
    static {
        try {
            w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Options.logFile), "utf-8"));
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void log(String text) {
        try {
            w.write("\r\n" + text);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void log(String text, int newLines) {
        try {
            String whitespace = "";
            for (int i = 0; i < newLines + 1; i++) {
                whitespace += "\r\n";
            }
            w.write(whitespace + text);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void end() {
        try {
            w.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    //called to init static block
    public static void start(){
    }
}