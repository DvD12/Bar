package com.justsomeapp.bar.framework;

import java.util.ArrayList;
import java.util.List;

/*
 * The garbage collector is VERY time consuming. We must therefore avoid creating too many instances of stuff.
 * For example, in our keyobard and touch event handlers we'll create tons of new List<KeyEvent> and List<TouchEvent>
 * instances via Input's getKeyEvents() and getTouchEvents() which will be collected by the garbage collector
 * in short intervals.
 * Therefore, we implement instance pooling: instead of repeatedly creating new instances of a class, we just reuse
 * previously created instances.
 *
 * A typical use case could be:
 *   PoolObjectFactory<TouchEvent> factory = new PoolObjectFactory<TouchEvent>()
 *   {
 *     @Override
 *     public TouchEvent createObject() { return new TouchEvent(); }
 *   };
 *   Pool<TouchEvent> touchEventPool = new Pool<TouchEvent>(factory, 50);
 *   // creates a new object if pool is empty or pops one from pool in order to reuse that one
 *   TouchEvent touchEvent = touchEventPool.newObject();
 *   ... do something with touchEvent...
 *   // we don't need touchEvent anymore: store it in the pool (if it's not full)
 *  touchEventPool.free(touchEvent);
 */
public class Pool<T>
{
    public interface PoolObjectFactory<T>
    {
        public T createObject();
    }
    private final List<T> freeObjects;
    private final PoolObjectFactory<T> factory;
    private final int maxSize;

    public Pool(PoolObjectFactory<T> factory, int maxSize)
    {
        this.factory = factory;
        this.maxSize = maxSize;
        this.freeObjects = new ArrayList<T>(maxSize);
    }
    public T newObject()
    {
        T object = null;
        if (freeObjects.isEmpty())
        {
            object = factory.createObject();
        }
        else
        {
            object = freeObjects.remove(freeObjects.size() - 1);
        }
        return object;
    }
    public void free(T object)
    {
        if (freeObjects.size() < maxSize) { freeObjects.add(object); }
    }
}
