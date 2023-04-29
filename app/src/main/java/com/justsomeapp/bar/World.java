package com.justsomeapp.bar;

import android.graphics.Point;

import java.util.Random;

import com.justsomeapp.bar.core.RotRect;
import com.justsomeapp.bar.core.Vector2D;
import com.justsomeapp.bar.framework.Input;

public class World
{
    /*
     *  WORLD DATA
     */
    // We map the world grid 1:1 to our virtual device resolution (320x480)
    static final int      WORLD_WIDTH = 480;
    static final int      WORLD_HEIGHT = 320;
    static final int      WORLD_CENTER_X = WORLD_WIDTH/2;
    static final int      WORLD_CENTER_Y = WORLD_HEIGHT/2;
    static final int      TOP_Y = 0; // side 0
    static final int      BOTTOM_Y = WORLD_HEIGHT; // side 1
    static final int      LEFT_X = 0; // side 2
    static final int      RIGHT_X = WORLD_WIDTH; // side 3
    public int            color = 0xff000000;
    public int[]          colors = new int[]{0xff000000, 0xff331166, 0xff772211, 0xff361829, 0xff202020, 0xff401020, 0xff001530, 0xff443322, 0xff157505, 0xff051545, 0xff123456, 0xff250505, 0xff153525, 0xff114300, 0xff155015, 0xff200000, 0xff050515};
    public float          red = 0x00;
    public float          green = 0x00;
    public float          blue = 0x00;
    static final float    BACKGROUND_CHANGE_TIME_COUNTER = 10;
    static final float    BACKGROUND_CHANGE_INCREMENT_TIME_COUNTER = (float) (0.04);
    static final float    BACKGROUND_CHANGE_TRANSITION_TIME = 5; // takes this many secs to change background color
    public int            currentColorIndex = 0;
    public int            nextColorIndex = 0;

    /*
     *  ASTEROIDS DATA -- should be incapsulated as we don't want the View to manipulate it
     */
    public Asteroid[]     asteroids = new Asteroid[MAX_ASTEROIDS];
    static final int      MAX_ASTEROIDS = 45;
    public float          ASTEROID_SPAWN_TIME = (float) (0.38);
    static final float    ASTEROID_OUT_OF_BOUNDS_GARBAGE_COLLECT_TIME = 10;

    /*
     *  DEBRIS DATA
     */
    public Debris[]       debriss = new Debris[MAX_DEBRIS];
    static final int      MAX_DEBRIS = 500;
    static final float    STAR_SPAWN_TIME = (float) (0.02);
    static final float    STAR_OUT_OF_BOUNDS_GARBAGE_COLLECT_TIME = (float) (1.5);
    static final int      MAX_DEBRIS_GENERATED_BY_ASTEROID = 10;
    static final float    CONTRAIL_LIFE_TIME = (float) (0.75);
    static final float    CONTRAIL_GENERATION_TIME = (float) (0.12);

    /*
     *  TIME DATA (all times in seconds)
     */
    float                 time = 0;
    float                 asteroidTimeCounter = 0;
    float                 asteroidOutOfBoundsGarbageCollectCounter = 0;
    float                 debrisTimeCounter = 0;
    float                 debrisOutOfBoundsGarbageCollectCounter = 0;
    float                 contrailGenerationTimeCounter = 0;
    float                 zapperTimeCounter = 0;
    float                 backgroundChangeTimeCounter = 0;
    float                 backgroundChangeIncrementTimeCounter = 0;

    /*
     *  PLAYER DATA
     */
    public Bar            bar;
    public float          score;
    public int            squaresDestroyed = 0;
    public Bonus          bonus;
    static final int      WEAPON_RADIUS_DEFAULT = 20;
    static int            WEAPON_RADIUS = 20;
    static final float    ZAPPER_REGEN_TIME_DEFAULT = 1.1f;
    static float          ZAPPER_REGEN_TIME = 1.1f;

    /*
     *  RANDOM
     */
    Random                random = new Random();
    public static final float TIME_SCORE_MODIFIER_DEFAULT = 10f;
    public static float       TIME_SCORE_MODIFIER = 10f;
    public boolean        gameOver = false;

