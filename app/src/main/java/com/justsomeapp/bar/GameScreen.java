package com.justsomeapp.bar;

import android.graphics.Point;
import android.graphics.Typeface;

import java.util.ArrayList;
import java.util.List;

import com.justsomeapp.bar.core.AllScores;
import com.justsomeapp.bar.core.NewScore;
import com.justsomeapp.bar.framework.Game;
import com.justsomeapp.bar.framework.Graphics;
import com.justsomeapp.bar.framework.Input;
import com.justsomeapp.bar.framework.Screen;

public class GameScreen extends Screen
{
    public static String[] hints = new String[]
	{
		"tap squares to zap them",
		"you can tap squares once every " + World.ZAPPER_REGEN_TIME + " seconds",
		"BLUE squares destroy you!",
		"GREEN squares give you invulnerability, points...",
		"RED squares give you nasty effects!"
	};
    public String[] silly = new String[]
	{
		"How much wood would a woodchuck chuck if a woodchuck could chuck wood?",
		"the year is not 1982",
		"square zaps to tap them",
		"them squares to zap taps",
		"why do they call it oven when you of in the cold food of out hot eat the food?",
		"tapping this place yields absolutely nothing of value",
		"stop staring at this dull moving text",
		"uyo cna stlil mkae seens otu fo tihs",
		"e^{i pi} + 1 = 0",
		"no squares were harmed during the making of this game",
		"0x2A",
		"this game is deceptively easy",
		"this game doesn't want to offend anybody... yet",
		"SMORM hath spoken: this game isn't worth Blondi's left paw",
		"Remember to go outside and touch grass",
		"if you say 'raB' three times in front of a mirror, absolutely nothing happens",
		"Hold your breath for BAR v2.0, now with 33% extra pixels!",
		"Don't eat yellow snow",
	};
    public int hint_x = 480;
    public int hint_y = 50;
    public int hint_pos = 0;
    public int silly_pos = 0;
    public static final float HINT_MOVE_TIME = 0.03f;
    public float hintMoveCounter = 0;
    public String[] badScore = new String[]{"don't give up your job", "go take up knitting", "don't give up trying", "my granny scores better than that", "you're supposed to avoid the blue squares", "hope this is your first game or something", "i'm sure you can do much better", "'disappointing' is an understatement", "FAIL.", "LOL U N00B", "blame it on bad luck"};
    public String[] mediumScore = new String[]{"pretty solid", "keep it up", "practice makes perfect"};
    public String[] highScore = new String[]{"everybody bow down to you", "impressive", "hail the new master"};
    public String taunt;
    public Typeface font;
    public int mediumThreshold = 1000;
    public int highThreshold = 10000;
    public int timeLapsed = 0;
    public int squaresDestroyed = 0;
    public int score = 0;
    public AllScores allScores;
    public NewScore newScore;
    public boolean isScoreDisplayToday = false;
    public boolean isShiftPressed = false;
    public boolean isCapsPressed = false;
    public String submitName = "";
    public boolean isScoreSubmitted = false;

    enum GameState
    {
        Ready,
        Running,
        Paused,
        GameOver,

        // Why are these here? These aren't game states.
        // They are here because we keep the game running even with the settings/hi-scores displayed.
        // Had we adopted a static design (no game flowing in the background), we would've
        // created a settingsScreen and a hiScoresScreen class.
        Settings,
        Hiscores,
        SubmitScore
    }

    GameState state = GameState.Ready; // start here
    World world;

    public GameScreen(Game game)
    {
        super(game);
        world = new World();
        font = Assets.commodoreFont;
        allScores = new AllScores();
        allScores.execute();
        Difficulty.updateDifficulty();
    }

    @Override
    public void update(float deltaTime)
    {
        /*
         * Instead of modelling one single update method, we create an update for each state the game
         * is in.
         */

        List<Input.TouchEvent> touchEvents = game.getInput().getTouchEvents();
        float accel_x = game.getInput().getAccelX();
        float accel_y = game.getInput().getAccelY();
        float accel_z = game.getInput().getAccelZ();

        if (!Assets.BarMusic.isMusicPlaying()) { Assets.BarMusic.play(); }

        if (state == GameState.Ready)
        {
            updateReady(touchEvents, accel_x, accel_y, accel_z, deltaTime);
        }
        if (state == GameState.Running)
        {
            updateRunning(touchEvents, accel_x, accel_y, accel_z, deltaTime);
        }
        if (state == GameState.Paused)
        {
            updatePaused(touchEvents, accel_x, accel_y, accel_z);
        }
        if (state == GameState.GameOver)
        {
            updateGameOver(touchEvents, accel_x, accel_y, accel_z, deltaTime);
        }
        if (state == GameState.Settings)
        {
            updateSettings(touchEvents, accel_x, accel_y, accel_z, deltaTime);
        }
        if (state == GameState.Hiscores)
        {
            updateHiscores(touchEvents, accel_x, accel_y, accel_z, deltaTime);
        }
        if (state == GameState.SubmitScore)
        {
            updateSubmitScore(touchEvents, accel_x, accel_y, accel_z, deltaTime);
        }
    }

    public void updateReady(List<Input.TouchEvent> touchEvents, float accel_x, float accel_y, float accel_z, float deltaTime)
    {
        int len = touchEvents.size();
        Input.TouchEvent event = null;
        for (int i = 0; i < len; i++)
        {
            event = touchEvents.get(i);
            if (event.type == Input.TouchEvent.TOUCH_UP)
            {
                if (inBounds(event, 240 - 60, 160 - 20, 120, 40)) // enter the game: destroy all asteroids, make bar vulnerable...
                {
                    world.reset();
                    state = GameState.Running;
                    return;
                }
                else if (inBounds(event, 10, 260, 50, 50)) // settings
                {
                    state = GameState.Settings;
                }
                else if (inBounds(event, 420, 260, 50, 50)) // hiscores
                {
                    allScores.execute();
                    state = GameState.Hiscores;
                }
            }
        }

        world.update(deltaTime, accel_x, accel_y, accel_z, event, true); // keep bar invulnerable with "true" constant
    }

