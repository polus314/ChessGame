package chessgui;

import chessgame.*;
import java.awt.BorderLayout;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
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
            switch(controller.getGameMode())
            {
               case SET_UP: doSetUp(e, a, b); break;
               case SINGLE:
               case VERSUS: doGamePlay(e, a,b); break;
            }
            repaint();
         }
      }

      /**
      Handles the mouseWheelMoved event for the component to which this
      adapter is attached. Cycles through the types and colors for the
      pieceToAdd.
      
      @param e mouseWheelEvent that is passed to this component
      */
      @Override
      public void mouseWheelMoved(MouseWheelEvent e)
      {
         if(pieceToAdd == null)
            return;
         
         if(!e.isShiftDown())
         {
            ChessPiece.Color curColor = pieceToAdd.getColor();
            switch(pieceToAdd.oneLetterIdentifier())
            {
               case "K": 
                  pieceToAdd = new Queen(curColor);
                  break;
               case "Q": 
                  pieceToAdd = new Bishop(curColor);
                  break;
               case "B": 
                  pieceToAdd = new Knight(curColor);
                  break;
               case "N": 
                  pieceToAdd = new Rook(curColor);
                  break;
               case "R": 
                  pieceToAdd = new Pawn(curColor);
                  break;
               case "":
                  pieceToAdd = new King(curColor);
                  break;

            }
            String label = pieceToAdd == null ? "Empty" : pieceToAdd.toString();
            lbl_pieceToAdd.setText(label);
         }
         else if(pieceToAdd != null)
         {
            int clicks = Math.abs(e.getWheelRotation());
            if(clicks % 2 == 1)
            {
               ChessPiece.Color nextColor = pieceToAdd.getColor().opposite();
               pieceToAdd.setColor(nextColor);
               lbl_pieceToAdd.setText(pieceToAdd.toString());
            }
         }
      }
   }
   
   private void initMouseListener()
   {
      MouseAdapter m = new MyMouseAdapter();
      addMouseListener(m);
      addMouseWheelListener(m);
   }
   
   private ModeMenu modeMenu;
   private GamePanel gamePanel;
   private ColorMenu colorMenu;
   private PieceMenu pieceMenu;
   private boolean modeChanged;
   private JMenuBar menuBar; 
   
   public GameController controller;
   public ChessPiece.Color humanPlayer;
   private JLabel lbl_pieceToAdd;

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
       System.out.println("This executes");
       ChessRobot robby = new ChessRobot();
       (new Thread(robby)).start();
       while(true)
       {
            try { Thread.sleep(1000); } catch(Exception e) {}
            System.out.println("Another day, another dollar");
       }
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
      
      lbl_pieceToAdd = new JLabel("Piece To Add");
      add(lbl_pieceToAdd);
      pieceToAdd = new Pawn(ChessPiece.Color.WHITE);
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
      menuBar.add(colorMenu);
      menuBar.add(pieceMenu);

      pieceMenu.addPropertyChangeListener(this);
      modeMenu.addPropertyChangeListener(this);
      colorMenu.addPropertyChangeListener(this);

      add(gamePanel, BorderLayout.NORTH);
      setJMenuBar(menuBar);
      
      initMouseListener();
   }
   
   private void doSetUp(MouseEvent e, int a, int b)
   {
      int button = e.getButton();
      if(button == MouseEvent.BUTTON1) // Left Mouse Button
      {
         System.out.println("Button 1");
         System.out.println("PieceToAdd: " + pieceToAdd);
         if(pieceToAdd != null)
         {
            ChessPiece newPiece = pieceToAdd.copyOfThis();
            newPiece.movePiece(a, b);
            gamePanel.myBoard.setPieceAt(a, b, newPiece);
         }
         else
            gamePanel.myBoard.setPieceAt(a, b, null);
      }
      else if(button == MouseEvent.BUTTON2) // Middle Mouse Button
      {         
         System.out.println("Button 2");
         pieceToAdd = gamePanel.myBoard.getPieceAt(a, b);
         if(pieceToAdd != null)
            pieceToAdd = pieceToAdd.copyOfThis();
         String label = pieceToAdd == null ? "Empty" : pieceToAdd.toString();
         lbl_pieceToAdd.setText(label);
      }
      else if(button == MouseEvent.BUTTON3) // Right Mouse Button
      {
         System.out.println("Button 3");
         gamePanel.myBoard.setPieceAt(a, b, null);
      }
      else
      {
         System.out.println("Button?: " + button);
      }
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
         changeMode(GameMode.UNDECIDED);
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

   @Override
   public void propertyChange(PropertyChangeEvent event)
   {
      String property = event.getPropertyName();
      switch (property)
      {
         case "piece":
            pieceToAdd = determinePieceToAdd();
            break;
         case "mode":
            handleModePropertyChange(event);
            break;
         case "color":
            GameMode mode = modeMenu.getMode();
            if (mode == GameMode.SET_UP)
            {
               pieceToAdd.setColor(colorMenu.getColor());
            }
            else if (mode == GameMode.SINGLE)
            {
               humanPlayer = colorMenu.getColor();
               remove(colorMenu);
            }
            pieceToAdd = determinePieceToAdd();
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
            if (!startGameWithDisplayedPieces())
            {
               JOptionPane.showMessageDialog(this, ILLEGAL_BOARD_POS);
               shouldChangeMode = false;
            }
         }
         else if (choice == 1)
         {
            setUpNewGame();
            
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
         case "Save":
            saveToFile();
            break;
         case "Load":
            load();
            break;
         case "Solve":
            String settings = JOptionPane.showInputDialog(null, "Choose an algorithm and heuristic");
            setSettings(settings);
            solveForMate();
            break;
      }
      refresh();
   }
   
   private void setSettings(String settings)
   {
      if(settings.charAt(0) == 'f')
      {
         controller.deepBlue.heuristic = AI.Heuristic.FORCING;
      }
      else if (settings.charAt(0) == 'm')
      {
         controller.deepBlue.heuristic = AI.Heuristic.MATERIAL;
      }
      else
      {
         controller.deepBlue.heuristic = AI.Heuristic.UNINFORMED;
      }
      if(settings.charAt(1) == 'b')
      {
         controller.deepBlue.algorithm = AI.Algorithm.BFS;
      }
      else if(settings.charAt(1) == 'd')
      {
         controller.deepBlue.algorithm = AI.Algorithm.DFS;
      }
      else
      {
         controller.deepBlue.algorithm = AI.Algorithm.GREEDY;
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

   private void refresh()
   {
      pieceToAdd = determinePieceToAdd();

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
      switch (controller.getGameMode())
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
      return piece;
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
   Loads a position from the file with the given pathname and displays it on
   the checkerboard.
   
   @param pathname location of board position save file
   @return true if position could be loaded successfully, false otherwise
   */
   private boolean loadFromFile(String pathname)
   {
      ChessBoard cb = new ChessBoard(controller.getPiecesList());
      if(GameController.loadPositionFromFile(pathname, cb))
      {
         gamePanel.myBoard.setPieces(cb.getPiecesList());
         return true;
      }
      else
      {
         return false;
      }
   }
   
   /**
   Prompts the user for file to load from and then attempts to load board
   position from that file. Displays a status message and, if successful, the
   new board position.
   */
   private void load()
   {
      File selectedFile;
      JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
      if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) 
      {
         selectedFile = chooser.getSelectedFile();
      }
      else
         return;
      if(loadFromFile(selectedFile.getAbsolutePath()))
      {
         System.out.println("Load Successful");
      }
      else
         System.out.println("Load failed");
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
