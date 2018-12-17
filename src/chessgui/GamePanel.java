package chessgui;

/**
 This panel is responsible for drawing the chess game on the JFrame.

 @author John Polus
 */
import chessgame.ChessBoard;
import chessgame.ChessMove;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;

public class GamePanel extends JPanel
{
   public Checkerboard myBoard;
   private ArrayList<ChessMove> moveList;
  
   /**
    Default constructor, initializes the Checkerboard
    */
   public GamePanel()
   {
      myBoard = new Checkerboard();
      setBorder(BorderFactory.createLineBorder(Color.black));
      setMinimumSize(new Dimension(750, 750));
      
      myBoard.setPieces(new ChessBoard().getPieces());
      moveList = new ArrayList<>();
      myBoard.setX(250);
      myBoard.setY(100);
   }
   
   @Override
   public Dimension getPreferredSize()
   {
      return new Dimension(750, 750);
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

      printMoveList(g);
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

   // KEPT FOR REFERENCE FOR WHEN I IMPLEMENT THIS SOMEWHERE ELSE ON THE FRAME

   /**
   This method prints out the moves that are played. Only 20 moves are able to
   be displayed at a time, so the 20 most recent are shown
   
   @param g - graphics object moveList will be drawn on 
   */
   public void printMoveList(Graphics g)
   {
      if(moveList == null)
         return;
      Font myFont = new Font("Arial", Font.PLAIN, 15);
      g.setFont(myFont);
      g.setColor(Color.BLACK);
      g.drawString("Move List", 30, 20);
      int start = 0;

      if (moveList.size() > 40)
         start = moveList.size() - 40;
      for (int i = start; i < moveList.size(); i++)
      {
         if(i % 2 == 0)
         {
            g.drawString(((i / 2) + 1) + ": " + 
                  moveList.get(i).toString(), 30, (10 * (i - start) + 40));
         }
         else
         {
            g.drawString(moveList.get(i).toString(), 130, (10 * (i - start) + 30));
         }
      }
   }
}
