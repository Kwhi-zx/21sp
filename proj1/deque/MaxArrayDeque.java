package deque;


import java.util.Comparator;
public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> mineComparator;

    public MaxArrayDeque(Comparator<T> c) {
        mineComparator = c;
    }
    public T max() {
        if (isEmpty()) {
            return null;
        }

        int arraySize = size();
        T maxItem = get(0);
        for (int i = 1; i < arraySize; i++) {
            T currentItem = get(i);
            // if currentItem > maxItem : 1
            //    currentItem = maxItem : 0
            //    currentItem < maxItem : -1
            if (mineComparator.compare(currentItem, maxItem) > 0) {
                maxItem = currentItem;
            }

        }
        return maxItem;
    }


    public T max(Comparator<T> c) {
        if (isEmpty()) {
            return null;
        }

        int arraySize = size();
        T maxItem = get(0);
        for(int i = 1; i < arraySize; i++) {
            T currentItem = get(i);
            if (c.compare(currentItem, maxItem) > 0) {
                maxItem = currentItem;
            }
        }
        return maxItem;
    }

}
