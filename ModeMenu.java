/**
To change this license header, choose License Headers in Project Properties.
To change this template file, choose Tools | Templates
and open the template in the editor.
*/

package chessgame;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 
@author jppolecat
*/
public class ModeMenu extends JPanel
{
   private JButton b1, b2, b3;
   private GameMode mode;
   
   public ModeMenu()
   {
      b1 = new JButton("Single Player");
      b1.setMnemonic(KeyEvent.VK_S);
      b1.setActionCommand("Single");

      b2 = new JButton("Versus Mode");
      b2.setMnemonic(KeyEvent.VK_V);
      b2.setActionCommand("Versus");
      
      b3 = new JButton("Set-Up Mode");
      b3.setMnemonic(KeyEvent.VK_U);
      b3.setActionCommand("Setup");

      add(b1);
      add(Box.createRigidArea(new Dimension(0, 10)));
      add(b2);
      add(Box.createRigidArea(new Dimension(0, 10)));
      add(b3);
      
      this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
      
      mode = GameMode.UNDECIDED;
   }
   
   public void setGameMode(GameMode gm)
   {
       this.mode = gm;
   }
   
   public GameMode getMode()
   {
      return mode;
   }
   
   public JButton getSingle()
   {
       return b1;
   }
   
   public JButton getVersus()
   {
       return b2;
   }
   
   public JButton getSetup()
   {
       return b3;
   }
   
   public void setMode(GameMode m)
   {
      mode = m;
   }
   
   public String toString()
   {
      return "Mode Menu";
   }
}
