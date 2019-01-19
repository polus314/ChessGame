package chessgame;

/**
 * Enumerated type that describes how the user wants the chess board to work.
 * SINGLE - one human playing chess against the computer VERSUS - two humans
 * playing against each other SET_UP - pieces can be placed anywhere and in any
 * number on the chessboard REFERENCE - board position will be accompanied by
 * reference materials, e.g. continuations from the current position according
 * to the standard opening UNDECIDED - user hasn't chosen an option, so board
 * cannot be interacted with
 *
 * @author jppolecat
 */
public enum GameMode
{
    SINGLE, VERSUS, SET_UP, REFERENCE, UNDECIDED
}
