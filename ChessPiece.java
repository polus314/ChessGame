package chessgame;

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

   public void movePieceShape(int xIndex, int yIndex)
   {
      xCoord = xIndex;
      yCoord = yIndex;
   }

   public int getX()
   {
      return xCoord;
   }

   public int getY()
   {
      return yCoord;
   }

   public PieceColor getColor()
   {
      return color;
   }

   public void toggleSelected()
   {
      selected = !selected;
   }

   public void select()
   {
      selected = true;
   }

   public void unselect()
   {
      selected = false;
   }

   public boolean isSelected()
   {
      return selected;
   }

   public boolean canMove(int x, int y)
   {
      return false;
   }

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

   public ChessPiece clone() throws CloneNotSupportedException
   {
      super.clone();
      return new ChessPiece(this);
   }

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
   
   public PieceType getType()
   {
      return type;
   }

   public String toString()
   {
      return pieceName;
   }

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