package chessutil;

import chessgame.*;

import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileReader {
    private static final int MAX_FILE_SIZE = 2048;

    /**
     * Loads a board position from the given file. Returns true if the board is
     * able to be loaded successfully, false otherwise
     *
     * @param filename name of file to read from
     * @return whether board position was loaded successfully
     */
    public static boolean loadPositionFromFile(String filename, ChessBoard cb)
    {
        FileInputStream file;
        byte[] buf;
        try
        {
            file = new FileInputStream(filename);
            buf = new byte[MAX_FILE_SIZE];
            file.read(buf, 0, MAX_FILE_SIZE);
        } catch (FileNotFoundException fnfe)
        {
            System.out.println("File not found!");
            return false;
        } catch (IOException ioe)
        {
            System.out.println("File reading failed");
            return false;
        }

        String posString = new String(buf);
        posString = posString.replace("\r", "");
        posString = posString.replace("\n", "");
        posString = posString.replace(" ", "");
        posString = posString.trim();
        if (posString.length() != 64 * 2)
        {
            return false;
        }

        for (int row = 0; row < ChessBoard.HEIGHT; row++) {
            for (int col = 0; col < ChessBoard.WIDTH; col++) {
                Point space = new Point(col, row);
                String nextPiece = posString.substring(0, 2);
                if (!nextPiece.equals("--"))
                {
                    ChessPiece cp = loadPiece(nextPiece);
                    cb.setPieceAt(cp, space);
                }
                else
                {
                    cb.setPieceAt(null, space);
                }
                posString = posString.substring(2);
            }
        }
        return true;
    }

    /**
     * Creates a piece based on the given string input.
     *
     * @param loadString string that specifies what piece to create
     * @return new piece created based on loadString
     */
    private static ChessPiece loadPiece(String loadString)
    {
        ChessPiece.Color color;
        if (loadString.charAt(0) == 'B')
        {
            color = ChessPiece.Color.BLACK;
        }
        else
        {
            color = ChessPiece.Color.WHITE;
        }
        switch (loadString.charAt(1))
        {
            case 'R':
                return new Rook(color);
            case 'N':
                return new Knight(color);
            case 'B':
                return new Bishop(color);
            case 'K':
                return new King(color);
            case 'Q':
                return new Queen(color);
            default:
                return new Pawn(color);
        }
    }
}
