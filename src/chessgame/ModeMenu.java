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
public class ModeMenu extends JPanel implements ActionListener
{
   private JButton btn_singlePlayer, btn_versus, btn_setUp;
   private GameMode mode;
   
   public ModeMenu()
   {
      init();
      this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
      
      mode = GameMode.UNDECIDED;
   }
   
   private void init()
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
      
      add(btn_singlePlayer);
      add(Box.createRigidArea(new Dimension(0, 10)));
      add(btn_versus);
      add(Box.createRigidArea(new Dimension(0, 10)));
      add(btn_setUp);  
   }
   
   @Override
   public void actionPerformed(ActionEvent event)
   {
      GameMode oldMode = mode;
      String command = event.getActionCommand();
      switch(command)
      {
         case "Single" : 
            mode = GameMode.SINGLE;
            break;
         case "Versus" :
            mode = GameMode.VERSUS;
            break;
         case "Setup" :
            mode = GameMode.SET_UP;
            break;
      }
      this.firePropertyChange("mode", oldMode, mode);
   }
   
   public void setGameMode(GameMode gm)
   {
       this.mode = gm;
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
