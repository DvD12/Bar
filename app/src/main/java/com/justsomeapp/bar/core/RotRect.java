package com.justsomeapp.bar.core;


import android.graphics.Point;

public class RotRect
{
    public float ang = 0;
    public Vector2D C, S;

    public RotRect(Vector2D newC, Vector2D newS, float newAng)
    {
        C = newC;
        S = newS;
        ang = newAng;
    }

    public void MoveTo(Vector2D newC)
    {
        C = newC;
    }

    public void Rotate(float dAng)
    {
        ang += dAng;
    }

    public Point getXY()
    {
        return new Point((int)(C.x - S.x/2), (int)(C.y - S.y/2));
    }
    public int getWidth()
    {
        return (int) S.x/2;
    }
    public int getHeight()
    {
        return (int) S.y/2;
    }

    public String toString()
    {
        return
                "Center: (" + C.x + "," + C.y + ")\n" +
                        "Width: " + S.x + "\n" +
                        "Height: " + S.y + "\n" +
                        "Rotation: " + ang;
    }
}
