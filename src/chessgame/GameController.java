package chessgame;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import chessgui.GameRequest;

/**
 Controller class for the chess game. Handles moving pieces as well as
 checking for capturing, castling, and promotion. Ensures that players are
 alternating turns and makes the CPU's move if in single player mode.

 @author John Polus
 */
public class GameController implements Runnable
{

   private ChessBoard board;
   private ChessPiece selectedPiece, pieceToAdd;
   private ChessPiece.Color playerToMove;
   public AI deepBlue;
   private ArrayList<ChessMove> moveList;
   private GameMode mode;
   private final BlockingQueue<GameRequest> tasks;
   private final BlockingQueue<GameRequest> responses;
   
   /**
    * Default constructor, sets board to standard starting position and human
    * player is White.
    * 
    * @param queue 
    */
   public GameController(BlockingQueue<GameRequest> tasks, BlockingQueue<GameRequest> responses)
   {
      board = new ChessBoard();
      playerToMove = ChessPiece.Color.WHITE;
      deepBlue = new AI(board, playerToMove);
      moveList = new ArrayList<>();
      mode = GameMode.UNDECIDED;
      this.tasks = tasks;
      this.responses = responses;
   }
   
   public void run()
   {
       GameRequest task, response;
       //int count = 0;
       while (true)
       {
           //try {Thread.sleep(5000); } catch (Exception e) {}
           //System.out.println(++count + ": I am processing stuff");
           //if (true) continue;
           //check queue
           while ((task = tasks.poll()) != null)
           {
               response = doTask(task);
               responses.add(response);
           }
               
       }
   }
   
   private GameRequest task_SetBoardPosition(GameRequest request)
   {
       GameRequest response = new GameRequest(request.task, null, false);
       ArrayList<ChessPiece> newPieceList = (ArrayList<ChessPiece>)request.info;
       if (newPieceList == null)
       {
           return response;
       }
       setBoardPosition(newPieceList, playerToMove);
       response.success = true;
       return response;
   }
   
   private GameRequest task_PlayMove(GameRequest request)
   {
       GameRequest response = new GameRequest(request.task, null, false);
       ChessMove move = (ChessMove)request.info;
       if (move == null || move.piece.getColor() != playerToMove)
       {
          return response;
       }
       if (move.getMoveType() != ChessMove.Type.NORMAL)
       {
           response.success = board.castle(move);
       }
       else if (move.captures)
       {
           response.success = board.capturePiece(move.piece, move.getXDest(), move.getYDest());
       }
       else // normal, non-capturing move
       {
           response.success = board.movePiece(move.piece, move.getXDest(), move.getYDest());
       }
       response.info = board.getPieces();
       if (response.success)
       {
           playerToMove = playerToMove.opposite();
           moveList.add(move);
       }
       return response;
   }
   
   private GameRequest task_FindBestMove(GameRequest request)
   {
       GameRequest response = new GameRequest(request.task, null, false);
       deepBlue = new AI(board, playerToMove);
       ChessMove bestMove = deepBlue.findBestMove();
       if (bestMove == null)
       {
           return response;
       }
       response.info = bestMove;
       response.success = true;
       return response;
   }
   
   
   /**
    * Request should have an incomplete ChessMove as its info. The ChessMove
    * should have the piece to move, and the desired destination coordinates.
    * This method will determine whether a legal move is possible, as well as
    * whether it involves capturing, giving check, en passant, etc.
    * 
    * @param request
    * @return 
    */
   private GameRequest task_CreateMove(GameRequest request)
   {
       GameRequest response = new GameRequest(request.task, null, false);
       ChessMove move = (ChessMove)request.info;
       move = board.validateMove(move);
       if (move == null)
       {
           return response;
       }
       
       response.info = move;
       response.success = true;
       return response;
   }
   
   private GameRequest task_Default(GameRequest request)
   {
       return new GameRequest();
   }
   
   private GameRequest doTask(GameRequest request)
   {
       switch (request.task)
       {
           case CREATE_MOVE:
               return task_CreateMove(request);
           case SET_BOARD_POSITION: 
               return task_SetBoardPosition(request);
           case PLAY_MOVE:          
               return task_PlayMove(request);
           case FIND_BEST_MOVE:     
               return task_FindBestMove(request);
           default:                 
               return task_Default(request);
       }
   }

   public void setGameMode(GameMode m)
   {
      mode = m;
   }

   public GameMode getGameMode()
   {
      return mode;
   }

   public ArrayList<ChessPiece> getPiecesList()
   {
      return board.getPieces();
   }

   public ChessPiece.Color getPlayerToMove()
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
    Sets the board with the given list of pieces IF the resulting position is
    legal. If position is illegal, does nothing. Criteria for legality are
    listed in ChessBoard.checkPositionIsLegal()
   
    @param pieces - list of pieces specifying the board position to set up
    @return whether board was legal and position was updated
    */
   public boolean setBoardPosition(ArrayList<ChessPiece> pieces, 
         ChessPiece.Color playerToMove)
   {
      ChessBoard temp = new ChessBoard(pieces);
      if (temp.checkPositionIsLegal())
      {
         board = temp;
         deepBlue = new AI(temp, playerToMove);
         return true;
      }
      return false;
   }
   
