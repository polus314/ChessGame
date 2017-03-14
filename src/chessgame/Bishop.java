package chessgame;

/**
This class is for the movements of the bishop piece
*/

/**
 
@author jppolecat
*/

public class Bishop extends ChessPiece
{
    /*
    This is the default constructor
    */
   public Bishop()
   {
      super();
   }
   
   /*
   This is the constructor that takes a color and the starting position 
   of the piece
   */
   public Bishop(PieceColor c, int xC, int yC)
   {
      super();
      value = 3;
      xCoord = xC;
      yCoord = yC;
      color = c;
   }
   
   /*
   This constructor sets the starting position to the default position of
   a bishop and takes a color
   */
   public Bishop(PieceColor c)
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
      xCoord = cp.xCoord;
      yCoord = cp.yCoord;
   }
   
   /**
   Returns a string describing this bishop
   
   @return String - description of this
   */
   @Override
   public String toString()
   {
      return color.toString() + " Bishop";
   }
   
   /**
   Returns the one letter used in identifying this piece when recording chess
   moves
   
   @return String - string of length 1, identifies this as a bishop
   */
   @Override
   public String oneLetterIdentifier()
   {
      return "B";
   }
   
   /*
   This method determines if this bishop can move to a selected 
   square. If the bishop can move to the selected square the method returns
   true, false otherwise
   */
   @Override
   public boolean canMove(int x, int y)
   {
      for(int i = 1; i < 8; i++)
         if((xCoord + i == x && yCoord + i == y) ||
            (xCoord - i == x && yCoord + i == y) ||
            (xCoord + i == x && yCoord - i == y) ||
            (xCoord - i == x && yCoord - i == y) )
            return true;
      return false;
   }
   
   @Override
   public ChessPiece copyOfThis()
   {
      return new Bishop(this);
   }
   
   /**
   This method checks to see if two bishops are the same.
   */
   @Override
   public boolean equals(Object obj)
   {
      if (obj instanceof Bishop)
      {
         Bishop cp = (Bishop) obj;
         return xCoord == cp.xCoord && 
                yCoord == cp.yCoord && 
                color == cp.color;
      }
      return false;
   }
}