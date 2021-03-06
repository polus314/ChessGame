package chessgame;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;

/**
 * This class represents an entity that can analyze a chess board in order to
 * find better and worse moves to play. Generates future board positions, rates
 * them, and finds the one most advantageous to the current player.
 *
 * @author John Polus
 */
public class AI
{

    public static final float FLOAT_ERROR = 0.0001f;
    public static final int MAX_BRANCH = 5;

    private static final boolean DEBUG = true;

    // Enumerates the algorithms that can be used to evaluate the position
    public enum Algorithm
    {
        DFS, BFS, GREEDY, MINI_MAX, SIMPLE
    };

    /**
     * This is a "struct" used in the game tree to hold information that is
     * necessary for finding the best move
     */
    protected class GameState implements Comparable<GameState>
    {
        // board represents the position of the pieces

        public ChessBoard board;

        // move is the move used to get here from the parent state
        public ChessMove move;

        // miniMaxRating is the rating given to this node, inherited from its
        // child
        public float miniMaxRating;
        public float overallRating;
        public float materialRating;
        public float mobilityRating;
        public float hangingRating;

        public boolean checkedForMiniMax;

        public GameState(ChessBoard b, ChessMove m)
        {
            board = b;
            move = m;
            miniMaxRating = 3.14f;
            checkedForMiniMax = false;
        }

        public GameState(ChessBoard b, ChessMove m, int mmR)
        {
            board = b;
            move = m;
            miniMaxRating = mmR;
            checkedForMiniMax = false;
        }

        /**
         * This method compares this to a given GameState gs to see which is
         * greater. Compares their overall rating
         *
         * @param rhs board to compare this to
         * @return -1 if this is less, 0 if equal, 1 if this is greater
         */
        @Override
        public int compareTo(GameState rhs)
        {
            if (rhs == null)
            {
                return 1;
            }
            if (overallRating > rhs.overallRating)
            {
                return 1;
            }
            return overallRating == rhs.overallRating ? 0 : -1;
        }
    }

    protected ChessBoard gameBoard;
    protected ChessPiece.Color playerToMove;
    public Algorithm algorithm;
    protected ArrayList<ChessMove> pathToMate;
    protected BoardRater rater;

    /**
     * Default constructor, initializes the chessboard to the start of a new
     * game
     */
    public AI()
    {
        rater = new BoardRater();
        gameBoard = new ChessBoard();
        playerToMove = ChessPiece.Color.WHITE;
        algorithm = Algorithm.SIMPLE;
    }

    /**
     * Parameterized constructor, sets the chessboard to cb
     *
     * @param cb - board that AI should use for calculations
     * @param playerToMove - color of player who has the next move
     */
    public AI(ChessBoard cb, ChessPiece.Color playerToMove)
    {
        rater = new BoardRater();
        gameBoard = new ChessBoard(cb);
        this.playerToMove = playerToMove;
        algorithm = Algorithm.SIMPLE;
    }

    private void dumpGameTree(Tree<GameState> gameTree, String filename)
    {
        try
        {
            PrintWriter writer = new PrintWriter(filename, "UTF-8");
            writer.println("Game Tree starts here");
            for (Tree<GameState> tree : gameTree.children)
            {
                String str = tree.info.move.toString();
                str += "\tOVR: " + tree.info.overallRating;
                str += "\tMATERIAL: " + tree.info.materialRating;
                str += "\tMOBILITY: " + tree.info.mobilityRating;
                str += "\tHANGING : " + tree.info.hangingRating;
                writer.println(str);
            }
            writer.println("One level of Game Tree printed successfully");
            writer.flush();
        } catch (Exception e)
        {
            System.out.println("Oops, debug printing failed!");
        }

    }

