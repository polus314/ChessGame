package chessgame;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 This class creates a 64 square board that holds ChessPieces and enforces that
 only legal moves are made.

 @author jppolecat
 */
public class ChessBoard implements Comparator<ChessBoard>, Comparable<ChessBoard>
{

   public static final int NUM_ROWS = 8;
   public static final int NUM_COLS = 8;

   private ChessPiece[][] pieceArray;

   // TODO - move this up to AI?
   private ArrayList<ChessMove> wMoveList;
   private ArrayList<ChessMove> bMoveList;
   private int numWMoves;
   private int numBMoves;

   // TODO - move this up to AI as well?
   public float mobilityRating;
   public float materialRating;
   private int hangingPieces;

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

      wMoveList = new ArrayList<>();
      bMoveList = new ArrayList<>();
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

   /**
    Constructor that takes a 2D array of pieces and places them accordingly on
    this chessboard. The first index specifies the 'x' position on the board
    and the second index specifies the 'y' position on the board.

    @param pieces - array of pieces to place on board
    */
   public ChessBoard(ChessPiece[][] pieces)
   {
      pieceArray = new ChessPiece[8][8];
      for (int i = 0; i < 8; i++)
      {
         for (int j = 0; j < 8; j++)
         {
            setPieceAt(pieces[i][j], i, j);
         }
      }
   }

   /**
    Constructor that takes a list of pieces and places them on the board in
    the position specified by each piece's x and y values.

    @param pieces - list of pieces to place on board
    */
   public ChessBoard(List<ChessPiece> pieces)
   {
      pieceArray = new ChessPiece[8][8];
      for (ChessPiece piece : pieces)
      {
         setPieceAt(piece, piece.getX(), piece.getY());
      }
   }

   /**
    Returns all the pieces on the board in a 2D array. The first index
    specifies the 'x' position on the board and the second index specifies the
    'y' position on the board.

    @return ChessPiece[][] - array of pieces
    */
   public ChessPiece[][] getPiecesArray()
   {
      ChessPiece[][] copy = new ChessPiece[8][8];
      for (int i = 0; i < 8; i++)
      {
         for (int j = 0; j < 8; j++)
         {
            if (getPieceAt(i, j) != null)
            {
               copy[i][j] = getPieceAt(i, j);
            }
         }
      }
      return copy;
   }

   /**
    Returns all the pieces on the board in a list.

    @return List - list of pieces
    */
   public List<ChessPiece> getPiecesList()
   {
      ArrayList<ChessPiece> tempList = new ArrayList<>();
      for (int i = 0; i < 8; i++)
      {
         for (int j = 0; j < 8; j++)
         {
            if (getPieceAt(i, j) != null)
            {
               tempList.add(getPieceAt(i, j));
            }
         }
      }
      return tempList;
   }

   @Override
   public int compare(ChessBoard cb1, ChessBoard cb2)
   {
      if (cb1.materialRating > cb2.materialRating)
      {
         return 1;
      }
      if (cb1.materialRating == cb2.materialRating)
      {
         if (cb1.mobilityRating > cb2.mobilityRating)
         {
            return 1;
         }
         if (cb1.mobilityRating == cb2.mobilityRating)
         {
            return 0;
         }
      }
      return -1;
   }
   /**
    Returns the piece at the given board coordinates

    @param row - row of piece, rows start at 0
    @param col - column of piece, columns start at 0
    @return ChessPiece - chess piece that is at the given coordinates
    */
   public ChessPiece getPieceAt(int row, int col)
   {
      return pieceArray[row][col];
   }

