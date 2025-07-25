package game2048;

import java.util.Formatter;
import java.util.Observable;


/** The state of a game of 2048.
 *  @author TODO: YOUR NAME HERE
 */
public class Model extends Observable {
    /** Current contents of the board. */
    private Board board;
    /** Current score. */
    private int score;
    /** Maximum score so far.  Updated when game ends. */
    private int maxScore;
    /** True iff game is ended. */
    private boolean gameOver;

    /* Coordinate System: column C, row R of the board (where row 0,
     * column 0 is the lower-left corner of the board) will correspond
     * to board.tile(c, r).  Be careful! It works like (x, y) coordinates.
     */

    /** Largest piece value. */
    public static final int MAX_PIECE = 2048;

    /** A new 2048 game on a board of size SIZE with no pieces
     *  and score 0. */
    public Model(int size) {
        board = new Board(size);
        score = maxScore = 0;
        gameOver = false;
    }

    /** A new 2048 game where RAWVALUES contain the values of the tiles
     * (0 if null). VALUES is indexed by (row, col) with (0, 0) corresponding
     * to the bottom-left corner. Used for testing purposes. */
    public Model(int[][] rawValues, int score, int maxScore, boolean gameOver) {
        int size = rawValues.length;
        board = new Board(rawValues, score);
        this.score = score;
        this.maxScore = maxScore;
        this.gameOver = gameOver;
    }

    /** Return the current Tile at (COL, ROW), where 0 <= ROW < size(),
     *  0 <= COL < size(). Returns null if there is no tile there.
     *  Used for testing. Should be deprecated and removed.
     *  */
    public Tile tile(int col, int row) {
        return board.tile(col, row);
    }

    /** Return the number of squares on one side of the board.
     *  Used for testing. Should be deprecated and removed. */
    public int size() {
        return board.size();
    }

    /** Return true iff the game is over (there are no moves, or
     *  there is a tile with value 2048 on the board). */
    public boolean gameOver() {
        checkGameOver();
        if (gameOver) {
            maxScore = Math.max(score, maxScore);
        }
        return gameOver;
    }

    /** Return the current score. */
    public int score() {
        return score;
    }

    /** Return the current maximum game score (updated at end of game). */
    public int maxScore() {
        return maxScore;
    }

    /** Clear the board to empty and reset the score. */
    public void clear() {
        score = 0;
        gameOver = false;
        board.clear();
        setChanged();
    }

    /** Add TILE to the board. There must be no Tile currently at the
     *  same position. */
    public void addTile(Tile tile) {
        board.addTile(tile);
        checkGameOver();
        setChanged();
    }

    /** Tilt the board toward SIDE. Return true iff this changes the board.
     *
     * 1. If two Tile objects are adjacent in the direction of motion and have
     *    the same value, they are merged into one Tile of twice the original
     *    value and that new value is added to the score instance variable
     * 2. A tile that is the result of a merge will not merge again on that
     *    tilt. So each move, every tile will only ever be part of at most one
     *    merge (perhaps zero).
     * 3. When three adjacent tiles in the direction of motion have the same
     *    value, then the leading two tiles in the direction of motion merge,
     *    and the trailing tile does not.
     * */
    public boolean tilt(Side side) {
        boolean changed;
        changed = false;

        // TODO: Modify this.board (and perhaps this.score) to account
        // for the tilt to the Side SIDE. If the board changed, set the
        // changed local variable to true.

        int b_size = this.board.size();
        int score = 0;
        MoveResult res = new MoveResult(0,false);
        // 遍历判断side方向是否能够move
        if(side == Side.NORTH) {
            for (int col = 0; col < b_size; col++) {
                res=upMoveOneCol(this.board, col);
                score += res.score;
                changed |= res.ischanged;
            }
            this.score += score;

//            changed = true;
        } else if (side == Side.SOUTH)
        {
            board.setViewingPerspective(Side.SOUTH);
            for (int col = 0; col < b_size; col++) {
                res=upMoveOneCol(this.board, col);
                score += res.score;
                changed |= res.ischanged;
            }
            this.score += score;

//            changed = true;
            board.setViewingPerspective(Side.NORTH);

        } else if (side == Side.EAST) {
            board.setViewingPerspective(Side.EAST);
            for (int col = 0; col < b_size; col++) {
                res=upMoveOneCol(this.board, col);
                score += res.score;
                changed |= res.ischanged;
            }
            this.score += score;

//            changed = true;
            board.setViewingPerspective(Side.NORTH);
        }
        else
        {
            board.setViewingPerspective(Side.WEST);
            for (int col = 0; col < b_size; col++) {
                res=upMoveOneCol(this.board, col);
                score += res.score;
                changed |= res.ischanged;
            }
            this.score += score;

//            changed = true;
            board.setViewingPerspective(Side.NORTH);
        }


        checkGameOver();
        if (changed) {
            setChanged();
        }
        return changed;
    }

