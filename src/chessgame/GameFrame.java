package chessgame;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**

 @author jppolecat
 */
public class GameFrame extends JFrame implements ActionListener, PropertyChangeListener
{

   protected ModeMenu modeMenu;
   protected GamePanel gamePanel;
   protected ColorMenu colorMenu;
   protected PieceMenu pieceMenu;
   protected JButton btn_mainMenu;
   
   public PieceColor humanPlayer;
   public GameMode mode;
   public ChessPiece pieceToAdd;
   public GameController controller;
   
   private static final int FRAME_HEIGHT = 650;
   private static final int FRAME_WIDTH = 750;
   private int topBorderHeight = 32;
   private int leftBorderWidth = 8;

   public static void main(String[] args)
   {
      SwingUtilities.invokeLater(new Runnable()
      {
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
      super("John Polus's Chess Game");
      modeMenu = new ModeMenu();
      gamePanel = new GamePanel();
      colorMenu = new ColorMenu();
      pieceMenu = new PieceMenu();
      
      mode = GameMode.UNDECIDED;
      humanPlayer = PieceColor.WHITE;
      controller = new GameController();
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
         public void mousePressed(MouseEvent e)
         {
            int x = e.getX() - (gamePanel.getX() + leftBorderWidth);
            int y = e.getY() - (gamePanel.getY() + topBorderHeight);
            if (x < Checkerboard.BOARD_WIDTH && y < Checkerboard.BOARD_HEIGHT)
            {
               int a = x / Checkerboard.SQUARE_WIDTH;
               int b = y / Checkerboard.SQUARE_HEIGHT;
               boolean success = controller.takeAction(a,b);
               if(!success)
                  return;

               gamePanel.myBoard.setSelectedPiece(controller.getSelectedPiece());
               gamePanel.myBoard.setPieces(controller.getPiecesList());
               repaint();
               if(mode == GameMode.SINGLE && 
                  controller.getPlayerToMove() != humanPlayer &&
                  !controller.isGameOver())
               {
                  if(controller.doCPUTurn())
                  {
                     gamePanel.myBoard.setSelectedPiece(controller.getSelectedPiece());
                     gamePanel.myBoard.setPieces(controller.getPiecesList());
                     repaint();
                  }
               }
            }
         }
      });
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
            controller.setGameMode((GameMode)event.getNewValue());
            break;
         case "color" :
            pieceToAdd = determinePieceToAdd();
            controller.setPieceToAdd(pieceToAdd);
            break;
         default:
            return;
      }
      refresh();
   }
   
   public void actionPerformed(ActionEvent event)
   {
      String command = event.getActionCommand();
      switch (command)
      {
         case "Main Menu":
            backToMainMenu();
            break;
         case "Single":
            modeMenu.setGameMode(GameMode.SINGLE);
            controller.setGameMode(GameMode.SINGLE);
            break;
         case "Versus":
            modeMenu.setGameMode(GameMode.VERSUS);
            controller.setGameMode(GameMode.VERSUS);
            colorMenu.setColor(PieceColor.WHITE);
            break;
         case "Setup":
            modeMenu.setGameMode(GameMode.SET_UP);
            controller.setGameMode(GameMode.SET_UP);
            break;
      }
      refresh();
   }

   private void backToMainMenu()
   {
      remove(pieceMenu);
      remove(colorMenu);
      add(modeMenu, BorderLayout.EAST);
      add(gamePanel, BorderLayout.CENTER);
      add(btn_mainMenu, BorderLayout.WEST);
      mode = GameMode.UNDECIDED;
      modeMenu.setMode(GameMode.UNDECIDED);

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

   private void refresh()
   {
      mode = modeMenu.getMode();
      humanPlayer = colorMenu.getColor();
      pieceToAdd = determinePieceToAdd();
      controller.setPieceToAdd(pieceToAdd);
      if (mode != GameMode.UNDECIDED)
      {
         remove(modeMenu);
         if (mode != GameMode.VERSUS)
         {
            add(colorMenu, BorderLayout.EAST);
         }
      }
      if (mode == GameMode.SET_UP)
      {
         add(pieceMenu, BorderLayout.SOUTH);
      }
      else if (mode == GameMode.SINGLE)
      {
         remove(colorMenu);
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

   private ChessPiece determinePieceToAdd()
   {
      if (pieceMenu.getPiece() == null)
      {
         return null;
      }
      ChessPiece piece = pieceMenu.getPiece().copyOfThis();
      piece.setColor(colorMenu.getColor());
      return piece;
   }
}
