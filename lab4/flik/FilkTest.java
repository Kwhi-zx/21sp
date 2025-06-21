package flik;

import org.junit.Assert;
import org.junit.Test;


public class FilkTest {
    @Test
    public  void filkTest1(){
        int a = 128;
        int b = 128;
        boolean res = Flik.isSameNumber(a,b);
        Assert.assertTrue(res);
    }

    @Test
    public void filkTest2(){
        int a = 256;
        int b = 256;
        Assert.assertTrue(Flik.isSameNumber(a,b));
    }
}
