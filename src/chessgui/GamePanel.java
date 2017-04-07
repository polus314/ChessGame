package chessgui;

/**
 This panel is responsible for drawing the chess game on the JFrame.

 @author John Polus
 */
import chessgame.ChessBoard;
import chessgame.ChessMove;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import chessgame.GameController;
import chessgame.GameMode;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import chessgame.ChessPiece;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class GamePanel extends JPanel
{

   public Checkerboard myBoard;
   private GameController controller;
   public ChessPiece.Color humanPlayer;
   /**
    Default constructor, initializes the Checkerboard
    */
   public GamePanel()
   {
      myBoard = new Checkerboard();
      setBorder(BorderFactory.createLineBorder(Color.black));
      setMinimumSize(new Dimension(425, 425));
      controller = new GameController();
      myBoard.setPieces(controller.getPiecesList());
      
      initMouseListener();
   }

   private void initMouseListener()
   {
      addMouseListener(new MouseAdapter()
      {
         @Override
         public void mousePressed(MouseEvent e)
         {
            int x = e.getX();
            int y = e.getY();
            if (x < Checkerboard.BOARD_WIDTH && y < Checkerboard.BOARD_HEIGHT)
            {
               int a = x / Checkerboard.SQUARE_WIDTH;
               int b = y / Checkerboard.SQUARE_HEIGHT;
               if (!controller.takeAction(a, b))
               {
                  return;
               }

               updateGamePanel();
               if (controller.getGameMode() == GameMode.SINGLE
                     && controller.getPlayerToMove() != humanPlayer
                     && !controller.isGameOver())
               {
                  if (controller.doCPUTurn())
                  {
                     //refresh();
                  }
               }
               if (controller.isGameOver())
               {
                  //displayEndGameMessage();
                  System.out.println("Done with game");
                  //changeMode(GameMode.UNDECIDED);
                  //refresh();
               }
            }
         }
      });  
            
   }
   
   private void updateGamePanel()
   {
      myBoard.setSelectedPiece(controller.getSelectedPiece());
      myBoard.setPieces(controller.getPiecesList());
      repaint();
   }
   
   public ArrayList<ChessPiece> getPiecesList()
   {
      return controller.getPiecesList();
   }
   
   public GameMode getGameMode()
   {
      return controller.getGameMode();
   }
   
   public void setPieceToAdd(ChessPiece cp)
   {
      controller.setPieceToAdd(cp);
   }
   
   public void solveForMate()
   {
      boolean validNumber = false;
      int x = 1;
      while(!validNumber)
      {
         String moves = JOptionPane.showInputDialog("How many moves?");
         try
         {
            x = Integer.parseInt(moves);
            validNumber = true;
         }
         catch(NumberFormatException e)
         {
         }
      }
      // use x in AI to solve for mate
      System.out.println("Moves: " + x);
      controller.setBoardPosition(myBoard.getPiecesList(), humanPlayer);
      ArrayList<ChessMove> moveList = 
            controller.solveForMate(humanPlayer, x, x > 1);
      System.out.println("MoveList: " + moveList);
   }
   
   public boolean loadFromFile(String pathname)
   {
      ChessBoard cb = new ChessBoard(controller.getPiecesList());
      if(GameController.loadPositionFromFile(pathname, cb))
      {
         myBoard.setPieces(cb.getPiecesList());
         return true;
      }
      else
      {
         return false;
      }
   }
   
   public boolean startGameWithDisplayedPieces()
   {
      return controller.setBoardPosition(myBoard.getPiecesList(), humanPlayer);
   }
   
   public void setUpNewGame()
   {
      ArrayList<ChessPiece> standardPosition = new ChessBoard().getPiecesList();
      controller.setBoardPosition(standardPosition, ChessPiece.Color.WHITE);
      myBoard.setPieces(standardPosition);
   }
   
   public void setGameMode(GameMode mode)
   {
      controller.setGameMode(mode);
   }
   
   public ChessPiece.Color getWinningSide()
   {
      return controller.getWinningSide();
   }
   
   @Override
   public Dimension getPreferredSize()
   {
      return new Dimension(425, 425);
   }

   /**
    This method is used for painting the panel, calls super as well as the
    paintBoard() method from Checkerboard class, which does most of the actual
    work

    @param g
    */
   @Override
   public void paintComponent(Graphics g)
   {
      super.paintComponent(g);

      myBoard.paintBoard(g);
   }

   // KEPT FOR REFERENCE FOR WHEN I IMPLEMENT THIS SOMEWHERE ELSE ON THE FRAME
//
//   /**
//   This method prints out the moves that are played. Only 20 moves are able to
//   be displayed at a time, so the 20 most recent are shown
//   
//   @param g - graphics object moveList will be drawn on 
//   */
//   public void printMoveList(Graphics g)
//   {
//      if(moveList == null)
//         return;
//      Font myFont = new Font("Arial", Font.PLAIN, 15);
//      g.setFont(myFont);
//      g.setColor(Color.BLACK);
//      g.drawString("Move List", 425, 15);
//      int start = 0;
//
//      if (moveList.size() > 40)
//         start = moveList.size() - 40;
//      for (int i = start; i < moveList.size(); i++)
//      {
//         if(i % 2 == 0)
//         {
//            g.drawString(((i / 2) + 1) + ": " + 
//                  moveList.get(i).toString(), 400, (10 * (i - start) + 40));
//         }
//         else
//         {
//            g.drawString(moveList.get(i).toString(), 500, (10 * (i - start) + 30));
//         }
//      }
//   }
}
