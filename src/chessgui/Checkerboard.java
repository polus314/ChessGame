package chessgui;

/**
 * This class represents the graphical checkerboard that a chess game is played
 * on. So it has 64 squares, in an 8x8 configuration, that alternate both
 * vertically and horizontally between a light color and dark color. Also stores
 * and displays all the pieces on the board.
 *
 * @author John Polus
 */
import chessgame.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Checkerboard
{

    private class HighlightSquare
    {

        int x;
        int y;
        Color color;

        public HighlightSquare(int x, int y, Color c)
        {
            this.x = x;
            this.y = y;
            this.color = c;
        }
    }

    public static final int NUM_ROWS = ChessBoard.HEIGHT;
    public static final int NUM_COLS = ChessBoard.WIDTH;

    public static final int CENTERING_AMT_Y = 12;
    public static final int CENTERING_AMT_X = 12;
    public static final int SQUARE_WIDTH = 50;
    public static final int SQUARE_HEIGHT = 50;
    public static final float CUSHION = 0.15f;
    public static final int BOARD_WIDTH = SQUARE_WIDTH * NUM_COLS;
    public static final int BOARD_HEIGHT = SQUARE_HEIGHT * NUM_ROWS;

    

    private final Color lightSquareColor = new Color(80, 80, 80);
    private final Color darkSquareColor = Color.gray;
    private final Color selectedPieceColor = Color.red;
    private final Color darkPieceColor = Color.black;
    private final Color lightPieceColor = Color.white;
    
    private static final Color HIGHLIGHT_COLOR = Color.YELLOW;

    private int xPos;
    private int yPos;
    private ChessPiece[][] pieces;
    public ChessPiece selectedPiece;
    private ArrayList<HighlightSquare> possMoveSquares;
    private ChessPiece possMovePiece;
    private HighlightSquare previousMove;

    public Checkerboard()
    {
        pieces = new ChessPiece[NUM_COLS][NUM_ROWS];
        xPos = 0;
        yPos = 0;
        
        possMoveSquares = new ArrayList<>();
        possMovePiece = null;
        previousMove = null;
    }

    public boolean containsPoint(Point p)
    {
        int width = NUM_COLS * SQUARE_WIDTH;
        int height = NUM_ROWS * SQUARE_HEIGHT;
        if (xPos <= p.x && p.x < xPos + width && yPos <= p.y && p.y < yPos + height)
        {
            return true;
        }
        return false;
    }

    public boolean showPieceCursor(Point p)
    {
        if (squareHasPiece(p))
        {
            int x = p.x % SQUARE_WIDTH;
            int y = p.y % SQUARE_HEIGHT;

            int xCushion = (int) (SQUARE_WIDTH * CUSHION);
            int yCushion = (int) (SQUARE_HEIGHT * CUSHION);

            return (xCushion < x && x < SQUARE_WIDTH - xCushion
                    && yCushion < y && y < SQUARE_HEIGHT - yCushion);
        }
        return false;
    }
    
    public void highlightPreviousMove(ChessMove cm)
    {
        if (cm == null)
        {
            return;
        }
        previousMove = new HighlightSquare(cm.getXDest(), cm.getYDest(), HIGHLIGHT_COLOR);
    }
    
    public void removePreviousMoveHighlight()
    {
        previousMove = null;
    }

    private boolean squareHasPiece(Point p)
    {
        if (containsPoint(p))
        {
            int col = (p.x - xPos) / SQUARE_WIDTH;
            int row = (p.y - yPos) / SQUARE_HEIGHT;
            try
            {
                if (pieces[col][row] != null)
                {
                    return true;
                }
            } catch (Exception e)
            {
                System.err.println("Error in Checkerboard.pointHasPiece");
            }
        }
        return false;
    }

    public void setPieces(List<ChessPiece> pieces)
    {
        this.pieces = new ChessPiece[NUM_COLS][NUM_ROWS];
        for (ChessPiece piece : pieces)
        {
            setPieceAt(piece.getX(), piece.getY(), piece);
        }
    }

    public void setPieceAt(int x, int y, ChessPiece cp)
    {
        ChessPiece myPiece = cp;
        if (cp != null)
        {
            myPiece = cp.copyOfThis();
        }
        this.pieces[x][y] = myPiece;
    }

    public ChessPiece getPieceAt(int x, int y)
    {
        ChessPiece piece = pieces[x][y];
        if (piece != null)
        {
            piece = piece.copyOfThis();
        }
        return piece;
    }

    public ChessPiece getSelectedPiece()
    {
        return selectedPiece.copyOfThis();
    }

    public void highlightPossibleMoves(ArrayList<ChessMove> moves)
    {
        possMoveSquares.clear();
        if (moves == null || moves.isEmpty())
        {
            return;
        }
        possMovePiece = moves.get(0).piece;
        for (ChessMove move : moves)
        {
            Color color = Color.YELLOW;
            if (move.getMoveType() == ChessMove.Type.CASTLE_KS || move.getMoveType() == ChessMove.Type.CASTLE_QS)
            {
                color = Color.BLUE;
            }
            else if (move.captures)
            {
                color = Color.RED;
            }
            possMoveSquares.add(new HighlightSquare(move.getXDest(), move.getYDest(), color));
        }
    }

    public void setSelectedPiece(ChessPiece cp)
    {
        possMoveSquares.clear();
        ChessPiece newSelPiece = cp;
        if (newSelPiece != null)
        {
            newSelPiece = newSelPiece.copyOfThis();
        }
        selectedPiece = newSelPiece;
    }

    public ArrayList<ChessPiece> getPiecesList()
    {
        ArrayList<ChessPiece> piecesList = new ArrayList<>();
        for (int i = 0; i < NUM_COLS; i++)
        {
            for (int j = 0; j < NUM_ROWS; j++)
            {
                ChessPiece current = pieces[i][j];
                if (current == null)
                {
                    continue;
                }
                piecesList.add(current);
            }
        }
        return piecesList;
    }

    /**
     * Sets the board's x position to x
     *
     * @param x - new x coordinate
     */
    public void setX(int x)
    {
        this.xPos = x;
    }

    /**
     * Returns the board's x position
     *
     * @return int - current x coordinate
     */
    public int getX()
    {
        return xPos;
    }

    /**
     * Sets the board's y position to y
     *
     * @param y - new y coordinate
     */
    public void setY(int y)
    {
        this.yPos = y;
    }

    /**
     * Returns the board's y position
     *
     * @return int - current y coordinate
     */
    public int getY()
    {
        return yPos;
    }

    /**
     * This method paints a 64 square board of alternating colors, as well as
     * all the pieces that are still in play
     *
     * @param g - graphics object used to draw the board
     */
    public void paintBoard(Graphics g)
    {
        //paints the light squares
        g.setColor(lightSquareColor);
        g.fillRect(xPos, yPos, BOARD_WIDTH, BOARD_HEIGHT);

        //paints the dark squares
        g.setColor(darkSquareColor);
        for (int j = 1; j < 9; j += 2)
        {
            for (int i = 0; i < 8; i += 2)
            {
                g.fillRect((xPos + (i * SQUARE_WIDTH)), (yPos + (j * SQUARE_HEIGHT)), SQUARE_WIDTH, SQUARE_HEIGHT);
            }
        }
        for (int j = 0; j < 8; j += 2)
        {
            for (int i = 1; i < 9; i += 2)
            {
                g.fillRect((xPos + (i * SQUARE_WIDTH)), (yPos + (j * SQUARE_HEIGHT)), SQUARE_WIDTH, SQUARE_HEIGHT);
            }
        }

        //highlights squares that can be moved to
        boolean highlight = (selectedPiece != null) && (selectedPiece.equals(possMovePiece));
        if (highlight)
        {
            for (HighlightSquare square : possMoveSquares)
            {
                drawHighlight(g, square);
            }
        }
        
        if (previousMove != null)
        {
            drawHighlight(g, previousMove);
        }

        //paints all the pieces, checking if they are white, black or currently
        //selected
        ChessPiece thisPiece;
        for (int i = 0; i < NUM_COLS; i++)
        {
            for (int j = 0; j < NUM_ROWS; j++)
            {
                thisPiece = pieces[i][j];
                if (thisPiece == null)
                {
                    continue;
                }
                if (thisPiece.getColor() == ChessPiece.Color.BLACK)
                {
                    g.setColor(darkPieceColor);
                }
                else //if (thisPiece.getColor() == Color.WHITE)
                {
                    g.setColor(lightPieceColor);
                }
                paintPiece(g, thisPiece);
            }
        }
        g.setColor(selectedPieceColor);
        paintPiece(g, selectedPiece);
        g.setColor(Color.BLACK);
        g.drawRect(xPos, yPos, BOARD_WIDTH, BOARD_HEIGHT);
    }

    private void drawHighlight(Graphics g, HighlightSquare square)
    {
        g.setColor(square.color);
        int x = xPos + (square.x * SQUARE_WIDTH);
        int y = yPos + (square.y * SQUARE_HEIGHT);
        g.drawRect(x, y, SQUARE_WIDTH, SQUARE_HEIGHT);
        g.drawRect(x + 1, y + 1, SQUARE_WIDTH - 2, SQUARE_HEIGHT - 2);

    }

    /**
     * This method paints the piece cp as a letter (R for Rook, etc.) or a
     * circle if a pawn
     *
     * @param g
     * @param cp
     */
    private void paintPiece(Graphics g, ChessPiece cp)
    {
        BufferedImage img;
        int pieceXPos, pieceYPos;

        // if there is a piece on this square, determines where on the board it 
        // should be drawn
        if (cp == null)
        {
            return;
        }

        pieceXPos = xPos + SQUARE_WIDTH * cp.getX() + CENTERING_AMT_X;
        pieceYPos = yPos + SQUARE_HEIGHT * cp.getY() + CENTERING_AMT_Y;

        img = PieceImages.getPieceImage(cp, g.getColor());
        g.drawImage(img, pieceXPos, pieceYPos, PieceImages.IMG_WIDTH, PieceImages.IMG_HEIGHT, null);
    }
}
