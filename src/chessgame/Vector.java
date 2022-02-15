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

    public final int dx;
    public final int dy;

    public Vector(int x, int y)
    {
        dx = x;
        dy = y;
    }

    @Override
    public boolean equals(Object rhs)
    {
        if (rhs instanceof Vector)
        {
            Vector vec = (Vector) rhs;
            return this.dx == vec.dx && this.dy == vec.dy;
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return 17 * dx + 31 * dy;
    }
}
