package chessgame;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**

 @author jppolecat
 */
public class GameFrame extends JFrame implements ActionListener, PropertyChangeListener
{

   private ModeMenu modeMenu;
   private GamePanel gamePanel;
   private ColorMenu colorMenu;
   private PieceMenu pieceMenu;
   private JButton btn_mainMenu;
   private boolean modeChanged;
   
   private PieceColor humanPlayer;
   private PieceColor colorToAdd;
   private ChessPiece pieceToAdd;
   private GameController controller;
      
   private static final int FRAME_HEIGHT = 650;
   private static final int FRAME_WIDTH = 750;
   private int topBorderHeight = 32;
   private int leftBorderWidth = 8;
   
   public static final String WINDOW_TITLE = "John Polus's Chess Game";
   
   public static final String KEEP_BOARD_TITLE = "Keep current board setup?";
   public static final String KEEP_BOARD_MSG = "Do you want to start playing "
         + "with the shown board setup or start a new game?";
   public static final String ILLEGAL_BOARD_POS = "Board configuration illegal";
   
   public static final String GAME_OVER_TITLE = "Game Over!";
   public static final String GAME_WON_MSG = "You have won!";
   public static final String GAME_TIED_MSG = "It's a tie";
   public static final String GAME_LOST_MSG = "You have lost!";
   
   

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
      modeMenu = new ModeMenu();
      gamePanel = new GamePanel();
      colorMenu = new ColorMenu();
      pieceMenu = new PieceMenu();
      
      DEBUG = true;
      controller = new GameController();
      colorToAdd = PieceColor.WHITE;
      changeMode(GameMode.UNDECIDED);
      gamePanel.myBoard.setPieces(controller.getPiecesList());

      btn_mainMenu = new JButton("Main Menu");
      btn_mainMenu.setMnemonic(KeyEvent.VK_M);
      btn_mainMenu.setActionCommand("Main Menu");
      btn_mainMenu.addActionListener(this);

      pieceMenu.addPropertyChangeListener(this);
      modeMenu.addPropertyChangeListener(this);
      colorMenu.addPropertyChangeListener(this);
      
      setSize(FRAME_WIDTH, FRAME_HEIGHT);

      add(modeMenu, BorderLayout.EAST);
      add(gamePanel, BorderLayout.CENTER);
      add(btn_mainMenu, BorderLayout.WEST);

