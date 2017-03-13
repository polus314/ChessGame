package chessgame;

/**
This class is for the super class of chess piece. It can determine movements
and create new chess pieces.
*/

/**
 @author jppolecat
 */
public abstract class ChessPiece
{
   protected int value;
   protected int xCoord;
   protected int yCoord;
   protected PieceColor color;
   protected boolean hasMoved;

   /*
   This is the default constructor for a chess piece
   */
   public ChessPiece()
   {
      value = 0;
      xCoord = 0;
      yCoord = 0;
      hasMoved = false;
      color = PieceColor.WHITE;
   }

   /*
   This constructor takes a chess piece and copies all the attributes 
   to the new chess piece object
   */
   public ChessPiece(ChessPiece cp)
   {
      color = cp.color;
      hasMoved = cp.hasMoved;
      value = cp.value;
      xCoord = cp.xCoord;
      yCoord = cp.yCoord;
   }

   /*
   This method inputs an new x index and a new y index and sets them as the
   new x coordinate and new y coordinate
   */
   public void movePiece(int x, int y)
   {
      xCoord = x;
      yCoord = y;
   }

   /*
   This returns the x coordinate
   */
   public int getX()
   {
      return xCoord;
   }

   /*
   This returns the Y coordinate
   */
   public int getY()
   {
      return yCoord;
   }

   /*
   This geturns the color of the chess piece
   */
   public PieceColor getColor()
   {
      return color;
   }
   
   public void setColor(PieceColor c)
   {
      color = c;
   }

   /*
   This method takes an x and y coordinate as input and returns whether or not
   this chess piece can move to those coordinates
   */
   public abstract boolean canMove(int x, int y);

   /*
   This will input a chess piece and copy all the attributes to the 
   selected chess piece
   */
   public void copy(ChessPiece cp)
   {
      color = cp.color;
      hasMoved = cp.hasMoved;
      value = cp.value;
      xCoord = cp.xCoord;
      yCoord = cp.yCoord;
   }

   /**
   This method will create and return a copy of this (basically clone())
   
   @return ChessPiece - copy of this chess piece
   */
   public abstract ChessPiece copyOfThis();

   /*
   This returns a string of the piece name
   */
   @Override
   public String toString()
   {
      return color.toString() + " ChessPiece";
   }
   
   /**
   Returns the one letter used in identifying this piece when recording chess
   moves
   
   @return String - string of length 1, identifies this specific type of piece
   */
   public abstract String oneLetterIdentifier();

   /*
   This method checks to see if two chess pieces are the same.
   If they are the same it will return true, false otherwise.
   */
   @Override
   public boolean equals(Object obj)
   {
      if (obj instanceof ChessPiece)
      {
         ChessPiece cp = (ChessPiece) obj;
         return xCoord == cp.xCoord && 
                yCoord == cp.yCoord && 
                color == cp.color;
      }
      return false;
   }
}