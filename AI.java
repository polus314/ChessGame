package chessgame;

/**
 This is supposed to be the brains behind the program. We shall see... This
 program is for Miguel Colunga-Santoyo.

 @author jppolecat
 */
public class AI
{

   private ChessBoard gameBoard;

   public AI()
   {
      gameBoard = new ChessBoard();
   }

   public AI(ChessBoard cb)
   {
      gameBoard = new ChessBoard(cb);
   }
   
   /**
    This method makes the given move on the given board and returns a copy of
    the resulting board.

    @param cb
    @param cm
    @return
    */
   public ChessBoard advancePosition(ChessBoard cb, ChessMove cm)
   {
      ChessBoard nextNode = new ChessBoard(cb);
      int xi, xf, yi, yf;
      xi = cm.piece.xCoord;
      yi = cm.piece.yCoord;
      xf = cm.getXDest();
      yf = cm.getYDest();
      nextNode.replacePiece(xi, yi, xf, yf);
      if (nextNode.pieceArray[xf][yf] instanceof King // castling concerns
            || cb.pieceArray[xf][yf] instanceof Rook)
      {
         nextNode.pieceArray[xf][yf].hasMoved = true;
      }
      nextNode.mobilityRating = cm.getMobilityRating();
      nextNode.materialRating = cm.getMaterialRating();
      return nextNode;
   }
   
   public ChessPiece[] aimedHere(ChessBoard cb, int xDest, int yDest)
   {
      ChessPiece [] defenders = new ChessPiece [32];
      for (int i = 0; i < 8; i++)
      {
         for (int j = 0; j < 8; j++)
         {
            ChessPiece cp = cb.pieceArray[i][j];
            cp.select();
            if(cp.canMove(xDest, yDest) 
                  && cb.pathIsClear(xDest, yDest)
                  && cp.type != PieceType.PAWN)
               defenders[length(defenders)] = cp;
            else if(cp.type == PieceType.PAWN
                  && cp.color == PieceColor.BLACK
                  && (cp.xCoord - 1 == xDest || cp.xCoord + 1 == xDest)
                  && cp.yCoord + 1 == yDest)
               defenders[length(defenders)] = cp;
            else if(cp.type == PieceType.PAWN
                  && cp.color == PieceColor.WHITE
                  && (cp.xCoord - 1 == xDest || cp.xCoord + 1 == xDest)
                  && cp.yCoord - 1 == yDest)
               defenders[length(defenders)] = cp;
            cp.unselect();
         }
      }
      return defenders;
   }
   
