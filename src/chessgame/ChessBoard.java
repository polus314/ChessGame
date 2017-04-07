package chessgame;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 This class creates a 64 square board that holds ChessPieces and enforces that
 only legal moves are made, e.g. pawns cannot move backwards, a player does
 not move into check, or a player doesn't castle after moving their king.

 @author John Polus
 */
public class ChessBoard implements Comparator<ChessBoard>, Comparable<ChessBoard>
{

   // Length of board along x - axis, in number of squares

   public static final int WIDTH = 8;
   // Length of board along y - axis, in number of squares
   public static final int HEIGHT = 8;

   private static final int HOME_ROW_B = 0;
   private static final int HOME_ROW_W = 7;
   private static final int PAWN_ROW_B = 1;
   private static final int PAWN_ROW_W = 6;

   private static final int K_ROOK_X = 7;
   private static final int K_BISHOP_X = 6;
   private static final int K_KNIGHT_X = 5;
   private static final int KING_X = 4;
   private static final int QUEEN_X = 3;
   private static final int Q_BISHOP_X = 2;
   private static final int Q_KNIGHT_X = 1;
   private static final int Q_ROOK_X = 0;

   private final ChessPiece[][] pieceArray;

   public float mobilityRating;
   public float materialRating;
   public float hangingRating;

   /**
    This constructor sets up a standard chess game on the board
    */
   public ChessBoard()
   {
      pieceArray = new ChessPiece[8][8];
      //set up pieces on each side
      for (int i = 0; i < WIDTH; i++)
      {
         pieceArray[i][PAWN_ROW_W] = new Pawn(ChessPiece.Color.WHITE, i, 6);
         pieceArray[i][PAWN_ROW_B] = new Pawn(ChessPiece.Color.BLACK, i, 1);
      }

      for (int i = 0; i < 8; i += 7)
      {
         pieceArray[i][7] = new Rook(ChessPiece.Color.WHITE, i, 7);
         pieceArray[i][0] = new Rook(ChessPiece.Color.BLACK, i, 0);
      }

      for (int i = 1; i < 7; i += 5)
      {
         pieceArray[i][7] = new Knight(ChessPiece.Color.WHITE, i, 7);
         pieceArray[i][0] = new Knight(ChessPiece.Color.BLACK, i, 0);
      }

      for (int i = 2; i < 6; i += 3)
      {
         pieceArray[i][7] = new Bishop(ChessPiece.Color.WHITE, i, 7);
         pieceArray[i][0] = new Bishop(ChessPiece.Color.BLACK, i, 0);
      }

      pieceArray[3][7] = new Queen(ChessPiece.Color.WHITE, 3, 7);
      pieceArray[4][7] = new King(ChessPiece.Color.WHITE, 4, 7);

      pieceArray[3][0] = new Queen(ChessPiece.Color.BLACK, 3, 0);
      pieceArray[4][0] = new King(ChessPiece.Color.BLACK, 4, 0);

      materialRating = 0;
   }

   /**
    Copy constructor, copies the pieces and places them on this board in the
    same positions

    @param template chess board to copy from
    */
   public ChessBoard(ChessBoard template)
   {
      pieceArray = new ChessPiece[WIDTH][HEIGHT];
      for (int i = 0; i < WIDTH; i++)
      {
         for (int j = 0; j < HEIGHT; j++)
         {
            ChessPiece current = template.getPieceAt(i, j);
            if (current != null)
            {
               setPieceAt(current.copyOfThis(), i, j);
            }
         }
      }
   }

   /**
    Constructor that takes a 2D array of pieces and places them accordingly on
    this chessboard. The first index specifies the 'x' position on the board
    and the second index specifies the 'y' position on the board.

    @param pieces array of pieces to place on board
    */
   public ChessBoard(ChessPiece[][] pieces)
   {
      pieceArray = new ChessPiece[WIDTH][HEIGHT];
      for (int i = 0; i < WIDTH; i++)
      {
         for (int j = 0; j < HEIGHT; j++)
         {
            setPieceAt(pieces[i][j], i, j);
         }
      }
   }

   /**
    Constructor that takes a list of pieces and places them on the board in
    the position specified by each piece's x and y values.

    @param pieces list of pieces to place on board
    */
   public ChessBoard(List<ChessPiece> pieces)
   {
      pieceArray = new ChessPiece[WIDTH][HEIGHT];
      for (ChessPiece piece : pieces)
      {
         setPieceAt(piece, piece.getX(), piece.getY());
      }
   }

