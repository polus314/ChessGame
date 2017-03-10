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
      type = PieceType.KING;
   }
   
   /*
   This constructor takes an input of the piece color and the starting
   position for the king
   */
   public King(PieceColor c, int xC, int yC)
   {
      type = PieceType.KING;
      hasMoved = false;
      value = 10;
      xCoord = xC;
      yCoord = yC;
      color = c;
      if (c == PieceColor.BLACK)
      {
         pieceName = "BKing";
      }
      else if (c == PieceColor.WHITE)
      {
         pieceName = "WKing";
      }
      else
         System.out.println("Error in King constructor!");
   }
   
   /*
   This constructor takes a piece color and sets the starting 
   position as the default king position
   */
   public King(PieceColor c)
   {
      type = PieceType.KING;
      value = 10;
      hasMoved = false;
      color = c;
      if (c == PieceColor.BLACK)
      {
         pieceName = "BKing";
      }
      else if (c == PieceColor.WHITE)
      {
         pieceName = "WKing";
      }
      else
         System.out.println("Error!");
   }
   
   /*
   This constructor takes a King as an input and copies all the
   attributes for the new king piece
   */
   public King(King cp)
   {
      type = cp.type;
      pieceName = cp.pieceName;
      color = cp.color;
      selected = cp.selected;
      hasMoved = cp.hasMoved;
      value = cp.value;
      xCoord = cp.xCoord;
      yCoord = cp.yCoord;
   }
   
   /**
   This returns the string "King" 
   */
   public String toString()
   {
      return "King";
   }
   
   /*
   This method determines if a selected King can move to a selected 
   square. If the bishop can move to the selected square the method returns
   true, false otherwise
   */
   public boolean canMove(int x, int y)
   {
      if(x - xCoord > -2 && x - xCoord < 2)
         if(y - yCoord > -2 && y - yCoord < 2)
            return true;
      return false;
   }
}