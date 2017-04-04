package chessgame;

/**
 This class represents the data and behavior of a generic chess piece. It
 specifies several things about pieces, like they have a value, a position,
 and a specific way of moving. Many of these are left to the specific child
 class to implement.

 @author John Polus
 */
public abstract class ChessPiece implements Comparable<ChessPiece>
{
   /**
   Enumerated type for which player a piece belongs to. 
   WHITE - player who moves first 
   BLACK - player who moves second

   @author John Polus
   */
   public enum Color
   {
      WHITE, BLACK;

      public Color opposite()
      {
         return this == WHITE ? BLACK : WHITE;
      }

      @Override
      public String toString()
      {
         return this == WHITE ? "White" : "Black";
      }

      public String oneLetter()
      {
         return this == WHITE ? "W" : "B";
      }
   }
   
   protected int value;
   protected int xCoord;
   protected int yCoord;
   protected Color color;
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
      color = Color.WHITE;
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
   public Color getColor()
   {
      return color;
   }

   public void setColor(Color c)
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

    @return String - string of length 1, identifies this specific type of
    piece
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
         return xCoord == cp.xCoord
               && yCoord == cp.yCoord
               && color == cp.color;
      }
      return false;
   }

   /**
    This method compares two Chess Pieces, useful for sorting

    @param cp - piece to compare this to
    @return -1 if this is worth less, 0 if equal, 1 if worth more than param
    */
   @Override
   public int compareTo(ChessPiece cp)
   {
      if (value < cp.value)
      {
         return -1;
      }
      else if (value == cp.value)
      {
         return 0;
      }
      else //if(value > cp.value)
      {
         return 1;
      }
   }
}
