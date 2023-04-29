package com.justsomeapp.bar.framework.impl;

import android.view.View;
import android.view.View.OnKeyListener;

import java.util.ArrayList;
import java.util.List;

import com.justsomeapp.bar.framework.Input;
import com.justsomeapp.bar.framework.Pool;

public class KeyboardHandler implements OnKeyListener
{
    boolean[] pressedKeys = new boolean[128];
    Pool<Input.KeyEvent> keyEventPool;
    List<Input.KeyEvent> keyEventsBuffer = new ArrayList<Input.KeyEvent>(); // KeyEvent instances not yet consumed
    List<Input.KeyEvent> keyEvents = new ArrayList<Input.KeyEvent>(); // KeyEvents that we return by calling getKeyEvents()

    public KeyboardHandler(View view) // the view from which we want to receive key events
    {
        Pool.PoolObjectFactory<Input.KeyEvent> factory = new Pool.PoolObjectFactory<Input.KeyEvent>()
        {
            public Input.KeyEvent createObject() { return new Input.KeyEvent(); }
        };
        keyEventPool = new Pool<Input.KeyEvent>(factory, 100);
        view.setOnKeyListener(this); // register this handler with the view as an OnKeyListener
        view.setFocusableInTouchMode(true);
        view.requestFocus();
    }

    // Called each time the View receives a new key event
    public boolean onKey(View v, int keyCode, android.view.KeyEvent event)
    {
        if (event.getAction() == android.view.KeyEvent.ACTION_MULTIPLE) { return false; }
        // The events are received on the UI thread and read on the main loop thread,
        // so we've to make sure that none of our member are accessed in parallel.
        synchronized (this)
        {
            Input.KeyEvent keyEvent = keyEventPool.newObject();
            keyEvent.keyCode = keyCode;
            keyEvent.keyChar = (char) event.getUnicodeChar();
            if (event.getAction() == android.view.KeyEvent.ACTION_DOWN)
            {
                keyEvent.type = Input.KeyEvent.KEY_DOWN;
                if (keyCode > 0 && keyCode < 127) { pressedKeys[keyCode] = true; }
            }
            if (event.getAction() == android.view.KeyEvent.ACTION_UP)
            {
                keyEvent.type = Input.KeyEvent.KEY_UP;
                if (keyCode > 0 && keyCode < 127) { pressedKeys[keyCode] = false; }
            }
            keyEventsBuffer.add(keyEvent);
        }
        return false;
    }

    // Returns whether the keyCode is pressed or not.
    public boolean isKeyPressed(int keyCode)
    {
        if (keyCode < 0 || keyCode > 127)
        {
            return false;
        }
        return pressedKeys[keyCode];
    }

    public List<Input.KeyEvent> getKeyEvents()
    {
        synchronized(this)
        {
            int len = keyEvents.size();
            for (int i = 0; i < len; i++)
            {
                keyEventPool.free(keyEvents.get(i));
            }
            keyEvents.clear();
            keyEvents.addAll(keyEventsBuffer);
            keyEventsBuffer.clear();
            return keyEvents;
        }
    }
}
