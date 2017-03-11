package chessgame;

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

   /**
    Default constructor, initializes the chessboard to the start of a new game
    */
   public AI()
   {
      gameBoard = new ChessBoard();
   }

   /**
    Parameterized constructor, sets the chessboard to cb

    @param cb - board that AI should use for calculations
    */
   public AI(ChessBoard cb)
   {
      gameBoard = new ChessBoard(cb);
   }

   /**
    This method makes the given move on the given board and returns a copy of
    the resulting board.

    @param cb - chess board on which to execute the move
    @param cm - move to execute
    @return ChessBoard - resulting board position after the move
    */
   private ChessBoard advancePosition(ChessBoard cb, ChessMove cm)
   {
      // TODO - all of this should be a method in ChessBoard

      ChessBoard nextNode = new ChessBoard(cb);
      int xi, xf, yi, yf;
      xi = cm.piece.xCoord;
      yi = cm.piece.yCoord;
      xf = cm.getXDest();
      yf = cm.getYDest();
      nextNode.replacePiece(xi, yi, xf, yf);
      if (nextNode.getPieceAt(xf, yf) instanceof King // castling concerns
            || cb.getPieceAt(xf, yf) instanceof Rook)
      {
         nextNode.getPieceAt(xf, yf).hasMoved = true;
      }
      nextNode.mobilityRating = cm.getMobilityRating();
      nextNode.materialRating = cm.getMaterialRating();
      return nextNode;
   }

   /**
    Finds all chess pieces that can attack/defend the square at (xDest, yDest)

    @param cb - board to check
    @param xDest - x position of target square
    @param yDest - y position of target square
    @return array of all chess pieces that can attack this square
    */
   private ChessPiece[] aimedHere(ChessBoard cb, int xDest, int yDest)
   {
      ChessPiece[] defenders = new ChessPiece[32];
      for (int i = 0; i < 8; i++)
      {
         for (int j = 0; j < 8; j++)
         {
            boolean isADefender = false;
            ChessPiece cp = cb.getPieceAt(i, j);

            // if regular non-empty piece, check if it can move here and the
            // path is clear
            if (!cp.equals(new ChessPiece()) && cp.type != PieceType.PAWN)
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
               defenders[length(defenders)] = cp;
            }
         }
      }
      return defenders;
   }

   /**
    Checks to see if either side has been checkmated or if stalemate has
    occurred.

    @return PieceColor - WHITE if White has won BLACK if Black has won EMPTY
    if stalemate has occurred null if game isn't over TODO - rename and/or
    write a second method
    */
   public PieceColor checkGameOver()
   {
      // don't want to change the instance variable gameBoard, so make a copy
      ChessBoard gammyBoard = new ChessBoard(gameBoard);
      int numWMoves = length(findAllMoves(PieceColor.WHITE, gammyBoard));
      int numBMoves = length(findAllMoves(PieceColor.BLACK, gammyBoard));

      // TODO - should take whose move it is into account?
      if (numBMoves == 0) // if Black has no moves
      {
         if (gammyBoard.checkForCheck(PieceColor.BLACK)) // Black is checkmated
         {
            gameBoard.gameOver = true;
            return PieceColor.WHITE;
         }
         else
         {
            return PieceColor.EMPTY;   // Black isn't in check, but can't move
         }                             // so it is stalemate
      }
      else if (numWMoves == 0)
      {
         if (gammyBoard.checkForCheck(PieceColor.WHITE)) // White is checkmated
         {
            gameBoard.gameOver = true;
            return PieceColor.BLACK;
         }
         else
         {
            return PieceColor.EMPTY;   // White isn't in check, but can't move
         }                             // so it is stalemate
      }
      // impossible to checkmate with this many pieces
      else if (gammyBoard.countPieces() == 2)
      {
         gameBoard.gameOver = true;
         return PieceColor.EMPTY;
      }
      gameBoard.gameOver = false;
      return null;
   }

   /**
    This method adds tempList to the end of mainList, assumes that mainList
    has enough space

    @param mainList
    @param tempList
    */
   private void concatenateMoveLists(ChessMove[] mainList, ChessMove[] tempList)
   {
      int start = length(mainList);
      int end = start + length(tempList);
      if (end > start)
      {
         for (int i = start; i < end; i++)
         {
            mainList[i] = tempList[i - start];
         }
      }
   }

   /**
    This method copies an array of ChessBoards and returns that copy

    @param moveTree
    @return
    */
   private ChessBoard[] copyBoards(ChessBoard[] moveTree)
   {
      ChessBoard[] copyTree = new ChessBoard[length(moveTree)];
      for (int i = 0; i < length(moveTree); i++)
      {
         copyTree[i] = new ChessBoard();
         copyTree[i].copy(moveTree[i]);
      }
      return copyTree;
   }

   /**
    This method finds all possible moves for the given color and updates that
    count in cb

    @param color
    @param cb
    @return list of moves
    */
   private ChessMove[] findAllMoves(PieceColor color, ChessBoard cb)
   {
      int numMoves = 0;
      ChessMove[] moveList = new ChessMove[121]; //121 is the most moves possible?
      for (int i = 0; i < 8; i++)
      {
         for (int j = 0; j < 8; j++)
         {
            if (cb.getPieceAt(i, j).getColor() == color)
            {
               ChessMove[] tempList = findMoves(cb, cb.getPieceAt(i, j));
               concatenateMoveLists(moveList, tempList);
            }
         }
      }
      return moveList;
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
      ChessMove[] moveList = findAllMoves(color, gammyBoard);
      int legalMoves = length(moveList);
      if (legalMoves == 0)
      {
         return new ChessMove();
      }
      rateMoves(gammyBoard, moveList);
      sortMovesD(moveList);
      //find moves that don't leave a piece hanging
      int noHangingMoves = 0;
      for (int i = 0; i < length(moveList); i++)
      {
         if (moveList[i].getHangRating() == 0)
         {
            noHangingMoves++;
         }
      }

      Tree moveTree = new Tree(null, gammyBoard);
      int counter1 = minimum(legalMoves, noHangingMoves, 5);
      if (noHangingMoves == 0)
      {
         counter1 = minimum(legalMoves, 5, 5);
      }
//      if(noHangingMoves < 6)
//      {
//         counter1 = noHangingMoves;
//      }
//      if (legalMoves < 5)
//      {
//         counter1 = legalMoves;
//      }

      for (int i = 0; i < counter1; i++)
      {
         moveTree.addChild(advancePosition(gammyBoard, moveList[i]));
      }

      //Checking for Checkmate, if no legal moves, then game is over
      int totalMoves = 0;

      int counter2 = 5;
      for (int i = 0; i < counter1; i++)
      {
         totalMoves += generateMoveTree(moveTree.getChildTree(i), oppositeColor);

         Tree childTree = moveTree.getChildTree(i);
         if (childTree.numChildren() < 5)
         {
            counter2 = childTree.numChildren();
         }
         for (int j = 0; j < counter2; j++)
         {
            totalMoves += generateMoveTree(childTree.getChildTree(j), color);

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
      List finalPositions = new List();
      //get all terminal Nodes
      nodeList.enqueue(moveTree);
      while (!nodeList.isEmpty())
      {
         Tree tree = (Tree) nodeList.dequeue();
         if (!tree.isTerminal())
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
      finalPositions.sortListD();

      Tree bestMove = moveTree.find(finalPositions.getBoard(0));
      Tree ancestor = bestMove.getRootChild();
      int index = moveTree.getIndex(ancestor);
      return moveList[index];
      //Sorting the positions given by the white moves, picking the worst one
   }

   /**
    This method takes an array of ChessBoards and finds the one that is equal
    to cb, NOTE: Currently, moveTree is always full

    @param moveTree
    @param cb
    @return
    */
   private int findBoard(ChessBoard[] moveTree, ChessBoard cb)
   {
      for (int i = 0; i < moveTree.length; i++)
      {
         if (moveTree[i].equals(cb))
         {
            return i;
         }
      }
      return -1;
   }

   /**
    This method finds all legal moves for this piece on this board

    @param cb
    @param cp
    @return
    */
   private ChessMove[] findMoves(ChessBoard cb, ChessPiece cp)
   {
      ChessMove[] moveList = new ChessMove[27];
      int numMoves = 0;
      PieceColor color = cp.getColor();
      int xi = cp.xCoord;
      int yi = cp.yCoord;
      for (int xf = 0; xf < 8; xf++)
      {
         for (int yf = 0; yf < 8; yf++)
         {
            ChessMove possMove = new ChessMove(cp, xf, yf);
            //if move puts mover in check, disregard it
            if (leadsToCheck(cb, possMove))
            {
               ;
            }
            else if (cp.canMove(xf, yf) //piece moves this way
                  && cb.pathIsClear(cp, xf, yf) //no pieces in the way
                  && cb.spaceIsEmpty(xf, yf))      //space is empty
            {
               moveList[numMoves] = possMove;
               numMoves++;
            }
            else if (cp.canMove(xf, yf)
                  && cb.pathIsClear(cp, xf, yf)
                  && cb.spaceIsOpen(xf, yf, cp.getColor()) //piece is opposite color
                  && !(cp instanceof Pawn))                 //pawns capture differently
            {                                               //than they move
               possMove.setMoveType(MoveType.CAPTURE);
               moveList[numMoves] = possMove;
               numMoves++;
            }
            else if (cp instanceof Pawn)
            {
               //this is how pawns capture
               if (color == PieceColor.WHITE && yi - yf == 1
                     && (xi - xf == 1 || xf - xi == 1)
                     && cb.getPieceAt(xf, yf).getColor() == PieceColor.BLACK)
               {
                  possMove.setMoveType(MoveType.CAPTURE);
                  moveList[numMoves] = possMove;
                  numMoves++;
               }
               if (color == PieceColor.BLACK && yf - yi == 1
                     && (xi - xf == 1 || xf - xi == 1)
                     && cb.getPieceAt(xf, yf).getColor() == PieceColor.WHITE)
               {
                  possMove.setMoveType(MoveType.CAPTURE);
                  moveList[numMoves] = possMove;
                  numMoves++;
               }
            }
         }
      }
      return moveList;
   }

   private int generateMoveTree(Tree parentTree, PieceColor color)
   {
      ChessMove[] moveList = findAllMoves(color, parentTree.info);
      int legalMoves = length(moveList);
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
         parentTree.addChild(advancePosition(parentTree.info, moveList[i]));
      }

      return legalMoves;
   }

   private ChessPiece[] getPieces(ChessBoard cb, PieceColor color)
   {
      ChessPiece[] pieces = new ChessPiece[64];
      for (int i = 0; i < 8; i++)
      {
         for (int j = 0; j < 8; j++)
         {
            if (cb.getPieceAt(i, j).getColor() == color)
            {
               pieces[length(pieces)] = cb.getPieceAt(i, j);
            }
         }
      }
      return pieces;
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
      ChessPiece[] goodPieces = getPieces(cb, color);
      ChessPiece[] badPieces = getPieces(cb, color.opposite());
      for (int i = 0; i < length(goodPieces); i++)
      {
         boolean isHanging = false;
         ChessPiece goodCP = goodPieces[i];
         for (int j = 0; j < length(badPieces); j++)
         {
            //case 1: piece is attacked by a lower valued piece
            if (cb.canCapture(badPieces[j], goodCP.xCoord, goodCP.yCoord)
                  && badPieces[j].value < goodCP.value)
            {
               isHanging = true;
            }
            //case 2: piece's attackers are worth less than the defenders
         }
         ChessPiece[] piecesInAction = aimedHere(cb, goodCP.xCoord, goodCP.yCoord);
         if (!isHanging)
         {
            ChessPiece[] attackers = new ChessPiece[16];
            ChessPiece[] defenders = new ChessPiece[16];
            for (int k = 0; k < length(piecesInAction); k++)
            {
               if (piecesInAction[k].color == color)
               {
                  defenders[length(defenders)] = piecesInAction[k];
               }
               else
               {
                  attackers[length(attackers)] = piecesInAction[k];
               }
            }
            if (length(attackers) == 0)
            {
               isHanging = false;
            }
            else if (length(defenders) < length(attackers))
            {
               isHanging = true;
            }
            //this is rather confusing, but I think it works
            else if (length(defenders) == length(attackers))
            {
               if (sumValue(defenders, length(attackers) - 1) + goodCP.value
                     >= sumValue(attackers, length(attackers)))
               {
                  isHanging = true;
               }
            }
            else if (length(defenders) > length(attackers))
            {
               isHanging = false;
            }
         }
         if (isHanging)
         {
            valueOfHanging += goodPieces[i].value;
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
      int numMoves = 0;
      for (int i = 0; i < 8; i++)
      {
         for (int j = 0; j < 8; j++)
         {
            ChessPiece cp = cb.getPieceAt(i, j);
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
                              && cb.getPieceAt(k, m).getColor() == PieceColor.BLACK)
                        {
                           numMoves++;
                        }
                        if (color == PieceColor.BLACK && m - j == 1
                              && (i - k == 1 || k - i == 1)
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
    This method determines whether a move will result in the moving player
    being in check.

    @param cb - chess board to execute move on
    @param cm - move to execute
    @return   - whether or not check is a result of this move
    */
   public boolean leadsToCheck(ChessBoard cb, ChessMove cm)
   {
      ChessBoard temp = advancePosition(cb, cm);
      return temp.checkForCheck(cm.piece.getColor());
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
    Returns the smallest of a, b, c

    @param a - first integer to compare
    @param b - second integer to compare
    @param c - third integer to compare
    @return int - value of parameter that is less than (or tied for least
    with) the other two
    */
   private int minimum(int a, int b, int c)
   {
      if (a < b && a < c)
      {
         return a;
      }
      if (b < c)
      {
         return b;
      }
      return c;
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
   private void rateMoves(ChessBoard cb, ChessMove[] moveList)
   {
      ChessBoard temp;
      PieceColor color = moveList[0].piece.getColor();
      for (int i = 0; i < length(moveList); i++)
      {
         temp = advancePosition(cb, moveList[i]);
         moveList[i].setMatRating(matRating(temp, color));
         moveList[i].setMobRating(mobRating(temp, color));
         moveList[i].setHangRating(hangRating(temp, color));
      }
   }

   /**
    Sorts the given list of boards in ascending order

    @param list - the list of boards to be sorted
    */
   private void sortBoardsA(ChessBoard[] list)
   {
      // TODO - make this more efficient than freakin' BUBBLE SORT
      for (int i = 0; i < length(list) - 1; i++)
      {
         for (int j = length(list) - 1; j > i; j--)
         {
            if (list[j].compareTo(list[j - 1]) < 0)
            {
               ChessBoard temp = list[j - 1];
               list[j - 1] = list[j];
               list[j] = temp;
            }
         }
      }
   }

   /**
    Sorts the given list of boards in descending order

    @param list - the list of boards to be sorted
    */
   private void sortBoardsD(ChessBoard[] list)
   {
      // TODO - make this more efficient than freakin' BUBBLE SORT
      for (int i = 0; i < length(list) - 1; i++)
      {
         for (int j = length(list) - 1; j > i; j--)
         {
            if (list[j].compareTo(list[j - 1]) > 0)
            {
               ChessBoard temp = list[j - 1];
               list[j - 1] = list[j];
               list[j] = temp;
            }
         }
      }
   }

   /**
    Sorts the given moveList in ascending order

    @param moveList
    */
   private void sortMovesA(ChessMove[] moveList)
   {
      // TODO - make this more efficient than freakin' BUBBLE SORT
      int numMoves = length(moveList);
      for (int i = 0; i < numMoves - 1; i++)
      {
         for (int j = numMoves - 1; j > i; j--)
         {
            if (moveList[j].compareTo(moveList[j - 1]) < 0)
            {
               ChessMove temp = moveList[j - 1];
               moveList[j - 1] = moveList[j];
               moveList[j] = temp;
            }
         }
      }
   }

   /**
    Sorts the given moveList in descending order

    @param moveList
    */
   private void sortMovesD(ChessMove[] moveList)
   {
      // TODO - make this more efficient than freakin' BUBBLE SORT
      int numMoves = length(moveList);
      for (int i = 0; i < numMoves - 1; i++)
      {
         for (int j = numMoves - 1; j > i; j--)
         {
            if (moveList[j].compareTo(moveList[j - 1]) > 0)
            {
               ChessMove temp = moveList[j - 1];
               moveList[j - 1] = moveList[j];
               moveList[j] = temp;
            }
         }
      }
   }

   /**
    This method finds the sum of the values of the "num"-smallest pieces in
    the array, simply summing the entire array if num > length(pieces)

    @param pieces
    @param num
    @return
    */
   private int sumValue(ChessPiece[] pieces, int num)
   {
      int sum = 0;
      if (num > length(pieces))
      {
         num = length(pieces);
      }
      // TODO - G-Dang it, can I stop using BUBBLE SORT??
      for (int i = 0; i < length(pieces) - 1; i++)
      {
         for (int j = length(pieces) - 1; j > i; j--)
         {
            if (pieces[j].value < pieces[j - 1].value)
            {
               ChessPiece temp = pieces[j - 1];
               pieces[j - 1] = pieces[j];
               pieces[j] = temp;
            }
         }
      }
      for (int i = 0; i < num; i++)
      {
         sum += pieces[i].value;
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
