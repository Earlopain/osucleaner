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
        GUI window = new GUI();
        Util.moveOldConfigs();
        Logger.start();
    }
}