   /**
    Sets the piece at the given board coordinates

    @param cp - chess piece to place at these coordinates
    @param row - row of piece, rows start at 0
    @param col - column of piece, columns start at 0
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
      if (cp == null || pieceArray[xDest][yDest] == null) // this might not be
      {
         return false;                                   // correct logically
      }
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
               && pathIsClear(cp, xDest, yDest)
               && spaceIsOpen(xDest, yDest, cp.getColor()))
         {
            return true;
         }
      }
      return false;
   }

   /**
    Determines if the King of the given color is able to castle king-side.
    Factors considered are: - Open path - King and Rook have not moved - King
    is not in check

    @param color - player who is trying to castle
    @return boolean - true if castling is legal, false otherwise
    */
   public boolean canCastleKS(PieceColor color)
   {
      int y = color == PieceColor.BLACK ? 0 : 7;

      //check to see if King and Rook are present and unmoved
      ChessPiece cp = getPieceAt(4, y);
      if (!(cp instanceof King) || cp.hasMoved)
      {
         return false;
      }

      cp = getPieceAt(7, y);
      if (!(cp instanceof Rook) || cp.hasMoved)
      {
         return false;
      }

      if (!spaceIsEmpty(5, y) || !spaceIsEmpty(6, y))
      {
         return false;
      }

      for (int i = 0; i < 8; i++)
      {
         for (int j = 0; j < 8; j++)
         {
            if (spaceIsEmpty(i, j))
            {
               continue;
            }
            cp = getPieceAt(i, j);
            if (cp.getColor() == color.opposite()
                  && (canCapture(cp, 4, y)
                  || canCapture(cp, 5, y)
                  || canCapture(cp, 6, y)))
            {
               return false;
            }
         }
      }
      return true;
   }

   /**
    Determines if the King of the given color is able to castle queen-side.
    Factors considered are: - Open path - King and Rook have not moved - King
    is not in check

    @param color - player who is trying to castle
    @return boolean - true if castling is legal, false otherwise
    */
   public boolean canCastleQS(PieceColor color)
   {
      int y = color == PieceColor.BLACK ? 0 : 7;

      //check to see if King and Rook are present and unmoved
      ChessPiece cp = getPieceAt(4, y);
      if (!(cp instanceof King) || cp.hasMoved)
      {
         return false;
      }

      cp = getPieceAt(0, y);
      if (!(cp instanceof Rook) || cp.hasMoved)
      {
         return false;
      }

      if (!spaceIsEmpty(1, y) || !spaceIsEmpty(2, y) || !spaceIsEmpty(3, y))
      {
         return false;
      }
      for (int i = 0; i < 8; i++)
      {
         for (int j = 0; j < 8; j++)
         {
            if (spaceIsEmpty(i, j))
            {
               continue;
            }

            cp = getPieceAt(i, j);
            if (cp.getColor() == color.opposite()
                  && (canCapture(cp, 2, y)
                  || canCapture(cp, 3, y)
                  || canCapture(cp, 4, y)))
            {
               return false;
            }
         }
      }
      return true;
   }

   public boolean capturePiece(ChessPiece attacker, int x, int y)
   {
      int xSel = attacker.getX();
      int ySel = attacker.getY();
      PieceColor attColor = getPieceAt(xSel, ySel).getColor();
      PieceColor captureColor = getPieceAt(x, y).getColor();
      if (getPieceAt(xSel, ySel) instanceof Pawn
            && attColor != captureColor)
      {
         if (ySel + 1 == y && attColor == PieceColor.BLACK)
         {
            if (xSel + 1 == x || xSel - 1 == x)
            {
               replacePiece(xSel, ySel, x, y);
               return true;
            }
         }
         if (ySel - 1 == y && attColor == PieceColor.WHITE)
         {
            if (xSel + 1 == x || xSel - 1 == x)
            {
               replacePiece(xSel, ySel, x, y);
               return true;
            }
         }
         return false;
      }

      ChessBoard movedBoard = new ChessBoard(this);
      if (getPieceAt(xSel, ySel).canMove(x, y))
      {
         if (pathIsClear(attacker, x, y) && attColor != captureColor)
         {
            movedBoard.replacePiece(xSel, ySel, x, y);
            if (!movedBoard.checkForCheck(attColor))
            {
               replacePiece(xSel, ySel, x, y);
               if (getPieceAt(x, y) instanceof King
                     || getPieceAt(x, y) instanceof Rook)
               {
                  getPieceAt(x, y).hasMoved = true;
               }
               //playerToMove = playerToMove.opposite();
               return true;
            }
            return false;
         }
      }
      return false;
   }

