/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chessgui;

import chessgame.ChessPiece;
import chessgame.GameMode;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import javax.swing.Timer;

/**
 *
 * @author John
 */
public class Scoreboard
{

    private Timer updateTimer;
    private ChessPiece.Color playerToMove;
    private int whiteTime, blackTime;
    private final GameMode mode;
    private Rectangle bounds;

    public Scoreboard(Rectangle boundingBox, GameMode gm)
    {
        whiteTime = blackTime = 3000;
        mode = gm;
        bounds = boundingBox;
        playerToMove = ChessPiece.Color.WHITE;
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
        g.drawString("SCOREBOARD", bounds.x, bounds.y);
        g.drawString("White: " + getTimeString(whiteTime), bounds.x, bounds.y + 15);
        g.drawString("Black: " + getTimeString(blackTime), bounds.x + bounds.width - 50, bounds.y + 15);
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