    public void updateRunning(List<Input.TouchEvent> touchEvents, float accel_x, float accel_y, float accel_z, float deltaTime)
    {
        Input.TouchEvent touchEvent = null;
        if (touchEvents.size() > 0)
        {
            touchEvent = touchEvents.get(0);
        }

        world.update(deltaTime, accel_x, accel_y, accel_z, touchEvent, false);

        if (world.gameOver)
        {
            if (world.score < mediumThreshold)
            {
                taunt = badScore[world.random.nextInt(badScore.length)];
            }
            else if (world.score < highThreshold)
            {
                taunt = mediumScore[world.random.nextInt(mediumScore.length)];
            }
            else
            {
                taunt = highScore[world.random.nextInt(highScore.length)];
            }

            timeLapsed = (int) world.time;
            squaresDestroyed = world.squaresDestroyed;
            score = (int) world.score;
            if (allScores.scoresList.isEmpty()) { allScores.execute(); }

            state = GameState.GameOver;
        }
    }

    public void updatePaused(List<Input.TouchEvent> touchEvents, float accel_x, float accel_y, float accel_z)
    {
        // If user touches the screen when the game is paused, resume it
        int len = touchEvents.size();
        for (int i = 0; i < len; i++)
        {
            Input.TouchEvent event = touchEvents.get(i);
            if (event.type == Input.TouchEvent.TOUCH_UP)
            {
                state = GameState.Running;
                return;
            }
        }
    }

    public void updateGameOver(List<Input.TouchEvent> touchEvents, float accel_x, float accel_y, float accel_z, float deltaTime)
    {
        if (score > Settings.personalHiScore)
        {
            Settings.personalHiScore = score;
            Settings.save(game.getFileIO());
        }

        int len = touchEvents.size();
        Input.TouchEvent event;
        for (int i = 0; i < len; i++)
        {
            event = touchEvents.get(i);
            if (event.type == Input.TouchEvent.TOUCH_UP)
            {
                if (inBounds(event, 170, 260, 150, 50))
                {
                    world.reset();
                    state = GameState.Running;
                    return;
                }
                else if (inBounds(event, 330, 260, 100, 50))
                {
                    world.reset();
                    state = GameState.Ready;
                    return;
                }
                else if (inBounds(event, 60, 260, 100, 50))
                {
                    world.reset();
                    submitName = "";
                    isScoreSubmitted = false;
                    state = GameState.SubmitScore;
                    return;
                }
            }
        }

        world.update(deltaTime, accel_x, accel_y, accel_z, null, false);
    }

    public void updateSettings(List<Input.TouchEvent> touchEvents, float accel_x, float accel_y, float accel_z, float deltaTime)
    {
        int len = touchEvents.size();
        Input.TouchEvent event;
        for (int i = 0; i < len; i++)
        {
            event = touchEvents.get(i);
            if (event.type == Input.TouchEvent.TOUCH_UP)
            {
                if (inBounds(event, 197, 81, 26, 26)) // - sensitivity
                {
                    if (Settings.sensitivity > 1)
                    {
                        --Settings.sensitivity;
                    }
                }
                else if (inBounds(event, 257, 81, 26, 26))
                {
                    if (Settings.sensitivity < 9)
                    {
                        ++Settings.sensitivity;
                    }
                }
                else if (inBounds(event, 37, 161, 26, 26))
                {
                    if (Settings.soundLevel > 0)
                    {
                        --Settings.soundLevel;
                    }
                }
                else if (inBounds(event, 97, 161, 26, 26))
                {
                    if (Settings.soundLevel < 9)
                    {
                        ++Settings.soundLevel;
                    }
                }
                else if (inBounds(event, 357, 161, 26, 26))
                {
                    if (Settings.musicLevel > 0)
                    {
                        --Settings.musicLevel;
                        Assets.BarMusic.refreshVolume();
                    }
                }
                else if (inBounds(event, 417, 161, 26, 26))
                {
                    if (Settings.musicLevel < 9)
                    {
                        ++Settings.musicLevel;
                        Assets.BarMusic.refreshVolume();
                    }
                }
                else if (inBounds(event, 200, 261, 80, 26))
                {
                    state = GameState.Ready;
                    Settings.save(game.getFileIO());
                    return;
                }
                else if (inBounds(event, 310,200, 90,30))
                {
                    Difficulty.difficulty_level = (Difficulty.difficulty_level+1) % 4;
                    Settings.difficultyLevel = Difficulty.difficulty_level;
                    Difficulty.updateDifficulty();
                }
            }
        }
        world.update(deltaTime, accel_x, accel_y, accel_z, null, true);
    }

    public void updateHiscores(List<Input.TouchEvent> touchEvents, float accel_x, float accel_y, float accel_z, float deltaTime)
    {
        int len = touchEvents.size();
        Input.TouchEvent event;
        for (int i = 0; i < len; i++)
        {
            event = touchEvents.get(i);
            if (event.type == Input.TouchEvent.TOUCH_UP)
            {
                if (inBounds(event, 25, 270, 135, 40))
                {
                    isScoreDisplayToday = true; // quite an internal state of Hiscores state
                }
                else if (inBounds(event, 175, 270, 135, 40))
                {
                    state = GameState.Ready;
                    return;
                }
                else if (inBounds(event, 325, 270, 135, 40))
                {
                    isScoreDisplayToday = false;
                }
            }
        }
        world.update(deltaTime, accel_x, accel_y, accel_z, null, true);
    }

