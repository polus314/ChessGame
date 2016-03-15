package chessgame;

/**
To change this license header, choose License Headers in Project Properties.
To change this template file, choose Tools | Templates
and open the template in the editor.
*/

/**
 
@author jppolecat
*/
public class King extends ChessPiece
{  
   public King()
   {
      type = PieceType.KING;
   }
   
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
   
   @return 
   */
   public String toString()
   {
      return "King";
   }
   
   public boolean canMove(int x, int y)
   {
      if(x - xCoord > -2 && x - xCoord < 2)
         if(y - yCoord > -2 && y - yCoord < 2)
            return true;
      return false;
   }
}