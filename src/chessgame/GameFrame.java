/**
 To change this license header, choose License Headers in Project Properties.
 To change this template file, choose Tools | Templates and open the template
 in the editor.
 */
package chessgame;

import java.awt.BorderLayout;
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

      setSize(750, 650);

      add(modeMenu, BorderLayout.EAST);
      add(gamePanel, BorderLayout.CENTER);
      add(b2, BorderLayout.WEST);
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
            gamePanel.controller.setGameMode(GameMode.SINGLE);
            break;
         case "Versus":
            modeMenu.setGameMode(GameMode.VERSUS);
            gamePanel.controller.setGameMode(GameMode.VERSUS);
            colorMenu.setColor(PieceColor.WHITE);
            break;
         case "Setup":
            modeMenu.setGameMode(GameMode.SET_UP);
            gamePanel.controller.setGameMode(GameMode.SET_UP);
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
      gamePanel.mode = GameMode.UNDECIDED;
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
      gamePanel.mode = modeMenu.getMode();
      gamePanel.humanPlayer = colorMenu.getColor();
      gamePanel.pieceToAdd = determinePieceToAdd();
      gamePanel.controller.setPieceToAdd(gamePanel.pieceToAdd);
      if (gamePanel.mode != GameMode.UNDECIDED)
      {
         remove(modeMenu);
         if (gamePanel.mode != GameMode.VERSUS)
         {
            add(colorMenu, BorderLayout.EAST);
         }
      }
      if (gamePanel.mode == GameMode.SET_UP)
      {
         add(pieceMenu, BorderLayout.SOUTH);
      }
      else if (gamePanel.mode == GameMode.SINGLE)
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
