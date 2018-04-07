package gitlet;
import java.io.File;
import java.util.*;
import java.io.Serializable;

/**
 * Created by wangkaihong on 2017/7/15.
 */
public class stage implements Serializable {
    public HashSet<Blob> added;//should be changed to Hashset
    public HashSet<Blob> removed;//should be changed to Hashset or String
    public HashSet<String> add_name;

    public stage() {
        added = new HashSet<Blob>();
        removed = new HashSet<Blob>();
        add_name = new HashSet<>();
    }

    public void clear() {
        added.clear();
        removed.clear();
        add_name.clear();
    }

    public boolean ifClear() {
        return (added.size() == 0 && removed.size() == 0 && add_name.size() == 0);
    }

    public void add(String fn,HashMap<String,Blob> m) {

        Blob staged = Blob.getBlob(fn,m);
        if(added.contains(staged)) {
            return;
        }
        if(removed.contains(staged)) {
            removed.remove(staged);
        }
        added.add(staged);
        add_name.add(staged.getFilename());

    }
    public boolean rm(String fn,HashMap<String,Blob> m) {
        Blob btemp = Blob.getBlob(fn,m);

        for (Blob b : added) {
            if (b.getFilename().equals(btemp.getFilename())) {//if in stage
                added.remove(b);
                add_name.remove(b.getFilename());
                removed.add(btemp);
                return true;
            }
        }
        return false;
    }
}