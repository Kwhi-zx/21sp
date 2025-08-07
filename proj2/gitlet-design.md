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
         --> readContent 
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
  	|---index(暂存区) --> (文件路径:文件内容哈希值) i.e (wug.txt:...) sha1(contents) 
  	|				 --> write/readObject(xxx,Hashmap.class) (add)
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
  
  

#### function

* 
  
  ~~~markdown
  # 路径问题
  因为代码运行在CWD下，文件一般以相对路径的形式出现，i.e: wug.txt, files/wug.txt
  此时，f.getPath() 获得的也是相对路径，i.e: wug.txt, files/wug.txt
  因此，对于需要与存储的相对路径比较的文件名称，不用join(CWD,filename)
  
  # git reset 与 git checkout之间的区别
· 处理完成后，HEAD指针指向不同。
  
  # 逻辑图注释
  --- : 解释
  --> : 下一步
  
  ~~~
  
  ![1754536724990](C:\Users\HUAWEI\AppData\Roaming\Typora\typora-user-images\1754536724990.png)
  
* init()

  ~~~java
  // 在当前目录中创建一个新的 Gitlet 版本控制系统
  // 如果当前目录中已经存在一个 Gitlet 版本控制系统，它应该中止。
  
  # 创建gitlet所需要的文件、目录
  # 初始化Commit
    
      Commit initCommit  
  1、初始化HEAD指针
  --> writeContents() .gitlet/refs/heads/master to .gitlet/HEAD    
  2、计算出Commit的哈希值，并将其存储到 ...    
  --> Utils.serialize(initCommit);(byte[]类型)
  --> hashcode = Utils.sha1(); // 计算出该Commit的哈希值 i.e f3af0eb43
  --> writeContents() hashcode to /refs/heads/master  
  3、存储initCommit
  --> writeObjects() initCommit to .gitlet/objects/f3/af0eb43    
  ~~~

* add(File name)

  ~~~java
  // 将文件当前存在的副本添加到暂存区
  // 如果文件不存在，则打印错误消息 File does not exist.
  
  // 获得文件内容、哈希值、文件路径
  // name如果是以相对路径写入，则获取的路径也是相对路径
  byte[] contents = readContents(name);
  String hashcode = Utils.sha1(contents);
  String path = name.getPath(); 
  // 获得头指针的Commit -- curCommit
  
  // logic pic
  
      read from STAGING_AREA(.gitlet/index)
  --- 如果已经暂存的文件再次暂存，则会用新内容覆盖暂存区中的先前条目
  --> 
  ~~~

* commit()

  ~~~
  
  ~~~

* rm()

  ~~~
  
  ~~~

* log()

  ~~~
  
  ~~~

* global-log()

  ~~~
  
  ~~~

* find()

  ~~~
  
  ~~~

  

* status()

  ~~~
  
  ~~~

  

* checkout()

  ~~~
  
  ~~~

  

* branch()

  ~~~
  
  ~~~

  

* rm-branch()

  ~~~
  
  ~~~

  

* reset()

  ~~~
  
  ~~~

  

* merge()

  ~~~
  
  ~~~

### Utils

~~~java
# blob的内容
byte[] content = serialize();
// writeObject(file,content);
writeContent(file,content);


# hashcode
writecontent(file,hashcode)
    
    
# 数据结构
Commit commit // Hashmap hm
writeObject(file,commit/hm);
readObject(file,Commit.class/Hashmap.class)
~~~



## Algorithms

## Persistence



