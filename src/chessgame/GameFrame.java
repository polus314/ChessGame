/**
 To change this license header, choose License Headers in Project Properties.
 To change this template file, choose Tools | Templates and open the template
 in the editor.
 */
package chessgame;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**

 @author jppolecat
 */
public class GameFrame extends JFrame implements ActionListener
{

   protected ModeMenu modeMenu;
   protected GamePanel gamePanel;
   protected ColorMenu colorMenu;
   protected PieceMenu pieceMenu;
   protected JButton b2;
   
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

      b2 = new JButton("Main Menu");
      b2.setMnemonic(KeyEvent.VK_M);
      b2.setActionCommand("Main Menu");
      b2.addActionListener(this);

      modeMenu.getSingle().addActionListener(this);
      modeMenu.getVersus().addActionListener(this);
      modeMenu.getSetup().addActionListener(this);
      colorMenu.getWhite().addActionListener(this);
      colorMenu.getBlack().addActionListener(this);
      
      pieceMenu.bButton.addActionListener(this);
      pieceMenu.eButton.addActionListener(this);
      pieceMenu.qButton.addActionListener(this);
      pieceMenu.rButton.addActionListener(this);
      pieceMenu.pButton.addActionListener(this);
      pieceMenu.nButton.addActionListener(this);
      pieceMenu.kButton.addActionListener(this);

      setSize(FRAME_WIDTH, FRAME_HEIGHT);

      add(modeMenu, BorderLayout.EAST);
      add(gamePanel, BorderLayout.CENTER);
      add(b2, BorderLayout.WEST);

      addMouseListener(new MouseAdapter()
      {
         public void mousePressed(MouseEvent e)
         {
            int x = e.getX() - (gamePanel.getX() + leftBorderWidth);
            int y = e.getY() - (gamePanel.getY() + topBorderHeight);
            System.out.println("x: " + x);
            System.out.println("y: " + y);
            if (x < Checkerboard.BOARD_WIDTH && y < Checkerboard.BOARD_HEIGHT)
            {
               int a = x / Checkerboard.SQUARE_WIDTH;
               int b = y / Checkerboard.SQUARE_HEIGHT;
               System.out.println("a: " + a);
               System.out.println("b: " + b);
               System.out.println("");
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

   public void paintComponent(Graphics g)
   {
      
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
         case "White":
            colorMenu.setColor(PieceColor.WHITE);
            break;
         case "Black":
            colorMenu.setColor(PieceColor.BLACK);
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
      add(b2, BorderLayout.WEST);
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
      System.out.println("In refresh method in GameFrame");
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
