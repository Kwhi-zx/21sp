package deque;

import org.junit.Test;
import static org.junit.Assert.*;


/** Performs some basic linked list tests. */
public class LinkedListDequeTest {

    @Test
    /** Adds a few things to the list, checking isEmpty() and size() are correct,
     * finally printing the results.
     *
     * && is the "and" operation. */
    public void addIsEmptySizeTest() {

//        System.out.println("Make sure to uncomment the lines below (and delete this print statement).");

        LinkedListDeque<String> lld1 = new LinkedListDeque<String>();

		assertTrue("A newly initialized LLDeque should be empty", lld1.isEmpty());
		lld1.addFirst("front");

		// The && operator is the same as "and" in Python.
		// It's a binary operator that returns true if both arguments true, and false otherwise.
        assertEquals(1, lld1.size());
        assertFalse("lld1 should now contain 1 item", lld1.isEmpty());

		lld1.addLast("middle");
		assertEquals(2, lld1.size());

		lld1.addLast("back");
		assertEquals(3, lld1.size());

		System.out.println("Printing out deque: ");
		lld1.printDeque();

    }

    @Test
    /** Adds an item, then removes an item, and ensures that dll is empty afterwards. */
    public void addRemoveTest() {

//        System.out.println("Make sure to uncomment the lines below (and delete this print statement).");

        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();
		// should be empty
		assertTrue("lld1 should be empty upon initialization", lld1.isEmpty());

		lld1.addFirst(10);
		// should not be empty
		assertFalse("lld1 should contain 1 item", lld1.isEmpty());

		lld1.removeFirst();
		// should be empty
		assertTrue("lld1 should be empty after removal", lld1.isEmpty());

    }

    @Test
    /* Tests removing from an empty deque */
    public void removeEmptyTest() {

//        System.out.println("Make sure to uncomment the lines below (and delete this print statement).");

        LinkedListDeque<Integer> lld1 = new LinkedListDeque<>();
        lld1.addFirst(3);

        lld1.removeLast();
        lld1.removeFirst();
        lld1.removeLast();
        lld1.removeFirst();

        int size = lld1.size();
        String errorMsg = "  Bad size returned when removing from empty deque.\n";
        errorMsg += "  student size() returned " + size + "\n";
        errorMsg += "  actual size() returned 0\n";

        assertEquals(errorMsg, 0, size);

    }

    @Test
    /* Check if you can create LinkedListDeques with different parameterized types*/
    public void multipleParamTest() {


        LinkedListDeque<String>  lld1 = new LinkedListDeque<String>();
        LinkedListDeque<Double>  lld2 = new LinkedListDeque<Double>();
        LinkedListDeque<Boolean> lld3 = new LinkedListDeque<Boolean>();

        lld1.addFirst("string");
        lld2.addFirst(3.14159);
        lld3.addFirst(true);

        String s = lld1.removeFirst();
        double d = lld2.removeFirst();
        boolean b = lld3.removeFirst();

    }

    @Test
    /* check if null is return when removing from an empty LinkedListDeque. */
    public void emptyNullReturnTest() {

        //System.out.println("Make sure to uncomment the lines below (and delete this print statement).");

        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();

        boolean passed1 = false;
        boolean passed2 = false;
        assertEquals("Should return null when removeFirst is called on an empty Deque,", null, lld1.removeFirst());
        assertEquals("Should return null when removeLast is called on an empty Deque,", null, lld1.removeLast());


    }

    @Test
    /* Add large number of elements to deque; check if order is correct. */
    public void bigLLDequeTest() {

        //System.out.println("Make sure to uncomment the lines below (and delete this print statement).");

        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();
        for (int i = 0; i < 1000000; i++) {
            lld1.addLast(i);
        }

        for (double i = 0; i < 500000; i++) {
            assertEquals("Should have the same value", i, (double) lld1.removeFirst(), 0.0);
        }

        for (double i = 999999; i > 500000; i--) {
            assertEquals("Should have the same value", i, (double) lld1.removeLast(), 0.0);
        }


    }

    @Test
    public void getIterTest1(){
        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();
        lld1.addLast(0);
        lld1.addFirst(1);
        lld1.addFirst(2);
        lld1.addLast(3);
        lld1.addLast(4);
        assertEquals("Should have the same value",lld1.get(4),Integer.valueOf(4));
    }
    @Test
    public void getIterTest2(){
        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();
        lld1.addLast(0);
        lld1.addFirst(1);
        lld1.addFirst(2);
        lld1.addLast(3);
        lld1.addLast(4);
        assertEquals("Should have the same value",lld1.get(0),Integer.valueOf(2));
    }

    @Test
    public void getRecurTest1(){
        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();
        lld1.addLast(0);
        lld1.addFirst(1);
        lld1.addFirst(2);
        lld1.addLast(3);
        lld1.addLast(4);
        assertEquals("Should have the same value",lld1.getRecursive(4),Integer.valueOf(4));
    }

    @Test
    public void getRecurTest2(){
        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();
        lld1.addLast(0);
        lld1.addFirst(1);
        lld1.addFirst(2);
        lld1.addLast(3);
        lld1.addLast(4);
        assertEquals("Should have the same value",lld1.getRecursive(0),Integer.valueOf(2));
    }

    @Test
    public void getRecurTest3(){
        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();
        lld1.addLast(1);
        lld1.removeFirst();
        lld1.addFirst(3);
        assertEquals("Should have the same value",lld1.getRecursive(0),Integer.valueOf(3));
    }

    @Test
    public void equalsTest1(){
        //LinkedListDeque and ArrayDeques with the same elements should be equal !
        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();
        ArrayDeque<Integer> ad2 = new ArrayDeque<Integer>();
        lld1.addLast(0);
        lld1.addFirst(1);
        lld1.addFirst(2);
        lld1.addLast(3);
        lld1.addLast(4);

        ad2.addLast(0);
        ad2.addFirst(1);
        ad2.addFirst(2);
        ad2.addLast(3);
        ad2.addLast(4);

        assertEquals(true,lld1.equals(ad2));
//        assertEquals(false,lld1.equals(ad2));
    }

    @Test
    public void equalsTest2(){
        //LinkedListDeque and ArrayDeques with the same elements should be equal !
        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();
        LinkedListDeque<Integer> lld2 = new LinkedListDeque<Integer>();
        lld1.addLast(0);
        lld1.addFirst(1);

        lld2.addLast(0);
        lld2.addFirst(1);

        assertEquals(true,lld1.equals(lld2));
    }

    @Test
    public void equalsTest3(){

        LinkedListDeque<String> a = new LinkedListDeque<>();
        LinkedListDeque<String> b = new LinkedListDeque<>();
        a.addLast("hello");
        a.addLast("world");

        b.addLast(new String("hello")); // 不同对象但内容相同
        b.addLast("world");

        assertEquals(true,a.equals(b));

    }

    @Test
    public void sizeTest(){
        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();
        lld1.addLast(0);
        lld1.addFirst(1);
        lld1.addFirst(2);

        lld1.removeFirst();
        lld1.removeFirst();
        lld1.removeFirst();
        lld1.removeFirst();

        System.out.println(lld1.size());
    }


}
