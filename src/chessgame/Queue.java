package chessgame;

import java.util.ArrayList;

/**
 Implements a basic Queue that accepts a type parameter T. Exposes the enqueue
 and dequeue methods to allow adding and removing elements.

 @author John Polus
 */
public class Queue<T>
{

   private final ArrayList<T> list;

   public Queue()
   {
      list = new ArrayList<>();
   }

   public void enqueue(T element)
   {
      list.add(element);
   }

   public T dequeue()
   {
      if (list.isEmpty())
      {
         return null;
      }
      return list.remove(0);
   }

   public boolean isEmpty()
   {
      return list.isEmpty();
   }

   public int size()
   {
      return list.size();
   }
}
