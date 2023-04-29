package com.justsomeapp.bar;

import android.graphics.Typeface;
import android.os.AsyncTask;

import java.util.Random;

import com.justsomeapp.bar.framework.Music;
import com.justsomeapp.bar.framework.Pixmap;
import com.justsomeapp.bar.framework.Sound;

public class Assets
{
    public static Random random = new Random();

    public static Pixmap mainMenu;
    public static Pixmap cog;
    public static Pixmap trophy;

    public static Typeface commodoreFont;
    public static Typeface lucidaFont;

    public static Music bar_track_01 = null;
    public static Music bar_track_02 = null;

    public static Sound bar_expl_01;
    public static Sound bar_expl_02;
    public static Sound bar_expl_03;
    public static Sound bar_expl_04;
    public static Sound bar_expl_05;
    public static Sound bar_expl_06;
    public static Sound bar_expl_07;

    public static Sound bonus_01;

    public static Sound expl_01;
    public static Sound expl_02;
    public static Sound expl_03;
    public static Sound expl_04;
    public static Sound expl_05;
    public static Sound expl_06;
    public static Sound expl_07;
    public static Sound expl_08;
    public static Sound expl_09;
    public static Sound expl_10;
    public static Sound expl_11;
    public static Sound expl_12;
    public static Sound expl_13;

    public static Sound malus_01;

    public static class BarMusic
    {
        static final int NUMBER_OF_TRACKS = 2;
        static int select = random.nextInt(NUMBER_OF_TRACKS);
        static Music selected = null;
        public static void resume()
        {
            if (selected != null) { selected.play(); }
        }
        public static void pause()
        {
            selected.pause();
        }
        public static void stop()
        {
            selected.stop();
        }
        public static void refreshVolume()
        {
            float volume = ((float)Settings.musicLevel/(float)Settings.MAX_MUSIC_LEVEL);
            selected.setVolume(volume);
        }
        public static void play()
        {
            select = random.nextInt(NUMBER_OF_TRACKS);
            float volume = ((float)Settings.musicLevel/(float)Settings.MAX_MUSIC_LEVEL);

            switch (select)
            {
                case 0: selected = bar_track_01; break;
                case 1: selected = bar_track_02; break;
            }
            selected.setVolume(volume);
            selected.play();
        }

        public static boolean isMusicPlaying()
        {
            if (selected != null) { return (selected.isPlaying()); }
            else return false;
        }
    }

    public static class BarExpl
    {
        public static void play()
        {
            float volume =  ((float)Settings.soundLevel/(float)Settings.MAX_SOUND_LEVEL);
            int select = random.nextInt(6);

            switch (select)
            {
                case 0: bar_expl_01.play(volume); break;
                case 1: bar_expl_02.play(volume); break;
                case 2: bar_expl_03.play(volume); break;
                case 3: bar_expl_04.play(volume); break;
                case 4: bar_expl_05.play(volume); break;
                case 5: bar_expl_06.play(volume); break;
                case 6: bar_expl_07.play(volume); break;
            }
        }
    }


    public static class Bonus
    {
        public static void play()
        {
            float volume =  ((float)Settings.soundLevel/(float)Settings.MAX_SOUND_LEVEL);
            int select = random.nextInt(1);

            switch (select)
            {
                case 0: bonus_01.play(volume); break;
            }
        }
    }

    public static class Malus
    {
        public static void play()
        {
            float volume =  ((float)Settings.soundLevel/(float)Settings.MAX_SOUND_LEVEL);
            int select = random.nextInt(1);

            switch (select)
            {
                case 0: malus_01.play(volume); break;
            }
        }
    }

    public static class Expl
    {
        public static void play()
        {
            float volume =  ((float)Settings.soundLevel/(float)Settings.MAX_SOUND_LEVEL);
            int select = random.nextInt(12);

            switch (select)
            {
                case  0: expl_01.play(volume); break;
                case  1: expl_02.play(volume); break;
                case  2: expl_03.play(volume); break;
                case  3: expl_04.play(volume); break;
                case  4: expl_05.play(volume); break;
                case  5: expl_06.play(volume); break;
                case  6: expl_07.play(volume); break;
                case  7: expl_08.play(volume); break;
                case  8: expl_09.play(volume); break;
                case  9: expl_10.play(volume); break;
                case 10: expl_11.play(volume); break;
                case 11: expl_12.play(volume); break;
                case 12: expl_13.play(volume); break;
            }
        }
    }
}
