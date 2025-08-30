package byow.Core;


public class Utils {


    public static class StringClass {
        String seed;
        String movement;
        String saveString;
        public StringClass(String seed, String movement, String save) {
            this.seed = seed;
            this.movement = movement;
            this.saveString = save;
        }
    }
    public static StringClass dealString(String input) {
        int len = input.length();
        int index = 0;
        int finalIndex = 0;
        for(int i=1; i<len; i++) {
            if(input.charAt(i) == 'S') {
                index = i;
            }else if(input.charAt(i) == ':') {
                finalIndex = i;
            }
        }

        String seed = input.substring(1,index);
        String movement = "";
        String save = "";
        if(index+1 < len) {
            if(finalIndex == 0) {
                movement = input.substring(index + 1);
            }else {
                movement = input.substring(index +1, finalIndex);
                save = input.substring(finalIndex);
            }
        }

        return new StringClass(seed,movement,save);
    }


}