   /**
    This method makes the given move and returns a copy of the resulting
    board.

    @param cm move to execute
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
      //TODO - consider castling, where two pieces are moved
      if (nextNode.getPieceAt(xf, yf) instanceof King // castling concerns
            || nextNode.getPieceAt(xf, yf) instanceof Rook)
      {
         nextNode.getPieceAt(xf, yf).hasMoved = true;
      }
      return nextNode;
   }

   /**
    This method determines whether a piece can capture another piece at the
    given coordinates, i.e., whether the piece can move there, except for
    pawns. Does not check if piece is present at (xDest, yDest) or is of the
    opposite color. Does check that path is clear though.

    @param cp piece that is potentially capturing
    @param xDest x index of square being attacked
    @param yDest y index of square being attacked
    @return boolean - whether this piece could capture a piece at the given
    square
    */
   public boolean canCapture(ChessPiece cp, int xDest, int yDest)
   {
      if (cp == null)
      {
         return false;
      }
      if (cp instanceof Pawn)
      {
         if (cp.yCoord + 1 == yDest && cp.color == ChessPiece.Color.BLACK)
         {
            if (cp.xCoord + 1 == xDest || cp.xCoord - 1 == xDest)
            {
               return true;
            }
         }
         if (cp.yCoord - 1 == yDest && cp.color == ChessPiece.Color.WHITE)
         {
            if (cp.xCoord + 1 == xDest || cp.xCoord - 1 == xDest)
            {
               return true;
            }
         }
         return false;
      }
      else // cp is not a Pawn, so capturing is same as moving
      {
         if (cp.canMove(xDest, yDest)
               && pathIsClear(cp, xDest, yDest))
         {
            return true;
         }
      }
      return false;
   }

   /**
    Determines if the King of the given color is able to castle king-side.
    Factors considered are: open path, king and rook have not moved, king is
    not in check

    @param color player who is trying to castle
    @return boolean - true if castling is legal, false otherwise
    */
   public boolean canCastleKS(ChessPiece.Color color)
   {
      int y = color == ChessPiece.Color.BLACK ? HOME_ROW_B : HOME_ROW_W;

      //check to see if King and Rook are present and unmoved
      ChessPiece cp = getPieceAt(KING_X, y);
      if (!(cp instanceof King) || cp.hasMoved)
      {
         return false;
      }

      cp = getPieceAt(K_ROOK_X, y);
      if (!(cp instanceof Rook) || cp.hasMoved)
      {
         return false;
      }

      if (!spaceIsEmpty(K_BISHOP_X, y) || !spaceIsEmpty(K_KNIGHT_X, y))
      {
         return false;
      }

      for (int i = 0; i < WIDTH; i++)
      {
         for (int j = 0; j < HEIGHT; j++)
         {
            if (spaceIsEmpty(i, j))
            {
               continue;
            }
            cp = getPieceAt(i, j);
            if (cp.getColor() == color.opposite()
                  && (canCapture(cp, KING_X, y)
                  || canCapture(cp, K_BISHOP_X, y)
                  || canCapture(cp, K_KNIGHT_X, y)))
            {
               return false;
            }
         }
      }
      return true;
   }

   /**
    Determines if the King of the given color is able to castle queen-side.
    Factors considered are: open path, king and rook have not moved, king is
    not in check

    @param color player who is trying to castle
    @return boolean - true if castling is legal, false otherwise
    */
   public boolean canCastleQS(ChessPiece.Color color)
   {
      int y = color == ChessPiece.Color.BLACK ? HOME_ROW_B : HOME_ROW_W;

      //check to see if King and Rook are present and unmoved
      ChessPiece cp = getPieceAt(KING_X, y);
      if (!(cp instanceof King) || cp.hasMoved)
      {
         return false;
      }

      cp = getPieceAt(Q_ROOK_X, y);
      if (!(cp instanceof Rook) || cp.hasMoved)
      {
         return false;
      }

      if (!spaceIsEmpty(Q_KNIGHT_X, y) || !spaceIsEmpty(Q_BISHOP_X, y)
            || !spaceIsEmpty(QUEEN_X, y))
      {
         return false;
      }
      for (int i = 0; i < WIDTH; i++)
      {
         for (int j = 0; j < HEIGHT; j++)
         {
            if (spaceIsEmpty(i, j))
            {
               continue;
            }

            cp = getPieceAt(i, j);
            if (cp.getColor() == color.opposite()
                  && (canCapture(cp, Q_BISHOP_X, y)
                  || canCapture(cp, QUEEN_X, y)
                  || canCapture(cp, KING_X, y)))
            {
               return false;
            }
         }
      }
      return true;
   }