   public PieceColor checkGameOver()
   {
      ChessBoard gammyBoard = new ChessBoard(gameBoard);
      int numWMoves, numBMoves;
      numWMoves = length(findAllMoves(PieceColor.WHITE, gammyBoard));
      numBMoves = length(findAllMoves(PieceColor.BLACK, gammyBoard));
      System.out.println("Number of pieces on the board: " + gammyBoard.countPieces());
      if (numBMoves == 0)
      {
         if(gammyBoard.checkForCheck(PieceColor.BLACK))
         {
            gameBoard.gameOver = true;
            return PieceColor.WHITE;
         }
         else
         {
            return PieceColor.EMPTY;
         }
      }
      else if (numWMoves == 0)
      {
         if(gammyBoard.checkForCheck(PieceColor.WHITE))
         {
            gameBoard.gameOver = true;
            return PieceColor.BLACK;
         }
         else
         {
            return PieceColor.EMPTY;
         }
      }
      else if(gammyBoard.countPieces() == 2)
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
   public void concatenateMoveLists(ChessMove [] mainList, ChessMove [] tempList)
   {
      int start = length(mainList);
      int end = start + length(tempList);
      if(end > start)
      {
         for (int i = start; i < end; i++)
         {
            mainList[i] = tempList[i-start];
         }
      }
   }
   
   /**
    This method copies an array of ChessBoards and returns that copy

    @param moveTree
    @return
    */
   public ChessBoard[] copyBoards(ChessBoard[] moveTree)
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
    This method simply finds the number of moves in the moveList
    THIS METHOD IS OBSOLETE - USE length() INSTEAD

    @param moveList
    @return
    */
   private int countMoves(ChessMove[] moveList)
   {
      int count = 0;
      while (moveList[count] != null)
      {
         count++;
      }
      return count;
   }
   
   /**
    This method finds all possible moves for the given color and updates that
    count in cb

    @param color
    @param cb
    @return list of moves
    */
   public ChessMove[] findAllMoves(PieceColor color, ChessBoard cb)
   {
      int numMoves = 0;
      ChessMove[] moveList = new ChessMove[121]; //121 is the most moves possible?
      for (int i = 0; i < 8; i++)
      {
         for (int j = 0; j < 8; j++)
         {
            cb.pieceArray[i][j].select();
            if (cb.pieceArray[i][j].getColor() == color)
            {
               ChessMove [] tempList = findMoves(cb, cb.pieceArray[i][j]);
               concatenateMoveLists(moveList, tempList);
            }
            cb.pieceArray[i][j].unselect();
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
      ChessBoard gammyBoard = new ChessBoard(gameBoard);
      PieceColor oppositeColor = color.opposite();

      //Checking for Checkmate, if no legal moves, then game is over
      ChessMove [] moveList = findAllMoves(color, gammyBoard);
      System.out.println("Length of moveList1: " + length(moveList));
      int legalMoves = length(moveList);
      if (legalMoves == 0)
      {
         return new ChessMove();
      }
      rateMoves(gammyBoard, moveList);
      sortMovesD(moveList);
      //find moves that don't leave a piece hanging
      int noHangingMoves = 0;
      for(int i = 0; i < length(moveList); i++)
      {
         if(moveList[i].getHangRating() == 0)
            noHangingMoves++;
      }
      
      System.out.println("Hanging Ratings");
      for (int i = 0; i < length(moveList); i++)
         System.out.println(moveList[i].getHangRating());
      
      Tree moveTree = new Tree(null, gammyBoard);
      int counter1 = minimum(legalMoves, noHangingMoves, 5);
      if(noHangingMoves == 0)
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
      while(!nodeList.isEmpty())
      {
         Tree tree = (Tree) nodeList.dequeue();
         if(!tree.isTerminal())
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
      
      System.out.println("Length of Move List: " + finalPositions.length());
      System.out.println("Best Move: " + finalPositions.getBoard(0).materialRating
         + " " + finalPositions.getBoard(0).mobilityRating);
      System.out.println("Worst Move: " + finalPositions.getBoard(finalPositions.length()).materialRating
         + " " + finalPositions.getBoard(finalPositions.length()).mobilityRating);
      
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
   public int findBoard(ChessBoard[] moveTree, ChessBoard cb)
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
   public ChessMove[] findMoves(ChessBoard cb, ChessPiece cp)
   {
      ChessMove [] moveList = new ChessMove[27];
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
            else if (cp.canMove(xf, yf)            //piece moves this way
                  && cb.pathIsClear(xf, yf)        //no pieces in the way
                  && cb.spaceIsEmpty(xf, yf))      //space is empty
            {
               moveList[numMoves] = possMove;
               numMoves++;
            }
            else if (cp.canMove(xf, yf)            
                  && cb.pathIsClear(xf, yf)
                  && cb.spaceIsOpen(xf, yf, cp.getColor())  //piece is opposite color
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
                     && cb.pieceArray[xf][yf].getColor() == PieceColor.BLACK)
               {
                  possMove.setMoveType(MoveType.CAPTURE);
                  moveList[numMoves] = possMove;
                  numMoves++;
               }
               if (color == PieceColor.BLACK && yf - yi == 1
                     && (xi - xf == 1 || xf - xi == 1)
                     && cb.pieceArray[xf][yf].getColor() == PieceColor.WHITE)
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
 
   public int generateMoveTree(Tree parentTree, PieceColor color)
   {
      ChessMove [] moveList = findAllMoves(color, parentTree.info);
      //System.out.println("Length of moveList1: " + length(moveList));
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
   
   public ChessPiece [] getPieces(ChessBoard cb, PieceColor color)
   {
      ChessPiece [] pieces = new ChessPiece[64];
      for (int i = 0; i < 8; i++)
      {
         for (int j = 0; j < 8; j++)
         {
            if(cb.pieceArray[i][j].getColor() == color)
               pieces[length(pieces)] = cb.pieceArray[i][j];
         }
      }
//      if(color == PieceColor.WHITE)
//         System.out.println("White pieces: " + length(pieces));
//      else
//         System.out.println("Black pieces:" + length(pieces));
      return pieces;
   }
   
   /**
   This method will return the number of pieces this color has "hanging" or
   insufficiently defended
   
   @param cb
   @param color
   @return 
   */
   public int hangRating(ChessBoard cb, PieceColor color)
   {
      int valueOfHanging = 0;
      ChessPiece [] goodPieces = getPieces(cb, color);
      ChessPiece [] badPieces = getPieces(cb, color.opposite());
      for(int i = 0; i < length(goodPieces); i++)
      {
         boolean isHanging = false;
         ChessPiece goodCP = goodPieces[i];
         for(int j = 0; j < length(badPieces); j++)
         {
            //case 1: piece is attacked by a lower valued piece
            if(cb.canCapture(badPieces[j], goodCP.xCoord, goodCP.yCoord) 
                  && badPieces[j].value < goodCP.value )
            {
               isHanging = true;
            }
            //case 2: piece's attackers are worth less than the defenders
         }
         ChessPiece [] piecesInAction = aimedHere(cb, goodCP.xCoord, goodCP.yCoord);
         if(!isHanging)
         {
            ChessPiece [] attackers = new ChessPiece[16];
            ChessPiece [] defenders = new ChessPiece[16];
            for(int k = 0; k < length(piecesInAction); k++)
            {
               if (piecesInAction[k].color == color)
                  defenders[length(defenders)] = piecesInAction[k];
               else
                  attackers[length(attackers)] = piecesInAction[k];
            }
            if(length(attackers) == 0)
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
               if(sumValue(defenders, length(attackers) - 1) + goodCP.value 
                  >= sumValue(attackers, length(attackers)))
               isHanging = true;
            }
            else if(length(defenders) > length(attackers))
            {
               isHanging = false;
            }
         }
         if(isHanging)
            valueOfHanging += goodPieces[i].value;
      }
      return valueOfHanging;
   }
   
   public int howManyMoves(PieceColor color, ChessBoard cb)
   {
      int numMoves = 0;
      for (int i = 0; i < 8; i++)
      {
         for (int j = 0; j < 8; j++)
         {
            cb.pieceArray[i][j].select();
            if (cb.pieceArray[i][j].getColor() == color)
            {
               for (int k = 0; k < 8; k++)
               {
                  for (int m = 0; m < 8; m++)
                  {
                     if (cb.pieceArray[i][j].canMove(k, m)
                           && cb.pathIsClear(k, m)
                           && cb.spaceIsEmpty(k, m))
                     {
                        numMoves++;
                     }
                     else if (cb.pieceArray[i][j].canMove(k, m)
                           && cb.pathIsClear(k, m)
                           && cb.spaceIsOpen(k, m, cb.pieceArray[i][j].getColor())
                           && !(cb.pieceArray[i][j] instanceof Pawn))
                     {
                        numMoves++;
                     }
                     else if (cb.pieceArray[i][j] instanceof Pawn)
                     {
                        if (color == PieceColor.WHITE && j - m == 1
                              && (i - k == 1 || k - i == 1)
                              && cb.pieceArray[k][m].getColor() == PieceColor.BLACK)
                        {
                           numMoves++;
                        }
                        if (color == PieceColor.BLACK && m - j == 1
                              && (i - k == 1 || k - i == 1)
                              && cb.pieceArray[k][m].getColor() == PieceColor.WHITE)
                        {
                           numMoves++;
                        }
                     }
                  }
               }
            }
            cb.pieceArray[i][j].unselect();
         }
      }
      return numMoves;
   }
   
   /**
    This method determines whether a move will result in the moving player
    being in check.

    @param cb
    @param cm
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
    @return
    */
   public int length(Object[] list)
   {
      int count = 0;
      if (list.length == 0)
      {
         return 0;
      }
      while (count < list.length && list[count] != null)
      {
         count++;
      }
      return count;
   }
   
   public float matRating(ChessBoard cb, PieceColor color)
   {
      float rating, myMaterial, totalMaterial;
      totalMaterial = myMaterial = 0.0f;
      for (int i = 0; i < 8; i++)
      {
         for (int j = 0; j < 8; j++)
         {
            totalMaterial += cb.pieceArray[i][j].value;
            if (cb.pieceArray[i][j].getColor().equals(color))
            {
               myMaterial += cb.pieceArray[i][j].value;
            }
         }
      }
      rating = myMaterial / (totalMaterial - myMaterial);
      return rating;
   }
   
   public int minimum(int a, int b, int c)
   {
      if(a < b && a < c)
         return a;
      if(b < c)
         return b;
      return c;
   }
   
   public int mobRating(ChessBoard cb, PieceColor color)
   {
      return howManyMoves(color, cb);
   }
   
   public void printList(PieceColor color)
   {
      if (color == PieceColor.WHITE)
      {
         for (int i = 0; i < 121; i++)
         {
            System.out.println(gameBoard.wMoveList[i].toString() + "\n");
         }
      }
      else if (color == PieceColor.BLACK)
      {
         for (int i = 0; i < 121; i++)
         {
            System.out.println(gameBoard.bMoveList[i].toString() + "\n");
         }
      }
   }
   
   /**
   This method takes a list of ChessMoves and the ChessBoard they will be made
   on, assigns each move a mobility, material, and hanging rating
   @param cb
   @param moveList 
   */
   public void rateMoves(ChessBoard cb, ChessMove [] moveList)
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
    This method removes any moves from moveList that will lead to the moving
    player being in check. THIS METHOD IS OBSOLETE BECAUSE OF leadsToCheck()

    @param color - same color as moveList
    @param cb
    @param moveList
    @return
    */
   private int removeBeingChecked(ChessBoard cb, ChessMove[] moveList)
   {
      int numRemoved = 0;
      int numMoves = countMoves(moveList);
      int removeIndices[] = new int[numMoves];
      PieceColor color = moveList[0].piece.getColor();
      for (int i = 0; i < numMoves; i++)
      {
         ChessBoard nextPosition = advancePosition(cb, moveList[i]);
         if (nextPosition.checkForCheck(color))
         {
            removeIndices[numRemoved] = i;
            numRemoved++;
         }
      }

      ChessMove[] checkless = new ChessMove[numMoves - numRemoved];
      for (int i = 0; i < numRemoved; i++)
      {
         moveList[removeIndices[i]] = null;
      }

      int numAdded = 0;
      for (int i = 0; i < numMoves; i++)
      {
         if (moveList[i] != null)
         {
            checkless[numAdded] = moveList[i];
            numAdded++;
         }
      }
      moveList = checkless;

      if (numMoves - numRemoved != numAdded)
      {
         System.out.println("Check the removeBeingChecked() method");
      }
      for (int i = 0; i < numAdded; i++)
      {
         System.out.println(i + ": " + moveList[i]);
      }
      return numAdded;
   }
   
   public void sortBoardsA(ChessBoard[] list)
   {
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
   
   public void sortBoardsD(ChessBoard[] list)
   {
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
   
   public void sortMovesA(ChessMove[] moveList)
   {
      int numMoves = countMoves(moveList);
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
    This method sorts the move list and puts the best ones at the lowest
    indices.

    @param moveList
    */
   public void sortMovesD(ChessMove[] moveList)
   {
      int numMoves = countMoves(moveList);
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
   This method finds the sum of the values of the "num"-smallest pieces in the
   array, simply summing the entire array if num > length(pieces)
   
   @param pieces
   @param num
   @return 
   */
   public int sumValue(ChessPiece [] pieces, int num)
   {
      int sum = 0;
      if(num > length(pieces))
      {
         num = length(pieces);
      }
      for (int i = 0; i < length(pieces) - 1; i++)
      {
         for (int j = length(pieces) - 1; j > i; j--)
         {
            if(pieces[j].value < pieces[j-1].value)
            {
               ChessPiece temp = pieces[j-1];
               pieces[j-1] = pieces[j];
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
      deepBlue.gameBoard.pieceArray[4][6].select();
      deepBlue.gameBoard.movePiece(4, 4);
      
      for(int i = 0; i < 8; i++)
      {
         deepBlue.gameBoard.pieceArray[i][1].select();
         deepBlue.gameBoard.movePiece(i, 3);
      }
      System.out.println(deepBlue.gameBoard);
      deepBlue.hangRating(deepBlue.gameBoard, PieceColor.BLACK);
   }
}