    /*  ******************************
     *  METHODS
     */

    public World()
    {
        bar = new Bar(WORLD_WIDTH/3, 4, WORLD_WIDTH/2, WORLD_HEIGHT/2);
        bonus = new Bonus(this, bar);
        score = 0;
        for (int i = 0; i < MAX_ASTEROIDS; i++)
        {
            asteroids[i] = null;
        }
    }

    public void placeAsteroid()
    {
        int free = 0;
        boolean isFree = false;
        for (; free < MAX_ASTEROIDS; free++)
        {
            if(asteroids[free] == null) { isFree = true; break; }
        }
        if (isFree)
        {
            /*
             * WIDTH AND HEIGHT
             */
            int width = random.nextInt(Asteroid.MAX_WIDTH - Asteroid.MIN_WIDTH) + Asteroid.MIN_WIDTH;
            int height = random.nextInt(Asteroid.MAX_HEIGHT - Asteroid.MIN_HEIGHT) + Asteroid.MIN_HEIGHT;

            /*
             * POSITION AND DESTINATION
             */
            // Identify two random sides of the world rectangle:
            // 1: top, 2: bottom, 3: left, 4: right
            // First one is the side the asteroid spawns from, second one the side it's directed towards
            int startSide = random.nextInt(4);
            int endSide = 0;
            do
            {
                endSide = random.nextInt(4);
            }
            while (endSide == startSide);

            // Assign start and destination positions as identified side (X or Y) and the other coord as random
            int srcX = 0, srcY = 0, dstX = 0, dstY = 0;

            // width and height included so that it doesn't looks like it pops out of nowhere when spawning and it stops at the edge
            // Multiplied by 2 because they start at center
            int wpad = width*2;
            int hpad = height*2;
            // We do want to narrow destinations down. We don't want asteroids to fly almost parallel to edges.
            int widthLimiter = (int) (WORLD_WIDTH/1.25); // how many cells (pixels) we want to remove from randomness?
            int heightLimiter = (int) (WORLD_HEIGHT/1.25);
            int srcx = random.nextInt(WORLD_WIDTH);
            int srcy = random.nextInt(WORLD_HEIGHT);
            int dstx = random.nextInt(WORLD_WIDTH - widthLimiter) + widthLimiter/2;
            int dsty = random.nextInt(WORLD_HEIGHT - heightLimiter) + heightLimiter/2;
            switch(startSide)
            {
                case 0: srcX = srcx; srcY = TOP_Y - hpad; break;
                case 1: srcX = srcx; srcY = BOTTOM_Y + hpad; break;
                case 2: srcX = LEFT_X - wpad; srcY = srcy; break;
                case 3: srcX = RIGHT_X + wpad; srcY = srcy; break;
            }
            switch(endSide)
            {
                case 0: dstX = dstx; dstY = TOP_Y - hpad; break;
                case 1: dstX = dstx; dstY = BOTTOM_Y + hpad; break;
                case 2: dstX = LEFT_X - wpad; dstY = dsty; break;
                case 3: dstX = RIGHT_X + wpad; dstY = dsty; break;
            }

            Point src = new Point(srcX, srcY);
            Point dst = new Point(dstX, dstY);

            /*
             * SPEED (this arcane formula is necessary to have a float with 0.x precision)
             */
            float speed = random.nextInt((Asteroid.MAX_SPEED - Asteroid.MIN_SPEED) * 10 + 10)/10 + Asteroid.MIN_SPEED;
            float rotation_speed = random.nextInt((Asteroid.MAX_ROTATION_VELOCITY - Asteroid.MIN_ROTATION_VELOCITY) * 10 + 10)/10 + Asteroid.MIN_ROTATION_VELOCITY;

            /*
             * TYPE
             */
            int rand = random.nextInt(Asteroid.CHANCE_SUM);
            Asteroid.Type type;
            Asteroid.Type[] typeChances = new Asteroid.Type[Asteroid.CHANCE_SUM];
            int i = 0;
            for (int x = 0; x < Asteroid.CHANCE_ENEMY; ++x)
            {
                typeChances[i] = Asteroid.Type.ENEMY;
                ++i;
            }
            for (int x = 0; x < Asteroid.CHANCE_SPEEDUP; ++x)
            {
                typeChances[i] = Asteroid.Type.SPEEDUP;
                ++i;
            }
            for (int x = 0; x < Asteroid.CHANCE_SPEEDDOWN; ++x)
            {
                typeChances[i] = Asteroid.Type.SPEEDDOWN;
                ++i;
            }
            for (int x = 0; x < Asteroid.CHANCE_INVULNERABILITY; ++x)
            {
                typeChances[i] = Asteroid.Type.INVULNERABILITY;
                ++i;
            }
            for (int x = 0; x < Asteroid.CHANCE_SHRINK; ++x)
            {
                typeChances[i] = Asteroid.Type.SHRINK;
                ++i;
            }
            for (int x = 0; x < Asteroid.CHANCE_GROW; ++x)
            {
                typeChances[i] = Asteroid.Type.GROW;
                ++i;
            }
            for (int x = 0; x < Asteroid.CHANCE_TRACEROUTES; ++x)
            {
                typeChances[i] = Asteroid.Type.TRACEROUTES;
                ++i;
            }
            for (int x = 0; x < Asteroid.CHANCE_POINTS; ++x)
            {
                typeChances[i] = Asteroid.Type.POINTS;
                ++i;
            }
            type = typeChances[rand];

            /*
             * ASTEROID CREATION
             */
            Asteroid a = new Asteroid(src, dst, type, speed, width, height, rotation_speed);
            asteroids[free] = a;
        }
    }

