package com.justsomeapp.bar;

import android.graphics.Point;

import java.util.List;

public class Asteroid extends Debris
{
    public static final int MAX_WIDTH   = 15;
    public static final int MAX_HEIGHT  = 15;
    public static final int MIN_WIDTH   = 5;
    public static final int MIN_HEIGHT  = 5;
    public static final int MIN_SPEED_DEFAULT = 50;
    public static final int MAX_SPEED_DEFAULT = 50;
    public static       int MIN_SPEED   = 50;
    public static       int MAX_SPEED   = 100;
    public static final int GOOD_COLOR  = 0xff11dd22;
    public static final int BAD_COLOR   = 0xffff1111;
    public static final int ENEMY_COLOR = 0xff4470ff;

    // Try to keep the sum equal to 100 to make things simple
    public static final int CHANCE_ENEMY           = 80;
    public static final int CHANCE_SPEEDUP         = 3;
    public static final int CHANCE_SPEEDDOWN       = 3;
    public static final int CHANCE_INVULNERABILITY = 3;
    public static final int CHANCE_SHRINK          = 3;
    public static final int CHANCE_GROW            = 2;
    public static final int CHANCE_TRACEROUTES     = 3;
    public static final int CHANCE_POINTS          = 3;
    public static final int CHANCE_SUM = CHANCE_ENEMY + CHANCE_SPEEDUP + CHANCE_SPEEDDOWN + CHANCE_INVULNERABILITY + CHANCE_SHRINK + CHANCE_GROW + CHANCE_TRACEROUTES + CHANCE_POINTS;

    public static boolean isSpeedModified = false;
    public static final float SCORE_MODIFIER_DEFAULT = 1.5f;
    public static       float SCORE_MODIFIER = 1.5f;

    public Type        type;
    public enum        Type
    {
        ENEMY,
        SPEEDUP,
        SPEEDDOWN,
        INVULNERABILITY,
        SHRINK,
        GROW,
        TRACEROUTES,
        POINTS;

        public boolean isGood()
        {
            switch(this)
            {
                case SPEEDUP:case GROW: return false;
                case SPEEDDOWN:case INVULNERABILITY:case SHRINK:case TRACEROUTES:case POINTS: return true;
                default: return false;
            }
        }
        public boolean isBad()
        {
            switch(this)
            {
                case SPEEDUP:case GROW: return true;
                case SPEEDDOWN:case INVULNERABILITY:case SHRINK:case TRACEROUTES:case POINTS: return false;
                default: return false;
            }
        }
        public boolean isEnemy()
        {
            switch(this)
            {
                case ENEMY: return true;
                default: return false;
            }
        }
    }

    public void setType(Type type)
    {
        this.type = type;
    }

    public Asteroid(int srcX, int srcY, int dstX, int dstY, Type type, float speed, int width, int height, float rotation_velocity)
    {
        super(srcX, srcY, dstX, dstY, speed, width, height, rotation_velocity);
        this.type = type;
        if (type.isGood()) { color = GOOD_COLOR; }
        else if (type.isBad()) { color = BAD_COLOR; }
        else if (type.isEnemy()) { color = ENEMY_COLOR; }
    }
    public Asteroid(Point src, Point dst, Type type, float speed, int width, int height, float rotation_velocity)
    {
        this(src.x, src.y, dst.x, dst.y, type, speed, width, height, rotation_velocity);
    }
    public Asteroid(List<Point> points, Type type, float speed, int width, int height, float rotation_velocity)
    {
        super(points, speed, width, height, rotation_velocity);
        this.type = type;
        if (type.isGood()) { color = GOOD_COLOR; }
        else if (type.isBad()) { color = BAD_COLOR; }
        else if (type.isEnemy()) { color = ENEMY_COLOR; }
    }

    public boolean isContrail()
    {
        return false;
    }
    public void setContrail(boolean val) {}
}
