package chessgame;

/**
This class is for the super class of chess piece. It can determine movements
and create new chess pieces.
*/

/**
 @author jppolecat
 */
public class ChessPiece
{
   protected PieceType type;
   protected String pieceName;
   protected int value;
   protected int xCoord;
   protected int yCoord;
   protected PieceColor color;
   protected boolean selected;
   protected boolean hasMoved;

   /*
   This is the default constructor for a chess piece
   */
   public ChessPiece()
   {
      type = PieceType.EMPTY;
      pieceName = "     ";
      value = 0;
      color = PieceColor.EMPTY;
      xCoord = 0;
      yCoord = 0;
      selected = false;
      hasMoved = false;
   }

   /*
   This constructor takes a chess piece and copies all the attributes 
   to the new chess piece object
   */
   public ChessPiece(ChessPiece cp)
   {
      type = cp.type;
      pieceName = cp.pieceName;
      color = cp.color;
      selected = cp.selected;
      hasMoved = cp.hasMoved;
      value = cp.value;
      xCoord = cp.xCoord;
      yCoord = cp.yCoord;
   }

   /*
   This method inputs an new x index and a new y index and sets them as the
   new x coordinate and new y coordinate
   */
   public void movePieceShape(int xIndex, int yIndex)
   {
      xCoord = xIndex;
      yCoord = yIndex;
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

   /*
   This will change the boolean value of if a piece is selected.
   If selected is true it will be set to false and visa versa
   */
   public void toggleSelected()
   {
      selected = !selected;
   }

   /*
   This sets selected to true
   */
   public void select()
   {
      selected = true;
   }

   /*
   This sets selected to false
   */
   public void unselect()
   {
      selected = false;
   }

   /*
   this returns the boolean value of selected
   */
   public boolean isSelected()
   {
      return selected;
   }

   /*
   This method inputs an x and y coordinate. It returns false here, but
   is overwritten in subclasses
   */
   public boolean canMove(int x, int y)
   {
      return false;
   }

   /*
   This will input a chess peice and copy all the attributes to the 
   selected chess piece
   */
   public void copy(ChessPiece cp)
   {
      pieceName = cp.pieceName;
      color = cp.color;
      selected = cp.selected;
      hasMoved = cp.hasMoved;
      value = cp.value;
      xCoord = cp.xCoord;
      yCoord = cp.yCoord;
   }

   /*
   This method will create a copy of whatever chess piece calls the method
   */
   public ChessPiece copyOfThis()
   {
      if (this instanceof King)
      {
         King king = (King) this;
         return new King(king);
      }
      if (this instanceof Queen)
      {
         Queen queen = (Queen) this;
         return new Queen(queen);
      }
      if (this instanceof Rook)
      {
         Rook rook = (Rook) this;
         return new Rook(rook);
      }
      if (this instanceof Bishop)
      {
         Bishop bishop = (Bishop) this;
         return new Bishop(bishop);
      }
      if (this instanceof Knight)
      {
         Knight knight = (Knight) this;
         return new Knight(knight);
      }
      if (this instanceof Pawn)
      {
         Pawn pawn = (Pawn) this;
         return new Pawn(pawn);
      }
      else
      {
         return new ChessPiece();
      }
   }
   
   /*
   This returns the type of chess piece
   */
   public PieceType getType()
   {
      return type;
   }

   /*
   This returns a string of the piece name
   */
   public String toString()
   {
      return pieceName;
   }

   /*
   This method checks to see if two chess pieces are the same.
   If they are the same it will return true, false otherwise.
   */
   public boolean equals(Object obj)
   {
      if (obj instanceof ChessPiece)
      {
         ChessPiece cp = (ChessPiece) obj;
         return type == cp.type && color == cp.color;
      }
      return false;
   }
}