/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chessgui;

import chessgame.ChessMove;
import chessgame.ChessPiece;
import chessgame.GameMode;

import java.awt.*;
import java.util.ArrayList;

/**
 *
 * @author John
 */
public class Scoreboard
{
    private ChessPiece.Color playerToMove;
    private int whiteTime, blackTime;
    private final GameMode mode;
    private Rectangle bounds;
    private ArrayList<ChessMove> moveList;

    private static final int FONT_HEIGHT = 25;
    
    public Scoreboard(GameMode gm)
    {
        whiteTime = blackTime = 300;
        mode = gm;
        playerToMove = ChessPiece.Color.WHITE;
    }

    public Scoreboard(Rectangle boundingBox, GameMode gm)
    {
        this(gm);
        bounds = boundingBox;
    }

    public void setMoveList(ArrayList<ChessMove> moves)
    {
        moveList = moves;
    }

    public void setBoundingBox(Rectangle bb)
    {
        bounds = new Rectangle(bb);
    }

    public ChessPiece.Color getPlayerToMove()
    {
        return playerToMove;
    }

    public void setPlayerToMove(ChessPiece.Color player)
    {
        if (player != null)
        {
            playerToMove = player;
        }
    }

    public void switchPlayerToMove()
    {
        switch (playerToMove){
            case WHITE:
                whiteTime += 3;
                break;
            case BLACK:
                blackTime += 3;
                break;
        }
        playerToMove = playerToMove.opposite();
    }

    public void update()
    {
        int offset = 1;
        switch (mode)
        {
            case SINGLE:
            case VERSUS:
                offset = -1;
        }

        if (playerToMove == ChessPiece.Color.WHITE)
        {
            whiteTime += offset;
        }
        else if (playerToMove == ChessPiece.Color.BLACK)
        {
            blackTime += offset;
        }

    }

    public void redraw(Graphics g)
    {
        printMoveList(g);

        Color textColorForBlack, textColorForWhite;
        boolean whiteToMove = playerToMove == ChessPiece.Color.WHITE;
        textColorForBlack = whiteToMove ? Color.BLACK : Color.RED;
        textColorForWhite = whiteToMove ? Color.RED : Color.BLACK;

        g.setColor(textColorForWhite);
        g.drawString("White: " + getTimeString(whiteTime), bounds.x, bounds.y + bounds.height - FONT_HEIGHT);
        g.setColor(textColorForBlack);
        g.drawString("Black: " + getTimeString(blackTime), bounds.x, bounds.y + FONT_HEIGHT);
        
    }

    private String getTimeString(int sec)
    {
        if (sec < 0)
        {
            sec = 0;
        }
        int minutes = sec / 60;
        int seconds = sec % 60;
        String str = minutes > 9 ? "" : "0";
        str = str + minutes + ":";
        str = str + (seconds > 9 ? "" : "0");
        str = str + seconds;
        return str;
    }

    /**
     * This method prints out the moves that are played. Only 20 moves are able
     * to be displayed at a time, so the 20 most recent are shown
     *
     * @param g - graphics object moveList will be drawn on
     */
    public void printMoveList(Graphics g)
    {
        if (moveList == null)
        {
            return;
        }

        int x = bounds.x;
        int y = bounds.y;
        Font myFont = new Font("Arial", Font.PLAIN, 15);
        g.setFont(myFont);
        g.setColor(Color.BLACK);
        g.drawString("Move List", x + 100, y + 20);
        int plies = moveList.size();
        int numMovesEach = (plies + 1) / 2;               // add one to round up
        int startPos = numMovesEach > 20 ? numMovesEach - 20 : 0;

        for (int i = startPos; i < numMovesEach; i++)
        {
            int curLineY = (17 * (i - startPos) + 40);
            g.drawString((i + 1) + ": " + moveList.get(i * 2).toString(), x + 100, y  + curLineY);
            if (plies > i * 2 + 1)
            {
                g.drawString(moveList.get(i * 2 + 1).toString(), x + 200, y + curLineY);
            }
        }
    }
}
