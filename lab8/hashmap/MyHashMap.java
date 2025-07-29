package hashmap;


import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.collections.FastArrayList;

import java.util.*;

import static org.junit.Assert.assertTrue;


/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author 
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    // set default value for loadFactor and initialSize
    private static final int DEFAULT_INITIAL_SIZE = 16;
    private static final double DEFAULT_LOAD_FACTOR = 0.75;

    /* Instance Variables */
    private Collection<Node>[] buckets;
    // You should probably define some more!
    private int kvSize;    // number of key-value pairs
//    private int bucketNum; // number of  hash table
    private double maxLoadFactor;

    /** Constructors */
    public MyHashMap() {
        this(DEFAULT_INITIAL_SIZE,DEFAULT_LOAD_FACTOR);
    }

    public MyHashMap(int initialSize) {
        this(initialSize,DEFAULT_LOAD_FACTOR);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */

    public MyHashMap(int initialSize, double maxLoad) {
        this.buckets = createTable(initialSize);
        this.kvSize = 0;
        this.maxLoadFactor = maxLoad;
//        bucketNum = initialSize;
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key,value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        MyHashMapALBuckets<K,V> myHashmapBucket = new MyHashMapALBuckets<>(); // Array List buckets
//        MyHashMapHSBuckets<K,V> myHashmapBucket = new MyHashMapHSBuckets<>(); // Hash Sets buckets
//        MyHashMapLLBuckets<K,V> myHashmapBucket = new MyHashMapLLBuckets<>(); // Linked List buckets
//        MyHashMapPQBuckets<K,V> myHashmapBucket = new MyHashMapPQBuckets<>(); // Priority Queue buckets
//        MyHashMapTSBuckets<K,V> myHashmapBucket = new MyHashMapTSBuckets<>(); // Tree Set buckets

        return myHashmapBucket.createBucket();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    @SuppressWarnings("unchecked")
    private Collection<Node>[] createTable(int tableSize) {
        return (Collection<Node>[]) new Collection[tableSize];
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!
    public void clear() {
        int bmLen = buckets.length;
        Collection<Node> bucket;
        for(int i=0; i < bmLen; i++) {
            bucket = buckets[i];
            if(bucket != null) {
                bucket.clear();
            }
        }
        kvSize = 0;
    }

    public boolean containsKey(K key) {
        for(Collection<Node> bucket:buckets) {
            if(bucket == null) {
                continue;
            }
            for(Node node:bucket) {
                if(node.key.equals(key)) {
                    return true;
                }
            }
        }
        return false;
    }

    public V get(K key) {
        for(Collection<Node> bucket:buckets) {
            if(bucket == null) {
                continue;
            }
            for(Node node:bucket) {
                if(node.key.equals(key)) {
                    return node.value;
                }
            }
        }
        return null;
    }

    public int size() {
        return kvSize;
    }

    /*
    *   注解：
    *   Hash Collision(哈希冲突): two different keys have the same bucket index
    *   Key Dup(键重复): some key has exits
    * */
    public void put(K key,V value) {
        int bucketIndex = Math.floorMod(key.hashCode(), buckets.length);// 使用模 hashcode可能是正、负;并且该数可能不在索引范围内
        Collection<Node> bucket = buckets[bucketIndex];
        Node newPair = new Node(key,value);
        if(bucket == null) {
            // 空桶
            bucket = createBucket();
            buckets[bucketIndex] = bucket; // 放回
            bucket.add(newPair);
            kvSize += 1;
        }else {
            // not null
            boolean keyExits = false;
            for(Node node:bucket) {
                if(node.key.equals(key)) {
                    // Key Dup
                    node.value = value;
                    keyExits = true;
                    break;
                }
            }
            if(!keyExits) {
                // not Key Dup
                bucket.add(newPair);
                kvSize += 1;
            }
        }

        if((double) kvSize / buckets.length > maxLoadFactor) {
            resize(buckets.length*2);
        }
    }

    private void resize(int capacity) {
        Collection<Node>[] newBuckets = createTable(capacity);
        for(Collection<Node> bucket:buckets) {
            if(bucket == null) {
                continue;
            }
            for(Node node:bucket) {
                // new bucketNum --> different index
                int newBucketIndex = Math.floorMod(node.key.hashCode(),capacity);
                if(newBuckets[newBucketIndex] == null) {
                    newBuckets[newBucketIndex] = createBucket();
                }
                newBuckets[newBucketIndex].add(node);
            }
        }
        buckets = newBuckets;
    }

    public Set<K> keySet() {
        Set<K> resultKeySet = new HashSet<>();
        for(Collection<Node> bucket:buckets) {
            if(bucket == null) {
                continue;
            }
            for(Node node:bucket) {
                resultKeySet.add(node.key);
            }
        }
        return resultKeySet;
    }

    public V remove(K key) {
        V removedvalue = null;
        for(Collection<Node> bucket:buckets) {
            if(bucket == null) {
                continue;
            }
            for(Node node:bucket) {
                if(node.key.equals(key)) {
                    removedvalue = node.value;
                    bucket.remove(node);
                    kvSize -= 1;
                    return removedvalue;
                }
            }
        }
        return removedvalue;
    }

    public V remove(K key, V value) {
        V removedvalue = null;
        for(Collection<Node> bucket:buckets) {
            if(bucket == null) {
                continue;
            }
            for(Node node:bucket) {
                if(node.key.equals(key) && node.value.equals(value)) {
                    removedvalue = node.value;
                    bucket.remove(node);
                    kvSize -= 1;
                    return removedvalue;
                }
            }
        }
        return removedvalue;
    }

    @Override
    public Iterator<K> iterator() {
        return new MyHashMapIterator();
    }

//    private class MyHashMapIterator implements Iterator<K> {
//        private Deque<Node> deque;
//        public MyHashMapIterator() {
//            deque = new ArrayDeque<>();
//            for(Collection<Node> bucket:buckets) {
//                if(bucket == null) {
//                    continue;
//                }
//                for(Node node:bucket) {
//                    deque.addFirst(node);
//                }
//            }
//        }
//
//        @Override
//        public boolean hasNext() {
//            return !deque.isEmpty();
//        }
//
//        @Override
//        public K next() {
//            if(!hasNext()) {
//                throw new NoSuchElementException("No more elements in the map.");
//            }
//            Node firstNode = deque.removeFirst();
//            return firstNode.key;
//        }
//    }

    /*
        Lazy Iterator by Gemini
    */
    private class MyHashMapIterator implements Iterator<K> {
        private int curBucketIdx; // 指示遍历到哪个bucket
        private Iterator<Node> curBucketIter; // 用于遍历当前bucket中的Node

        public MyHashMapIterator() {
            curBucketIdx = 0;
            curBucketIter = null;
            // Find the first valid node to start
            findNextValidNode();

        }

        @Override
        public boolean hasNext() {
            // We have a next element if the current iterator exists and has a next element.
            return curBucketIter != null && curBucketIter.hasNext();
        }

        @Override
        public K next() {
            if(!hasNext()) {
                throw new NoSuchElementException("No more elements in the map.");
            }
            // Get the next node from the current bucket's iterator
            Node nextNode = curBucketIter.next();
            // find the next available node to prepare for the next call to hasNext()/next()
            findNextValidNode();
            return nextNode.key;
        }

        private void findNextValidNode() {
            // already exist
            // 当前 bucket 还有 node，不需要换
            if(curBucketIter != null && curBucketIter.hasNext()) {
                return;
            }

            // otherwise
            while(curBucketIdx < buckets.length) {
                Collection<Node> bucket = buckets[curBucketIdx];
                if(bucket != null && !bucket.isEmpty()) {
                    this.curBucketIter = bucket.iterator();
                    return;
                }
                curBucketIdx++;
            }

            this.curBucketIter = null;
        }
    }



}