      addMouseListener(new MouseAdapter()
      {
         @Override
         public void mousePressed(MouseEvent e)
         {
            if(DEBUG)
               printDebugInfo();
            
            int x = e.getX() - (gamePanel.getX() + leftBorderWidth);
            int y = e.getY() - (gamePanel.getY() + topBorderHeight);
            if (x < Checkerboard.BOARD_WIDTH && y < Checkerboard.BOARD_HEIGHT)
            {
               int a = x / Checkerboard.SQUARE_WIDTH;
               int b = y / Checkerboard.SQUARE_HEIGHT;
               if(!controller.takeAction(a,b))
                  return;

               updateGamePanel();
               if(controller.getGameMode() == GameMode.SINGLE && 
                  controller.getPlayerToMove() != humanPlayer &&
                  !controller.isGameOver())
               {
                  if(controller.doCPUTurn())
                  {
                     refresh();
                  }
               }
               if(controller.isGameOver())
               {
                  displayEndGameMessage();
                  System.out.println("Done with game");
                  changeMode(GameMode.UNDECIDED);
                  refresh();
               }
            }
         }
      });
   }
   
   private void displayEndGameMessage()
   {
      PieceColor winner = controller.getWinningSide();
      String msg;
      if(winner == humanPlayer)
      {
         msg = GAME_WON_MSG;
      }
      else if(winner == null)
      {
         msg = GAME_TIED_MSG;
      }
      else
      {
         msg = GAME_LOST_MSG;
      }
      JOptionPane.showMessageDialog(this, msg, GAME_OVER_TITLE, JOptionPane.PLAIN_MESSAGE);
   }
   
   private void updateGamePanel()
   {
      gamePanel.myBoard.setSelectedPiece(controller.getSelectedPiece());
      gamePanel.myBoard.setPieces(controller.getPiecesList());
      repaint();
   }
   
   @Override
   public void propertyChange(PropertyChangeEvent event)
   {
      String property = event.getPropertyName();
      switch(property)
      {
         case "piece" :
            pieceToAdd = determinePieceToAdd();
            controller.setPieceToAdd(pieceToAdd);
            break;
         case "mode" :
            handleModePropertyChange(event);
            break;
         case "color" :
            GameMode mode = modeMenu.getMode();
            if(mode == GameMode.SET_UP)
               colorToAdd = colorMenu.getColor();
            else if(mode == GameMode.SINGLE)
            {
               humanPlayer = colorMenu.getColor();
               remove(colorMenu);
            }
            pieceToAdd = determinePieceToAdd();
            controller.setPieceToAdd(pieceToAdd);
            break;
         default:
            return;
      }
      refresh();
   }
   
   /**
   Handles the logic of how to transition between modes, such as resetting
   values that need to be chosen on each transition into a new state.
   
   @param event - change event that is being handled
   */
   private void handleModePropertyChange(PropertyChangeEvent event)
   {
      GameMode newMode = (GameMode)event.getNewValue();
      boolean shouldChangeMode = true;
      if(newMode == GameMode.SINGLE || newMode == GameMode.VERSUS)
      {
         Object [] options = { "Keep Current Set-Up", "New Game", "Cancel" };
         int choice = JOptionPane.showOptionDialog(this, KEEP_BOARD_MSG, KEEP_BOARD_TITLE, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
         if(choice == 0)
         {
            if(!controller.setBoardPosition(gamePanel.myBoard.getPiecesList()))
            {
               JOptionPane.showMessageDialog(this, ILLEGAL_BOARD_POS);
               shouldChangeMode = false;
            }
         }
         else if(choice == 1)
         {
            ArrayList<ChessPiece> standardPosition = new ChessBoard().getPiecesList();
            controller.setBoardPosition(standardPosition);
            gamePanel.myBoard.setPieces(standardPosition);
         }
         else
         {
            shouldChangeMode = false;
         }
      }
      
      if(shouldChangeMode)
      {
         changeMode(newMode);
         colorMenu.setColor(null);
         humanPlayer = null;
      }
   }
   
   private void changeMode(GameMode newMode)
   {
      modeMenu.setMode(newMode);
      controller.setGameMode(newMode);
      modeChanged = true;
   }
   
   @Override
   public void actionPerformed(ActionEvent event)
   {
      String command = event.getActionCommand();
      switch (command)
      {
         case "Main Menu":
            changeMode(GameMode.UNDECIDED);
            break;
      }
      refresh();
   }

   private void refresh()
   {
      pieceToAdd = determinePieceToAdd();
      controller.setPieceToAdd(pieceToAdd);
      
      if(modeChanged)
         setComponentsForNewMode();
      updateGamePanel();
      
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
   
   private void setComponentsForNewMode()
   {
      remove(modeMenu);
      remove(pieceMenu);
      remove(colorMenu);
      switch(controller.getGameMode())
      {
         case UNDECIDED :
            add(modeMenu, BorderLayout.EAST);
            break;
         case SINGLE :
            if(humanPlayer == null)
               add(colorMenu, BorderLayout.EAST);
            break;
         case VERSUS :
            break;
         case SET_UP :
            add(pieceMenu, BorderLayout.SOUTH);
            add(colorMenu, BorderLayout.EAST);
            break;
      }
      modeChanged = false;
   }

   private ChessPiece determinePieceToAdd()
   {
      if (pieceMenu.getPiece() == null)
      {
         return null;
      }
      ChessPiece piece = pieceMenu.getPiece().copyOfThis();
      piece.setColor(colorToAdd);
      return piece;
   }
   
   ///* DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG
   
   private boolean DEBUG;
   
   private void printDebugInfo()
   {
      System.out.println("\nDEBUG INFO");
      System.out.println("modeChanged: " + modeChanged);
      System.out.println("pieceToAdd: " + pieceToAdd);
      System.out.println("humanPlayer: " + humanPlayer);
      System.out.println("mode(GUI): " + modeMenu.getMode());
      System.out.println("mode(cont): " + controller.getGameMode());
      System.out.println("selectedPiece(GUI): " + gamePanel.myBoard.selectedPiece);
      System.out.println("selectedPiece(cont): " + controller.getSelectedPiece());
      System.out.println("playerToMove: " + controller.getPlayerToMove());
      System.out.println("color from ColorMenu: " + colorMenu.getColor() + "\n");
      
      System.out.println("MoveList:" + controller.getMoveList().toString());
      System.out.println("Board: \n" + new ChessBoard().toString());
   }
   
//*/ // DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG
   
   
}
