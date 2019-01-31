package chessgame;

import java.util.HashSet;

/**
 * This class represents the Queen chess piece. The Queen is the most powerful
 * piece, combining the movements of the Rook and Bishop, and as a result is the
 * (second) most valuable piece (behind the King). The Queen is one of the
 * "major" pieces along with the Rook.
 *
 * @author John Polus
 */
public class Queen extends ChessPiece
{

    private static final HashSet<Vector> MOVE_SET = generateMoveList();

    /*
    This is the default constructor for a Queen
     */
    public Queen()
    {
        super();
    }

    /*
    This constructor takes an input of the piece color and the starting
    position for the Queen
     */
    public Queen(Color c, int xC, int yC)
    {
        super();
        value = 9;
        color = c;
        xCoord = xC;
        yCoord = yC;
    }

    /*
    This constructor takes a piece color and sets the starting 
    position as the default Queen position
     */
    public Queen(Color c)
    {
        super();
        value = 9;
        color = c;
    }

    /*
    This constructor takes a Queen as an input and copies all the
    attributes for the new Queen piece
     */
    public Queen(Queen cp)
    {
        super();
        color = cp.color;
        hasMoved = cp.hasMoved;
        value = cp.value;
        xCoord = cp.xCoord;
        yCoord = cp.yCoord;
    }

    private static HashSet<Vector> generateMoveList()
    {
        HashSet<Vector> moves = new HashSet<>();
        for (int i = 1; i < 8; i++)
        {
            // add Bishop moves
            moves.add(new Vector(i, i));
            moves.add(new Vector(-i, i));
            moves.add(new Vector(i, -i));
            moves.add(new Vector(-i, -i));

            // add Rook moves
            moves.add(new Vector(0, i));
            moves.add(new Vector(i, 0));
            moves.add(new Vector(0, -i));
            moves.add(new Vector(-i, 0));
        }
        return moves;
    }

    @Override
    public final HashSet<Vector> getMoveSet()
    {
        return MOVE_SET;
    }

    /**
     * Returns a string describing this queen
     *
     * @return String - description of this
     */
    @Override
    public String toString()
    {
        String str = color == null ? "" : color.toString() + " ";
        str += "Queen";
        return str;
    }

    /**
     * Returns the one letter used in identifying this piece when recording
     * chess moves
     *
     * @return String - string of length 1, identifies this as a queen
     */
    @Override
    public String oneLetterIdentifier()
    {
        return "Q";
    }

    @Override
    public ChessPiece copyOfThis()
    {
        return new Queen(this);
    }

    /**
     * This method checks to see if two queens are the same.
     */
    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof Queen)
        {
            Queen cp = (Queen) obj;
            return xCoord == cp.xCoord
                    && yCoord == cp.yCoord
                    && color == cp.color;
        }
        return false;
    }
}
