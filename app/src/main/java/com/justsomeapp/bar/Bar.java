package com.justsomeapp.bar;

import android.graphics.Point;

import com.justsomeapp.bar.core.RotRect;
import com.justsomeapp.bar.core.Vector2D;

public class Bar
{
    public float rotation = 0;
    public float velocity = (float) 1;
    public static int WIDTH_DEFAULT;
    public int width, height;
    public int color = 0xffee1122;
    public Point center;
    public boolean isInvulnerable = false;

    public Bar(int width, int height, int center_x, int center_y)
    {
        this.width = width;
        this.height = height;
        WIDTH_DEFAULT = width;
        this.center = new Point(center_x, center_y);
    }

    public RotRect getRotRect()
    {
        //return new RotRect(new Vector2D(240, 160), new Vector2D(80, 2), 0);
        return new RotRect(new Vector2D(center.x, center.y), new Vector2D(width/2, height/2), rotation);
    }

    public void rotate(float x, float y, float z, float deltaTime)
    {
        // x, y, z should come from accelerometer
        // But since this is the Model component of MVC we don't deal with
        // where this data comes from, here.
        float mod = (float) (Math.sqrt(x*x + y*y + z*z));
        float result = (float) (Math.asin(-y/mod)) * Settings.sensitivity;
        float delta = Math.abs(result - rotation);
        if (result > rotation) { rotation += delta*velocity*deltaTime; }
        else { rotation -= delta*velocity*deltaTime; }
    }
    public void setRotation(int rot)
    {
        if (rot >= 2*Math.PI || rot < 0) { rotation = (float) (Math.abs(rot) % 2*Math.PI); }
        else { rotation = rot; }
    }
    public void dRotation(int rot)
    {
        rotation += rot;
        if (rotation >= 2*Math.PI || rotation < 0) { rotation = (float) (Math.abs(rotation) % 2*Math.PI); }
    }
    public void setVelocity(int v)
    {
        velocity = v;
    }
    public void dVelocity(int dv)
    {
        velocity += dv;
    }
}
