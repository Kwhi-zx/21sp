package gitlet;

import net.sf.saxon.trans.SymbolicName;

import java.io.File;
import java.util.*;

import static gitlet.Utils.*;

// TODO: any imports you need here
import java.nio.file.Path;
import java.io.IOException;

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author wzx
 */
public class Repository {
    /**
     * TODO: add instance variables here.
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


    public static final File LOGS = join(GITLET_DIR,"logs");

    /* TODO: fill in the rest of this class. */

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
        writeContents(HEAD,"refs/heads/master"); // "yeah,the HEAD pointer --->" ref: refs/heads/master

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

        byte[] contents = readContents(name);
        String hashCode = Utils.sha1(contents); // sha1 contents

        String hashDirName = hashCode.substring(0,2); // get first 2 chars for the file name
        String hashFileName = hashCode.substring(2);

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
        }

        // .git/index
        // persistence --> read from file
        HashMap<String,String> oldStagingInfo = new HashMap<>();

        if(STAGING_AREA.length() !=0) {
            oldStagingInfo = readObject(STAGING_AREA, HashMap.class);
        }

        // store filePath and hashCode
        String fileRelativePaths = name.getPath();
        // if two files have the same filePath
        if(!oldStagingInfo.isEmpty() &&
         oldStagingInfo.containsKey(fileRelativePaths)) {
            // case1: a.txt --> a.txt  --> content doesn't change
            // --> it should pass
            String hashvalue = oldStagingInfo.get(fileRelativePaths);
            if(hashvalue.equals(hashCode)) {
                return;
            }
        }
        // TODO: case4: COMMIT VERSION
        // case2: a.txt & b.txt    --> same as case3: create a new one
        oldStagingInfo.put(fileRelativePaths,hashCode);
        byte[] serializedStagingInfo = Utils.serialize(oldStagingInfo);
        Utils.writeObject(STAGING_AREA,serializedStagingInfo); // overwrites
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
        Commit parentCommit = readObject(parentContentFile,Commit.class);

        newCommit.setParent(parentCommitHash);
        newCommit.setFilesandBlob(parentCommit.getFilesCommitBlob());

        // .git/index
        // TODO: rm
        if(STAGING_AREA.length() == 0) {
            // if staging area is empty
            System.out.println("Everything up-to-date!");
            System.exit(0);
        }

        // refresh the inherentHashMap
        HashMap<String,String> newStagingInfo = new HashMap<>();
        HashMap<String,String> inherentHashMap = newCommit.getFilesCommitBlob();
        newStagingInfo = readObject(STAGING_AREA,HashMap.class);
        for(Map.Entry<String,String> entry: newStagingInfo.entrySet()) {
            // go through the STAGING AREA and overwrite
            String filePath = entry.getKey();
            String fileHashCode = entry.getValue();
            inherentHashMap.put(filePath,fileHashCode); // overwrite
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
            writeObject(newCommitHashFile,serializedNewCommit);
        }

        //TODO: .git/index  --> clear
        byte[] serializedemptyHashmap = Utils.serialize(new HashMap<String,String>());
        writeObject(STAGING_AREA,serializedemptyHashmap);

        //TODO: .git/HEAD --> store the new branch


        //TODO: .git/logs
    }

    @SuppressWarnings("unchecked")
    public void rmCommand(File name) {

        boolean tracked = false;
        boolean added = false;
        // check it if add --> in the staging area
        HashMap<String,String> indexContent = readObject(STAGING_AREA,HashMap.class);
        if(indexContent.containsKey(name.getPath())) {
            indexContent.remove(name.getPath());
            byte[] serializedIndexContent = Utils.serialize(indexContent);
            writeObject(STAGING_AREA,serializedIndexContent); // refresh the STAGING_AREA
            added = true;
        }

        // check it if tracked
        // read from HEAD
        String headHashCode = getHeadHashCode();
        File headCommitFile = getHashFile(OBJECTS,headHashCode);
        Commit headCommit = readObject(headCommitFile, Commit.class);
        if(headCommit.getFilesCommitBlob().containsKey(name.getPath())) {
            tracked = true;
        }

        if(tracked) {
            // do not remove it unless it is tracked in the current commit
            // so it is tracked here --> remove it from CWD
            // stage it for removal --> Hashset
            HashSet<String> removalHashSet = new HashSet<>();
            if(REMOVE_INDEX.length() != 0) {
                removalHashSet = readObject(REMOVE_INDEX, HashSet.class);
            }
            removalHashSet.add(name.getPath());
            byte[] serializedRemovalPath = Utils.serialize(removalHashSet);
            writeObject(REMOVE_INDEX,serializedRemovalPath);
            name.delete();
        }

        if(!added && !tracked) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
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


}
