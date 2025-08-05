package gitlet;

import static gitlet.Utils.*;

// any imports you need here
import java.io.IOException;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author wzx
 */
public class Repository {
    /**
     * add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    // mine instance

    public static final File OBJECTS = join(GITLET_DIR,"objects");
    /** The staging area*/
    public static final File STAGING_AREA = join(GITLET_DIR,"index");

    /** The staging area for removal*/
    public static final File REMOVE_INDEX = join(GITLET_DIR,"rm_index");

    /** storing  master*/
    public static final File REFS = join(GITLET_DIR,"refs");
    public static final File Heads = join(REFS,"heads");
    /** HEAD*/
    public static final File HEAD = join(GITLET_DIR,"HEAD");



    /* fill in the rest of this class. */

    /**
    *   Creates a new Gitlet version-control system in the current directory
    *   This system will automatically start with one commit
    *   a commit that contains no files and has the commit message "initial commit"
    *   It will have a single branch "master"
    *   which initially points to this initial commit,
    *   and master will be the current branch
    *   The timestamp for this initial commit will be 00:00:00 UTC, Thursday, 1 January 1970
    * */
    public void initCommand() {
        if(GITLET_DIR.exists()) {
            // If there is already a Gitlet version-control system in the current directory, it should abort
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            return;
        }
        // create dir
        GITLET_DIR.mkdir();
        OBJECTS.mkdir();
        REFS.mkdir();
        Heads.mkdir();

        // create file
        try {
            STAGING_AREA.createNewFile();
            REMOVE_INDEX.createNewFile();
            HEAD.createNewFile(); // where the HEAD pointer --> storing a path
        }catch (IOException e) {
            e.printStackTrace();
        }

        File headFile = join(Heads,"master");
        // .git/HEAD
        // TODO: relativaly Path ?
        writeContents(HEAD,headFile.getPath()); // "yeah,the HEAD pointer --->" ref: refs/heads/master


        // initail the commit message
        Commit initCommit = new Commit();
        byte[] serializedCommit = Utils.serialize(initCommit); // serialize the Commit for the sha1

        String hashCode = Utils.sha1(serializedCommit);

        // refs/heads/master --> master branch
        Utils.writeContents(headFile,hashCode);

        String hashDirName = hashCode.substring(0,2); // get first 2 chars for the file name
        String hashFileName = hashCode.substring(2);

        // .git/objects/xx
        File hashFileDir = join(OBJECTS,hashDirName);
        if (!hashFileDir.exists()) {
            hashFileDir.mkdir();
        }
        // .git/objects/xx/...
        File hashFile = join(hashFileDir,hashFileName);
        Utils.writeObject(hashFile,initCommit);

    }

    /**
     *
     *  Adds a copy of the file as it currently exists to the staging area
     *  The staging area should be somewhere in .gitlet
     *  only one file may be added at a time.
    * */
    @SuppressWarnings("unchecked")
    public void addCommand(File name) {

        // the cur file
        byte[] contents = readContents(name);
        String hashCode = Utils.sha1(contents); // sha1 contents
        String fileRelativePaths = name.getPath();

        String hashDirName = hashCode.substring(0,2); // get first 2 chars for the file name
        String hashFileName = hashCode.substring(2);

        // .git/index
        // persistence --> read from file
        HashMap<String, String> oldStagingInfo = new HashMap<>();
        if (STAGING_AREA.length() != 0) {
            oldStagingInfo = Utils.readObject(STAGING_AREA, HashMap.class);
        }

        // .git/rm_index which store the file path
        HashSet<String> rmHashset = new HashSet<>();
        if(REMOVE_INDEX.length() != 0) {
            rmHashset = readObject(REMOVE_INDEX,HashSet.class);
        }

        // Get the cur commit
        String headHashcode = getHeadHashCode();
        File headFile = getHashFile(OBJECTS,headHashcode);
        Commit curCommit = Utils.readObject(headFile,Commit.class);
        HashMap<String,String> curCommitContent = curCommit.getFilesCommitBlob();

        // If the current working version of the file is
        // identical to the version in the current commit,
        if(curCommitContent.containsKey(fileRelativePaths)) {
            String hashValue = curCommitContent.get(fileRelativePaths);
            if(hashCode.equals(hashValue)) {
                if (!oldStagingInfo.isEmpty()) {
                    // remove it from the staging area if it is already there
                    oldStagingInfo.remove(fileRelativePaths);
                    if (!rmHashset.isEmpty()) {
                        rmHashset.remove(fileRelativePaths);
                    }
                    // save
                    Utils.writeObject(STAGING_AREA, oldStagingInfo);
                    Utils.writeObject(REMOVE_INDEX,rmHashset);
                    // do not stage it to be added,
                    return;
                }
            }
        }
        // .git/objects/xx
        File hashFileDir = join(OBJECTS,hashDirName);
        if (!hashFileDir.exists()) {
            hashFileDir.mkdir();
        }
        // .git/objects/xx/...
        // this calls a blob : A file in .git/objects
        File hashFile = join(hashFileDir,hashFileName);
        if(!hashFile.exists()) {
            // if it doesn't exist --> create a new file
            // if it does exist --> same hashcode --> objects don't change
//           writeObject(hashFile,contents);
           writeContents(hashFile,contents);
        }

        // store filePath and hashCode
        // if two files have the same filePath
        if (!oldStagingInfo.isEmpty() && oldStagingInfo.containsKey(fileRelativePaths)) {
            // case1: a.txt --> a.txt  --> content doesn't change
            // --> it should pass
            String hashvalue = oldStagingInfo.get(fileRelativePaths);
            if (hashvalue.equals(hashCode)) {
                return;
            }
        }
        // case2: a.txt & b.txt    --> same as case3: create a new one
        oldStagingInfo.put(fileRelativePaths, hashCode);
        // don't serialize
        Utils.writeObject(STAGING_AREA, oldStagingInfo); // overwrites

        // The file will no longer be staged for removal ,
        // if it was at the time of the command.
        if (!rmHashset.isEmpty()) {
            // remove() already check if path exist
            rmHashset.remove(fileRelativePaths);
        }
        writeObject(REMOVE_INDEX,rmHashset);
    }

