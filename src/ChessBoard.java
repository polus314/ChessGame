package chessgame;

import java.util.ArrayList;

/**
 This class creates a 64 square checkerboard using an array that can hold
 chess pieces.

 @author jppolecat
 */
public class ChessBoard
{
   // TODO - make not all these public, pretty smelly
   private ChessPiece[][] pieceArray;
   public boolean gameOver;
   public PieceColor playerToMove;
   public ArrayList<ChessMove> wMoveList;
   public ArrayList<ChessMove> bMoveList;
   public int numWMoves;
   public int numBMoves;
   public float mobilityRating;
   public float materialRating;
   public int hangingPieces;

   /**
    This constructor sets up a standard chess game on the board
    */
   public ChessBoard()
   {
      pieceArray = new ChessPiece[8][8];
      //set up pieces on each side
      for (int i = 0; i < 8; i++)
      {
         pieceArray[i][6] = new Pawn(PieceColor.WHITE, i, 6);
         pieceArray[i][1] = new Pawn(PieceColor.BLACK, i, 1);
      }

      for (int i = 0; i < 8; i += 7)
      {
         pieceArray[i][7] = new Rook(PieceColor.WHITE, i, 7);
         pieceArray[i][0] = new Rook(PieceColor.BLACK, i, 0);
      }

      for (int i = 1; i < 7; i += 5)
      {
         pieceArray[i][7] = new Knight(PieceColor.WHITE, i, 7);
         pieceArray[i][0] = new Knight(PieceColor.BLACK, i, 0);
      }

      for (int i = 2; i < 6; i += 3)
      {
         pieceArray[i][7] = new Bishop(PieceColor.WHITE, i, 7);
         pieceArray[i][0] = new Bishop(PieceColor.BLACK, i, 0);
      }

      pieceArray[3][7] = new Queen(PieceColor.WHITE, 3, 7);
      pieceArray[4][7] = new King(PieceColor.WHITE, 4, 7);

      pieceArray[3][0] = new Queen(PieceColor.BLACK, 3, 0);
      pieceArray[4][0] = new King(PieceColor.BLACK, 4, 0);

      for (int i = 2; i < 6; i++)
      {
         for (int j = 0; j < 8; j++)
         {
            pieceArray[j][i] = new ChessPiece();
         }
      }

      wMoveList = new ArrayList<>();
      bMoveList = new ArrayList<>();
      gameOver = false;
      playerToMove = PieceColor.WHITE;
   }

   /**
    Copy constructor

    @param template
    */
   public ChessBoard(ChessBoard template)
   {
      pieceArray = new ChessPiece[8][8];
      copy(template);
   }

   public ChessBoard(PieceType type)
   {
      //implement setting up only white or black?

      if (type == PieceType.EMPTY)
      {
         for (int i = 0; i < 8; i++)
         {
            for (int j = 0; j < 8; j++)
            {
               pieceArray[i][j] = new ChessPiece();
            }
         }
      }
   }
   
   /**
   Returns the piece at the given board coordinates
   @param row           - row of piece, rows start at 0
   @param col           - column of piece, columns start at 0
   @return ChessPiece   - chess piece that is at the given coordinates
   */
   public ChessPiece getPieceAt(int row, int col)
   {
      return pieceArray[row][col];
   }
   
   /**
   Sets the piece at the given board coordinates
   @param cp   - chess piece to place at these coordinates
   @param row  - row of piece, rows start at 0
   @param col  - column of piece, columns start at 0
   */
   public void setPieceAt(ChessPiece cp, int row, int col)
   {
      pieceArray[row][col] = cp;
   }

   /**
    This method determines whether a piece can capture another piece at the
    given coordinates, basically whether the piece can move there, except for
    pawns. Used in checkForCheck(), ironically doesn't check for Check for
    this move

    @param cp
    @param xDest
    @param yDest
    @return
    */
   public boolean canCapture(ChessPiece cp, int xDest, int yDest)
   {
      if (cp instanceof Pawn
            && pieceArray[xDest][yDest].getColor() == cp.getColor().opposite())
      {
         if (cp.yCoord + 1 == yDest && cp.color == PieceColor.BLACK)
         {
            if (cp.xCoord + 1 == xDest || cp.xCoord - 1 == xDest)
            {
               return true;
            }
         }
         if (cp.yCoord - 1 == yDest && cp.color == PieceColor.WHITE)
         {
            if (cp.xCoord + 1 == xDest || cp.xCoord - 1 == xDest)
            {
               return true;
            }
         }
         return false;
      }
      else
      {
         if (cp.canMove(xDest, yDest)
               && pathIsClear(xDest, yDest)
               && spaceIsOpen(xDest, yDest, cp.getColor()))
         {
            return true;
         }
      }
      return false;
   }

