package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
    public static void testThreeAddThreeRemove()
    {
        AListNoResizing<Integer> Correct_Alist = new AListNoResizing<>();
        BuggyAList<Integer> Bug_Alist = new BuggyAList<>();

        for(int i=0;i<3;i++)
        {
            Correct_Alist.addLast(3+i);
            Bug_Alist.addLast(3+i);
        }

        int Correct_value = 0;
        int Bug_value = 0;
        for(int i=0;i<3;i++)
        {
            Correct_value = Correct_Alist.getLast();
            Bug_value = Bug_Alist.getLast();
            assertEquals(Correct_value,Bug_value);
        }

    }

    public static void randomizedTest()
    {
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> Bug_L = new BuggyAList<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 3);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                Bug_L.addLast(randVal);
//                System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1)
            {
                if(L.size() ==0 && Bug_L.size() == 0)
                {
                    continue;
                }
                int lastValue = L.getLast();
                int BuggyLastValue = Bug_L.getLast();
//                System.out.println("getLast(" + lastValue + ")");
//                System.out.println("getLast(" + BuggyLastValue + ")");
                assertEquals(lastValue,BuggyLastValue);
            }else if (operationNumber == 2)
            {
                if(L.size() ==0 && Bug_L.size() == 0)
                {
                    continue;
                }
                L.removeLast();
//                System.out.println("Alist removeLast Success!");
                Bug_L.removeLast();
//                System.out.println("Buggy Alist removeLast Success!");
            }
        }
    }

    public static void main(String[] args)
    {
        randomizedTest();
    }
}
