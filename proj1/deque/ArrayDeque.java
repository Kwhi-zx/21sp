package deque;

import java.util.Iterator;

public class ArrayDeque<Item> implements Deque<Item>, Iterable<Item>{
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
        int iterTime = size;
        int i = 1;
        int tempPos = capacity / 2 - size / 2;
        int itemPos;
        // linear the old data
        while(iterTime != 0){
            itemPos = (nextFirst + i + items.length) % items.length;
            temp[tempPos] = items[itemPos];
            iterTime--;
            i++;
            tempPos++;
        }
        items = temp;
        nextLast = tempPos;
        nextFirst = nextLast - size - 1;
    }

    @Override
    public void addFirst(Item item)
    {
        if(size == items.length){
            resize(size * 2);
        }
        items[nextFirst] = item;
        size += 1;
        nextFirst = (nextFirst - 1 + items.length) % items.length;  // circular
    }

    @Override
    public void addLast(Item item)
    {
        if(size == items.length){
            resize(size*2);
        }

        items[nextLast] = item;
        size += 1;
        nextLast = (nextLast + 1 + items.length) % items.length; // circular
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
        int index;
        int i;
        for(i=1;i<size;i++)
        {
            index = (nextFirst + i + items.length) % items.length;
            System.out.print(items[index] + " ");
        }
        index = (nextFirst + i + items.length) % items.length;
        System.out.println(items[index] + " ");
    }

    @Override
    public Item removeFirst()
    {
        if(items.length >= 16 && size*4 < items.length){
            resize(items.length/4);
        }

        nextFirst = (nextFirst + 1 + items.length) % items.length;
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

        nextLast = (nextLast - 1 + items.length) % items.length;
        Item res = items[nextLast];
        items[nextLast] = null;
        size -= 1;
        return res;
    }


    @Override
    public Item get(int index){
        int indexPos = (nextFirst + 1 + index + items.length) % items.length;
        return items[indexPos];
    }

    public Iterator<Item> iterator(){
        return new ArrayDequeIterator();
    }

    public class ArrayDequeIterator implements Iterator<Item>{
        private int wizPos;

        public ArrayDequeIterator(){
            wizPos = nextFirst + 1;
        }

        public boolean hasNext(){
            return wizPos < size;
        }
        public Item next(){
            Item returnItem = get(wizPos);
//            wizPos += 1;
            wizPos = (wizPos + 1 + items.length) % items.length;
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

}
