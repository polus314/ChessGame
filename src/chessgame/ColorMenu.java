/**
To change this license header, choose License Headers in Project Properties.
To change this template file, choose Tools | Templates
and open the template in the editor.
*/

package chessgame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.event.KeyEvent;
import javax.swing.BoxLayout;

/**
 
@author jppolecat
*/
public class ColorMenu extends JPanel
{
   private JButton b1, b2;
   private PieceColor chosenColor;
   
   public ColorMenu()
   {
      b1 = new JButton("Choose Black Pieces");
      b1.setMnemonic(KeyEvent.VK_B);
      b1.setActionCommand("Black");
      
      b2 = new JButton("Choose White Pieces");
      b2.setMnemonic(KeyEvent.VK_W);
      b2.setActionCommand("White");
      
      add(b1);
      add(b2); 
      
      this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
      
      chosenColor = PieceColor.WHITE;
   }
   
   public PieceColor getColor()
   {
      return chosenColor;
   }
   
   public JButton getWhite()
   {
       return b2;
   }
   
   public JButton getBlack()
   {
       return b1;
   }
   
   public void setColor(PieceColor pc)
   {
       this.chosenColor = pc;
   }
}
