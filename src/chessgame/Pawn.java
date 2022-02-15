package chessgame;

import java.util.HashSet;

/**
 * This class represents the Pawn chess piece. The Pawn is the weakest and most
 * common piece in Chess. At the beginning of the game, there are eight pawns
 * filling the second row for each side. If a pawn moves all the way to the
 * opponent's side of the board, it is "promoted" and is changed to any piece
 * except for a King or Pawn.
 *
 * @author John Polus
 */
public class Pawn extends ChessPiece
{

    /*
    This is the default constructor for a Pawn
     */
    public Pawn()
    {
        super();
    }

    /*
    This constructor takes a piece color
     */
    public Pawn(Color c)
    {
        super();
        value = 1;
        color = c;
    }

    /*
    This constructor takes a Pawn as an input and copies all the
    attributes for the new pawn piece
     */
    public Pawn(Pawn cp)
    {
        super();
        color = cp.color;
        hasMoved = cp.hasMoved;
        value = cp.value;
    }

    @Override
    public final HashSet<Vector> getMoveSet()
    {
        HashSet<Vector> moves = new HashSet<>();

        // White pawns move in -y direction
        if (color == ChessPiece.Color.WHITE)
        {
            moves.add(new Vector(0, -1));
            moves.add(new Vector(0, -2));
        }
        else // Black pawns move in +y direction
        {
            moves.add(new Vector(0, 1));
            moves.add(new Vector(0, 2));
        }
        return moves;
    }
    
    public final HashSet<Vector> getCaptureMoveSet()
    {
        HashSet<Vector> moves = new HashSet<>();

        // White pawns move in -y direction
        if (color == ChessPiece.Color.WHITE)
        {
            moves.add(new Vector(-1, -1));
            moves.add(new Vector(1, -1));
        }
        else // Black pawns move in +y direction
        {
            moves.add(new Vector(1, 1));
            moves.add(new Vector(-1, 1));
        }
        return moves;
    }

    /**
     * Returns a string describing this pawn
     *
     * @return String - description of this
     */
    @Override
    public String toString()
    {
        String str = color == null ? "" : color.toString() + " ";
        str += "Pawn";
        return str;
    }

    /**
     * Returns the one letter used in identifying this piece when recording
     * chess moves
     *
     * @return String - string of length 0, pawns are only referenced by their
     * position
     */
    @Override
    public String oneLetterIdentifier()
    {
        return "";
    }

    /*
    This method determines if this Pawn can move to a selected 
    square. If the pawn can move to the selected square the method returns
    true, false otherwise
     */
    @Override
    public boolean canMove(Vector move)
    {
        HashSet<Vector> moveset = getMoveSet();
        return moveset.contains(move);
    }

    @Override
    public ChessPiece copyOfThis()
    {
        return new Pawn(this);
    }

    /**
     * This method checks to see if two pawns are the same.
     *
     * @param obj -
     * @return whether obj is equal to this
     */
    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof Pawn)
        {
            Pawn cp = (Pawn) obj;
            return color == cp.color;
        }
        return false;
    }
}
