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
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class GamePanel extends JPanel implements ActionListener
{

    public Checkerboard myBoard;
    private Scoreboard scoreboard;
    private Timer updateTimer;
    private boolean timerStopped;
    
    private static final int MARGIN = 10;
    private static final int MIN_WIDTH = 750;
    private static final int MIN_HEIGHT = 750;

    /**
     * Default constructor, initializes the Checkerboard
     *
     * @param s keeps track of score and time for each player
     */
    public GamePanel(Scoreboard s)
    {
        myBoard = new Checkerboard();
        setBackground(Color.lightGray);
        setBorder(BorderFactory.createLineBorder(Color.black));
        setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));

        myBoard.setPieces(new ChessBoard().getPiecesArray());
        myBoard.setX(5);
        myBoard.setY(5);


        s.setBoundingBox(new Rectangle(Checkerboard.BOARD_WIDTH + MARGIN, 0, MIN_WIDTH - Checkerboard.BOARD_WIDTH - MARGIN, Checkerboard.BOARD_HEIGHT));
        scoreboard = s;
        updateTimer = new Timer(1000, this);
        updateTimer.setActionCommand("Update Timer");
        updateTimer.start();

        timerStopped = false;
    }

    @Override
    public void actionPerformed(ActionEvent event)
    {
        if (!event.getActionCommand().equals("Update Timer"))
        {
            return;
        }
        if (!timerStopped)
        {
            scoreboard.update();
        }
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
        if (myBoard.showPieceCursor(e.getPoint()))
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

        scoreboard.redraw(g);
        myBoard.paintBoard(g);
    }

    public void setMoveList(ArrayList<ChessMove> moves)
    {
        scoreboard.setMoveList(moves);
    }

    public void switchPlayerToMove()
    {
        scoreboard.switchPlayerToMove();
    }

    public void startTimer()
    {
        timerStopped = false;
    }

    public void stopTimer()
    {
        timerStopped = true;
    }


}
