package chessgame;

import java.util.ArrayList;
import java.util.Collections;

/**
 This class represents an entity that can analyze a chess board in order to
 find better and worse moves to play. Generates future board positions, rates
 them, and finds the one most advantageous to the current player. 
 @author John Polus
 */
public class AI
{
   public static final float FLOAT_ERROR = 0.0001f;
   
   // Enumerates the algorithms that can be used to evaluate the position
   public enum Algorithm { DFS, BFS, MINI_MAX };
   
   //public enum Heuristic { OPENING };
   // have subclasses with separate heuristics?
   
   /**
   This is a "struct" used in the game tree to hold information that is
   necessary for finding the best move
   */
   private class GameState 
   {
      // board represents the position of the pieces
      public ChessBoard board;
      
      // move is the move used to get here from the parent state
      public ChessMove move;
      
      // miniMaxRating is the rating given to this node, inherited from its
      // child
      public float miniMaxRating;
      
      public boolean checkedForMiniMax;
      
      public GameState(ChessBoard b, ChessMove m)
      {
         board = b;
         move = m;
         miniMaxRating = 0;
         checkedForMiniMax = false;
      }
      
      public GameState(ChessBoard b, ChessMove m, int mmR)
      {
         board = b;
         move = m;
         miniMaxRating = mmR;
         checkedForMiniMax = false;
      }
   }
   
   private final ChessBoard gameBoard;
   private final ChessPiece.Color playerToMove;
   private final boolean gameOver;
   private Algorithm algorithm;

   /**
    Default constructor, initializes the chessboard to the start of a new game
    */
   public AI()
   {
      gameBoard = new ChessBoard();
      playerToMove = ChessPiece.Color.WHITE;
      gameOver = gameBoard.checkForMate(playerToMove);
      algorithm = Algorithm.MINI_MAX;
   }

