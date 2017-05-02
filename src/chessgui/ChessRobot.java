/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chessgui;

import java.awt.Robot;
import java.awt.event.InputEvent;

/**
 *
 * @author uwpne
 */
public class ChessRobot implements Runnable
{
    @Override
    public void run()
    {
        Robot robby = null;
        try 
        {
            robby = new Robot();
        } 
        catch(Exception e) 
        {
            System.out.println("No Robots Allowed!");
            e.printStackTrace();
            return;
        }

        int x = 50;
        int y = 50;
        robby.mouseMove(150, 200);
        

        while(true)
        {
            robby.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robby.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            try { Thread.sleep(10000); } catch(Exception e) {}
            System.out.println("We ain't never scared");
        }
    }
}