    public void updateSubmitScore(List<Input.TouchEvent> touchEvents, float accel_x, float accel_y, float accel_z, float deltaTime)
    {
        int len = touchEvents.size();
        Input.TouchEvent event;
        if (!isScoreSubmitted)
        {
            boolean sh = !isShiftPressed;
            boolean cp = !isCapsPressed;
            int h = 30;
            int v = 45;
            int hs = 30;
            int vs = 90;

            for (int i = 0; i < len; i++)
            {
                event = touchEvents.get(i);
                if (event.type == Input.TouchEvent.TOUCH_UP)
                {
                    if (submitName.length() < 21)
                    {
                        if (inBounds(event, hs, vs, 20, 20))
                        {
                            submitName += sh ? '`' : '~';
                        }
                        else if (inBounds(event, hs + h, vs, 20, 20))
                        {
                            submitName += sh ? '1' : '!';
                        }
                        else if (inBounds(event, hs + h * 2, vs, 20, 20))
                        {
                            submitName += sh ? '2' : '@';
                        }
                        else if (inBounds(event, hs + h * 3, vs, 20, 20))
                        {
                            submitName += sh ? "3" : "#";
                        }
                        else if (inBounds(event, hs + h * 4, vs, 20, 20))
                        {
                            submitName += sh ? "4" : "$";
                        }
                        else if (inBounds(event, hs + h * 5, vs, 20, 20))
                        {
                            submitName += sh ? "5" : "%";
                        }
                        else if (inBounds(event, hs + h * 6, vs, 20, 20))
                        {
                            submitName += sh ? "6" : "^";
                        }
                        else if (inBounds(event, hs + h * 7, vs, 20, 20))
                        {
                            submitName += sh ? "7" : "&";
                        }
                        else if (inBounds(event, hs + h * 8, vs, 20, 20))
                        {
                            submitName += sh ? "8" : "*";
                        }
                        else if (inBounds(event, hs + h * 9, vs, 20, 20))
                        {
                            submitName += sh ? "9" : "(";
                        }
                        else if (inBounds(event, hs + h * 10, vs, 20, 20))
                        {
                            submitName += sh ? "0" : ")";
                        }
                        else if (inBounds(event, hs + h * 11, vs, 20, 20))
                        {
                            submitName += sh ? "-" : "_";
                        }
                        else if (inBounds(event, hs + h * 12, vs, 20, 20))
                        {
                            submitName += sh ? "=" : "+";
                        }
                        else if (inBounds(event, hs, vs + v, 20, 20))
                        {
                            submitName += !(cp ^ sh) ? "q" : "Q";
                        }
                        else if (inBounds(event, hs + h, vs + v, 20, 20))
                        {
                            submitName += !(cp ^ sh) ? "w" : "W";
                        }
                        else if (inBounds(event, hs + h * 2, vs + v, 20, 20))
                        {
                            submitName += !(cp ^ sh) ? "e" : "E";
                        }
                        else if (inBounds(event, hs + h * 3, vs + v, 20, 20))
                        {
                            submitName += !(cp ^ sh) ? "r" : "R";
                        }
                        else if (inBounds(event, hs + h * 4, vs + v, 20, 20))
                        {
                            submitName += !(cp ^ sh) ? "t" : "T";
                        }
                        else if (inBounds(event, hs + h * 5, vs + v, 20, 20))
                        {
                            submitName += !(cp ^ sh) ? "y" : "Y";
                        }
                        else if (inBounds(event, hs + h * 6, vs + v, 20, 20))
                        {
                            submitName += !(cp ^ sh) ? "u" : "U";
                        }
                        else if (inBounds(event, hs + h * 7, vs + v, 20, 20))
                        {
                            submitName += !(cp ^ sh) ? "i" : "I";
                        }
                        else if (inBounds(event, hs + h * 8, vs + v, 20, 20))
                        {
                            submitName += !(cp ^ sh) ? "o" : "O";
                        }
                        else if (inBounds(event, hs + h * 9, vs + v, 20, 20))
                        {
                            submitName += !(cp ^ sh) ? "p" : "P";
                        }
                        else if (inBounds(event, hs + h * 10, vs + v, 20, 20))
                        {
                            submitName += sh ? "[" : "{";
                        }
                        else if (inBounds(event, hs + h * 11, vs + v, 20, 20))
                        {
                            submitName += sh ? "]" : "}";
                        }
                        else if (inBounds(event, hs + h * 12, vs + v, 20, 20))
                        {
                            submitName += sh ? "\\" : "|";
                        }
                        else if (inBounds(event, hs, vs + v * 2, 20, 20))
                        {
                            submitName += !(cp ^ sh) ? "a" : "A";
                        }
                        else if (inBounds(event, hs + h, vs + v * 2, 20, 20))
                        {
                            submitName += !(cp ^ sh) ? "s" : "S";
                        }
                        else if (inBounds(event, hs + h * 2, vs + v * 2, 20, 20))
                        {
                            submitName += !(cp ^ sh) ? "d" : "D";
                        }
                        else if (inBounds(event, hs + h * 3, vs + v * 2, 20, 20))
                        {
                            submitName += !(cp ^ sh) ? "f" : "F";
                        }
                        else if (inBounds(event, hs + h * 4, vs + v * 2, 20, 20))
                        {
                            submitName += !(cp ^ sh) ? "g" : "G";
                        }
                        else if (inBounds(event, hs + h * 5, vs + v * 2, 20, 20))
                        {
                            submitName += !(cp ^ sh) ? "h" : "H";
                        }
                        else if (inBounds(event, hs + h * 6, vs + v * 2, 20, 20))
                        {
                            submitName += !(cp ^ sh) ? "j" : "J";
                        }
                        else if (inBounds(event, hs + h * 7, vs + v * 2, 20, 20))
                        {
                            submitName += !(cp ^ sh) ? "k" : "K";
                        }
                        else if (inBounds(event, hs + h * 8, vs + v * 2, 20, 20))
                        {
                            submitName += !(cp ^ sh) ? "l" : "L";
                        }
                        else if (inBounds(event, hs + h * 9, vs + v * 2, 20, 20))
                        {
                            submitName += sh ? ";" : ":";
                        }
                        else if (inBounds(event, hs + h * 10, vs + v * 2, 20, 20))
                        {
                            submitName += sh ? "'" : "\"";
                        }
                        else if (inBounds(event, hs, vs + v * 3, 20, 20))
                        {
                            submitName += !(cp ^ sh) ? "z" : "Z";
                        }
                        else if (inBounds(event, hs + h, vs + v * 3, 20, 20))
                        {
                            submitName += !(cp ^ sh) ? "x" : "X";
                        }
                        else if (inBounds(event, hs + h * 2, vs + v * 3, 20, 20))
                        {
                            submitName += !(cp ^ sh) ? "c" : "C";
                        }
                        else if (inBounds(event, hs + h * 3, vs + v * 3, 20, 20))
                        {
                            submitName += !(cp ^ sh) ? "v" : "V";
                        }
                        else if (inBounds(event, hs + h * 4, vs + v * 3, 20, 20))
                        {
                            submitName += !(cp ^ sh) ? "b" : "B";
                        }
                        else if (inBounds(event, hs + h * 5, vs + v * 3, 20, 20))
                        {
                            submitName += !(cp ^ sh) ? "n" : "N";
                        }
                        else if (inBounds(event, hs + h * 6, vs + v * 3, 20, 20))
                        {
                            submitName += !(cp ^ sh) ? "m" : "M";
                        }
                        else if (inBounds(event, hs + h * 7, vs + v * 3, 20, 20))
                        {
                            submitName += sh ? "," : "<";
                        }
                        else if (inBounds(event, hs + h * 8, vs + v * 3, 20, 20))
                        {
                            submitName += sh ? "." : ">";
                        }
                        else if (inBounds(event, hs + h * 9, vs + v * 3, 20, 20))
                        {
                            submitName += sh ? "/" : "?";
                        }
                    }

                    if (inBounds(event, hs + h * 12, vs + v * 2, 35, 20))
                    {
                        isShiftPressed = !isShiftPressed;
                    }
                    else if (inBounds(event, hs + h * 11, vs + v * 3, 35, 20))
                    {
                        isCapsPressed = !isCapsPressed;
                    }
                    else if (inBounds(event, hs + h * 13, vs + v * 3, 35, 20))
                    {
                        int l = submitName.length();
                        if (l == 0) {}
                        else if (l == 1)
                        {
                            submitName = "";
                        }
                        else
                        {
                            submitName = submitName.substring(0, submitName.length() - 1);
                        }
                    }
                    else if (inBounds(event, 163, 258, 156, 35))
                    {
                        isScoreSubmitted = true;
                        newScore = new NewScore(submitName, score);
                        allScores.execute();
                    }
                }
            }
        }
        else
        {
            for (int i = 0; i < len; i++)
            {
                event = touchEvents.get(i);
                if (inBounds(event, 175, 270, 135, 40)) // back
                {
                    state = GameState.Ready;
                    return;
                }

                if (allScores.success && allScores.finishedLoading)
                {
                    if (inBounds(event, 25, 270, 135, 40)) // today's scores
                    {
                        isScoreDisplayToday = true; // quite an internal state of Hiscores state
                    }
                    else if (inBounds(event, 325, 270, 135, 40)) // all time scores
                    {
                        isScoreDisplayToday = false;
                    }

                }

                if (!newScore.success && newScore.finishedSubmitting)
                {
                    if (inBounds(event, 175, 220, 135, 40)) // Retry
                    {
                        isScoreSubmitted = true;
                        newScore.execute(submitName, score);
                        allScores.execute();
                    }
                }
            }
        }

        world.update(deltaTime, accel_x, accel_y, accel_z, null, true);
    }

