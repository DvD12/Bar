package com.justsomeapp.bar.framework;

public abstract class Screen
{
    protected final Game game;
    public Screen(Game game)
    {
        this.game = game;
    }
    public abstract void update(float deltaTime); // Controller part in MVC: fetch input and modify the state of the world accordingly
    public abstract void present(float deltaTime); // View part in MVC: present the world according to the (possibly mutated) world state
    public abstract void pause();
    public abstract void resume();
    public abstract void dispose();
}