   /**
    Attempts to execute the capture of the piece at (x,y) by attacker. Checks
    to make sure capture is legal (piece can move to that square and move
    won't put this player in check).

    @param attacker piece attempting the capture
    @param x x coordinate of piece being attacked
    @param y y coordinate of piece being attacked
    @return boolean - whether attacker successfully captured
    */
   public boolean capturePiece(ChessPiece attacker, int x, int y)
   {
      if (attacker == null)
      {
         return false;
      }

      int xAtt = attacker.getX();
      int yAtt = attacker.getY();
      ChessBoard movedBoard = new ChessBoard(this);
      ChessPiece defender = movedBoard.getPieceAt(x, y);
      if (movedBoard.canCapture(attacker, x, y) && defender != null
            && defender.getColor() != attacker.getColor())
      {
         movedBoard.replacePiece(xAtt, yAtt, x, y);
         if (!movedBoard.checkForCheck(attacker.getColor()))
         {
            replacePiece(xAtt, yAtt, x, y);
            if (getPieceAt(x, y) instanceof King
                  || getPieceAt(x, y) instanceof Rook)
            {
               getPieceAt(x, y).hasMoved = true;
            }
            return true;   // piece captured successfully
         }
         return false;     // capture causes check to attacker
      }
      return false;        // piece can't move to there, path is blocked, or
   }                       // space is empty or piece of same color

   /**
    Attempts to castle according to the given move. Checks to make sure
    castling is legal from this position.

    @param move move that is trying to castle
    @return boolean - whether castling was successful or not
    */
   public boolean castle(ChessMove move)
   {
      ChessPiece castler = move.piece;
      if (castler instanceof King)
      {
         int y = castler.getColor() == ChessPiece.Color.BLACK ? HOME_ROW_B : HOME_ROW_W;

         if (canCastleKS(castler.getColor()) && move.getXDest() == K_KNIGHT_X)
         {
            move.setMoveType(ChessMove.Type.CASTLE_KS);
            replacePiece(KING_X, y, K_KNIGHT_X, y);
            replacePiece(K_ROOK_X, y, K_BISHOP_X, y);
            return true;
         }
         else if (canCastleQS(castler.getColor()) && move.getXDest() == Q_BISHOP_X)
         {
            move.setMoveType(ChessMove.Type.CASTLE_QS);
            replacePiece(KING_X, y, Q_BISHOP_X, y);
            replacePiece(Q_ROOK_X, y, QUEEN_X, y);
            return true;
         }
      }
      return false;
   }

   /**
    This method determines if the player of the given color is in check, if no
    King is found, returns false

    @param color color whose king would be in check
    @return boolean - true if opposing player can capture King from this
    position
    */
   public boolean checkForCheck(ChessPiece.Color color)
   {
      King king = findKing(color);
      if (king == null)
      {
         return false;
      }

      for (int i = 0; i < WIDTH; i++)
      {
         for (int j = 0; j < HEIGHT; j++)
         {
            if (!spaceIsEmpty(i, j)
                  && canCapture(getPieceAt(i, j), king.xCoord, king.yCoord)
                  && getPieceAt(i, j).getColor() != color)
            {
               return true;
            }
         }
      }
      return false;
   }

   /**
    Checks the current board position to see if one side is checkmated

    @param playerToMove color whose move is next and who could be mated
    @return boolean - whether the game on the board is over
    */
   public boolean checkForMate(ChessPiece.Color playerToMove)
   {
      int numMoves = findAllMoves(playerToMove).size();
      return numMoves == 0 && checkForCheck(playerToMove);
   }

   /**
    Checks to see that board position is legal. Ensures the following: each
    color has exactly one king, kings are not both in check

    @return boolean - whether position is legal as described above
    */
   public boolean checkPositionIsLegal()
   {
      if (!(checkSingleKing(ChessPiece.Color.WHITE)
            && checkSingleKing(ChessPiece.Color.BLACK)))
      {
         return false;
      }

      if (checkForCheck(ChessPiece.Color.WHITE) && checkForCheck(ChessPiece.Color.BLACK))
      {
         return false;
      }

      return true;
   }