    @Override
    public void present(float deltaTime)
    {
        /*
         *  Similar to update logic: instead of modelling one single view method, we split it up
         *  into three methods for each game state: each of them has its own UI to be rendered.
         */
        Graphics g = game.getGraphics();
        g.clear(0x00000000);

        // Draw world first and UI later: it's superimposed
        drawWorld(world);
        if (state == GameState.Ready)
        {
            drawReadyUI(deltaTime);
        }
        if (state == GameState.Running)
        {
            drawRunningUI();
        }
        if (state == GameState.Paused)
        {
            drawPausedUI();
        }
        if (state == GameState.GameOver)
        {
            drawGameOverUI();
        }
        if (state == GameState.Settings)
        {
            drawSettingsUI();
        }
        if (state == GameState.Hiscores)
        {
            drawHiscoresUI();
        }
        if (state == GameState.SubmitScore)
        {
            drawSubmitScoreUI(deltaTime);
        }
    }

    private void drawWorld(World world)
    {
        Graphics g = game.getGraphics();
        Bar bar = world.bar;
        Asteroid[] asteroids = world.asteroids;
        Debris[] debriss = world.debriss;

        // RENDER BACKGROUND
        g.drawRect(0, 0, World.WORLD_WIDTH + 1, World.WORLD_HEIGHT + 1, world.color);

        // RENDER DEBRIS, STARS
        for (int i = 0; i < World.MAX_DEBRIS; i++)
        {
            if (debriss[i] == null)
            {
                continue;
            }
            Debris d = debriss[i];
            Point deb = d.getPos();
            int deb_width = d.width;
            int deb_height = d.height;
            int debx = deb.x - d.width / 2;
            int deby = deb.y - d.height / 2;
            if (d.rotation != 0)
            {
                g.drawRect(debx, deby, deb_width, deb_height, d.color, d.rotation);
            }
            else
            {
                g.drawRect(debx, deby, deb_width, deb_height, d.color);
            }
        }

        // RENDER BAR PLAYER
        if (bar != null)
        {
            int bar_offset_x = (int) (Math.cos(bar.rotation) * bar.width / 2);
            int bar_offset_y = (int) (Math.sin(bar.rotation) * bar.width / 2);

            int bar_start_x = bar.center.x - bar_offset_x;
            int bar_end_x = bar.center.x + bar_offset_x;
            int bar_start_y = bar.center.y - bar_offset_y;
            int bar_end_y = bar.center.y + bar_offset_y;
            g.drawLine(bar_start_x, bar_start_y, bar_end_x, bar_end_y, bar.color, bar.height);
        }

        // RENDER ASTEROIDS
        for (int i = 0; i < World.MAX_ASTEROIDS; i++)
        {
            if (asteroids[i] == null)
            {
                continue;
            }
            Asteroid a = asteroids[i];
            Point ast = a.getPos();
            int ast_width = a.width;
            int ast_height = a.height;
            int ax = ast.x - a.width / 2;
            int ay = ast.y - a.height / 2;
            g.drawRect(ax, ay, ast_width, ast_height, a.color, a.rotation);
            if (Debris.showRoute)
            {
                g.drawLine(a.getSrc().x, a.getSrc().y, a.getDst().x, a.getDst().y, 0x30FFFFFF);
            }
        }
    }

