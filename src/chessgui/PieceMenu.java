package chessgui;

import chessgame.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 This class implements a menu where a certain type of chess piece can be
 selected.

 @author John Polus
 */
public class PieceMenu extends JMenu implements ActionListener
{

   private static final String AC_KING = "king";
   private static final String AC_QUEEN = "queen";
   private static final String AC_ROOK = "rook";
   private static final String AC_BISHOP = "bishop";
   private static final String AC_KNIGHT = "knight";
   private static final String AC_PAWN = "pawn";
   private static final String AC_EMPTY = "empty";

   public JMenuItem item_King, item_Queen, item_Rook, item_Bishop,
         item_Knight, item_Pawn, item_Empty;
   // chosenPiece should never be null
   private ChessPiece chosenPiece;

   public PieceMenu()
   {
      super("Piece Types");
      initComponents();
      chosenPiece = new King();
   }
   
   private void initComponents()
   {
      item_King = new JMenuItem("King");
      item_King.setMnemonic(KeyEvent.VK_K);
      item_King.setActionCommand(AC_KING);

      item_Queen = new JMenuItem("Queen");
      item_Queen.setMnemonic(KeyEvent.VK_Q);
      item_Queen.setActionCommand(AC_QUEEN);

      item_Rook = new JMenuItem("Rook");
      item_Rook.setMnemonic(KeyEvent.VK_R);
      item_Rook.setActionCommand(AC_ROOK);

      item_Bishop = new JMenuItem("Bishop");
      item_Bishop.setMnemonic(KeyEvent.VK_B);
      item_Bishop.setActionCommand(AC_BISHOP);

      item_Knight = new JMenuItem("Knight");
      item_Knight.setMnemonic(KeyEvent.VK_N);
      item_Knight.setActionCommand(AC_KNIGHT);

      item_Pawn = new JMenuItem("Pawn");
      item_Pawn.setMnemonic(KeyEvent.VK_P);
      item_Pawn.setActionCommand(AC_PAWN);

      item_Empty = new JMenuItem("Empty");
      item_Empty.setMnemonic(KeyEvent.VK_E);
      item_Empty.setActionCommand(AC_EMPTY);

      item_King.addActionListener(this);
      item_Queen.addActionListener(this);
      item_Rook.addActionListener(this);
      item_Bishop.addActionListener(this);
      item_Knight.addActionListener(this);
      item_Pawn.addActionListener(this);
      item_Empty.addActionListener(this);
      
      add(item_King);
      add(item_Queen);
      add(item_Rook);
      add(item_Bishop);
      add(item_Knight);
      add(item_Pawn);
      add(item_Empty);
   }

   @Override
   public void actionPerformed(ActionEvent event)
   {
      ChessPiece oldPiece = chosenPiece;
      String command = event.getActionCommand();
      switch (command)
      {
         case AC_KING:
            chosenPiece = new King();
            break;
         case AC_QUEEN:
            chosenPiece = new Queen();
            break;
         case AC_ROOK:
            chosenPiece = new Rook();
            break;
         case AC_BISHOP:
            chosenPiece = new Bishop();
            break;
         case AC_KNIGHT:
            chosenPiece = new Knight();
            break;
         case AC_PAWN:
            chosenPiece = new Pawn();
            break;
         default:
             chosenPiece = new King();
            return;
      }
      this.firePropertyChange("piece", oldPiece, chosenPiece);
   }

   public ChessPiece getPiece()
   {
      return chosenPiece;
   }
   
   public void setPiece(ChessPiece piece)
   {
       if (piece == null)
       {
           return;
       }
       chosenPiece = piece;
   }

   @Override
   public String toString()
   {
      return "Piece Menu";
   }
}
