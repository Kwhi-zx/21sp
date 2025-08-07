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
                    System.out.println("Please enter a commit message.");
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
                // checkout [branchName]
                if(args.length == 2) {
                    String branchName = args[1];
                    repo.checkout(branchName);
                }
                // checkout -- [file name]
                else if (args.length == 3 && args[1].equals("--")) {
                    String fileName = args[2];
                    File file = new File(fileName);
                    repo.checkout(file);
                }
                // checkout [commit id] -- [file name]
                else if (args.length == 4 && args[2].equals("--")) {
                    String commitId = args[1];
                    String fileName = args[3];
                    File file = new File(fileName);
                    repo.checkout(commitId,file);
                }
                else {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
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
            case "rm-branch": {
                if(args[1].isEmpty()) {
                    System.out.println("Please entry a branch name");
                    System.exit(0);
                }
                String branchName = args[1];
                repo.rm_branch(branchName);
                break;
            }
            case "reset": {
                if(args[1].isEmpty()) {
                    System.out.println("Please entry a commit id");
                    System.exit(0);
                }
                String commitId = args[1];
                repo.reset(commitId);
                break;
            }
            case "merge": {
                if(args[1].isEmpty()) {
                    System.out.println("Please entry a branch name");
                    System.exit(0);
                }
                String branchName = args[1];
                repo.merge(branchName);
                break;
            }
            default: {
                System.out.println("No command with that name exists.");
                System.exit(0);
            }
        }
    }
}
