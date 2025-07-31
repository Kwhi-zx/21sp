# Gitlet Design Document

**Name**:wzx

## Classes and Data Structures

### Commit (implements Serializable)

#### Instance

* ```
  String message;
  ```

* ```
  Date timestamp;
  ```

* ```
  String parent;
  ```

* ```
  private String hashcode;
  ```

* ```
  private HashMap<String,String> filesandBlob;
  ```

#### function

* get function

  ~~~
  
  ~~~

* set function

  ~~~
  
  ~~~

  


### Repository

#### Instance

* ```java
  File CWD = new File(System.getProperty("user.dir"));
  ```

* ```
  File GITLET_DIR = join(CWD, ".gitlet");
  ```

* ```
  File OBJECTS = join(GITLET_DIR,"objects");
  ```

* ```
  File STAGING_AREA = join(GITLET_DIR,"index");
  ```

* ```
  File REFS = join(GITLET_DIR,"refs");
  ```

* ```
  public static final File Heads = join(REFS,"heads");
  ```

* ```
  public static final File HEAD = join(GITLET_DIR,"HEAD");* 
  ```

* ```
  public static final File LOGS = join(GITLET_DIR,"logs");
  ```

#### function

* ```
  public void initCommand()
  ```

* ```
  public void addCommand(File name)
  ```

## Algorithms

## Persistence

