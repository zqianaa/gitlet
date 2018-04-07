package gitlet;
import java.io.Serializable;
import java.util.*;
import java.text.*;
/**
 * Created by wangkaihong on 2017/7/15.
 */
public class Commit implements Serializable {
    //store blobs of files included in this commit
    private HashSet<Blob> tracked;

    //the time commit happened
    private String timestamp;

    //the newest commit before this commit happen
    private Commit parent;

    //identification of this commit
    private String SHA1name;

    private Map<String, Blob> SHA1blob;
    //A description of this commit
    private String message;

    private Map<String,Blob> Nameblob;

    public Map<String, Blob> getNameblob() {
        return Nameblob;
    }

    public Map<String, Blob> getSHA1blob() {
        return SHA1blob;
    }

    //constructor
    public Commit() {
        parent = null;
        tracked = new HashSet<Blob>();
        timestamp = null;
        SHA1name = "";
        message = "";
        SHA1blob = new HashMap<String, Blob>();
        Nameblob = new HashMap<String, Blob>();
    }

    public HashSet<Blob> getTracked() {
        return tracked;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public Commit getParent() {
        return parent;
    }

    public String getSHA1name() {
        return SHA1name;
    }

    public String getMessage() {
        return message;
    }

    public Commit(stage s, Commit parent, String mes) {
        this.parent = parent;
        message = mes;
        Date date = new Date();
        long times = date.getTime();//时间戳
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        timestamp = formatter.format(times);
        tracked = new HashSet<Blob>();
        tracked.addAll(s.added);//add all elements in added in stage
        if(parent != null) {/*
            /////////must be fixed
            for (Blob b2 : parent.getTracked()) {//for each blob tracked by parent
                boolean hasblob = false;
                for (Blob b4 : s.removed) {//for each blob in current stage removed
                    if (b2.getFilename().equals(b4.getFilename())) {//if parent tracking blob is marked removed
                        hasblob = true;//label it
                    }
                }
                if (!hasblob) {//if not labeled
                    tracked.add(b2);//continue tracking it
                }
            }*/
            ////////must be fixed
            for(Blob b : parent.getTracked()) {
                if(!s.added.contains(b) && !s.removed.contains(b) && !s.add_name.contains(b.getFilename())) {
                    tracked.add(b);
                }
            }
        }
        StringBuilder temp = new StringBuilder();//compute SHA-1 name
        for(Blob btemp : tracked) {
            temp.append(btemp.SHA1());
        }
        SHA1name = Utils.sha1(new String(temp));
        for (Blob b : tracked) {
            SHA1blob.put(b.getSHA1(),b);
            Nameblob.put(b.getFilename(),b);
        }
    }
}
