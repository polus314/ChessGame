/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chessgui;

/**
 *
 * @author John
 */
public class GameRequest
{
    public GameTask task;
    public Object info;
    public boolean success;
    
    public GameRequest()
    {
        this.task = GameTask.NONE;
        this.info = null;
        this.success = false;
    }
    
    public GameRequest(GameTask task, Object info, boolean success)
    {
        this.task = task;
        this.info = info;
        this.success = success;
    }
}