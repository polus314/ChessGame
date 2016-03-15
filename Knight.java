package chessgame;

/**
To change this license header, choose License Headers in Project Properties.
To change this template file, choose Tools | Templates
and open the template in the editor.
*/

/**
 
@author jppolecat
*/
public class Knight extends ChessPiece
{
   public Knight()
   {
      type = PieceType.KNIGHT;
   }
   
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
   
   @return 
   */
   public String toString()
   {
      return "Knight";
   }
   
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
   
   public Knight clone() throws CloneNotSupportedException
   {
      super.clone();
      return new Knight(this);
   }
}