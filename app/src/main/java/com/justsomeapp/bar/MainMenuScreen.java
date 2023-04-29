package com.justsomeapp.bar;

import java.util.List;

import com.justsomeapp.bar.framework.Game;
import com.justsomeapp.bar.framework.Graphics;
import com.justsomeapp.bar.framework.Input;
import com.justsomeapp.bar.framework.Screen;

// NOTE: OBSOLETED BY GAMESCREEN
public class MainMenuScreen extends Screen
{
    private int changingColor = 0xffffff05;
    private int startGameColor = 0xffff0000;
    private boolean direction = false;
    public MainMenuScreen(Game game)
    {
        super(game);
    }

    public void update(float deltaTime)
    {
        List<Input.TouchEvent> touchEvents = game.getInput().getTouchEvents();
        int len = touchEvents.size();

        Graphics g = game.getGraphics();
        for (int i = 0; i < len; i++)
        {
            Input.TouchEvent event = touchEvents.get(i);
            if (event.type == Input.TouchEvent.TOUCH_UP)
            {
                if(inBounds(event, g.getWidth()/2 - 60, g.getHeight()/2 - 20, 120, 40))
                {
                    game.setScreen(new GameScreen(game));
                    return;
                }
                // TODO: settings and hi-scores screens
            }
        }
    }

    public boolean inBounds(Input.TouchEvent event, int x, int y, int width, int height)
    {
        if (event.x > x && event.x < x + width - 1 &&
                event.y > y && event.y < y + height - 1)
        { return true;}
        else { return false; }
    }

    public void present(float deltaTime)
    {
        Graphics g = game.getGraphics();
        g.clear(0x00000000);
        g.drawRect(0, 0, g.getWidth(), g.getHeight(), changingColor);
        g.drawRect(g.getWidth()/2 - 60, g.getHeight()/2 - 20, 120, 40, startGameColor);
        if (!direction) { ++changingColor; }
        else { --changingColor; }
        if (changingColor >= 0xfffffffa) { direction = true; }
        else if (changingColor <= 0xffffff03) { direction = false; }
        // TODO: draw rects relative to hi score and settings buttons
    }
    public void pause()
    {
        Settings.save(game.getFileIO());
    }
    public void resume() {}
    public void dispose() {}
}