    private void drawReadyUI(float deltaTime)
    {
        hintMoveCounter += deltaTime;
        Graphics g = game.getGraphics();
        // TOP:
        // 1- game name
        // 2- moving text from right to left with hints (like: tap squares to destroy them)
        // BOTTOM:
        // 3- LEFT:
        // 3.1- Settings button
        // 4- RIGHT:
        // 4.1- Hi-scores button
        // 5- CENTER:
        // 5.1- flashing text: "Tap center to play"

        g.drawRect(10, 260, 50, 50, 0x772211ee); // 3.1
        g.drawPixmap(Assets.cog, 15, 265);
        g.drawRect(420, 260, 50, 50, 0x772211ee); // 4.1
        g.drawPixmap(Assets.trophy, 425, 265);

        if (!allScores.scoresList.isEmpty() && !allScores.scoresListToday.isEmpty())
        {
            if (world.time % 11 > 5)
            {
                if (world.time % 6 < 3)
                {
                    g.drawText(font, "TODAY #1 ", "CENTER", 14, 240, 275, 0xFFFFFF11);
                    g.drawText(font, allScores.scoresListToday.get(0)[0], "LEFT", 14, 120, 304, 0xFFFFDD11);
                    g.drawText(font, allScores.scoresListToday.get(0)[1], "RIGHT", 14, 360, 304, 0xFFFFDD11);
                }
                else
                {
                    g.drawText(font, "ALL TIME #1 ", "CENTER", 14, 240, 275, 0xFFFFFF11);
                    g.drawText(font, allScores.scoresList.get(0)[0], "LEFT", 14, 120, 304, 0xFFFFDD11);
                    g.drawText(font, allScores.scoresList.get(0)[1], "RIGHT", 14, 360, 304, 0xFFFFDD11);
                }
            }
            else if (world.time % 2 < 1) // 5.1
            {
                g.drawText(font, "TAP CENTER TO START", "CENTER", 16, 240, 260 + 4 + 50 / 2, 0xFFFFFFFF);
            }
        }
        else if (world.time % 2 < 1) // 5.1
        {
            g.drawText(font, "TAP CENTER TO START", "CENTER", 16, 240, 260 + 4 + 50 / 2, 0xFFFFFFFF);
        }


        // Apparently, rendering text BEFORE pixmaps and rects makes them weird.

        g.drawText(font, "bar!", "CENTER", 30, 240, 30, 0xFFFFFFFF); // 1


        String currentHint;
        if (world.time < 61)
        {
            currentHint = hints[hint_pos];
            g.drawText(font, currentHint, "LEFT", 16, hint_x, hint_y, 0xFFFFFFFF);
        }
        else
        {
            currentHint = silly[silly_pos];
            g.drawText(font, currentHint, "LEFT", 16, hint_x, hint_y, 0xFFFFFFFF);
        }
        if (hintMoveCounter > HINT_MOVE_TIME)
        {
            hintMoveCounter = hintMoveCounter % HINT_MOVE_TIME;
            hint_x = hint_x - 3;
        }
        if (hint_x < 0 - 200 - currentHint.length() * 10)
        {
            hint_x = 485;
            if (world.time < 61)
            {
                hint_pos = (hint_pos + 1) % hints.length;
            }
            else
            {
                silly_pos = world.random.nextInt(silly.length);
            }
        }
    }

    // No UI when playing!
    private void drawRunningUI()
    {}

    // No UI when paused either. Keep it simple!
    private void drawPausedUI()
    {}

    private void drawGameOverUI()
    {
        Graphics g = game.getGraphics();

        g.drawRect(60, 260, 100, 50, 0x772211ee);  // submit button
        g.drawRect(170, 260, 150, 50, 0x772211ee); // play again button
        g.drawRect(330, 260, 100, 50, 0x772211ee); // main menu button

        //                                             fontsize
        //                                                     x   y
        g.drawText(font, "GAME OVER", "CENTER", 40, 240, 40, 0xFFFFFFFF);

        g.drawText(font, "TIME LAPSED", "CENTER", 14, 90, 70, 0xFFFFFFFF);
        g.drawText(font, "SQUARES ZAPPED", "CENTER", 14, 270, 70, 0xFFFFFFFF);
        g.drawText(font, "SCORE", "CENTER", 14, 415, 70, 0xFFFFFFFF);

        g.drawText(font, timeLapsed + "", "CENTER", 18, 90, 100, 0xFFFFFFFF);
        g.drawText(font, squaresDestroyed + "", "CENTER", 18, 270, 100, 0xFFFFFFFF);
        g.drawText(font, score + "", "CENTER", 18, 415, 100, 0xFFFFFFFF);

        g.drawText(font, "PERSONAL BEST    " + Settings.personalHiScore + "", "CENTER", 18, 240, 130, 0xFFFFFFFF);


        g.drawText(font, taunt, "CENTER", 13, 240, 160, 0xFFFFFFFF);

        if (!allScores.scoresList.isEmpty() && !allScores.scoresListToday.isEmpty())
        {
            g.drawText(font, "Est. #: " + calculateScorePosition(false, true) + " (today), " + calculateScorePosition(false, false) + " (overall)", "CENTER", 14, 240, 230, 0xFFFFDD11);
        }


        if (!allScores.scoresList.isEmpty() && !allScores.scoresListToday.isEmpty())
        {
            g.drawText(font, "TODAY #1:", "RIGHT", 14, 150, 180, 0xFFFFDD11);
            g.drawText(font, allScores.scoresListToday.get(0)[0], "LEFT", 14, 160, 180, 0xFFFFDD11);
            g.drawText(font, allScores.scoresListToday.get(0)[1], "RIGHT", 14, 460, 180, 0xFFFFDD11);
            g.drawText(font, "ALL TIME #1:", "RIGHT", 14, 150, 200, 0xFF55DD88);
            g.drawText(font, allScores.scoresList.get(0)[0], "LEFT", 14, 160, 200, 0xFF55DD88);
            g.drawText(font, allScores.scoresList.get(0)[1], "RIGHT", 14, 460, 200, 0xFF55DD88);
        }

        g.drawText(font, "SUBMIT", "CENTER", 16, 110, 290, 0xFFFF4411);
        g.drawText(font, "PLAY AGAIN", "CENTER", 16, 245, 290, 0xFFFFFFFF);
        g.drawText(font, "BACK", "CENTER", 16, 380, 290, 0xFFFFFFFF);
    }