    @SuppressWarnings("unchecked")
    public void commitCommand(String msg) {
        Commit newCommit = new Commit();

        // set timeStamp and message  --> MetaData
        Date date = new Date(); // real time
        newCommit.setTimestamp(date);
        newCommit.setMessage(msg);
        // set parent --> parent
        // parent Commit
        // inherent from parent
        String parentCommitHash = getHeadHashCode();
        File parentContentFile = getHashFile(OBJECTS,parentCommitHash);
        Commit parentCommit = Utils.readObject(parentContentFile,Commit.class);

        List<String> parentList = new ArrayList<>();
        parentList.add(parentCommitHash);
        newCommit.setParents(parentList);

        newCommit.setFilesandBlob(parentCommit.getFilesCommitBlob());

        // .git/index   .git/rm_index
        if(STAGING_AREA.length() == 0 && REMOVE_INDEX.length() == 0) {
            // if staging area is empty
            System.out.println("Everything up-to-date!");
            System.exit(0);
        }

        // refresh the inherentHashMap
        HashMap<String,String> inherentHashMap = newCommit.getFilesCommitBlob();

        // add
        HashMap<String,String> newStagingInfo = new HashMap<>();
        newStagingInfo = Utils.readObject(STAGING_AREA,HashMap.class);
        for(Map.Entry<String,String> entry: newStagingInfo.entrySet()) {
            // go through the STAGING AREA and overwrite
            String filePath = entry.getKey();
            String fileHashCode = entry.getValue();
            inherentHashMap.put(filePath,fileHashCode); // overwrite
        }

        // remove
        HashSet<String> newRemoveInfo = new HashSet<>();
        newRemoveInfo = Utils.readObject(REMOVE_INDEX,HashSet.class);
        for(String pathName:newRemoveInfo) {
            inherentHashMap.remove(pathName);
        }

        // .git/objects --> store the newCommit
        byte[] serializedNewCommit = Utils.serialize(newCommit);
        String newCommitHashcode = Utils.sha1(serializedNewCommit);
        String newCommitHashDirName = newCommitHashcode.substring(0,2); // get first 2 chars for the file name
        String newCommitHashFileName = newCommitHashcode.substring(2);
        // .git/objects/xx
        File newCommitHashFileDir = join(OBJECTS,newCommitHashDirName);
        if (!newCommitHashFileDir.exists()) {
            newCommitHashFileDir.mkdir();
        }
        // .git/objects/xx/...
        File newCommitHashFile = join(newCommitHashFileDir,newCommitHashFileName);
        if(!newCommitHashFile.exists()) {
            // if it doesn't exist --> create a new file
            // if it does exist --> same hashcode --> objects don't change
            // don't serialize
            writeObject(newCommitHashFile,newCommit);
        }

        //.git/index  --> clear
        HashMap<String,String> emptyHashmap = new HashMap<>();
        writeObject(STAGING_AREA,emptyHashmap);

        //.git/rm_index --> clear
        HashSet<String> emptyHashset = new HashSet<>();
        writeObject(REMOVE_INDEX,emptyHashset);

        //.git/refs/heads/master  --> change HEAD
        File headFile = join(Heads,"master");
        writeContents(headFile,newCommitHashcode);

    }

