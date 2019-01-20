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
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

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

    public static final int CENTERING_AMT_Y = 25;
    public static final int CENTERING_AMT_X = 12;
    public static final int SQUARE_WIDTH = 50;
    public static final int SQUARE_HEIGHT = 50;
    public static final int BOARD_WIDTH = SQUARE_WIDTH * NUM_COLS;
    public static final int BOARD_HEIGHT = SQUARE_HEIGHT * NUM_ROWS;

    // used for accessing piece images
    private static final int IMG_WHITE = 0;
    private static final int IMG_BLACK = 1;
    private static final int IMG_RED = 2;
    private static final int NUM_COLORS = 3;

    private static final int IMG_KING = 0;
    private static final int IMG_QUEEN = 1;
    private static final int IMG_ROOK = 2;
    private static final int IMG_BISHOP = 3;
    private static final int IMG_KNIGHT = 4;
    private static final int IMG_PAWN = 5;
    private static final int NUM_PIECE_TYPES = 6;

    private static final int IMG_WIDTH = 30;
    private static final int IMG_HEIGHT = 30;

    private Color lightSquareColor = new Color(80, 80, 80);
    private Color darkSquareColor = Color.gray;
    private Color selectedPieceColor = Color.red;
    private Color darkPieceColor = Color.black;
    private Color lightPieceColor = Color.white;

    private final BufferedImage[] pieceImages;

    private int xPos;
    private int yPos;
    private ChessPiece[][] pieces;
    public ChessPiece selectedPiece;
    private ArrayList<HighlightSquare> possMoveSquares;
    private ChessPiece possMovePiece;

    public Checkerboard()
    {
        pieces = new ChessPiece[NUM_COLS][NUM_ROWS];
        xPos = 0;
        yPos = 0;
        pieceImages = new BufferedImage[NUM_COLORS * NUM_PIECE_TYPES];
        possMoveSquares = new ArrayList<>();
        possMovePiece = null;
        initializeImages();
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

    public boolean pointHasPiece(Point p)
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
                System.out.println("Error in Checkerboard.pointHasPiece: " + e.getMessage());
            }
        }
        return false;
    }

    /**
     * Populates the images that will be used to display the chess pieces.
     */
    private void initializeImages()
    {
        String[] pieceTypes =
        {
            "king", "queen", "rook", "bishop", "knight", "pawn"
        };
        String[] colors =
        {
            "w", "b", "r"
        };

        String imgFolderPath = "resources/";
        int count = 0;
        for (String piece : pieceTypes)
        {
            for (String color : colors)
            {
                try
                {
                    String filepath = imgFolderPath + piece + "_" + color + ".png";
                    pieceImages[count++] = ImageIO.read(new File(filepath));
                } catch (Exception e)
                {
                    System.out.println("Error loading piece images");
                }
            }
        }
    }

    public void setPieces(List<ChessPiece> pieces)
    {
        this.pieces = new ChessPiece[NUM_COLS][NUM_ROWS];
        for (ChessPiece piece : pieces)
        {
            this.pieces[piece.getX()][piece.getY()] = piece;
        }
    }

    public void setPieceAt(int x, int y, ChessPiece cp)
    {
        this.pieces[x][y] = cp;
    }

    public ChessPiece getPieceAt(int x, int y)
    {
        return pieces[x][y];
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
            possMoveSquares.add(new HighlightSquare(move.getXDest(), move.getYDest(),color));
        }
    }

    public void setSelectedPiece(ChessPiece cp)
    {
        possMoveSquares.clear();
        selectedPiece = cp;
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
        pieceYPos = yPos + SQUARE_HEIGHT * cp.getY() + CENTERING_AMT_X;

        int imageIndex = getImageIndex(cp, g.getColor());
        img = pieceImages[imageIndex];
        g.drawImage(img, pieceXPos, pieceYPos, IMG_WIDTH, IMG_HEIGHT, null);
    }

    /**
     * Finds the image for a piece of this type and color in the image array.
     * Images are organized first by type and then by color
     *
     * @param cp - chess piece of the type to display
     * @param c - color of piece to display
     * @return int - index of image to display
     */
    private int getImageIndex(ChessPiece cp, Color c)
    {
        return getPieceOffset(cp) * NUM_COLORS + getColorOffset(c);
    }

    /**
     * Returns the pieceOffset for this piece in the image array
     *
     * @param cp - chess piece for which image is being retrieved
     * @return int - pieceOffset of image
     */
    private int getPieceOffset(ChessPiece cp)
    {
        Class c = cp.getClass();
        if (c == Rook.class)
        {
            return IMG_ROOK;
        }
        else if (c == Bishop.class)
        {
            return IMG_BISHOP;
        }
        else if (c == Knight.class)
        {
            return IMG_KNIGHT;
        }
        else if (c == Queen.class)
        {
            return IMG_QUEEN;
        }
        else if (c == King.class)
        {
            return IMG_KING;
        }
        else if (c == Pawn.class)
        {
            return IMG_PAWN;
        }

        return 0;
    }

    /**
     * Returns the colorOffset for a piece of the given Color
     *
     * @param c - color of the piece for which an image is being found
     * @return int - colorOffset for image
     */
    private int getColorOffset(Color c)
    {
        if (c == Color.red)
        {
            return IMG_RED;
        }
        else if (c == Color.black)
        {
            return IMG_BLACK;
        }
        else
        {
            return IMG_WHITE;
        }
    }
}
