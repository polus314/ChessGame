package test;

import chessgame.BoardRater;
import chessgame.ChessBoard;
import chessgame.ChessPiece;
import chessgame.GameState;
import chessutil.FileReader;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardRaterTest {
    @Test
    void RateBoard_StartingPosition() {
        GameState gs = setupAndRatePosition("NewGame.txt");

        assert gs.materialRating == 0.0f;
        assert gs.mobilityRating == 0.0f;
        assert gs.overallRating == 0.0f;
    }

    @Test
    void RateBoard_KvsKR() {
        GameState gs = setupAndRatePosition("K-KR.txt");

        assert gs.materialRating < 0.0f;
        assert gs.mobilityRating < 0.0f;
        assert gs.overallRating < 0.0f;
    }

    @Test
    void RateBoard_Queens() {
        GameState gs = setupAndRatePosition("Queens.txt");

        assert gs.materialRating > 0.0f;
        assert gs.mobilityRating > 0.0f;
        assert gs.overallRating > 0.0f;
    }

    @Test
    void RateBoard_SomePiecesMissing() {
        GameState gs = setupAndRatePosition("SomePiecesMissing.txt");

        assert gs.materialRating == 0.0f;
        assert gs.mobilityRating == 0.0f;
        assert gs.overallRating == 0.0f;
    }

    @Test
    void RateBoard_BlackMobility() {
        GameState gs = setupAndRatePosition("BlackMobility.txt");

        assert gs.materialRating == 0.0f;
        assert gs.mobilityRating < 0.0f;
        assert gs.overallRating < 0.0f;
    }

    @Test
    void RateBoard_HangingQueen() {
        GameState gs = setupAndRatePosition("HangingQueen.txt");

        assert gs.materialRating > 0.0f;
        assert gs.mobilityRating < 0.0f;
        assert gs.overallRating > 0.0f;
    }

    @Test
    void RateBoard_HangingQueen2() {
        GameState gs = setupAndRatePosition("HangingQueen2.txt");

        assert gs.materialRating > 0.0f;
        assert gs.mobilityRating < 0.0f;
        assert gs.overallRating > 0.0f;
    }

    private GameState setupAndRatePosition(String filename) {
        ChessBoard cb = new ChessBoard();
        assert FileReader.loadPositionFromFile("src/test/positions/" + filename, cb);
        GameState gs = new GameState(cb, null);
        BoardRater rater = new BoardRater();

        rater.rateBoard(gs, ChessPiece.Color.WHITE);
        return gs;
    }
}