package chessgame;

import java.util.ArrayList;

/**
Controller class for the chess game. Handles moving pieces as well as checking
for capturing, castling, and promotion.

@author jppolecat
*/
public class GameController 
{
   private ChessBoard board;
   private ChessPiece selectedPiece, pieceToAdd;
   private PieceColor playerToMove, humanPlayer;
   private AI deepBlue;
   private ArrayList<ChessMove> moveList;
   private GameMode mode;
   private boolean moveFinished;
   
   /**
   Default constructor, sets board to standard starting position and
   human player is White.
   */
   public GameController()
   {
      board = new ChessBoard();
      humanPlayer = PieceColor.WHITE;
      playerToMove = PieceColor.WHITE;
      deepBlue = new AI(board, playerToMove);
      moveList = new ArrayList<>();
      mode = GameMode.UNDECIDED;
      moveFinished = false;
   }
   
   public void setGameMode(GameMode m)
   {
      mode = m;
   }
   
   public ArrayList<ChessPiece> getPiecesList()
   {
      return board.getPiecesList();
   }
   
   public PieceColor getPlayerToMove()
   {
      return playerToMove;
   }
   
   public ChessPiece getSelectedPiece()
   {
      return selectedPiece;
   }
   
   public void setPieceToAdd(ChessPiece cp)
   {
      pieceToAdd = cp;
   }
   
   public ArrayList<ChessMove> getMoveList()
   {
      return moveList;
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
            if(isGameOver())
               return false;
             return doHumanTurn(x,y);
         case VERSUS:
            if(isGameOver())
               return false;
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
      return deepBlue.isGameOver();
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
   @return boolean - whether CPU was successful in making a move
   */
   public boolean doCPUTurn()
   {
      ChessMove move = deepBlue.findBestMove(humanPlayer.opposite());
      //checking for checkmate
      if(move == null)
      {
         //board.gameOver = true;
         //moveList.get(moveList.size() - 1).givesMate = true;
         return false;
      }
      
      int x = move.piece.getX();
      int y = move.piece.getY();
      //check for "type" of best move, if unoccupied simply move, else try to 
      //capture
      if (!move.captures)
      {
         ChessPiece cp = board.getPieceAt(x,y);
         board.movePiece(cp, move.getXDest(), move.getYDest());
      }
      else// if (move.getMoveType() == MoveType.CAPTURE)
      {
         selectedPiece = board.getPieceAt(x,y);
         board.capturePiece(selectedPiece, move.getXDest(), move.getYDest());
      }
      advanceTurn(move);
      return true;
   }

   /**
   Determines the appropriate action to take and attempts to execute that 
   action at the coordinates x and y. If the action is successful, that is,
   the game state has in any way changed (player has made a move, selected
   piece has changed, etc.), returns true.
   
   @param x - x coordinate at which to take action
   @param y - y coordinate at which to take action
   @return boolean - whether the appropriate action was successful
   */
   private boolean doHumanTurn(int x, int y)
   {
      boolean moveSuccessful = false;
      ChessMove move = new ChessMove(selectedPiece, x, y);
      if (selectedPiece == null)
      {
         return trySelect(x,y);
      }
      else if (selectedPiece.equals(board.getPieceAt(x,y)))
      {  //if clicked piece is selected, unselect the piece
         selectedPiece = null;
         return true;
      }
      // if piece is selected and space clicked on is empty, try to 
      // move to that space
      else if (board.spaceIsEmpty(x, y))
      {
         if(selectedPiece instanceof King) // try to castle
         {
            if(tryCastling(move))
            {
               moveSuccessful = true;
            }
         }
         if (!moveSuccessful && board.movePiece(selectedPiece, x, y)) // else try to move normally
         {
            moveSuccessful = true;
         }
      }
      else //capture the piece at the coordinates
      {
         if(board.capturePiece(selectedPiece, x, y))
         {
            moveSuccessful = true;
            move.captures = true;
         }
      }
      if(moveSuccessful)
      {
         advanceTurn(move);
         return true;
      }
      return false;
   }
   
   private boolean tryToPromote()
   {
      ChessPiece pawn = board.needPromotion();
      if (pawn != null)
      {
         int xCoord = pawn.getX(), yCoord = pawn.getY();
         ChessPiece queen = new Queen(pawn.getColor(), xCoord, yCoord);
         board.setPieceAt(queen, xCoord, yCoord);
         return true;
      }
      return false;
   }
   
   /**
   Attempts to select the piece at the given coordinates. If the space does 
   not contain a piece of the playerToMove's color, returns false.
   
   @param x - x coordinate of square of piece to select
   @param y - y coordinate of square of piece to select
   @return boolean - whether piece was successfully selected
   */
   private boolean trySelect(int x, int y)
   {
      if(board.getPieceAt(x,y) != null && 
         board.getPieceAt(x,y).getColor() == playerToMove)
      {
         selectedPiece = board.getPieceAt(x,y);
         return true;
      }
      return false;
   }
   
   private boolean doSetUp(int a, int b)
   {
      if(pieceToAdd != null)
      {
         pieceToAdd.xCoord = a;
         pieceToAdd.yCoord = b;
         board.setPieceAt(pieceToAdd.copyOfThis(), a, b);
      }
      else
      {
         board.setPieceAt(null, a, b);
      }
      return true;
   }
   
   /**
   Attempts to castle using the given move
   
   @param move - move used to attempt castling
   @return boolean - whether castling was successful or not
   */
   private boolean tryCastling(ChessMove move)
   {
      return board.castle(move);
   }
   
   private void advanceTurn(ChessMove move)
   {
      if(tryToPromote())
      {
         move.promotes = true;
      }
      if(board.checkForCheck(playerToMove.opposite()))
      {
         move.givesCheck = true;
      }
      if (board.checkForMate(playerToMove.opposite()))
      {
         move.givesMate = true;
      }
      selectedPiece = null;
      playerToMove = playerToMove.opposite();
      moveFinished = true;
      deepBlue = new AI(board, playerToMove);
      moveList.add(move);
   }
}
