package chessgame;

import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import chessgui.GameRequest;

/**
 * Controller class for the chess game. Handles moving pieces as well as
 * checking for capturing, castling, and promotion. Ensures that players are
 * alternating turns and makes the CPU's move if in single player mode.
 *
 * @author John Polus
 */
public class GameController implements Runnable
{

    private ChessBoard board;
    private ChessPiece.Color playerToMove;
    public AI deepBlue;
    private final ArrayList<ChessMove> moveList;
    private final BlockingQueue<GameRequest> tasks;
    private final BlockingQueue<GameRequest> responses;

    /**
     * Default constructor, sets board to standard starting position and human
     * player is White.
     *
     * @param tasks
     * @param responses
     */
    public GameController(BlockingQueue<GameRequest> tasks, BlockingQueue<GameRequest> responses)
    {
        board = new ChessBoard();
        playerToMove = ChessPiece.Color.WHITE;
        deepBlue = new AI(board, playerToMove);
        moveList = new ArrayList<>();
        this.tasks = tasks;
        this.responses = responses;
    }

    public void run()
    {
        GameRequest task, response;
        while (true)
        {
            //check queue
            while ((task = tasks.poll()) != null)
            {
                response = doTask(task);
                sendResponse(response);
            }
        }
    }

    private void sendResponse(GameRequest response)
    {
        responses.add(response);
    }

    private GameRequest task_SetBoardPosition(GameRequest request)
    {
        GameRequest response = new GameRequest(request.task, null, false);
        ChessPiece[][] newPieceArray = (ChessPiece[][]) request.info;
        if (newPieceArray == null)
        {
            return response;
        }
        setBoardPosition(newPieceArray, playerToMove);
        response.success = true;
        return response;
    }

    private GameRequest task_PlayMove(GameRequest request)
    {
        GameRequest response = new GameRequest(request.task, request.info, false);
        ChessMove move = (ChessMove) request.info;
        if (move == null || move.piece.getColor() != playerToMove)
        {
            return response;
        }
        if (move.getMoveType() != ChessMove.Type.NORMAL)
        {
            response.success = board.castle(move);
        }
        else if (move.captures)
        {
            response.success = board.capturePiece(move.piece, move.orig, move.dest);
        }
        else // normal, non-capturing move
        {
            response.success = board.movePiece(move.piece, move.orig, move.dest);
        }
        // check if any pawns made it to the opposite side of the board
        Point spPawn = board.needPromotion();
        if (spPawn != null)
        {
            move.promotes = true;
        }
        move.givesMate = board.checkForMate(playerToMove.opposite());
        move.givesCheck = board.checkForCheck(playerToMove.opposite());

        response.info = move;
        if (response.success)
        {
            playerToMove = playerToMove.opposite();
            moveList.add(move);
        }
        return response;
    }

    private GameRequest task_FindBestMove(GameRequest request)
    {
        GameRequest response = new GameRequest(request.task, null, false);
        deepBlue = new AI(board, playerToMove);
        ChessMove bestMove = deepBlue.findBestMove();
        if (bestMove == null)
        {
            return response;
        }
        response.info = bestMove;
        response.success = true;
        return response;
    }

    /**
     * Request should have an incomplete ChessMove as its info. The ChessMove
     * should have the piece to move, and the desired destination coordinates.
     * This method will determine whether a legal move is possible, as well as
     * whether it involves capturing, giving check, en passant, etc.
     *
     * @param request
     * @return
     */
    private GameRequest task_CreateMove(GameRequest request)
    {
        GameRequest response = new GameRequest(request.task, null, false);
        ChessMove move = (ChessMove) request.info;
        move = board.validateMove(move);
        if (move == null)
        {
            return response;
        }

        response.info = move;
        response.success = true;
        return response;
    }

