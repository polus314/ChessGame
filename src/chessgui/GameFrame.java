package chessgui;

import chessgame.*;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 Main JFrame for the project. Handles transferring user input between the
 various menus and the GameController as well as adding or removing menus so
 that the user can choose piece color, game mode and so on.

 @author John Polus
 */
public class GameFrame extends JFrame
{
   private static final int FRAME_HEIGHT = 500;
   private static final int FRAME_WIDTH = 500;

   public static final String WINDOW_TITLE = "John Polus's Chess Game";

   public static final String KEEP_BOARD_TITLE = "Keep current board setup?";
   public static final String KEEP_BOARD_MSG = "Do you want to start playing "
         + "with the shown board setup or start a new game?";
   public static final String ILLEGAL_BOARD_POS = "Board configuration illegal";

   public static final String GAME_OVER_TITLE = "Game Over!";
   public static final String GAME_WON_MSG = "You have won!";
   public static final String GAME_TIED_MSG = "It's a tie";
   public static final String GAME_LOST_MSG = "You have lost!";
   
   private class MyMouseAdapter extends MouseAdapter
   {
      /**
      Handles the mousePressed event for the component to which this adapter
      is attached. If the mouse is pressed on the checkerboard, then the 
      appropriate methods are called on the game controller.
      
      @param e mouse event that was passed to this component
      */
      @Override
      public void mousePressed(MouseEvent e)
      {
         e = SwingUtilities.convertMouseEvent(null, e, gamePanel);
         int x = e.getX();
         int y = e.getY() - gamePanel.myBoard.getY();
         if (x < Checkerboard.BOARD_WIDTH && y < Checkerboard.BOARD_HEIGHT)
         {
            int a = x / Checkerboard.SQUARE_WIDTH;
            int b = y / Checkerboard.SQUARE_HEIGHT;
            doGamePlay(e, a,b);
            repaint();
         }
      }
   }
   
   private void initMouseListener()
   {
      MouseAdapter m = new MyMouseAdapter();
      addMouseListener(m);
      addMouseWheelListener(m);
   }
   
   private GamePanel gamePanel;
   private JMenuBar menuBar; 
   
   public GameController controller;
   public ChessPiece.Color humanPlayer;

   private ChessPiece pieceToAdd; // should never be null, default to WPawn

   public static void main(String[] args)
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         @Override
         public void run()
         {
            createAndShowGUI();
         }

      });
   }

   public static void createAndShowGUI()
   {
      GameFrame gf = new GameFrame();
      gf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      gf.setVisible(true);

   }

   public GameFrame()
   {
      super(WINDOW_TITLE);
      initComponents();
      
      controller = new GameController();
      humanPlayer = ChessPiece.Color.WHITE;
      
      setSize(FRAME_WIDTH, FRAME_HEIGHT);
   }
   
   private void initComponents()
   {
      gamePanel = new GamePanel();

      add(gamePanel, BorderLayout.NORTH);
      setJMenuBar(menuBar);
      
      initMouseListener();
   }
   
   private void updateGamePanel()
   {
      gamePanel.myBoard.setSelectedPiece(controller.getSelectedPiece());
      gamePanel.myBoard.setPieces(controller.getPiecesList());
      repaint();
   }
   
   private void doGamePlay(MouseEvent e, int a, int b)
   {
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
            refresh();
         }
      }
      if (controller.isGameOver())
      {
         displayEndGameMessage();
         System.out.println("Done with game");
         refresh();
      }
   }

   private void displayEndGameMessage()
   {
      ChessPiece.Color winner = controller.getWinningSide();
      String msg;
      if (winner == humanPlayer)
      {
         msg = GAME_WON_MSG;
      }
      else if (winner == null)
      {
         msg = GAME_TIED_MSG;
      }
      else
      {
         msg = GAME_LOST_MSG;
      }
      JOptionPane.showMessageDialog(this, msg, GAME_OVER_TITLE, JOptionPane.PLAIN_MESSAGE);
   }

   private void refresh()
   {
      validate();
      int resize = getHeight();
      if (resize % 2 == 0)
      {
         resize++;
      }
      else
      {
         resize--;
      }
      setSize(750, resize);
      repaint();
   }
   
   private void solveForMate()
   {
      boolean validNumber = false;
      int x = 1;
      while(!validNumber)
      {
         String moves = JOptionPane.showInputDialog("How many moves?");
         if(moves == null) // user has chosen to cancel inputting a number
            return;
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
      controller.setBoardPosition(gamePanel.myBoard.getPiecesList(), humanPlayer);
      ArrayList<ChessMove> moveList = 
            controller.solveForMate(humanPlayer, x, x > 1);
      System.out.println("MoveList: " + moveList);
   }

   
   /**
   Tries to start a game with the displayed pieces, fails if the pieces do not
   constitute a legal position
   
   @return true if displayed position is legal, false otherwise
   */
   private boolean startGameWithDisplayedPieces()
   {
      return controller.setBoardPosition(gamePanel.myBoard.getPiecesList(), humanPlayer);
   }
   
   /**
   Sets up the standard start-of-game board position on the checkerboard
   */
   private void setUpNewGame()
   {
      ArrayList<ChessPiece> standardPosition = new ChessBoard().getPiecesList();
      controller.setBoardPosition(standardPosition, ChessPiece.Color.WHITE);
      gamePanel.myBoard.setPieces(standardPosition);
   }
}
