package chessgame;

/**
 * This class represents a move that is taken in chess. It has an associated
 * piece that is moving, where that piece is moving to, and what type of move it
 * is, e.g. castling, moving to an empty square, capturing an enemy piece, etc.
 *
 * @author John Polus
 */
public class ChessMove //implements Comparable<ChessMove>
{

    /**
     * Enumerated type that describes the general type that a move is. NOTE:
     * These types are mutually exclusive, unlike promotion, check, and
     * checkmate, which could be coincident with one or more of these. NORMAL -
     * Covers most moves that are made CASTLE_KS - King castles with the rook
     * closer to him CASTLE_QS - King castles with the rook further from him
     *
     * @author John Polus
     */
    public enum Type
    {
        NORMAL, CASTLE_KS, CASTLE_QS;
    }

    public ChessPiece piece;
    private int xDest;
    private int yDest;
    private Type moveType;

    public boolean givesCheck = false;
    public boolean givesMate = false;
    public boolean captures = false;
    public boolean promotes = false;
    public boolean takesWithEP = false;
    public String promotionPiece;

    // used when more than one piece of this type can make this move
    public boolean specifyOriginRow = false;
    public boolean specifyOriginCol = false;

    public ChessMove()
    {
        xDest = 7;
        yDest = 7;
        moveType = Type.NORMAL;
    }

    public ChessMove(ChessPiece cp, int xD, int yD)
    {
        piece = cp;
        xDest = xD;
        yDest = yD;
        moveType = Type.NORMAL;
    }

    public ChessMove(int xD, int yD, Type mt)
    {
        xDest = xD;
        yDest = yD;
        moveType = mt;
    }

    public void setMoveType(Type mt)
    {
        moveType = mt;
    }

    public void setXDest(int x)
    {
        xDest = x;
    }

    public void setYDest(int y)
    {
        yDest = y;
    }

    public int getXDest()
    {
        return xDest;
    }

    public int getYDest()
    {
        return yDest;
    }

    public Type getMoveType()
    {
        return moveType;
    }

    public void setPiece(ChessPiece cp)
    {
        piece = cp;
    }

    public void copy(ChessMove cm)
    {
        cm.setPiece(piece);
        cm.setMoveType(moveType);
    }

    private String captureMoveToString()
    {
        String moveName = "";
        if (piece instanceof Pawn)
        {
            moveName = intToColumn(piece.xCoord);
        }
        moveName = moveName + pieceToString() + "x" + intToColumn(xDest) + ""
                + intToRow(yDest);
        return moveName;
    }

    private String intToColumn(int xCoord)
    {
        String column;
        switch (xCoord)
        {
            case 0:
                column = "a";
                break;
            case 1:
                column = "b";
                break;
            case 2:
                column = "c";
                break;
            case 3:
                column = "d";
                break;
            case 4:
                column = "e";
                break;
            case 5:
                column = "f";
                break;
            case 6:
                column = "g";
                break;
            case 7:
                column = "h";
                break;
            default:
                column = "ZZZ";
        }
        return column;
    }

    private String intToRow(int yCoord)
    {
        return Integer.toString(8 - yCoord);
    }

    private String pieceToString()
    {
        if (piece != null)
        {
            return piece.oneLetterIdentifier();
        }
        else
        {
            return "???";
        }
    }

    public String toString()
    {
        String moveName = "";
        switch (moveType)
        {
            case CASTLE_QS:
                moveName = "O-O-O";
                break;
            case CASTLE_KS:
                moveName = "O-O";
                break;
            case NORMAL:
                if (!captures)
                {
                    moveName = pieceToString() + intToColumn(xDest) + "" + intToRow(yDest);
                }
                else
                {
                    moveName = captureMoveToString();
                }
                break;
        }
        if (takesWithEP)
        {
            moveName += "e.p.";
        }
        if (promotes && promotionPiece != null)
        {
            moveName += "=" + promotionPiece;
        }
        if (givesMate)
        {
            moveName += "#";
        }
        else if (givesCheck)
        {
            moveName += "+";
        }
        return moveName;
    }

    public boolean equals(Object obj)
    {
        if (obj instanceof ChessMove)
        {
            ChessMove cm = (ChessMove) obj;
            return cm.piece.equals(piece) && cm.xDest == xDest && cm.yDest == yDest
                    && cm.moveType.equals(moveType);
        }
        return false;
    }
}
