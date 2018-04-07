package gitlet;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
/**
 * Created by wangkaihong on 2017/7/15.
 */
public class gitlet implements Serializable {
    //An array to store branches in this repo
    public Map<String, Commit>  branches; //(default master)

    //A pointer point to the commit under operating
    public Commit head;

    private ArrayList<Commit> AllCommit;
    //Store blobs under stage
    public stage staging;

    public HashMap<String,Blob> toBlob;

    public gitlet() {
        branches = new HashMap<String, Commit>();
        AllCommit = new ArrayList<Commit>();
        head = null;
        staging = null;
        toBlob = new HashMap<String, Blob>();
    }

    //Create a new gitlet
    public void init() {
        staging = new stage();
        branches.put("master",new Commit(staging,null,"Initial Commit"));
        head = branches.get("master");
        AllCommit.put(head.getSHA1name(),head);
    }

    //input file name
    //call blob constructor(pass in filename)
    //construct a new blob
    //add into staging
    public void add(String filename) {
        File f = new File(filename);//If no file,
        if(!f.exists()) {
            System.out.println("File does not exist");//report and quit
            return;
        }

        Blob temp = Blob.getBlob(filename,toBlob);
        for (Blob b : head.getTracked()) {
            if (b.SHA1().equals(temp.SHA1())) {//If the current working version of the file is identical
                return;//do not stage it to be added
            }
        }
        staging.add(filename,toBlob);
    }
    //new a commit, include information about blob[] in
    //head point to the newest commit
    //staging clear
    //add information in String[] log
    public void commit(String messageName) {
        boolean isBranch = false;
        for (String s : branches.keySet()) {
            if (head == branches.get(s)){
                isBranch = true;
            }
        }
        if (!isBranch) {
            System.out.println("Please create a new branch!");
            return;
        }
        if(staging.added.size() == 0 && staging.removed.size() == 0) {
            System.out.println("No changes added to the commit.");
            return;
        }
        Commit newcommit = new Commit(staging, head, messageName);
        for (String s : branches.keySet()) {
            if (head == branches.get(s)) {
                branches.put(s, newcommit);
            }
        }
        AllCommit.add(newcommit);
        head = newcommit;
        staging.clear();
    }
    public void log() {
        Helplog(head);
    }

    public void Helplog(Commit Head) {
        if (Head == null) {
            return;
        }
        System.out.println("===");
        System.out.println("Commit " + Head.getSHA1name());
        System.out.println(Head.getTimestamp());
        System.out.println(Head.getMessage());
        System.out.println();
        Helplog(Head.getParent());
    }
    //Display all commits in this branch
    public void global_log() {
        for (Commit c : AllCommit) {
            System.out.println("===");
            System.out.println("Commit " + c.getSHA1name());
            System.out.println(c.getTimestamp());
            System.out.println(c.getMessage());
            System.out.println();
            Helplog(c.getParent());
        }
    }
    //According to commit message to find the commit
    public void find(String commitMessage) {
        for (Commit c : AllCommit) {
            if (c.getMessage().equals(commitMessage)){
                System.out.println(c.getSHA1name());
            }
        }
        throw new IllegalArgumentException("");
    }
    //Displays what branches currently exist, and marks the current branch with a *.
    //Also displays what files have been staged or marked for untracking.
    public void status() {

        System.out.println("=== Branches ===");
        for(String s : branches.keySet()) {
            if(head == branches.get(s)) {
                System.out.println("*"+s);
            }
            else {
                System.out.println(s);
            }
        }


        System.out.println("=== Staged Files ===");
        ArrayList<Blob> cwd = new ArrayList<Blob>();
        File f = new File(this.getClass().getResource("/").getPath());
        String[] filelist = f.list();


        for(int i = 0 ; i < filelist.length;i++) {
            if(!filelist[i].startsWith(".") && !filelist[i].equals("gitlet") && !filelist[i].equals("proj2.iml") && !filelist[i].equals("testing")) {
                cwd.add(Blob.getBlob(filelist[i],toBlob));
            }
        }
        for(Blob b :head.getTracked()) {
            for(Blob c :cwd) {
                if(b.getSHA1().equals(c.getSHA1())) {
                    System.out.println(b.getFilename());
                }
            }
        }
        for(Blob b :staging.added) {
            for(Blob c :cwd) {
                if(b.getSHA1().equals(c.getSHA1())) {
                    System.out.println(b.getFilename());
                }
            }
        }


        System.out.println("=== Removed Files ===");
        for(Blob b :staging.removed) {
            System.out.println(b.getFilename());
        }

        System.out.println("=== Modifications Not Staged For Commit ===");

        System.out.println("=== Untracked Files ===");

    }

