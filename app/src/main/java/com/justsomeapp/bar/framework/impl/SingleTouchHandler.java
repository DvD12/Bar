package com.justsomeapp.bar.framework.impl;

import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import com.justsomeapp.bar.framework.Input;
import com.justsomeapp.bar.framework.Pool;

public class SingleTouchHandler implements TouchHandler
{
    boolean isTouched;
    int touchX;
    int touchY;
    Pool<Input.TouchEvent> touchEventPool;
    List<Input.TouchEvent> touchEvents = new ArrayList<Input.TouchEvent>();
    List<Input.TouchEvent> touchEventsBuffer = new ArrayList<Input.TouchEvent>();
    float scaleX;
    float scaleY;

    public SingleTouchHandler(View view, float scaleX, float scaleY)
    {
        Pool.PoolObjectFactory<Input.TouchEvent> factory = new Pool.PoolObjectFactory<Input.TouchEvent>()
        {
            @Override
            public Input.TouchEvent createObject()
            {
                return new Input.TouchEvent();
            }
        };
        touchEventPool = new Pool<Input.TouchEvent>(factory, 100);
        view.setOnTouchListener(this);

        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }

    public boolean onTouch(View v, MotionEvent event)
    {
        synchronized(this)
        {
            Input.TouchEvent touchEvent = touchEventPool.newObject();
            switch (event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    touchEvent.type = Input.TouchEvent.TOUCH_DOWN;
                    isTouched = true;
                    break;
                case MotionEvent.ACTION_MOVE:
                    touchEvent.type = Input.TouchEvent.TOUCH_DRAGGED;
                    isTouched = true;
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    touchEvent.type = Input.TouchEvent.TOUCH_UP;
                    isTouched = false;
                    break;
            }
            touchEvent.x = touchX = (int)(event.getX() * scaleX);
            touchEvent.y = touchY = (int)(event.getY() * scaleY);

            return true;
        }
    }

    public boolean isTouchDown(int pointer)
    {
        synchronized(this)
        {
            if (pointer == 0) { return isTouched; }
            else { return false; }
        }
    }
    public int getTouchX(int pointer)
    {
        synchronized (this)
        {
            return touchX;
        }
    }
    public int getTouchY(int pointer)
    {
        synchronized (this)
        {
            return touchY;
        }
    }

    public List<Input.TouchEvent> getTouchEvents()
    {
        synchronized(this)
        {
            int len = touchEvents.size();
            for (int i = 0; i < len; i++)
            {
                touchEventPool.free(touchEvents.get(i));
            }
            touchEvents.clear();
            touchEvents.addAll(touchEventsBuffer);
            touchEventsBuffer.clear();
            return touchEvents;
        }
    }
}
