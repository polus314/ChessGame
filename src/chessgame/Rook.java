package chessgame;

/**
 This class represents the Rook chess piece. The Rook moves parallel to the
 axes of the board, either vertical or horizontal, as far as it wants. The
 Rook is involved in castling, where the King moves two spaces toward it and
 the Rook is placed on the other side. The Rook is one of the "major" pieces
 along with the Queen.

 @author John Polus
 */
public class Rook extends ChessPiece
{
   /*
    This is the default constructor for a Rook
    */

   public Rook()
   {
      super();
   }

   /*
    This constructor takes an input of the piece color and the starting
    position for the Rook
    */
   public Rook(Color c, int xC, int yC)
   {
      super();
      value = 5;
      xCoord = xC;
      yCoord = yC;
      color = c;
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
      xCoord = cp.xCoord;
      yCoord = cp.yCoord;
   }

   /**
    Returns a string describing this rook

    @return String - description of this
    */
   @Override
   public String toString()
   {
      String str = color == null ? "" : color.toString() + " ";
      str += "Rook";
      return str;
   }

   /**
    Returns the one letter used in identifying this piece when recording chess
    moves

    @return String - string of length 1, identifies this as a rook
    */
   @Override
   public String oneLetterIdentifier()
   {
      return "R";
   }

   /*
    This method determines if a selected Rook can move to a selected 
    square. If the rook can move to the selected square the method returns
    true, false otherwise
    */
   @Override
   public boolean canMove(int x, int y)
   {
      return xCoord == x || yCoord == y;
   }

   @Override
   public ChessPiece copyOfThis()
   {
      return new Rook(this);
   }

   /**
    This method checks to see if two rooks are the same.
    */
   @Override
   public boolean equals(Object obj)
   {
      if (obj instanceof Rook)
      {
         Rook cp = (Rook) obj;
         return xCoord == cp.xCoord
               && yCoord == cp.yCoord
               && color == cp.color;
      }
      return false;
   }
}