   /**
    Checks to see if the given color has exactly one king on the board.

    @param c color whose pieces should be checked
    @return boolean - true if c has exactly one king, false otherwise
    */
   private boolean checkSingleKing(ChessPiece.Color c)
   {
      int numKings = 0;
      for (int i = 0; i < WIDTH; i++)
      {
         for (int j = 0; j < HEIGHT; j++)
         {
            ChessPiece current = getPieceAt(i, j);
            if (current == null)
            {
               continue;
            }
            if (current instanceof King && current.getColor() == c)
            {
               numKings++;
            }
         }
      }
      return numKings == 1;
   }

   /**
    Helper method to check all squares that a bishop will move through when
    moving from (xi,yi) to (x,y)

    @param xi original x coordinate
    @param yi original y coordinate
    @param x destination x coordinate
    @param y destination y coordinate
    @return boolean - whether bishop has an unobstructed path
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

    @param xi original x coordinate
    @param yi original y coordinate
    @param x destination x coordinate
    @param y destination y coordinate
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
    Compares two chessboards to see which one is greater. Compares first both
    boards' material rating, then if those are equal looks at both boards'
    mobility rating.

    @param cb1 first board to be compared
    @param cb2 second board to be compared
    @return int - 1 if second board is greater, 0 if both are equal, -1
    otherwise
    */
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
    This method compares this to a given Chess Board cb to see which is
    greater. Compares first the material rating, if both boards' are equal,
    checks their mobility rating.

