package com.justsomeapp.bar;

public class Bonus
{
    public static final float SPEEDUP_TIME = 5;
    public static final float SPEEDDOWN_TIME = 5;
    public static final float INVULNERABILITY_TIME = 10;
    public static final float SHRINK_TIME = 5;
    public static final float GROW_TIME = 3;
    public static final float TRACEROUTES_TIME = 10;

    public static float       speedUpTime = 0;
    public static float       speedDownTime = 0;
    public static float       invulnerabilityTime = 0;
    public static float       shrinkTime = 0;
    public static float       growTime = 0;
    public static float       traceRoutesTime = 0;

    public static boolean     isSpeedUp = false;
    public static boolean     isSpeedDown = false;
    public static boolean     isInvulnerability = false;
    public static boolean     isShrink = false;
    public static boolean     isGrow = false;
    public static boolean     isTraceRoutes = false;

    public static final float SPEED_UP_MODIFIER = 1.5f;
    public static final float SPEED_DOWN_MODIFIER = 0.5f;
    public static final float shrinkModifier = 0.75f;
    public static final float growModifier = 1.25f;
    public static final int   POINTS_GIVEN = 500;

    // Pointers needed to modify game state
    public Bar bar;
    public World world;
    public int oldColor;

    public void update(float deltaTime) // eventually remove effects here
    {
        if (bar != null)
        {
            if (isSpeedUp)
            {
                speedUpTime -= deltaTime;
                if(speedUpTime < 0)
                {
                    isSpeedUp = false;
                    Asteroid.MAX_SPEED = (int) (Asteroid.MAX_SPEED_DEFAULT * Difficulty.MAX_SPEED);
                    Asteroid.MIN_SPEED = (int) (Asteroid.MIN_SPEED_DEFAULT * Difficulty.MIN_SPEED);
                }
            }
            if (isSpeedDown)
            {
                speedDownTime -= deltaTime;
                if(speedDownTime < 0)
                {
                    isSpeedDown = false;
                    Asteroid.MAX_SPEED = (int) (Asteroid.MAX_SPEED_DEFAULT * Difficulty.MAX_SPEED);
                    Asteroid.MIN_SPEED = (int) (Asteroid.MIN_SPEED_DEFAULT * Difficulty.MIN_SPEED);
                }
            }
            if (isInvulnerability)
            {
                invulnerabilityTime -= deltaTime;
                if(invulnerabilityTime < 0)
                {
                    isInvulnerability = false;
                    bar.isInvulnerable = false;
                    bar.color = oldColor;
                }
            }
            if (isShrink)
            {
                shrinkTime -= deltaTime;
                if(shrinkTime < 0)
                {
                    isShrink = false;
                    bar.width = Bar.WIDTH_DEFAULT;
                }
            }
            if (isGrow)
            {
                growTime -= deltaTime;
                if(growTime < 0)
                {
                    isGrow = false;
                    bar.width = Bar.WIDTH_DEFAULT;
                }
            }
            if (isTraceRoutes)
            {
                traceRoutesTime -= deltaTime;
                if(traceRoutesTime < 0)
                {
                    isTraceRoutes = false;
                    Debris.showRoute = false;
                }
            }
        }
    }

    public void activate(Asteroid.Type t)
    {
        if (bar != null)
        {
            switch (t)
            {
                case SPEEDUP:
                {
                    isSpeedUp = true;
                    speedUpTime = SPEEDUP_TIME;

                    Asteroid.MAX_SPEED = (int) (Asteroid.MAX_SPEED_DEFAULT * Difficulty.MAX_SPEED * SPEED_UP_MODIFIER);
                    Asteroid.MIN_SPEED = (int) (Asteroid.MIN_SPEED_DEFAULT * Difficulty.MIN_SPEED * SPEED_UP_MODIFIER);

                    break;
                }
                case SPEEDDOWN:
                {
                    isSpeedDown = true;
                    speedDownTime = SPEEDDOWN_TIME;

                    Asteroid.MAX_SPEED = (int) (Asteroid.MAX_SPEED_DEFAULT * Difficulty.MAX_SPEED * SPEED_DOWN_MODIFIER);
                    Asteroid.MIN_SPEED = (int) (Asteroid.MIN_SPEED_DEFAULT * Difficulty.MIN_SPEED * SPEED_DOWN_MODIFIER);

                    break;
                }
                case INVULNERABILITY:
                {
                    isInvulnerability = true;
                    invulnerabilityTime = INVULNERABILITY_TIME;

                    bar.isInvulnerable = true;
                    bar.color = 0xFF3333FF;

                    break;
                }
                case SHRINK:
                {
                    isShrink = true;
                    shrinkTime = SHRINK_TIME;

                    bar.width = (int) (Bar.WIDTH_DEFAULT * shrinkModifier);

                    break;
                }
                case GROW:
                {
                    isGrow = true;
                    growTime = GROW_TIME;

                    bar.width = (int) (Bar.WIDTH_DEFAULT * growModifier);

                    break;
                }
                case TRACEROUTES:
                {
                    isTraceRoutes = true;
                    traceRoutesTime = TRACEROUTES_TIME;

                    Debris.showRoute = true;

                    break;
                }
                case POINTS: // one shot: needs no deactivation
                {
                    world.score += POINTS_GIVEN;
                }
                default:
            }
        }
    }

    public void resetToZero() // sets all bonii to false
    {
        speedUpTime = -10;
        speedDownTime = -10;
        invulnerabilityTime = -10;
        shrinkTime = -10;
        growTime = -10;
        traceRoutesTime = -10;
    }

    public Bonus(World world, Bar bar) {this.world = world; this.bar = bar; oldColor = bar.color;}
}
