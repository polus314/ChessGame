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
      type = PieceType.PAWN;
   }
   
   /*
   This constructor takes an input of the piece color and the starting
   position for the pawn
   */
   public Pawn(PieceColor c, int xC, int yC)
   {
      type = PieceType.PAWN;
      value = 1;
      xCoord = xC;
      yCoord = yC;
      color = c;
      if (c == PieceColor.BLACK)
      {
         pieceName = "BPawn";
      }
      else if (c == PieceColor.WHITE)
      {
         pieceName = "WPawn";
      }
      else
         System.out.println("Error!");
   }
   
   /*
   This constructor takes a piece color and sets the starting 
   position as the default pawn position
   */
   public Pawn(PieceColor c)
   {
      type = PieceType.PAWN;
      value = 1;
      color = c;
      if (c == PieceColor.BLACK)
      {
         pieceName = "BPawn";
      }
      else if (c == PieceColor.WHITE)
      {
         pieceName = "WPawn";
      }
      else
         System.out.println("Error!");
   }
   
   /*
   This constructor takes a Pawn as an input and copies all the
   attributes for the new pawn piece
   */
   public Pawn(Pawn cp)
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
   This returns the string "Pawn" 
   */
   public String toString()
   {
      return "Pawn";
   }
   
   /*
   This method determines if a selected Pawn can move to a selected 
   square. If the bishop can move to the selected square the method returns
   true, false otherwise
   */
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
}