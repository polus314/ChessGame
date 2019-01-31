/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chessgame;

/**
 *
 * @author John
 */
public class Vector
{

    private final int xDiff;
    private final int yDiff;

    public Vector(int x, int y)
    {
        xDiff = x;
        yDiff = y;
    }

    @Override
    public boolean equals(Object rhs)
    {
        if (rhs instanceof Vector)
        {
            Vector vec = (Vector) rhs;
            return this.xDiff == vec.xDiff && this.yDiff == vec.yDiff;
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return 17 * xDiff + 31 * yDiff;
    }
    
    public int getXDiff()
    {
        return xDiff;
    }
    
    public int getYDiff()
    {
        return yDiff;
    }
}
