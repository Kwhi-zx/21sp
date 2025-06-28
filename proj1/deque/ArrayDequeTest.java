package deque;

import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;

public class ArrayDequeTest {

    @Test
    public void bigADequeTest1() {

        //System.out.println("Make sure to uncomment the lines below (and delete this print statement).");

        ArrayDeque<Integer> ad1 = new ArrayDeque<Integer>();
        for (int i = 0; i < 1000000; i++) {
            ad1.addLast(i);
        }

        // test printDeque
//        ad1.printDeque();

        for (double i = 0; i < 500000; i++) {
            assertEquals("Should have the same value1", i,  ad1.removeFirst(), 0.0);
        }

        for (double i = 999999; i > 500000; i--) {
            assertEquals("Should have the same value2", i, (double) ad1.removeLast(), 0.0);
        }


    }

    @Test
    public void bigADequeTest2(){

        ArrayDeque<Integer> ad1 = new ArrayDeque<Integer>();
        for (int i = 0; i < 500; i++) {
            ad1.addLast(i);
        }

//        ad1.printDeque();

    }

    @Test
    public void getTest(){
        ArrayDeque<Integer> ad1 = new ArrayDeque<Integer>();
        for (int i = 0; i < 1000000; i++) {
            ad1.addLast(i);
        }

        for(int i = 0; i< 1000000; i++){
            assertEquals("Should have the same value1", i,  ad1.get(i), 0.0);
        }
    }

    @Test
    public void removeTest(){
        ArrayDeque<Integer> ad1 = new ArrayDeque<Integer>();
        for (int i = 0; i < 1000000; i++) {
            ad1.addLast(i);
        }
        for(int i = 0; i < 500000; i++){
            assertEquals("Should have the same value", i, (double) ad1.removeFirst(), 0.0);
        }

        for (double i = 999999; i > 500000; i--) {
            assertEquals("Should have the same value", i, (double) ad1.removeLast(), 0.0);
        }

    }

    @Test
    public void sizeTest(){
        ArrayDeque<Integer> ad1 = new ArrayDeque<Integer>();
        ad1.removeFirst();
//        System.out.println(ad1.size());
        Assert.assertEquals(0,ad1.size());
    }


    @Test
    public void equalTest(){
        ArrayDeque<Integer> ad1 = new ArrayDeque<Integer>();
        ArrayDeque<Integer> ad2 = new ArrayDeque<Integer>();

        ad1.addFirst(1);
        ad1.addLast(2);

        ad2.addFirst(1);
        ad2.addLast(1);

        Assert.assertEquals(false,ad1.equals(ad2));
    }

    @Test
    public void equalTest2(){
        ArrayDeque<String> ad1 = new ArrayDeque();
        ArrayDeque<String> ad2 = new ArrayDeque();

        ad1.addFirst("good");
        ad1.addLast("night");

        ad2.addFirst("good");
        ad2.addLast("night1");

        Assert.assertEquals(false,ad1.equals(ad2));

    }
}
