package chessgame;

/**
To change this license header, choose License Headers in Project Properties.
To change this template file, choose Tools | Templates
and open the template in the editor.
*/

/**
 
@author jppolecat
*/
public class Pawn extends ChessPiece
{
   public Pawn()
   {
      type = PieceType.PAWN;
   }
   
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
   
   @return 
   */
   public String toString()
   {
      return "Pawn";
   }
   
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
   
   public Pawn clone() throws CloneNotSupportedException
   {
      super.clone();
      return new Pawn(this);
   }
}