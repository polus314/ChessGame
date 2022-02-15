package chessgame;

import java.util.HashSet;

/**
 * This class represents the Rook chess piece. The Rook moves parallel to the
 * axes of the board, either vertical or horizontal, as far as it wants. The
 * Rook is involved in castling, where the King moves two spaces toward it and
 * the Rook is placed on the other side. The Rook is one of the "major" pieces
 * along with the Queen.
 *
 * @author John Polus
 */
public class Rook extends ChessPiece
{

    private static final HashSet<Vector> MOVE_SET = generateMoveList();

    /*
    This is the default constructor for a Rook
     */
    public Rook()
    {
        super();
    }

    /*
    This constructor takes a piece color and sets the starting 
    position as the default Rook position
     */
    public Rook(Color c)
    {
        super();
        value = 5;
        color = c;
    }

    /*
    This constructor takes a Rook as an input and copies all the
    attributes for the new Rook piece
     */
    public Rook(Rook cp)
    {
        super();
        color = cp.color;
        hasMoved = cp.hasMoved;
        value = cp.value;
    }

    private static HashSet<Vector> generateMoveList()
    {
        HashSet<Vector> moves = new HashSet<>();
        for (int i = 1; i < 8; i++)
        {
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
     * Returns a string describing this rook
     *
     * @return String - description of this
     */
    @Override
    public String toString()
    {
        String str = color == null ? "" : color.toString() + " ";
        str += "Rook";
        return str;
    }

    /**
     * Returns the one letter used in identifying this piece when recording
     * chess moves
     *
     * @return String - string of length 1, identifies this as a rook
     */
    @Override
    public String oneLetterIdentifier()
    {
        return "R";
    }

    @Override
    public ChessPiece copyOfThis()
    {
        return new Rook(this);
    }

    /**
     * This method checks to see if two rooks are the same.
     */
    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof Rook)
        {
            Rook cp = (Rook) obj;
            return color == cp.color;
        }
        return false;
    }
}
