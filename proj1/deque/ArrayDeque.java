package deque;

public class ArrayDeque<Item> {
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

    public void addFirst(Item item)
    {
        if(nextFirst < 0 ){
            resize(size * 2);
        }
        items[nextFirst] = item;
        size += 1;
        nextFirst -= 1;
    }

    public void addLast(Item item)
    {
        if(nextLast >= items.length){
            resize(size*2);
        }

        items[nextLast] = item;
        size += 1;
        nextLast += 1;

    }

    public boolean isEmpty()
    {
        if(size == 0)
        {
            return true;
        }
        return false;
    }

    public int size()
    {
        return size;
    }

    public void printDeque()
    {
        for(int i=nextFirst+1;i<nextLast;i++)
        {
            if(i == nextLast-1){
                System.out.println(items[i] + " ");
                break;
            }
            System.out.print(items[i] + " ");
        }
    }

    public Item removeFirst()
    {
        if(items.length >=16 && size*4 < items.length){
            resize(items.length/4);
        }

        if(items[nextFirst+1]==null){
            return null;
        }
        nextFirst += 1;
        items[nextFirst] = null;    //remove the First item
        return items[nextFirst+1];  //return the removed's next item
    }

    public Item removeLast(){
        //resize
        if(items.length >=16 && size*4 < items.length){
            resize(items.length/4);
        }

        if(items[nextLast-1] == null){
            return null;
        }
        nextLast -= 1;
        items[nextLast] = null;
        return items[nextFirst-1];
    }


    public Item get(int index){
        if(index <= nextFirst || index >= nextLast)
        {
            return null;
        }
        return items[index];
    }

//    public static void main(String args[]){
//        ArrayDeque<Integer> aDeque = new ArrayDeque<Integer>();
//        for(int i=0;i<5;i++)
//            aDeque.addLast(i);
//
//        aDeque.printDeque();
//        System.out.println("size: "+ aDeque.get(12));
//
//    }
}
