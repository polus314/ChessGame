package chessgame;

/**
 This class does most of the rendering of the board and pieces

 @author jppolecat
 */
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

class Checkerboard
{
   public final int CENTERING_AMT_Y = 25;
   public final int CENTERING_AMT_X = 12;
   public final int SQUARE_WIDTH = 50;
   public final int SQUARE_HEIGHT = 50;
   public final int FONT_SIZE = 40;

   // used for accessing piece images
   private final int IMG_WHITE = 0;
   private final int IMG_BLACK = 1;
   private final int IMG_RED = 2;
   private final int NUM_COLORS = 3;

   private final int IMG_KING = 0;
   private final int IMG_QUEEN = 1;
   private final int IMG_ROOK = 2;
   private final int IMG_BISHOP = 3;
   private final int IMG_KNIGHT = 4;
   private final int IMG_PAWN = 5;  
   private final int NUM_PIECE_TYPES = 6;
   
   private final BufferedImage[] pieceImages;

   public PieceColor humanPlayer = PieceColor.WHITE;
   private int xPos;
   private int yPos;
   public ChessBoard gameBoard;

   public Checkerboard()
   {
      xPos = 0;
      yPos = 0;
      gameBoard = new ChessBoard();
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

      String imgFolderPath = "src/chessgame/";
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

   public void setX(int x)
   {
      this.xPos = x;
   }

   public int getX()
   {
      return xPos;
   }

   public void setY(int y)
   {
      this.yPos = y;
   }

   public int getY()
   {
      return yPos;
   }

   /**
    This method paints a 64 square board of alternating colors, as well as all
    the pieces that are still in play

    @param g
    */
   public void paintBoard(Graphics g)
   {
      //paints the light squares
      g.setColor(Color.white);
      for (int j = 0; j < 8; j += 2)
      {
         for (int i = 0; i < 8; i += 2)
         {
            g.fillRect((xPos + (i * SQUARE_WIDTH)), (yPos + (j * SQUARE_HEIGHT)), SQUARE_WIDTH, SQUARE_HEIGHT);
         }
      }
      for (int j = 1; j < 9; j += 2)
      {
         for (int i = 1; i < 9; i += 2)
         {
            g.fillRect(xPos + (i * SQUARE_WIDTH), yPos + (j * SQUARE_HEIGHT), SQUARE_WIDTH, SQUARE_HEIGHT);
         }
      }

      //paints the dark squares
      g.setColor(Color.gray);
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
      for (int i = 0; i < 8; i++)
      {
         for (int j = 0; j < 8; j++)
         {
            if (gameBoard.getPieceAt(i, j).getColor() == PieceColor.BLACK)
            {
               g.setColor(Color.black);
            }
            else if (gameBoard.getPieceAt(i, j).getColor() == PieceColor.WHITE)
            {
               g.setColor(Color.white);
            }
            if (gameBoard.getPieceAt(i, j).isSelected())
            {
               g.setColor(Color.red);
            }
            paintPiece(g, gameBoard.getPieceAt(i, j));
         }
      }
   }

   /**
    This method paints the piece cp as a letter (R for Rook, etc.) or a circle
    if a pawn

    @param g
    @param cp
    */
   public void paintPiece(Graphics g, ChessPiece cp)
   {
      BufferedImage img;
      int pieceXPos, pieceYPos;

      //if piece isn't a placeholder, generic ChessPiece that represents an
      //empty square logically, determines where on the board it should be drawn
      if (cp.equals(new ChessPiece()))
      {
         return;
      }

      if (humanPlayer == PieceColor.BLACK)
      {
         pieceXPos = xPos + SQUARE_WIDTH * flipCoords(cp.getX()) + CENTERING_AMT_X;
         pieceYPos = yPos + SQUARE_HEIGHT * flipCoords(cp.getY()) + CENTERING_AMT_X;
      }
      else
      {
         pieceXPos = xPos + SQUARE_WIDTH * cp.getX() + CENTERING_AMT_X;
         pieceYPos = yPos + SQUARE_HEIGHT * cp.getY() + CENTERING_AMT_X;
      }
      int pieceOffset = 0, colorOffset = 0;
      switch (cp.getType())
      {
         case ROOK:
         {
            pieceOffset = IMG_ROOK;
            break;
         }
         case KNIGHT:
         {
            pieceOffset = IMG_KNIGHT;
            break;
         }
         case BISHOP:
         {
            pieceOffset = IMG_BISHOP;
            break;
         }
         case QUEEN:
         {
            pieceOffset = IMG_QUEEN;
            break;
         }
         case KING:
         {
            pieceOffset = IMG_KING;
            break;
         }
         case PAWN:
         {
            pieceOffset = IMG_PAWN;
            break;
         }
         case EMPTY:
         {
            break;
         }
      }
      if (g.getColor() == Color.white)
      {
         colorOffset = IMG_WHITE;
      }
      else if (g.getColor() == Color.black)
      {
         colorOffset = IMG_BLACK;
      }
      else if (g.getColor() == Color.red)
      {
         colorOffset = IMG_RED;
      }
      img = pieceImages[pieceOffset * NUM_COLORS + colorOffset];
      g.drawImage(img, pieceXPos, pieceYPos, 30, 30, null);
   }

   private int flipCoords(int x)
   {
      return 7 - x;
   }
}
