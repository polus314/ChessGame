package chessgame;

import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author John
 */
public class BoardRater {
    private static final int TOTAL_MATERIAL = 39;
    
    /**
    This method takes a ChessBoard and assigns it a material, mobility, and
    hanging rating.

    @param player
    @param cb
    */
   public void rateBoard(ChessBoard cb, ChessPiece.Color player)
   {
        cb.materialRating = matRating(cb);
        cb.mobilityRating = mobRating(cb);
        cb.hangingRating = hangRating(cb);
        cb.overallRating = cb.materialRating * 0.75f + cb.mobilityRating * 0.25f;
   }
   
   /**
    Finds all chess pieces that can attack/defend the square at (xDest, yDest)

    @param cb - board to check
    @param xDest - x position of target square
    @param yDest - y position of target square
    @return array of all chess pieces that can attack this square
    */
   private ArrayList<ChessPiece> aimedHere(ChessBoard cb, int xDest, int yDest)
   {
      ArrayList<ChessPiece> defenders = new ArrayList<>();
      for (int i = 0; i < 8; i++)
      {
         for (int j = 0; j < 8; j++)
         {
            boolean isADefender = false;
            ChessPiece cp = cb.getPieceAt(i, j);
            if (cp == null) // if no piece, don't check for anything
            {
               continue;
            }

            // if regular non-empty piece, check if it can move here and the
            // path is clear
            if (!(cp instanceof Pawn))
            {
               isADefender = cp.canMove(xDest, yDest) && cb.pathIsClear(cp, xDest, yDest);
            }
            // if a pawn, they capture differently than they move
            else
            {
               // square must be one column to the left or right
               if (cp.xCoord - 1 == xDest || cp.xCoord + 1 == xDest)
               {
                  // Black captures down, White captures up
                  if (cp.color == ChessPiece.Color.BLACK && cp.yCoord + 1 == yDest)
                  {
                     isADefender = true;
                  }
                  else if (cp.color == ChessPiece.Color.WHITE && cp.yCoord - 1 == yDest)
                  {
                     isADefender = true;
                  }
               }
            }

            if (isADefender)
            {
               defenders.add(cp);
            }
         }
      }
      return defenders;
   }
   
   /**
    Rates the given board based on how many pieces each player has. A rating
    of 1.0 means all the pieces are white, 0.0 means equal material and -1.0
    means all pieces are black

    @param cb the chessboard to rate
    @return float - rating in terms of material
    */
   private float matRating(ChessBoard cb)
   {
      float wMaterial, bMaterial, totalMaterial;
      wMaterial = bMaterial = 0.0f;
      
      ArrayList<ChessPiece> pieces = cb.getPieces(ChessPiece.Color.WHITE);
      for(ChessPiece piece : pieces)
      {
         wMaterial += piece.value;
      }
      wMaterial -= hangValue(cb, ChessPiece.Color.WHITE);
      
      pieces = cb.getPieces(ChessPiece.Color.BLACK);
      for(ChessPiece piece : pieces)
      {
         bMaterial += piece.value;
      }
      bMaterial -= hangValue(cb, ChessPiece.Color.BLACK);
      totalMaterial = wMaterial + bMaterial;
      
      return (wMaterial - bMaterial) / totalMaterial;
   }
   
    /**
    Rates the given board based on how mobile the pieces of each side are.
    A rating of 1.0 means that all possible moves are ones that white can make,
    a rating of 0.0 means that black and white have an equal number of moves,
    and a rating of -1.0 means that black has the only possible moves.

    @param cb - chess board to evaluate
    @return int - rating indicating whose pieces are more mobile
    */
   private float mobRating(ChessBoard cb)
   {
      float wMobility = howManyMoves(ChessPiece.Color.WHITE, cb);
      float bMobility = howManyMoves(ChessPiece.Color.BLACK, cb);
      float totalMobility = wMobility + bMobility;
      
      return (wMobility - bMobility) / totalMobility;
   }
   