    /**
     * This method tests all moves and evaluate the positions that result. The
     * moves are then ranked and the best one is chosen.
     *
     * @return ChessMove - the best one it can find
     */
    public ChessMove findBestMove()
    {
        int depth = 5;
        long startTime = System.currentTimeMillis();
        Tree<GameState> gameTree = generateGameTree(depth, true);
        System.out.println("Time elapsed: " + (System.currentTimeMillis() - startTime) + "ms");
        if (gameTree.children.isEmpty())
        {
            return null;
        }

        switch (algorithm)
        {
            case SIMPLE:
                return simpleBestMove(gameTree);
            case MINI_MAX:
                return miniMaxBestMove(gameTree);
            default:
                return new ChessMove();
        }
    }

    /**
     * Takes the given parent and generates all legal children for that tree,
     * including giving each of them their ratings
     *
     * @param parentTree
     * @param color
     * @return int number of children generated
     */
    private ArrayList<Tree<GameState>> generateAllChildren(Tree<GameState> parentTree, ChessPiece.Color color)
    {
        if (parentTree == null || parentTree.info == null)
        {
            return null;
        }
        ChessBoard curBoard = parentTree.info.board;
        ArrayList<ChessMove> moveList = curBoard.findAllMoves(color);

        for (int i = 0; i < moveList.size(); i++)
        {
            ChessMove move = moveList.get(i);
            ChessBoard newBoard = curBoard.advancePosition(move);
            GameState newState = new GameState(newBoard, move);
            rater.rateBoard(newState, color.opposite());
            Tree<GameState> child = new Tree<>(newState);
            parentTree.children.add(child);
        }

        parentTree.children.trimToSize();
        return parentTree.children;
    }

    /**
     * Generates all possible positions down to the given depth. A depth of 0
     * will return just the root node
     *
     * @param depth number of plies to expand the game tree
     * @return Tree - the root node, which has the other expanded nodes attached
     */
    protected Tree<GameState> generateGameTree(int depth, boolean trim)
    {
        ChessPiece.Color playerColor = playerToMove;

        // create the root node
        GameState root = new GameState(gameBoard, null);
        rater.rateBoard(root, playerToMove);
        Tree<GameState> gameTree = new Tree<>(root);

        // maintain a list of current level nodes and a list of all their children
        ArrayList<Tree<GameState>> curLevel = new ArrayList<>();
        ArrayList<Tree<GameState>> childLevel = new ArrayList<>();

        // current level starts out as just the root node
        curLevel.add(gameTree);
        int curDepth = 0;

        while (curDepth < depth)
        {
            // take all the nodes at the current level and generate their children
            for (Tree tree : curLevel)
            {
                generateAllChildren(tree, playerColor);
                if (trim)
                {
                    trimTree(tree, playerColor == ChessPiece.Color.WHITE);
                }
                childLevel.addAll(tree.children);
            }
            // the children become the current nodes for the next iteration
            curLevel = childLevel;
            childLevel = new ArrayList<>();
            playerColor = playerColor.opposite();
            curDepth++;
        }

        if (DEBUG)
        {
            dumpGameTree(gameTree, "gameTree.txt");
        }

        return gameTree;
    }

    /**
     * Returns the color that has the won the game
     *
     * @return Color - color that won the game, null if tied or game isn't over
     */
    public ChessPiece.Color getWinningSide()
    {
        if (!isGameOver())
        {
            return null;
        }

        if (gameBoard.checkForCheck(ChessPiece.Color.WHITE))
        {
            return ChessPiece.Color.BLACK;
        }
        else if (gameBoard.checkForCheck(ChessPiece.Color.BLACK))
        {
            return ChessPiece.Color.WHITE;
        }
        else
        {
            return null;
        }
    }

    /**
     * Returns whether or not the game on gameBoard is over, whether that be
     * win, draw or loss.
     *
     * @return boolean - true if play should halt, false otherwise
     */
    public boolean isGameOver()
    {
        return gameBoard.checkForGameOver(playerToMove);
    }

