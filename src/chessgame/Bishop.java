package chessgame;

import java.util.HashSet;

/**
 * This class represents the Bishop piece in chess. A bishop can move diagonally
 * and is one of the "minor" pieces, along with the knight.
 *
 * @author John Polus
 */
public class Bishop extends ChessPiece
{

    private static final HashSet<Vector> MOVE_SET = generateMoveList();

    /**
     * This is the default constructor
     */
    public Bishop()
    {
        super();
    }

    /*
    This constructor sets the starting position to the default position of
    a bishop and takes a color
     */
    public Bishop(Color c)
    {
        super();
        value = 3;
        color = c;
    }

    /*
    This constructor inputs a bishop and copies all the attributes
    for a new bishop piece
     */
    public Bishop(Bishop cp)
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
            moves.add(new Vector(i, i));
            moves.add(new Vector(-i, i));
            moves.add(new Vector(i, -i));
            moves.add(new Vector(-i, -i));
        }
        return moves;
    }

    @Override
    public final HashSet<Vector> getMoveSet()
    {
        return MOVE_SET;
    }

    /**
     * Returns a string describing this bishop
     *
     * @return String - description of this
     */
    @Override
    public String toString()
    {
        String str = color == null ? "" : color.toString() + " ";
        str += "Bishop";
        return str;
    }

    /**
     * Returns the one letter used in identifying this piece when recording
     * chess moves
     *
     * @return String - string of length 1, identifies this as a bishop
     */
    @Override
    public String oneLetterIdentifier()
    {
        return "B";
    }

    public HashSet<Vector> getMoves()
    {
        return (HashSet<Vector>) MOVE_SET.clone();
    }

    @Override
    public ChessPiece copyOfThis()
    {
        return new Bishop(this);
    }

    /**
     * This method checks to see if two bishops are the same.
     */
    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof Bishop)
        {
            Bishop cp = (Bishop) obj;
            return color == cp.color;
        }
        return false;
    }
}
