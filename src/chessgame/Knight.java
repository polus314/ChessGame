package chessgame;

import java.util.HashSet;

/**
 * This class represents the Knight chess piece. The Knight moves in a peculiar
 * way, two spaces in a direction then one in a perpendicular direction, like an
 * "L" shape. The knight can also "jump over" other pieces and so is never
 * prevented from moving to a space by pieces in its way. The Knight is one of
 * the "minor" pieces along with the Bishop.
 *
 * @author John Polus
 */
public class Knight extends ChessPiece
{

    private static final HashSet<Vector> MOVE_SET = generateMoveList();

    /**
     * This is the default constructor for the knight piece
     */
    public Knight()
    {
        super();
    }

    /*
    This constructor takes a piece color
     */
    public Knight(Color c)
    {
        super();
        value = 3;
        color = c;
    }

    /*
    This takes a knight object as input and copies all the attributes
    to the new knight piece
     */
    public Knight(Knight cp)
    {
        super();
        color = cp.color;
        hasMoved = cp.hasMoved;
        value = cp.value;
    }

    private static HashSet<Vector> generateMoveList()
    {
        HashSet<Vector> moves = new HashSet<>();
        moves.add(new Vector(2, 1));
        moves.add(new Vector(1, 2));
        moves.add(new Vector(-2, 1));
        moves.add(new Vector(1, -2));
        moves.add(new Vector(2, -1));
        moves.add(new Vector(-1, 2));
        moves.add(new Vector(-2, -1));
        moves.add(new Vector(-1, -2));
        return moves;
    }

    @Override
    public final HashSet<Vector> getMoveSet()
    {
        return MOVE_SET;
    }

    /**
     * Returns a string describing this knight
     *
     * @return String - description of this
     */
    @Override
    public String toString()
    {
        String str = color == null ? "" : color.toString() + " ";
        str += "Knight";
        return str;
    }

    /**
     * Returns the one letter used in identifying this piece when recording
     * chess moves
     *
     * @return String - string of length 1, identifies this as a knight
     */
    @Override
    public String oneLetterIdentifier()
    {
        return "N";
    }

    @Override
    public ChessPiece copyOfThis()
    {
        return new Knight(this);
    }

    /**
     * This method checks to see if two knights are the same.
     */
    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof Knight)
        {
            Knight cp = (Knight) obj;
            return color == cp.color;
        }
        return false;
    }
}
