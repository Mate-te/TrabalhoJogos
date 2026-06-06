package io.github.teste;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public abstract class GameEntity extends Sprite {
    protected boolean alive;

    public GameEntity(Texture texture) {
        super(texture);
        this.alive = true; // Entities are alive by default when created
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public abstract void update(float delta);
}
