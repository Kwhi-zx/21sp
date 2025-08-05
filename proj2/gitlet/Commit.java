package gitlet;

// any imports you need here


import java.io.Serializable;
import java.util.*;


/** Represents a gitlet commit object.
 *  It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author wzx
 */
public class Commit implements Serializable {
    /**
     * add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;
    // instance
    private Date timestamp;
//    private String parent;
//    private boolean merged;
    private List<String> parents;
    private HashMap<String,String> filesandBlob; // hello.txt && hashcode


    /* fill in the rest of this class. */
    public Commit() {
        this.timestamp = new Date(0); // 00:00:00 UTC, Thursday, 1 January 1970
        this.message = "initial commit";
//        this.parent = null;
//        this.merged = false;
        this.parents = new ArrayList<>();
        this.filesandBlob = new HashMap<>();

    }

    /**  get method */
    public Date getTimestamp() {
        return this.timestamp;
    }
    public String getMessage() {
        return this.message;
    }
    public String getFirstParent() {
        if(parents != null && !parents.isEmpty()) {
            return parents.get(0);
        }
        return null;
    }
    public boolean getMerge() {
        if(parents != null && parents.size() > 1) {
            return true;
        }
        return false;
    }
    public List<String> getParents() {return this.parents;}
    public HashMap<String, String> getFilesCommitBlob() {
        return filesandBlob;
    }



    /** set method*/
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
    public void setMessage(String msg) {
        this.message = msg;
    }

    public void setParents(List<String> parentsHashcodeList) {
        this.parents = parentsHashcodeList;
    }



    public void setFilesandBlob(HashMap<String, String> hashMap) {
        for(Map.Entry<String,String> entry:hashMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            this.filesandBlob.put(key,value);
        }
    }

}
