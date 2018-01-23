import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class Logger {
    static Writer w;
    static {
        try {
            w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Options.logFile), "utf-8"));
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void log(String s) {
        try {
            w.write(s);
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
}