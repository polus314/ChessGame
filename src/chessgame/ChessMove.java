package chessgame;

/**

 @author jppolecat
 */
public class ChessMove
{

   public ChessPiece piece;
   private int xDest;
   private int yDest;
   private MoveType moveType;
   private float materialRating;
   private float mobilityRating;
   private int hangRating;
   public boolean givesCheck = false;
   public boolean givesMate = false;

   public ChessMove()
   {
      piece = new ChessPiece();
      xDest = 7;
      yDest = 7;
      moveType = MoveType.UNOCCUPIED;
      materialRating = 0.0f;
      mobilityRating = 0.0f;
   }

   public ChessMove(ChessPiece cp, int xD, int yD)
   {
      piece = cp.copyOfThis();
      xDest = xD;
      yDest = yD;
      moveType = MoveType.UNOCCUPIED;
   }

   public ChessMove(int xD, int yD, MoveType mt)
   {
      xDest = xD;
      yDest = yD;
      moveType = mt;
   }

   public void setMoveType(MoveType mt)
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

   public void setMatRating(float mr)
   {
      materialRating = mr;
   }

   public void setMobRating(float mr)
   {
      mobilityRating = mr;
   }

   public void setHangRating(int hr)
   {
      hangRating = hr;
   }

   public float getMaterialRating()
   {
      return materialRating;
   }

   public int getHangRating()
   {
      return hangRating;
   }

   public float getMobilityRating()
   {
      return mobilityRating;
   }

   public MoveType getMoveType()
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

   /**
    This method compares two Chess Moves, useful for sorting

    @param cm
    @return -1 if this is worse, 0 if equal, 1 if better than param
    */
   public int compareTo(ChessMove cm)
   {
      if (cm.hangRating < hangRating)
      {
         return -1;
      }
      if (cm.hangRating == hangRating)
      {
         if (cm.materialRating > materialRating)
         {
            return -1;
         }
         if (cm.materialRating == materialRating)
         {
            if (cm.mobilityRating > mobilityRating)
            {
               return -1;
            }
            if (cm.mobilityRating == mobilityRating)
            {
               return 0;
            }
         }
      }
      return 1;
   }

   private String unoccupiedMoveToString()
   {
      String moveName = "";
      moveName = pieceToString() + intToColumn(xDest) + "" + intToRow(yDest);
      return moveName;
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
      String row;
      switch (yCoord)
      {
         case 0:
            row = "8";
            break;
         case 1:
            row = "7";
            break;
         case 2:
            row = "6";
            break;
         case 3:
            row = "5";
            break;
         case 4:
            row = "4";
            break;
         case 5:
            row = "3";
            break;
         case 6:
            row = "2";
            break;
         case 7:
            row = "1";
            break;
         default:
            row = "ZZZ";
      }
      return row;
   }

   private String pieceToString()
   {
      if(piece instanceof King)
         return "K";
      else if(piece instanceof Queen)
         return "Q";
      else if(piece instanceof Bishop)
         return "B";
      else if(piece instanceof Rook)
         return "R";
      else if(piece instanceof Knight)
         return "N";
      else if(piece instanceof Pawn)
         return "";
      else
         return "???";
   }

   public String toString()
   {
      String moveName;
      switch (moveType)
      {
         case UNOCCUPIED:
            moveName = unoccupiedMoveToString();
            break;
         case CAPTURE:
            moveName = captureMoveToString();
            break;
         case CASTLE_QS:
            moveName = "O-O-O";
            break;
         case CASTLE_KS:
            moveName = "O-O";
            break;
         case PROMOTION:
            if (piece.xCoord != xDest)
            {
               moveName = intToColumn(piece.xCoord) + "x" + intToColumn(xDest)
                     + "" + intToRow(yDest) + "=Q";
            }
            else
            {
               moveName = intToColumn(xDest) + "" + intToRow(yDest)
                     + "=Q";
            }
            break;
         case EN_PASSANT:
            moveName = captureMoveToString() + "e.p.";
            break;
         default:
            moveName = "Error in ChessMove toString()";
      }
      if (givesMate)
      {
         moveName = moveName + "#";
      }
      else if (givesCheck)
      {
         moveName = moveName + "+";
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