    private GameRequest task_FindMovesForPiece(GameRequest request)
    {
        GameRequest response = new GameRequest(request.task, null, false);
        Object[] info = (Object[]) request.info;
        // validate the info that was passed
        if (info == null || info.length != 2)
        {
            return response; // bad info passed, task unsuccessful
        }
        ChessPiece[][] pieceArray = (ChessPiece[][]) info[0];
        Point selSpace = (Point) info[1];
        ChessPiece selPiece = pieceArray[selSpace.x][selSpace.y];
        if (pieceArray == null || selSpace == null || selPiece == null || selPiece.color != playerToMove)
        {
            return response; // bad info passed, task unsuccessful
        }

        ChessBoard cb = new ChessBoard(pieceArray);
        // check for castling, since that is specifically excluded from cb.findMoves
        ArrayList<ChessMove> moves = cb.findMoves(selPiece, selSpace);
        if (selPiece instanceof King)
        {
            ChessPiece.Color pieceColor = selPiece.getColor();
            if (cb.canCastleKS(pieceColor))
            {
                moves.add(cb.getCastleKSMove(pieceColor));
            }
            if (cb.canCastleQS(pieceColor))
            {
                moves.add(cb.getCastleQSMove(pieceColor));
            }
        }
        response.info = moves;
        response.success = true;
        return response;
    }

    private GameRequest task_Default(GameRequest request)
    {
        return new GameRequest();
    }

    private GameRequest doTask(GameRequest request)
    {
        switch (request.task)
        {
            case CREATE_MOVE:
                return task_CreateMove(request);
            case SET_BOARD_POSITION:
                return task_SetBoardPosition(request);
            case PLAY_MOVE:
                return task_PlayMove(request);
            case FIND_BEST_MOVE:
                return task_FindBestMove(request);
            case FIND_MOVES_FOR_PIECE:
                return task_FindMovesForPiece(request);
            default:
                return task_Default(request);
        }
    }

    public ChessPiece[][] getPiecesArray() {
        return board.getPiecesArray();
    }

    public ChessPiece.Color getPlayerToMove()
    {
        return playerToMove;
    }

    public void setPlayerToMove(ChessPiece.Color color)
    {
        playerToMove = color;
    }

    public ArrayList<ChessMove> getMoveList()
    {
        return moveList;
    }

    public boolean promoteToThisType(ChessPiece piece)
    {
        Point spPawn = board.needPromotion();
        if (spPawn == null || piece instanceof King || piece instanceof Pawn)
        {
            return false;
        }
        board.setPieceAt(piece, spPawn);
        return true;
    }

    /**
     * Sets the board with the given list of pieces IF the resulting position is
     * legal. If position is illegal, does nothing. Criteria for legality are
     * listed in ChessBoard.checkPositionIsLegal()
     *
     * @param pieces - list of pieces specifying the board position to set up
     * @param playerToMove - the player whose move it is next
     * @return whether board was legal and position was updated
     */
    public boolean setBoardPosition(ChessPiece[][] pieces,
            ChessPiece.Color playerToMove)
    {
        ChessBoard temp = new ChessBoard(pieces);
        this.playerToMove = playerToMove;
        if (temp.checkPositionIsLegal())
        {
            board = temp;
            deepBlue = new AI(temp, playerToMove);
            return true;
        }
        return false;
    }

    public void startNewGame()
    {
        playerToMove = ChessPiece.Color.WHITE;
        setBoardPosition(new ChessBoard().getPiecesArray(), playerToMove);
        moveList.clear();
    }

    public ArrayList<ChessMove> solveForMate(ChessPiece.Color color, int moves,
            boolean quickly)
    {
        return null; //deepBlue.solveForMate(color, moves, quickly);
    }

    public ChessMove findBestMove()
    {
        return deepBlue.findBestMove();
    }

    /**
     * Returns true if the current game has ended.
     *
     * @return boolean - whether or not game is over
     */
    public boolean isGameOver()
    {
        deepBlue = new AI(board, playerToMove);
        return deepBlue.isGameOver();
    }

    /**
     * Returns the color that won the game. If the game hasn't ended or is a
     * tie, returns null.
     *
     * @return
     */
    public ChessPiece.Color getWinningSide()
    {
        return deepBlue.getWinningSide();
    }



    /**
     * Saves the given chess board's position to a file.
     *
     * @param cb board whose position should be saved
     * @param filename name of file to store position in
     * @return whether the position was saved successfully
     */
    public static boolean savePositionToFile(ChessBoard cb, String filename)
    {
        FileOutputStream file;
        byte buffer[] = cb.toString().getBytes();
        try
        {
            if (!filename.endsWith(".txt"))
            {
                filename += ".txt";
            }
            file = new FileOutputStream(filename);
            file.write(buffer);
        } catch (FileNotFoundException fnfe)
        {
            return false;
        } catch (IOException ioe)
        {
            return false;
        }
        return true;
    }
}
