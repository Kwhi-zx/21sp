package bstmap;

import edu.princeton.cs.algs4.BST;
import net.sf.saxon.functions.NamespaceUri_1;
import org.junit.Test;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class BSTMap<K extends Comparable<K>,V> implements Map61B<K,V> {
    BSTNode root;   // root of the BSTMap
    private class BSTNode {
        private K key;
        private V value;
        private BSTNode left, right;
        int size;

        public BSTNode(K key, V value, int size) {
            this.key = key;
            this.value = value;
            this.size = size;
        }
    }

    // Initializes an empty Map
    public BSTMap() {

    }

    public void clear() {
        this.root = clear(this.root);
    }

    private BSTNode clear(BSTNode x) {
        if(x == null) return null;

        clear(x.left);
        clear(x.right);
        x = null;
        return x;
    }

    public boolean containsKey(K key) {
        if(key == null) throw new IllegalArgumentException("calls containsKey() with a null key");

        if(containsKey(this.root, key) != null) return true;
        return false;
    }

    private K containsKey(BSTNode x, K key) {
        if(x == null) return null;
        int cmp = x.key.compareTo(key);
        if(cmp < 0) return containsKey(x.right, key);
        else if (cmp > 0) return containsKey(x.left, key);
        else return x.key;
    }

    public V get(K key) {
        if(key == null) throw new IllegalArgumentException("calls get() with a null key");
        return get(this.root,key);
    }

    private V get(BSTNode x, K key) {
        if(x == null) return null;
        int cmp = x.key.compareTo(key);
        if(cmp < 0) return get(x.right,key);
        else if (cmp > 0) return get(x.left,key);
        else return x.value;
    }

    public int size() {
        return size(this.root);
    }

    private int size(BSTNode x) {
        if(x == null) return 0;
        return x.size;
    }

    public void put(K key, V value) {
        if(key == null) throw new IllegalArgumentException("calls put() with a null key");
        this.root = put(this.root, key, value);
    }

    private BSTNode put(BSTNode x, K key, V value) {
        if(x == null) return new BSTNode(key, value,1);
        int cmp = x.key.compareTo(key);
        // x.key < key --> cmp < 0
        if(cmp < 0) x.right = put(x.right, key, value);
        else if (cmp > 0) x.left = put(x.left, key, value);
        else x.value = value;
        x.size = 1 + size(x.left) + size(x.right);
        return x;
    }

    public Set<K> keySet() {
        throw new UnsupportedOperationException("keySet not support");
    }


    public V remove(K key) {
        throw new UnsupportedOperationException("remove not support");
    }

    public V remove(K key, V value) {
        throw new UnsupportedOperationException("remove not support");
    }

    public Iterator<K> iterator() {
//        return null;
        throw new UnsupportedOperationException("iterator not support");
    }

    public void printInOrder() {
        printInOrder(this.root);
    }

    private void printInOrder(BSTNode x) {
        if(x == null) return;

        printInOrder(x.left);
        System.out.println(x.key + ": " + x.value);
        printInOrder(x.right);
    }

//    public static void main(String args[]) {
//        BSTMap<String, Integer> b = new BSTMap<String, Integer>();
//        b.put("bee",null);
//        b.put("ant", null);
//        b.put("cat",null);
//        b.put("dog",null);
//        b.printInOrder();
//    }



}
