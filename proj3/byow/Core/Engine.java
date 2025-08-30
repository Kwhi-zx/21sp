package byow.Core;

import byow.Core.HUD.Frame;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    // the algorithm requires map's width and height must be odd
    public static final int WIDTH = 61;
    public static final int HEIGHT = 41;

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        ter.initialize(WIDTH,HEIGHT);
        Frame framework = new Frame(WIDTH,HEIGHT);
        // draw the start frame
        framework.drawStartFrame();
        String input = framework.inputFromUser(1);
        switch (input) {
            case "N","n"->{
                framework.clearCanvas();
                // read a seed from user
                String seedString = framework.inputFromUser();
                // if seed too long ???????
                long seed = Long.parseLong(seedString); // if too long?
                // set font for the maze
                StdDraw.setFont(new Font("Monaca",Font.PLAIN,20));
                Variables variables = new Variables(seed);
                variables.initializeTheWorld();
                // show the world
                ter.renderFrame(variables.getWorld().getTiles());

                // user play the game loop
                String moveString = "";
                while (true) {
                    if(StdDraw.hasNextKeyTyped()) {
                        char moveChar = StdDraw.nextKeyTyped();
                        variables.getAvatar().kMovement(moveChar, variables.getWorld());
                        // refresh the world
                        ter.renderFrame(variables.getWorld().getTiles());
                        moveString += Character.toString(moveChar);
                        System.out.println(moveString);
                        if(moveString.length() >= 2) {
                            String quitString = moveString.substring(moveString.length()-2);
                            if (quitString.equals(":q") || quitString.equals(":Q")) {
                                // save and quit
                                System.exit(0);
                            }
                        }

                    }
                }


            }
            case "L","l"->{

            }
            case "Q","q"->{
                System.exit(0);
            }
            default -> {
                System.out.println("Error input");
            }
        }
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // TODO: Fill out this method so that it run the engine using the input
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.
        int inputLen = input.length();
        String seed = input.substring(1,inputLen-1);
        Variables variables = new Variables(Long.parseLong(seed));
        variables.initializeTheWorld();

        return variables.getWorld().getTiles();
    }
}
