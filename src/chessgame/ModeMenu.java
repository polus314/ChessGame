package chessgame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 This panel implements a menu where the user can choose which mode they would
 like to the game to be in. See GameMode.java for descriptions of each mode.

 @author John Polus
 */
public class ModeMenu extends JMenu implements ActionListener
{

   private JMenuItem item_singlePlayer, item_versus, item_setUp;
   private GameMode mode;

   public ModeMenu()
   {
      super("Modes ");
      initComponents();
      this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

      add(item_singlePlayer);
      add(item_versus);
      add(item_setUp);

      mode = GameMode.UNDECIDED;
   }

   private void initComponents()
   {
      item_singlePlayer = new JMenuItem("Single Player");
      item_singlePlayer.setMnemonic(KeyEvent.VK_S);
      item_singlePlayer.setActionCommand("Single");
      item_singlePlayer.addActionListener(this);

      item_versus = new JMenuItem("Versus Mode");
      item_versus.setMnemonic(KeyEvent.VK_V);
      item_versus.setActionCommand("Versus");
      item_versus.addActionListener(this);

      item_setUp = new JMenuItem("Set-Up Mode");
      item_setUp.setMnemonic(KeyEvent.VK_U);
      item_setUp.setActionCommand("Setup");
      item_setUp.addActionListener(this);
   }

   @Override
   public void actionPerformed(ActionEvent event)
   {
      GameMode oldMode = mode;
      String command = event.getActionCommand();
      switch (command)
      {
         case "Single":
            setGameMode(GameMode.SINGLE);
            break;
         case "Versus":
            setGameMode(GameMode.VERSUS);
            break;
         case "Setup":
            setGameMode(GameMode.SET_UP);
            break;
      }
   }

   public void setGameMode(GameMode gm)
   {
      GameMode oldMode = mode;
      mode = gm;
      firePropertyChange("mode", oldMode, mode);
   }

   public GameMode getMode()
   {
      return mode;
   }

   public void setMode(GameMode m)
   {
      mode = m;
   }

   @Override
   public String toString()
   {
      return "Mode Menu";
   }
}
