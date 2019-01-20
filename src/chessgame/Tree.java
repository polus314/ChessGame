package chessgame;

import java.util.ArrayList;

/**
 * Implements a basic tree structure that takes a type parameter T for the data
 * stored in the Tree. Allows an arbitrary number of children, contrasted with
 * just two for a binary tree.
 *
 * @param <T> type of the information stored in the Tree
 * @author John Polus
 */
public class Tree<T extends Comparable<T>> implements Comparable<Tree<T>>
{

    public ArrayList<Tree<T>> children;
    public T info;

    public Tree(T in)
    {
        children = new ArrayList<>();
        info = in;
    }

    @Override
    public int compareTo(Tree<T> rhs)
    {
        return info.compareTo(rhs.info);
    }

    /**
     * This is my first recursive method ever. Hopefully my computer doesn't
     * blow up. This method finds a tree that has the given ChessBoard from
     * among this and all the children of this
     *
     * @param targetInfo -
     * @return
     */
    public Tree find(T targetInfo)
    {
        if (info.equals(targetInfo))
        {
            return this;
        }
        else if (children.isEmpty())
        {
            return null;
        }
        else
        {
            for (Tree branch : children)
            {
                Tree found = branch.find(targetInfo);
                if (found != null)
                {
                    return found;
                }
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
        for (int i = 0; i < numChildren(); i++)
        {
            if (tree.equals(getChildTree(i)))
            {
                return i;
            }
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
        if (o instanceof Tree)
        {
            Tree tree = (Tree) o;
            return info.equals(tree.info);// && children.equals(tree.children);
        }
        else
        {
            return false;
        }
    }
}
