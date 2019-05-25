/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chessgui;

import chessgame.Bishop;
import chessgame.ChessPiece;
import chessgame.King;
import chessgame.Knight;
import chessgame.Pawn;
import chessgame.Queen;
import chessgame.Rook;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 *
 * @author John
 */
public final class PieceImages
{
    private static final int NUM_COLORS = 3;
    private static final int NUM_PIECE_TYPES = 6;
    
    // used for accessing piece images
    private static final int IMG_WHITE = 0;
    private static final int IMG_BLACK = 1;
    private static final int IMG_RED = 2;

    private static final int IMG_KING = 0;
    private static final int IMG_QUEEN = 1;
    private static final int IMG_ROOK = 2;
    private static final int IMG_BISHOP = 3;
    private static final int IMG_KNIGHT = 4;
    private static final int IMG_PAWN = 5;

    public static final int IMG_WIDTH = 30;
    public static final int IMG_HEIGHT = 30;
    
    public static final int SMALL_WIDTH = 8;
    public static final int SMALL_HEIGHT = 8;
    
    private static final BufferedImage pieceImages[] = initializeImages();
    
    private PieceImages() { }
    
    /**
     * Populates the images that will be used to display the chess pieces.
     */
    private static BufferedImage[] initializeImages()
    {
        BufferedImage images[] = new BufferedImage[NUM_COLORS * NUM_PIECE_TYPES];
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
                    images[count++] = ImageIO.read(new File(filepath));
                } catch (Exception e)
                {
                    System.out.println("Error loading piece images: " + e.toString());
                }
            }
        }
        return images;
    }
    
    /**
     * Finds the image for a piece of this type and color in the image array.
     * Images are organized first by type and then by color
     *
     * @param cp - chess piece of the type to display
     * @param c - color of piece to display
     * @return int - index of image to display
     */
    private static int getImageIndex(ChessPiece cp, Color c)
    {
        return getPieceOffset(cp) * NUM_COLORS + getColorOffset(c);
    }

    /**
     * Returns the pieceOffset for this piece in the image array
     *
     * @param cp - chess piece for which image is being retrieved
     * @return int - pieceOffset of image
     */
    private static int getPieceOffset(ChessPiece cp)
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
    private static int getColorOffset(Color c)
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
    
    public static BufferedImage getPieceImage(ChessPiece cp, Color c)
    {
        return pieceImages[getImageIndex(cp, c)];
    }
    
    public static BufferedImage getSmallPieceImage(ChessPiece cp, Color c)
    {        
        BufferedImage img = getPieceImage(cp, c);
        if (img == null)
        {
            return null;
        }
        BufferedImage smallImg = new BufferedImage(SMALL_WIDTH, SMALL_HEIGHT, img.getType());
        
        Graphics2D g = smallImg.createGraphics();
        g.drawImage(img, 0, 0, SMALL_WIDTH, SMALL_HEIGHT, null);
        return smallImg;
    }
}
