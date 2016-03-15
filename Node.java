/**
To change this license header, choose License Headers in Project Properties.
To change this template file, choose Tools | Templates
and open the template in the editor.
*/

package chessgame;

/**
 
@author jppolecat
*/
public class Node<T> 
{
   public Node<T> next;
   public Node<T> prev;
   public Tree tree;
   public T info; //for Queue
   
   public Node(Node<T> p, Node<T> n, Tree parent, ChessBoard cb)
   {
      next = n;
      prev = p;
      tree = new Tree(parent, cb);
   }
   
   public Node(T t, Node<T> p, Node<T> n)
   {
      prev = p;
      next = n;
      info = t;
   }
}
