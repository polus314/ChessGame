package chessgame;


/**
This class is for the movements of the Queen piece
*/

/**

 @author jppolecat
 */
public class Queen extends ChessPiece
{
   /*
   This is the default constructor for a Queen
   */
   public Queen()
   {
      type = PieceType.QUEEN;
   }
   
   /*
   This constructor takes an input of the piece color and the starting
   position for the Queen
   */
   public Queen(PieceColor c, int xC, int yC)
   {
      type = PieceType.QUEEN;
      value = 9;
      color = c;
      xCoord = xC;
      yCoord = yC;
      if (c == PieceColor.BLACK)
      {
         pieceName = "BQueen";
      }
      else if (c == PieceColor.WHITE)
      {
         pieceName = "WQueen";
      }
      else
      {
         System.out.println("Error!");
      }
   }
   
   /*
   This constructor takes a piece color and sets the starting 
   position as the default Queen position
   */
   public Queen(PieceColor c)
   {
      type = PieceType.QUEEN;
      value = 9;
      color = c;
            if (c == PieceColor.BLACK)
      {
         pieceName = "BQueen";
      }
      else if (c == PieceColor.WHITE)
      {
         pieceName = "WQueen";
      }
      else
      {
         System.out.println("Error!");
      }
   }
   
   /*
   This constructor takes a Queen as an input and copies all the
   attributes for the new Queen piece
   */
   public Queen(Queen cp)
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
      return "Queen";
   }

   /*
   This method determines if a selected Queen can move to a selected 
   square. If the bishop can move to the selected square the method returns
   true, false otherwise
   */
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
}
