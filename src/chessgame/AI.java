package chessgame;

import java.util.ArrayList;
import java.util.Collections;

/**
 Kept for posterity:

 //Shoutout to my homeboy Miguel Colunga-Santoyo

 */
/**
 This class represents an entity that can analyze a chess board in order to
 find better and worse moves to play.

 @author jppolecat
 */
public class AI
{

   private final ChessBoard gameBoard;
   private final PieceColor playerToMove;
   private final boolean gameOver;

   /**
    Default constructor, initializes the chessboard to the start of a new game
    */
   public AI()
   {
      gameBoard = new ChessBoard();
      playerToMove = PieceColor.WHITE;
      gameOver = gameBoard.checkForMate(playerToMove);
   }

   /**
    Parameterized constructor, sets the chessboard to cb

    @param cb - board that AI should use for calculations
    @param playerToMove - color of player who has the next move
    */
   public AI(ChessBoard cb, PieceColor playerToMove)
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
            if(cp == null) // if no piece, don't check for anything
               continue;
            
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
                  if (cp.color == PieceColor.BLACK && cp.yCoord + 1 == yDest)
                  {
                     isADefender = true;
                  }
                  else if (cp.color == PieceColor.WHITE && cp.yCoord - 1 == yDest)
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
   Returns whether or not the game on gameBoard is over, whether that be win,
   draw or tie.
   
   @return boolean - true if play should halt, false otherwise
   */
   public boolean isGameOver()
   {
      return gameOver;
   }
   
   /**
   Returns the color that has the won the game
   
   @return PieceColor - color that won the game, null if tied or game isn't
   over
   */
   public PieceColor getWinningSide()
   {
      if(!isGameOver())
      {
         return null;
      }
      
      if(gameBoard.checkForCheck(PieceColor.WHITE))
      {
         return PieceColor.BLACK;
      }
      else if(gameBoard.checkForCheck(PieceColor.BLACK))
      {
         return PieceColor.WHITE;
      }
      else
      {
         return null;
      }
   }

   /**
    This method tests all moves and evaluate the positions that result. The
    moves are then ranked and the best one is chosen.

    @param color
    @return ChessMove - the best one it can find
    */
   public ChessMove findBestMove(PieceColor color)
   {
      // TODO - refactor this method
      ChessBoard gammyBoard = new ChessBoard(gameBoard);
      PieceColor oppositeColor = color.opposite();

      //Checking for Checkmate, if no legal moves, then game is over
      ArrayList<ChessMove> moveList = gammyBoard.findAllMoves(color);
      int legalMoves = moveList.size();
      if (legalMoves == 0)
      {
         return null;
      }
      rateMoves(gammyBoard, moveList);
      sortMovesD(moveList);
      //find moves that don't leave a piece hanging
      int noHangingMoves = 0;
      for (int i = 0; i < moveList.size(); i++)
      {
         if (moveList.get(i).getHangRating() == 0)
         {
            noHangingMoves++;
         }
      }

      Tree<ChessBoard> moveTree = new Tree<>(gammyBoard);
      int counter1 = Math.min(Math.min(legalMoves, noHangingMoves), 5);
      if (noHangingMoves == 0)
      {
         counter1 = Math.min(legalMoves, 5);
      }

      for (int i = 0; i < counter1; i++)
      {
         moveTree.addChild(gammyBoard.advancePosition(moveList.get(i)));
      }

      //Checking for Checkmate, if no legal moves, then game is over
      int totalMoves = 0;

      int counter2 = 5;
      for (int i = 0; i < counter1; i++)
      {
         totalMoves += generateMoveTree(moveTree.getChildTree(i), oppositeColor);

         Tree<ChessBoard> childTree = moveTree.getChildTree(i);
         if (childTree.numChildren() < 5)
         {
            counter2 = childTree.numChildren();
         }
         for (int j = 0; j < counter2; j++)
         {
            totalMoves += generateMoveTree(childTree.getChildTree(j), color);

            // this seems to be just iterating one level deeper, cut because
            // it consumed too much time
            
//            int counter3 = 5; 
//            Tree grandChild = childTree.getChildTree(j);
//            if(grandChild.numChildren() < 5)
//            {
//               counter3 = grandChild.numChildren();
//            }
//            for (int k = 0; k < counter3; k++)
//            {
//               totalMoves += generateMoveTree(grandChild.getChildTree(k), oppositeColor);
//            }
         }

      }
      if (totalMoves == 0)
      {
         return new ChessMove();
      }

      Queue nodeList = new Queue();
      ArrayList<ChessBoard> finalPositions = new ArrayList<>();
      //get all terminal Nodes
      nodeList.enqueue(moveTree);
      while (!nodeList.isEmpty())
      {
         Tree<ChessBoard> tree = (Tree) nodeList.dequeue();
         if (!tree.isLeaf())
         {
            for (int i = 0; i < tree.numChildren(); i++)
            {
               nodeList.enqueue(tree.getChildTree(i));
            }
         }
         else
         {
            finalPositions.add(tree.info);
         }
      }
      //Have all the positions, now need to sort them and trace back the best
      //move to its root
      sortBoardsD(finalPositions);

      Tree bestMove = moveTree.find(finalPositions.get(0));
      
      Tree ancestor = null;
      for(Tree childOfRoot : moveTree.children)
      {
         if(childOfRoot.find(bestMove) != null)
            ancestor = childOfRoot;
      }
      
      

      // REMOVE ONCE THIS METHOD IS SORTED OUT
      if(ancestor == null)
         return moveList.get(0);
      // REMOVE ONCE THIS METHOD IS SORTED OUT
      
      
      
      
      int index = moveTree.getIndex(ancestor);
      return moveList.get(index);
      //Sorting the positions given by the white moves, picking the worst one
   }

