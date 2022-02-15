package chessgame;

import java.util.HashSet;

/**
 * This class represents the King piece in chess. The King is the most
 * important, though not most powerful, piece in the game. An attack on the King
 * ("check"), must be dealt with immediately, as the game ends when the King
 * cannot escape capture ("checkmate"). The King moves one square in any
 * direction, except when castling, when he moves two spaces toward one of his
 * rooks.
 *
 * @author John Polus
 */
public class King extends ChessPiece
{

    private static final HashSet<Vector> MOVE_SET = generateMoveList();

    /*
    This is the default constructor for the King piece
     */
    public King()
    {
        super();
    }

    /*
    This constructor takes a piece color
     */
    public King(Color c)
    {
        super();
        value = 10;
        hasMoved = false;
        color = c;
    }

    /*
    This constructor takes a King as an input and copies all the
    attributes for the new king piece
     */
    public King(King cp)
    {
        super();
        color = cp.color;
        hasMoved = cp.hasMoved;
        value = cp.value;
    }

    private static HashSet<Vector> generateMoveList()
    {
        HashSet<Vector> moves = new HashSet<>();
        moves.add(new Vector(0, 1));
        moves.add(new Vector(1, 1));
        moves.add(new Vector(1, 0));
        moves.add(new Vector(1, -1));
        moves.add(new Vector(0, -1));
        moves.add(new Vector(-1, -1));
        moves.add(new Vector(-1, 0));
        moves.add(new Vector(-1, 1));
        return moves;
    }

    @Override
    public final HashSet<Vector> getMoveSet()
    {
        return MOVE_SET;
    }

    /**
     * Returns a string describing this king
     *
     * @return String - description of this
     */
    @Override
    public String toString()
    {
        String str = color == null ? "" : color.toString() + " ";
        str += "King";
        return str;
    }

    /**
     * Returns the one letter used in identifying this piece when recording
     * chess moves
     *
     * @return String - string of length 1, identifies this as a king
     */
    @Override
    public String oneLetterIdentifier()
    {
        return "K";
    }

    @Override
    public ChessPiece copyOfThis()
    {
        return new King(this);
    }

    /**
     * This method checks to see if two kings are the same.
     */
    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof King)
        {
            King cp = (King) obj;
            return color == cp.color;
        }
        return false;
    }
}
