package chessgame;

/**
This class is for the movements of the Rook piece
*/

/**
 
@author jppolecat
*/
public class Rook extends ChessPiece
{
    /*
   This is the default constructor for a Rook
   */
   public Rook()
   {
      type = PieceType.ROOK;
   }
   
   /*
   This constructor takes an input of the piece color and the starting
   position for the Rook
   */
   public Rook(PieceColor c, int xC, int yC)
   {
      type = PieceType.ROOK;
      value = 5;
      xCoord = xC;
      yCoord = yC;
      color = c;
   }
   
   /*
   This constructor takes a piece color and sets the starting 
   position as the default Rook position
   */
   public Rook(PieceColor c)
   {
      type = PieceType.ROOK;
      value = 5;
      color = c;
   }
   
   /*
   This constructor takes a Rook as an input and copies all the
   attributes for the new Rook piece
   */
   public Rook(Rook cp)
   {
      type = cp.type;
      color = cp.color;
      hasMoved = cp.hasMoved;
      value = cp.value;
      xCoord = cp.xCoord;
      yCoord = cp.yCoord;
   }
   
   /**
   Returns a string describing this rook
   
   @return String - description of this
   */
   @Override
   public String toString()
   {
      return color.toString() + " Rook";
   }
   
   /*
   This method determines if a selected Rook can move to a selected 
   square. If the rook can move to the selected square the method returns
   true, false otherwise
   */
   @Override
   public boolean canMove(int x, int y)
   {
      return xCoord == x || yCoord == y;
   }
}