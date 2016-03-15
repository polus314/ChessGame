/**
To change this license header, choose License Headers in Project Properties.
To change this template file, choose Tools | Templates
and open the template in the editor.
*/

package chessgame;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
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
   protected JButton b1, b2;
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

   private static void createAndShowGUI()
   {
      GameFrame gf = new GameFrame();
      gf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      gf.setVisible(true);
   }
   
   public GameFrame()
   {   
      super("Polecat's Chess Game");
      modeMenu = new ModeMenu();
      gamePanel = new GamePanel();
      colorMenu = new ColorMenu();
      pieceMenu = new PieceMenu();
      
      b1 = new JButton("Refresh button");
      b1.setMnemonic(KeyEvent.VK_R);
      b1.setActionCommand("Refresh");
      b1.addActionListener(this);
      
      b2 = new JButton("Main Menu");
      b2.setMnemonic(KeyEvent.VK_M);
      b2.setActionCommand("Main Menu");
      b2.addActionListener(this);
      
      System.out.println("Created GUI on EDT? "
            + SwingUtilities.isEventDispatchThread());      
      setSize(750, 650);
      
      add(modeMenu, BorderLayout.EAST);
      add(gamePanel, BorderLayout.CENTER);
      add(b1, BorderLayout.NORTH);
      add(b2,BorderLayout.WEST);
            
//      setVisible(true);
   }
   
   public void actionPerformed(ActionEvent event)
   {
      String command = event.getActionCommand();
      switch(command)
      {
         case ("Refresh"): refresh();
            break;
         case ("Main Menu"): backToMainMenu();
            break;
      }
   }
   
   private void backToMainMenu()
   {
      remove(pieceMenu);
      remove(colorMenu);
      add(modeMenu, BorderLayout.EAST);
      add(gamePanel, BorderLayout.CENTER);
      add(b1, BorderLayout.NORTH);
      add(b2,BorderLayout.WEST);
      gamePanel.mode = GameMode.UNDECIDED;
      modeMenu.setMode(GameMode.UNDECIDED);
      
      int resize = getHeight();
      if(resize % 2 == 0)
         resize++;
      else
         resize--;
      setSize(750, resize);
      repaint();
   }
   
   private void refresh()
   {
      System.out.println("In refresh method in GameFrame");
      gamePanel.mode = modeMenu.getMode();
      gamePanel.humanPlayer = colorMenu.getColor();
      gamePanel.pieceToAdd = determinePieceToAdd();
      if(gamePanel.mode != GameMode.UNDECIDED)
      {
         remove(modeMenu);
         add(colorMenu, BorderLayout.EAST);
      }
      if(gamePanel.mode == GameMode.SET_UP)
      {
         add(pieceMenu, BorderLayout.SOUTH);
      }
      else if(gamePanel.mode == GameMode.SINGLE &&
            gamePanel.humanPlayer != PieceColor.EMPTY)
      {
         remove(colorMenu);
      }
      else if(gamePanel.mode == GameMode.VERSUS &&
            gamePanel.humanPlayer != PieceColor.EMPTY)
      {
         remove(colorMenu);
      }
      validate();
      int resize = getHeight();
      if(resize % 2 == 0)
         resize++;
      else
         resize--;
      setSize(750, resize);
      repaint();
   }
   
   private ChessPiece determinePieceToAdd()
   {
      PieceType type = pieceMenu.getPieceType();
      ChessPiece piece = new ChessPiece();
      switch (type)
      {
         case KING: piece = new King(colorMenu.getColor());
            break;
         case QUEEN: piece = new Queen(colorMenu.getColor());
            break;
         case ROOK: piece = new Rook(colorMenu.getColor());
            break;
         case BISHOP: piece = new Bishop(colorMenu.getColor());
            break;
         case KNIGHT: piece = new Knight(colorMenu.getColor());
            break;
         case PAWN: piece = new Pawn(colorMenu.getColor());
            break;
         default: piece = new ChessPiece();
      }
      return piece;
   }
}
