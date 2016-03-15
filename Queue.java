/**
To change this license header, choose License Headers in Project Properties.
To change this template file, choose Tools | Templates
and open the template in the editor.
*/

package chessgame;

/**
 
@author jppolecat
*/
public class Queue 
{
   private Node<Tree> front;
   private Node<Tree> rear;
   
   public Queue()
   {
      front = rear = null;
   }
   
   public void enqueue(Tree tree)
   {
      if (front == null)
      {
         front = new Node(tree, null, null);
      }
      else if (front.next == null)
      {
         front.next = new Node(tree, front, null);
         rear = front.next;
      }
      else
      {
         rear.next = new Node(tree, rear, null);
         rear = rear.next;
      }
   }
   
   public Tree dequeue()
   {
      if (front == null)
      {
         return null;
      }
      else if (front.next == null)
      {
         Tree tree = front.info;
         front = rear = null;
         return tree;
      }
      else
      {
         Tree tree = front.info;
         front.next.prev = null;
         front = front.next;
         return tree;
      }
   }
   
   public boolean isEmpty()
   {
      return front == null;
   }
}
