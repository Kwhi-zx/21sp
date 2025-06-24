package tester;

import static org.junit.Assert.*;

import edu.princeton.cs.introcs.StdRandom;
import jh61b.junit.In;
import org.junit.Assert;
import org.junit.Test;
import student.StudentArrayDeque;

public class TestArrayDequeEC {

    @Test
    public void testAddFirst_rf() {
        StudentArrayDeque<Integer> stuAddFirst = new StudentArrayDeque<Integer>();
        ArrayDequeSolution<Integer> correctAddFirst = new ArrayDequeSolution<Integer>();
        int N = 100;
        for (int i = 0; i < N; i++) {
            double numberBetweenZeroAndOne = StdRandom.uniform();

            if (numberBetweenZeroAndOne < 0.5) {
                stuAddFirst.addFirst(i);
                correctAddFirst.addFirst(i);
            } else {
                stuAddFirst.addFirst(i);
                correctAddFirst.addFirst(i);
            }

//            Assert.assertEquals(stuAddFirst.removeFirst(), correctAddFirst.removeFirst());
        }
        for(int i=0;i<N;i++){
            Integer expected = correctAddFirst.removeFirst();
            Integer actual   = stuAddFirst.removeFirst();
            assertEquals("Oh noooo!\nThis is bad:\n   Random number " + actual
                            + " not equal to " + expected + "!",
                    expected, actual);
        }
    }

    @Test
    public void testAddFirst_rl(){
        StudentArrayDeque<Integer> stuAddFirst = new StudentArrayDeque<Integer>();
        ArrayDequeSolution<Integer> correctAddFirst = new ArrayDequeSolution<Integer>();
        int N = 100;
        for (int i = 0; i < N; i++) {
            double numberBetweenZeroAndOne = StdRandom.uniform();

            if (numberBetweenZeroAndOne < 0.5) {
                stuAddFirst.addFirst(i);
                correctAddFirst.addFirst(i);
            } else {
                stuAddFirst.addFirst(i);
                correctAddFirst.addFirst(i);
            }

            Integer actual = stuAddFirst.removeLast();
            Integer expected = correctAddFirst.removeLast();
            assertEquals("Oh noooo!\nThis is bad:\n   Random number " + actual
                            + " not equal to " + expected + "!",
                    expected, actual);
        }

    }

    @Test
    public void testAddLast_rf(){
        StudentArrayDeque<Integer> stuAddFirst = new StudentArrayDeque<Integer>();
        ArrayDequeSolution<Integer> correctAddFirst = new ArrayDequeSolution<Integer>();
        int N = 100;
        for (int i = 0; i < N; i++) {
            double numberBetweenZeroAndOne = StdRandom.uniform();

            if (numberBetweenZeroAndOne < 0.5) {
                stuAddFirst.addLast(i);
                correctAddFirst.addLast(i);
            } else {
                stuAddFirst.addLast(i);
                correctAddFirst.addLast(i);
            }
            Integer actual = stuAddFirst.removeFirst();
            Integer expected = correctAddFirst.removeFirst();
            assertEquals("Oh noooo!\nThis is bad:\n   Random number " + actual
                            + " not equal to " + expected + "!",
                    expected, actual);

        }

    }

    @Test
    public void testAddLast_rl(){
        StudentArrayDeque<Integer> stuAddFirst = new StudentArrayDeque<Integer>();
        ArrayDequeSolution<Integer> correctAddFirst = new ArrayDequeSolution<Integer>();
        int N = 100;
        for (int i = 0; i < N; i++) {
            double numberBetweenZeroAndOne = StdRandom.uniform();

            if (numberBetweenZeroAndOne < 0.5) {
                stuAddFirst.addLast(i);
                correctAddFirst.addLast(i);
            } else {
                stuAddFirst.addLast(i);
                correctAddFirst.addLast(i);
            }

            Integer actual = stuAddFirst.removeLast();
            Integer expected = correctAddFirst.removeLast();
            assertEquals("Oh noooo!\nThis is bad:\n   Random number " + actual
                            + " not equal to " + expected + "!",
                    expected, actual);
        }

    }


}