    @SuppressWarnings("unchecked")
    public void rmCommand(File name) {

        boolean tracked = false;
        boolean added = false;
        // check it if add --> in the staging area
        HashMap<String,String> indexContent = Utils.readObject(STAGING_AREA,HashMap.class);
        if(indexContent.containsKey(name.getPath())) {
            // Unstage the file if it is currently staged for addition
            indexContent.remove(name.getPath());
            // don't serialize
            writeObject(STAGING_AREA,indexContent); // refresh the STAGING_AREA
            added = true;
        }

        // check it if tracked
        // read from HEAD
        String headHashCode = getHeadHashCode();
        File headCommitFile = getHashFile(OBJECTS,headHashCode);
        Commit headCommit = Utils.readObject(headCommitFile, Commit.class);
        if(headCommit.getFilesCommitBlob().containsKey(name.getPath())) {
            tracked = true;
        }

        if(tracked) {
            // do not remove it unless it is tracked in the current commit
            // so it is tracked here --> remove it from CWD
            // stage it for removal --> Hashset
            HashSet<String> removalHashSet = new HashSet<>();
            if(REMOVE_INDEX.length() != 0) {
                removalHashSet = Utils.readObject(REMOVE_INDEX, HashSet.class);
            }
            removalHashSet.add(name.getPath());
            // don't serialize
            writeObject(REMOVE_INDEX,removalHashSet);
            name.delete();
        }

        if(!added && !tracked) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
    }

//    public void log() {
//
//        // define string format
//        String logFormat = "===";
////        String formatString = "commit %s%nDate: %tc%n%s%n";
//
//        // Linear History
//        // The HEAD
//        String headHashcode = getHeadHashCode();
//        File headHashFile = getHashFile(OBJECTS,headHashcode);
//        Commit headCommit = Utils.readObject(headHashFile,Commit.class);
//        String output = formatWithStringBuilder(headHashcode,
//                headCommit.getTimestamp(),
//                headCommit.getMessage(),
//                headCommit.getMerge(),
//                headCommit.getParent());
//
//        System.out.println(logFormat);
//        System.out.println(output);
//
//        // iterate the parent
//        String childHashcode = headCommit.getParent();
//        while (childHashcode !=null) {
//            System.out.println(logFormat); // ===
//            File childHashFile = getHashFile(OBJECTS, childHashcode);
//            Commit childCommit = Utils.readObject(childHashFile, Commit.class);
//            String childOutput = formatWithStringBuilder(childHashcode,
//                    childCommit.getTimestamp(),
//                    childCommit.getMessage(),
//                    childCommit.getMerge(),
//                    childCommit.getParent());
//            System.out.println(childOutput);
//            childHashcode = childCommit.getParent();
//        }
//
//    }

    public void log() {
        String logFormat = "===";
        String curHashcode = getHeadHashCode();
        System.out.println(logFormat);
        while(curHashcode != null) {
            System.out.println(logFormat);
            File curHashFile = getHashFile(OBJECTS,curHashcode);
            Commit curCommit = Utils.readObject(curHashFile, Commit.class);
            String curOutput = formatWithStringBuilder(curHashcode,
                    curCommit.getTimestamp(),
                    curCommit.getMessage(),
                    curCommit.getMerge(),
                    curCommit.getParents());
            System.out.print(curOutput);
            curHashcode = curCommit.getFirstParent(); // inherent the first parent
        }
    }

    public void globalLog() {

        // displays information about all commits ever made.
        // The order of the commits does not matter.
        String logFormat = "===";
//        String formatString = "commit %s%nDate: %tc%n%s%n";

        // get .git/objects/xx(dir)
        File[] filesList = OBJECTS.listFiles();
        if (filesList != null) {
            for(File dir:filesList) {
                List<String> filenames = Utils.plainFilenamesIn(dir);
                    for(String filename:filenames) {
                        File file = join(dir,filename); // .gitlet/objects/xx + /...
                        try {
                            Commit logCommit = readObject(file,Commit.class);
                            String hashcode = dir.getName() + filename; //  /xx + /...
                            String output = formatWithStringBuilder(hashcode,
                                    logCommit.getTimestamp(),
                                    logCommit.getMessage(),
                                    logCommit.getMerge(),
                                    logCommit.getParents());
                            System.out.println(logFormat);
                            System.out.println(output);
                        } catch (Exception e) {
                            // Just ignore it and continue to the next file.
                        }
                    }
            }
        }

    }

