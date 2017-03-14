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
   public PieceColor humanPlayer;
   public GameMode mode;
   public ChessPiece pieceToAdd;
   public GameController controller;

   /**
    This creates the game panel. The game panel receives mouse events and 
   processes them appropriately.
    */
   public GamePanel()
   {
      controller = new GameController();
      humanPlayer = PieceColor.WHITE;
      myBoard = new Checkerboard();
      myBoard.setPieces(controller.getPiecesList());  
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
               if(!success)
                  return;

               myBoard.setSelectedPiece(controller.getSelectedPiece());
               myBoard.setPieces(controller.getPiecesList());
               repaint();
               if(mode == GameMode.SINGLE && 
                  controller.getPlayerToMove() != humanPlayer &&
                  !controller.isGameOver())
               {
                  if(controller.doCPUTurn())
                  {
                     myBoard.setSelectedPiece(controller.getSelectedPiece());
                     myBoard.setPieces(controller.getPiecesList());
                     repaint();
                  }
               }
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
      if(!(mode == GameMode.SINGLE || mode == GameMode.VERSUS))
      {
         return;
      }
      Font myFont = new Font("Arial", Font.PLAIN, 15);
      g.setFont(myFont);
      g.setColor(Color.BLACK);
      g.drawString("Move List", 425, 15);
      int start = 0;
      
      ArrayList<ChessMove> moveList = controller.getMoveList();
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
      if(!controller.isGameOver())
         return;
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
         g.drawString("LOSE", 300, 500);
      }
      else if(controller.getWinningSide() == null)
      {
         g.drawString("STALEMATE", 0, 285);
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
