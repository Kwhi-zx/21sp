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
  private ArrayList<File> filesCommit;
  ```

* ```
  private ArrayList<Objects> blob;
  ```




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
  File HEAD = join(GITLET_DIR,"HEAD");
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

