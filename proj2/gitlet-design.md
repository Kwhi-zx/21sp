# Gitlet Design Document

**Name**:wzx

## Classes and Data Structures

### Commit (implements Serializable)

#### Instance

* ```java
  private String message;
  private Date timestamp;
private String parent;
  // 用于存储此Commit中修改的文件路径以及其哈希值
  // (key:value) --> (path:hashcode)
  private HashMap<String,String> filesandBlob; // hello.txt && hashcode
  
  
  ------------------------------------------
  |		| metadata: message && timestamp |
  |parent |---------------------------------
  |		|	Hashmap<String,String>		 |
  |		|	: <path:hashcode>			 |							 
  ------------------------------------------
  path: i.e hello.txt    
  hashcode: i.e 0e93cac    
      
  logic pic:
  --------------------------------------------------------------------------------------
         HEAD (.gitlet/HEAD) 
         --> refs/heads/..(i.e master) 
         --> Commit hashcode(i.e 5e6194cbf)
         --> .gitlet/objects/5e/6194cbf  (这里存储的是Commit class)
         --> readObject(Commit.class)  --> hashmap(i.e 0e93cac) 
         --> .gitlet/objects/0e/93cac... 
         --> readObject(byte[])/readContent 
         --> 获得文件内容
  --------------------------------------------------------------------------------------
  ```
  

![1754126923485](C:\Users\HUAWEI\AppData\Roaming\Typora\typora-user-images\1754126923485.png)



#### function

* get function

* set function

  


### Repository

#### Instance

* ```java
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
  ```
  
* ~~~
  .gitlet
  	|
  	|---objects(dir)
  	|	    |
  	|		|---6a(initCommit)  ---> write/readObject(xxx,Commit.class)  (init)
  	|		|	|---7b...
  	|		|
  	|		|---4b(File content (Blob)) ---> write/readObject(xxx,byte[])   (add)
  	|		|	|---8d...
  	|		|
  	|		|---2d(new Commit)	(add)
  	|		|	|---3c
  	|
  	|
  	|---index(暂存区) --> (path:hashcode) --> write/readObject(xxx,Hashmap.class) (add)
  	|	  
  	|
  	|---rm_index(暂存区) --> (path) --> (xxx,HashSet.class) (rm)
  	|
  	|---refs
  	|	 |
  	|	 |--heads
  	|	 |	  |
  	|	 |	  |--master:Commit's hashcode
  	|	 |	  |	
      |	 |	  |--cool-beans: 
  	|	 |
  	|
  	|
  	|---HEAD:refs/heads/xxx
  	|
  
  
  
  ~~~
  
  

### Utils

~~~
# 文本内容
byte[] content = serialize();
writeObject(file,content);

# 数据结构
Commit commit // Hashmap hm
writeObject(file,commit/hm);
readObject(file,Commit.class/Hashmap.class)

# hashcode
writecontent(file,hashcode)
~~~



## Algorithms

## Persistence



