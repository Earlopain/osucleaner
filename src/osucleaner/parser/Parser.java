package osucleaner.parser;

import osucleaner.Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    public static String get(File file, String property, String findThis, boolean regex) throws FileNotFoundException{
        boolean inProperty = false;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = br.readLine()) != null) {
                if (inProperty) {
                    if (line.startsWith("[")) {
                        br.close();
                        return null;
                    }

                    if (regex) {
                        Pattern p = Pattern.compile(findThis);
                        Matcher m = p.matcher(line);
                        while (m.find()) {
                            br.close();
                            return m.group(0);
                        }
                    } else {
                        final String[] splitted = line.split(":", 2);
                        if (splitted[0].equals(findThis)) {
                            br.close();
                            return splitted[1].startsWith(" ") ? splitted[1].substring(1) : splitted[1];
                        }
                    }
                }
                if (line.startsWith("[" + property))
                    inProperty = true;

            }
            //probably the filepaths are too long, or file was deleted, but believe it's the other
        } catch (FileNotFoundException e) {
            throw e;
        } catch (Exception e) {
            Util.throwException(e);
        }
        return null;
    }
}