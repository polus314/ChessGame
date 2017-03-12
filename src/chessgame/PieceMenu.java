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
   private JButton kButton, qButton, rButton, bButton, 
         nButton, pButton, eButton;
   private ChessPiece piece;
   
   public PieceMenu()
   {
      kButton = new JButton("King");
      kButton.setMnemonic(KeyEvent.VK_K);
      kButton.setActionCommand("king");

      qButton = new JButton("Queen");
      qButton.setMnemonic(KeyEvent.VK_Q);
      qButton.setActionCommand("queen");
      
      rButton = new JButton("Rook");
      rButton.setMnemonic(KeyEvent.VK_R);
      rButton.setActionCommand("rook");
      
      bButton = new JButton("Bishop");
      bButton.setMnemonic(KeyEvent.VK_B);
      bButton.setActionCommand("bishop");
      
      nButton = new JButton("Knight");
      nButton.setMnemonic(KeyEvent.VK_N);
      nButton.setActionCommand("knight");
      
      pButton = new JButton("Pawn");
      pButton.setMnemonic(KeyEvent.VK_P);
      pButton.setActionCommand("pawn");
      
      eButton = new JButton("Empty");
      eButton.setMnemonic(KeyEvent.VK_E);
      eButton.setActionCommand("empty");
      
      add(kButton);
//      add(Box.createRigidArea(new Dimension(0, 10)));
      add(qButton);
//      add(Box.createRigidArea(new Dimension(0, 10)));
      add(rButton);
//      add(Box.createRigidArea(new Dimension(0, 10)));
      add(bButton);
//      add(Box.createRigidArea(new Dimension(0, 10)));
      add(nButton);
//      add(Box.createRigidArea(new Dimension(0, 10)));
      add(pButton);
//      add(Box.createRigidArea(new Dimension(0, 10)));
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
         case ("king"): piece = new King();
            break;
         case ("queen"): piece = new Queen();
            break;
         case ("rook"): piece = new Rook();
            break;
         case ("bishop"): piece = new Bishop();
            break;
         case ("knight"): piece = new Knight();
            break;
         case ("pawn"): piece = new Pawn();
            break;
         case ("empty"): piece = null;
            break;
         default: System.out.println("Button clicked, maybe??");
      }
      System.out.println(piece.toString());
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
