/**
To change this license header, choose License Headers in Project Properties.
To change this template file, choose Tools | Templates
and open the template in the editor.
*/

package chessgame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 
@author jppolecat
*/
public class PieceMenu extends JPanel implements ActionListener
{
   private static final String AC_KING = "king";
   private static final String AC_QUEEN = "queen";
   private static final String AC_ROOK = "rook";
   private static final String AC_BISHOP = "bishop";
   private static final String AC_KNIGHT = "knight";
   private static final String AC_PAWN = "pawn";
   private static final String AC_EMPTY = "empty";
   
   private final JButton kButton, qButton, rButton, bButton, 
         nButton, pButton, eButton;
   private ChessPiece piece;
   
   public PieceMenu()
   {
      kButton = new JButton("King");
      kButton.setMnemonic(KeyEvent.VK_K);
      kButton.setActionCommand(AC_KING);

      qButton = new JButton("Queen");
      qButton.setMnemonic(KeyEvent.VK_Q);
      qButton.setActionCommand(AC_QUEEN);
      
      rButton = new JButton("Rook");
      rButton.setMnemonic(KeyEvent.VK_R);
      rButton.setActionCommand(AC_ROOK);
      
      bButton = new JButton("Bishop");
      bButton.setMnemonic(KeyEvent.VK_B);
      bButton.setActionCommand(AC_BISHOP);
      
      nButton = new JButton("Knight");
      nButton.setMnemonic(KeyEvent.VK_N);
      nButton.setActionCommand(AC_KNIGHT);
      
      pButton = new JButton("Pawn");
      pButton.setMnemonic(KeyEvent.VK_P);
      pButton.setActionCommand(AC_PAWN);
      
      eButton = new JButton("Empty");
      eButton.setMnemonic(KeyEvent.VK_E);
      eButton.setActionCommand(AC_EMPTY);
      
      add(kButton);
      add(qButton);
      add(rButton);
      add(bButton);
      add(nButton);
      add(pButton);
      add(eButton);
      
      
      this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
      
      kButton.addActionListener(this);
      qButton.addActionListener(this);
      rButton.addActionListener(this);
      bButton.addActionListener(this);
      nButton.addActionListener(this);
      pButton.addActionListener(this);
      eButton.addActionListener(this);
   }
   
   public void actionPerformed(ActionEvent event)
   {
      String command = event.getActionCommand();
      switch(command)
      {
         case AC_KING: piece = new King();
            break;
         case AC_QUEEN: piece = new Queen();
            break;
         case AC_ROOK: piece = new Rook();
            break;
         case AC_BISHOP: piece = new Bishop();
            break;
         case AC_KNIGHT: piece = new Knight();
            break;
         case AC_PAWN: piece = new Pawn();
            break;
         case AC_EMPTY: piece = null;
            break;
         default: System.out.println("Button clicked, maybe??");
      }
      repaint();
      
   }
   
   public ChessPiece getPiece()
   {
      return piece;
   }
   
   public String toString()
   {
      return "Piece Menu";
   }
}
