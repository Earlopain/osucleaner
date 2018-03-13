package osucleaner.parser;

import osucleaner.Util;
import osucleaner.parser.Parameter.FindThis;
import osucleaner.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    public static Results get(File file, Parameter paras, String... optional) throws FileNotFoundException {
        String encoding = (optional.length == 0) ? "UTF-8": optional[0];
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding))) {
            String line;
            ArrayList<FindThis> list = paras.list;
            Results results = new Results(paras.list.size());
            while ((line = br.readLine()) != null) {
                if (paras.finished()) {
                    results.finish();
                    return results;
                }

                for (int i = 0; i < paras.list.size(); i++) {
                    if (list.get(i).inProperty) {
                        if (list.get(i).inProperty && !list.get(i).finished) {
                            if (line.startsWith("[")) {
                                list.get(i).finished = true;
                            }

                            if (list.get(i).regex) {
                                Pattern p = Pattern.compile(list.get(i).findThis, Pattern.CASE_INSENSITIVE);
                                Matcher m = p.matcher(line);
                                while (m.find()) {
                                    results.add(i, m.group(0));
                                    if (list.get(i).findOnce)
                                        list.get(i).finished = true;
                                }
                            } else {
                                final String[] splitted = line.split(":", 2);
                                if (splitted[0].equals(list.get(i).findThis)) {
                                    results.add(i,
                                            splitted[1].startsWith(" ") ? splitted[1].substring(1) : splitted[1]);
                                    list.get(i).finished = true;
                                }
                            }
                        }
                    }
                    else if (line.startsWith("[" + list.get(i).property))
                        list.get(i).inProperty = true;
                }

            }
            //probably the filepaths are too long, or file was deleted, but believe it's the other
        } catch (FileNotFoundException e) {
            throw e;
        } catch (Exception e) {
            Util.throwException(e);
        }
        
        //Neither utf8 or itf16, return nothing
        if(optional.length != 0){
            Logger.log("Encoding not supported");
            Results r = new Results(paras.list.size());
            r.finish();
            return r;
        }
        //Results were not returend, perhaps the encoding is wrong, try again
        Logger.log("Not UTF8: " + file.getName());
        return get(file, paras, "UTF-16");
    }

}