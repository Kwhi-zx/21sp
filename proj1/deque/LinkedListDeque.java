package deque;


import java.util.Iterator;
import java.util.Objects;

public class LinkedListDeque<T> implements Deque<T> , Iterable<T> {
    /**
     *  create a Node class for ListDeque
     * */
    private class IntNode {
        public T item;
        public IntNode next;
        public IntNode prev;

        public IntNode(T i,IntNode n,IntNode pre) {
            item = i;
            next = n;
            prev = pre;
        }
    }

    private IntNode sentinel;
    private int size;

    /**
     * construction function
     * Creates an empty linked list deque
    */
    public LinkedListDeque() {
        sentinel = new IntNode(null,null,null);
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
        size = 0;
    }

//    public LinkedListDeque(T item)
//    {
//        sentinel = new IntNode(null,sentinel,sentinel);
//        sentinel.next = new IntNode(item,sentinel,sentinel);
//        sentinel.prev = sentinel.next;
//        size = 1;
//    }

    @Override
    public void addFirst(T item) {
        IntNode temp = sentinel.next;
        sentinel.next = new IntNode(item,temp,sentinel);    // sentinel <--> new_firstNode --> temp
        temp.prev = sentinel.next;  // sentinel <--> new_firstNode <--> temp
        size +=1;
    }

    @Override
    public void addLast(T item) {
        IntNode p = sentinel;
        IntNode lastNode = p.prev;

        lastNode.next = new IntNode(item,p,lastNode); // lastNode <--> new_lastNode --> sentinel
        p.prev = lastNode.next; // lastNode --> new_lastNode <--> sentinel
        size = size + 1;
    }

//    @Override
//    public boolean isEmpty()
//    {
//        IntNode p = sentinel;
//        if(p.next == sentinel)
//        {
//            return true;
//        }
//        return false;
//    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        IntNode p = sentinel;
        while(p.next != sentinel) {
            if(p == sentinel) {
                p = p.next;
                continue;
            }
            System.out.print(p.item + " ");
            p = p.next;
        }

        if(p != sentinel) {
            System.out.println(p.item);
        }

    }

    @Override
    public T removeFirst() {
        IntNode p = sentinel;
        IntNode nextNode = p.next;
        IntNode next2Node = nextNode.next;
        // assert not null
        if(p.next != sentinel) {
            T res = nextNode.item;
            p.next = next2Node;
            next2Node.prev = p;
            nextNode = null;
            size -= 1;
            return res;
        }
        return null;
    }

    @Override
    public T removeLast() {
        IntNode p = sentinel;
        IntNode lastNode = p.prev;
        IntNode last2Node = lastNode.prev;
        //assert not null
        if(p.prev != sentinel) {
            T res = lastNode.item;
            p.prev = last2Node;
            last2Node.next = p;
            lastNode = null;
            size -= 1;
            return res;
        }
        return  null;
    }

    @Override
    public T get(int index) {
        IntNode p = sentinel.next; //index 0
        for(int i = 0;i < index; i++) {
            p = p.next;
        }
        return p.item;
    }


    private T getRecursiveHelper(IntNode p,int index) {
        if(index == 0) {
            return p.item;
        }
        return getRecursiveHelper(p.next,index - 1);
    }
    // same as get() but use recursion
    public T getRecursive(int index) {
        if(index < 0 || index >= size) {
            return null;
        }
        return getRecursiveHelper(sentinel.next,index);
    }


    public Iterator<T> iterator() {
        return new LinkListIterator();
    }

    private class LinkListIterator implements Iterator<T> {
        private IntNode wizSentinel;

        public LinkListIterator() {
            wizSentinel = sentinel.next;
        }

        public boolean hasNext() {
            return wizSentinel != sentinel;
        }

        public T next() {
            T returnItem = wizSentinel.item;
            wizSentinel = wizSentinel.next;
            return returnItem;
        }

    }

//    public boolean equals(Object obj){
//        if(obj instanceof LinkedListDeque){
//            LinkedListDeque<?> lks = (LinkedListDeque<?>) obj;
//            if(lks.size != this.size){
//                return false;
//            }
//            IntNode p = this.sentinel.next;
//            IntNode l = (IntNode) lks.sentinel.next;
//            for(int i = 0;i < this.size; i++){
//                if(p.item != l.item){
//                    return false;
//                }
//                p = p.next;
//                l = l.next;
//            }
//
//            return true;
//        }
//        return false;
//    }
    public boolean equals(Object obj) {
        if(obj instanceof Deque) {
            Deque<?> deque = (Deque<?>) obj;
            if(this.size != deque.size()) {
                return false;
            }
            for(int i=0;i<this.size;i++) {
//                if(this.get(i) != deque.get(i))
                if(!Objects.equals(this.get(i),deque.get(i))) {
                    return false;
                }

            }
            return true;
        }
        return false;
    }
}
