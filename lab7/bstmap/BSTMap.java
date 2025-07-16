package bstmap;


import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>,V> implements Map61B<K,V> {
    private BSTNode root;   // root of the BSTMap
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
//        throw new UnsupportedOperationException("keySet not support");
        Set<K> keyset = new HashSet<>();
        // object inference
        keySet(this.root,keyset);
        return keyset;
    }

    private Set<K> keySet(BSTNode x,Set<K> ks) {
        if(x == null) return null;

        keySet(x.left,ks);
        ks.add(x.key);
        keySet(x.right,ks);
        return ks;
    }

    // remove problem has 3 cases:
    // 1\ Key with no children
    // 2\ Key with 1 child
    // 3\ Key with 2 children

    public V remove(K key) {
//        throw new UnsupportedOperationException("remove not support");
        if(key == null) throw new IllegalArgumentException("calls remove() with a null key");
        if(!containsKey(key)) return null;

        V removedVal = get(key);
        this.root = remove(this.root, key);
        return removedVal;
    }

    private BSTNode remove(BSTNode x, K key) {
        if(x == null) return null;

        int cmp = x.key.compareTo(key);
        // In-order traversal to find the goal
        if(cmp < 0) x.right = remove(x.right, key);
        else if (cmp > 0) x.left = remove(x.left, key);
        else {
            // case 1 && 2(no left child)
            if(x.left == null) return x.right;
            // case 2(no right child)
            if(x.right == null) return x.left;

            // case 3: has two children  (algorithm: Hibbard deletion)
            BSTNode successor = findMin(x.right);
            x.value = successor.value;
            x.key = successor.key;
            x.right = remove(x.right, successor.key); // remove the successor
        }
        x.size = size(x.left) + size(x.right) + 1;
        return x;
    }

    public V remove(K key, V value) {
//        throw new UnsupportedOperationException("remove not support");
        if(key == null) throw new IllegalArgumentException("calls remove() with a null key");
        if(!containsKey(key)) return null;
        if(get(key) != value) throw new IllegalArgumentException("calls remove() with a wrong key-value");

        V removeVal = get(key);
        this.root = remove(this.root, key);
        return removeVal;
    }

    private BSTNode findMin(BSTNode x) {
        while(x.left != null) x = x.left;
        return x;
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
//        Set<String> ks;
//        b.put("bee",2);
//        b.put("ant", 3);
//        b.put("cat",1);
//        b.put("dog",8);
//        b.put("elephant",21);
//        b.put("pig",30);
//        b.put("monkey",40);
//        b.printInOrder();
//        System.out.println("keyset: "+b.keySet());
////        Integer x = b.remove("dog");
////        Integer x = b.remove("cat",1);
////        b.printInOrder();
////        System.out.println(b.size());
////        System.out.println(x);
//    }



}
