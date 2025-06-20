package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeSLList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeGetLast();
    }

    public static void timeGetLast() {
        // TODO: YOUR CODE HERE
        int n = 8;
        int iterTimes = 1000;
        double timeInSec = 0;
        int M = 10000;
        AList<Integer> Ns = new AList<>();
        AList<Double>  times = new AList<>();
        AList<Integer> opCounts = new AList<>();
        SLList<Integer> testObj = new SLList<>();

        while(n!=0) {

            for (int i = 0; i < iterTimes; i++) {
                testObj.addLast(1);
            }

            Stopwatch sw = new Stopwatch();
            for (int i = 0; i < M; i++) {
                testObj.getLast();
            }
            timeInSec = sw.elapsedTime();
            Ns.addLast(iterTimes);
            times.addLast(timeInSec);
            opCounts.addLast(M);

            n--;
            iterTimes *=2;
        }
        TimeSLList.printTimingTable(Ns,times,opCounts);


    }

}