    public void drawSettingsUI()
    {
        Graphics g = game.getGraphics();

        //           x   y    w    h
        g.drawRect(197, 81, 26, 26, 0x772211ee); //  1
        g.drawRect(257, 81, 26, 26, 0x772211ee); //  3
        g.drawRect(37, 161, 26, 26, 0x772211ee); //  4
        g.drawRect(97, 161, 26, 26, 0x772211ee); //  6
        g.drawRect(357, 161, 26, 26, 0x772211ee); //  7
        g.drawRect(417, 161, 26, 26, 0x772211ee); //  9
        g.drawRect(200, 261, 80, 26, 0x772211ee); // 10

        //                                                fontsize
        //                                                       x    y
        g.drawText(font, "SETTINGS", "CENTER", 40, 240, 40, 0xFFFFFFFF);

        g.drawText(font, "SENSITIVITY", "CENTER", 20, 240, 70, 0xFFFFFFFF);
        g.drawText(font, "-", "CENTER", 20, 210, 100, 0xFFFFFFFF); //  1
        g.drawText(font, Settings.sensitivity + "", "CENTER", 20, 240, 100, 0xFFFFFFFF); //  2
        g.drawText(font, "+", "CENTER", 20, 270, 100, 0xFFFFFFFF); //  3
        g.drawText(font, "SOUND", "CENTER", 20, 80, 150, 0xFFFFFFFF);
        g.drawText(font, "-", "CENTER", 20, 50, 180, 0xFFFFFFFF); //  4
        g.drawText(font, Settings.soundLevel + "", "CENTER", 20, 80, 180, 0xFFFFFFFF); //  5
        g.drawText(font, "+", "CENTER", 20, 110, 180, 0xFFFFFFFF); //  6
        g.drawText(font, "MUSIC", "CENTER", 20, 400, 150, 0xFFFFFFFF);
        g.drawText(font, "-", "CENTER", 20, 370, 180, 0xFFFFFFFF); //  7
        g.drawText(font, Settings.musicLevel + "", "CENTER", 20, 400, 180, 0xFFFFFFFF); //  8
        g.drawText(font, "+", "CENTER", 20, 430, 180, 0xFFFFFFFF); //  9

        g.drawText(font, "DIFFICULTY", "LEFT", 20, 40, 230, 0xFFFFFFFF);
        String diffName = Difficulty.getDifficultyName();
        int diffColor = 0xFFFF9988;
        switch(diffName)
        {
            case "EASY":
                diffColor = 0xFFFF9988;
                break;
            case "NORMAL":
                diffColor = 0xFFBBFFBB;
                break;
            case "HARD":
                diffColor = 0xFFFF5555;
                break;
            case "IMPOSSIBLE":
                diffColor = 0xFFFF0000;
                break;
        }
        g.drawText(font, diffName, "CENTER", 20, 330, 230, diffColor);

        g.drawText(font, "BACK", "CENTER", 20, 240, 280, 0xFFFFFFFF); // 10
    }

    public void drawHiscoresUI()
    {
        Graphics g = game.getGraphics();
        int yPos = 50;

        //           x   y      w   h
        g.drawRect(25, 270, 135, 40, 0x772211ee);
        g.drawRect(175, 270, 135, 40, 0x772211ee);
        g.drawRect(325, 270, 135, 40, 0x772211ee);

        //                                                 fontsize
        //                                                         x    y
        g.drawText(font, "HISCORES", "CENTER", 35, 240, 30, 0xFFFFFFFF);
        if (allScores.success && !allScores.finishedLoading)
        {
            g.drawText(font, "fetching server data...", "CENTER", 20, 240, 120, 0xFFFFFFFF);
        }
        else if (!allScores.success && allScores.finishedLoading)
        {
            g.drawText(font, "ERROR: connection issues", "CENTER", 20, 240, 120, 0xFFFFFFFF);
        }
        else if (allScores.success)
        {
            ArrayList<String[]> scores = isScoreDisplayToday ? allScores.scoresListToday : allScores.scoresList;
            for (int i = 0; i < 10 && i < scores.size(); i++)
            {
                int color = 0xFFFFFFFF;
                if (i == 0)
                {
                    color = 0xFFDDAA11;
                }
                else if (i == 1)
                {
                    color = 0xFFAAAA99;
                }
                else if (i == 2)
                {
                    color = 0xFF994440;
                }
                g.drawText(font, i + 1 + ".", "RIGHT", 14, 65, yPos, color);
                g.drawText(font, scores.get(i)[0], "LEFT", 14, 70, yPos, color);
                g.drawText(font, scores.get(i)[1], "RIGHT", 14, 460, yPos, color);
                yPos += 20;
            }

            g.drawText(font, "your best score (" + Settings.personalHiScore + ") " + "is #" + calculateScorePosition(true), "CENTER", 14, 240, 265, 0xFFFFFFFF);
        }
        g.drawText(font, "TODAY", "CENTER", 20, 90, 295, isScoreDisplayToday ? 0xFFFF4400 : 0xFFFFFFFF);
        g.drawText(font, "BACK", "CENTER", 20, 243, 295, 0xFFFFFFFF);
        g.drawText(font, "ALL TIME", "CENTER", 20, 392, 295, isScoreDisplayToday ? 0xFFFFFFFF : 0xFFFF4400);
    }