    // titl helper func for one col
    public static MoveResult upMoveOneCol(Board b,int col){
        int b_size = b.size();

        boolean[] temp = new boolean[]{false,false,false,false};
        MoveResult result =new MoveResult(0,false);
        // top row
        if(b.tile(col,3) ==null)
        {
            // mark the null
            temp[3] = true;
        }
        else{
            for(int i=2;i>=0;i--)
            {
                if(b.tile(col,i)!=null)
                {
                    if(b.tile(col,i).value() != b.tile(col,3).value())
                    {
                        break;
                    }
                    else
                    {
                        //merge
                        b.move(col,3,b.tile(col,i));
                        temp[3] = false;
                        temp[i] = true;
                        result.ischanged = true;
                        result.score += b.tile(col,3).value();
                        break;
                    }
                }
            }
        }
        // 2nd top row
        if(b.tile(col,2) == null)
        {
            temp[2] = true;
        }
        else
        {
            if(temp[2] == true){
                // goto null
                for(int i=1;i>=0;i--){
                    if(b.tile(col,i) != null){
                        b.move(col,2,b.tile(col,i));
                        temp[2] = false;
                        temp[i] = true;
                        result.ischanged = true;

                        break;
                    }
                }
            }
            else{
                for(int i=1;i>=0;i--)
                {
                    if(b.tile(col,i) !=null)
                    {
                        if(b.tile(col,i).value() == b.tile(col,2).value())
                        {
                            //merge
                            b.move(col,2,b.tile(col,i));
                            temp[2] = false;
                            temp[i] = true;
                            result.ischanged = true;
                            result.score += b.tile(col,2).value();
                            break;
                        }
                        else {
                            break;
                        }
                    }
                }
            }
        }
        //3rd top row
        if(b.tile(col,1) ==null)
        {
            temp[1] = true;
        }
        else
        {
            if(b.tile(col,0) != null)
            {
                if(b.tile(col,1).value() == b.tile(col,0).value())
                {
                    //merge
                    b.move(col,1,b.tile(col,0));
                    temp[1] = false;
                    temp[0] = true;
                    result.ischanged = true;
                    result.score += b.tile(col,1).value();
                }
            }
        }
        result.ischanged |=helpMove2null(temp,b,col);
        return result;
    }

    // helper func to get the right position
    public static boolean helpMove2null(boolean[] temp,Board b,int col)
    {
        boolean is_changed = false;
        for(int i=b.size()-1;i>=0;i--)
        {
            if(b.tile(col,i)!=null)
            {
                for(int j=b.size()-1;j>0;j--)
                {
                    if(temp[j] == true && j>i)
                    {
                        b.move(col,j,b.tile(col,i));
                        temp[i] = true;
                        temp[j] = false;
                        is_changed = true;
                        break;
                    }
                }
            }
        }
        return is_changed;
    }


    /** Checks if the game is over and sets the gameOver variable
     *  appropriately.
     */
    private void checkGameOver() {
        gameOver = checkGameOver(board);
    }

