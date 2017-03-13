package chessgame;

/**

 @author: jppolecat
 */

import javax.swing.JPanel;
import javax.swing.BorderFactory;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

public class GamePanel extends JPanel
{
   // TODO - improve abstraction
   public Checkerboard myBoard;
   public ChessMove[] moveList;
   public AI deepBlue;
   public PieceColor humanPlayer;
   public GameMode mode;
   public ChessPiece pieceToAdd;
   public boolean moveFinished = false;

   /**
    This creates the game panel. The game panel receives mouse events and 
   processes them appropriately.
    */
   public GamePanel()
   {
      moveList = new ChessMove[10];
      humanPlayer = PieceColor.WHITE;
      myBoard = new Checkerboard();
      deepBlue = new AI(myBoard.gameBoard);      
      mode = GameMode.UNDECIDED;
      setBorder(BorderFactory.createLineBorder(Color.black));
      
      addMouseListener(new MouseAdapter()
      {
         public void mousePressed(MouseEvent e)
         {
            int x = e.getX();
            int y = e.getY();
            if (x < 400 && y < 400)
            {
               if(mode == GameMode.SINGLE
                     && myBoard.gameBoard.getPlayerToMove() == humanPlayer) {
                     doHumanTurn(e);
                     if(moveFinished) {
                        doCPUTurn();
                        moveFinished = false;
                     }
               }       
               if(mode == GameMode.VERSUS)
                  doHumanTurn(e);
               if(mode == GameMode.SET_UP)
                  doSetUp(e);
            }
         }
      });
      setMinimumSize(new Dimension(425, 425));
   }

   //Used for when single player plays as black, AI must move first.
   public void flipFirstTurn()
   {
       doCPUTurn();
       moveFinished = false;
   }
   
   public Dimension getPreferredSize()
   {
      return new Dimension(250, 200);
   }
   
   /**
   This method is used for painting the panel, calls super as well as the
   paintBoard() method from Checkerboard class, which does most of the actual
   work
   @param g 
   */
   public void paintComponent(Graphics g)
   {
      super.paintComponent(g);
      
      myBoard.paintBoard(g);
      printMoveList(g);
      if (myBoard.gameBoard.isGameOver())
      {
         printEndMessage(g);
      }
   }

   /**
   This method selects a piece at the given coordinates
   @param x
   @param y 
   */
   public void selectPiece(int x, int y)
   {
      myBoard.selectedPiece = myBoard.gameBoard.getPieceAt(x,y);
   }

   /**
   This method prints out the moves that are played. Only 20 moves are able to
   be displayed at a time, so the 20 most recent are shown
   @param g 
   */
   public void printMoveList(Graphics g)
   {
      Font myFont = new Font("Arial", Font.PLAIN, 15);
      g.setFont(myFont);
      g.setColor(Color.BLACK);
      if(mode == GameMode.SINGLE || mode == GameMode.VERSUS)
      {
         g.drawString("Move List", 425, 15);
      }
      int start = 0;
      if (length(moveList) > 40)
         start = length(moveList) - 40;
      for (int i = start; i < length(moveList); i++)
      {
         if(i % 2 == 0)
         {
            g.drawString(((i / 2) + 1) + ": " + 
                  moveList[i].toString(), 400, (10 * (i - start) + 40));
         }
         else
         {
            g.drawString(moveList[i].toString(), 500, (10 * (i - start) + 30));
         }
      }
   }
   
   /**
   This method adds the given move to the GUI list of moves that have been
   played.
   
   @param cm 
   */
   public void updateMoveList(ChessMove cm)
   {
      if(length(moveList) == moveList.length)
         moveList = grow(moveList);
      moveList[length(moveList)] = cm;
   }
   
   /**
    This method finds how many objects are in a list. Assumes a contiguous
    list that starts at index 0

    @param list
    @return
    */
   public int length(Object[] list)
   {
      int count = 0;
      if (list.length == 0)
      {
         return 0;
      }
      while (count < list.length && list[count] != null)
      {
         count++;
      }
      return count;
   }
   
   public ChessMove [] grow(ChessMove [] moveList)
   {
      int newLength = moveList.length + 20;
      ChessMove [] newList = new ChessMove [newLength];
      for (int i = 0; i < moveList.length; i++)
         newList[i] = moveList[i];
      return newList;
   }

