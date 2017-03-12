/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chessgame;

/**
The two colors that a piece can be, or an empty piece
 @author jppolecat
 */
public enum PieceColor
{
   WHITE, BLACK;
   
   public PieceColor opposite()
   {
      if (this == WHITE)
         return BLACK;
      else //if (this == BLACK)
         return WHITE;
   }
   
   @Override
   public String toString()
   {
      if(this == WHITE)
         return "White";
      else //(this == BLACK)
         return "Black";
   }
}
