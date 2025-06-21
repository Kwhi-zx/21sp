package flik;

/** An Integer tester created by Flik Enterprises.
 * @author Josh Hug
 * */
public class Flik {
    /** @param a Value 1
     *  @param b Value 2
     *  @return Whether a and b are the same */
    public static boolean isSameNumber(Integer a, Integer b) {
        // the reason : Integer is a reference type
        // use "==" to compare is to compare their addr
        // so it needs to use equals
        return a.equals(b);
    }
}
