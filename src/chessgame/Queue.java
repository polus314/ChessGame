/**
To change this license header, choose License Headers in Project Properties.
To change this template file, choose Tools | Templates
and open the template in the editor.
*/

package chessgame;

import java.util.ArrayList;

/**
 
@author jppolecat
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
      if(list.isEmpty())
         return null;
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