   /**
    Attempts to castle according to the given move. Checks to make sure
    castling is legal from this position.

    @param move - move that is trying to castle
    @return boolean - whether castling was successful or not
    */
   public boolean castle(ChessMove move)
   {
      ChessPiece castler = move.piece;
      if (castler instanceof King)
      {
         int y = castler.getColor() == PieceColor.WHITE ? 7 : 0;

         if (canCastleKS(castler.getColor()) && move.getXDest() == 6)
         {
            move.setMoveType(MoveType.CASTLE_KS);
            replacePiece(4, y, 6, y);
            replacePiece(7, y, 5, y);
            return true;
         }
         else if (canCastleQS(castler.getColor()) && move.getXDest() == 2)
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
      King king = findKing(color);
      if (king == null)
      {
         return false;
      }

      for (int i = 0; i < 8; i++)
      {
         for (int j = 0; j < 8; j++)
         {
            if (canCapture(getPieceAt(i, j), king.xCoord, king.yCoord))
            {
               hasCheck = true;
            }
         }
      }
      return hasCheck;
   }
   
   /**
    This method finds all legal moves for this piece on this board

    @param cb
    @param cp
    @return
    */
   public ArrayList<ChessMove> findMoves(ChessPiece cp)
   {
      ArrayList<ChessMove> moveList = new ArrayList<>();
      int numMoves = 0;
      PieceColor color = cp.getColor();
      int xi = cp.xCoord;
      int yi = cp.yCoord;
      for (int xf = 0; xf < 8; xf++)
      {
         for (int yf = 0; yf < 8; yf++)
         {
            
            ChessMove possMove = new ChessMove(cp, xf, yf);
            //if move puts mover in check, disregard it
            if (leadsToCheck(possMove))
            {
               ;
            }
            else if (cp.canMove(xf, yf) //piece moves this way
                  && pathIsClear(cp, xf, yf) //no pieces in the way
                  && spaceIsEmpty(xf, yf))      //space is empty
            {
               moveList.add(possMove);
            }
            else if (cp.canMove(xf, yf)
                  && pathIsClear(cp, xf, yf)
                  && spaceIsOpen(xf, yf, cp.getColor()) //piece is opposite color
                  && !(cp instanceof Pawn))                 //pawns capture differently
            {                                               //than they move
               possMove.captures = true;
               moveList.add(possMove);
            }
            else if (cp instanceof Pawn)
            {
               //this is how pawns capture
               if (color == PieceColor.WHITE && yi - yf == 1
                     && (xi - xf == 1 || xf - xi == 1)
                     && getPieceAt(xf, yf) != null
                     && getPieceAt(xf, yf).getColor() == PieceColor.BLACK)
               {
                  possMove.captures = true;
                  moveList.add(possMove);
               }
               if (color == PieceColor.BLACK && yf - yi == 1
                     && (xi - xf == 1 || xf - xi == 1)
                     && getPieceAt(xf, yf) != null
                     && getPieceAt(xf, yf).getColor() == PieceColor.WHITE)
               {
                  possMove.captures = true;
                  moveList.add(possMove);
               }
            }
         }
      }
      return moveList;
   }
   
   /**
    This method finds all possible moves for the given color

    @param color
    @return list of moves
    */
   public ArrayList<ChessMove> findAllMoves(PieceColor color)
   {
      ArrayList<ChessMove> moveList = new ArrayList<>();
      for (int i = 0; i < 8; i++)
      {
         for (int j = 0; j < 8; j++)
         {
            ChessPiece current = getPieceAt(i,j);
            if(current == null)
               continue;
            if (current.getColor() == color)
            {
               moveList.addAll(findMoves(current));
            }
         }
      }
      return moveList;
   }
   
      /**
    This method determines whether a move will result in the moving player
    being in check.

    @param cb - chess board to execute move on
    @param cm - move to execute
    @return   - whether or not check is a result of this move
    */
   public boolean leadsToCheck(ChessMove cm)
   {
      ChessBoard temp = advancePosition(cm);
      return temp.checkForCheck(cm.piece.getColor());
   }
   
   /**
    This method makes the given move and returns a copy of
    the resulting board.

    @param cm - move to execute
    @return ChessBoard - resulting board position after the move
    */
   public ChessBoard advancePosition(ChessMove cm)
   {
      ChessBoard nextNode = new ChessBoard(this);
      int xi, xf, yi, yf;
      xi = cm.piece.xCoord;
      yi = cm.piece.yCoord;
      xf = cm.getXDest();
      yf = cm.getYDest();
      nextNode.replacePiece(xi, yi, xf, yf);
      if (nextNode.getPieceAt(xf, yf) instanceof King // castling concerns
            || nextNode.getPieceAt(xf, yf) instanceof Rook)
      {
         nextNode.getPieceAt(xf, yf).hasMoved = true;
      }
      nextNode.mobilityRating = cm.getMobilityRating();
      nextNode.materialRating = cm.getMaterialRating();
      return nextNode;
   }
   
   /**
   Checks the current board position to see if one side is checkmated
   
   @param playerToMove - color whose move is next
   @return boolean   - whether the game on the board is over
   */
   public boolean checkForMate(PieceColor playerToMove)
   {
      // don't want to change the instance variable gameBoard, so make a copy
      ChessBoard gammyBoard = new ChessBoard(this);
      // TODO - isn't there a method that returns number of moves?
      int numMoves = gammyBoard.findAllMoves(playerToMove).size();
//      int numBMoves = gammyBoard.findAllMoves(PieceColor.BLACK).size();

      if (numMoves == 0 && checkForCheck(playerToMove))
      {
         return true;
      }
      return false;
   }

   /**
   Custom find method because using .equals won't work if you don't know the
   coordinates ahead of time
   
   @param c - color of King to find
   @return King - king of this color or null if not found
   */
   private King findKing(PieceColor c)
   {
      for (int i = 0; i < 8; i++)
      {
         for (int j = 0; j < 8; j++)
         {
            ChessPiece current = getPieceAt(i, j);
            if (current == null)
            {
               continue;
            }
            if (current instanceof King && current.getColor() == c)
            {
               return (King) current;
            }
         }
      }
      return null;
   }

   /**
    Helper method to check all squares that a bishop will move through when
    moving from (xi,yi) to (x,y)

    @param xi - original x coordinate
    @param yi - original y coordinate
    @param x - destination x coordinate
    @param y - destination y coordinate
    @return true - whether bishop has an unobstructed path
    */
   private boolean clearPathBishop(int xi, int yi, int x, int y)
   {
      for (int i = 1; i < 8; i++) //see Bishop.canMove(int,int)
      {
         if (xi + i == x && yi + i == y)
         {
            for (int j = 1; j < i; j++)
            {
               if (!spaceIsEmpty(xi + j, yi + j))
               {
                  return false;
               }
            }
         }
         if (xi + i == x && yi - i == y)
         {
            for (int j = 1; j < i; j++)
            {
               if (!spaceIsEmpty(xi + j, yi - j))
               {
                  return false;
               }
            }
         }
         if (xi - i == x && yi + i == y)
         {
            for (int j = 1; j < i; j++)
            {
               if (!spaceIsEmpty(xi - j, yi + j))
               {
                  return false;
               }
            }
         }
         if (xi - i == x && yi - i == y)
         {
            for (int j = 1; j < i; j++)
            {
               if (!spaceIsEmpty(xi - j, yi - j))
               {
                  return false;
               }
            }
         }
      }
      return true;
   }

   /**
    Helper method to check all squares that a rook will move through when
    moving from (xi,yi) to (x,y)

    @param xi - original x coordinate
    @param yi - original y coordinate
    @param x - destination x coordinate
    @param y - destination y coordinate
    @return true - whether rook has an unobstructed path
    */
   private boolean clearPathRook(int xi, int yi, int x, int y)
   {
      if (y < yi && xi == x)
      {
         for (int i = y + 1; i < yi; i++)
         {
            if (!spaceIsEmpty(x, i))
            {
               return false;
            }
         }
      }
      if (yi < y && xi == x)
      {
         for (int i = yi + 1; i < y; i++)
         {
            if (!spaceIsEmpty(x, i))
            {
               return false;
            }
         }
      }
      if (x < xi && yi == y)
      {
         for (int i = x + 1; i < xi; i++)
         {
            if (!spaceIsEmpty(i, y))
            {
               return false;
            }
         }
      }
      if (xi < x && yi == y)
      {
         for (int i = xi + 1; i < x; i++)
         {
            if (!spaceIsEmpty(i, y))
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
   @Override
   public int compareTo(ChessBoard cb)
   {
      if (materialRating < cb.materialRating)
      {
         return -1;
      }
      if (materialRating == cb.materialRating)
      {
         if (mobilityRating < cb.mobilityRating)
         {
            return -1;
         }
         if (mobilityRating == cb.mobilityRating)
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
      for (int i = 0; i < 8; i++)
      {
         for (int j = 0; j < 8; j++)
         {
            if (getPieceAt(i, j) != null)
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
      if (!(obj instanceof ChessBoard))
      {
         return false;
      }
      else
      {
         ChessBoard cb = (ChessBoard) obj;
         ChessPiece lhs, rhs;
         for (int i = 0; i < 8; i++)
         {
            for (int j = 0; j < 8; j++)
            {
               lhs = getPieceAt(i, j);
               rhs = cb.getPieceAt(i, j);
               if (lhs == null && rhs == null)
               {
                  continue;
               }
               if (lhs == null || rhs == null)
               {
                  return false;
               }
               if (!lhs.equals(rhs))
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
            if (cp.equals(getPieceAt(i, j)))
            {
               return getPieceAt(i, j);
            }
         }
      }
      return null;
   }

   /**
    Moves the given piece to the destination coordinates, if it is a legal
    move, and updates which player is next to move. If move is not legal,
    returns false and doesn't change whose move is next.

    @param mover - piece trying to move
    @param xf - destination x coordinate
    @param yf - destination y coordinate
    @return boolean - whether or not a legal move was executed
    */
   public boolean movePiece(ChessPiece mover, int xf, int yf)
   {
      int xi = mover.getX();
      int yi = mover.getY();
      ChessBoard movedBoard = new ChessBoard(this);
      if (mover.canMove(xf, yf) && pathIsClear(mover, xf, yf))
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
            return true;
         }
         return false;
      }
      return false;
   }

   /**
    Checks to see if there any pawns that have reached the other side of the
    board and thus need to be promoted.

    @return ChessPiece - the pawn that needs promoting, else new ChessPiece()
    */
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
      return null;
   }

   /**
    This method determines if a chess piece has any pieces blocking its way.
    It is assumed that the coordinates constitute a valid move for that piece

    @param mover
    @param x
    @param y
    @return true if the piece has nothing blocking it, false otherwise
    */
   public boolean pathIsClear(ChessPiece mover, int x, int y)
   {
      boolean clear = true;
      int xi = mover.getX();
      int yi = mover.getY();
      Class c = mover.getClass();
      if (c == Pawn.class)
      {
         // check to make sure initial 2-space move is unobstructed
         if (mover.getColor() == PieceColor.WHITE && yi == 6 && y == 4)
         {
            clear = spaceIsEmpty(xi, yi - 1);
         }
         if (mover.getColor() == PieceColor.BLACK && yi == 1 && y == 3)
         {
            clear = spaceIsEmpty(xi, yi + 1);
         }
      }
      else if (c == Rook.class)
      {
         clear = clearPathRook(xi, yi, x, y);
      }
      else if (c == Bishop.class)
      {
         clear = clearPathBishop(xi, yi, x, y);
      }
      else if (c == Queen.class)
      {
         //Bishop combined with Rook clearPath methods
         clear = clearPathBishop(xi, yi, x, y) && clearPathRook(xi, yi, x, y);
      }
      else
      {
         clear = true; // King and Knight always have a clear path
      }

      return clear;
   }

   public boolean spaceIsEmpty(int x, int y)
   {
      return pieceArray[x][y] == null;
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
      return spaceIsEmpty(x, y) || pieceArray[x][y].getColor() != color;
   }

   public void replacePiece(int xi, int yi, int xf, int yf)
   {
      if (getPieceAt(xi, yi) == null)
      {
         return;
      }
      getPieceAt(xi, yi).movePiece(xf, yf);
      setPieceAt(getPieceAt(xi, yi), xf, yf);
      setPieceAt(null, xi, yi);
   }

   public void copy(ChessBoard cb)
   {
      ChessPiece current;
      for (int i = 0; i < 8; i++)
      {
         for (int j = 0; j < 8; j++)
         {
            current = cb.getPieceAt(i, j);
            if (current != null)
            {
               pieceArray[i][j] = current.copyOfThis();
            }
         }
      }
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
}
