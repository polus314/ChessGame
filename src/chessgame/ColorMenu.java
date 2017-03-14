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
   private JButton btn_black, btn_white;
   private PieceColor chosenColor;
   
   public ColorMenu()
   {
      btn_black = new JButton("Choose Black Pieces");
      btn_black.setMnemonic(KeyEvent.VK_B);
      btn_black.setActionCommand("Black");
      
      btn_white = new JButton("Choose White Pieces");
      btn_white.setMnemonic(KeyEvent.VK_W);
      btn_white.setActionCommand("White");
      
      add(btn_black);
      add(btn_white);
      
      btn_black.addActionListener(this);
      btn_white.addActionListener(this);
      
      this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
      
      chosenColor = PieceColor.WHITE;
   }
   
   @Override
   public void actionPerformed(ActionEvent event)
   {
      PieceColor oldColor = chosenColor;
      String command = event.getActionCommand();
      switch(command)
      {
         case "Black" : 
            chosenColor = PieceColor.BLACK;
            break;
         case "White" :
            chosenColor = PieceColor.WHITE;
            break;
         default :
            return;
      }
      firePropertyChange("color", oldColor, chosenColor);
   }
   
   public PieceColor getColor()
   {
      return chosenColor;
   }
   
   public void setColor(PieceColor pc)
   {
       chosenColor = pc;
   }
}
