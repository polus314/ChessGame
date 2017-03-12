package chessgame;

/**
This class is for the movements of the King piece
*/

/**
 
@author jppolecat
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
   public King(PieceColor c, int xC, int yC)
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
   public King(PieceColor c)
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
      return color.toString() + " King";
   }
   
   /*
   This method determines if this king can move to a selected 
   square. If the king can move to the selected square the method returns
   true, false otherwise
   */
   @Override
   public boolean canMove(int x, int y)
   {
      if(-1 <= x - xCoord && x - xCoord <= 1)
         if(-1 <= y - yCoord && y - yCoord <= 1)
            return true;
      return false;
   }
}