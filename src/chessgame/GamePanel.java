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
import java.util.ArrayList;

public class GamePanel extends JPanel
{
   // TODO - improve abstraction
   public Checkerboard myBoard;
   public ArrayList<ChessMove> moveList;
   //public AI deepBlue;
   public PieceColor humanPlayer;
   public GameMode mode;
   public ChessPiece pieceToAdd;
   public boolean moveFinished = false;
   private GameController controller;

   /**
    This creates the game panel. The game panel receives mouse events and 
   processes them appropriately.
    */
   public GamePanel()
   {
      controller = new GameController();
      moveList = new ArrayList<>();
      humanPlayer = PieceColor.WHITE;
      myBoard = new Checkerboard();
      myBoard.setPieces(controller.board.getPiecesList());
      //deepBlue = new AI(controller.board);      
      mode = GameMode.UNDECIDED;
      setBorder(BorderFactory.createLineBorder(Color.black));
      setMinimumSize(new Dimension(425, 425));
      
      addMouseListener(new MouseAdapter()
      {
         public void mousePressed(MouseEvent e)
         {
            int x = e.getX();
            int y = e.getY();
            if (x < Checkerboard.BOARD_WIDTH && y < Checkerboard.BOARD_HEIGHT)
            {
               int a = x / Checkerboard.SQUARE_WIDTH;
               int b = y / Checkerboard.SQUARE_HEIGHT;
               boolean success = controller.takeAction(a,b);
//               if(!success)
//                  return;

               myBoard.setSelectedPiece(controller.getSelectedPiece());
               repaint();
            }
         }
      });
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
      if (controller.isGameOver())
      {
         printEndMessage(g);
      }
   }

   /**
   This method prints out the moves that are played. Only 20 moves are able to
   be displayed at a time, so the 20 most recent are shown
   
   @param g - graphics object moveList will be drawn on 
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
      
      
      if (moveList.size() > 40)
         start = moveList.size() - 40;
      for (int i = start; i < moveList.size(); i++)
      {
         if(i % 2 == 0)
         {
            g.drawString(((i / 2) + 1) + ": " + 
                  moveList.get(i).toString(), 400, (10 * (i - start) + 40));
         }
         else
         {
            g.drawString(moveList.get(i).toString(), 500, (10 * (i - start) + 30));
         }
      }
   }

   /**
   This method prints an end-of-game message
   @param g 
   */
   private void printEndMessage(Graphics g)
   {
      Font myFont = new Font("Arial", Font.BOLD, 100);
      g.setFont(myFont);
      g.setColor(Color.blue);
      if(controller.getWinningSide() == humanPlayer)
      {
         g.drawString("YOU", 100, 500);
         g.drawString("WIN", 350, 500);
      }
      else if(controller.getWinningSide() == humanPlayer.opposite())
      {
         g.drawString("YOU", 50, 500);
         g.drawString("STINK", 50, 500);
      }
      else if(controller.getWinningSide() == null)
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
   private void movePiece(int xi, int yi, int xf, int yf)
   {
      controller.board.replacePiece(xi, yi, xf, yf);
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