    /** Determine whether game is over. */
    private static boolean checkGameOver(Board b) {
        return maxTileExists(b) || !atLeastOneMoveExists(b);
    }

    /** Returns true if at least one space on the Board is empty.
     *  Empty spaces are stored as null.
     * */
    public static boolean emptySpaceExists(Board b) {
        // TODO: Fill in this function.
        int b_size = b.size();
        for(int i=0;i<b_size;i++)
        {
            for(int j=0;j<b_size;j++)
            {
                if(b.tile(i,j) == null)
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns true if any tile is equal to the maximum valid value.
     * Maximum valid value is given by MAX_PIECE. Note that
     * given a Tile object t, we get its value with t.value().
     */
    public static boolean maxTileExists(Board b) {
        // TODO: Fill in this function.
        int b_size = b.size();
        for(int i=0;i<b_size;i++)
        {
            for(int j=0;j<b_size;j++)
            {
                Tile t = b.tile(i,j);
                if(t == null){
                    continue;
                }
                if(t.value() == MAX_PIECE)
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns true if there are any valid moves on the board.
     * There are two ways that there can be valid moves:
     * 1. There is at least one empty space on the board.
     * 2. There are two adjacent tiles with the same value.
     */
    public static boolean atLeastOneMoveExists(Board b) {
        // TODO: Fill in this function.
        int b_size = b.size();
        //at least one empty spcae
        for(int i=0;i<b_size;i++)
        {
            for(int j=0;j<b_size;j++)
            {
                Tile t = b.tile(i,j);
                // at least one empty space on the board
                if(t == null) {
                    return true;
                }
            }
        }
        return checkSameValues(b);
    }

    //helper function
    public static boolean checkSameValues(Board b)
    {
        /**
         *  up:[i][j+1]
         *  down:[i][j-1]
         *  left:[i-1][j]
         *  right:[i+1][j]
         * */

        int b_size = b.size();
        Tile up_t = null;
        Tile down_t = null;
        Tile left_t = null;
        Tile right_t = null;
        for(int i=0;i<b_size;i++)
        {
            for(int j=0;j<b_size;j++)
            {
                Tile curr_t = b.tile(i,j);
                if(j+1<b_size){
                    up_t = b.tile(i,j+1);
                    if(curr_t.value() == up_t.value()){
                        return true;
                    }
                }
                if(j-1>=0){
                    down_t = b.tile(i,j-1);
                    if(curr_t.value() == down_t.value()){
                        return true;
                    }
                }
                if(i-1>=0){
                    left_t = b.tile(i-1,j);
                    if(curr_t.value() == left_t.value()){
                        return true;
                    }
                }
                if(i+1<b_size){
                    right_t = b.tile(i+1,j);
                    if(curr_t.value() == right_t.value()){
                        return true;
                    }
                }

            }
        }
        return false;
    }


    @Override
     /** Returns the model as a string, used for debugging. */
    public String toString() {
        Formatter out = new Formatter();
        out.format("%n[%n");
        for (int row = size() - 1; row >= 0; row -= 1) {
            for (int col = 0; col < size(); col += 1) {
                if (tile(col, row) == null) {
                    out.format("|    ");
                } else {
                    out.format("|%4d", tile(col, row).value());
                }
            }
            out.format("|%n");
        }
        String over = gameOver() ? "over" : "not over";
        out.format("] %d (max: %d) (game is %s) %n", score(), maxScore(), over);
        return out.toString();
    }

    @Override
    /** Returns whether two models are equal. */
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (getClass() != o.getClass()) {
            return false;
        } else {
            return toString().equals(o.toString());
        }
    }

    @Override
    /** Returns hash code of Model’s string. */
    public int hashCode() {
        return toString().hashCode();
    }
}

class MoveResult{
    int score;
    boolean ischanged;

    MoveResult(int score,boolean ischanged)
    {
        this.score = score;
        this.ischanged = ischanged;
    }
}