   /**
   This method prints an end-of-game message
   @param g 
   */
   public void printEndMessage(Graphics g)
   {
      Font myFont = new Font("Arial", Font.BOLD, 100);
      g.setFont(myFont);
      g.setColor(Color.blue);
      if(deepBlue.getWinningSide() == humanPlayer)
      {
         g.drawString("YOU", 100, 500);
         g.drawString("WIN", 350, 500);
      }
      else if(deepBlue.getWinningSide() == humanPlayer.opposite())
      {
         g.drawString("YOU", 50, 500);
         g.drawString("STINK", 50, 500);
      }
      else if(deepBlue.getWinningSide() == null)
      {
         g.drawString("STALEMATE", 0, 285);
      }
   }
   
   /**
   This method should NOT be used for actual gameplay, because it doesn't 
   check to see if move is legal, etc. Should be used for setting up a position
   
   @param xi
   @param yi
   @param xf
   @param yf 
   */
   public void movePiece(int xi, int yi, int xf, int yf)
   {
      myBoard.gameBoard.replacePiece(xi, yi, xf, yf);
   }

   /**
   This method does most of the things done in the main GUI method, but for
   the computer player
   */
   public void doCPUTurn()
   {
      //create an instance of the AI, used for finding the best move
      deepBlue = new AI(myBoard.gameBoard);
      if (myBoard.gameBoard.checkForCheck(humanPlayer.opposite()))
         System.out.println("Human: Check! You dumb computer!");
      ChessPiece queen;
      System.out.println("It is black's turn");
      ChessMove move = deepBlue.findBestMove(humanPlayer.opposite());
      //checking for checkmate
      if(move == null)
      {
         myBoard.gameBoard.gameOver = true;
         moveList[length(moveList) - 1].givesMate = true;
         return;
      }
      System.out.println("The move is " + move.toString() + "\nRating:"
         + move.getHangRating() + ", " + move.getMobilityRating() + "\n");
      //check for "type" of best move, if unoccupied simply move, else try to 
      //capture
      if (move.getMoveType() == MoveType.UNOCCUPIED)
      {
         ChessPiece cp = myBoard.gameBoard.getPieceAt(move.piece.getX(), move.piece.getY());
         myBoard.gameBoard.movePiece(cp, move.getXDest(), move.getYDest());
      }
      else if (move.getMoveType() == MoveType.CAPTURE)
      {
         myBoard.selectedPiece = myBoard.gameBoard.getPieceAt(move.piece.getX(), move.piece.getY());
         myBoard.gameBoard.capturePiece(myBoard.selectedPiece, move.getXDest(), move.getYDest());
      }
      else
      {
         System.out.println("The CPU Turn is messed up!");
      }
      queen = myBoard.gameBoard.needPromotion();
      //promote pawns as necessary
      if (queen != null)
      {
         int x = queen.getX(), y = queen.getY();
         queen = new Queen(queen.getColor(), x, y);
         myBoard.gameBoard.setPieceAt(queen, x, y);
         move.setMoveType(MoveType.PROMOTION);
      }
      //introduces concept of check, not implemented logically yet for human
      if (myBoard.gameBoard.checkForCheck(humanPlayer))
      {
         System.out.println("CPU: Check puny human!");
         move.givesCheck = true;
      }
      //GUI list of moves is updated
      updateMoveList(move);
      deepBlue = new AI(myBoard.gameBoard);
      //unless the game is over
      if (deepBlue.isGameOver())
         return;
      //allow human to make next move
      myBoard.gameBoard.setPlayerToMove(humanPlayer);

   }
   
