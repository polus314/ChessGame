package chessgame;

/**
 This class represents the King piece in chess. The King is the most
 important, though not most powerful, piece in the game. An attack on the King
 ("check"), must be dealt with immediately, as the game ends when the King
 cannot escape capture ("checkmate"). The King moves one square in any
 direction, except when castling, when he moves two spaces toward one of his
 rooks.

 @author John Polus
 */
public class King extends ChessPiece
{

   /*
    This is the default constructor for the King piece
    */
   public King()
   {
      super();
   }

   /*
    This constructor takes an input of the piece color and the starting
    position for the king
    */
   public King(Color c, int xC, int yC)
   {
      super();
      hasMoved = false;
      value = 10;
      xCoord = xC;
      yCoord = yC;
      color = c;
   }

   /*
    This constructor takes a piece color and sets the starting 
    position as the default king position
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
      xCoord = cp.xCoord;
      yCoord = cp.yCoord;
   }

   /**
    Returns a string describing this king

    @return String - description of this
    */
   @Override
   public String toString()
   {
      String str = color == null ? "" : color.toString() + " ";
      str += "King";
      return str;
   }

   /**
    Returns the one letter used in identifying this piece when recording chess
    moves

    @return String - string of length 1, identifies this as a king
    */
   @Override
   public String oneLetterIdentifier()
   {
      return "K";
   }

   /*
    This method determines if this king can move to a selected 
    square. If the king can move to the selected square the method returns
    true, false otherwise
    */
   @Override
   public boolean canMove(int x, int y)
   {
      if (-1 <= x - xCoord && x - xCoord <= 1)
      {
         if (-1 <= y - yCoord && y - yCoord <= 1)
         {
            return true;
         }
      }
      return false;
   }

   @Override
   public ChessPiece copyOfThis()
   {
      return new King(this);
   }

   /**
    This method checks to see if two kings are the same.
    */
   @Override
   public boolean equals(Object obj)
   {
      if (obj instanceof King)
      {
         King cp = (King) obj;
         return xCoord == cp.xCoord
               && yCoord == cp.yCoord
               && color == cp.color;
      }
      return false;
   }
}
