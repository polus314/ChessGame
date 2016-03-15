package chessgame;

/**
To change this license header, choose License Headers in Project Properties.
To change this template file, choose Tools | Templates
and open the template in the editor.
*/

/**
 
@author jppolecat
*/

public class Bishop extends ChessPiece
{
   public Bishop()
   {
      type = PieceType.BISHOP;
   }
   
   public Bishop(PieceColor c, int xC, int yC)
   {
      type = PieceType.BISHOP;
      value = 3;
      xCoord = xC;
      yCoord = yC;
      color = c;
      if (c == PieceColor.BLACK)
      {
         pieceName = "BBishop";
      }
      else if (c == PieceColor.WHITE)
      {
         pieceName = "WBishop";
      }
      else
         System.out.println("Error!");
   }
   
   public Bishop(PieceColor c)
   {
      type = PieceType.BISHOP;
      value = 3;
      color = c;
      if (c == PieceColor.BLACK)
      {
         pieceName = "BBishop";
      }
      else if (c == PieceColor.WHITE)
      {
         pieceName = "WBishop";
      }
      else
         System.out.println("Error!");
   }
   
   public Bishop(Bishop cp)
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
      return "Bishop";
   }
   
   public boolean canMove(int x, int y)
   {
      for(int i = 1; i < 8; i++)
         if((xCoord+i == x && yCoord+i == y) ||
            (xCoord-i == x && yCoord+i == y) ||
            (xCoord+i == x && yCoord-i == y) ||
            (xCoord-i == x && yCoord-i == y) )
            return true;
      return false;
   }
   
   public Bishop clone() throws CloneNotSupportedException
   {
      super.clone();
      return new Bishop(this);
   }
}