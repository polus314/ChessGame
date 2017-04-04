package chessgame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import java.awt.event.KeyEvent;
import javax.swing.BoxLayout;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 This class implements a menu where the user can choose between black and
 white pieces to be used somewhere else.

 @author John Polus
 */
public class ColorMenu extends JMenu implements ActionListener
{

   private final JMenuItem item_black, item_white;
   private PieceColor chosenColor;

   public ColorMenu()
   {
      super("Colors");
      item_black = new JMenuItem("Choose Black Pieces");
      item_black.setMnemonic(KeyEvent.VK_B);
      item_black.setActionCommand("Black");

      item_white = new JMenuItem("Choose White Pieces");
      item_white.setMnemonic(KeyEvent.VK_W);
      item_white.setActionCommand("White");

      add(item_black);
      add(item_white);

      item_black.addActionListener(this);
      item_white.addActionListener(this);

      this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

      chosenColor = null;
   }

   @Override
   public void actionPerformed(ActionEvent event)
   {
      PieceColor oldColor = chosenColor;
      String command = event.getActionCommand();
      switch (command)
      {
         case "Black":
            chosenColor = PieceColor.BLACK;
            break;
         case "White":
            chosenColor = PieceColor.WHITE;
            break;
         default:
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
