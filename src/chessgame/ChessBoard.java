package chessgame;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * This class creates a 64 square board that holds ChessPieces and enforces that
 * only legal moves are made, e.g. pawns cannot move backwards, a player does
 * not move into check, or a player doesn't castle after moving their king.
 *
 * @author John Polus
 */
public class ChessBoard implements Serializable
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
    private static final int K_KNIGHT_X = 6;
    private static final int K_BISHOP_X = 5;
    private static final int KING_X = 4;
    private static final int QUEEN_X = 3;
    private static final int Q_BISHOP_X = 2;
    private static final int Q_KNIGHT_X = 1;
    private static final int Q_ROOK_X = 0;

    private final ChessPiece[][] pieceArray;

    /**
     * This constructor sets up a standard chess game on the board
     */
    public ChessBoard()
    {
        pieceArray = new ChessPiece[WIDTH][HEIGHT];
        //set up pieces on each side
        ChessPiece.Color white = ChessPiece.Color.WHITE;
        pieceArray[0][HOME_ROW_W] = new Rook(white);
        pieceArray[1][HOME_ROW_W] = new Knight(white);
        pieceArray[2][HOME_ROW_W] = new Bishop(white);
        pieceArray[3][HOME_ROW_W] = new Queen(white);
        pieceArray[4][HOME_ROW_W] = new King(white);
        pieceArray[5][HOME_ROW_W] = new Bishop(white);
        pieceArray[6][HOME_ROW_W] = new Knight(white);
        pieceArray[7][HOME_ROW_W] = new Rook(white);

        ChessPiece.Color black = ChessPiece.Color.BLACK;
        pieceArray[0][HOME_ROW_B] = new Rook(black);
        pieceArray[1][HOME_ROW_B] = new Knight(black);
        pieceArray[2][HOME_ROW_B] = new Bishop(black);
        pieceArray[3][HOME_ROW_B] = new Queen(black);
        pieceArray[4][HOME_ROW_B] = new King(black);
        pieceArray[5][HOME_ROW_B] = new Bishop(black);
        pieceArray[6][HOME_ROW_B] = new Knight(black);
        pieceArray[7][HOME_ROW_B] = new Rook(black);

        for (int i = 0; i < WIDTH; i++)
        {
            pieceArray[i][PAWN_ROW_W] = new Pawn(white);
            pieceArray[i][PAWN_ROW_B] = new Pawn(black);
        }
    }

    /**
     * Copy constructor, copies the pieces and places them on this board in the
     * same positions
     *
     * @param template chess board to copy from
     */
    public ChessBoard(ChessBoard template)
    {
        pieceArray = new ChessPiece[WIDTH][HEIGHT];
        if (template == null)
        {
            return;
        }

        for (int i = 0; i < WIDTH; i++)
        {
            for (int j = 0; j < HEIGHT; j++)
            {
                Point space = new Point(i,j);
                setPieceAt(template.getCopyOfPieceAt(space), space);
            }
        }
    }

    /**
     * Constructor that takes a 2D array of pieces and places them accordingly
     * on this chessboard. The first index specifies the 'x' position on the
     * board and the second index specifies the 'y' position on the board.
     *
     * @param pieces array of pieces to place on board
     */
    public ChessBoard(ChessPiece[][] pieces)
    {
        pieceArray = new ChessPiece[WIDTH][HEIGHT];
        if (pieces == null)
        {
            return;
        }

        for (int i = 0; i < WIDTH; i++)
        {
            for (int j = 0; j < HEIGHT; j++)
            {
                Point space = new Point(i,j);
                setPieceAt(pieces[i][j], space);
            }
        }
    }

    /**
     * This method makes the given move and returns a copy of the resulting
     * board.
     *
     * @param cm move to execute
     * @return ChessBoard - resulting board position after the move
     */
    public ChessBoard advancePosition(ChessMove cm)
    {
        ChessBoard nextNode = new ChessBoard(this);
        if (cm == null || cm.piece == null)
        {
            return nextNode;
        }
        nextNode.replacePiece(cm.orig, cm.dest);
        //TODO - consider castling, where two pieces are moved
        return nextNode;
    }

    /**
     * This method determines whether a piece can capture another piece at the
     * given coordinates, i.e., whether the piece can move there, except for
     * pawns. Does not check if piece is present at (xDest, yDest) or is of the
     * opposite color. Does check that path is clear though.
     *
     * @param cp piece that is potentially capturing
     * @param orig current coordinates of piece
     * @param dest coordinates of square being attacked
     * @return boolean - whether this piece could capture a piece at the given
     * square
     */
    public boolean canCapture(ChessPiece cp, Point orig, Point dest)
    {
        if (cp == null)
        {
            return false;
        }
        if (cp instanceof Pawn)
        {
            if (orig.y + 1 == dest.y && cp.color == ChessPiece.Color.BLACK)
            {
                if (orig.x + 1 == dest.x || orig.x - 1 == dest.x)
                {
                    return true;
                }
            }
            if (orig.y - 1 == dest.y && cp.color == ChessPiece.Color.WHITE)
            {
                if (orig.x + 1 == dest.x || orig.x - 1 == dest.x)
                {
                    return true;
                }
            }
            return false;
        }
        else // cp is not a Pawn, so capturing is same as moving
        {
            Vector possMove = new Vector(dest.x - orig.x, dest.y - orig.y);
            if (cp.canMove(possMove) && pathIsClear(cp, orig, dest))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the King of the given color is able to castle king-side.
     * Factors considered are: open path, king and rook have not moved, king is
     * not in check
     *
     * @param color player who is trying to castle
     * @return boolean - true if castling is legal, false otherwise
     */
    public boolean canCastleKS(ChessPiece.Color color)
    {
        int y = color == ChessPiece.Color.BLACK ? HOME_ROW_B : HOME_ROW_W;

        Point spKing = new Point(KING_X, y);
        Point spBishop = new Point(K_BISHOP_X, y);
        Point spKnight = new Point(K_KNIGHT_X, y);
        Point spRook = new Point(K_ROOK_X, y);

        //check to see if King and Rook are present and unmoved
        ChessPiece cp = getCopyOfPieceAt(spKing);
        if (cp == null || cp.hasMoved || !(cp instanceof King))
        {
            return false;
        }

        cp = getCopyOfPieceAt(spRook);
        if (cp == null || cp.hasMoved || !(cp instanceof Rook))
        {
            return false;
        }

        if (!spaceIsEmpty(spBishop) || !spaceIsEmpty(spKnight))
        {
            return false;
        }

        for (int i = 0; i < WIDTH; i++)
        {
            for (int j = 0; j < HEIGHT; j++)
            {
                Point space = new Point(i,j);
                if (spaceIsEmpty(space))
                {
                    continue;
                }
                cp = getCopyOfPieceAt(space);
                if (cp.getColor() == color.opposite()
                        && (canCapture(cp, space, spKing)
                        || canCapture(cp, space, spBishop)
                        || canCapture(cp, space, spKnight)))
                {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Determines if the King of the given color is able to castle queen-side.
     * Factors considered are: open path, king and rook have not moved, king is
     * not in check
     *
     * @param color player who is trying to castle
     * @return boolean - true if castling is legal, false otherwise
     */
    public boolean canCastleQS(ChessPiece.Color color)
    {
        int y = color == ChessPiece.Color.BLACK ? HOME_ROW_B : HOME_ROW_W;

        Point spKing = new Point(KING_X, y);
        Point spQueen = new Point(QUEEN_X, y);
        Point spBishop = new Point(Q_BISHOP_X, y);
        Point spKnight = new Point(Q_KNIGHT_X, y);
        Point spRook = new Point(Q_ROOK_X, y);

        //check to see if King and Rook are present and unmoved
        ChessPiece cp = getCopyOfPieceAt(spKing);
        if (cp == null || cp.hasMoved || !(cp instanceof King))
        {
            return false;
        }

        cp = getCopyOfPieceAt(spRook);
        if (cp == null || cp.hasMoved || !(cp instanceof Rook))
        {
            return false;
        }

        if (!spaceIsEmpty(spKnight) || !spaceIsEmpty(spBishop)
                || !spaceIsEmpty(spQueen))
        {
            return false;
        }
        for (int i = 0; i < WIDTH; i++)
        {
            for (int j = 0; j < HEIGHT; j++)
            {
                Point space = new Point(i,j);
                if (spaceIsEmpty(space))
                {
                    continue;
                }

                cp = getCopyOfPieceAt(space);
                if (cp.getColor() == color.opposite()
                        && (canCapture(cp, space, spBishop)
                        || canCapture(cp, space, spQueen)
                        || canCapture(cp, space, spKing)))
                {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean canMovePiece(ChessPiece mover, Point orig, Point dest)
    {
        if (mover == null)
        {
            return false;
        }

        ChessBoard movedBoard = new ChessBoard(this);
        Vector possMove = new Vector(dest.x - orig.x, dest.y - orig.y);
        if (mover.canMove(possMove) && pathIsClear(mover, orig, dest)
                && spaceIsEmpty(dest) && areIndicesInBounds(dest.x, dest.y))
        {
            movedBoard.replacePiece(orig,dest);
            if (!movedBoard.checkForCheck(mover.getColor()))
            {
                return true;   // move is successful
            }
            return false;     // move puts mover in check, so unsuccessful
        }
        return false;        // piece can't move there, path is blocked, or space
    }                       // is occupied

    /**
     * Attempts to execute the capture of the piece at (x,y) by attacker. Checks
     * to make sure capture is legal (piece can move to that square and move
     * won't put this player in check).
     *
     * @param attacker piece attempting the capture
     * @param orig coordinates of attacking piece
     * @param dest coordinates of piece being attacked
     * @return boolean - whether attacker successfully captured
     */
    public boolean capturePiece(ChessPiece attacker, Point orig, Point dest)
    {
        if (attacker == null)
        {
            return false;
        }
        ChessPiece myAttacker = getCopyOfPieceAt(orig);
        if (myAttacker == null)
        {
            return false;
        }

        ChessBoard movedBoard = new ChessBoard(this);
        ChessPiece defender = movedBoard.getCopyOfPieceAt(dest);
        if (movedBoard.canCapture(myAttacker, orig, dest) && defender != null
                && defender.getColor() != myAttacker.getColor())
        {
            movedBoard.replacePiece(orig, dest);
            if (!movedBoard.checkForCheck(myAttacker.getColor()))
            {
                replacePiece(orig, dest);
                return true;   // piece captured successfully
            }
            return false;     // capture causes check to attacker
        }
        return false;        // piece can't move to there, path is blocked, or
    }                       // space is empty or piece of same color

    /**
     * Attempts to castle according to the given move. Checks to make sure
     * castling is legal from this position.
     *
     * @param move move that is trying to castle
     * @return boolean - whether castling was successful or not
     */
    public boolean castle(ChessMove move)
    {
        if (move == null)
        {
            return false;
        }
        ChessPiece castler = move.piece;
        if (castler instanceof King)
        {
            int y = castler.getColor() == ChessPiece.Color.BLACK ? HOME_ROW_B : HOME_ROW_W;

            if (canCastleKS(castler.getColor()) && move.dest.x == K_KNIGHT_X)
            {
                move.setMoveType(ChessMove.Type.CASTLE_KS);
                replacePiece(new Point(KING_X, y), new Point(K_KNIGHT_X, y));
                replacePiece(new Point(K_ROOK_X, y), new Point(K_BISHOP_X, y));
                return true;
            }
            else if (canCastleQS(castler.getColor()) && move.dest.x == Q_BISHOP_X)
            {
                move.setMoveType(ChessMove.Type.CASTLE_QS);
                replacePiece(new Point(KING_X, y), new Point(Q_BISHOP_X, y));
                replacePiece(new Point(Q_ROOK_X, y), new Point(QUEEN_X, y));
                return true;
            }
        }
        return false;
    }

    /**
     * This method determines if the player of the given color is in check, if
     * no King is found, returns false
     *
     * @param color color whose king would be in check
     * @return boolean - true if opposing player can capture King from this
     * position
     */
    public boolean checkForCheck(ChessPiece.Color color)
    {
        Point spKing = findKing(color);
        if (spKing == null)
        {
            return false;
        }

        ChessPiece piece;
        for (int i = 0; i < WIDTH; i++)
        {
            for (int j = 0; j < HEIGHT; j++)
            {
                Point space = new Point(i, j);
                piece = getCopyOfPieceAt(space);
                if (piece != null
                        && canCapture(piece, space, spKing)
                        && piece.getColor() != color)
                {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean checkForGameOver(ChessPiece.Color playerToMove)
    {
        return findAllMoves(playerToMove).isEmpty();
    }

    /**
     * Checks the current board position to see if one side is checkmated
     *
     * @param playerToMove color whose move is next and who could be mated
     * @return boolean - whether the game on the board is over
     */
    public boolean checkForMate(ChessPiece.Color playerToMove)
    {
        int numMoves = findAllMoves(playerToMove).size();
        return numMoves == 0 && checkForCheck(playerToMove);
    }
    
    public boolean checkForStalemate(ChessPiece.Color playerToMove)
    {
        int numMoves = findAllMoves(playerToMove).size();
        return numMoves == 0 && !checkForCheck(playerToMove);
    }

    /**
     * Checks to see that board position is legal. Ensures the following: each
     * color has exactly one king, kings are not both in check
     *
     * @return boolean - whether position is legal as described above
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
     * Checks to see if the given color has exactly one king on the board.
     *
     * @param c color whose pieces should be checked
     * @return boolean - true if c has exactly one king, false otherwise
     */
    private boolean checkSingleKing(ChessPiece.Color c)
    {
        int numKings = 0;
        ArrayList<ChessPiece> pieces = getPieces(c);
        for (ChessPiece piece : pieces) {
            if (piece instanceof King) {
                numKings++;
            }
        }

        return numKings == 1;
    }

    /**
     * Helper method to check all squares that a bishop will move through when
     * moving from (xi,yi) to (x,y)
     *
     * @param xi original x coordinate
     * @param yi original y coordinate
     * @param x destination x coordinate
     * @param y destination y coordinate
     * @return boolean - whether bishop has an unobstructed path
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
     * Helper method to check all squares that a rook will move through when
     * moving from (xi,yi) to (x,y)
     *
     * @param xi original x coordinate
     * @param yi original y coordinate
     * @param x destination x coordinate
     * @param y destination y coordinate
     * @return true - whether rook has an unobstructed path
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
     * Two ChessBoards are equal if for each square, both boards have equal
     * pieces at that location
     *
     * @param obj ChessBoard to compare this to
     * @return boolean - true if this equals obj, false otherwise
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
     * Finds a piece on the this board. See ChessPiece for what makes two pieces
     * "equal".
     *
     * @param cpToFind piece to be found
     * @return ChessPiece first ChessPiece on the ChessBoard that matches cp, or
     * null if piece is not found
     */
    public Point find(ChessPiece cpToFind)
    {
        if (cpToFind == null) {
            return null;
        }
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                Point space = new Point(x, y);
                ChessPiece piece = getPieceAt(space);
                if (piece != null && piece.equals(cpToFind)) {
                    return space;
                }
            }
        }
        return null;
    }

    /**
     * This method finds all possible moves for all pieces of the given color.
     * Doesn't consider castling or en passant.
     *
     * @param color color that is moving
     * @return ArrayList - list of all moves
     */
    public ArrayList<ChessMove> findAllMoves(ChessPiece.Color color)
    {
        ArrayList<ChessMove> moveList = new ArrayList<>();
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                Point space = new Point(i, j);
                ChessPiece cp = getPieceAt(space);

                if (cp != null && cp.color == color) {
                    moveList.addAll(findMoves(cp, space));
                }
            }
        }

        return moveList;
    }

    /**
     * This method finds all legal moves for this piece on this board. Doesn't
     * consider castling or en passant.
     *
     * @param cp piece whose moves are being generated
     * @param space coordinates of cp on the board
     * @return ArrayList - list of all moves that cp can legally take
     */
    public ArrayList<ChessMove> findMoves(ChessPiece cp, Point space)
    {
        if (cp == null)
        {
            return null;
        }
        ArrayList<ChessMove> moveList = new ArrayList<>();

        HashSet<Vector> moveVectors = cp.getMoveSet();
        for (Vector vector : moveVectors)
        {
            int xf = vector.dx + space.x;
            int yf = vector.dy + space.y;
            Point dest = new Point(xf, yf);

            ChessMove possMove = new ChessMove(cp, space, dest);
            if (leadsToCheck(possMove) || !areIndicesInBounds(xf, yf))         //if it puts mover in check, disregard
            {
                continue;
            }
            if (pathIsClear(cp, space, dest) // no pieces in the way
                    && spaceIsEmpty(dest))    // space is empty
            {
                moveList.add(possMove);
            }
            else if (canCapture(cp, space, dest)
                    && spaceIsEnemy(dest, cp.getColor()))
            {
                possMove.captures = true;
                moveList.add(possMove);
            }
        }
        // Pawns are the only piece that capture differently than they move
        if (cp instanceof Pawn)
        {
            for (Vector vector : ((Pawn) cp).getCaptureMoveSet())
            {
                int xf = vector.dx + space.x;
                int yf = vector.dy + space.y;
                Point dest = new Point(xf, yf);
                
                ChessMove possMove = new ChessMove(cp,space, dest);
                if (spaceIsEnemy(dest, cp.getColor()))
                {
                    possMove.captures = true;
                    moveList.add(possMove);
                }
            }
        }
        return moveList;
    }

    /**
     * Custom find method because using .equals won't work if you don't know the
     * coordinates ahead of time
     *
     * @param c color of King to find
     * @return point - coordinates for king of this color or null if not found
     */
    private Point findKing(ChessPiece.Color c)
    {
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                Point space = new Point (i,j);
                ChessPiece cp = getPieceAt(space);

                if (cp instanceof King && cp.color == c) {
                    return space;
                }
            }
        }
        return null;
    }

    public ChessMove getCastleKSMove(ChessPiece.Color color)
    {
        Point spKing = findKing(color);
        King king = (King)getPieceAt(spKing);
        if (!canCastleKS(color) || king == null)
        {
            return null;
        }
        int x = K_KNIGHT_X;
        int y = (color == ChessPiece.Color.WHITE) ? HOME_ROW_W : HOME_ROW_B;

        Point dest = new Point(x, y);
        ChessMove move = new ChessMove(king, spKing, dest);
        move.setMoveType(ChessMove.Type.CASTLE_KS);
        return move;
    }

    public ChessMove getCastleQSMove(ChessPiece.Color color)
    {
        Point spKing = findKing(color);
        King king = (King)getPieceAt(spKing);
        if (!canCastleQS(color) || king == null)
        {
            return null;
        }
        int x = Q_BISHOP_X;
        int y = (color == ChessPiece.Color.WHITE) ? HOME_ROW_W : HOME_ROW_B;
        Point dest = new Point(x, y);
        ChessMove move = new ChessMove(king, spKing, dest);
        move.setMoveType(ChessMove.Type.CASTLE_QS);
        return move;
    }

    /**
     * Returns the piece at the given board coordinates
     *
     * @param space row and column of piece, both are 0-indexed
     * @return ChessPiece - chess piece that is at the given coordinates
     */
    private ChessPiece getPieceAt(Point space)
    {
        return getPieceAt(space.x, space.y);
    }

    /**
     * Returns the piece at the given board coordinates
     *
     * @param x row of piece, 0-indexed
     * @param y col of piece, 0-indexed
     * @return ChessPiece - chess piece that is at the given coordinates
     */
    private ChessPiece getPieceAt(int x, int y)
    {
        if (areIndicesInBounds(x, y)) {
            return pieceArray[x][y];
        }
        return null;
    }


    public ChessPiece getCopyOfPieceAt(Point space)
    {
        return getCopyOfPieceAt(space.x, space.y);
    }

    public ChessPiece getCopyOfPieceAt(int x, int y) {
        ChessPiece piece = getPieceAt(x, y);
        if (piece != null)
        {
            piece = piece.copyOfThis();
        }
        return piece;
    }

    /**
     * Returns all the pieces on the board in a list.
     *
     * @return List - list of pieces that are on this board
     */
    public ArrayList<ChessPiece> getPieces()
    {
        ArrayList<ChessPiece> tempList = new ArrayList<>();
        ChessPiece piece;
        for (int i = 0; i < WIDTH; i++)
        {
            for (int j = 0; j < HEIGHT; j++)
            {
                piece = getCopyOfPieceAt(i, j);
                if (piece != null)
                {
                    tempList.add(piece);
                }
            }
        }
        return tempList;
    }

    /**
     * Returns all the pieces of the given color in a list.
     *
     * @param color color whose pieces should be included
     * @return ArrayList - list of pieces for this color
     */
    public ArrayList<ChessPiece> getPieces(ChessPiece.Color color)
    {
        ArrayList<ChessPiece> pieces = new ArrayList<>();
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                Point space = new Point(i, j);
                ChessPiece piece = getPieceAt(space);
                if (piece != null && piece.getColor() == color)
                {
                    pieces.add(piece);
                }
            }
        }
        return pieces;
    }

    public ArrayList<Point> getPieceSpaces(ChessPiece.Color color) {
        ArrayList<Point> spaces = new ArrayList<>();
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                Point space = new Point(i, j);
                ChessPiece piece = getPieceAt(space);
                if (piece != null && piece.getColor() == color) {
                    spaces.add(space);
                }
            }
        }
        return spaces;
    }

    /**
     * Returns all the pieces on the board in a 2D array. The first index
     * specifies the 'x' position on the board and the second index specifies
     * the 'y' position on the board.
     *
     * @return ChessPiece[][] - array of pieces
     */
    public ChessPiece[][] getPiecesArray()
    {
        ChessPiece[][] copy = new ChessPiece[WIDTH][HEIGHT];
        ChessPiece piece;
        for (int i = 0; i < WIDTH; i++)
        {
            for (int j = 0; j < HEIGHT; j++)
            {
                Point space = new Point(i, j);
                piece = getCopyOfPieceAt(space);
                if (piece != null)
                {
                    copy[i][j] = piece;
                }
            }
        }
        return copy;
    }

    /**
     * This method determines whether a move will result in the moving player
     * being in check.
     *
     * @param cm move to execute
     * @return - whether or not check is a result of this move
     */
    public boolean leadsToCheck(ChessMove cm)
    {
        if (cm == null)
        {
            return false;
        }
        ChessBoard temp = advancePosition(cm);
        return temp.checkForCheck(cm.piece.getColor());
    }

    /**
     * Moves the given piece to the destination coordinates, if it is a legal
     * move. If move is not legal, board is not changed and returns false.
     *
     * @param mover piece trying to move
     * @param orig current coordinates of piece
     * @param dest coordinates of destination
     * @return boolean - whether or not a legal move was executed
     */
    public boolean movePiece(ChessPiece mover, Point orig, Point dest)
    {
        if (mover == null)
        {
            return false;
        }

        ChessPiece myMover = getCopyOfPieceAt(orig);
        if (myMover != null && canMovePiece(myMover, orig, dest))
        {
            replacePiece(orig, dest);
            return true;
        }
        return false;
    }

    /**
     * Checks to see if there any pawns that have reached the other side of the
     * board and thus need to be promoted.
     *
     * @return space - coordinates of the pawn that needs promoting, else null
     */
    public Point needPromotion()
    {
        for (int i = 0; i < WIDTH; i++)
        {
            ChessPiece cp = getPieceAt(i, HOME_ROW_B);
            if (cp instanceof Pawn
                    && cp.getColor() == ChessPiece.Color.WHITE)
            {
                return new Point(i, HOME_ROW_B);
            }
            cp = getPieceAt(i, HOME_ROW_W);
            if (cp instanceof Pawn
                    && cp.getColor() == ChessPiece.Color.BLACK)
            {
                return new Point(i, HOME_ROW_W);
            }
        }
        return null;
    }

    /**
     * This method determines if a chess piece has any pieces blocking its way.
     * It is assumed that the coordinates constitute a valid move for that piece
     *
     * @param mover piece whose path is being examined
     * @param orig coordinates of piece currently
     * @param dest coordinates of destination
     * @return boolean - true if the piece has nothing blocking it, false
     * otherwise
     */
    public boolean pathIsClear(ChessPiece mover, Point orig, Point dest)
    {
        if (mover == null)
        {
            return false;
        }
        boolean clear = true;
        int xi = orig.x;
        int yi = orig.y;
        int xf = dest.x;
        int yf = dest.y;
        if (mover instanceof Pawn)
        {
            // check to make sure initial 2-space move is unobstructed
            if (mover.getColor() == ChessPiece.Color.WHITE && yi == 6 && yf == 4)
            {
                clear = spaceIsEmpty(new Point(xi, yi - 1));
            }
            if (mover.getColor() == ChessPiece.Color.BLACK && yi == 1 && yf == 3)
            {
                clear = spaceIsEmpty(new Point(xi, yi + 1));
            }
        }
        else if (mover instanceof Rook)
        {
            clear = clearPathRook(xi, yi, xf, yf);
        }
        else if (mover instanceof Bishop)
        {
            clear = clearPathBishop(xi, yi, xf, yf);
        }
        else if (mover instanceof Queen)
        {
            // Queen moves like a Bishop and Rook put together
            clear = clearPathBishop(xi, yi, xf, yf) && clearPathRook(xi, yi, xf, yf);
        }
        else
        {
            clear = true; // King and Knight always have a clear path
        }

        return clear;
    }

    /**
     * Places the piece currently at (xi, yi) at (xf, yf) and makes (xi, yi)
     * empty.
     *
     * @param orig initial coordinates
     * @param dest final coordinates
     */
    private void replacePiece(Point orig, Point dest)
    {
        ChessPiece piece = getPieceAt(orig);
        if (piece != null)
        {
            piece.hasMoved = true;
        }
        setPieceAt(piece, dest);
        setPieceAt(null, orig);
    }

    /**
     * Sets the piece at the given board coordinates
     *
     * @param cp chess piece to place at these coordinates
     * @param space row and column piece is in, both are 0-indexed
     */
    public void setPieceAt(ChessPiece cp, Point space)
    {
        if (!areIndicesInBounds(space.x, space.y))
        {
            return;
        }
        ChessPiece myPiece;
        if (cp != null)
        {
            myPiece = cp.copyOfThis();
        }
        else
        {
            myPiece = null;
        }
        pieceArray[space.x][space.y] = myPiece;
    }

    /**
     * Returns whether there is a piece present at the given coordinates
     *
     * @param space set of coordinates to be checked for a piece
     * @return boolean - true if no piece is present, false otherwise
     */
    public boolean spaceIsEmpty(Point space)
    {
        return spaceIsEmpty(space.x, space.y);
    }

    public boolean spaceIsEmpty(int x, int y) {
        if (!areIndicesInBounds(x, y))
        {
            return true;
        }
        return pieceArray[x][y] == null;
    }

    public boolean areIndicesInBounds(int x, int y)
    {
        return (0 <= x && x <= 7 && 0 <= y && y <= 7);

    }

    public boolean spaceIsEnemy(Point space, ChessPiece.Color color)
    {
        int x = space.x;
        int y = space.y;

        if (!areIndicesInBounds(x, y))
        {
            return false;
        }
        return pieceArray[x][y] != null && pieceArray[x][y].getColor() == color.opposite();
    }

    /**
     * Like spaceIsEmpty but also allows a piece of the opposite color to be on
     * the square
     *
     * @param space coordinates to be checked for a piece
     * @param color color of piece moving to this space, so opposite colored
     * piece can be present
     * @return boolean - true if no piece or piece of opposite color, false
     * otherwise
     */
    public boolean spaceIsOpen(Point space, ChessPiece.Color color)
    {
        return spaceIsEmpty(space) || spaceIsEnemy(space, color);
    }

    /**
     * Returns a string with each row of pieces on its own line.
     *
     * @return String - string representation of this ChessBoard
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
                ChessPiece cp = getCopyOfPieceAt(new Point(j, i));
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

    public ChessMove validateMove(ChessMove move)
    {
        if (move == null)
        {
            return null;
        }

        ChessPiece mover = move.piece;
        if (mover == null)
        {
            return null;
        }

        boolean done = false;
        Point orig = move.orig;
        Point dest = move.dest;
        if (spaceIsEmpty(dest))
        {
            if (mover instanceof King) // try to castle
            {
                if (canCastleKS(mover.getColor()) && dest.x == K_KNIGHT_X)
                {
                    move.setMoveType(ChessMove.Type.CASTLE_KS);
                    done = true;
                }
                else if (canCastleQS(mover.getColor()) && dest.x == Q_BISHOP_X)
                {
                    move.setMoveType(ChessMove.Type.CASTLE_QS);
                    done = true;
                }
            }
            if (!done)
            {
                if (canMovePiece(mover, orig, dest))
                {
                    move.setMoveType(ChessMove.Type.NORMAL);
                    done = true;
                }
            }
        }
        if (!done && canCapture(mover, orig, dest) && spaceIsEnemy(dest, mover.getColor()))
        {
            move.setMoveType(ChessMove.Type.NORMAL);
            move.captures = true;
            done = true;
        }
        if (!done)
        {
            return null;
        }

        // TODO - check for en passant
        // TODO - check for check, mate, etc.
        return move;
    }
}
