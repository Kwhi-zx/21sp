package byow.lab13;


import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;


public class MemoryGame {
    /** The width of the window of this game. */
    private int width;
    /** The height of the window of this game. */
    private int height;
    /** The current round the user is on. */
    private int round;
    /** The Random object used to randomly generate Strings. */
    private Random rand;
    /** Whether or not the game is over. */
    private boolean gameOver;
    /** Whether or not it is the player's turn. Used in the last section of the
     * spec, 'Helpful UI'. */
    private boolean playerTurn;
    /** The characters we generate random Strings from. */
    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    /** Encouraging phrases. Used in the last section of the spec, 'Helpful UI'. */
    private static final String[] ENCOURAGEMENT = {"You can do this!", "I believe in you!",
                                                   "You got this!", "You're a star!", "Go Bears!",
                                                   "Too easy for you!", "Wow, so impressive!"};
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please enter a seed");
            return;
        }

        long seed = Long.parseLong(args[0]);
        MemoryGame game = new MemoryGame(40, 40, seed);
        game.startGame();
    }

    public MemoryGame(int width, int height, long seed) {
        /* Sets up StdDraw so that it has a width by height grid of 16 by 16 squares as its canvas
         * Also sets up the scale so the top left is (0,0) and the bottom right is (width, height)
         */
        this.width = width;
        this.height = height;
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();

        // Initialize random number generator
        rand = new Random(seed);
    }

    public String generateRandomString(int n) {
        // Generate random string of letters of length n
        // loop n times
        int charSize = CHARACTERS.length;
        String finalString = new String();
        for(int i=0; i<n; i++) {
            int randomIndex = rand.nextInt(charSize);
            char randomChar = CHARACTERS[randomIndex];
            finalString += randomChar;
        }
        return finalString;
    }

    public void drawFrame(String s) {
        // Take the string and display it in the center of the screen
        // If game is not over, display relevant game information at the top of the screen
        StdDraw.clear(Color.black);
        StdDraw.setPenColor(Color.white);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 30));
        StdDraw.text(width/2,height/2,s);

        if(!gameOver) {
            StdDraw.setFont(new Font("Monaco", Font.PLAIN, 15));
            if(playerTurn) {
                StdDraw.text(width/2,height-1,"Type!");
            }else {
                StdDraw.text(width/2,height-1,"Watch!");
            }
            StdDraw.text(3, height - 1, "Round: " + round);
            StdDraw.text(width - 5, height - 1,ENCOURAGEMENT[rand.nextInt(ENCOURAGEMENT.length)]);
            StdDraw.text(0,height-3,"-".repeat(width * 16));
        }

        StdDraw.show();
    }

    public void flashSequence(String letters) {
        // Display each character in letters, making sure to blank the screen between letters
        int len = letters.length();
        String s;
        for(int i=0; i<len; i++) {
            s = Character.toString(letters.charAt(i));
            drawFrame(s);
            // keep 1 seconds
            sleep(1000);

            clearCanvas();
            sleep(500);
        }
        playerTurn = true;
    }

    public String solicitNCharsInput(int n) {
        // Read n letters of player input
        StringBuilder res = new StringBuilder();

        while(res.length() < n) {
            if(StdDraw.hasNextKeyTyped()) {
                char ch = StdDraw.nextKeyTyped();
                res.append(ch);
                drawFrame(res.toString());
            }
        }
        playerTurn = false;
        return res.toString();
    }

    public void startGame() {
        // Set any relevant variables before the game starts
        this.round = 1; // game round
        String letters = generateRandomString(round);
        // Establish Engine loop
        while(!gameOver) {
            drawFrame("Round: " + round);
            sleep(500);

            flashSequence(letters);
            clearCanvas();

            String readLetters = solicitNCharsInput(round);

            if(letters.equals(readLetters)) {
                round++;
                letters = generateRandomString(round);

            }else {
                drawFrame("Game Over! You made it to round: "+ round);
                gameOver = true;
            }

        }
    }

    public void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void clearCanvas() {
        StdDraw.clear(Color.black);
        drawFrame("");
        StdDraw.show();
    }



}
