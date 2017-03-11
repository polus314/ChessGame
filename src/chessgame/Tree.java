package chessgame;

import java.util.ArrayList;

/**

 @author jppolecat
 */
public class Tree<T>
{
   public ArrayList<Tree> children;
   public T info;

   public Tree(T in)
   {
      children = new ArrayList<>();
      info = in;
   }

   public void addChild(T childInfo)
   {
      children.add(new Tree(childInfo));
   }

   /**
   This is my first recursive method ever. Hopefully my computer doesn't blow
   up. This method finds a tree that has the given ChessBoard from among this
   and all the children of this
   
   @param targetInfo - 
   @return 
   */
   public Tree find(T targetInfo)
   {
      if(info.equals(targetInfo))
         return this;
      else if(isLeaf())
      {
         return null;
      }
      else
      {
         for(Tree branch : children)
         {
            Tree found = branch.find(targetInfo);
            if(found != null)
               return found;
         }
      }
      return null;
   }

   public Tree getChildTree(int index)
   {
      return children.get(index);
   }
   
   public int getIndex(Tree tree)
   {      
      for(int i = 0; i < numChildren(); i++)
      {
         if(tree.equals(getChildTree(i)))
            return i;
      }
      return -1;
   }

   public boolean isLeaf()
   {
      return numChildren() == 0;
   }

   public int numChildren()
   {
      return children.size();
   }
   
   @Override
   public boolean equals(Object o)
   {
      if(o instanceof Tree)
      {
         Tree tree = (Tree)o;
         return info.equals(tree.info);// && children.equals(tree.children);
      }
      else
         return false;
   }
}
