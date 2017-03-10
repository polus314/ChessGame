package chessgame;

/**
This class is for the movements of the Knight piece
*/

/**
 
@author jppolecat
*/
public class Knight extends ChessPiece
{
    
    /*
    This is the default constructor for the knight piece
    */
   public Knight()
   {
      type = PieceType.KNIGHT;
   }
   
   /*
   This is the constructor that takes a piece color and a starting 
   position for inputs
   */
   public Knight(PieceColor c, int xC, int yC)
   {
      type = PieceType.KNIGHT;
      value = 3;
      xCoord = xC;
      yCoord = yC;
      color = c;
      if (c == PieceColor.BLACK)
      {
         pieceName = "BKnight";
      }
      else if (c == PieceColor.WHITE)
      {
         pieceName = "WKnight";
      }
      else
         System.out.println("Error!");
   }
   
   /*
   This constructor takes a piece color and sets the starting 
   position as the default position for the knight piece
   */
   public Knight(PieceColor c)
   {
      type = PieceType.KNIGHT;
      value = 3;
      color = c;
      if (c == PieceColor.BLACK)
      {
         pieceName = "BKnight";
      }
      else if (c == PieceColor.WHITE)
      {
         pieceName = "WKnight";
      }
      else
         System.out.println("Error!");
   }
   
   /*
   This takes a knight oject as input and copies all the attributes
   to the new knight piece
   */
   public Knight(Knight cp)
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
   This returns the string "Knight" 
   */
   public String toString()
   {
      return "Knight";
   }
   
   /*
   This method determines if a selected Knight can move to a selected 
   square. If the bishop can move to the selected square the method returns
   true, false otherwise
   */
   public boolean canMove(int x, int y)
   {
      if(xCoord + 2 == x && yCoord + 1 == y)
         return true;
      if(xCoord + 1 == x && yCoord + 2 == y)
         return true;
      if(xCoord - 2 == x && yCoord + 1 == y)
         return true;
      if(xCoord + 1 == x && yCoord - 2 == y)
         return true;
      if(xCoord + 2 == x && yCoord - 1 == y)
         return true;
      if(xCoord -1 == x && yCoord + 2 == y)
         return true;
      if(xCoord - 2 == x && yCoord - 1 == y)
         return true;
      if(xCoord - 1 == x && yCoord - 2 == y)
         return true;
      return false;
   }
}