    public void freeAsteroidsOutOfBounds()
    {
        for (int i = 0; i < MAX_ASTEROIDS; i++)
        {
            if (asteroids[i] == null) { continue; }
            Point pos = asteroids[i].getPos();
            if (pos.x > WORLD_WIDTH || pos.y > WORLD_HEIGHT || pos.x < 0 || pos.y < 0)
            {
                asteroids[i] = null;
            }
        }
    }

    public void destroyBar()
    {
        Point pos = bar.center;

        int debrisNumber = 50;
        for (int i = 0; i < debrisNumber; i++)
        {
            int dstX = (int) (pos.x - WORLD_HEIGHT/2 * Math.cos((Math.PI*2)*i/debrisNumber));
            int dstY = (int) (pos.y - WORLD_HEIGHT/2 * Math.sin((Math.PI*2)*i/debrisNumber));
            float rotation = (float) Math.PI/2 + (float) Math.PI*2*i/debrisNumber;
            Point dst = new Point(dstX, dstY);
            int speed = Debris.MIN_SPEED;
            int width = 3;
            int height = 32;
            int color = random.nextInt(0x00ffffff) + 0xff000000;
            Debris d = new Debris(pos, dst, speed, width, height, color, 0);
            d.rotation = rotation;
            placeDebris(d);
        }
        bar = null;
    }

    public void destroyAsteroid(Asteroid a, int j)
    {
        Point pos = a.getPos();

        int debrisNumber = random.nextInt(MAX_DEBRIS_GENERATED_BY_ASTEROID) + 10;
        for (int i = 0; i < debrisNumber; i++)
        {
            int dstX = (int) (pos.x - WORLD_HEIGHT/2 * Math.cos((Math.PI*2)*i/debrisNumber));
            int dstY = (int) (pos.y - WORLD_HEIGHT/2 * Math.sin((Math.PI*2)*i/debrisNumber));
            Point dst = new Point(dstX, dstY);
            int speed = Debris.MIN_SPEED;
            int width = 4;
            int height = 4;
            int color = a.color - 0x00005500;
            Debris d = new Debris(pos, dst, speed, width, height, color, 0);
            placeDebris(d);
        }
        asteroids[j] = null;
    }

