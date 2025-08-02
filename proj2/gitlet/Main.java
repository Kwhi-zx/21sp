package gitlet;

import java.io.File;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author wzx
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        // if args is empty
        if(args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        Repository repo = new Repository();
        // create an instance
        switch(firstArg) {
            case "init": {
                repo.initCommand();
                break;
            }
            case "add": {
                if(args[1].isEmpty()) {
                    System.out.println("Please enter a File name");
                    System.exit(0);
                }
                String filePath = args[1];
                File addedFile = new File(filePath);
                repo.addCommand(addedFile);
                break;
            }
            case "commit": {
                if(args[1].isEmpty()) {
                    System.out.println("Please entry commit message!");
                    System.exit(0);
                }
                String message = args[1];
                repo.commitCommand(message);
                break;
            }
            case "rm": {
                if(args[1].isEmpty()) {
                    System.out.println("Please enter a File name");
                    System.exit(0);
                }
                String filePath = args[1];
                File rmFile = new File(filePath);
                repo.rmCommand(rmFile);
                break;
            }
            case "log": {
                repo.log();
                break;
            }
            case "global-log": {
                repo.globalLog();
                break;
            }
            case "find": {
                if(args[1].isEmpty()) {
                    System.out.println("Please entry commit message!");
                    System.exit(0);
                }
                String message = args[1];
                repo.find(message);
                break;
            }
            case "status": {
                repo.status();
                break;
            }
            case "checkout": {
                if(args[1].isEmpty()) {
                    System.out.println("Please entry [file name]/[commit id] -- [file name]/[branch name]");
                    System.exit(0);
                }
                if(args[2].isEmpty()) {
                    // only one args
                    String name = args[1];
                    File f = new File(name);
                    if(f.exists()) {
                        // call [file name]
                        repo.checkout(f);
                        break;
                    }
                    // call [branch name]
                    repo.checkout(name);
                    break;
                }
                // call [commit id] -- [file name]
                String commitId = args[1];
                String name = args[2];
                File f = new File(name);
                repo.checkout(commitId,f);
                break;
            }
            case "branch": {
                if(args[1].isEmpty()) {
                    System.out.println("Please entry a branch name");
                    System.exit(0);
                }
                String branchName = args[1];
                repo.branch(branchName);
                break;
            }
            case "rm-branch":
                break;
            case "reset":
                break;
            case "merge":
                break;
            default:
                System.out.println("No such a command.");
                System.exit(0);
        }
    }
}
