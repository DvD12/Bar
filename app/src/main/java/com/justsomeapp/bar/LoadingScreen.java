package com.justsomeapp.bar;

import java.io.IOException;

import com.justsomeapp.bar.framework.Game;
import com.justsomeapp.bar.framework.Graphics;
import com.justsomeapp.bar.framework.Screen;

public class LoadingScreen extends Screen
{
    public static final int MAX_SENSITIVITY = 9;
    public static final int MIN_SENSITIVITY = 1;
    public static final int MAX_SOUND = 9;
    public static final int MIN_SOUND = 0;
    public static final int MAX_MUSIC = 9;
    public static final int MIN_MUSIC = 0;

    public LoadingScreen(Game game)
    {
        super(game);
    }
    public void update(float deltaTime)
    {
        Graphics g = game.getGraphics();
        Assets.mainMenu = g.newPixmap("mainmenu.png", Graphics.PixmapFormat.RGB565);
        Assets.cog = g.newPixmap("cog.png", Graphics.PixmapFormat.RGB565);
        Assets.trophy = g.newPixmap("trophy.png", Graphics.PixmapFormat.RGB565);
        Assets.bar_track_01 = game.getAudio().newMusic("3025.ogg");
        Assets.bar_track_02 = game.getAudio().newMusic("Deep Horizons.ogg");
        Assets.bar_expl_01 = game.getAudio().newSound("bar_expl_01.ogg");
        Assets.bar_expl_02 = game.getAudio().newSound("bar_expl_02.ogg");
        Assets.bar_expl_03 = game.getAudio().newSound("bar_expl_03.ogg");
        Assets.bar_expl_04 = game.getAudio().newSound("bar_expl_04.ogg");
        Assets.bar_expl_05 = game.getAudio().newSound("bar_expl_05.ogg");
        Assets.bar_expl_06 = game.getAudio().newSound("bar_expl_06.ogg");
        Assets.bar_expl_07 = game.getAudio().newSound("bar_expl_07.ogg");
        Assets.bonus_01 = game.getAudio().newSound("bonus_01.ogg");
        Assets.expl_01 = game.getAudio().newSound("expl_01.ogg");
        Assets.expl_02 = game.getAudio().newSound("expl_02.ogg");
        Assets.expl_03 = game.getAudio().newSound("expl_03.ogg");
        Assets.expl_04 = game.getAudio().newSound("expl_04.ogg");
        Assets.expl_05 = game.getAudio().newSound("expl_05.ogg");
        Assets.expl_06 = game.getAudio().newSound("expl_06.ogg");
        Assets.expl_07 = game.getAudio().newSound("expl_07.ogg");
        Assets.expl_08 = game.getAudio().newSound("expl_08.ogg");
        Assets.expl_09 = game.getAudio().newSound("expl_09.ogg");
        Assets.expl_10 = game.getAudio().newSound("expl_10.ogg");
        Assets.expl_11 = game.getAudio().newSound("expl_11.ogg");
        Assets.expl_12 = game.getAudio().newSound("expl_12.ogg");
        Assets.expl_13 = game.getAudio().newSound("expl_13.ogg");
        Assets.malus_01 = game.getAudio().newSound("malus_01.ogg");
        try
        {
            Assets.commodoreFont = game.getFileIO().readFont("commodore");
            Assets.lucidaFont = game.getFileIO().readFont("lucida");
        }
        catch(IOException e)
        {
            System.out.println("File not found!");
        }
        Settings.load(game.getFileIO());
        if (Settings.sensitivity > MAX_SENSITIVITY) {Settings.sensitivity = MAX_SENSITIVITY;}
        else if (Settings.sensitivity < MIN_SENSITIVITY) {Settings.sensitivity = MIN_SENSITIVITY;}
        if (Settings.soundLevel > MAX_SOUND) {Settings.soundLevel = MAX_SOUND;}
        else if (Settings.soundLevel < MIN_SOUND) {Settings.soundLevel = MIN_SOUND;}
        if (Settings.musicLevel > MAX_MUSIC) {Settings.musicLevel = MAX_MUSIC;}
        else if (Settings.musicLevel < MIN_MUSIC) {Settings.musicLevel = MIN_MUSIC;}

        game.setScreen(new GameScreen(game));
    }
    public void present(float deltaTime)
    {}
    public void pause() {}
    public void resume() {}
    public void dispose() {}
}
