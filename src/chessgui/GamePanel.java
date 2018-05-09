package chessgui;

/**
 This panel is responsible for drawing the chess game on the JFrame.

 @author John Polus
 */
import chessgame.ChessBoard;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

public class GamePanel extends JPanel
{
   public Checkerboard myBoard;
  
   /**
    Default constructor, initializes the Checkerboard
    */
   public GamePanel()
   {
      myBoard = new Checkerboard();
      setBorder(BorderFactory.createLineBorder(Color.black));
      setMinimumSize(new Dimension(425, 425));
      
      myBoard.setPieces(new ChessBoard().getPiecesList());
   }
   
   @Override
   public Dimension getPreferredSize()
   {
      return new Dimension(425, 425);
   }

   /**
    This method is used for painting the panel, calls super as well as the
    paintBoard() method from Checkerboard class, which does most of the actual
    work

    @param g
    */
   @Override
   public void paintComponent(Graphics g)
   {
      super.paintComponent(g);

      myBoard.paintBoard(g);
   }
}
