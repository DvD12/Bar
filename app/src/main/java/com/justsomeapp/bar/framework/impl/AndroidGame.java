package com.justsomeapp.bar.framework.impl;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.justsomeapp.bar.R;
import com.justsomeapp.bar.Assets;
import com.justsomeapp.bar.framework.Audio;
import com.justsomeapp.bar.framework.FileIO;
import com.justsomeapp.bar.framework.Game;
import com.justsomeapp.bar.framework.Graphics;
import com.justsomeapp.bar.framework.Input;
import com.justsomeapp.bar.framework.Screen;

// getStartScreen() is the method you have to implement
public abstract class AndroidGame extends Activity implements Game
{
    AndroidFastRenderView renderView; // we'll draw here. This will also manage our main loop thread for us.
    Graphics graphics;
    Audio audio;
    Input input;
    FileIO fileIO;
    Screen screen; // Currently active screen.
    PowerManager.WakeLock wakeLock;

    public static final String PREFS_NAME = "MyPrefsFile";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // We design our UI according to a 320x480 device and act as all devices are this size.
        // The graphics class will then scale our assets accordingly to the real device size.
        boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        int frameBufferWidth = isLandscape ? 480 : 320;
        int frameBufferHeight = isLandscape ? 320 : 480;
        Bitmap frameBuffer = Bitmap.createBitmap(frameBufferWidth, frameBufferHeight, Bitmap.Config.RGB_565);

        float scaleX = (float) frameBufferWidth / getWindowManager().getDefaultDisplay().getWidth();
        float scaleY = (float) frameBufferHeight / getWindowManager().getDefaultDisplay().getHeight();

        renderView = new AndroidFastRenderView(this, frameBuffer);
        graphics = new AndroidGraphics(getAssets(), frameBuffer);
        fileIO = new AndroidFileIO(this);
        audio = new AndroidAudio(this);
        input = new AndroidInput(this, renderView, scaleX, scaleY);
        screen = getStartScreen();
        setContentView(renderView);

        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        //wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "GLGame");
    }

    @Override
    public void onResume()
    {
        super.onResume();
        //wakeLock.acquire();
        screen.resume();
        renderView.resume();
    }
    @Override
    public void onPause()
    {
        super.onPause();
        //wakeLock.release();
        renderView.pause();
        screen.pause();

        if (isFinishing()) { screen.dispose(); }
    }

    public Input getInput()
    {
        return input;
    }
    public FileIO getFileIO()
    {
        return fileIO;
    }
    public Graphics getGraphics()
    {
        return graphics;
    }
    public Audio getAudio()
    {
        return audio;
    }

    public void setScreen(Screen screen)
    {
        if (screen == null)
        {
            throw new IllegalArgumentException("Screen must not be null");
        }
        this.screen.pause();
        this.screen.dispose();
        screen.resume();
        screen.update(0);
        this.screen = screen;
    }
    public Screen getCurrentScreen()
    {
        return screen;
    }
}
