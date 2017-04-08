package chessgame;

/**
 This AI is specialized for the end of a chess game. When there are only a few
 pieces left on the board, different factors are important to search for.

@author jppolecat
*/
public class EndGameAI extends AI 
{
   
   public EndGameAI()
   {
      super();
   }
   
   public EndGameAI(ChessBoard cb, ChessPiece.Color playerToMove)
   {
      super(cb, playerToMove);
   }
   
   /**
   
   @param cb 
   */
   @Override
   protected void rateBoard(ChessBoard cb, ChessPiece.Color player)
   {
      int kingDistance = findDistanceBetweenKings();
   }
   
   private int findDistanceBetweenKings()
   {
      return 0;
   }
}
