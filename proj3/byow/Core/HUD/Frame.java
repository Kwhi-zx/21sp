package byow.Core.HUD;

import edu.princeton.cs.introcs.StdDraw;


import java.awt.*;

public class Frame {
    private int width;
    private int height;


    public Frame(int width, int height) {
        // Tenderer is case of StdDraw
        // so no need to StdDraw.setCanvasSize/setXscale/setYscale here
        this.width = width;
        this.height = height;
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
    }
    public void drawStartFrame() {
        StdDraw.clear(Color.black);
        StdDraw.setPenColor(Color.white);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 45));
        StdDraw.text(width/2,height*0.75,"CS61B: THE GAME");

        StdDraw.setFont(new Font("Monaca",Font.PLAIN,25));
        StdDraw.text(width/2,height/4,"New Game (N)");
        StdDraw.text(width/2,height/4-2,"Load Game (L)");
        StdDraw.text(width/2,height/4-4,"Quit (Q)");

        StdDraw.show();
    }

    public void drawFrame(String s) {
        StdDraw.clear(Color.black);
        StdDraw.setPenColor(Color.white);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 45));
        StdDraw.text(width/2,height/2,s);
        StdDraw.show();
    }

    public String inputFromUser(int n) {
        // receive input from user
        StringBuilder res = new StringBuilder();
        while (res.length() < n) {
            if(StdDraw.hasNextKeyTyped()) {
                char ch = StdDraw.nextKeyTyped();
                res.append(ch);
            }
        }

        return res.toString();
    }

    public String inputFromUser() {
        // this is a version without parameters
        StringBuilder res = new StringBuilder();
        while (true) {
            // can not put it in while(); it will read all at a time
            if (StdDraw.hasNextKeyTyped()) {
                char ch = StdDraw.nextKeyTyped();
                if (ch == '\n') {
                    // if call an "Enter"
                    break;
                }
                res.append(ch);
                drawFrame(res.toString()); // print out what user type
            }
            StdDraw.pause(50);
        }

        return res.toString();
    }

    public void clearCanvas() {
        StdDraw.clear(Color.black);
        drawFrame("");
        StdDraw.show();
    }


}
