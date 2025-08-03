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
           writeObject(hashFile,contents);
//           writeContents(hashFile,contents);
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

        newCommit.setParent(parentCommitHash);
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

    public void log() {

        // define string format
        String logFormat = "===";
        String formatString = "commit %s%nDate: %tc%n%s%n";

        // Linear History
        // The HEAD
        String headHashcode = getHeadHashCode();
        File headHashFile = getHashFile(OBJECTS,headHashcode);
        Commit headCommit = Utils.readObject(headHashFile,Commit.class);

        String output = String.format(formatString,
                                        headHashcode,
                                        headCommit.getTimestamp(),
                                        headCommit.getMessage());
        System.out.println(logFormat);
        System.out.println(output);

        // iterate the parent
        String childHashcode = headCommit.getParent();
        while (childHashcode !=null) {
            System.out.println(logFormat); // ===
            File childHashFile = getHashFile(OBJECTS, childHashcode);
            Commit childCommit = Utils.readObject(childHashFile, Commit.class);
            String childOutput = String.format(formatString,
                                                childHashcode,
                                                childCommit.getTimestamp(),
                                                childCommit.getMessage());
            System.out.println(childOutput);
            childHashcode = childCommit.getParent();
        }


        //TODO: merge
    }

    public void globalLog() {

        // displays information about all commits ever made.
        // The order of the commits does not matter.
        String logFormat = "===";
        String formatString = "commit %s%nDate: %tc%n%s%n";

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
                            String output = String.format(formatString,
                                    hashcode,
                                    logCommit.getTimestamp(),
                                    logCommit.getMessage());
                            System.out.println(logFormat);
                            System.out.println(output);
                        } catch (Exception e) {
                            // Just ignore it and continue to the next file.
                        }
                    }
            }
        }

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

        /** Branches */
        List<String> branchesList = plainFilenamesIn(Heads); // list:[master、main、skeleton...]
        String headHashcode = getHeadHashCode();
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
            
//            writeContents(cwdFile,blobContent);
            writeObject(cwdFile, blobContent); // automatically create the file if it doesn't exist
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

        // get the cur Commit
        // .gitlet/HEAD  --> check if it is
        String headPositionStr = readContentsAsString(HEAD);
        // .gitlet/heads/master
        File curHeadPath = new File(headPositionStr);
        if(branchName.equals(curHeadPath.getName())) {
            System.out.println("No need to checkout the current branch.");
            return;
        }

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
                File f = new File(filename);
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
                File f = new File(filename);
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
//                writeContents(cwdBlobFile,blobContent);
                writeObject(cwdBlobFile,blobContent);
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

    }

    public void merge(String branchName) {

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
    public String getRelativePath(File mainPath,File name) {
        Path pathMain = mainPath.toPath(); // CWD/OBJECTS...
        Path inputPath = name.toPath();    // file name
        Path relativePath = pathMain.relativize(inputPath);
        return relativePath.toString().replace('\\', '/');
    }



}
