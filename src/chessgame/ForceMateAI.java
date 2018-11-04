package chessgame;

import java.util.ArrayList;

/**
 * This AI is used for determining if it is possible and how to force the
 * opposing player into checkmate.
 * @author John
 */
public class ForceMateAI extends AI {
    /**
   Generates the game tree to the given number of moves and determines whether
   checkmate can be forced by the given player.
   
   @param player side trying to force checkmate
   @param moves depth to search through
   @return list containing the moves used to force mate, null if mate isn't
   forced
   */
   public ArrayList<ChessMove> solveForMate(ChessPiece.Color player, int moves, 
         boolean quickly)
   {
      Tree<GameState> tree = generateGameTree(2 * moves, quickly);
      pathToMate = new ArrayList<>();
      if(mateIsFound(tree))
      {
         tracePathToMate(player, tree);
      }
      return pathToMate;
   }
   
   /**
   Assembles a list of moves that lead to the opponent being checkmated.
   
   @param player player who has won after these moves are played
   @param tree positions that are being searched through
   @return true if mate has been found, false otherwise
   */
   private boolean tracePathToMate(ChessPiece.Color player, Tree<GameState> tree)
   {
      // if position is checkmate, search is unnecessary
      if(tree.info.board.checkForMate(player.opposite()))
      {
         return true;
      }
      
      for(Tree<GameState> child : tree.children)
      {
         if(child.info.board.checkForMate(player.opposite()))
         {
            pathToMate.add(child.info.move);
            return true;
         }
      }
      
      for(Tree<GameState> child : tree.children)
      {
         for(Tree<GameState> grandChild : child.children)
         {
            if(tracePathToMate(player, grandChild))
            {
               pathToMate.add(grandChild.info.move);
               return true;
            }
         }
      }
      return false; 
   }
   
   private int bfsFindMate(Tree<GameState> gameTree)
   {
      // search breadth first
      // return number of moves to mate
      ArrayList<Tree<GameState>> states = new ArrayList<>(), 
                                 nextLevel = new ArrayList<>();
      states.add(gameTree);
      int count = 0;
      boolean mateFound = false;
      System.out.println("In BFS Find Mate");
      while(!states.isEmpty() && !mateFound)
      {
         Tree<GameState> current = states.get(0);
         states.remove(0);
         if(current.info.board.checkForMate(ChessPiece.Color.BLACK))
         {
            mateFound = true;
            return count;
         }
         nextLevel.addAll(current.children);
         if(states.isEmpty())
         {
            states = nextLevel;
            nextLevel = new ArrayList<>();
            count++;
            System.out.println("Count: " + count);
         }
      }
      return -1;
   }
   
      
   private boolean mateIsFound(Tree<GameState> tree)
   {
      int numMoves;
      switch(algorithm)
      {
         case BFS: numMoves = bfsFindMate(tree); break;
         default: numMoves = -1;
      }
      return numMoves != -1;
   }
   
   /**
   Determines whether for the given game tree, the player passed in has a
   move that wins the game regardless of what the opponent plays. The opponent
   is assumed to have just moved and gameTree branches from there.
   
   @param player side who might be guaranteed to win
   @param gameTree positions (moves) that are being considered
   @return true if player can always checkmate their opponent, false otherwise
   */
   private boolean forcesMate(ChessPiece.Color player, Tree<GameState> gameTree)
   {
      // if position is checkmate, search is unnecessary
      if(gameTree.info.board.checkForMate(player.opposite()))
         return true;
      
      // if no more children, mate is forced by opponent on this side or depth
      // has been reached
      if(gameTree.children.isEmpty())
         return false;
      
      for(Tree<GameState> child : gameTree.children)
      {
         if(child.info.board.checkForMate(player.opposite()))
         {
            return true;
         }
      }
      
      // check each child: if for every move the opponent takes there is a move
      // for this side that forces checkmate, mate is forced as well.
      for(Tree<GameState> child : gameTree.children)
      {
         boolean leadsToMate = false;
         for(Tree<GameState> grandChild : child.children)
         {
            if(forcesMate(player, grandChild))
               leadsToMate = true;
         }
         // opponent has move that prevents them being checkmated
         if(!leadsToMate)
            return false;
      }
      return true;
   }
}