    public String formatWithStringBuilder(String hashcode,Date time,String msg,boolean isMerged,List<String> parentsList) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("commit %s%n", hashcode));
        if(isMerged) {
            String parent1Hashcode = parentsList.get(0);
            String parent2Hashcode = parentsList.get(1);
            String parentCommitHash = parent1Hashcode.substring(0,7) + " " + parent2Hashcode.substring(0,7);
            sb.append(String.format("Merge: %s%n", parentCommitHash));
        }
        sb.append(String.format("Date: %tc%n",time));
        sb.append(String.format("%s%n",msg));

        return sb.toString();
    }

    public void find(String message) {

        boolean existed = false;
        // get .git/objects/xx(dir)
        File[] filesList = OBJECTS.listFiles();
        if (filesList != null) {
            for(File dir:filesList) {
                List<String> filenames = Utils.plainFilenamesIn(dir);
                for(String filename:filenames) {
                    File file = join(dir,filename); // .gitlet/objects/xx + /...
                    try {
                        Commit findCommit = readObject(file,Commit.class);
                        if(message.equals(findCommit.getMessage())) {
                            existed = true;
                            String hashcode = dir.getName() + filename;
                            System.out.println(hashcode);
                        }
                    } catch (Exception e) {
                        // Just ignore it and continue to the next file.
                    }
                }

            }
        }
        if(!existed) {
            System.out.println("Found no commit with that message.");
        }
    }
    @SuppressWarnings("unchecked")
    public void status() {

        String str1 = "=== Branches ===";
        String str2 = "=== Staged Files ===";
        String str3 = "=== Removed Files ===";
        String str4 = "=== Modifications Not Staged For Commit ===";
        String str5 = "=== Untracked Files ===";

        // head Commit
        String headHashcode = getHeadHashCode();
        File headFile = getHashFile(OBJECTS,headHashcode);
        Commit headCommit = readObject(headFile,Commit.class);
        HashMap<String,String> commitHashmap = headCommit.getFilesCommitBlob();

        /** Branches */
        List<String> branchesList = plainFilenamesIn(Heads); // list:[master、main、skeleton...]
        for(String branch:branchesList) {
            System.out.println(str1);
            File bfile = join(Heads,branch);
            String bHashcode = readContentsAsString(bfile);
            if(headHashcode.equals(bHashcode)) {
                System.out.println("*"+branch+"%n");
            }else {
                System.out.println(branch+"%n");
            }
        }

        /** Staged Files */
        System.out.println(str2);
        if(STAGING_AREA.length() != 0) {
            HashMap<String, String> stageFile = readObject(STAGING_AREA, HashMap.class);
            for (Map.Entry<String, String> entry : stageFile.entrySet()) {
                String stagePath = entry.getKey(); // absolute path
                File stagefile = new File(stagePath);
                System.out.println(stagefile.getName()+"%n");
            }
        }

        /** Removed Files*/
        System.out.println(str3);
        if(REMOVE_INDEX.length()!=0) {
            HashSet<String> removeFile = readObject(REMOVE_INDEX,HashSet.class);
            for(String path:removeFile) {
                // it has been removed
                Path p = Paths.get(path);
                System.out.println(p.getFileName()+"%n");
            }
        }

        /** TODO:Modifications Not Staged For Commit */
        System.out.println(str4);


        /** TODO:Untracked Files */
        System.out.println(str5);
        List<String> filesList = plainFilenamesIn(CWD);
        if (filesList != null) {
            // 1、check if a working file is untracked in the current branch
            // 2、and would be overwritten by the checkout
            for(String filename:filesList) {
//                File f = new File(filename);
                File f = join(CWD,filename);
                if(!commitHashmap.containsKey(f.getPath())) {

                }
            }
            }
    }

    public void checkout(File fName) {
        // get the head commit
        String headHashCode= getHeadHashCode();
        File headFile = getHashFile(OBJECTS,headHashCode);
        Commit headCommit = readObject(headFile,Commit.class);
        checkoutHelper(headCommit,fName);
    }

    public void checkout(String commitId,File fName) {

        File specFile = getHashFile(OBJECTS,commitId);
        if(!specFile.exists()) {
            System.out.println("No commit with that id exists.");
            return;
        }
        Commit specCommit = readObject(specFile,Commit.class);
        checkoutHelper(specCommit,fName);
    }
    public void checkoutHelper(Commit cm,File fName) {

        if(cm.getFilesCommitBlob().containsKey(fName.getPath())) {

            // get the file's relative path
            // if the fName == "/proj2/ab.java" --getPath--> /proj2/ab.java
            String pathName = fName.getPath();
            File cwdFile = new File(pathName);
            String hashcode = cm.getFilesCommitBlob().get(pathName);
            // get the file content(Blob) from .gitlet/objects
            File blobFile = getHashFile(OBJECTS,hashcode);
            byte[] blobContent = readContents(blobFile);

            writeContents(cwdFile,blobContent); // automatically create the file if it doesn't exist
            return;
        }
        System.out.println("File does not exist in that commit.");

    }

    public void checkout(String branchName) {

        // .gitlet/refs/heads/branchName --> check if it exists
        File branchFile = join(Heads,branchName);
        if(!branchFile.exists()) {
            System.out.println("No such branch exists.");
            return;
        }


        // .gitlet/HEAD  --> check if it is
        String headPositionStr = readContentsAsString(HEAD);
        // .gitlet/heads/master
        File curHeadPath = new File(headPositionStr);
        if(branchName.equals(curHeadPath.getName())) {
            System.out.println("No need to checkout the current branch.");
            return;
        }

        // get the cur Commit
        String curCommitHashcode =readContentsAsString(curHeadPath);
        File curCommitFile = getHashFile(OBJECTS,curCommitHashcode);
        Commit curCommit = readObject(curCommitFile,Commit.class);
        HashMap<String,String> curCommitTrackedF = curCommit.getFilesCommitBlob();

        // get the checkout branch Commit
        // according to the Commit's Hashmap
        String checkoutCommitHashcode = readContentsAsString(branchFile);
        File checkoutCommitFile = getHashFile(OBJECTS,checkoutCommitHashcode);
        // Commit --> branch needs to create one?
        // no need --> the new branch inherent the old commit
        Commit checkoutCommit =  readObject(checkoutCommitFile, Commit.class);
        HashMap<String,String> checkoutHashmap = checkoutCommit.getFilesCommitBlob();

        // get CWD files
        List<String> fileList = plainFilenamesIn(CWD);

        if (fileList != null) {
            // 1、check if a working file is untracked in the current branch
            // 2、and would be overwritten by the checkout
            for(String filename:fileList) {
//                File f = new File(filename);
                File f = join(CWD,filename);
                if(!curCommitTrackedF.containsKey(f.getPath())) {
                    if(checkoutHashmap.containsKey(f.getPath())) {
                        System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                        return;
                    }
                }
            }

            // delete the CWD files that checkoutBranch doesn't have
            // 1、Any files that are tracked in the current branch
            // 2、but are not present in the checked-out branch are deleted.
            for (String filename : fileList) {
//                File f = new File(filename);
                File f = join(CWD,filename);
                if(!checkoutHashmap.containsKey(f.getPath())
                && curCommitTrackedF.containsKey(f.getPath())) {
                    // delete from the CWD
                    f.delete();
                }
            }

            // overwrite or create a new file
            for(String checkoutHashKey:checkoutHashmap.keySet()) {
                String blobHash = checkoutHashmap.get(checkoutHashKey);
                File blobFile =getHashFile(OBJECTS,blobHash);
                byte[] blobContent = readContents(blobFile);

                // overwrite or create a new file
                File cwdBlobFile = new File(checkoutHashKey);
                writeContents(cwdBlobFile,blobContent);
//                writeObject(cwdBlobFile,blobContent);
            }

        }

        // clear the staging area
        //.git/index  --> clear
        HashMap<String,String> emptyHashmap = new HashMap<>();
        writeObject(STAGING_AREA,emptyHashmap);

        // given branch will now be considered the current branch (HEAD)
        // refresh the HEAD file with new branch path
        writeContents(HEAD,branchFile.getPath());
    }

    public void branch(String name) {

        // cur HEAD Commit
        String headHashCode = getHeadHashCode();
        // .gitlet/refs/heads/new_branch
        File newBranchFile = join(Heads,name);
        if(newBranchFile.exists()) {
            System.out.println("A branch with that name already exists.");
            return;
        }
        // Creates a new branch with the given name,
        // and points it at the current head commit
        writeContents(newBranchFile,headHashCode);
    }

    public void rm_branch(String branchName) {

        String headPositionStr = readContentsAsString(HEAD);
        // refs/heads/..
        File headPosition = new File(headPositionStr);
        if(branchName.equals(headPosition.getName())) {
            // If you try to remove the branch you’re currently on, aborts
            System.out.println("Cannot remove the current branch.");
            return;
        }

        File rmBranchFile = new File(Heads,branchName);
        if (!rmBranchFile.exists()) {
            // If a branch with the given name does not exist, aborts.
            System.out.println("A branch with that name does not exist.");
            return;
        }

        rmBranchFile.delete();
    }

    public void reset(String commitId) {

        // get the commitId branch name
         List<String> branchList = plainFilenamesIn(Heads);
         String branch = new String();
         for(String branchName:branchList) {
             File branchPath = join(Heads,branchName);
             String branchHashcode = readContentsAsString(branchPath);
             if(commitId.equals(branchHashcode)) {
                 branch = branchName;
                 break;
             }
         }
         checkout(branch);

    }

    /**
     *  Merges files from the given branch into the current branch.
     *
     *  The case below do nothing
     *
     *  // case 2:
     *  // Any files that have been modified in the current branch
     *  // but not in the given branch since the split point should stay as they are.
     *  // stay the same
     *
     *  // case 3a:
     *  // Any files that have been modified in both the current and given branch in the same way
     *  // are left unchanged by the merge
     *
     *  // case 3b:If a file was removed from both the current and given branch,
     *  // but a file of the same name is present in the working directory,
     *  // it is left alone and continues to be absent (not tracked nor staged) in the merge.
     *
     *  // case 4:
     *  // Any files that were not present at the split point and
     *  // are present only in the current branch
     *  // should remain as they are.
     *
     *  // case 7:
     *  // Any files present at the split point,
     *  // unmodified in the given branch,
     *  // and absent in the current branch should remain absent.
     *
     *
     *
     * */
    public void merge(String branchName) {

        // get branchName newest commit
        File branchPosition = join(Heads,branchName);
        if(!branchPosition.exists()) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        String branchHashcode =readContentsAsString(branchPosition);
        File branchCommitFile = getHashFile(OBJECTS,branchHashcode);
        Commit branchCommit = readObject(branchCommitFile,Commit.class);

        // get cur commit
        String headPositionStr = readContentsAsString(HEAD);
        File headPosition = new File(headPositionStr);
        if(branchName.equals(headPosition.getName())) {
            System.out.println("Cannot merge a branch with itself.");
            return;
        }
        String curCommitHashcode = readContentsAsString(headPosition);
        File curCommitFile = getHashFile(OBJECTS,curCommitHashcode);
        Commit curCommit = readObject(curCommitFile,Commit.class);

        // find the split point
        Commit splitPoint = findTheSplitPoint(curCommit,branchCommit);

        // If the split point is the same commit as the given branch, then we do nothing
        if(splitPoint.equals(branchCommit)) {
            System.out.println("Given branch is an ancestor of the current branch.");
            return;
        }
        // If the split point is the current branch, then the effect is to check out the given branch
        if(splitPoint.equals(curCommit)) {
            checkout(branchName);
            System.out.println("Current branch fast-forwarded.");
            return;
        }
        // get Commit Hashmap
        HashMap<String,String> curHashmap = curCommit.getFilesCommitBlob();
        HashMap<String,String> givenHashmap = branchCommit.getFilesCommitBlob();
        HashMap<String,String> splitHashmap = splitPoint.getFilesCommitBlob();

        // If an untracked file in the current commit would be overwritten or deleted by the merge
        List<String> fileList = plainFilenamesIn(CWD);
        if(fileList != null) {
            for(String filename:fileList) {
                File f = join(CWD,filename);
                String untrackedPath = f.getPath();
                if(!curHashmap.containsKey(untrackedPath)) {
                    boolean wouldBeOverwritten = givenHashmap.containsKey(untrackedPath);
                    boolean wouldBeDeleted = splitHashmap.containsKey(untrackedPath) && !givenHashmap.containsKey(untrackedPath);
                    if(wouldBeDeleted || wouldBeOverwritten) {
                        System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                        return;
                    }
                }
            }
        }


        // If there are staged additions or removals present
        if(STAGING_AREA.length() != 0 || REMOVE_INDEX.length() != 0) {
            System.out.println("You have uncommitted changes.");
            return;
        }

        /** three-way merge */

        // merge these hashmap key
        Set<String> allFilepathset = new HashSet<>();
        allFilepathset.addAll(curHashmap.keySet());
        allFilepathset.addAll(givenHashmap.keySet());
        allFilepathset.addAll(splitHashmap.keySet());

        boolean mergeConflict = false;


        // go through all the file path three-way have
        for(String pathName: allFilepathset) {
            File fileInSet = new File(pathName); // exist or not?
            boolean curExist = curHashmap.containsKey(pathName);
            boolean givenExist = givenHashmap.containsKey(pathName);
            boolean splitExist = splitHashmap.containsKey(pathName);

            if (curExist && givenExist && splitExist) {
                String curValue = curHashmap.get(pathName);
                String givenValue = givenHashmap.get(pathName);
                String splitValue = splitHashmap.get(pathName);
                // case 1:
                // Any files that have been modified in the given branch since the split point,
                // but not modified in the current branch since the split point
                if (curValue.equals(splitValue) && !curValue.equals(givenValue)) {
                    // 1、should be changed to their versions in the given branch
                    byte[] givenContent = getBlobContent(givenValue);
                    // In the current branch(i.e master)
                    writeContents(fileInSet, givenContent);
                    // 2、These files should then all be automatically staged.
                    addCommand(fileInSet);
                }

                // case 8a:
                // the contents of both are changed and different from other
                else if(!splitValue.equals(curValue) &&!splitValue.equals(givenValue) &&!curValue.equals(givenValue)) {
                    byte[] conflictContent = case8action(curValue,givenValue);
                    writeContents(fileInSet,conflictContent);
                    System.out.println("Encountered a merge conflict.");
                    mergeConflict = true;
                }

            } else if(!splitExist && !curExist && givenExist) {
                // case 5:
                // Any files that were not present at the split point
                // and are present only in the given branch
                // should be checked out and staged.
                String givenValue = givenHashmap.get(pathName);
                // checkout(newIncurFile) ??
                byte[] givenContent = getBlobContent(givenValue);
                // In the current branch(i.e master)
                writeContents(fileInSet, givenContent);
                // 2、These files should then all be automatically staged.
                addCommand(fileInSet);

            } else if(splitExist && curExist && !givenExist) {
                // case 6:
                // Any files present at the split point,
                // unmodified in the current branch,
                // and absent in the given branch should be removed (and untracked).
                String curValue = curHashmap.get(pathName);
                String splitValue = splitHashmap.get(pathName);
                if(curValue.equals(splitValue)) {
                    // untracked ?
                    rmCommand(fileInSet);
                }
                // case 8b
                // the contents of one are changed and the other file is deleted
                else if(!curValue.equals(splitValue)) {
                    // Treat a deleted file in a branch as an empty file.
                    byte[] conflictContent = case8action(curValue,"");
                    writeContents(fileInSet,conflictContent);
                    System.out.println("Encountered a merge conflict.");
                    mergeConflict = true;
                }

            } else if(splitExist && !curExist && givenExist) {
                // case 8b
                // the contents of one are changed and the other file is deleted
                String givenValue = givenHashmap.get(pathName);
                String splitValue = splitHashmap.get(pathName);
                if(!givenValue.equals(splitValue)) {
                    // Treat a deleted file in a branch as an empty file.
                    byte[] conflictContent = case8action("",givenValue);
                    writeContents(fileInSet,conflictContent);
                    System.out.println("Encountered a merge conflict.");
                    mergeConflict = true;
                }

            } else if (!splitExist && curExist && givenExist) {
                String curValue = curHashmap.get(pathName);
                String givenValue = givenHashmap.get(pathName);
                if(!curValue.equals(givenValue)) {
                    // case 8c
                    // the file was absent at the split point
                    // and has different contents in the given and current branches.
                    byte[] conflictContent = case8action(curValue,givenValue);
                    writeContents(fileInSet,conflictContent);
                    System.out.println("Encountered a merge conflict.");
                    mergeConflict = true;
                }
            }
        }
        if(mergeConflict) {
            // Do not create a commit.
            return;
        }
        String mergeMsg = "Merged %s into %s";
        mergeCommit(String.format(mergeMsg,headPosition.getName(),branchName),
                    curCommit,
                    headPosition.getName(),
                    curCommitHashcode,
                    branchHashcode);
    }

    // Merge commits differ from other commits: they record as parents
    // both the head of the current branch (called the first parent) and
    // the head of the branch given on the command line to be merged in.
    public void mergeCommit(String msg,Commit curCommit,String curBranch,String curHashcode,String givenHashcode) {

        Commit newCommit = new Commit();
        // set timeStamp and message  --> MetaData
        Date date = new Date(); // real time
        newCommit.setTimestamp(date);
        newCommit.setMessage(msg);


        // set parent --> parent
        List<String> parentList = new ArrayList<>();
        parentList.add(curHashcode);
        parentList.add(givenHashcode);
        newCommit.setParents(parentList);
        newCommit.setFilesandBlob(curCommit.getFilesCommitBlob());


        // refresh the inherentHashMap
        HashMap<String,String> inherentHashMap = newCommit.getFilesCommitBlob();

        // add
        HashMap<String,String> newStagingInfo = new HashMap<>();
        newStagingInfo = Utils.readObject(STAGING_AREA,HashMap.class);
        for(Map.Entry<String,String> entry: newStagingInfo.entrySet()) {
            // go through the STAGING AREA and overwrite
            String filePath = entry.getKey();
            String fileHashCode = entry.getValue();
            inherentHashMap.put(filePath,fileHashCode); // overwrite
        }

        // remove
        HashSet<String> newRemoveInfo = new HashSet<>();
        newRemoveInfo = Utils.readObject(REMOVE_INDEX,HashSet.class);
        for(String pathName:newRemoveInfo) {
            inherentHashMap.remove(pathName);
        }

        // .git/objects --> store the newCommit
        byte[] serializedNewCommit = Utils.serialize(newCommit);
        String newCommitHashcode = Utils.sha1(serializedNewCommit);
        String newCommitHashDirName = newCommitHashcode.substring(0,2); // get first 2 chars for the file name
        String newCommitHashFileName = newCommitHashcode.substring(2);
        // .git/objects/xx
        File newCommitHashFileDir = join(OBJECTS,newCommitHashDirName);
        if (!newCommitHashFileDir.exists()) {
            newCommitHashFileDir.mkdir();
        }
        // .git/objects/xx/...
        File newCommitHashFile = join(newCommitHashFileDir,newCommitHashFileName);
        if(!newCommitHashFile.exists()) {
            // if it doesn't exist --> create a new file
            // if it does exist --> same hashcode --> objects don't change
            // don't serialize
            writeObject(newCommitHashFile,newCommit);
        }

        //.git/index  --> clear
        HashMap<String,String> emptyHashmap = new HashMap<>();
        writeObject(STAGING_AREA,emptyHashmap);

        //.git/rm_index --> clear
        HashSet<String> emptyHashset = new HashSet<>();
        writeObject(REMOVE_INDEX,emptyHashset);

        //.git/refs/heads/master  --> change HEAD
        File headFile = join(Heads,curBranch);
        writeContents(headFile,newCommitHashcode);


    }

    public byte[] case8action(String curHashcode,String givenHashcode) {
        byte[] curContent = null;
        byte[] givenContent = null;
        if(!curHashcode.isEmpty()) {
            curContent = getBlobContent(curHashcode);
        }
        if(!givenHashcode.isEmpty()) {
            givenContent = getBlobContent(givenHashcode);
        }

        String curContentStr = (curContent == null) ? "" : new String(curContent);
        String givenContentStr = (givenContent == null) ? "" : new String(givenContent);

        StringBuilder conflictBuilder = new StringBuilder();
        conflictBuilder.append("<<<<<<< HEAD");
        conflictBuilder.append(curContentStr);
        conflictBuilder.append("=======");
        conflictBuilder.append(givenContentStr);
        conflictBuilder.append(">>>>>>>");

        return Utils.serialize(conflictBuilder);
    }

    public Commit findTheSplitPoint(Commit cur,Commit given) {

        Commit splitPoint = new Commit();
        // find the cur parent
        String curParentHashcode = cur.getFirstParent();
        Commit curParent = getParentCommit(curParentHashcode);
        // find the given parent
        String givenParentHashcode = given.getFirstParent();
        Commit givenParent = getParentCommit(givenParentHashcode);

        while(curParent != null && givenParent != null) {

            if(curParent.equals(givenParent)) {
                splitPoint = curParent;
                break;
            }

            curParentHashcode = curParent.getFirstParent();
            curParent = getParentCommit(curParentHashcode);
            givenParentHashcode = givenParent.getFirstParent();
            givenParent = getParentCommit(givenParentHashcode);

        }
        return splitPoint;
    }

    public Commit getParentCommit(String parentHashcode) {
        File ParentFile = getHashFile(OBJECTS,parentHashcode);
        return readObject(ParentFile,Commit.class);
    }


    public String getHeadHashCode() {
        // 1、read from HEAD --> who is the HEAD
        String headPositionStr = readContentsAsString(HEAD);
        // 2、refs/heads/..(master)
        File headPosition = new File(headPositionStr);
        // return hashcode
        return readContentsAsString(headPosition);
    }

    public File getHashFile(File path,String hashcode) {
        String headHashDirName = hashcode.substring(0,2);
        String headHashFileName = hashcode.substring(2);
        return join(path,headHashDirName,headHashFileName);
    }

    public byte[] getBlobContent(String hashcode) {
        String headHashDirName = hashcode.substring(0,2);
        String headHashFileName = hashcode.substring(2);
        File blobFile = join(OBJECTS,headHashDirName,headHashFileName);
        return readContents(blobFile);
    }








}