    public void drawSubmitScoreUI(float deltaTime)
    {
        Graphics g = game.getGraphics();

        if (!isScoreSubmitted)
        {
            boolean sh = !isShiftPressed;
            boolean cp = !isCapsPressed;
            int h = 30;
            int v = 45;
            int hs = 40;
            int vs = 100;


            g.drawRect(163, 258, 156, 35, 0x772211ee); // SUBMIT

            if (world.time % 2 < 1)
            {
                g.drawText(font, submitName + "_", "CENTER", 32, 240, 30, 0xFFFFFFFF);
            }
            else
            {
                g.drawText(font, submitName + " ", "CENTER", 32, 240, 30, 0xFFFFFFFF);
            }

            // first row
            g.drawText(font, sh ? "`" : "˜", "CENTER", 30, hs, vs, 0xFFFFFFFF);
            g.drawText(font, sh ? "1" : "!", "CENTER", 30, hs + h, vs, 0xFFFFFFFF);
            g.drawText(font, sh ? "2" : "@", "CENTER", 30, hs + h * 2, vs, 0xFFFFFFFF);
            g.drawText(font, sh ? "3" : "#", "CENTER", 30, hs + h * 3, vs, 0xFFFFFFFF);
            g.drawText(font, sh ? "4" : "$", "CENTER", 30, hs + h * 4, vs, 0xFFFFFFFF);
            g.drawText(font, sh ? "5" : "%", "CENTER", 30, hs + h * 5, vs, 0xFFFFFFFF);
            g.drawText(font, sh ? "6" : "^", "CENTER", 30, hs + h * 6, vs, 0xFFFFFFFF);
            g.drawText(font, sh ? "7" : "&", "CENTER", 30, hs + h * 7, vs, 0xFFFFFFFF);
            g.drawText(font, sh ? "8" : "*", "CENTER", 30, hs + h * 8, vs, 0xFFFFFFFF);
            g.drawText(font, sh ? "9" : "(", "CENTER", 30, hs + h * 9, vs, 0xFFFFFFFF);
            g.drawText(font, sh ? "0" : ")", "CENTER", 30, hs + h * 10, vs, 0xFFFFFFFF);
            g.drawText(font, sh ? "-" : "_", "CENTER", 30, hs + h * 11, vs, 0xFFFFFFFF);
            g.drawText(font, sh ? "=" : "+", "CENTER", 30, hs + h * 12, vs, 0xFFFFFFFF);
            // second row
            g.drawText(font, !(cp ^ sh) ? "q" : "Q", "CENTER", 30, hs, vs + v, 0xFFFFFFFF);
            g.drawText(font, !(cp ^ sh) ? "w" : "W", "CENTER", 30, hs + h, vs + v, 0xFFFFFFFF);
            g.drawText(font, !(cp ^ sh) ? "e" : "E", "CENTER", 30, hs + h * 2, vs + v, 0xFFFFFFFF);
            g.drawText(font, !(cp ^ sh) ? "r" : "R", "CENTER", 30, hs + h * 3, vs + v, 0xFFFFFFFF);
            g.drawText(font, !(cp ^ sh) ? "t" : "T", "CENTER", 30, hs + h * 4, vs + v, 0xFFFFFFFF);
            g.drawText(font, !(cp ^ sh) ? "y" : "Y", "CENTER", 30, hs + h * 5, vs + v, 0xFFFFFFFF);
            g.drawText(font, !(cp ^ sh) ? "u" : "U", "CENTER", 30, hs + h * 6, vs + v, 0xFFFFFFFF);
            g.drawText(font, !(cp ^ sh) ? "i" : "I", "CENTER", 30, hs + h * 7, vs + v, 0xFFFFFFFF);
            g.drawText(font, !(cp ^ sh) ? "o" : "O", "CENTER", 30, hs + h * 8, vs + v, 0xFFFFFFFF);
            g.drawText(font, !(cp ^ sh) ? "p" : "P", "CENTER", 30, hs + h * 9, vs + v, 0xFFFFFFFF);
            g.drawText(font, sh ? "[" : "{", "CENTER", 30, hs + h * 10, vs + v, 0xFFFFFFFF);
            g.drawText(font, sh ? "]" : "}", "CENTER", 30, hs + h * 11, vs + v, 0xFFFFFFFF);
            g.drawText(font, sh ? "\\" : "|", "CENTER", 30, hs + h * 12, vs + v, 0xFFFFFFFF);
            // third row
            g.drawText(font, !(cp ^ sh) ? "a" : "A", "CENTER", 30, hs, vs + v * 2, 0xFFFFFFFF);
            g.drawText(font, !(cp ^ sh) ? "s" : "S", "CENTER", 30, hs + h, vs + v * 2, 0xFFFFFFFF);
            g.drawText(font, !(cp ^ sh) ? "d" : "D", "CENTER", 30, hs + h * 2, vs + v * 2, 0xFFFFFFFF);
            g.drawText(font, !(cp ^ sh) ? "f" : "F", "CENTER", 30, hs + h * 3, vs + v * 2, 0xFFFFFFFF);
            g.drawText(font, !(cp ^ sh) ? "g" : "G", "CENTER", 30, hs + h * 4, vs + v * 2, 0xFFFFFFFF);
            g.drawText(font, !(cp ^ sh) ? "h" : "H", "CENTER", 30, hs + h * 5, vs + v * 2, 0xFFFFFFFF);
            g.drawText(font, !(cp ^ sh) ? "j" : "J", "CENTER", 30, hs + h * 6, vs + v * 2, 0xFFFFFFFF);
            g.drawText(font, !(cp ^ sh) ? "k" : "K", "CENTER", 30, hs + h * 7, vs + v * 2, 0xFFFFFFFF);
            g.drawText(font, !(cp ^ sh) ? "l" : "L", "CENTER", 30, hs + h * 8, vs + v * 2, 0xFFFFFFFF);
            g.drawText(font, sh ? ";" : ":", "CENTER", 30, hs + h * 9, vs + v * 2, 0xFFFFFFFF);
            g.drawText(font, sh ? "'" : "\"", "CENTER", 30, hs + h * 10, vs + v * 2, 0xFFFFFFFF);
            g.drawText(font, "SHIFT", "CENTER", 16, hs + h * 12, vs + v * 2, sh ? 0xFFFFFFFF : 0xFFFF4444);
            // fourth row
            g.drawText(font, !(cp ^ sh) ? "z" : "Z", "CENTER", 30, hs, vs + v * 3, 0xFFFFFFFF);
            g.drawText(font, !(cp ^ sh) ? "x" : "X", "CENTER", 30, hs + h, vs + v * 3, 0xFFFFFFFF);
            g.drawText(font, !(cp ^ sh) ? "c" : "C", "CENTER", 30, hs + h * 2, vs + v * 3, 0xFFFFFFFF);
            g.drawText(font, !(cp ^ sh) ? "v" : "V", "CENTER", 30, hs + h * 3, vs + v * 3, 0xFFFFFFFF);
            g.drawText(font, !(cp ^ sh) ? "b" : "B", "CENTER", 30, hs + h * 4, vs + v * 3, 0xFFFFFFFF);
            g.drawText(font, !(cp ^ sh) ? "n" : "N", "CENTER", 30, hs + h * 5, vs + v * 3, 0xFFFFFFFF);
            g.drawText(font, !(cp ^ sh) ? "m" : "M", "CENTER", 30, hs + h * 6, vs + v * 3, 0xFFFFFFFF);
            g.drawText(font, sh ? "," : "<", "CENTER", 30, hs + h * 7, vs + v * 3, 0xFFFFFFFF);
            g.drawText(font, sh ? "." : ">", "CENTER", 30, hs + h * 8, vs + v * 3, 0xFFFFFFFF);
            g.drawText(font, sh ? "/" : "?", "CENTER", 30, hs + h * 9, vs + v * 3, 0xFFFFFFFF);
            g.drawText(font, "CAPS", "CENTER", 16, hs + h * 11, vs + v * 3, cp ? 0xFFFFFFFF : 0xFFFF4444);
            g.drawText(font, "BKSP", "CENTER", 16, hs + h * 13, vs + v * 3, 0xFFFFFFFF);

            g.drawText(font, "SUBMIT", "CENTER", 30, 240, vs + 5 + v * 4, 0xFFFFFFFF);
        }
        else
        {
            //           x   y      w   h
            g.drawRect(175, 270, 135, 40, 0x772211ee);

            g.drawText(font, "YOUR STANDING", "CENTER", 35, 240, 30, 0xFFFFFFFF);

            if (newScore.success && !newScore.finishedSubmitting)
            {
                g.drawText(font, "connecting to server...", "CENTER", 20, 240, 120, 0xFFFFFFFF);
            }
            else if (!newScore.success && newScore.finishedSubmitting)
            {
                g.drawText(font, "ERROR: connection issues", "CENTER", 20, 240, 120, 0xFFFFFFFF);
                g.drawRect(175, 220, 135, 40, 0x772211ee);
                g.drawText(font, "RETRY", "CENTER", 20, 243, 245, 0xFFFFFFFF);
            }
            else if (newScore.success)
            {
                if (allScores.success && !allScores.finishedLoading)
                {
                    g.drawText(font, "fetching server data...", "CENTER", 20, 240, 120, 0xFFFFFFFF);
                }
                else if (!allScores.success && allScores.finishedLoading)
                {
                    g.drawText(font, "ERROR: connection issues", "CENTER", 20, 240, 120, 0xFFFFFFFF);
                    g.drawRect(175, 220, 135, 40, 0x772211ee);
                    g.drawText(font, "RETRY", "CENTER", 20, 243, 245, 0xFFFFFFFF);
                }
                else if (allScores.success)
                {
                    int yPos = 70;
                    ArrayList<String[]> scores = isScoreDisplayToday ? allScores.scoresListToday : allScores.scoresList;
                    int ownPosition = calculateScorePosition(false);
                    int scoreLength = scores.size();
                    for (int i = ownPosition - 10; i < ownPosition + 9; i++)
                    {
                        if (i < 0 || i > scoreLength - 1)
                        {
                            continue;
                        }
                        int listScore = Integer.parseInt(isScoreDisplayToday ? allScores.scoresListToday.get(i)[1] : allScores.scoresList.get(i)[1]);
                        String listName = isScoreDisplayToday ? allScores.scoresListToday.get(i)[0] : allScores.scoresList.get(i)[0];
                        if (listScore != score || !listName.equals(submitName))
                        {
                            continue;
                        }
                        ownPosition = i;
                    }
                    for (int i = ownPosition - 5; i < ownPosition + 5; i++)
                    {
                        if (i < 0 || i > scoreLength - 1)
                        {
                            continue;
                        }
                        int color = 0xFFFFFFFF;
                        if (i == ownPosition)
                        {
                            color = 0xFFDDDD11;
                        }
                        if (i == ownPosition - 5 || i == ownPosition + 5)
                        {
                            color = 0x66FFFFFF;
                        }
                        if (i == ownPosition - 4 || i == ownPosition + 4)
                        {
                            color = 0x88FFFFFF;
                        }
                        if (i == ownPosition - 3 || i == ownPosition + 3)
                        {
                            color = 0xAAFFFFFF;
                        }
                        g.drawText(font, i + 1 + ".", "RIGHT", 14, 65, yPos, color);
                        g.drawText(font, scores.get(i)[0], "LEFT", 14, 70, yPos, color);
                        g.drawText(font, scores.get(i)[1], "RIGHT", 14, 460, yPos, color);

                        yPos += 20;
                    }
                }
                g.drawRect(25, 270, 135, 40, 0x772211ee);
                g.drawRect(325, 270, 135, 40, 0x772211ee);
                g.drawText(font, "TODAY", "CENTER", 20, 90, 295, isScoreDisplayToday ? 0xFFFF4400 : 0xFFFFFFFF);
                g.drawText(font, "ALL TIME", "CENTER", 20, 392, 295, isScoreDisplayToday ? 0xFFFFFFFF : 0xFFFF4400);
            }
            g.drawText(font, "BACK", "CENTER", 20, 243, 295, 0xFFFFFFFF);
        }
    }