    @param cb board to compare this to
    @return -1 if this is less, 0 if equal, 1 if this is greater
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
//         boolean lhsCheck = checkForCheck(ChessPiece.Color.WHITE) || 
//               checkForCheck(ChessPiece.Color.BLACK);
//         boolean rhsCheck = cb.checkForCheck(ChessPiece.Color.WHITE) || 
//               cb.checkForCheck(ChessPiece.Color.BLACK);
//         if(lhsCheck && rhsCheck)
//         {
            if (mobilityRating < cb.mobilityRating)
            {
               return -1;
            }
            if (mobilityRating == cb.mobilityRating)
            {
               return 0;
            }
//         }
//         if(rhsCheck)
//            return -1;
      }
      return 1;
   }

   /**
    Two ChessBoards are equal if for each square, both boards have equal
    pieces at that location

    @param obj ChessBoard to compare this to
    @return boolean - true if this equals obj, false otherwise
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
    Finds a piece on the this board. See ChessPiece for what makes two pieces
    "equal".

    @param cp piece to be found
    @return ChessPiece first ChessPiece on the ChessBoard that matches cp, or
    null if piece is not found
    */
   public ChessPiece find(ChessPiece cp)
   {
      if (cp == null)
      {
         return null;
      }
      for (int i = 0; i < WIDTH; i++)
      {
         for (int j = 0; j < HEIGHT; j++)
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
    This method finds all possible moves for all pieces of the given color.
    Doesn't consider castling or en passant.

    @param color color that is moving
    @return ArrayList - list of all moves
    */
   public ArrayList<ChessMove> findAllMoves(ChessPiece.Color color)
   {
      ArrayList<ChessMove> moveList = new ArrayList<>();
      for (int i = 0; i < WIDTH; i++)
      {
         for (int j = 0; j < HEIGHT; j++)
         {
            ChessPiece current = getPieceAt(i, j);
            if (current == null)
            {
               continue;
            }
            if (current.getColor() == color)
            {
               moveList.addAll(findMoves(current));
            }
         }
      }
      return moveList;
   }

   /**
    This method finds all legal moves for this piece on this board. Doesn't
    consider castling or en passant.

    @param cp piece whose moves are being generated
    @return ArrayList - list of all moves that cp can legally take
    */
   public ArrayList<ChessMove> findMoves(ChessPiece cp)
   {
      ArrayList<ChessMove> moveList = new ArrayList<>();
      ChessPiece.Color color = cp.getColor();
      int xi = cp.xCoord;
      int yi = cp.yCoord;
      for (int xf = 0; xf < WIDTH; xf++)
      {
         for (int yf = 0; yf < HEIGHT; yf++)
         {

            ChessMove possMove = new ChessMove(cp, xf, yf);
            if (leadsToCheck(possMove)) //if it puts mover in check, disregard
            {
               continue;
            }
            if (cp.canMove(xf, yf) // piece moves this way
                  && pathIsClear(cp, xf, yf) // no pieces in the way
                  && spaceIsEmpty(xf, yf))   // space is empty
            {
               moveList.add(possMove);
            }
            else if (canCapture(cp, xf, yf)
                  && !spaceIsEmpty(xf, yf)
                  && spaceIsOpen(xf, yf, cp.getColor()))
            {
               possMove.captures = true;
               moveList.add(possMove);
            }
         }
      }
      return moveList;
   }

   /**
    Custom find method because using .equals won't work if you don't know the
    coordinates ahead of time

    @param c color of King to find
    @return King - king of this color or null if not found
    */
   private King findKing(ChessPiece.Color c)
   {
      for (int i = 0; i < WIDTH; i++)
      {
         for (int j = 0; j < HEIGHT; j++)
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
    Returns the piece at the given board coordinates

    @param row row of piece, rows start at 0
    @param col column of piece, columns start at 0
    @return ChessPiece - chess piece that is at the given coordinates
    */
   public final ChessPiece getPieceAt(int row, int col)
   {
      return pieceArray[row][col];
   }

   /**
    Returns all the pieces of the given color in a list.

    @param color color whose pieces should be included
    @return ArrayList - list of pieces for this color
    */
   public ArrayList<ChessPiece> getPieces(ChessPiece.Color color)
   {
      ArrayList<ChessPiece> pieces = new ArrayList<>();
      for (int i = 0; i < WIDTH; i++)
      {
         for (int j = 0; j < HEIGHT; j++)
         {
            if (getPieceAt(i, j) == null)
            {
               continue;
            }
            if (getPieceAt(i, j).getColor() == color)
            {
               pieces.add(getPieceAt(i, j));
            }
         }
      }
      return pieces;
   }

   /**
    Returns all the pieces on the board in a 2D array. The first index
    specifies the 'x' position on the board and the second index specifies the
    'y' position on the board.

    @return ChessPiece[][] - array of pieces
    */
   public ChessPiece[][] getPiecesArray()
   {
      ChessPiece[][] copy = new ChessPiece[WIDTH][HEIGHT];
      for (int i = 0; i < WIDTH; i++)
      {
         for (int j = 0; j < HEIGHT; j++)
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

    @return List - list of pieces that are on this board
    */
   public ArrayList<ChessPiece> getPiecesList()
   {
      ArrayList<ChessPiece> tempList = new ArrayList<>();
      for (int i = 0; i < WIDTH; i++)
      {
         for (int j = 0; j < HEIGHT; j++)
         {
            if (getPieceAt(i, j) != null)
            {
               tempList.add(getPieceAt(i, j));
            }
         }
      }
      return tempList;
   }

   /**
    This method determines whether a move will result in the moving player
    being in check.

    @param cm move to execute
    @return - whether or not check is a result of this move
    */
   public boolean leadsToCheck(ChessMove cm)
   {
      ChessBoard temp = advancePosition(cm);
      return temp.checkForCheck(cm.piece.getColor());
   }

   /**
    Moves the given piece to the destination coordinates, if it is a legal
    move. If move is not legal, board is not changed and returns false.

    @param mover piece trying to move
    @param xf destination x coordinate
    @param yf destination y coordinate
    @return boolean - whether or not a legal move was executed
    */
   public boolean movePiece(ChessPiece mover, int xf, int yf)
   {
      int xi = mover.getX();
      int yi = mover.getY();
      ChessBoard movedBoard = new ChessBoard(this);
      if (mover.canMove(xf, yf) && pathIsClear(mover, xf, yf)
            && spaceIsEmpty(xf, yf))
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
            return true;   // move is successful
         }
         return false;     // move puts mover in check, so unsuccessful
      }
      return false;        // piece can't move there, path is block, or space
   }                       // is occupied

   /**
    Checks to see if there any pawns that have reached the other side of the
    board and thus need to be promoted.

    @return ChessPiece - the pawn that needs promoting, else null
    */
   public ChessPiece needPromotion()
   {
      for (int i = 0; i < WIDTH; i++)
      {
         ChessPiece pawn = getPieceAt(i, HOME_ROW_B);
         if (pawn instanceof Pawn
               && pawn.getColor() == ChessPiece.Color.WHITE)
         {
            return pawn;
         }
         pawn = getPieceAt(i, HOME_ROW_W);
         if (pawn instanceof Pawn
               && pawn.getColor() == ChessPiece.Color.BLACK)
         {
            return pawn;
         }
      }
      return null;
   }

   /**
    This method determines if a chess piece has any pieces blocking its way.
    It is assumed that the coordinates constitute a valid move for that piece

    @param mover piece whose path is being examined
    @param x x coordinate of destination
    @param y y coordinate of destination
    @return boolean - true if the piece has nothing blocking it, false
    otherwise
    */
   public boolean pathIsClear(ChessPiece mover, int x, int y)
   {
      boolean clear = true;
      int xi = mover.getX();
      int yi = mover.getY();
      Class c = mover.getClass();
      if (mover instanceof Pawn)
      {
         // check to make sure initial 2-space move is unobstructed
         if (mover.getColor() == ChessPiece.Color.WHITE && yi == 6 && y == 4)
         {
            clear = spaceIsEmpty(xi, yi - 1);
         }
         if (mover.getColor() == ChessPiece.Color.BLACK && yi == 1 && y == 3)
         {
            clear = spaceIsEmpty(xi, yi + 1);
         }
      }
      else if (mover instanceof Rook)
      {
         clear = clearPathRook(xi, yi, x, y);
      }
      else if (mover instanceof Bishop)
      {
         clear = clearPathBishop(xi, yi, x, y);
      }
      else if (mover instanceof Queen)
      {
         // Queen moves like a Bishop and Rook put together
         clear = clearPathBishop(xi, yi, x, y) && clearPathRook(xi, yi, x, y);
      }
      else
      {
         clear = true; // King and Knight always have a clear path
      }

      return clear;
   }

   /**
    Places the piece currently at (xi, yi) at (xf, yf) and makes (xi, yi)
    empty.

    @param xi initial x coordinate
    @param yi initial y coordinate
    @param xf final x coordinate
    @param yf final y coordinate
    */
   public void replacePiece(int xi, int yi, int xf, int yf)
   {
      if (getPieceAt(xi, yi) != null)
      {
         getPieceAt(xi, yi).movePiece(xf, yf);
      }
      setPieceAt(getPieceAt(xi, yi), xf, yf);
      setPieceAt(null, xi, yi);
   }

   /**
    Sets the piece at the given board coordinates

    @param cp chess piece to place at these coordinates
    @param x column piece is in, columns start at 0
    @param y row piece is in, rows start at 0
    */
   public final void setPieceAt(ChessPiece cp, int x, int y)
   {
      pieceArray[x][y] = cp;
      if (cp != null)
      {
         cp.movePiece(x, y);
      }
   }

   /**
    Returns whether there is a piece present at the given coordinates

    @param x x coordinate to be checked for a piece
    @param y y coordinate to be checked for a piece
    @return boolean - true if a piece is present, false otherwise
    */
   public boolean spaceIsEmpty(int x, int y)
   {
      return pieceArray[x][y] == null;
   }

   /**
    Like spaceIsEmpty but also allows a piece of the opposite color to be on
    the square

    @param x x coordinate to be checked for a piece
    @param y y coordinate to be checked for a piece
    @param color color of piece moving to this space, so opposite colored
    piece can be present
    @return boolean - true if no piece or piece of opposite color, false
    otherwise
    */
   public boolean spaceIsOpen(int x, int y, ChessPiece.Color color)
   {
      return spaceIsEmpty(x, y) || pieceArray[x][y].getColor() != color;
   }

   /**
    Returns a string with each row of pieces on its own line.

    @return String - string representation of this ChessBoard
    */
   @Override
   public String toString()
   {
      String string = "";
      for (int i = 0; i < WIDTH; i++)
      {
         string += "\n";
         for (int j = 0; j < HEIGHT; j++)
         {
            ChessPiece cp = getPieceAt(j, i);
            if (cp == null)
            {
               string = string + "-- ";
            }
            else
            {
               string = string + cp.getColor().oneLetter()
                     + cp.oneLetterIdentifier() + " ";
               if (cp instanceof Pawn)
               {
                  string = string.trim() + "P ";
               }
            }
         }
      }
      return string;
   }
}
