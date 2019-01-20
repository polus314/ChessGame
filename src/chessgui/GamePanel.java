package chessgui;

/**
 * This panel is responsible for drawing the chess game on the JFrame.
 *
 * @author John Polus
 */
import chessgame.ChessBoard;
import chessgame.ChessMove;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class GamePanel extends JPanel implements ActionListener
{

    public Checkerboard myBoard;
    private ArrayList<ChessMove> moveList;
    private Scoreboard scoreboard;
    private Timer updateTimer;

    /**
     * Default constructor, initializes the Checkerboard
     */
    public GamePanel(Scoreboard s)
    {
        myBoard = new Checkerboard();
        setBackground(Color.lightGray);
        setBorder(BorderFactory.createLineBorder(Color.black));
        setMinimumSize(new Dimension(750, 750));

        myBoard.setPieces(new ChessBoard().getPieces());
        moveList = new ArrayList<>();
        myBoard.setX(250);
        myBoard.setY(100);

        scoreboard = s;
        updateTimer = new Timer(1000, this);
        updateTimer.setActionCommand("Update Timer");
        updateTimer.start();
    }

    @Override
    public void actionPerformed(ActionEvent event)
    {
        if (!event.getActionCommand().equals("Update Timer"))
        {
            return;
        }
        scoreboard.update();
        repaint();
    }

    @Override
    public Dimension getPreferredSize()
    {
        return new Dimension(750, 750);
    }

    public void mouseMoved(MouseEvent e)
    {
        e = SwingUtilities.convertMouseEvent(null, e, this);
        if (myBoard.pointHasPiece(e.getPoint()))
        {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        else
        {
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }

    /**
     * This method is used for painting the panel, calls super as well as the
     * paintBoard() method from Checkerboard class, which does most of the
     * actual work
     *
     * @param g
     */
    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        printMoveList(g);
        scoreboard.redraw(g);
        myBoard.paintBoard(g);
    }

    public void addMove(ChessMove move)
    {
        moveList.add(move);
    }

    public void setMoveList(ArrayList<ChessMove> moves)
    {
        moveList.clear();
        moveList.addAll(moves);
    }

    /**
     * This method prints out the moves that are played. Only 20 moves are able
     * to be displayed at a time, so the 20 most recent are shown
     *
     * @param g - graphics object moveList will be drawn on
     */
    public void printMoveList(Graphics g)
    {
        if (moveList == null)
        {
            return;
        }
        Font myFont = new Font("Arial", Font.PLAIN, 15);
        g.setFont(myFont);
        g.setColor(Color.BLACK);
        g.drawString("Move List", 30, 20);
        int start = 0;

        if (moveList.size() > 40)
        {
            start = moveList.size() - 40;
        }
        for (int i = start; i < moveList.size(); i++)
        {
            if (i % 2 == 0)
            {
                g.drawString(((i / 2) + 1) + ": "
                        + moveList.get(i).toString(), 30, (10 * (i - start) + 40));
            }
            else
            {
                g.drawString(moveList.get(i).toString(), 130, (10 * (i - start) + 30));
            }
        }
    }
}