    // Deletes the branch with the given name. This only means to delete the pointer 	//associated with the branch; it does not mean to delete all commits that were
    //created under the branch, or anything like that.
    public void rm_branch(String name) {
        for (String s : branches.keySet()) {
            if (s.equals(name)) {
                remove(name);
            }
        }
    }

    // checkout all the file in given commit,and delete all the other files which is
    // not in the given commit in the working directory.That is,make the working dir
    // ectory exact the same as the given commit
    public void reset(String comId) {

        boolean comSeen = false;
        Commit givenCom = null;

        for (Commit bran : AllCommit) {
            if (bran.getSHA1name().equals(comId)) { // choose the commit
                givenCom = bran;
                comSeen = true;
            }
        }

        if (!comSeen) {throw new IllegalArgumentException("No commit with that id exists.");}

        File workingDir = new File(this.getClass().getResource("/").getPath());
        File[] workingDirLs = workingDir.listFiles();

        // get the current commit file name string list
        ArrayList<String> currFileLst = new ArrayList<String>();
        for (Blob b: head.getTracked()) {
            currFileLst.add(b.getFilename());
        }

        // get the given commit file
        ArrayList<String> givenFileLst = new ArrayList<String>();
        ArrayList<String> givenSha1Lst = new ArrayList<String>();

        for (Blob b: givenCom.getTracked()) {
            givenFileLst.add(b.getFilename()); // update the given commit file
            givenSha1Lst.add(b.getSHA1());
        }

        for (File f : workingDirLs) {
            String Sha1 = Utils.sha1(f.getName() + new String(Utils.readContents(f)));
            if (f.getName().equals("gitlet") || f.getName().equals("out") || f.getName().equals("testing")) {
                continue;
            }
            if (!currFileLst.contains(f.getName()) && givenFileLst.contains(f.getName())
                    && givenSha1Lst.contains(Sha1)) { // untracked file update
                throw new IllegalStateException("There is an untracked file in the way; delete it or add it first.");
            } else {
                f.delete(); // clear up the dir file
            }
        }
        for (Blob b: givenCom.getTracked()) {
            b.toFileblob(); // update the given commit file to working directory
        }

        head = givenCom;
        staging.clear();
    }
    // pick up all the file store in current commit,and put them into working dire
    // ctory,which may refresh the origin version in working directory.
// pick up all the file store in current commit,and put them into working dire-
    // -ctory,which may refresh the origin version in working directory.
    public void checkout(String[] args, int type) {
        File workingDir = new File(this.getClass().getResource("/").getPath());
        File[] workingDirLs = workingDir.listFiles();
        if (type == 1) { // file name case
            String fileName = args[0];

            boolean fileSeen = false;
            for (File f : workingDirLs) {
                if (f.getName().equals(fileName)) {
                    fileSeen = true;
                    f.delete(); // update

                }
            }
            if (!fileSeen) {throw new IllegalArgumentException("File does not exist in that commit.");}


            for (Blob btemp : head.getTracked()) {
                if (btemp.getFilename().equals(fileName)) {
                    btemp.toFileblob();
                    return;
                }
            }

        } else if (type == 2) { // commit Id && file name
            String comId = args[0];
            String fileName = args[1];

            boolean comSeen = false;

            for (Commit bran : AllCommit) {
                if (bran.getSHA1name().equals(comId)) { // choose the commit
                    for (Blob b : bran.getTracked()) {
                        if (b.getFilename().equals(fileName)) { // pick up the file
                            comSeen = true;
                            b.toFileblob();
                            return;
                        }
                    }
                }
                throw new IllegalArgumentException("File does not exist in that commit.");
            }
            if (comSeen) {throw new IllegalArgumentException("No commit with that id exists.");}

        } else if (type == 3) { // branch
            String branName = args[0];
            Commit givenBran;
            try {
                givenBran = branches.get(branName);
            } catch(Exception ex) {
                throw new IllegalArgumentException("No such branch exists.");
            }

            if (givenBran.equals(head)) {
                throw new IllegalArgumentException("No need to checkout the current branch.");
            }


            ArrayList<String> fileLst = null;
            // take all files in the commit at the head of the given branch
            for (Blob givenFile : givenBran.getTracked()) {
                if (!head.getTracked().contains(givenFile)) {
                    throw new IllegalArgumentException("There is an untracked file in the way; delete it or add it first.");
                }
                fileLst.add(givenFile.getFilename());
                givenFile.toFileblob();
            }

            // files that are tracked in the current branch but are not present in the checked-out branch are deleted.
            for (Blob currFile : head.getTracked()) {
                if (!fileLst.contains(currFile.getFilename())) {
                    File toDelet = new File(currFile.getFilename());
                    toDelet.delete();
                }
            }
            if (!head.equals(givenBran)) {
                head = givenBran; // change the head pointer
                staging.clear(); // clear the stage
            }
        }
    }
    /**
     * merge method
     *
     * @param  : the target commit to merge in
     * @Object splitpoint: the shared commit
     *         currentCommit: the commit the head points to
     * @functionality
     * stage file
     *       case1: files in current isolated from given and share
     *              remain
     *       case2: files in given isolated from current and share
     *              check-out
     *       case3: files in shared isolated from current and given
     *              removed
     *       case4: files only in intersection of shared and current
     *              if unmodified: removed
     *                 modified:   remain
     *       case5: files in intersection of current and given and are different
     *              merge conflict
     * if not merge conflict
     *    make new commit
     */
    public void merge(String branchname) {
        Commit givenCommit = null;
        for (String s : branches.keySet()) {
            if (s.equals(branchname)) {
                givenCommit = branches.get(s);
            }
        }
        if (givenCommit == null) {
            System.out.println("NO SUCH BRANCH");
            return;
        }
        Commit Pointer = head;
        ArrayList<Commit> currBranch = new ArrayList<Commit>();
        while (Pointer != null) {
            currBranch.add(Pointer);
            Pointer = Pointer.getParent();
        }
        Commit ancestor = helpMerge(currBranch, givenCommit);
        if (ancestor == givenCommit) {
            System.out.println("Given commit is the ancestor of current commit");
            return;
        }
        if (ancestor == head) {
            head = givenCommit;
        }
        for (Blob c : givenCommit.getTracked()) {
            boolean sameblob = false;
            for (Blob c1 : ancestor.getTracked()) {
                if (c.getFilename().equals(c1.getFilename())) {
                    sameblob = true;
                }
                for (Blob c2 : head.getTracked()) {
                    if (c.getFilename().equals(c2.getFilename())) {
                        sameblob = true;
                    }
                }
            }
            if (!sameblob) {
                checkout(c.getFilename());
                add(c.getFilename());
            }22
        }
        for (Blob c : givenCommit.getTracked()) {
            int hasblob = 0;
            String cSHA1 = c.getSHA1();
            String c1SHA1 = "";
            String c2SHA1 = "";
            for (Blob c1 : ancestor.getTracked()) {
                if (c.getFilename().equals(c1.getFilename())) {
                    hasblob++;
                    c1SHA1 = c1.getSHA1();
                }
                for (Blob c2 : head.getTracked()) {
                    if (c.getFilename().equals(c2.getFilename())) {
                        hasblob++;
                        c2SHA1 = c2.getSHA1();
                    }
                }
            }
            if (hasblob == 2 && cSHA1 != c1SHA1 && c1SHA1 == c2SHA1) {
                checkout(c.getFilename());
                add(c.getFilename());
            }
        }
        for (Blob c2 : head.getTracked()) {
            boolean hasblob1 = false;
            boolean sameblob = false;
            boolean hasblob2 = false;
            for (Blob c1 : ancestor.getTracked()) {
                if (c2.getFilename().equals(c1.getFilename())) {
                    hasblob1 = true;
                    if (c2.getSHA1().equals(c1.getSHA1())) {
                        sameblob = true;
                    }
                }
                for (Blob c : givenCommit.getTracked()) {
                    if (c.getFilename().equals(c2.getFilename())) {
                        hasblob2 = true;
                    }
                }
            }
            if (hasblob1 && sameblob && !hasblob2) {
                remove(c2.getFilename());
            }
        }
        for (Blob c2 : head.getTracked()) {
            boolean hasblob1 = false;
            boolean hasblob2 = false;
            boolean sameblob1 = false;
            boolean sameblob2 = false;
            Blob conflict = new Blob();
            for (Blob c : givenCommit.getTracked()) {
                if (c2.getFilename().equals(c.getFilename())) {
                    hasblob1 = true;
                    if (c2.getSHA1().equals(c.getSHA1())) {
                        sameblob1 = true;
                    } else {
                        conflict = c;
                    }
                }
                for (Blob c1 : ancestor.getTracked()) {
                    if (c2.getFilename().equals(c1.getFilename())) {
                        hasblob2 = true;
                        if (c2.getSHA1().equals(c1.getSHA1())) {
                            sameblob2 = true;
                        }
                    }
                }
            }
            if (hasblob1 && !hasblob2 && !sameblob1) {
                System.out.println("Encountered a merge conflict.");
                File f1 = new File(c2.getFilename());
                File f2 = new File(conflict.getFilename());
                StringBuffer sb1 = new StringBuffer("<<<<<<< HEAD\n" + "contents of file in current branch\n" + "=======\n" + "contents of file in given branch\n" + ">>>>>>>\n");
                StringBuffer sb2 = new StringBuffer("<<<<<<< HEAD\n" + "contents of file in current branch\n" + "=======\n" + "contents of file in given branch\n" + ">>>>>>>\n");
                try {
                    FileWriter fwriter = new FileWriter(f1);
                    BufferedWriter bwriter = new BufferedWriter(fwriter);
                    bwriter.write(sb1.toString());
                    bwriter.close();
                    FileWriter fwriter2 = new FileWriter(f2);
                    BufferedWriter bwriter2 = new BufferedWriter(fwriter2);
                    bwriter2.write(sb2.toString());
                    bwriter2.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (hasblob1 && hasblob2 && !sameblob1 && !sameblob2) {
                    System.out.println("Encountered a merge conflict.");
                    File f3 = new File(c2.getFilename());
                    File f4 = new File(conflict.getFilename());
                    StringBuffer sb3 = new StringBuffer("<<<<<<< HEAD\n" + "contents of file in current branch\n" + "=======\n" + "contents of file in given branch\n" + ">>>>>>>\n");
                    StringBuffer sb4 = new StringBuffer("<<<<<<< HEAD\n" + "contents of file in current branch\n" + "=======\n" + "contents of file in given branch\n" + ">>>>>>>\n");
                    try {
                        FileWriter fwriter = new FileWriter(f3);
                        BufferedWriter bwriter = new BufferedWriter(fwriter);
                        bwriter.write(sb3.toString());
                        bwriter.close();
                        FileWriter fwriter2 = new FileWriter(f4);
                        BufferedWriter bwriter2 = new BufferedWriter(fwriter2);
                        bwriter2.write(sb4.toString());
                        bwriter2.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                String currname = "";
                for (String s : branches.keySet()) {
                    if (branches.get(s) == head) {
                        currname = s;
                    }
                }
                commit("merged " + currname + "with" + branchname);
            }
        }
    }

    public Commit helpMerge (ArrayList<Commit> a , Commit givencommit) {
        for (Commit c : a) {
            if (c == givencommit) {
                return c;
            }
        }
        if (givencommit.getParent() == null) {
            return null;
        }
        return helpMerge(a,givencommit.getParent());
    }
    /**
     * remove method
     *
     * @param filename
     *
     *  step1:
     *       check whether file is in the current commit
     *       if in: remove the file in the working directory if existed
     *  step2:
     *      check whether file is in the stage
     *      if in: remove the file
     *      (make sure the next commit will not have the file)
     */
    public void remove(String filename) {
        boolean flag = false;
        Blob btemp = Blob.getBlob(filename,toBlob);
        if(!staging.rm(filename,toBlob)) {//check stage first
            for (Blob b : head.getTracked()) {
                if (b.getFilename().equals(filename)) {//then check previous commit
                    File f = new File(filename);
                    f.delete();
                    flag = true;
                    staging.removed.add(btemp);
                    break;
                }
            }
            if(!flag) {
                System.out.println("No reason to remove the file.");
            }
        }
    }
    /**
     * branch method
     *
     * @param name
     *
     * create a new pointer with current commit with the name [String name]
     * gitlet branches commit list adds the new pointer
     * head pointer is now assigned to the new pointer
     */
    public void branch(String name) {
        if (branches.containsKey(name)) {
            System.out.println("A branch with that name already exists.");
            return;
        }
        branches.put(name,head);
    }

}