   public boolean canCastleKS(PieceColor color)
   {
      int y = color == PieceColor.BLACK ? 0 : 7;
      
      //check to see if King and Rook are present and unmoved
      ChessPiece cp = pieceArray[4][y];
      if (cp.type != PieceType.KING || cp.hasMoved == true)
      {
         return false;
      }

      cp = pieceArray[7][y];
      if (cp.type != PieceType.ROOK || cp.hasMoved == true)
      {
         return false;
      }

      if (!spaceIsEmpty(5, y) || !spaceIsEmpty(6, y))
      {
         return false;
      }

      pieceArray[4][y].unselect();
      for (int i = 0; i < 8; i++)
      {
         for (int j = 0; j < 8; j++)
         {
            pieceArray[i][j].select();
            if (pieceArray[i][j].getColor() == color.opposite()
                  && (canCapture(pieceArray[i][j], 4, y)
                  || canCapture(pieceArray[i][j], 5, y)
                  || canCapture(pieceArray[i][j], 6, y)))
            {
               pieceArray[i][j].unselect();
               return false;
            }
            pieceArray[i][j].unselect();
         }
      }
      pieceArray[4][y].select();
      System.out.println("Level 4");
      return true;
   }

   public boolean canCastleQS(PieceColor color)
   {
      int y = color == PieceColor.BLACK ? 0 : 7;
      
      //check to see if King and Rook are present and unmoved
      ChessPiece cp = pieceArray[4][y];
      if (cp.type != PieceType.KING || cp.hasMoved == true)
      {
         return false;
      }

      cp = pieceArray[0][y];
      if (cp.type != PieceType.ROOK || cp.hasMoved == true)
      {
         return false;
      }

      if (!spaceIsEmpty(1, y) || !spaceIsEmpty(2, y) || !spaceIsEmpty(3, y))
      {
         return false;
      }
      pieceArray[4][y].unselect();
      for (int i = 0; i < 8; i++)
      {
         for (int j = 0; j < 8; j++)
         {
            pieceArray[i][j].select();
            if (pieceArray[i][j].getColor() == color.opposite()
                  && (canCapture(pieceArray[i][j], 2, y)
                  || canCapture(pieceArray[i][j], 3, y)
                  || canCapture(pieceArray[i][j], 4, y)))
            {
               pieceArray[i][j].unselect();
               return false;
            }
            pieceArray[i][j].unselect();
         }
      }
      pieceArray[4][y].select();
      return true;
   }

   public boolean capturePiece(int x, int y)
   {
      ChessPiece selected = findSelected();
      int xSel = selected.getX();
      int ySel = selected.getY();
      PieceColor selColor = pieceArray[xSel][ySel].getColor();
      PieceColor captureColor = pieceArray[x][y].getColor();
      if (pieceArray[xSel][ySel] instanceof Pawn
            && selColor != captureColor)
      {
         if (ySel + 1 == y && selColor == PieceColor.BLACK)
         {
            if (xSel + 1 == x || xSel - 1 == x)
            {
               replacePiece(xSel, ySel, x, y);
               playerToMove = playerToMove.opposite();
               return true;
            }
         }
         if (ySel - 1 == y && selColor == PieceColor.WHITE)
         {
            if (xSel + 1 == x || xSel - 1 == x)
            {
               replacePiece(xSel, ySel, x, y);
               playerToMove = playerToMove.opposite();
               return true;
            }
         }
         return false;
      }

      ChessBoard movedBoard = new ChessBoard(this);
      if (pieceArray[xSel][ySel].canMove(x, y))
      {
         if (pathIsClear(x, y) && selColor != captureColor)
         {
            movedBoard.replacePiece(xSel, ySel, x, y);
            if (!movedBoard.checkForCheck(selColor))
            {
               replacePiece(xSel, ySel, x, y);
               if (pieceArray[x][y] instanceof King
                     || pieceArray[x][y] instanceof Rook)
               {
                  pieceArray[x][y].hasMoved = true;
               }
               playerToMove = playerToMove.opposite();
               return true;
            }
            System.out.println("Can't capture into check!");
            return false;
         }
      }
      return false;
   }

   public boolean castle(ChessMove move)
   {
      ChessPiece castler = move.piece;
      if (castler.type == PieceType.KING)
      {
         int y = castler.getColor() == PieceColor.WHITE ? 7 : 0;

         if (canCastleKS(castler.getColor()) && move.getXDest() == 6)
         {
            move.setMoveType(MoveType.CASTLE_KS);
            replacePiece(4, y, 6, y);
            replacePiece(7, y, 5, y);
            return true;
         }
         if (canCastleQS(castler.getColor()) && move.getXDest() == 2)
         {
            move.setMoveType(MoveType.CASTLE_QS);
            replacePiece(4, y, 2, y);
            replacePiece(0, y, 3, y);
            return true;
         }
      }
      return false;
   }

