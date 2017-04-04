package chessgame;

/**
 This class represents the Knight chess piece. The Knight moves in a peculiar
 way, two spaces in a direction then one in a perpendicular direction, like an
 "L" shape. The knight can also "jump over" other pieces and so is never
 prevented from moving to a space by pieces in its way. The Knight is one of
 the "minor" pieces along with the Bishop.

 @author John Polus
 */
public class Knight extends ChessPiece
{

   /**
    This is the default constructor for the knight piece
    */
   public Knight()
   {
      super();
   }

   /*
    This is the constructor that takes a piece color and a starting 
    position for inputs
    */
   public Knight(Color c, int xC, int yC)
   {
      super();
      value = 3;
      xCoord = xC;
      yCoord = yC;
      color = c;
   }

   /*
    This constructor takes a piece color and sets the starting 
    position as the default position for the knight piece
    */
   public Knight(Color c)
   {
      super();
      value = 3;
      color = c;
   }

   /*
    This takes a knight oject as input and copies all the attributes
    to the new knight piece
    */
   public Knight(Knight cp)
   {
      super();
      color = cp.color;
      hasMoved = cp.hasMoved;
      value = cp.value;
      xCoord = cp.xCoord;
      yCoord = cp.yCoord;
   }

   /**
    Returns a string describing this knight

    @return String - description of this
    */
   @Override
   public String toString()
   {
      return color.toString() + " Knight";
   }

   /**
    Returns the one letter used in identifying this piece when recording chess
    moves

    @return String - string of length 1, identifies this as a knight
    */
   @Override
   public String oneLetterIdentifier()
   {
      return "N";
   }

   /*
    This method determines if this Knight can move to a selected 
    square. If the Knight can move to the selected square the method returns
    true, false otherwise
    */
   @Override
   public boolean canMove(int x, int y)
   {
      if (xCoord + 2 == x && yCoord + 1 == y)
      {
         return true;
      }
      if (xCoord + 1 == x && yCoord + 2 == y)
      {
         return true;
      }
      if (xCoord - 2 == x && yCoord + 1 == y)
      {
         return true;
      }
      if (xCoord + 1 == x && yCoord - 2 == y)
      {
         return true;
      }
      if (xCoord + 2 == x && yCoord - 1 == y)
      {
         return true;
      }
      if (xCoord - 1 == x && yCoord + 2 == y)
      {
         return true;
      }
      if (xCoord - 2 == x && yCoord - 1 == y)
      {
         return true;
      }
      if (xCoord - 1 == x && yCoord - 2 == y)
      {
         return true;
      }
      return false;
   }

   @Override
   public ChessPiece copyOfThis()
   {
      return new Knight(this);
   }

   /**
    This method checks to see if two knights are the same.
    */
   @Override
   public boolean equals(Object obj)
   {
      if (obj instanceof Knight)
      {
         Knight cp = (Knight) obj;
         return xCoord == cp.xCoord
               && yCoord == cp.yCoord
               && color == cp.color;
      }
      return false;
   }
}
