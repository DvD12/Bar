package com.justsomeapp.bar;

import android.graphics.Point;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import com.justsomeapp.bar.core.RotRect;
import com.justsomeapp.bar.core.Vector2D;

public class Debris
{
    public static final int MAX_WIDTH = 2;
    public static final int MAX_HEIGHT = 2;
    public static final int MIN_WIDTH = 1;
    public static final int MIN_HEIGHT = 1;
    public static final int MIN_SPEED = 150;
    public static final int MAX_SPEED = 500;
    public static final int MIN_ROTATION_VELOCITY = -15;
    public static final int MAX_ROTATION_VELOCITY = 15;

    public float       lifeTime = 0;
    public List<Point> points = new ArrayList<>();
    public Point       pos; // position relative to CENTER of asteroid, not (0,0) edges!
    protected float    posX; // they're needed so that pos is clipped to nearest integer. Else we lose information when incrementing position by <1 increments and all we'd get is movement towards 45° axes. The end result IS an int anyway because the world is made up by integer coordinates, not floats.
    protected float    posY;
    public int         width;
    public int         height;
    public float       speed = 1;
    public float       rotation = 0;
    public float       rotation_velocity = 0; // rad/s
    public float       dirVersorX = 0;
    public float       dirVersorY = 0;
    public int         color = 0xffffffff;
    private boolean    isContrail = false;
    public static boolean showRoute = false;

    protected float getVersorComponent(boolean isX, boolean forceRecalc)
    {
        if ((dirVersorX == 0 && dirVersorY == 0) || forceRecalc)
        {
            /*
             * Get direction versor (unitary vector):
             * 1. Define destination vector D as the vector linking source and destination:
             *      D  := (dst.x - src.x, dst.y - src.y) = (x,y)
             * 2. Get D's mod M:
             *      M  := |D| = sqrt(x^2 + y^2)
             * 3. Define direction versor DD as D's components divided by M:
             *      DD := D/M = (x/M, y/M)
             */
            int x = getDst().x - getSrc().x;
            int y = getDst().y - getSrc().y;
            float mod = (float) (Math.sqrt(x*x + y*y));
            dirVersorX = x/mod;
            dirVersorY = y/mod;
            if (isX)
            {
                return dirVersorX;
            }
            else
            {
                return dirVersorY;
            }
        }
        else
        {
            if (isX) { return dirVersorX; }
            else { return dirVersorY; }
        }
    }

    public void advance(float deltaTime)
    {
        float advX = getVersorComponent(true, false) * speed * deltaTime;
        float advY = getVersorComponent(false, false) * speed * deltaTime;
        posX += advX;
        posY += advY;
        pos.x = (int) posX;
        pos.y = (int) posY;

        rotation += rotation_velocity*deltaTime;
        rotation = (float) (rotation % (2*Math.PI));
    }
    public void setSrc(Point src)
    {
        if (this.points.size() == 0)
        {
            points.add(src);
        }
        else
        {
            points.set(0, src);
        }
    }
    public void setPos(Point pos)
    {
        this.pos = pos;
        posX = pos.x;
        posY = pos.y;
    }
    public void setDst(Point dst)
    {
        if (this.points.size() == 0)
        {
            points.add(new Point(0,0));
            points.add(dst);
        }
        else
        {
            points.set(points.size()-1, dst);
        }
    }
    public void setSpeed(float speed)
    {
        this.speed = speed;
    }
    public void setWidth(int width)
    {
        this.width = width;
    }
    public void setHeight(int height)
    {
        this.height = height;
    }
    public void setContrail(boolean val) { this.isContrail = val; }

    public Point getSrc()
    {
        return this.points.get(0);
    }
    public Point getDst()
    {
        return this.points.get(points.size() - 1);
    }
    public Point getPos()
    {
        return pos;
    }
    public RotRect getRotRect() { int w = width/2; int h = height/2; if(w==0) {w=1;} if (h==0) {h=1;} return new RotRect(new Vector2D(pos.x,pos.y), new Vector2D(w,h), rotation); }
    public boolean isContrail() { return this.isContrail; }

    public String toString()
    {
        String output = this.getClass().toString() + "\n" + "---------\n" + "Pos: " + pos.toString() + "\n" + "Src: " + getSrc().toString() + "\n" + "Dst: " + getDst().toString() + "\n" + "Dir: " + "(" + dirVersorX + "," + dirVersorY + ")";
        return output;
    }

    public Debris(int srcX, int srcY, int dstX, int dstY, float speed, int width, int height, float rotation_velocity)
    {
        points.add(new Point(srcX, srcY));
        points.add(new Point(dstX, dstY));
        this.speed = speed;
        this.width = width;
        this.height = height;
        this.pos = new Point(srcX, srcY);
        posX = pos.x;
        posY = pos.y;
        this.rotation_velocity = rotation_velocity;
    }
    public Debris(Point src, Point dst, float speed, int width, int height, float rotation_velocity)
    {
        this(src.x, src.y, dst.x, dst.y, speed, width, height, rotation_velocity);
    }
    public Debris(Point src, Point dst, float speed, int width, int height, int color, float rotation_velocity)
    {
        this(src.x, src.y, dst.x, dst.y, speed, width, height, rotation_velocity);
        this.color = color;
    }
    public Debris(List<Point> points, float speed, int width, int height, float rotation_velocity)
    {
        if (points.size() < 2)
        {
            Log.d(this.getClass().toString(), "Too few points allocated to new asteroid!");
            this.points.add(new Point(0,0));
            this.points.add(new Point(0,0));
            this.speed = 1;
            this.width = 10;
            this.height = 10;
            this.pos = new Point(0,0);
            this.rotation_velocity = rotation_velocity;
        }
        else
        {
            for (int i = 0; i < points.size(); i++)
            {
                this.points.add(points.get(i));
            }
            this.speed = speed;
            this.width = width;
            this.height = height;
            this.pos = points.get(0);
            posX = pos.x;
            posY = pos.y;
            this.rotation_velocity = rotation_velocity;
        }
    }
}
