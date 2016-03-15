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
   WHITE, BLACK, EMPTY;
   
   public PieceColor next()
   {
      if (this == WHITE)
         return BLACK;
      if (this == BLACK)
         return EMPTY;
      if (this == EMPTY)
         return WHITE;
      return EMPTY;
   }
   
   public PieceColor opposite()
   {
      if (this == WHITE)
         return BLACK;
      if (this == BLACK)
         return WHITE;
      return EMPTY;
   }
}
