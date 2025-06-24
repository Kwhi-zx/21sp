package deque;


import java.util.Comparator;
public class MaxArrayDeque<Item> extends ArrayDeque<Item> {
    private Comparator<Item> comparator_;

    public MaxArrayDeque(Comparator<Item> c){
        comparator_ = c;
    }
    public Item max() {
        if (isEmpty()) {
            return null;
        }

        int arraySize = size();
        Item maxItem = get(0);
        for (int i = 1; i < arraySize; i++) {
            Item currentItem = get(i);
            // if currentItem > maxItem : 1
            //    currentItem = maxItem : 0
            //    currentItem < maxItem : -1
            if (comparator_.compare(currentItem, maxItem) > 0) {
                maxItem = currentItem;
            }

        }
        return maxItem;
    }


    public Item max(Comparator<Item> c){
        if(isEmpty()){
            return null;
        }

        int arraySize = size();
        Item maxItem = get(0);
        for(int i=1;i<arraySize;i++){
            Item currentItem = get(i);
            if(c.compare(currentItem,maxItem)>0){
                maxItem = currentItem;
            }
        }
        return maxItem;
    }

}
