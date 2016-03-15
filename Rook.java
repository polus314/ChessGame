package chessgame;

/**
To change this license header, choose License Headers in Project Properties.
To change this template file, choose Tools | Templates
and open the template in the editor.
*/

/**
 
@author jppolecat
*/
public class Rook extends ChessPiece
{
   public Rook()
   {
      type = PieceType.ROOK;
   }
   
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
   
   @return 
   */
   public String toString()
   {
      return "Rook";
   }
   
   public boolean canMove(int x, int y)
   {
      if(xCoord != x && yCoord != y)
         return false; 
      return true;
   }
   
   public Rook clone() throws CloneNotSupportedException
   {
      super.clone();
      return new Rook(this);
   }
}