    private ChessMove miniMaxBestMove(Tree<GameState> gameTree)
    {

        boolean max = playerToMove == ChessPiece.Color.WHITE;
        float bestRating = runMiniMax(gameTree, max);

        for (Tree<GameState> child : gameTree.children)
        {
            if (Math.abs(child.info.miniMaxRating - bestRating) < FLOAT_ERROR)
            {
                return child.info.move;
            }
        }
        // if all else fails, return best first move
        System.out.println("Minimax algorithm has failed");
        int indexOfBest = 0;
        bestRating = max ? -999.0f : 999.0f;
        for (int i = 0; i < gameTree.children.size(); i++)
        {
            Tree<GameState> child = gameTree.children.get(i);
            if ((max && child.info.materialRating > bestRating)
                    || (!max && child.info.materialRating < bestRating))
            {
                indexOfBest = i;
            }
        }
        return (gameTree.children.get(indexOfBest)).info.move;
    }

    /**
     * Recursive method that traverses the tree and assigns a mini-max rating to
     * every tree node. Leaf nodes get their ratings from their board position,
     * all other nodes get their value from either the largest or smallest value
     * among their children, depending on if whose turn it is to move
     *
     * @param gameTree current node being examined
     * @param max indicates whether to find the max or min
     * @return float the mini-max rating of this node
     */
    private float runMiniMax(Tree<GameState> gameTree, boolean max)
    {
        // leaf nodes just set their own value, not a problem
        if (gameTree.children == null || gameTree.children.isEmpty())
        {
            GameState gs = gameTree.info;
            gameTree.info.checkedForMiniMax = true;
            gameTree.info.miniMaxRating = 5 * gs.materialRating + 2 * gs.mobilityRating + 3 * gs.hangingRating;
            return gameTree.info.miniMaxRating;
        }
        else // non-leaf nodes check their children
        {
            float childRating, extreme = 0.0f; // highest/lowest value seen so far
            for (Tree<GameState> child : gameTree.children)
            {
                // if child doesn't have a value yet, recurse
                if (!child.info.checkedForMiniMax)
                {
                    runMiniMax(child, !max);
                }
                // now that child has a value, compare to other children
                childRating = child.info.miniMaxRating;
                if ((max && childRating > extreme)
                        || (!max && childRating < extreme))
                {
                    extreme = childRating;
                }
            }
            gameTree.info.checkedForMiniMax = true;
            gameTree.info.miniMaxRating = extreme;
            return extreme;
        }
    }

    /**
     * Searches one ply deep and returns the best move from that.
     *
     * @param gameTree the pre-generated gameTree, must be at least one ply deep
     * @return move that is best for the playerToMove
     */
    private ChessMove simpleBestMove(Tree<GameState> gameTree)
    {
        boolean max = playerToMove == ChessPiece.Color.WHITE;
        if (gameTree.numChildren() == 0)
        {
            return null;
        }
        ChessMove bestMove = null;
        float bestRank = max ? -1.0f : 1.0f;
        for (Tree<GameState> tree : gameTree.children)
        {
            float childRating = tree.info.overallRating;
            if ((max && childRating > bestRank)
                    || (!max && childRating < bestRank))
            {
                bestRank = childRating;
                bestMove = tree.info.move;
            }
        }
        return bestMove;
    }

    /**
     * Set the algorithm used by this AI.
     *
     * @param a algorithm used by this AI
     */
    public void setAlgorithm(Algorithm a)
    {
        algorithm = a;
    }

    private void trimTree(Tree<GameState> tree, boolean max)
    {
        // make sure there are children to trim
        if (tree.children.size() <= MAX_BRANCH)
        {
            return;
        }

        // sort best positions to the front (negative is good for black)
        Collections.sort(tree.children);
        if (max)
        {
            Collections.reverse(tree.children);
        }

        // trim off past the given threshold, or keep all if less than MAX_BRANCH
        int oldLength = tree.children.size();
        tree.children.subList(MAX_BRANCH, oldLength).clear();

        for (Tree<GameState> child : tree.children)
        {
            trimTree(child, !max);
        }
    }
}
