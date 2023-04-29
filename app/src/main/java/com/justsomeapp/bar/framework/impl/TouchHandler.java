package com.justsomeapp.bar.framework.impl;

import android.view.View;
import java.util.List;
import com.justsomeapp.bar.framework.Input;

// Registers the handler with a View
public interface TouchHandler extends View.OnTouchListener
{
    public boolean isTouchDown(int pointer);
    public int getTouchX(int pointer);
    public int getTouchY(int pointer);
    public List<Input.TouchEvent> getTouchEvents();
}
