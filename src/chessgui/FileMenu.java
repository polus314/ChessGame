/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chessgui;

import chessgame.GameMode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 *
 * @author John
 */
public class FileMenu extends JMenu implements ActionListener
{
    private JMenuItem load, save, solve;
    public FileMenu()
    {
      super(" File ");
      initComponents();
    }
    
    private void initComponents()
    {
      load = new JMenuItem("Load Position");
      load.setActionCommand("Load");
      load.addActionListener(this);
      add(load);
      
      save = new JMenuItem("Save Position");
      save.setActionCommand("Save");
      save.addActionListener(this);
      add(save);
      
      solve = new JMenuItem("Solve for Mate");
      solve.setActionCommand("Solve");
      solve.addActionListener(this);
      add(solve);
    }

    public void enableAll()
    {
        setEnabled(true);
        load.setEnabled(true);
        save.setEnabled(true);
        solve.setEnabled(true);
    }
    
    public void disable(GameMode mode)
    {
        switch(mode)
        {
            case SINGLE:
                load.setEnabled(false);
                solve.setEnabled(false);
                break;
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) 
    {
        firePropertyChange(e.getActionCommand(), null, null);
    }
}
