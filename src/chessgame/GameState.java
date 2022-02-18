package chessgame;

/**
 * This is a "struct" used in the game tree to hold information that is
 * necessary for finding the best move
 */
public class GameState implements Comparable<GameState>
{
    // board represents the position of the pieces
    public ChessBoard board;

    // move is the move used to get here from the parent state
    public ChessMove move;

    // miniMaxRating is the rating given to this node, inherited from its
    // child
    public float miniMaxRating;
    public float overallRating;
    public float materialRating;
    public float mobilityRating;
//    public float hangingRating;
    public float kingMobilityRating;
    public float kingProximityRating;


    public boolean checkedForMiniMax;

    public GameState(ChessBoard b, ChessMove m)
    {
        board = b;
        move = m;
        miniMaxRating = 3.14f;
        checkedForMiniMax = false;
    }

    public GameState(ChessBoard b, ChessMove m, int mmR)
    {
        board = b;
        move = m;
        miniMaxRating = mmR;
        checkedForMiniMax = false;
    }

    /**
     * This method compares this to a given GameState gs to see which is
     * greater. Compares their overall rating
     *
     * @param rhs board to compare this to
     * @return -1 if this is less, 0 if equal, 1 if this is greater
     */
    @Override
    public int compareTo(GameState rhs)
    {
        if (rhs == null)
        {
            return 1;
        }
        if (overallRating > rhs.overallRating)
        {
            return 1;
        }
        return overallRating == rhs.overallRating ? 0 : -1;
    }
}
