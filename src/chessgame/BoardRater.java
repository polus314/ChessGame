package chessgame;

import chessgame.AI.GameState;
import javafx.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

/**
 *
 * @author John
 */
public class BoardRater
{

    private static final int TOTAL_MATERIAL = 39;

    /**
     * This method takes a ChessBoard and assigns it a material, mobility, and
     * hanging rating.
     *
     * @param player
     * @param gs
     */
    public void rateBoard(GameState gs, ChessPiece.Color player)
    {
        if (gs == null || gs.board == null)
        {
            return;
        }
        if (gs.board.getPieces().size() < 8){
            gs.overallRating = getEndgameRating(gs, player);
            return;
        }
        ChessBoard cb = gs.board;
        gs.materialRating = matRating(cb);
        gs.mobilityRating = mobRating(cb);
        gs.hangingRating = hangRating(cb);
        gs.overallRating = gs.materialRating * 0.75f + gs.mobilityRating * 0.25f;
    }

    /**
     * Finds all chess pieces that can attack/defend the square at (xDest,
     * yDest)
     *
     * @param cb - board to check
     * @param xDest - x position of target square
     * @param yDest - y position of target square
     * @return array of all chess pieces that can attack this square
     */
    private ArrayList<ChessPiece> aimedHere(ChessBoard cb, int xDest, int yDest)
    {
        Point dest = new Point(xDest, yDest);
        ArrayList<ChessPiece> defenders = new ArrayList<>();
        for (int i = 0; i < ChessBoard.WIDTH; i++) {
            for (int j = 0; j < ChessBoard.HEIGHT; j++) {
                Point cpSpace = new Point(i, j);
                ChessPiece cp = cb.getCopyOfPieceAt(cpSpace);

                // if regular non-empty piece, check if it can move here and the
                // path is clear
                if (cb.canCapture(cp, cpSpace, dest)) {
                    defenders.add(cp);
                }
            }
        }

        return defenders;
    }

    /**
     * Rates the given board based on how many pieces each player has. A rating
     * of 1.0 means all the pieces are white, 0.0 means equal material and -1.0
     * means all pieces are black
     *
     * @param cb the chessboard to rate
     * @return float - rating in terms of material
     */
    private float matRating(ChessBoard cb)
    {
        float wMaterial, bMaterial, totalMaterial;
        wMaterial = bMaterial = 0.0f;

        ArrayList<ChessPiece> pieces = cb.getPieces(ChessPiece.Color.WHITE);
        for (ChessPiece piece : pieces)
        {
            wMaterial += piece.value;
        }
        wMaterial -= hangValue(cb, ChessPiece.Color.WHITE);

        pieces = cb.getPieces(ChessPiece.Color.BLACK);
        for (ChessPiece piece : pieces)
        {
            bMaterial += piece.value;
        }
        bMaterial -= hangValue(cb, ChessPiece.Color.BLACK);
        totalMaterial = wMaterial + bMaterial;

        return (wMaterial - bMaterial) / totalMaterial;
    }

    /**
     * Rates the given board based on how mobile the pieces of each side are. A
     * rating of 1.0 means that all possible moves are ones that white can make,
     * a rating of 0.0 means that black and white have an equal number of moves,
     * and a rating of -1.0 means that black has the only possible moves.
     *
     * @param cb - chess board to evaluate
     * @return int - rating indicating whose pieces are more mobile
     */
    private float mobRating(ChessBoard cb)
    {
        float wMobility = howManyMoves(ChessPiece.Color.WHITE, cb);
        float bMobility = howManyMoves(ChessPiece.Color.BLACK, cb);
        float totalMobility = wMobility + bMobility;

        return (wMobility - bMobility) / totalMobility;
    }

    /**
     * Counts how many moves there are for the given color and board position,
     * doesn't consider check preventing any of these moves
     *
     * @param color - player whose moves should be considered
     * @param cb - board position to analyze
     * @return int - number of potential moves that are available
     */
    private int howManyMoves(ChessPiece.Color color, ChessBoard cb)
    {
        return cb.findAllMoves(color).size();
    }

    /**
     * This method will return the total value of the pieces this color has
     * "hanging" or insufficiently defended
     *
     * @param cb
     * @return int - value of pieces that are not defended sufficiently
     */
    private float hangRating(ChessBoard cb)
    {
        int whiteHangers = hangValue(cb, ChessPiece.Color.WHITE);
        int blackHangers = hangValue(cb, ChessPiece.Color.BLACK);
        return (blackHangers - whiteHangers) / (float) TOTAL_MATERIAL;
    }