    @Override
    public void pause()
    {
        if (state == GameState.Running)
        {
            //Assets.BarMusic.pause();
            state = GameState.Paused;
        }
    }

    @Override
    public void resume()
    {
        //Assets.BarMusic.resume();
    }

    @Override
    public void dispose()
    {
        Assets.BarMusic.stop();
    }

    public boolean inBounds(Input.TouchEvent event, int x, int y, int width, int height)
    {
        return (event.x > x && event.x < x + width - 1 &&
                event.y > y && event.y < y + height - 1);
    }

    public int calculateScorePosition(boolean personalHiScore, boolean today)
    {
        boolean oldToday = isScoreDisplayToday;

        isScoreDisplayToday = today;
        int result = calculateScorePosition(personalHiScore);

        isScoreDisplayToday = oldToday;
        return result;
    }


    public int calculateScorePosition(boolean personalHiScore)
    {
        int result = 1;
        int step = isScoreDisplayToday? allScores.scoresListToday.size()-1 : allScores.scoresList.size()-1;
        ArrayList<String[]> allScores = isScoreDisplayToday? this.allScores.scoresListToday : this.allScores.scoresList;
        int score = personalHiScore? Settings.personalHiScore : this.score;
        if (step == 0) {return 1;}
        for (int i = 0; step > 1; )
        {
            if (i < 0) { result = 1; break; }
            step /= 2;
            int listScore = Integer.parseInt(allScores.get(i)[1]);
            if (score < listScore)
            {
                i += step; // look further down the list
                result = i+1;
            }
            else if (score > listScore)
            {
                i -= step;
                result = i+1;
            }
            else { result = i; break; }
        }
        int prev_i = 0;
        for (int i = result; i < allScores.size();)
        {
            if (result == 0) { break;}
            int listScore = Integer.parseInt(allScores.get(i)[1]);
            if(listScore > score) {prev_i = i; ++i; result = i;}
            if(listScore < score && prev_i == i-1) {result = i; break;}
            else if(listScore < score) {--i; result = i;}
            else {result = i; break;}
        }
        return result+1;
    }
}