   public void doHumanTurn(MouseEvent e)
   {
      //turn exact x and y values into checkerboard coordinates a and b
      int a, b, x, y;
      x = e.getX();
      y = e.getY();
      a = x / 50;
      b = y / 50;
      
      //need to declare a piece for possible promotion
      ChessPiece queen;
      //declare a possible move
      ChessMove move = new ChessMove(myBoard.selectedPiece, a, b);
      //if no piece is selected, select the piece clicked on
      if (myBoard.selectedPiece == null)
      {
         if(myBoard.gameBoard.getPieceAt(a,b).getColor() == myBoard.gameBoard.getPlayerToMove())
            selectPiece(a, b);
      }
      //if clicked piece is selected, unselect the piece
      else if (myBoard.selectedPiece.equals(myBoard.gameBoard.getPieceAt(a,b)))
      {
         myBoard.selectedPiece = null;
      }
      //if piece is selected and space clicked on is empty, try to 
      //move to that space, will only work if piece moves that direction
      //and the path is clear
      else if (myBoard.gameBoard.spaceIsEmpty(a, b))
      {
         if(move.piece instanceof King)
         {
            if(myBoard.gameBoard.canCastleKS(humanPlayer)
                  || myBoard.gameBoard.canCastleQS(humanPlayer))
            {
               if( myBoard.gameBoard.castle(move))
               {
                  updateMoveList(move);
                  if (!deepBlue.isGameOver() && mode == GameMode.SINGLE)
                  {
                     myBoard.gameBoard.setPlayerToMove(myBoard.gameBoard.getPlayerToMove().opposite());
                     myBoard.selectedPiece = null;
                     moveFinished = true;
                  }
               }
            }
         }
         ChessPiece selected = myBoard.selectedPiece;
         if (myBoard.gameBoard.movePiece(selected, a, b))
         {
            //if a pawn is on the last rank, places a queen there
            queen = myBoard.gameBoard.needPromotion();
            if (queen != null)
            {
               int xCoord = queen.getX(), yCoord = queen.getY();
               queen = new Queen(queen.getColor(), xCoord, yCoord);
               myBoard.gameBoard.setPieceAt(queen, xCoord, yCoord);
               move.setMoveType(MoveType.PROMOTION);
            }
            if(myBoard.gameBoard.checkForCheck(humanPlayer.opposite()))
               move.givesCheck = true;
            //end game if a king is missing, set CPU to move so no
            //more mouse events are processed on the checker board
            if (!deepBlue.isGameOver() && mode == GameMode.SINGLE)
            {
               updateMoveList(move);
               myBoard.gameBoard.setPlayerToMove(myBoard.gameBoard.getPlayerToMove().opposite());       
               moveFinished = true;
            }
            else if(deepBlue.isGameOver())
            {
               move.givesMate = true;
               updateMoveList(move);
               repaint();
            }
            myBoard.selectedPiece = null;
         }
      }
      else
      {
         //capture the piece that was clicked on, which might fail
         if(myBoard.gameBoard.capturePiece(myBoard.selectedPiece, a, b))
         {
            move.setMoveType(MoveType.CAPTURE);
            if(myBoard.gameBoard.checkForCheck(humanPlayer.opposite()))
               move.givesCheck = true;
            queen = myBoard.gameBoard.needPromotion();
            if (myBoard.gameBoard.needPromotion() != null)
            {
               int xCoord = queen.getX(), yCoord = queen.getY();
               queen = new Queen(queen.getColor(), xCoord, yCoord);
               myBoard.gameBoard.setPieceAt(queen, xCoord, yCoord);  
               move.setMoveType(MoveType.PROMOTION);
            }
            if (!deepBlue.isGameOver())
            {
               updateMoveList(move);
               moveFinished = true;
            }
            else
            {
               move.givesMate = true;
               updateMoveList(move);
               repaint();
            }
            myBoard.selectedPiece = null;
         }
      }
      repaint();
   }
   
   public void doSetUp(MouseEvent e)
   {
      ChessBoard board = myBoard.gameBoard;
      int a,b,x,y;
      x = e.getX();
      y = e.getY();
      a = x / 50;
      b = y / 50;
      
      if(pieceToAdd == null)
      {
         board.setPieceAt(null, a, b);
      }
      if(myBoard.selectedPiece == null
            && board.getPieceAt(a,b) != null)
      {
         myBoard.selectedPiece = board.getPieceAt(a,b);
      }
      else if(myBoard.selectedPiece == null)
      {
         board.setPieceAt(addPiece(a,b), a, b);
      }
      else if(myBoard.selectedPiece.equals(board.getPieceAt(a,b)))
      {
         myBoard.selectedPiece = null;
      }
      else
      {
         movePiece(myBoard.selectedPiece.getX(), myBoard.selectedPiece.getY(), a, b);
      }
      repaint();
   }
   
   public ChessPiece addPiece(int x, int y)
   {
      if(pieceToAdd == null)
         return null;
      
      PieceColor color = pieceToAdd.getColor();
      if(pieceToAdd instanceof Queen)
         return new Queen(color, x, y);
      else if(pieceToAdd instanceof King)
         return new King(color, x, y);
      else if(pieceToAdd instanceof Bishop)
         return new Bishop(color, x, y);
      else if(pieceToAdd instanceof Rook)
         return new Rook(color, x, y);
      else if(pieceToAdd instanceof Knight)
         return new Knight(color, x, y);
      else //if(pieceToAdd instanceof Pawn)
         return new Pawn(color, x, y);
   }
   
   public void setMode(GameMode gameMode)
   {
      mode = gameMode;
   }
   
   public void setColor(PieceColor color)
   {
      humanPlayer = color;
   }
}
