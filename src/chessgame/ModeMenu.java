package chessgame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 This panel implements a menu where the user can choose which mode they would
 like to the game to be in. See GameMode.java for descriptions of each mode.

 @author John Polus
 */
public class ModeMenu extends JPanel implements ActionListener
{

   private JButton btn_singlePlayer, btn_versus, btn_setUp;
   private GameMode mode;

   public ModeMenu()
   {
      initComponents();
      this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

      add(btn_singlePlayer);
      add(btn_versus);
      add(btn_setUp);

      mode = GameMode.UNDECIDED;
   }

   private void initComponents()
   {
      btn_singlePlayer = new JButton("Single Player");
      btn_singlePlayer.setMnemonic(KeyEvent.VK_S);
      btn_singlePlayer.setActionCommand("Single");
      btn_singlePlayer.addActionListener(this);

      btn_versus = new JButton("Versus Mode");
      btn_versus.setMnemonic(KeyEvent.VK_V);
      btn_versus.setActionCommand("Versus");
      btn_versus.addActionListener(this);

      btn_setUp = new JButton("Set-Up Mode");
      btn_setUp.setMnemonic(KeyEvent.VK_U);
      btn_setUp.setActionCommand("Setup");
      btn_setUp.addActionListener(this);
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

   public String toString()
   {
      return "Mode Menu";
   }
}
