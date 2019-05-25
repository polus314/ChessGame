/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chessgui;

import chessgame.ChessPiece;
import chessgame.GameMode;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

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

    private static final int FONT_HEIGHT = 25;
    
    public Scoreboard(GameMode gm)
    {
        whiteTime = blackTime = 3000;
        mode = gm;
        playerToMove = ChessPiece.Color.WHITE;
    }

    public Scoreboard(Rectangle boundingBox, GameMode gm)
    {
        this(gm);
        bounds = boundingBox;
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
        Color blackColor, whiteColor;
        boolean whiteToMove = playerToMove == ChessPiece.Color.WHITE;
        blackColor = whiteToMove ? Color.BLACK : Color.RED;
        whiteColor = whiteToMove ? Color.RED : Color.BLACK;

        g.setColor(whiteColor);
        g.drawString("White: " + getTimeString(whiteTime), bounds.x, bounds.y + bounds.height - FONT_HEIGHT);
        g.setColor(blackColor);
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
}