    private int hangValue(ChessBoard cb, ChessPiece.Color color)
    {
        int valueOfHanging = 0;
        ArrayList<Point> goodPieceSpaces = cb.getPieceSpaces(color);
        ArrayList<Point> badPieceSpaces = cb.getPieceSpaces(color.opposite());
        for (int i = 0; i < goodPieceSpaces.size(); i++)
        {
            boolean isHanging = false;

            Point goodSpace = goodPieceSpaces.get(i);
            ChessPiece goodCP = cb.getCopyOfPieceAt(goodSpace);
            for (int j = 0; j < badPieceSpaces.size(); j++)
            {
                Point badSpace = badPieceSpaces.get(j);
                ChessPiece badCP = cb.getCopyOfPieceAt(badSpace);

                //case 1: piece is attacked by a lower valued piece
                if (cb.canCapture(badCP, badSpace, goodSpace)
                        && badCP.value < goodCP.value)
                {
                    isHanging = true;
                }
                //case 2: piece's attackers are worth less than the defenders
            }
            ArrayList<ChessPiece> piecesInAction = aimedHere(cb, goodSpace.x, goodSpace.y);
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
                } //this is rather confusing, but I think it works
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
                valueOfHanging += goodCP.value;
            }
        }
        return valueOfHanging;
    }

    /**
     * This method finds the sum of the values of the "num"-smallest pieces in
     * the array, simply summing the entire array if num > length(pieces)
     *
     * @param pieces
     * @param num
     * @return
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

    private float getEndgameRating(GameState gs, ChessPiece.Color player){
        float matRating = matRating(gs.board);
        float kingProx = kingProximity(gs);
        float oppKingMob = kingMobility(gs, player);
        float hangRating = hangRating(gs.board);

        return hangRating * 0.15f + matRating * 0.15f + kingProx * 0.65f + oppKingMob * 0.05f;
    }

    private float MAX_DISTANCE = 9.8995f;

    private float kingProximity(GameState gs) {
        Point spWhiteKing = gs.board.find(new King(ChessPiece.Color.WHITE));
        Point spBlackKing = gs.board.find(new King(ChessPiece.Color.BLACK));

        if (spWhiteKing == null || spBlackKing == null) {
            return 0.0f;
        }
        float xDiff = Math.abs(spWhiteKing.x - spBlackKing.x);
        float yDiff = Math.abs(spWhiteKing.y - spBlackKing.y);

        float distance = (float)Math.sqrt(Math.pow(xDiff, 2.0) + Math.pow(yDiff, 2.0));

        // normalize between 0.0 and 1.0, lower distance is good (i.e. closer to 1)
        return 1.0f - (distance / MAX_DISTANCE);
    }

    private float kingMobility(GameState gs, ChessPiece.Color player) {
        ChessBoard board = new ChessBoard(gs.board);

        Point spOppKing = board.find(new King(player.opposite()));
        if (spOppKing == null) {
            return 0.0f;
        }

        Point initialSpace = spOppKing;
        King oppKing = (King)board.getCopyOfPieceAt(spOppKing);

        // Find all squares king can move to (without passing through check)
        HashSet<Vector> moveSet = oppKing.getMoveSet();
        HashSet<Point> availableSquares = new HashSet<>();
        ArrayList<Point> newSquares = new ArrayList<>();
        availableSquares.add(spOppKing);
        newSquares.add(spOppKing);

        while (!newSquares.isEmpty()) {
            Point nextSpace = newSquares.remove(0);
            board.setPieceAt(null, spOppKing);
            board.setPieceAt(oppKing, nextSpace);
            spOppKing = nextSpace;

            for (Vector move : moveSet) {
                int xf = move.dx + spOppKing.x;
                int yf = move.dy + spOppKing.y;
                Point possDest = new Point(xf, yf);

                // if there is a legal square that we haven't looked at yet, increment count
                if (board.canMovePiece(oppKing, spOppKing, possDest) && !availableSquares.contains(possDest)) {
                    availableSquares.add(possDest);
                    newSquares.add(possDest);
                }
            }
        }

        return 1.0f - (availableSquares.size() / 64.0f);
    }
}
