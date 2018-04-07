package gitlet;
import java.io.*;
import java.util.Iterator;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) {
        if(args.length == 0) {
            throw new IllegalArgumentException("No argument input !");
        }

        File f = new File(".gitlet");
        if (!f.exists()) {
            if(args.length == 1 && args[0].equals("init")) {
                gitlet git = new gitlet();
            }
            else {
                throw new IllegalArgumentException("Not in an initialized gitlet directory !");
            }
        }
        else {
            if(args.length == 1 && args[0].equals("init")) {
                System.out.println("A gitlet version-control system already exists in the current directory.");
                return;
            }
        }
        gitlet git = null;
        // deserialize the file for operation
        try {
            ObjectInputStream is = new ObjectInputStream(new FileInputStream(".gitlet"));
            git = (gitlet) is.readObject();
        } catch (Exception e) {
            git = new gitlet();
        }

        if(args[0].equals("init")) {
            if(args.length == 1) {
                git.init();
            }
            else {
                throw new IllegalArgumentException("Illegal Argument At Init !");
            }
        }
        if(args[0].equals("add")) {
            if(args.length == 2) {
                git.add(args[1]);
            }
            else {
                throw new IllegalArgumentException("Illegal Argument At Add !");
            }
        }
        if(args[0].equals("test")) {
            System.out.println((git.head.getParent().getTracked().size()));
            System.out.println( (git.head.getTracked().size()));

            System.out.println(git.staging.removed.size());
            System.out.println(git.staging.added.size());
/*
            Iterator<Blob> i = git.head.getTracked().iterator();
            System.out.println(((new String(i.next().getContent()))));
            System.out.println(((new String(i.next().getContent()))));
            System.out.println(((new String(i.next().getContent()))));
            System.out.println(((new String(i.next().getContent()))));
            System.out.println(((new String(i.next().getContent()))));
*/
            for(Blob b :git.head.getTracked()) {
                System.out.println(new String(b.getContent()));
            }
            /*
            System.out.println(((new String(git.head.getTracked().get(0).getContent()))));
            System.out.println(((new String(git.head.getTracked().get(1).getContent()))));
            System.out.println(((new String(git.head.getTracked().get(2).getContent()))));
            */
        }


        if(args[0].equals("commit")) {
            if(args.length == 1) {
                System.out.println("Please enter a commit message.");
                return;
            }
            if(args.length == 2) {
                git.commit(args[1]);
            }
            else {
                throw new IllegalArgumentException("Illegal Argument At Commit !");

            }

        }
        if(args[0].equals("rm")) {
            if(args.length == 2) {
                git.remove(args[1]);
            }
            else {
                throw new IllegalArgumentException("Illegal Argument At Rm !");
            }

        }
        if(args[0].equals("log")) {
            if(args.length == 1) {
                git.log();
            }
            else {
                throw new IllegalArgumentException("Illegal Argument At Log !");
            }

        }
        if(args[0].equals("global-log")) {
            if(args.length == 1) {
                git.global_log();
            }
            else {
                throw new IllegalArgumentException("Illegal Argument At Global-log !");

            }

        }
        if(args[0].equals("find")) {
            if(args.length == 2) {
                git.find(args[1]);
            }
            else {
                throw new IllegalArgumentException("Illegal Argument At Find !");

            }

        }
        if(args[0].equals("status")) {
            if(args.length == 1) {
                git.status();
            }
            else {
                throw new IllegalArgumentException("Illegal Argument At Status !");

            }

        }
        if(args[0].equals("checkout")) {
            if(args.length == 3) {
                if(args[1].equals("--")) {
                    //git.checkout();//to be modified
                }
                else {
                    throw new IllegalArgumentException("Illegal Argument At Checkout !");
                }
            }
            if(args.length == 4) {
                if(args[2].equals("--")) {
                    //git.checkout();//to be modified
                }
                else {
                    throw new IllegalArgumentException("Illegal Argument At Checkout !");
                }
            }
            if(args.length == 2) {
                //git.checkout();//to be modified
            }
            else {
                throw new IllegalArgumentException("Illegal Argument At Checkout !");

            }

        }
        if(args[0].equals("branch")) {
            if(args.length == 2) {
                git.branch(args[1]);
            }
            else {
                throw new IllegalArgumentException("Illegal Argument At Branch !");

            }

        }
        if(args[0].equals("rm-branch")) {
            if(args.length == 2) {
                git.rm_branch(args[1]);
            }
            else {
                throw new IllegalArgumentException("Illegal Argument At Rm-branch !");

            }

        }
        if(args[0].equals("reset")) {
            if(args.length == 2) {
                //git.reset(args[1]);
            }
            else {
                throw new IllegalArgumentException("Illegal Argument At Reset !");

            }

        }
        if(args[0].equals("merge")) {
            if(args.length == 2) {
                //git.merge(args[1]);
            }
            else {
                throw new IllegalArgumentException("Illegal Argument At Merge !");

            }

        }


        // serialize the file
        try {
            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(".gitlet"));
            os.writeObject(git);
        } catch (IOException e) {
            e.printStackTrace();
        }




    }

}
