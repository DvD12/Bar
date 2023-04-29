package com.justsomeapp.bar;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import com.justsomeapp.bar.framework.FileIO;

public class Settings
{
    public static int sensitivity = 4;
    public static int soundLevel = 5;
    public static int musicLevel = 1;
    public static int personalHiScore = 0;
    public static int difficultyLevel = 1;

    public static final int MAX_SOUND_LEVEL = 9;
    public static final int MAX_MUSIC_LEVEL = 9;

    public static void load(FileIO files)
    {
        BufferedReader in = null;
        try
        {
            in = new BufferedReader(new InputStreamReader(files.readFile(".bar")));
            sensitivity = Integer.parseInt(in.readLine());
            soundLevel = Integer.parseInt(in.readLine());
            musicLevel = Integer.parseInt(in.readLine());
            personalHiScore = Integer.parseInt(in.readLine());
            difficultyLevel = Integer.parseInt(in.readLine());
        }
        catch (IOException e)
        {}
        catch (NumberFormatException e)
        {}
        finally
        {

            try
            {
                if (in != null) { in.close(); }
            }
            catch(IOException e) {}
        }
    }

    public static void save(FileIO files)
    {
        BufferedWriter out = null;
        try
        {
            out = new BufferedWriter(new OutputStreamWriter(files.writeFile(".bar")));
            out.write(Integer.toString(sensitivity) + "\n");
            out.write(Integer.toString(soundLevel) + "\n");
            out.write(Integer.toString(musicLevel) + "\n");
            out.write(Integer.toString(personalHiScore) + "\n");
            out.write(Integer.toString(difficultyLevel) + "\n");
        }
        catch(IOException e)
        {Log.d("Debug", e.toString());}
        finally
        {
            try
            {
                if (out != null) { out.close(); }
            }
            catch(IOException e) {}
        }
    }
}
