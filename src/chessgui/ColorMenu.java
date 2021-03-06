package chessgui;

import chessgame.ChessPiece;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 * This class implements a menu where the user can choose between black and
 * white pieces to be used somewhere else.
 *
 * @author John Polus
 */
public class ColorMenu extends JMenu implements ActionListener
{

    private JMenuItem item_black, item_white;
    private ChessPiece.Color chosenColor;

    public ColorMenu()
    {
        super("Colors");
        initComponents();

        chosenColor = null;
    }

    private void initComponents()
    {
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
    }

    @Override
    public void actionPerformed(ActionEvent event)
    {
        ChessPiece.Color oldColor = chosenColor;
        String command = event.getActionCommand();
        switch (command)
        {
            case "Black":
                chosenColor = ChessPiece.Color.BLACK;
                break;
            case "White":
                chosenColor = ChessPiece.Color.WHITE;
                break;
            default:
                return;
        }
        firePropertyChange("color", oldColor, chosenColor);
    }

    public ChessPiece.Color getColor()
    {
        return chosenColor;
    }

    public void setColor(ChessPiece.Color pc)
    {
        chosenColor = pc;
    }

    public void flipColor()
    {
        if (chosenColor == null)
        {
            return;
        }
        chosenColor = chosenColor.opposite();
    }
}
