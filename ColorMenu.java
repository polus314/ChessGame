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
public class ColorMenu extends JPanel implements ActionListener
{
   private JButton b1, b2;
   private PieceColor chosenColor;
   
   public ColorMenu()
   {
      b1 = new JButton("Choose Black Pieces");
      b1.setMnemonic(KeyEvent.VK_B);
      b1.setActionCommand("black");
      
      b2 = new JButton("Choose White Pieces");
      b2.setMnemonic(KeyEvent.VK_W);
      b2.setActionCommand("white");
      
      add(b1);
      add(b2); 
      
      b1.addActionListener(this);
      b2.addActionListener(this);
      
      this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
      
      chosenColor = PieceColor.EMPTY;
   }
   
   public void actionPerformed(ActionEvent event)
   {
      if(event.getActionCommand().equals("white"))
      {
         chosenColor = PieceColor.WHITE;
      }
      else
      {
         chosenColor = PieceColor.BLACK;
      }
   }
   
   public PieceColor getColor()
   {
      return chosenColor;
   }
}
