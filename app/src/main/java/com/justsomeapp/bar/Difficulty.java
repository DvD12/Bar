package com.justsomeapp.bar;

public class Difficulty
{
    public static final int MAX_DIFF_LEVELS = 4;
    public static int difficulty_level = Settings.difficultyLevel;

    private static final float TIME_SCORE_MODIFIER_0 = 0.5f;
    private static final float WEAPON_RADIUS_0 = 1.2f;
    private static final float ZAPPER_REGEN_TIME_0 = 1.2f;
    private static final float MAX_SPEED_0 = 0.7f;
    private static final float MIN_SPEED_0 = 0.7f;
    private static final float SCORE_MODIFIER_0 = 0.4f;

    private static final float TIME_SCORE_MODIFIER_1 = 1f;
    private static final float WEAPON_RADIUS_1 = 1f;
    private static final float ZAPPER_REGEN_TIME_1 = 1f;
    private static final float MAX_SPEED_1 = 1f;
    private static final float MIN_SPEED_1 = 1f;
    private static final float SCORE_MODIFIER_1 = 1f;

    private static final float TIME_SCORE_MODIFIER_2 = 1.4f;
    private static final float WEAPON_RADIUS_2 = .98f;
    private static final float ZAPPER_REGEN_TIME_2 = .85f;
    private static final float MAX_SPEED_2 = 1.6f;
    private static final float MIN_SPEED_2 = 1.6f;
    private static final float SCORE_MODIFIER_2 = 1.4f;

    private static final float TIME_SCORE_MODIFIER_3 = 1.6f;
    private static final float WEAPON_RADIUS_3 = .95f;
    private static final float ZAPPER_REGEN_TIME_3 = .5f;
    private static final float MAX_SPEED_3 = 3.4f;
    private static final float MIN_SPEED_3 = 3.4f;
    private static final float SCORE_MODIFIER_3 = 1.6f;

    public static float TIME_SCORE_MODIFIER = TIME_SCORE_MODIFIER_1;
    public static float WEAPON_RADIUS = WEAPON_RADIUS_1;
    public static float ZAPPER_REGEN_TIME = ZAPPER_REGEN_TIME_1;
    public static float MAX_SPEED = MAX_SPEED_1;
    public static float MIN_SPEED = MIN_SPEED_1;
    public static float SCORE_MODIFIER = SCORE_MODIFIER_1;

    public static void updateDifficulty()
    {
        updateDifficulty(difficulty_level);
    }

    public static void updateDifficulty(int diff)
    {
        difficulty_level = diff;
        switch(diff)
        {
            case 0: // EASY
                TIME_SCORE_MODIFIER = TIME_SCORE_MODIFIER_0;
                WEAPON_RADIUS = WEAPON_RADIUS_0;
                ZAPPER_REGEN_TIME = ZAPPER_REGEN_TIME_0;
                MAX_SPEED = MAX_SPEED_0;
                MIN_SPEED = MIN_SPEED_0;
                SCORE_MODIFIER = SCORE_MODIFIER_0;
                break;
            case 1: // NORMAL
                TIME_SCORE_MODIFIER = TIME_SCORE_MODIFIER_1;
                WEAPON_RADIUS = WEAPON_RADIUS_1;
                ZAPPER_REGEN_TIME = ZAPPER_REGEN_TIME_1;
                MAX_SPEED = MAX_SPEED_1;
                MIN_SPEED = MIN_SPEED_1;
                SCORE_MODIFIER = SCORE_MODIFIER_1;
                break;
            case 2: // HARD
                TIME_SCORE_MODIFIER = TIME_SCORE_MODIFIER_2;
                WEAPON_RADIUS = WEAPON_RADIUS_2;
                ZAPPER_REGEN_TIME = ZAPPER_REGEN_TIME_2;
                MAX_SPEED = MAX_SPEED_2;
                MIN_SPEED = MIN_SPEED_2;
                SCORE_MODIFIER = SCORE_MODIFIER_2;
                break;
            case 3: // IMPOSSIBLE
                TIME_SCORE_MODIFIER = TIME_SCORE_MODIFIER_3;
                WEAPON_RADIUS = WEAPON_RADIUS_3;
                ZAPPER_REGEN_TIME = ZAPPER_REGEN_TIME_3;
                MAX_SPEED = MAX_SPEED_3;
                MIN_SPEED = MIN_SPEED_3;
                SCORE_MODIFIER = SCORE_MODIFIER_3;
                break;
        }

        World.TIME_SCORE_MODIFIER = World.TIME_SCORE_MODIFIER_DEFAULT * TIME_SCORE_MODIFIER;
        World.WEAPON_RADIUS = (int) (World.WEAPON_RADIUS_DEFAULT * WEAPON_RADIUS);
        World.ZAPPER_REGEN_TIME = World.ZAPPER_REGEN_TIME_DEFAULT * ZAPPER_REGEN_TIME;

        Asteroid.MIN_SPEED = (int) (Asteroid.MIN_SPEED_DEFAULT * MIN_SPEED);
        Asteroid.MAX_SPEED = (int) (Asteroid.MAX_SPEED_DEFAULT * MAX_SPEED);
        Asteroid.SCORE_MODIFIER = Asteroid.SCORE_MODIFIER_DEFAULT * SCORE_MODIFIER;

        GameScreen.hints[1] = "you can tap squares once every " + World.ZAPPER_REGEN_TIME + " seconds";
    }

    public static String getDifficultyName()
    {
        switch(difficulty_level)
        {
            case (0): return "EASY";
            case (1): return "NORMAL";
            case (2): return "HARD";
            case (3): return "IMPOSSIBLE";
            default : return "EASY";
        }
    }
}
