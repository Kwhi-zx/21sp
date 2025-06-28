package deque;

import java.util.Iterator;
import java.util.Objects;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    private int size;
    private T[] items;
    private int nextFirst;
    private int nextLast;

    //Creates an empty array deque.
    public ArrayDeque() {
        size = 0;
        items = (T[]) new Object[8]; //The starting size of your array should be 8.
        nextFirst = 4; // |0|1|2|3|4|5|6|7|  8/2 = 4
        nextLast = 5;
    }

    private void resize(int capacity) {
        T[] temp = (T[]) new Object[capacity];
        int iterTime = size;
        int i = 1;
        int tempPos = capacity / 2 - size / 2;
        int itemPos;
        // linear the old data
        while(iterTime != 0) {
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
    public void addFirst(T item)
    {
        if(size == items.length){
            resize(size * 2);
        }
        items[nextFirst] = item;
        size += 1;
        nextFirst = (nextFirst - 1 + items.length) % items.length;  // circular
    }

    @Override
    public void addLast(T item)
    {
        if(size == items.length){
            resize(size*2);
        }

        items[nextLast] = item;
        size += 1;
        nextLast = (nextLast + 1) % items.length; // circular
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
    public T removeFirst()
    {
        if(items.length >= 16 && size*4 < items.length){
            resize(items.length/4);
        }
        if(size == 0){
            return null;
        }

//        nextFirst = (nextFirst + 1 + items.length) % items.length;
        nextFirst = (nextFirst + 1) % items.length;
        T res = items[nextFirst];
        items[nextFirst] = null;    //remove the First item
        size -=1;
        return res;  //return the removed's next item


    }

    @Override
    public T removeLast(){
        //resize
        if(items.length >=16 && size*4 < items.length){
            resize(items.length/4);
        }

        if(size == 0){
            return null;
        }

        nextLast = (nextLast - 1 + items.length) % items.length;
        T res = items[nextLast];
        items[nextLast] = null;
        size -= 1;
        return res;
    }


    @Override
    public T get(int index){
//        int indexPos = (nextFirst + 1 + index + items.length) % items.length;
        int indexPos = (nextFirst + 1 + index) % items.length;
        return items[indexPos];
    }

    public Iterator<T> iterator(){
        return new ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator<T>{
        private int wizPos;

        public ArrayDequeIterator(){
            wizPos = 0;
        }

        public boolean hasNext(){
            return wizPos < size;
        }
        public T next(){
            T returnItem = get(wizPos);
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
                if(!Objects.equals(this.get(i),deque.get(i)))
//                if(this.get(i) != deque.get(i))
                {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

//    public static void main(String args[]){
//        ArrayDeque<Integer> ad1 = new ArrayDeque<>();
//        Iterator<Integer> iter1 = ad1.iterator();
//        ad1.addFirst(1);
//        ad1.addLast(1);
//        ad1.printDeque();
//        while (iter1.hasNext()){
//            System.out.println(iter1.next());
//        }
//    }
}