    public void placeDebris(Debris d)
    {
        int free = 0;
        boolean isFree = false;
        for (; free < MAX_DEBRIS; free++)
        {
            if(debriss[free] == null) { isFree = true; break; }
        }
        if (isFree)
        {
            debriss[free] = d;
        }
    }

    public void placeDebris()
    {
        int free = 0;
        boolean isFree = false;
        for (; free < MAX_DEBRIS; free++)
        {
            if(debriss[free] == null) { isFree = true; break; }
        }
        if (isFree)
        {
            /*
             * WIDTH AND HEIGHT
             */
            int width = random.nextInt(Debris.MAX_WIDTH - Debris.MIN_WIDTH + 2) + Debris.MIN_WIDTH;
            int height = random.nextInt(Debris.MAX_HEIGHT - Debris.MIN_HEIGHT + 2) + Debris.MIN_HEIGHT;

            /*
             * POSITION AND DESTINATION
             */
            int srcX = random.nextInt(WORLD_WIDTH);
            int srcY = 0 - height;
            int dstX = srcX;
            int dstY = WORLD_HEIGHT + height;

            Point src = new Point(srcX, srcY);
            Point dst = new Point(dstX, dstY);

            /*
             * SPEED
             */
            float speed = random.nextInt((Debris.MAX_SPEED - Debris.MIN_SPEED) * 10 + 10)/10 + Debris.MIN_SPEED;

            /*
             * COLOR
             */
            int color = random.nextInt(0x00ffffff) + 0xff000001;

            /*
             * DEBRIS CREATION
             */
            Debris d = new Debris(src, dst, speed, width, height, color, 0);
            debriss[free] = d;
        }
    }

    public void freeDebrisOutOfBounds()
    {
        for (int i = 0; i < MAX_DEBRIS; i++)
        {
            if (debriss[i] == null) { continue; }
            Point pos = debriss[i].getPos();
            if (pos.x > WORLD_WIDTH || pos.y > WORLD_HEIGHT || pos.x < 0 || pos.y < 0)
            {
                debriss[i] = null;
            }
        }
    }

    // Gradually changes background color
    public void colorChanger(int startColor, int endColor, float deltaTime)
    {
        if (this.color != endColor)
        {
            if (backgroundChangeIncrementTimeCounter > BACKGROUND_CHANGE_INCREMENT_TIME_COUNTER)
            {
                backgroundChangeIncrementTimeCounter = backgroundChangeIncrementTimeCounter % deltaTime;
                int startRed = (startColor & 0x00ff0000) >> 16;
                int startGreen = (startColor & 0x0000ff00) >> 8;
                int startBlue = startColor & 0x000000ff;
                int endRed = (endColor & 0x00ff0000) >> 16;
                int endGreen = (endColor & 0x0000ff00) >> 8;
                int endBlue = endColor & 0x000000ff;
                float deltaRed     = (endRed - startRed) * (BACKGROUND_CHANGE_INCREMENT_TIME_COUNTER / BACKGROUND_CHANGE_TRANSITION_TIME);
                float deltaGreen   = (endGreen - startGreen) * (BACKGROUND_CHANGE_INCREMENT_TIME_COUNTER / BACKGROUND_CHANGE_TRANSITION_TIME);
                float deltaBlue    = (endBlue - startBlue) * (BACKGROUND_CHANGE_INCREMENT_TIME_COUNTER / BACKGROUND_CHANGE_TRANSITION_TIME);
                if (Math.abs(endRed - startRed) < deltaRed+1 && Math.abs(endGreen - startGreen) < deltaGreen+1 && Math.abs(endBlue - startBlue) < deltaBlue+1)
                {
                    red = endRed;
                    green = endGreen;
                    blue = endBlue;
                }
                else
                {
                    red += deltaRed;
                    green += deltaGreen;
                    blue += deltaBlue;
                }
                int nextColor = (int) (0xff000000 + ((int) red << 16) + ((int) green << 8) + blue);
                this.color = nextColor;
            }
        }
    }