   /**
    Counts how many moves there are for the given color and board position,
    doesn't consider check preventing any of these moves

    @param color - player whose moves should be considered
    @param cb - board position to analyze
    @return int - number of potential moves that are available
    */
   private int howManyMoves(ChessPiece.Color color, ChessBoard cb)
   {
      // TODO - make this more efficient somehow, quadruple for loops is bad
      // Big Picture: using vectors instead of checking every square will make
      // checking a piece's moves faster, but would require re-doing a large
      // chunk of the code.
      // Similarly, storing the pieces in a list, rather than an array could
      // also speed this up, might slow other things down though
      int numMoves = 0;
      for (int i = 0; i < 8; i++)
      {
         for (int j = 0; j < 8; j++)
         {
            ChessPiece cp = cb.getPieceAt(i, j);
            if (cp == null)
            {
               continue;
            }
            if (cp.getColor() == color)
            {
               for (int k = 0; k < 8; k++)
               {
                  for (int m = 0; m < 8; m++)
                  {
                     if (cp.canMove(k, m)
                           && cb.pathIsClear(cp, k, m)
                           && cb.spaceIsEmpty(k, m))
                     {
                        numMoves++;
                     }
                     else if (cp.canMove(k, m)
                           && cb.pathIsClear(cp, k, m)
                           && cb.spaceIsOpen(k, m, cp.getColor())
                           && !(cp instanceof Pawn))
                     {
                        numMoves++;
                     }
                     else if (cp instanceof Pawn)
                     {
                        if (color == ChessPiece.Color.WHITE && j - m == 1
                              && (i - k == 1 || k - i == 1)
                              && cb.getPieceAt(k, m) != null
                              && cb.getPieceAt(k, m).getColor() == ChessPiece.Color.BLACK)
                        {
                           numMoves++;
                        }
                        if (color == ChessPiece.Color.BLACK && m - j == 1
                              && (i - k == 1 || k - i == 1)
                              && cb.getPieceAt(k, m) != null
                              && cb.getPieceAt(k, m).getColor() == ChessPiece.Color.WHITE)
                        {
                           numMoves++;
                        }
                     }
                  }
               }
            }
         }
      }
      return numMoves;
   }
   
   /**
    This method will return the total value of the pieces this color has 
    "hanging" or insufficiently defended

    @param cb
    @param color
    @return int - value of pieces that are not defended sufficiently
    */
   private float hangRating(ChessBoard cb)
   {
       int whiteHangers = hangValue(cb, ChessPiece.Color.WHITE);
       int blackHangers = hangValue(cb, ChessPiece.Color.BLACK);
       return (blackHangers - whiteHangers) / (float)TOTAL_MATERIAL;
   }
   
   private int hangValue(ChessBoard cb, ChessPiece.Color color)
   {
      int valueOfHanging = 0;
      ArrayList<ChessPiece> goodPieces = cb.getPieces(color);
      ArrayList<ChessPiece> badPieces = cb.getPieces(color.opposite());
      for (int i = 0; i < goodPieces.size(); i++)
      {
         boolean isHanging = false;
         ChessPiece goodCP = goodPieces.get(i);
         for (int j = 0; j < badPieces.size(); j++)
         {
            //case 1: piece is attacked by a lower valued piece
            if (cb.canCapture(badPieces.get(j), goodCP.xCoord, goodCP.yCoord)
                  && badPieces.get(j).value < goodCP.value)
            {
               isHanging = true;
            }
            //case 2: piece's attackers are worth less than the defenders
         }
         ArrayList<ChessPiece> piecesInAction = aimedHere(cb, goodCP.xCoord, goodCP.yCoord);
         if (!isHanging)
         {
            ArrayList<ChessPiece> attackers = new ArrayList<>();
            ArrayList<ChessPiece> defenders = new ArrayList<>();
            for (int k = 0; k < piecesInAction.size(); k++)
            {
               if (piecesInAction.get(k).color == color)
               {
                  defenders.add(piecesInAction.get(k));
               }
               else
               {
                  attackers.add(piecesInAction.get(k));
               }
            }

            if (attackers.isEmpty())
            {
               isHanging = false;
            }
            else if (defenders.size() < attackers.size())
            {
               isHanging = true;
            }
            //this is rather confusing, but I think it works
            else if (defenders.size() == attackers.size())
            {
               if (sumValue(defenders, attackers.size() - 1) + goodCP.value
                     >= sumValue(attackers, attackers.size()))
               {
                  isHanging = true;
               }
            }
            else if (defenders.size() > attackers.size())
            {
               isHanging = false;
            }
         }
         if (isHanging)
         {
            valueOfHanging += goodPieces.get(i).value;
         }
      }
      return valueOfHanging;
   }
   
   /**
    This method finds the sum of the values of the "num"-smallest pieces in
    the array, simply summing the entire array if num > length(pieces)

    @param pieces
    @param num
    @return
    */
   private int sumValue(ArrayList<ChessPiece> pieces, int num)
   {
      int sum = 0;
      if (num > pieces.size())
      {
         num = pieces.size();
      }
      Collections.sort(pieces);
      for (int i = 0; i < num; i++)
      {
         sum += pieces.get(i).value;
      }
      return sum;
   }
}
