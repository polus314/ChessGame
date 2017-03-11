/**
To change this license header, choose License Headers in Project Properties.
To change this template file, choose Tools | Templates
and open the template in the editor.
*/

package chessgame;

/**
 
@author jppolecat
*/
public class List
{
   private Node<ChessBoard> front;
   private Node<ChessBoard> rear;
   
   public List()
   {
      front = rear = null;
   }
   
   public void add(ChessBoard cb)
   {
      if (front == null) //empty list
      {
         front = new Node(cb, null, null);
      }
      else if (front.next == null) //list with one node
      {
         front.next = new Node(cb, front, null);
         rear = front.next;
      }
      else //add node to rear
      {
         rear.next = new Node(cb, rear, null);
         rear = rear.next;
      }
   }
   
   public boolean remove(ChessBoard cb)
   {
      Node<ChessBoard> found = (Node<ChessBoard>) search(cb);
      if (found == null) //list is empty or cb isn't in list
      {
         return false;
      }
      else if (found == front && front.next != null)//front node, multiple nodes
      {
         front.next.prev = null;
         front = front.next;
      }
      else if (found == front && front.next == null) //front (and rear) node, only one node
      {
         front = rear = null;
      }
      else if (found == rear) //rear node, multiple nodes
      {
         rear.prev.next = null;
         rear = rear.prev;
      }
      else //middle node
      {
         found.prev.next = found.next;
         found.next.prev = found.prev;
      }
      return true;
   }
   
   public ChessBoard getBoard(int index)
   {
      if(index > length())
         return null;
      if(index == length())
         return rear.info;
      Node<ChessBoard> p = front;
      for (int i = 0; i < index; i++)
      {
         p = p.next;
      }
      return p.info;
      
   }
   
   private Object search(ChessBoard cb)
   {
      if(front == null)
         return null;
      Node<ChessBoard> p = front;
      while(p != null)
      {
         if(p.info.equals(cb))
            return p;
         p = p.next;
      }
      return null;
   }
   
   public int length()
   {
      if(front == null)
         return 0;
      int count = 1;
      Node<ChessBoard> p = front;
      while(p.next != null)
      {
         p = p.next;
         count++;
      }
      return count;
   }
   
   /**
   Holy references Batman!! This method is full of them. This method switches
   the order of two nodes.
   
   @param former
   @param latter 
   */
   private void switchNodes(Node<ChessBoard> former, Node<ChessBoard> latter)
   {
      if(former.next != latter || latter.prev != former)
         return;
      if(former == front)
         front = latter;
      if(latter == rear)
         rear = former;
      former.next = latter.next;
      latter.prev = former.prev;
      if(former.prev != null)
         former.prev.next = latter;
      if(latter.next != null)
         latter.next.prev = former;
      former.prev = latter;
      latter.next = former;
   }
   
   public void sortListA()
   {
      if(length() < 2)
         return;
      Node<ChessBoard> p = front;
      Node<ChessBoard> sorted = null;
      for (int i = 0; i < length() - 1; i++)
      {
         while(p.next != sorted)
         {
            //if nodes are switched, no need to advance pointer, since p.next is
            //now different
            if(p.info.compareTo(p.next.info) > 0)
            {
               switchNodes(p, p.next);
            }
            else
            {
               p = p.next;
            }
         }
         p = front;
         if (i == 0)
            sorted = rear;
         else if(sorted != null)
            sorted = sorted.prev;
         else
            System.out.println("sortListA() error in List class");
      }
   }
   
   public void sortListD()
   {
      if(length() < 2)
         return;
      Node<ChessBoard> p = front;
      Node<ChessBoard> sorted = null;
      if(p.info instanceof ChessBoard)
      {
         for (int i = 0; i < length() - 1; i++)
         {
            while(p.next != sorted)
            {
               //if nodes are switched, no need to advance pointer, since p.next is
               //now different
               if(p.info.compareTo(p.next.info) < 0)
               {
                  switchNodes(p, p.next);
               }
               else
               {
                  p = p.next;
               }
            }
            p = front;
            if (i == 0)
               sorted = rear;
            else if(sorted != null)
               sorted = sorted.prev;
            else
               System.out.println("sortListD() error in List class");
         }
      }
   }
   
}
