package chessgame;


/**
 To change this license header, choose License Headers in Project Properties.
 To change this template file, choose Tools | Templates and open the template
 in the editor.
 */

/**

 @author jppolecat
 */
public class Queen extends ChessPiece
{
   public Queen()
   {
      type = PieceType.QUEEN;
   }
   
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

    @return
    */
   public String toString()
   {
      return "Queen";
   }

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
