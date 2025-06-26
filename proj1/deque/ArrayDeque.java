package deque;

import java.util.Iterator;

public class ArrayDeque<Item> implements Deque<Item>,Iterable<Item>{
    private int size;
    private Item[] items;
    private int nextFirst;
    private int nextLast;

    //Creates an empty array deque.
    public ArrayDeque()
    {
        size = 0;
        items = (Item[]) new Object[8]; //The starting size of your array should be 8.
        nextFirst = 4; // |0|1|2|3|4|5|6|7|  8/2 = 4
        nextLast = 5;
    }

    private void resize(int capacity)
    {
        Item[] temp = (Item[]) new Object[capacity];
        int dstPos = temp.length/2 - size/2;
        System.arraycopy(items,nextFirst+1,temp,dstPos,size);
        items = temp;

        nextFirst = dstPos-1;
        nextLast  = dstPos+size;
    }

    @Override
    public void addFirst(Item item)
    {
        if(nextFirst < 0 ){
            resize(size * 2);
        }
        items[nextFirst] = item;
        size += 1;
        nextFirst -= 1;
    }

    @Override
    public void addLast(Item item)
    {
        if(nextLast >= items.length){
            resize(size*2);
        }

        items[nextLast] = item;
        size += 1;
        nextLast += 1;

    }

//    @Override
//    public boolean isEmpty()
//    {
//        if(size == 0)
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
        for(int i = nextFirst+1;i < nextLast; i++)
        {
            if(i == nextLast-1){
                System.out.println(items[i] + " ");
                break;
            }
            System.out.print(items[i] + " ");
        }
    }

    @Override
    public Item removeFirst()
    {
        if(items.length >= 16 && size*4 < items.length){
            resize(items.length/4);
        }

        if(items[nextFirst+1]==null){
            return null;
        }
        nextFirst += 1;
        Item res = items[nextFirst];
        items[nextFirst] = null;    //remove the First item
        size -=1;
        return res;  //return the removed's next item
    }

    @Override
    public Item removeLast(){
        //resize
        if(items.length >=16 && size*4 < items.length){
            resize(items.length/4);
        }

        if(items[nextLast-1] == null){
            return null;
        }
        nextLast -= 1;
        Item res = items[nextLast];
        items[nextLast] = null;
        size -= 1;
        return res;
    }


    @Override
    public Item get(int index){
        return items[nextFirst + 1 + index];
    }

    public Iterator<Item> iterator(){
        return new ArrayDequeIterator();
    }

    public class ArrayDequeIterator implements Iterator<Item>{
        private int wizPos;

        public ArrayDequeIterator(){
            wizPos = 0;
        }

        public boolean hasNext(){
            return wizPos < size;
        }
        public Item next(){
            Item returnItem = get(wizPos);
            wizPos += 1;
            return returnItem;
        }
    }

//    public boolean equals(Object obj){
//        if(obj instanceof ArrayDeque){
//            ArrayDeque<?> ad = (ArrayDeque<?>) obj;
//            if(ad.size != this.size){
//                return false;
//            }
//            for(int i=0;i<this.size;i++){
//                if(ad.get(i) != this.get(i)){
//                    return false;
//                }
//            }
//            return true;
//        }
//        return false;
//    }
    public boolean equals(Object obj){
        if(obj instanceof Deque){
            Deque<?> deque = (Deque<?>) obj;
            if(this.size != deque.size()){
                return false;
            }
            for(int i=0;i<this.size;i++){
                if(this.get(i) != deque.get(i)){
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static void main(String args[]){
        ArrayDeque<Integer> test = new ArrayDeque();
        ArrayDeque<Integer> test1 = new ArrayDeque();
        test.addFirst(2);
        test.addLast(3);
        test1.addFirst(2);
        test1.addLast(3);

        test.printDeque();
        Iterator<Integer> testIter = test.iterator();
        while(testIter.hasNext()){
            int i = testIter.next();
            System.out.println(i);
        }
//        System.out.println(test.get(0));
        System.out.println("equals? "+ test.equals(test1));
        for(int x:test){
            System.out.println(x);
        }
    }

}
