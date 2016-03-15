package chessgame;
/**
 This class does most of the rendering of the board and pieces
@author jppolecat
*/

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

class Checkerboard
{
   public final int CENTERING_AMT_Y = 25;
   public final int CENTERING_AMT_X = 12;
   public final int SQUARE_WIDTH = 50;
   public final int SQUARE_HEIGHT = 50;
   public final int FONT_SIZE = 40;
   
   public PieceColor humanPlayer = PieceColor.WHITE;
   private int xPos;
   private int yPos;
   public ChessBoard gameBoard;
    
    public Checkerboard()
    {
       xPos = 0;
       yPos = 0;
       gameBoard = new ChessBoard();
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
    This method paints a 64 square board of alternating colors, as well as
    all the pieces that are still in play
    @param g 
    */
    public void paintBoard(Graphics g)
    {
      //paints the light squares
      g.setColor(Color.lightGray);
      for(int j = 0; j < 8; j+=2)
         for(int i = 0; i < 8; i+=2)
            g.fillRect((xPos+(i*SQUARE_WIDTH)),(yPos+(j*SQUARE_HEIGHT)),SQUARE_WIDTH,SQUARE_HEIGHT);
      for(int j = 1; j < 9; j+=2)
         for(int i = 1; i < 9; i+=2)
            g.fillRect(xPos + (i*SQUARE_WIDTH),yPos + (j*SQUARE_HEIGHT),SQUARE_WIDTH,SQUARE_HEIGHT);
      
      //paints the dark squares
      g.setColor(Color.green);
      for(int j = 1; j < 9; j+=2)
         for(int i = 0; i < 8; i+=2)
            g.fillRect((xPos+(i*SQUARE_WIDTH)),(yPos+(j*SQUARE_HEIGHT)),SQUARE_WIDTH,SQUARE_HEIGHT);
      for(int j = 0; j < 8; j+=2)
         for(int i = 1; i < 9; i+=2)
            g.fillRect((xPos+(i*SQUARE_WIDTH)),(yPos+(j*SQUARE_HEIGHT)),SQUARE_WIDTH,SQUARE_HEIGHT);

      //paints all the pieces, checking if they are white, black or currently
      //selected
      for(int i = 0; i < 8; i++)
         for(int j = 0; j < 8; j++)
         {
            if(gameBoard.pieceArray[i][j].getColor() == PieceColor.BLACK)
               g.setColor(Color.black);
            else if (gameBoard.pieceArray[i][j].getColor() == PieceColor.WHITE)
               g.setColor(Color.white);
            if (gameBoard.pieceArray[i][j].isSelected())
               g.setColor(Color.red);
            paintPiece(g,gameBoard.pieceArray[i][j]);
         }
    }
    
    /**
    This method paints the piece cp as a letter (R for Rook, etc.) or a circle
    if a pawn
    @param g
    @param cp 
    */
    public void paintPiece(Graphics g,ChessPiece cp)
    {
       Font myFont = new Font("Arial", Font.BOLD, FONT_SIZE);
       g.setFont(myFont);
       int pieceXPos, pieceYPos;
       pieceXPos = pieceYPos = 0;
       //if piece isn't a placeholder, generic ChessPiece that represents an
       //empty square logically, determines where on the board it should be drawn
       if(!cp.equals(new ChessPiece()))
       {
         if(humanPlayer == PieceColor.BLACK)
         {
            pieceXPos = xPos + SQUARE_WIDTH * flipCoords(cp.getX()) + CENTERING_AMT_X;
            pieceYPos = yPos + SQUARE_HEIGHT * flipCoords(cp.getY()) + CENTERING_AMT_X;
         }
         else 
         {
            pieceXPos = xPos + SQUARE_WIDTH * cp.getX() + CENTERING_AMT_X;
            pieceYPos = yPos + SQUARE_HEIGHT * cp.getY() + CENTERING_AMT_X;
         }
       }
       switch(cp.getType())
       {
          case ROOK:
         {
            g.drawString("R", pieceXPos, pieceYPos + CENTERING_AMT_Y);
            break;
         }
          case KNIGHT:
         {
            g.drawString("N", pieceXPos, pieceYPos + CENTERING_AMT_Y);
            break;
         }
          case BISHOP:
         {
            g.drawString("B", pieceXPos, pieceYPos + CENTERING_AMT_Y);
            break;
         }
          case QUEEN:
         {
            g.drawString("Q", pieceXPos, pieceYPos + CENTERING_AMT_Y);
            break;
         }
          case KING:
         {
            g.drawString("K", pieceXPos, pieceYPos + CENTERING_AMT_Y);
            break;
         }
          case PAWN:
         {
            g.fillOval(pieceXPos, pieceYPos, SQUARE_WIDTH/2, SQUARE_HEIGHT/2);
            break;
         }
          case EMPTY:
         {
            break;
         }   
       }
    }
    
    private int flipCoords(int x)
    {
       return 7 - x;
    }
}