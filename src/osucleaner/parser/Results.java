package osucleaner.parser;

import java.sql.PseudoColumnUsage;
import java.util.ArrayList;

public class Results {

    private ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();

    protected Results(int size){
        for(int i = 0; i < size; i++){
            list.add(new ArrayList<String>());
        }
    }

    public String getFirst(int position) {
        return list.get(position).get(0);
    }

    public String[] getAll(int position) {
        return list.get(position).toArray(new String[list.size()]);
    }

    protected void add(int position, String what) {
        list.get(position).add(what);
    }

    protected void finish() {
        for (ArrayList<String> al : list) {
            if (al.size() == 0) {
                al.add(null);
            }
        }
    }
}