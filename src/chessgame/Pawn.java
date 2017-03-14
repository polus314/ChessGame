package chessgame;

/**
This class is for the movements of the Pawn piece
*/

/**
 
@author jppolecat
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
   This constructor takes an input of the piece color and the starting
   position for the pawn
   */
   public Pawn(PieceColor c, int xC, int yC)
   {
      super();
      value = 1;
      xCoord = xC;
      yCoord = yC;
      color = c;
   }
   
   /*
   This constructor takes a piece color and sets the starting 
   position as the default pawn position
   */
   public Pawn(PieceColor c)
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
      xCoord = cp.xCoord;
      yCoord = cp.yCoord;
   }
   
   /**
   Returns a string describing this pawn
   
   @return String - description of this
   */
   @Override
   public String toString()
   {
      return color.toString() + " Pawn";
   }
   
   /**
   Returns the one letter used in identifying this piece when recording chess
   moves
   
   @return String - string of length 0, pawns are only referenced by their
   position
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
   public boolean canMove(int x, int y)
   {
      if(x != xCoord)
         return false;
      if(color == PieceColor.WHITE)
      {
         if(yCoord - y == 2)
            if(yCoord == 6)
               return true;
         if(yCoord - y == 1)
            return true;
      }
      if(color == PieceColor.BLACK)
      {
         if(y - yCoord == 2)
            if(yCoord == 1)
               return true;
         if(y - yCoord == 1)
            return true;
      }
      return false;
   }
   
   @Override
   public ChessPiece copyOfThis()
   {
      return new Pawn(this);
   }
   
   /**
   This method checks to see if two pawns are the same.
   */
   @Override
   public boolean equals(Object obj)
   {
      if (obj instanceof Knight)
      {
         Knight cp = (Knight) obj;
         return xCoord == cp.xCoord && 
                yCoord == cp.yCoord && 
                color == cp.color;
      }
      return false;
   }
}