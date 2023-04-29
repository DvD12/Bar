package com.justsomeapp.bar.core;

public class Vector2D implements Cloneable
{
    public float x;
    public float y;
    public Vector2D() { x = 0; y = 0; }
    public Vector2D(float newX, float newY)
    {
        x = newX;
        y = newY;
    }
    public Vector2D copy()
    {
        Vector2D x;
        x = this;
        try
        {
            x = (Vector2D) clone();
        }
        catch (CloneNotSupportedException e)
        {}
        return x;
    }
    public void addVector(Vector2D v2)
    {
        x += v2.x; y += v2.y;
    }
    public void subVector(Vector2D v2)
    {
        x -= v2.x; y -= v2.y;
    }
    public void rotateClockwise(float ang)
    {
        float t;
        float cosa = (float)Math.cos((double)ang), sina = (float)Math.sin((double)ang);
        t = x;
        x = t*cosa + y*sina;
        y = -t*sina + y*cosa;
    }
}
