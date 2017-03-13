package chessgame;

import java.util.ArrayList;

/**
Controller class for the chess game. Handles moving pieces as well as checking
for capturing, castling, and promotion.

@author jppolecat
*/
public class GameController 
{
   // TODO - make this private
   public ChessBoard board;
   private ChessPiece selectedPiece, pieceToAdd;
   private PieceColor playerToMove, humanPlayer;
   private AI deepBlue;
   private ArrayList<ChessMove> moveList;
   private GameMode mode;
   private boolean moveFinished;
   
   /**
   Default constructor, sets board to standard starting position and
   playerToMove is White.
   */
   public GameController()
   {
      board = new ChessBoard();
      playerToMove = PieceColor.WHITE;
      humanPlayer = PieceColor.WHITE;
      deepBlue = new AI(board);
      moveList = new ArrayList<>();
      mode = GameMode.UNDECIDED;
      moveFinished = false;
   }
   
   public ChessPiece getSelectedPiece()
   {
      return selectedPiece;
   }
   
   /**
   Takes the appropriate action for when the given coordinates are selected.
   Considers what game mode it is, whether there is a piece selected, and the
   next player to move.
   
   @param x - x coordinate of square to take action on
   @param y - y coordinate of square to take action on
   @return boolean - whether an action was successfully executed or not
   */
   public boolean takeAction(int x, int y)
   {
      switch(mode)
      {
         case SINGLE:
            return doHumanTurn(x,y);
         case VERSUS:
            return doHumanTurn(x,y);
         case SET_UP:
            return doSetUp(x,y);
            
      }
      return false;
   }
   
   /**
   Returns true if the current game has ended. If in set-up mode, always
   returns true.
   
   @return boolean - whether or not game is over
   */
   public boolean isGameOver()
   {
      return false;
   }
   
   /**
   Returns the color that won the game. If the game hasn't ended or is a tie,
   returns null.
   
   @return 
   */
   public PieceColor getWinningSide()
   {
      return deepBlue.getWinningSide();
   }
   
   /**
   This method does most of the things done in the main GUI method, but for
   the computer player
   */
   private boolean doCPUTurn()
   {
      ChessPiece queen;
      ChessMove move = deepBlue.findBestMove(humanPlayer.opposite());
      //checking for checkmate
      if(move == null)
      {
         board.gameOver = true;
         moveList.get(moveList.size() - 1).givesMate = true;
         return false;
      }
      //check for "type" of best move, if unoccupied simply move, else try to 
      //capture
      if (move.getMoveType() == MoveType.UNOCCUPIED)
      {
         ChessPiece cp = board.getPieceAt(move.piece.getX(), move.piece.getY());
         board.movePiece(cp, move.getXDest(), move.getYDest());
      }
      else if (move.getMoveType() == MoveType.CAPTURE)
      {
         selectedPiece = board.getPieceAt(move.piece.getX(), move.piece.getY());
         board.capturePiece(selectedPiece, move.getXDest(), move.getYDest());
      }
      queen = board.needPromotion();
      //promote pawns as necessary
      if (queen != null)
      {
         int x = queen.getX(), y = queen.getY();
         queen = new Queen(queen.getColor(), x, y);
         board.setPieceAt(queen, x, y);
         move.setMoveType(MoveType.PROMOTION);
      }
      //introduces concept of check, not implemented logically yet for human
      if (board.checkForCheck(humanPlayer))
      {
         move.givesCheck = true;
      }
      //GUI list of moves is updated
      moveList.add(move);
      //unless the game is over
      if (deepBlue.isGameOver())
         return false;
      //allow human to make next move
      board.setPlayerToMove(humanPlayer);
      return true;
   }
   
   
   private boolean doHumanTurn(int a, int b)
   {
      
      //need to declare a piece for possible promotion
      ChessPiece queen;
      //declare a possible move
      ChessMove move = new ChessMove(selectedPiece, a, b);
      //if no piece is selected, select the piece clicked on
      if (selectedPiece == null)
      {
         if(board.getPieceAt(a,b).getColor() == board.getPlayerToMove())
            selectedPiece = board.getPieceAt(a,b);
      }
      //if clicked piece is selected, unselect the piece
      else if (selectedPiece.equals(board.getPieceAt(a,b)))
      {
         selectedPiece = null;
      }
      // if piece is selected and space clicked on is empty, try to 
      // move to that space, will only work if piece moves that direction
      // and the path is clear
      else if (board.spaceIsEmpty(a, b))
      {
         if(move.piece instanceof King)
         {
            if(board.canCastleKS(humanPlayer)
                  || board.canCastleQS(humanPlayer))
            {
               if(board.castle(move))
               {
                  moveList.add(move);
                  if (!deepBlue.isGameOver() && mode == GameMode.SINGLE)
                  {
                     board.setPlayerToMove(board.getPlayerToMove().opposite());
                     selectedPiece = null;
                     moveFinished = true;
                  }
               }
            }
         }
         ChessPiece selected = selectedPiece;
         if (board.movePiece(selected, a, b))
         {
            //if a pawn is on the last rank, places a queen there
            queen = board.needPromotion();
            if (queen != null)
            {
               int xCoord = queen.getX(), yCoord = queen.getY();
               queen = new Queen(queen.getColor(), xCoord, yCoord);
               board.setPieceAt(queen, xCoord, yCoord);
               move.setMoveType(MoveType.PROMOTION);
            }
            if(board.checkForCheck(humanPlayer.opposite()))
               move.givesCheck = true;
            //end game if a king is missing, set CPU to move so no
            //more mouse events are processed on the checker board
            if (!deepBlue.isGameOver() && mode == GameMode.SINGLE)
            {
               moveList.add(move);
               board.setPlayerToMove(board.getPlayerToMove().opposite());       
               moveFinished = true;
            }
            else if(deepBlue.isGameOver())
            {
               move.givesMate = true;
               moveList.add(move);
            }
            selectedPiece = null;
         }
      }
      else
      {
         //capture the piece that was clicked on, which might fail
         if(board.capturePiece(selectedPiece, a, b))
         {
            move.setMoveType(MoveType.CAPTURE);
            if(board.checkForCheck(humanPlayer.opposite()))
               move.givesCheck = true;
            queen = board.needPromotion();
            if (board.needPromotion() != null)
            {
               int xCoord = queen.getX(), yCoord = queen.getY();
               queen = new Queen(queen.getColor(), xCoord, yCoord);
               board.setPieceAt(queen, xCoord, yCoord);  
               move.setMoveType(MoveType.PROMOTION);
            }
            if (!deepBlue.isGameOver())
            {
               moveList.add(move);
               moveFinished = true;
            }
            else
            {
               move.givesMate = true;
               moveList.add(move);
            }
            selectedPiece = null;
         }
      }
      return true;
   }
   
   
   private boolean doSetUp(int a, int b)
   {
      if(pieceToAdd == null)
      {
         board.setPieceAt(null, a, b);
      }
      if(selectedPiece == null
            && board.getPieceAt(a,b) != null)
      {
         selectedPiece = board.getPieceAt(a,b);
      }
      else if(selectedPiece == null)
      {
         board.setPieceAt(pieceToAdd, a, b);
      }
      else if(selectedPiece.equals(board.getPieceAt(a,b)))
      {
         selectedPiece = null;
      }
      else
      {
         board.movePiece(selectedPiece, a, b);
      }
      return true;
   }
}
