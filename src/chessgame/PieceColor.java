package chessgame;

/**
 Enumerated type for which player a piece belongs to. 
 WHITE - player who moves first 
 BLACK - player who moves second

 @author John Polus
 */
public enum PieceColor
{

   WHITE, BLACK;

   public PieceColor opposite()
   {
      if (this == WHITE)
      {
         return BLACK;
      }
      else //if (this == BLACK)
      {
         return WHITE;
      }
   }

   @Override
   public String toString()
   {
      if (this == WHITE)
      {
         return "White";
      }
      else //(this == BLACK)
      {
         return "Black";
      }
   }

   public String oneLetter()
   {
      if (this == WHITE)
      {
         return "W";
      }
      else //(this == BLACK)
      {
         return "B";
      }
   }
}