   /**
    This method determines if the player of the given color is in check, if no
    King is found, returns false

    @param color
    @return true if opposing player can capture King from this position
    */
   public boolean checkForCheck(PieceColor color)
   {
      boolean hasCheck = false;
      ChessPiece cp = find(new King(color));
      if (cp.equals(new ChessPiece()))
      {
         return false;
      }
      
      for (int i = 0; i < 8; i++)
      {
         for (int j = 0; j < 8; j++)
         {
            pieceArray[i][j].select();
            if (canCapture(pieceArray[i][j], cp.xCoord, cp.yCoord))
            {
               hasCheck = true;
            }
            pieceArray[i][j].unselect();
         }
      }
      return hasCheck;
   }

   private boolean clearPathBishop(int xi, int yi, int x, int y)
   {
      for (int i = 1; i < 8; i++) //see Bishop.canMove(int,int)
      {
         if (xi + i == x && yi + i == y)
         {
            for (int j = 1; j < i; j++)
            {
               if (!pieceArray[xi + j][yi + j].equals(new ChessPiece()))
               {
                  return false;
               }
            }
         }
         if (xi + i == x && yi - i == y)
         {
            for (int j = 1; j < i; j++)
            {
               if (!pieceArray[xi + j][yi - j].equals(new ChessPiece()))
               {
                  return false;
               }
            }
         }
         if (xi - i == x && yi + i == y)
         {
            for (int j = 1; j < i; j++)
            {
               if (!pieceArray[xi - j][yi + j].equals(new ChessPiece()))
               {
                  return false;
               }
            }
         }
         if (xi - i == x && yi - i == y)
         {
            for (int j = 1; j < i; j++)
            {
               if (!pieceArray[xi - j][yi - j].equals(new ChessPiece()))
               {
                  return false;
               }
            }
         }
      }
      return true;
   }

   private boolean clearPathRook(int xi, int yi, int x, int y)
   {
      if (y < yi && xi == x)
      {
         for (int i = y + 1; i < yi; i++)
         {
            if (!pieceArray[x][i].equals(new ChessPiece()))
            {
               return false;
            }
         }
      }
      if (yi < y && xi == x)
      {
         for (int i = yi + 1; i < y; i++)
         {
            if (!pieceArray[x][i].equals(new ChessPiece()))
            {
               return false;
            }
         }
      }
      if (x < xi && yi == y)
      {
         for (int i = x + 1; i < xi; i++)
         {
            if (!pieceArray[i][y].equals(new ChessPiece()))
            {
               return false;
            }
         }
      }
      if (xi < x && yi == y)
      {
         for (int i = xi + 1; i < x; i++)
         {
            if (!pieceArray[i][y].equals(new ChessPiece()))
            {
               return false;
            }
         }
      }
      return true;
   }

   /**
    This method compares two Chess Boards, useful for sorting

    @param cb
    @return -1 if this is worse, 0 if equal, 1 if better than param
    */
   public int compareTo(ChessBoard cb)
   {
      if (cb.materialRating > materialRating)
      {
         return -1;
      }
      if (cb.materialRating == materialRating)
      {
         if (cb.mobilityRating > mobilityRating)
         {
            return -1;
         }
         if (cb.mobilityRating == mobilityRating)
         {
            return 0;
         }
      }
      return 1;
   }

   /**
    An apparently useless method I wrote early in making this game

    @return int - number of pieces on the board
    */
   public int countPieces()
   {
      int count = 0;
      ChessPiece blank = new ChessPiece();
      for (int i = 0; i < 8; i++)
      {
         for (int j = 0; j < 8; j++)
         {
            if (!pieceArray[i][j].equals(blank))
            {
               count++;
            }
         }
      }
      return count;
   }

   /**
    Two ChessBoards are equal if each square has the same piece on both

    @param obj
    @return
    */
   @Override
   public boolean equals(Object obj)
   {
      if(!(obj instanceof ChessBoard))
      {
         return false;
      } 
      else
      {
         ChessBoard cb = (ChessBoard) obj;
         for (int i = 0; i < 8; i++)
         {
            for (int j = 0; j < 8; j++)
            {
               if (!pieceArray[i][j].equals(cb.pieceArray[i][j]))
               {
                  return false;
               }
            }
         }
      }
      return true;
   }

   /**
    Finds a piece on the ChessBoard. A piece is the same if it is the same
    type and color.

    @param cp
    @return first ChessPiece on the ChessBoard that matches cp, or "empty"
    piece
    */
   public ChessPiece find(ChessPiece cp)
   {
      for (int i = 0; i < 8; i++)
      {
         for (int j = 0; j < 8; j++)
         {
            if (cp.equals(pieceArray[i][j]))
            {
               return pieceArray[i][j];
            }
         }
      }
      return new ChessPiece();
   }