   public ArrayList<ChessMove> solveForMate(ChessPiece.Color color, int moves, 
         boolean quickly)
   {
      return null; //deepBlue.solveForMate(color, moves, quickly);
   }
   
   public ChessMove findBestMove()
   {
       return deepBlue.findBestMove();
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
      switch (mode)
      {
         case SINGLE:
            if (isGameOver())
            {
               return false;
            }
            return doHumanTurn(x, y);
         case VERSUS:
            if (isGameOver())
            {
               return false;
            }
            return doHumanTurn(x, y);
         case SET_UP:
            return doSetUp(x, y);

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
   public ChessPiece.Color getWinningSide()
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
      ChessMove move = deepBlue.findBestMove();
      //checking for checkmate
      if (move == null)
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
         ChessPiece cp = board.getPieceAt(x, y);
         board.movePiece(cp, move.getXDest(), move.getYDest());
      }
      else// if (move.getMoveType() == MoveType.CAPTURE)
      {
         selectedPiece = board.getPieceAt(x, y);
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
         return trySelect(x, y);
      }
      else if (selectedPiece.equals(board.getPieceAt(x, y)))
      {  //if clicked piece is selected, unselect the piece
         selectedPiece = null;
         return true;
      }
      // if piece is selected and space clicked on is empty, try to 
      // move to that space
      else if (board.spaceIsEmpty(x, y))
      {
         if (selectedPiece instanceof King) // try to castle
         {
            if (tryCastling(move))
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
         if (board.capturePiece(selectedPiece, x, y))
         {
            moveSuccessful = true;
            move.captures = true;
         }
      }
      if (moveSuccessful)
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
      if (board.getPieceAt(x, y) != null
            && board.getPieceAt(x, y).getColor() == playerToMove)
      {
         selectedPiece = board.getPieceAt(x, y);
         return true;
      }
      return false;
   }

   private boolean doSetUp(int a, int b)
   {
      if (pieceToAdd != null)
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
      if (tryToPromote())
      {
         move.promotes = true;
      }
      if (board.checkForCheck(playerToMove.opposite()))
      {
         move.givesCheck = true;
      }
      if (board.checkForMate(playerToMove.opposite()))
      {
         move.givesMate = true;
      }
      selectedPiece = null;
      playerToMove = playerToMove.opposite();
      deepBlue = new AI(board, playerToMove);
      moveList.add(move);
   }
   
   /**
   Loads a board position from the given file. Returns true if the board is
   able to be loaded successfully, false otherwise
   
   @param filename name of file to read from
   @return whether board position was loaded successfully
   */
   public static boolean loadPositionFromFile(String filename, ChessBoard cb)
   {
      FileInputStream file;
      byte [] buf;
      try {
         file = new FileInputStream(filename);
         buf = new byte[1024];
         file.read(buf, 0, 1024);
      }
      catch(FileNotFoundException fnfe)
      {
         System.out.println("File not found!");
         return false;
      }
      catch(IOException ioe)
      {
         System.out.println("File reading failed");
         return false;
      }
      
      String posString = new String(buf);
      posString = posString.replace("\r","");
      posString = posString.replace("\n", "");
      posString = posString.replace(" ", "");
      posString = posString.trim();
      if(posString.length() != 64 * 2)
         return false;
      
      for(int row = 0; row < 8; row++)
      {
         for(int col = 0; col < 8; col++)
         {
            String nextPiece = posString.substring(0, 2);
            if(!nextPiece.equals("--"))
            {
               ChessPiece cp = LoadPiece(nextPiece);
               cp.xCoord = col;
               cp.yCoord = row;
               cb.setPieceAt(cp, col, row);
            }
            else
            {
               cb.setPieceAt(null, col, row);
            }
            posString = posString.substring(2);
         }
      }
      return true;
   }
   
   /**
   Creates a piece based on the given string input.
   
   @param loadString string that specifies what piece to create
   @return new piece created based on loadString
   */
   private static ChessPiece LoadPiece(String loadString)
   {
      ChessPiece.Color color;
      if(loadString.charAt(0) == 'B')
      {
         color = ChessPiece.Color.BLACK;
      }
      else
      {
         color = ChessPiece.Color.WHITE;
      }
      switch(loadString.charAt(1))
      {
         case 'R': return new Rook(color);
         case 'N': return new Knight(color);
         case 'B': return new Bishop(color);
         case 'K': return new King(color);
         case 'Q': return new Queen(color);
         default: return new Pawn(color);
      }
   }
   
   /**
   Saves the given chess board's position to a file.
   
   @param cb board whose position should be saved
   @param filename name of file to store position in
   @return whether the position was saved successfully
   */
   public static boolean savePositionToFile(ChessBoard cb, String filename)
   {
      FileOutputStream file;
      byte buffer[] = cb.toString().getBytes();
      try
      {
         if(!filename.endsWith(".txt"))
            filename += ".txt";
         file = new FileOutputStream(filename);
         file.write(buffer);
      }
      catch(FileNotFoundException fnfe)
      {
         return false;
      }
      catch(IOException ioe)
      {
         return false;
      }
      return true;
   }
}
