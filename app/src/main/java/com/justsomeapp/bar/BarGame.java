package com.justsomeapp.bar;

import com.justsomeapp.bar.framework.Screen;
import com.justsomeapp.bar.framework.impl.AndroidGame;

public class BarGame extends AndroidGame
{
    public Screen getStartScreen()
    {
        return new LoadingScreen(this);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        Assets.BarMusic.pause();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Assets.BarMusic.resume();
    }
}
