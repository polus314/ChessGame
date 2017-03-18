package chessgame;

/**
 This class represents the graphical checkerboard that a chess game is played
 on. So it has 64 squares, in an 8x8 configuration, that alternate both
 vertically and horizontally between a light color and dark color. Also stores
 and displays all the pieces on the board.

 @author John Polus
 */
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class Checkerboard
{

   // TODO - change this to width and height as well

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

   private Color lightSquareColor = Color.white;
   private Color darkSquareColor = Color.gray;
   private Color selectedPieceColor = Color.red;
   private Color darkPieceColor = Color.black;
   private Color lightPieceColor = Color.white;

   private final BufferedImage[] pieceImages;

   private int xPos;
   private int yPos;
   private ChessPiece[][] pieces;
   public ChessPiece selectedPiece;

   public Checkerboard()
   {
      pieces = new ChessPiece[NUM_COLS][NUM_ROWS];
      xPos = 0;
      yPos = 0;
      pieceImages = new BufferedImage[NUM_COLORS * NUM_PIECE_TYPES];
      initializeImages();
   }

   /**
    Populates the images that will be used to display the chess pieces.
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
            }
            catch (Exception e)
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

   public void setSelectedPiece(ChessPiece cp)
   {
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
    Sets the board's x position to x

    @param x - new x coordinate
    */
   public void setX(int x)
   {
      this.xPos = x;
   }

   /**
    Returns the board's x position

    @return int - current x coordinate
    */
   public int getX()
   {
      return xPos;
   }

   /**
    Sets the board's y position to y

    @param y - new y coordinate
    */
   public void setY(int y)
   {
      this.yPos = y;
   }

   /**
    Returns the board's y position

    @return int - current y coordinate
    */
   public int getY()
   {
      return yPos;
   }

   /**
    This method paints a 64 square board of alternating colors, as well as all
    the pieces that are still in play

    @param g - graphics object used to draw the board
    */
   public void paintBoard(Graphics g)
   {
      //paints the light squares
      g.setColor(lightSquareColor);
      g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);

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
            if (thisPiece.getColor() == PieceColor.BLACK)
            {
               g.setColor(darkPieceColor);
            }
            else //if (thisPiece.getColor() == PieceColor.WHITE)
            {
               g.setColor(lightPieceColor);
            }
            paintPiece(g, thisPiece);
         }
      }
      g.setColor(selectedPieceColor);
      paintPiece(g, selectedPiece);
   }

   /**
    This method paints the piece cp as a letter (R for Rook, etc.) or a circle
    if a pawn

    @param g
    @param cp
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
    Finds the image for a piece of this type and color in the image array.
    Images are organized first by type and then by color

    @param cp - chess piece of the type to display
    @param c - color of piece to display
    @return int - index of image to display
    */
   private int getImageIndex(ChessPiece cp, Color c)
   {
      return getPieceOffset(cp) * NUM_COLORS + getColorOffset(c);
   }

   /**
    Returns the pieceOffset for this piece in the image array

    @param cp - chess piece for which image is being retrieved
    @return int - pieceOffset of image
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
    Returns the colorOffset for a piece of the given Color

    @param c - color of the piece for which an image is being found
    @return int - colorOffset for image
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
