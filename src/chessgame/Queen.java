package chessgame;

/**
 This class represents the Queen chess piece. The Queen is the most powerful
 piece, combining the movements of the Rook and Bishop, and as a result is the
 (second) most valuable piece (behind the King). The Queen is one of the
 "major" pieces along with the Rook.

 @author John Polus
 */
public class Queen extends ChessPiece
{
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

   /**
    Returns a string describing this queen

    @return String - description of this
    */
   @Override
   public String toString()
   {
      String str = color == null ? "" : color.toString() + " ";
      str += "Queen";
      return str;
   }

   /**
    Returns the one letter used in identifying this piece when recording chess
    moves

    @return String - string of length 1, identifies this as a queen
    */
   @Override
   public String oneLetterIdentifier()
   {
      return "Q";
   }

   /*
    This method determines if this queen can move to a selected 
    square. If the queen can move to the selected square the method returns
    true, false otherwise
    */
   @Override
   public boolean canMove(int x, int y)
   {
      for (int i = 1; i < 8; i++)
      {
         if ((xCoord + i == x && yCoord + i == y)
               || (xCoord - i == x && yCoord + i == y)
               || (xCoord + i == x && yCoord - i == y)
               || (xCoord - i == x && yCoord - i == y))
         {
            return true;
         }
      }
      if (x != xCoord && y != yCoord)
      {
         return false;
      }
      return true;
   }

   @Override
   public ChessPiece copyOfThis()
   {
      return new Queen(this);
   }

   /**
    This method checks to see if two queens are the same.
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
