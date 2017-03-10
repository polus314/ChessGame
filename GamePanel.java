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
import java.util.logging.Level;
import java.util.logging.Logger;

public class GamePanel extends JPanel
{
   public Checkerboard myBoard;
   public ChessMove[] moveList;
   public AI deepBlue;
   public PieceColor humanPlayer;
   public GameMode mode;
   public ChessPiece pieceToAdd;
   
///added next line
   private boolean moveFinished = false;

   /**
    This creates the game panel. The game panel receives mouse events and 
   processes them appropriately.
    */
   public GamePanel()
   {
      moveList = new ChessMove[10];
      humanPlayer = PieceColor.EMPTY;
      myBoard = new Checkerboard();
      deepBlue = new AI(myBoard.gameBoard);
      mode = GameMode.UNDECIDED;
      pieceToAdd = new ChessPiece();
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
                     && myBoard.gameBoard.playerToMove == humanPlayer) {
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
      if (myBoard.gameBoard.gameOver)
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
      myBoard.gameBoard.pieceArray[x][y].toggleSelected();
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
//      if (!myBoard.gameBoard.find(new King(PieceColor.WHITE)).equals(new ChessPiece()))
      if(deepBlue.checkGameOver() == humanPlayer)
      {
         g.drawString("YOU", 100, 500);
         g.drawString("WIN", 350, 500);
      }
      else if(deepBlue.checkGameOver() == humanPlayer.opposite())
      {
         g.drawString("YOU", 50, 500);
         g.drawString("STINK", 50, 500);
      }
      else if(deepBlue.checkGameOver() == PieceColor.EMPTY)
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
      if(move.equals(new ChessMove()))
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
         myBoard.gameBoard.pieceArray[move.piece.getX()][move.piece.getY()].select();
         myBoard.gameBoard.movePiece(move.getXDest(), move.getYDest());
      }
      else if (move.getMoveType() == MoveType.CAPTURE)
      {
         myBoard.gameBoard.pieceArray[move.piece.getX()][move.piece.getY()].select();
         myBoard.gameBoard.capturePiece(move.getXDest(), move.getYDest());
      }
      else
      {
         System.out.println("The CPU Turn is messed up!");
      }
      //promote pawns as necessary
      if (!myBoard.gameBoard.needPromotion().equals(new ChessPiece()))
      {
         queen = myBoard.gameBoard.needPromotion();
         myBoard.gameBoard.pieceArray[queen.getX()][queen.getY()]
               = new Queen(queen.getColor(), queen.getX(), queen.getY());
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
      if (deepBlue.checkGameOver() != null)
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
      a = b = 0;
      for (int i = 0; i < 8; i++)
      {
         for (int j = 0; j < 8; j++)
         {
            if (x > (i * 50) && x < (i * 50 + 50) && y > (j * 50) && y < (j * 50 + 50))
            {
               a = i;
               b = j;
            }
         }
      }
      //need to declare a piece for possible promotion
      ChessPiece queen;
      //declare a possible move
      ChessMove move = new ChessMove(myBoard.gameBoard.findSelected(), a, b);
      //if no piece is selected, select the piece clicked on
      if (myBoard.gameBoard.findSelected().equals(new ChessPiece()))
      {
         if(myBoard.gameBoard.pieceArray[a][b].getColor() == myBoard.gameBoard.playerToMove)
            selectPiece(a, b);
      }
      //if clicked piece is selected, unselect the piece
      else if (myBoard.gameBoard.findSelected().equals(myBoard.gameBoard.pieceArray[a][b]))
      {
         myBoard.gameBoard.pieceArray[a][b].unselect();
      }
      //if piece is selected and space clicked on is empty, try to 
      //move to that space, will only work if piece moves that direction
      //and the path is clear
      else if (myBoard.gameBoard.spaceIsEmpty(a, b))
      {
         if(move.piece.type == PieceType.KING)
         {
            if(myBoard.gameBoard.canCastleKS(humanPlayer)
                  || myBoard.gameBoard.canCastleQS(humanPlayer))
            {
               if( myBoard.gameBoard.castle(move))
               {
                  updateMoveList(move);
                  if (deepBlue.checkGameOver() == null && mode == GameMode.SINGLE)
                  {
                     myBoard.gameBoard.playerToMove = myBoard.gameBoard.playerToMove.opposite();
                     moveFinished = true;
                  }
               }
            }
         }
         if (myBoard.gameBoard.movePiece(a, b))
         {
            //if a pawn is on the last rank, places a queen there
            if (!myBoard.gameBoard.needPromotion().equals(new ChessPiece()))
            {
               queen = myBoard.gameBoard.needPromotion();
               myBoard.gameBoard.pieceArray[queen.getX()][queen.getY()]
                     = new Queen(queen.getColor(), queen.getX(), queen.getY());
               move.setMoveType(MoveType.PROMOTION);
            }
            if(myBoard.gameBoard.checkForCheck(humanPlayer.opposite()))
               move.givesCheck = true;
            //end game if a king is missing, set CPU to move so no
            //more mouse events are processed on the checker board
            if (deepBlue.checkGameOver() == null && mode == GameMode.SINGLE)
            {
               updateMoveList(move);
               myBoard.gameBoard.playerToMove = myBoard.gameBoard.playerToMove.opposite();       
               moveFinished = true;
            }
            else if(deepBlue.checkGameOver() != null)
            {
               move.givesMate = true;
               updateMoveList(move);
               repaint();
            }
         }
      }
      else
      {
         //capture the piece that was clicked on, which might fail
         if(myBoard.gameBoard.capturePiece(a, b))
         {
            move.setMoveType(MoveType.CAPTURE);
            if(myBoard.gameBoard.checkForCheck(humanPlayer.opposite()))
               move.givesCheck = true;
            if (!myBoard.gameBoard.needPromotion().equals(new ChessPiece()))
            {
               queen = myBoard.gameBoard.needPromotion();
               myBoard.gameBoard.pieceArray[queen.getX()][queen.getY()]
                     = new Queen(queen.getColor(), queen.getX(), queen.getY());
               move.setMoveType(MoveType.PROMOTION);
            }
            if (deepBlue.checkGameOver() == null)
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
      
      a = b = 0;
      for (int i = 0; i < 8; i++)
      {
         for (int j = 0; j < 8; j++)
         {
            if (x > (i * 50) && x < (i * 50 + 50) && y > (j * 50) && y < (j * 50 + 50))
            {
               a = i;
               b = j;
            }
         }
      }
      
      ChessPiece selPiece = board.findSelected();
      if(pieceToAdd.equals(new ChessPiece()))
      {
         board.pieceArray[a][b] = new ChessPiece();
      }
      if(selPiece.equals(new ChessPiece())
            && !board.pieceArray[a][b].equals(new ChessPiece()))
      {
         board.pieceArray[a][b].select();
      }
      else if(selPiece.equals(new ChessPiece()))
      {
         board.pieceArray[a][b] = addPiece(a,b);
      }
      else if(selPiece.equals(board.pieceArray[a][b]))
      {
         board.pieceArray[a][b].unselect();
      }
      else
      {
         movePiece(selPiece.getX(), selPiece.getY(), a, b);
      }
      repaint();
   }
   
   public ChessPiece addPiece(int x, int y)
   {
      if(pieceToAdd == null)
         return new ChessPiece();
      PieceColor color = pieceToAdd.getColor();
      switch(pieceToAdd.type)
      {
         case QUEEN: 
            return new Queen(color, x, y);
         case KING:
            return new King(color, x, y);
         case ROOK:
            return new Rook(color, x, y);
         case BISHOP:
            return new Bishop(color, x, y);
         case KNIGHT:
            return new Knight(color, x, y);
         case PAWN:
            return new Pawn(color, x, y);
         default:
            return new ChessPiece();
                 
      }
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