    // Our controller part of the MVC pattern. Everything else is pretty much model: modeling the state of the world and how it can be manipulated
    public void update(float deltaTime, float x, float y, float z, Input.TouchEvent touchEvent, boolean alwaysInvulnerable)
    {
        /*
         * Time management: increase time and counters. Make sure all variables in the "Time" section are increased. Also increases score
         */
        time += deltaTime;
        asteroidTimeCounter += deltaTime;
        asteroidOutOfBoundsGarbageCollectCounter += deltaTime;
        debrisTimeCounter += deltaTime;
        debrisOutOfBoundsGarbageCollectCounter += deltaTime;
        bonus.update(deltaTime); // updates bonus timers AND deals with effects too
        score += deltaTime*TIME_SCORE_MODIFIER;
        for (int i = 0; i < MAX_ASTEROIDS; i++)
        {
            if (asteroids[i] == null) { continue; }
            asteroids[i].lifeTime += deltaTime;
        }
        for (int i = 0; i < MAX_DEBRIS; i++)
        {
            if (debriss[i] == null) { continue; }
            debriss[i].lifeTime += deltaTime;
        }
        contrailGenerationTimeCounter += deltaTime;
        zapperTimeCounter += deltaTime;
        backgroundChangeTimeCounter += deltaTime;
        backgroundChangeIncrementTimeCounter += deltaTime;

        /*
         * World management
         */
        if (backgroundChangeTimeCounter > BACKGROUND_CHANGE_TIME_COUNTER)
        {
            currentColorIndex = nextColorIndex;
            backgroundChangeTimeCounter = backgroundChangeTimeCounter % BACKGROUND_CHANGE_TIME_COUNTER;
            nextColorIndex = random.nextInt(colors.length);
        }
        colorChanger(colors[currentColorIndex], colors[nextColorIndex], deltaTime);

        /*
         * Bar (player) management
         */
        if (bar != null)
        {
            bar.rotate(x,y,z, deltaTime);

            // Collision detection
            for (int i = 0; i < MAX_ASTEROIDS; i++)
            {
                Asteroid a = asteroids[i];
                if (a == null) { continue; }
                RotRect ar = a.getRotRect();
                //Log.d("Events", asteroids[i].posX + "");
                //Log.d("Events", "Colliding: " + ar.toString());
                RotRect b = bar.getRotRect();

                if (rotRectsCollision(ar, b))
                {
                    Asteroid.Type type = a.type;
                    if (type.isEnemy())
                    {
                        Assets.Expl.play();
                        if (!bar.isInvulnerable && !alwaysInvulnerable)
                        {
                            destroyBar();
                            Assets.BarExpl.play();
                            gameOver = true;
                            break;
                        }
                    }
                    else if (type.isGood() || type.isBad())
                    {
                        if (type.isGood()) { Assets.Bonus.play(); } if (type.isBad()) { Assets.Malus.play(); }
                        bonus.activate(type);
                    }
                    destroyAsteroid(a,i);
                }
            }
        }

        /*
         * Asteroids management
         */
        for (int i = 0; i < MAX_ASTEROIDS; i++)
        {
            if (asteroids[i] == null) { continue; }
            asteroids[i].advance(deltaTime);
        }
        if (asteroidTimeCounter > ASTEROID_SPAWN_TIME)
        {
            asteroidTimeCounter = asteroidTimeCounter % ASTEROID_SPAWN_TIME;
            placeAsteroid();
        }
        if (asteroidOutOfBoundsGarbageCollectCounter > ASTEROID_OUT_OF_BOUNDS_GARBAGE_COLLECT_TIME)
        {
            asteroidOutOfBoundsGarbageCollectCounter = asteroidOutOfBoundsGarbageCollectCounter % ASTEROID_OUT_OF_BOUNDS_GARBAGE_COLLECT_TIME;
            freeAsteroidsOutOfBounds();
        }

        /*
         * Debris management
         */
        for (int i = 0; i < MAX_DEBRIS; i++)
        {
            if (debriss[i] == null) { continue; }
            if (debriss[i].isContrail() && debriss[i].lifeTime > CONTRAIL_LIFE_TIME) { debriss[i] = null; continue; }
            debriss[i].advance(deltaTime);
        }
        if (debrisTimeCounter > STAR_SPAWN_TIME)
        {
            debrisTimeCounter = debrisTimeCounter % STAR_SPAWN_TIME;
            placeDebris();
        }
        if (debrisOutOfBoundsGarbageCollectCounter > STAR_OUT_OF_BOUNDS_GARBAGE_COLLECT_TIME)
        {
            debrisOutOfBoundsGarbageCollectCounter = debrisOutOfBoundsGarbageCollectCounter % STAR_OUT_OF_BOUNDS_GARBAGE_COLLECT_TIME;
            freeDebrisOutOfBounds();
        }
        if (contrailGenerationTimeCounter > CONTRAIL_GENERATION_TIME) // asteroid contrail
        {
            contrailGenerationTimeCounter = contrailGenerationTimeCounter % CONTRAIL_GENERATION_TIME;
            for (int i = 0; i < MAX_ASTEROIDS; i++)
            {
                if (asteroids[i] == null) { continue; }
                int contrailNumber = random.nextInt(4);
                for (int j = 0; j < contrailNumber; j++)
                {
                    int randX = random.nextInt(200) - 100;
                    int randY = random.nextInt(200) - 100;
                    int dstX = asteroids[i].getSrc().x - randX;
                    int dstY = asteroids[i].getSrc().y - randY;
                    Point dst = new Point(dstX, dstY);
                    int speed = (int) (asteroids[i].speed)/2;
                    int width = 3;
                    int height = 3;
                    int color = 0x45ffa010 + randX;
                    Debris d = new Debris(asteroids[i].getPos(), dst, speed, width, height, color, 0);
                    d.setContrail(true);
                    placeDebris(d);
                }
            }
        }

        /*
         * User input management
         */
        if (touchEvent != null)
        {
            if (zapperTimeCounter > ZAPPER_REGEN_TIME)
            {
                int a = touchEvent.x;
                int b = touchEvent.y;
                Point c = new Point(a,b);

                /*
                int debrisNumber = 2;
                for (int i = 0; i < debrisNumber; i++)
                {
                    int dstX = a + (a - WORLD_CENTER_X);
                    int dstY = b + (b - WORLD_CENTER_Y);
                    Point dst = new Point(dstX, dstY);
                    int speed = 300;
                    int width = 6;
                    int height = 6;
                    int color = 0xffddaa66;
                    int posX = (int) (bar.center.x + (i*20 - 10) * Math.cos(bar.rotation));
                    int posY = (int) (bar.center.y + (i*20 - 10) * Math.sin(bar.rotation));
                    Debris d = new Debris(new Point(posX, posY), dst, speed, width, height, color, 0);
                    placeDebris(d);
                } */

                boolean atLeastOneDestroyed = false;
                for (int i = 0; i < MAX_ASTEROIDS; i++)
                {
                    if (asteroids[i] == null) { continue; }
                    Point p = asteroids[i].getPos();
                    if (inBoundsCircle(p, c, WEAPON_RADIUS))
                    {
                        score += Asteroid.SCORE_MODIFIER*asteroids[i].width*asteroids[i].height;
                        ++squaresDestroyed;
                        Assets.Expl.play();
                        destroyAsteroid(asteroids[i], i);
                        atLeastOneDestroyed = true;
                    }
                }
                if (atLeastOneDestroyed) { zapperTimeCounter = 0; } // reset counter
            }
        }
    }