   /**
    Parameterized constructor, sets the chessboard to cb

    @param cb - board that AI should use for calculations
    @param playerToMove - color of player who has the next move
    */
   public AI(ChessBoard cb, ChessPiece.Color playerToMove)
   {
      gameBoard = new ChessBoard(cb);
      this.playerToMove = playerToMove;
      gameOver = gameBoard.checkForMate(playerToMove);
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
   
   private ChessMove bfsBestMove()
   {
      // search breadth first
      // return move that is best
      return new ChessMove();
   }
   
   private ChessMove dfsBestMove()
   {
      // search depth first
      // return move that is best
      return new ChessMove();
   }

   /**
    This method tests all moves and evaluate the positions that result. The
    moves are then ranked and the best one is chosen.

    @return ChessMove - the best one it can find
    */
   public ChessMove findBestMove()
   {
      switch(algorithm)
      {
         case BFS: return bfsBestMove();
         case DFS: return dfsBestMove();
         case MINI_MAX: return miniMaxBestMove();
         default: return new ChessMove();
      }
   }

   /**
    Takes the given parent and generates all legal children for that tree,
    including giving each of them their ratings
    
   @param parentTree
   @param color
   @return int number of children generated
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
         rateBoard(newBoard);
         Tree<GameState> child = new Tree<>(new GameState(newBoard, move));
         parentTree.children.add(child);
      }

      return parentTree.children;
   }
   
   /**
   Generates all possible positions down to the given depth. A depth of 0 will
   return just the root node
   
   @param depth number of plies to expand the game tree
   @return Tree - the root node, which has the other expanded nodes attached
   */
   private Tree<GameState> generateGameTree(int depth)
   {
      ChessPiece.Color playerColor = playerToMove;
      
      // create the root node
      rateBoard(gameBoard);
      Tree<GameState> gameTree = new Tree<>(new GameState(gameBoard, null));
      
      // maintain a list of current level nodes and a list of all their children
      ArrayList<Tree<GameState>> curLevel = new ArrayList<>(); 
      ArrayList<Tree<GameState>> childLevel = new ArrayList<>();
      
      // current level starts out as just the root node
      curLevel.add(gameTree);
      int curDepth = 0;
      
      while(curDepth < depth)
      {
         // take all the nodes at the current level and generate their children
         for(Tree tree : curLevel)
         {
            childLevel.addAll(generateAllChildren(tree, playerColor));
         }
         // the children become the current nodes for the next iteration
         curLevel = childLevel;
         childLevel = new ArrayList<>();
         playerColor = playerColor.opposite();
         curDepth++;
      }
      
      return gameTree;
   }
   
   /**
    Returns the color that has the won the game

    @return Color - color that won the game, null if tied or game isn't
    over
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
    This method will return the total value of the pieces this color has 
    "hanging" or insufficiently defended

    @param cb
    @param color
    @return int - value of pieces that are not defended sufficiently
    */
   private int hangRating(ChessBoard cb, ChessPiece.Color color)
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
    Returns whether or not the game on gameBoard is over, whether that be win,
    draw or tie.

    @return boolean - true if play should halt, false otherwise
    */
   public boolean isGameOver()
   {
      return gameOver;
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
      float rating, wMaterial, bMaterial, totalMaterial;
      wMaterial = bMaterial = 0.0f;
      
      ArrayList<ChessPiece> pieces = cb.getPieces(ChessPiece.Color.WHITE);
      for(ChessPiece piece : pieces)
      {
         wMaterial += piece.value;
      }
      
      pieces = cb.getPieces(ChessPiece.Color.BLACK);
      for(ChessPiece piece : pieces)
      {
         bMaterial += piece.value;
      }
      totalMaterial = wMaterial + bMaterial;
      
      return (wMaterial - bMaterial) / totalMaterial;
   }
   
   private ChessMove miniMaxBestMove()
   {
      
      boolean max = playerToMove == ChessPiece.Color.WHITE;
      int depth = 3;
      long startTime = System.currentTimeMillis();
      Tree<GameState> gameTree = generateGameTree(depth);
      System.out.println("Time elapsed: " + (System.currentTimeMillis() - startTime) + "ms");
      if (gameTree.children.isEmpty())
      {
         return null;
      }

      float bestRating = runMiniMax(gameTree, max);
      
      for(Tree<GameState> child : gameTree.children)
      {
         if(Math.abs(child.info.miniMaxRating - bestRating) < FLOAT_ERROR)
         {
            return child.info.move;
         }
      }
      // if all else fails, return best first move
      int indexOfBest = 0;
      bestRating = 0.0f;
      for(int i = 0; i < gameTree.children.size(); i++)
      {
         Tree<GameState> child = gameTree.children.get(i);
         if((max && child.info.board.materialRating > bestRating) ||
            (!max && child.info.board.materialRating < bestRating))
         {
            indexOfBest = i;
         }
      }
      return (gameTree.children.get(indexOfBest)).info.move;
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
    This method takes a ChessBoard and assigns it a material, mobility, and
    hanging rating.

    @param cb
    @param moveList
    */
   private void rateBoard(ChessBoard cb)
   {
      cb.materialRating = matRating(cb);
      cb.mobilityRating = 0.0f;//mobRating(cb);
      
//      //TODO - rewrite hangRating method and do this in there
//      // also LOOK CAREFULLY, I am switching these because a positive should
//      // be good for that color
//      float wHRating = hangRating(cb, Color.BLACK);
//      float bHRating = hangRating(cb, Color.WHITE);
//      float totalHRating = wHRating + bHRating;
//      if(totalHRating < (FLOAT_ERROR))
//      {
         cb.hangingRating = 0.0f;
//      }
//      else
//      {
//         cb.hangingRating = (wHRating - bHRating) / totalHRating;
//      }
   }
   
   /**
   Recursive method that traverses the tree and assigns a mini-max rating to
   every tree node. Leaf nodes get their ratings from their board position,
   all other nodes get their value from either the largest or smallest value
   among their children, depending on if whose turn it is to move
   
   @param gameTree current node being examined
   @param max indicates whether to find the max or min
   @return float the mini-max rating of this node
   */
   private float runMiniMax(Tree<GameState> gameTree, boolean max)
   {
      // leaf nodes just set their own value, not a problem
      if(gameTree.children == null || gameTree.children.isEmpty())
      {
         ChessBoard cb = gameTree.info.board;
         gameTree.info.checkedForMiniMax = true;
         gameTree.info.miniMaxRating = (10 * cb.materialRating) + cb.mobilityRating + cb.hangingRating;
         return gameTree.info.miniMaxRating;
      }
      else // non-leaf nodes check their children
      {
         float childRating, extreme = 0.0f; // highest/lowest value seen so far
         int count = 0;
         for(Tree<GameState> child : gameTree.children)
         {
            // if child doesn't have a value yet, recurse
            if(!child.info.checkedForMiniMax)
            {
               runMiniMax(child, !max);
            }
            // now that child has a value, compare to other children
            childRating = child.info.miniMaxRating;
            if((max && childRating > extreme) ||
               (!max && childRating < extreme))
            {
               extreme = childRating;
               //System.out.println("Extreme!!" + count++);
            }
         }
         gameTree.info.checkedForMiniMax = true;
         gameTree.info.miniMaxRating = extreme;
         //System.out.println(extreme);
         return extreme;
      }
   }
   
   /**
   Set the algorithm used by this AI.
   
   @param a algorithm used by this AI
   */
   public void setAlgorithm(Algorithm a)
   {
      algorithm  = a;
   }

   /**
    Sorts the given list of boards in ascending order

    @param list - the list of boards to be sorted
    */
   private void sortBoardsA(ArrayList<ChessBoard> list)
   {
      Collections.sort(list);
   }

   /**
    Sorts the given list of boards in descending order

    @param list - the list of boards to be sorted
    */
   private void sortBoardsD(ArrayList<ChessBoard> list)
   {
      Collections.sort(list);
      Collections.reverse(list);
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
