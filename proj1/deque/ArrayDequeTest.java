package deque;

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
        for (int i = 0; i < 200; i++) {
            ad1.addFirst(i);
        }
        for(int i = 0; i < 200; i++){
            System.out.println(":  "+ ad1.get(i));
        }
    }
}
