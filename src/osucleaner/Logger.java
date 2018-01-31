package osucleaner;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Logger {
    private static ScheduledExecutorService executor;
    private static Writer w;

    static{
        try {
            w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Options.logFile), "utf-8"));
        } catch (Exception e) {
            Util.throwException(new Exception("Logfile could not be created"));
        }
        executor = Executors.newScheduledThreadPool(1);
        Runnable writeLog = new Runnable() {
            public void run() {
                try {
                    w.flush();
                } catch (Exception e) {
                    Util.notice("Logfile moved or deleted");
                }
            }
        };
        executor.scheduleAtFixedRate(writeLog, 0, 1, TimeUnit.SECONDS);
    }

    public static void log(String text) {
        try {
            w.write("\r\n" + text);
        } catch (Exception e) {
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
        }
    }

    public static void end() {
        executor.shutdown();
        try {
            w.close();
        } catch (Exception e) {
            Util.notice("Logfile could not be saved");
        }
    }
}