    // Checks whether p falls within circle centered in c with radius r
    private boolean inBoundsCircle(Point p, Point c, int r)
    {
        // Define a line connecting P and C.
        int dx = p.x - c.x;
        int dy = p.y - c.y;

        // Calculate its length
        float l = (float) (Math.sqrt(dx*dx + dy*dy));

        // If this line is smaller than R, then the point falls within C
        return (l < r);
    }

    public void reset()
    {
        gameOver = false;
        for (int j = 0; j < MAX_ASTEROIDS; j++)
        {
            if (asteroids[j] == null) { continue; }
            Assets.Expl.play();
            destroyAsteroid(asteroids[j], j);
        }
        if (bar == null)
        {
            bar = new Bar(WORLD_WIDTH/3, 4, WORLD_WIDTH/2, WORLD_HEIGHT/2);
            bonus = new Bonus(this, bar);
        }
        bar.isInvulnerable = false;
        score = 0;
        time = 0;
        squaresDestroyed = 0;
        bonus.resetToZero();
    }

    // http://www.ragestorm.net/tutorial?id=22
    private boolean rotRectsCollision(RotRect rr1, RotRect rr2)
    {
        Vector2D A = new Vector2D();
        Vector2D B = new Vector2D();   // vertices of the rotated
        Vector2D C;      // center of rr2
        Vector2D BL, TR; // vertices of rr2 (bottom-left, top-right)

        float ang = rr1.ang - rr2.ang, // orientation of rotated rr1
                cosa = (float)Math.cos((double)ang),           // precalculated trigonometic -
                sina = (float)Math.sin((double)ang);              // - values for repeated use

        float t, x, a;      // temporary variables for various uses
        float dx;           // deltaX for linear equations
        float vert1, vert2; // min/max vertical values

        // move rr2 to make rr1 cannonic
        C = (Vector2D) rr2.C.copy();
        C.subVector(rr1.C);



        // rotate rr2 clockwise by rr2.ang to make rr2 axis-aligned
        C.rotateClockwise(rr2.ang);

        // calculate vertices of (moved and axis-aligned := 'ma') rr2
        BL = (Vector2D) C.copy();
        TR = (Vector2D) C.copy();
        BL.subVector(rr2.S);
        TR.addVector(rr2.S);

        // calculate vertices of (rotated := 'r') rr1
        A.x = -rr1.S.y*sina; B.x = A.x; t = rr1.S.x*cosa; A.x += t; B.x -= t;
        A.y =  rr1.S.y*cosa; B.y = A.y; t = rr1.S.x*sina; A.y += t; B.y -= t;

        t = sina*cosa;

        // verify that A is vertical min/max, B is horizontal min/max
        if (t < 0)
        {
            t = A.x; A.x = B.x; B.x = t;
            t = A.y; A.y = B.y; B.y = t;
        }

        // verify that B is horizontal minimum (leftest-vertex)
        if (sina < 0) { B.x = -B.x; B.y = -B.y; }

        // if rr2(ma) isn't in the horizontal range of
        // colliding with rr1(r), collision is impossible
        if (B.x > TR.x || B.x > -BL.x) { return false; }

        // if rr1(r) is axis-aligned, vertical min/max are easy to get
        if (t == 0) {vert1 = A.y; vert2 = -vert1; }
        // else, find vertical min/max in the range [BL.x, TR.x]
        else
        {
            x = BL.x-A.x; a = TR.x-A.x;
            vert1 = A.y;
            // if the first vertical min/max isn't in (BL.x, TR.x), then
            // find the vertical min/max on BL.x or on TR.x
            if (a*x > 0)
            {
                dx = A.x;
                if (x < 0) { dx -= B.x; vert1 -= B.y; x = a; }
                else       { dx += B.x; vert1 += B.y; }
                vert1 *= x; vert1 /= dx; vert1 += A.y;
            }

            x = BL.x+A.x; a = TR.x+A.x;
            vert2 = -A.y;
            // if the second vertical min/max isn't in (BL.x, TR.x), then
            // find the local vertical min/max on BL.x or on TR.x
            if (a*x > 0)
            {
                dx = -A.x;
                if (x < 0) { dx -= B.x; vert2 -= B.y; x = a; }
                else       { dx += B.x; vert2 += B.y; }
                vert2 *= x; vert2 /= dx; vert2 -= A.y;
            }
        }
        if (vert1 > vert2) { t = vert1; vert1 = vert2; vert2 = t; }

        // check whether rr2(ma) is in the vertical range of colliding with rr1(r)
        // (for the horizontal range of rr2)
        return !((vert1 < BL.y && vert2 < BL.y) ||
                (vert1 > TR.y && vert2 > TR.y));
    }
}
