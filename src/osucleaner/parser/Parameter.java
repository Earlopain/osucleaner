package osucleaner.parser;

import java.util.ArrayList;

public class Parameter {

    protected ArrayList<FindThis> list = new ArrayList<FindThis>();

    public void add(String property, String findThis) {
        list.add(new FindThis(property, findThis));
    }

    public void add(String property, String findThis, boolean findOnce) {
        list.add(new FindThis(property, findThis, findOnce));
    }

    protected boolean finished() {
        for (FindThis a : list) {
            if (!a.finished)
                return false;
        }
        return true;
    }

    protected class FindThis {
        String property;
        String findThis;
        boolean regex;
        boolean findOnce;
        boolean inProperty = false;
        boolean finished = false;

        FindThis(String property, String findThis) {
            this.property = property;
            this.findThis = findThis;
            this.regex = false;
            this.findOnce = true;
        }

        FindThis(String property, String findThis, boolean findOnce) {
            this.property = property;
            this.findThis = findThis;
            this.regex = true;
            this.findOnce = findOnce;
        }
    }
}