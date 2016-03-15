package chessgame;

/**

 @author jppolecat
 */
public class Tree
{

   public Tree parent;
   public Node firstChild;
   public Node lastChild;
   public ChessBoard info;

   public Tree()
   {
      info = new ChessBoard();
   }

   public Tree(Tree p, ChessBoard in)
   {
      parent = p;
      info = in;
   }

   public void addChild(ChessBoard board)
   {
      if (firstChild == null)
      {
         firstChild = lastChild = new Node(null, null, this, board);
         return;
      }
      if (firstChild.next == null)
      {
         firstChild.next = lastChild = new Node(firstChild, null, this, board);
         return;
      }
      lastChild.next = new Node(lastChild, null, this, board);
      lastChild = lastChild.next;
   }

   /**
   This is my first recursive method ever. Hopefully my computer doesn't blow
   up. This method finds a tree that has the given ChessBoard from among this
   and all the children of this
   
   @param cb
   @return 
   */
   public Tree find(ChessBoard cb)
   {
      if (info.equals(cb))
      {
         return this;
      }
      else
      {
         Tree found = null;
         if (!isTerminal())
         {
            for (int i = 0; i < numChildren() && found == null; i++)
            {
               found = getChildTree(i).find(cb);
            }
         }
         return found;
      }
   }

   public ChessBoard getChildBoard(int index)
   {
      if (index > numChildren())
      {
         return new ChessBoard(PieceType.EMPTY);
      }
      if (index == 0)
      {
         return firstChild.tree.info;
      }
      if (index == numChildren())
      {
         return lastChild.tree.info;
      }
      Node p = firstChild;
      for (int i = 0; i < index; i++)
      {
         p = p.next;
      }
      return p.tree.info;
   }

   public Tree getChildTree(int index)
   {
      if (index > numChildren())
      {
         return null;
      }
      if (index == 0)
      {
         return firstChild.tree;
      }
      if (index == numChildren())
      {
         return lastChild.tree;
      }
      Node p = firstChild;
      for (int i = 0; i < index; i++)
      {
         p = p.next;
      }
      return p.tree;
   }
   
   public int getIndex(Tree tree)
   {
      for(int i = 0; i < numChildren(); i++)
      {
         if(tree == getChildTree(i))
            return i;
      }
      return -1;
   }
   
   /**
   This is my second recursive method ever. This method finds the root of this
   tree structure
   
   @return Tree - root of entire structure
   */
   public Tree getRoot()
   {
      if(isRoot())
      {
         return this;
      }
      else
      {
         return parent.getRoot();
      }
   }
   
   /**
   Well, I'm now in the business of recursion it seems. This should be useful
   for my AI class, finds the second level Tree that this Tree has as an 
   ancestor
   
   @return Tree - one level below root Tree
   */
   public Tree getRootChild()
   {
      if(isRoot())
      {
         return null;
      }
      else if(parent.isRoot())
      {
         return this;
      }
      else
      {
         return parent.getRootChild();
      }
   }

   public boolean isRoot()
   {
      return parent == null;
   }

   public boolean isTerminal()
   {
      return numChildren() == 0;
   }

   public int numChildren()
   {
      if (firstChild == null)
      {
         return 0;
      }
      int count = 1;
      Node p = firstChild;
      while (p.next != null)
      {
         p = p.next;
         count++;
      }
      return count;
   }
}
