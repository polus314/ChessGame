package chessgui;

/**
 * This is an action that can be taken on the chess board.
 * 
 * @author John
 */
public enum GameTask 
{
    CREATE_MOVE,
    FIND_BEST_MOVE,
    PLAY_MOVE,
    NONE,
    SET_BOARD_POSITION, 
    SET_PLAYER_TO_MOVE,
    VALIDATE_POSITION,
}
