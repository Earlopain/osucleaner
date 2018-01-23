import java.io.BufferedReader;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Parser {
    public static String get(String file, String property, String findThis, boolean regex) {
        boolean inProperty = false;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = br.readLine()) != null) {
                if (inProperty) {
                    if (line.startsWith("[")) {
                        br.close();
                        return "";
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
                            return splitted[1].substring(1);
                        }
                    }
                }
                if (line.startsWith("[" + property))
                    inProperty = true;

            }
        } catch (Exception e) {

        }
        return "";
    }
}