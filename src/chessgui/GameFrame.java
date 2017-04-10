package chessgui;

import chessgame.AI;
import chessgame.ChessBoard;
import chessgame.ChessPiece;
import chessgame.GameController;
import chessgame.GameMode;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 Main JFrame for the project. Handles transferring user input between the
 various menus and the GameController as well as adding or removing menus so
 that the user can choose piece color, game mode and so on.

 @author John Polus
 */
public class GameFrame extends JFrame implements ActionListener, PropertyChangeListener
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
   
   private ModeMenu modeMenu;
   private GamePanel gamePanel;
   private ColorMenu colorMenu;
   private PieceMenu pieceMenu;
   private boolean modeChanged;
   private JMenuBar menuBar;

   //private ChessPiece.Color humanPlayer;
   private ChessPiece.Color colorToAdd;
   private ChessPiece pieceToAdd;
   //private GameController controller;

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
      
      colorToAdd = ChessPiece.Color.WHITE;
      changeMode(GameMode.UNDECIDED);
      
      setSize(FRAME_WIDTH, FRAME_HEIGHT);
   }
   
   private void initComponents()
   {
      modeMenu = new ModeMenu();
      gamePanel = new GamePanel();
      colorMenu = new ColorMenu();
      pieceMenu = new PieceMenu();
      
      menuBar = new JMenuBar();
      JMenu fileMenu = new JMenu(" File ");
      menuBar.add(fileMenu);
      
      JMenuItem load = new JMenuItem("Load Position");
      load.setActionCommand("Load");
      load.addActionListener(this);
      fileMenu.add(load);
      
      JMenuItem save = new JMenuItem("Save Position");
      save.setActionCommand("Save");
      save.addActionListener(this);
      fileMenu.add(save);
      
      JMenuItem solve = new JMenuItem("Solve for Mate");
      solve.setActionCommand("Solve");
      solve.addActionListener(this);
      fileMenu.add(solve);
      
      menuBar.add(modeMenu);

      pieceMenu.addPropertyChangeListener(this);
      modeMenu.addPropertyChangeListener(this);
      colorMenu.addPropertyChangeListener(this);

      add(gamePanel, BorderLayout.NORTH);
      setJMenuBar(menuBar);
   }

   private void displayEndGameMessage()
   {
      ChessPiece.Color winner = gamePanel.getWinningSide();
      String msg;
      if (winner == gamePanel.humanPlayer)
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

   @Override
   public void propertyChange(PropertyChangeEvent event)
   {
      String property = event.getPropertyName();
      switch (property)
      {
         case "piece":
            pieceToAdd = determinePieceToAdd();
            gamePanel.setPieceToAdd(pieceToAdd);
            break;
         case "mode":
            handleModePropertyChange(event);
            break;
         case "color":
            GameMode mode = modeMenu.getMode();
            if (mode == GameMode.SET_UP)
            {
               colorToAdd = colorMenu.getColor();
            }
            else if (mode == GameMode.SINGLE)
            {
               gamePanel.humanPlayer = colorMenu.getColor();
               remove(colorMenu);
            }
            pieceToAdd = determinePieceToAdd();
            gamePanel.setPieceToAdd(pieceToAdd);
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
      GameMode newMode = (GameMode) event.getNewValue();
      boolean shouldChangeMode = true;
      if (newMode == GameMode.SINGLE || newMode == GameMode.VERSUS)
      {
         Object[] options =
         {
            "Keep Current Set-Up", "New Game", "Cancel"
         };
         int choice = JOptionPane.showOptionDialog(this, KEEP_BOARD_MSG, KEEP_BOARD_TITLE, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
         if (choice == 0)
         {
            if (!gamePanel.startGameWithDisplayedPieces())
            {
               JOptionPane.showMessageDialog(this, ILLEGAL_BOARD_POS);
               shouldChangeMode = false;
            }
         }
         else if (choice == 1)
         {
            gamePanel.setUpNewGame();
            
         }
         else
         {
            shouldChangeMode = false;
         }
      }

      if (shouldChangeMode)
      {
         changeMode(newMode);
         colorMenu.setColor(null);
      }
   }

   private void changeMode(GameMode newMode)
   {
      modeMenu.setMode(newMode);
      gamePanel.setGameMode(newMode);
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
         case "Save":
            saveToFile();
            break;
         case "Load":
            loadFromFile();
            break;
         case "Solve":
            String settings = JOptionPane.showInputDialog(null, "Choose an algorithm and heuristic");
            setSettings(settings);
            gamePanel.solveForMate();
            break;
      }
      refresh();
   }
   
   private void setSettings(String settings)
   {
      if(settings.charAt(0) == 'f')
      {
         gamePanel.controller.deepBlue.heuristic = AI.Heuristic.FORCING;
      }
      else if (settings.charAt(0) == 'm')
      {
         gamePanel.controller.deepBlue.heuristic = AI.Heuristic.MATERIAL;
      }
      else
      {
         gamePanel.controller.deepBlue.heuristic = AI.Heuristic.UNINFORMED;
      }
      if(settings.charAt(1) == 'b')
      {
         gamePanel.controller.deepBlue.algorithm = AI.Algorithm.BFS;
      }
      else if(settings.charAt(1) == 'd')
      {
         gamePanel.controller.deepBlue.algorithm = AI.Algorithm.DFS;
      }
      else
      {
         gamePanel.controller.deepBlue.algorithm = AI.Algorithm.GREEDY;
      }
   }
   
   private void saveToFile()
   {
      JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
      File selectedFile;
      if(chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) 
      {
         selectedFile = chooser.getSelectedFile();
      }
      else
         return;
      if(GameController.savePositionToFile(new ChessBoard(gamePanel.myBoard.getPiecesList()), selectedFile.getAbsolutePath()))
         System.out.println("Save Successful");
      else
         System.out.println("Save Failed");
   }
   
   private void loadFromFile()
   {
      File selectedFile;
      JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
      if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) 
      {
         selectedFile = chooser.getSelectedFile();
      }
      else
         return;
      if(gamePanel.loadFromFile(selectedFile.getAbsolutePath()))
      {
         System.out.println("Load Successful");
      }
      else
         System.out.println("Load failed");
   }

   private void refresh()
   {
      pieceToAdd = determinePieceToAdd();
      gamePanel.setPieceToAdd(pieceToAdd);

      if (modeChanged)
      {
         setComponentsForNewMode();
      }

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
      switch (gamePanel.getGameMode())
      {
         case UNDECIDED:
            menuBar.add(modeMenu, BorderLayout.EAST);
            break;
         case SINGLE:
            break;
         case VERSUS:
            break;
         case SET_UP:
            menuBar.add(colorMenu);
            menuBar.add(pieceMenu);
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
}
