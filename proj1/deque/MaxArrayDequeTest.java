package deque;

import org.junit.Assert;
import org.junit.Test;

import java.util.Comparator;

import static org.junit.Assert.*;

public class MaxArrayDequeTest {
    @Test
    public void testInterger(){
//        Comparator<Integer> comparator1 = new Comparator<Integer>() {
//            @Override
//            public int compare(Integer o1, Integer o2) {
//                return o1-o2;
//            }
//        };
        Comparator<Integer> comparator1 =(a, b)->a-b;
        MaxArrayDeque<Integer> mar = new MaxArrayDeque<>(comparator1);
        mar.addFirst(1);
        mar.addFirst(2);
        mar.addFirst(3);
//        System.out.println("the max is : "+ mar.max());
        Assert.assertEquals(Integer.valueOf(3),mar.max());

    }

    @Test
    public void testString(){

        Comparator<String> comparator2 = (a,b) ->a.compareTo(b);

        MaxArrayDeque<String> marStr = new MaxArrayDeque<>(comparator2);
        marStr.addFirst("dog");
        marStr.addFirst("cat");
        marStr.addFirst("pig");
//        System.out.println("the max is " + marStr.max());
        Assert.assertEquals("pig",marStr.max());
    }
}