   public ChessPiece findSelected()
   {
      for (int i = 0; i < 8; i++)
      {
         for (int j = 0; j < 8; j++)
         {
            if (pieceArray[i][j].selected)
            {
               return pieceArray[i][j];
            }
         }
      }
      return new ChessPiece();
   }

   public boolean movePiece(int xf, int yf)
   {
      int xi, yi;
      xi = yi = -1;
      for (int i = 0; i < 8; i++)
      {
         for (int j = 0; j < 8; j++)
         {
            if (pieceArray[i][j].selected)
            {
               xi = i;
               yi = j;
            }
         }
      }
      if (xi == -1)
      {
         return false;
      }
      ChessPiece mover = pieceArray[xi][yi];

      ChessBoard movedBoard = new ChessBoard(this);
      if (mover.canMove(xf, yf) && pathIsClear(xf, yf))
      {
         movedBoard.replacePiece(xi, yi, xf, yf);
         if (!movedBoard.checkForCheck(mover.getColor()))
         {
            replacePiece(xi, yi, xf, yf);
            if (mover instanceof King // castling concerns
                  || mover instanceof Rook)
            {
               mover.hasMoved = true;
            }
            playerToMove = playerToMove.opposite();
            return true;
         }
         System.out.println("Can't move into check!!");
         return false;
      }
      return false;
   }

   public ChessPiece needPromotion()
   {
      for (int i = 0; i < 8; i++)
      {
         if (pieceArray[i][0] instanceof Pawn)
         {
            return pieceArray[i][0];
         }
         if (pieceArray[i][7] instanceof Pawn)
         {
            return pieceArray[i][7];
         }
      }
      return new ChessPiece();
   }

   /**
    This method determines if a chess piece has any pieces blocking its way.
    It is assumed that the coordinates constitute a valid move for that piece
    and the piece being considered MUST BE SELECTED

    @param x
    @param y
    @return true if the piece has nothing blocking it, false otherwise
    */
   public boolean pathIsClear(int x, int y)
   {
      boolean clear = true;
      ChessPiece selected = findSelected();
      int xi = selected.getX();
      int yi = selected.getY();
      switch (findSelected().type)
      {
         case PAWN:
         {
            if (findSelected().getColor() == PieceColor.WHITE && yi == 6)
            {
               clear = pieceArray[xi][yi - 1].equals(new ChessPiece());
            }
            if (findSelected().getColor() == PieceColor.BLACK && yi == 1)
            {
               clear = pieceArray[xi][yi + 1].equals(new ChessPiece());
            }
            break;
         }
         case ROOK:
         {
            clear = clearPathRook(xi, yi, x, y);
            break;
         }
         case BISHOP:
         {
            clear = clearPathBishop(xi, yi, x, y);
            break;
         }
         case QUEEN: //Bishop combined with Rook clearPath methods
         {
            clear = clearPathBishop(xi, yi, x, y) && clearPathRook(xi, yi, x, y);
            break;
         }
      }
      return clear;
   }

   public boolean spaceIsEmpty(int x, int y)
   {
      return pieceArray[x][y].equals(new ChessPiece());
   }

   /**
    Like spaceIsEmpty but also allows a piece of the opposite color to be on
    the square

    @param x
    @param y
    @param color
    @return
    */
   public boolean spaceIsOpen(int x, int y, PieceColor color)
   {
      return pieceArray[x][y].equals(new ChessPiece())
            || pieceArray[x][y].getColor() != color;
   }

   // TODO - not deal with graphics in this class
   public void replacePiece(int xi, int yi, int xf, int yf)
   {
      pieceArray[xi][yi].movePieceShape(xf, yf); //graphical position
      pieceArray[xf][yf] = pieceArray[xi][yi];//logical position
      pieceArray[xi][yi] = new ChessPiece();
      pieceArray[xf][yf].selected = false;
   }

   public void setPlayerToMove(PieceColor player)
   {
      playerToMove = player;
   }

   public void copy(ChessBoard cb)
   {
      for (int i = 0; i < 8; i++)
      {
         for (int j = 0; j < 8; j++)
         {
            pieceArray[i][j] = cb.pieceArray[i][j].copyOfThis();
         }
      }
      gameOver = cb.gameOver;
      playerToMove = cb.playerToMove;
   }

   public String toString()
   {
      String string = "";
      for (int i = 0; i < 8; i++)
      {
         string += "\n";
         for (int j = 0; j < 8; j++)
         {
            string = string + pieceArray[j][i].toString() + " ";
         }
      }
      return string;
   }

   /**
    This method tells you whose turn it is to make the next move.

    @return PieceColor of player whose turn it is
    */
   public PieceColor whoseTurn()
   {
      return playerToMove;
   }
}
