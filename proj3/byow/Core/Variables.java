package byow.Core;

import java.util.Random;

public class Variables {
    private World world;
    private Random RANDOM;

    public Variables(long seed) {
        RANDOM = new Random(seed);
    }

    public Random getRANDOM() {
        return RANDOM;
    }
}
