package chessgame;

/**
This class is for the movements of the Rook piece
*/

/**
 
@author jppolecat
*/
public class Rook extends ChessPiece
{
    /*
   This is the default constructor for a Rook
   */
   public Rook()
   {
      type = PieceType.ROOK;
   }
   
   /*
   This constructor takes an input of the piece color and the starting
   position for the Rook
   */
   public Rook(PieceColor c, int xC, int yC)
   {
      type = PieceType.ROOK;
      value = 5;
      xCoord = xC;
      yCoord = yC;
      color = c;
      if (c == PieceColor.BLACK)
      {
         pieceName = "BRook";
      }
      else if (c == PieceColor.WHITE)
      {
         pieceName = "WRook";
      }
      else
         System.out.println("Error!");
   }
   
   /*
   This constructor takes a piece color and sets the starting 
   position as the default Rook position
   */
   public Rook(PieceColor c)
   {
      type = PieceType.ROOK;
      value = 5;
      color = c;
      if (c == PieceColor.BLACK)
      {
         pieceName = "BRook";
      }
      else if (c == PieceColor.WHITE)
      {
         pieceName = "WRook";
      }
   }
   
   /*
   This constructor takes a Rook as an input and copies all the
   attributes for the new Rook piece
   */
   public Rook(Rook cp)
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
   This returns the string "Rook" 
   */
   public String toString()
   {
      return "Rook";
   }
   
   /*
   This method determines if a selected Rook can move to a selected 
   square. If the bishop can move to the selected square the method returns
   true, false otherwise
   */
   public boolean canMove(int x, int y)
   {
      if(xCoord != x && yCoord != y)
         return false; 
      return true;
   }
}