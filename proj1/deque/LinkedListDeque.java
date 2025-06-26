package deque;


import java.util.Iterator;

public class LinkedListDeque<Item> implements Deque<Item> ,Iterable<Item>
{
    /**
     *  create a Node class for ListDeque
     * */
    private class IntNode
    {
        public Item item;
        public IntNode next;
        public IntNode prev;

        public IntNode(Item i,IntNode n,IntNode pre)
        {
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
    public LinkedListDeque()
    {
        sentinel = new IntNode(null,null,null);
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
        size = 0;
    }

    public LinkedListDeque(Item item)
    {
        sentinel = new IntNode(null,sentinel,sentinel);
        sentinel.next = new IntNode(item,sentinel,sentinel);
        sentinel.prev = sentinel.next;
        size = 1;
    }
    @Override
    public void addFirst(Item item)
    {
        IntNode temp = sentinel.next;
        sentinel.next = new IntNode(item,temp,sentinel);    // sentinel <--> new_firstNode --> temp
        temp.prev = sentinel.next;  // sentinel <--> new_firstNode <--> temp
        size +=1;
    }
    @Override
    public void addLast(Item item)
    {
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
    public int size()
    {
        return size;
    }

    @Override
    public void printDeque()
    {
        IntNode p = sentinel;
        while(p.next != sentinel)
        {
            if(p == sentinel){
                p = p.next;
                continue;
            }
            System.out.print(p.item + " ");
            p = p.next;
        }

        if(p!=sentinel){
            System.out.println(p.item);
        }

    }

    @Override
    public Item removeFirst()
    {
        IntNode p = sentinel;
        IntNode nextNode = p.next;
        IntNode next2Node = nextNode.next;
        // assert not null
        if(p.next != sentinel){
            Item res = nextNode.item;
            p.next = next2Node;
            next2Node.prev = p;
            nextNode = null;
            size -= 1;
            return res;
        }
        return null;
    }

    @Override
    public Item removeLast()
    {
        IntNode p = sentinel;
        IntNode lastNode = p.prev;
        IntNode last2Node = lastNode.prev;
        //assert not null
        if(p.prev != sentinel){
            Item res = lastNode.item;
            p.prev = last2Node;
            last2Node.next = p;
            lastNode = null;
            size -= 1;
            return res;
        }
        return  null;
    }

    @Override
    public Item get(int index)
    {
        IntNode p = sentinel;
        for(int i=0;i<index;i++)
        {
            p = p.next;
        }
        return p.item;
    }

    private Item getRecursiveHelper(IntNode p,int index)
    {
        if(index == 1){
            return p.item;
        }
        return getRecursiveHelper(p.next,index - 1);
    }
    // same as get() but use recursion
    public Item getRecursive(int index)
    {
        if(index < 0 || index >= size)
        {
            return null;
        }
        return getRecursiveHelper(sentinel.next,index);
    }

    public Iterator<Item> iterator(){
        return new LinkListIterator();
    }

    private class LinkListIterator implements Iterator<Item>{
        private IntNode wizSentinel;

        public LinkListIterator(){
            wizSentinel = sentinel.next;
        }

        public boolean hasNext(){
            return wizSentinel != sentinel;
        }

        public Item next(){
            Item returnItem = wizSentinel.item;
            wizSentinel = wizSentinel.next;
            return returnItem;
        }

    }

    public boolean equals(Object obj){
        if(obj instanceof LinkedListDeque lks){
            if(lks.size != this.size){
                return false;
            }
            IntNode p = this.sentinel.next;
            IntNode l = lks.sentinel.next;
            for(int i=0;i<this.size;i++){
                if(p.item != l.item){
                    return false;
                }
                p = p.next;
                l = l.next;
            }

            return true;
        }
        return false;
    }

    public static void main(String args[])
    {

//        LinkedListDeque<Integer> noNode = new LinkedListDeque();
//        LinkedListDeque<Integer> noNode1 = new LinkedListDeque();
        LinkedListDeque<Integer> intNode = new LinkedListDeque(5);
//        LinkedListDeque<Integer> anotherNode = new LinkedListDeque(5);
        intNode.addFirst(3);
        intNode.addLast(6);
//        anotherNode.addFirst(3);
//        anotherNode.addLast(6);
        intNode.printDeque();
//        System.out.println("intNode size: "+ intNode.size());

//        intNode.removeFirst();
//        intNode.printDeque();

//        intNode.removeLast();
//        intNode.printDeque();

//        System.out.println(intNode.get(2));
//        System.out.println(intNode.getRecursive(2));
//            System.out.println("equal? "+ intNode.equals(anotherNode));
//            System.out.println("equal? "+ noNode.equals(noNode1));

        /**
         *  iterator test
         * */
//        Iterator<Integer>  intIter  = intNode.iterator();
//        while(intIter.hasNext()){
//            int i = intIter.next();
//            System.out.println(i);
//        }
        for(int x:intNode){
            System.out.println(x);
        }
    }

}
