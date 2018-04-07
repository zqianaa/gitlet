package gitlet;
import java.io.*;
import java.util.*;
/**
 * Created by wangkaihong on 2017/7/15.
 */
public class Blob implements Serializable{
    //store file name
    private String Filename;

    private String SHA1;

    //store file content
    private byte[] content;

    public Blob() {
        Filename = "";
        content = null;
        SHA1 = "";
    }

    public String getFilename() {
        return Filename;
    }

    public String getSHA1() {
        return SHA1;
    }

    public byte[] getContent() {
        return content;
    }

    public Blob(String fileName) {
        fromFileblob(fileName);
    }

    //when we new a blob, this function takes one file'name and its file content.
    //see help function readContents() in Utils.java
    public void fromFileblob(String fileName) {
        File f = new File(fileName);
        if(!f.exists()) {
            System.out.println("File does not exist");
            return;
        }
        Filename = fileName;
        content = Utils.readContents(f);
        SHA1 = Utils.sha1(Filename + new String(content));
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Blob blob = (Blob) o;

        if (Filename != null ? !Filename.equals(blob.Filename) : blob.Filename != null) return false;
        if (SHA1 != null ? !SHA1.equals(blob.SHA1) : blob.SHA1 != null) return false;
        return Arrays.equals(content, blob.content);
    }
    public static Blob getBlob(String fn,HashMap<String,Blob> m) {
        Blob temp = new Blob(fn);
        if(m.containsKey(temp.getSHA1())) {
            return m.get(temp.getSHA1());
        }
        else {
            Blob temp2 = new Blob(fn);
            m.put(temp2.getSHA1(),temp2);
            return temp2;
        }
    }
    //when we modify some files and make a checkout, we write the new content into
    //see help function writeContents() in Utils.java
    public void toFileblob() {
        File f = new File(Filename);
        if(!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Utils.writeContents(f,content);
    }

    public String SHA1() {
        return SHA1;
    }

    public static void main(String[] args) {
        Blob b = new Blob("./x/1.txt");
        //b.toFileblob();
        System.out.println(b.SHA1());

        Blob a = new Blob("./x/1.txt");
        //a.toFileblob();
        System.out.println(a.SHA1());

    }

}