   private int generateMoveTree(Tree<ChessBoard> parentTree, PieceColor color)
   {
      if(parentTree == null || parentTree.info == null)
         return 0;
      
      ArrayList<ChessMove> moveList = parentTree.info.findAllMoves(color);
      int legalMoves = moveList.size();
      if (legalMoves == 0)
      {
         return 0;
      }
      rateMoves(parentTree.info, moveList);
      sortMovesD(moveList);

      int counter1 = 5;
      if (legalMoves < 5)
      {
         counter1 = legalMoves;
      }
      for (int i = 0; i < counter1; i++)
      {
         parentTree.addChild(parentTree.info.advancePosition(moveList.get(i)));
      }

      return legalMoves;
   }

   /**
    This method will return the number of pieces this color has "hanging" or
    insufficiently defended

    @param cb
    @param color
    @return
    */
   private int hangRating(ChessBoard cb, PieceColor color)
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
   @param cb    - board position to analyze
   @return int - number of potential moves that are available
   */
   private int howManyMoves(PieceColor color, ChessBoard cb)
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
            if(cp == null)
               continue;
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
                        if (color == PieceColor.WHITE && j - m == 1
                              && (i - k == 1 || k - i == 1)
                              && cb.getPieceAt(k, m) != null
                              && cb.getPieceAt(k, m).getColor() == PieceColor.BLACK)
                        {
                           numMoves++;
                        }
                        if (color == PieceColor.BLACK && m - j == 1
                              && (i - k == 1 || k - i == 1)
                              && cb.getPieceAt(k, m) != null
                              && cb.getPieceAt(k, m).getColor() == PieceColor.WHITE)
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
    This method finds how many objects are in a list. Assumes a contiguous
    list

    @param list
    @return int - number of elements in this list
    */
   private int length(Object[] list)
   {
      int count = 0;
      if (list == null || list.length == 0)
      {
         return 0;
      }
      while (count < list.length && list[count] != null)
      {
         count++;
      }
      return count;
   }

   /**
   Rates the given board
   
   @param cb
   @param color
   @return 
   */
   private float matRating(ChessBoard cb, PieceColor color)
   {
      float rating, myMaterial, totalMaterial;
      totalMaterial = myMaterial = 0.0f;
      for (int i = 0; i < 8; i++)
      {
         for (int j = 0; j < 8; j++)
         {
            if(cb.getPieceAt(i, j) == null)
               continue;
            totalMaterial += cb.getPieceAt(i, j).value;
            if (cb.getPieceAt(i, j).getColor().equals(color))
            {
               myMaterial += cb.getPieceAt(i, j).value;
            }
         }
      }
      rating = myMaterial / (totalMaterial - myMaterial);
      return rating;
   }

   /**
    Rates the mobility of the given board position

    @param cb - chess board to evaluate
    @param color - player whose pieces' mobility will be evaluated
    @return int - rating of how mobile this player's pieces are
    */
   private int mobRating(ChessBoard cb, PieceColor color)
   {
      return howManyMoves(color, cb);
   }

   /**
    This method takes a list of ChessMoves and the ChessBoard they will be
    made on, assigns each move a mobility, material, and hanging rating

    @param cb
    @param moveList
    */
   private void rateMoves(ChessBoard cb, ArrayList<ChessMove> moveList)
   {
      ChessBoard temp;
      PieceColor color = moveList.get(0).piece.getColor();
      for (int i = 0; i < moveList.size(); i++)
      {
         temp = cb.advancePosition(moveList.get(i));
         moveList.get(i).setMatRating(matRating(temp, color));
         moveList.get(i).setMobRating(mobRating(temp, color));
         moveList.get(i).setHangRating(hangRating(temp, color));
      }
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
    Sorts the given moveList in ascending order

    @param moveList
    */
   private void sortMovesA(ArrayList<ChessMove> moveList)
   {
      Collections.sort(moveList);
   }

   /**
    Sorts the given moveList in descending order

    @param moveList
    */
   private void sortMovesD(ArrayList<ChessMove> moveList)
   {
      Collections.sort(moveList);
      Collections.reverse(moveList);
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

   public static void main(String args[])
   {
      AI deepBlue = new AI();
      deepBlue.hangRating(deepBlue.gameBoard, PieceColor.WHITE);
      ChessPiece mover = deepBlue.gameBoard.getPieceAt(4, 6);
      deepBlue.gameBoard.movePiece(mover, 4, 4);

      for (int i = 0; i < 8; i++)
      {
         mover = deepBlue.gameBoard.getPieceAt(i, 1);
         deepBlue.gameBoard.movePiece(mover, i, 3);
      }
      System.out.println(deepBlue.gameBoard);
      deepBlue.hangRating(deepBlue.gameBoard, PieceColor.BLACK);
   }
}
