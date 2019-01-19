package chessgui;

import chessgame.GameMode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 * This panel implements a menu where the user can choose which mode they would
 * like to the game to be in. See GameMode.java for descriptions of each mode.
 *
 * @author John Polus
 */
public class ModeMenu extends JMenu implements ActionListener
{

    private JMenuItem item_singlePlayer, item_versus, item_setUp, item_puzzle;
    private GameMode mode;

    public ModeMenu()
    {
        super("Modes");
        initComponents();

        mode = GameMode.SET_UP;
    }

    private void initComponents()
    {
        item_singlePlayer = new JMenuItem("Play the Computer");
        item_singlePlayer.setMnemonic(KeyEvent.VK_C);
        item_singlePlayer.setActionCommand("Single");
        item_singlePlayer.addActionListener(this);

        item_versus = new JMenuItem("Play your Friend");
        item_versus.setMnemonic(KeyEvent.VK_F);
        item_versus.setActionCommand("Versus");
        item_versus.addActionListener(this);

        item_setUp = new JMenuItem("Set Up Board");
        item_setUp.setMnemonic(KeyEvent.VK_U);
        item_setUp.setActionCommand("Setup");
        item_setUp.addActionListener(this);

        item_puzzle = new JMenuItem("Puzzle Solver");
        item_puzzle.setMnemonic(KeyEvent.VK_P);
        item_puzzle.setActionCommand("Puzzle");
        item_puzzle.addActionListener(this);

        add(item_singlePlayer);
        add(item_versus);
        add(item_setUp);
        add(item_puzzle);
    }

    @Override
    public void actionPerformed(ActionEvent event)
    {
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
            case "Puzzle":
                setGameMode(GameMode.REFERENCE);
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
