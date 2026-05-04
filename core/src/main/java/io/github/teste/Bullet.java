package io.github.teste;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

public class Bullet implements Pool.Poolable{


    private Vector2 position;
    private boolean alive;
    private Sound Som;

    public Bullet() {
        this.position = new Vector2();
        this.alive = false;
    }
    public void init(float posX, float posY) {
        position.set(posX, posY);
        alive = true;
    }

    @Override
    public void reset() {
        position.set(0,0);
        alive = false;
    }
    public void update (float delta) {
        if (isOutOfScreen()) {
            alive = false;
        } else {
            position.add(500*delta, 0);
        }
    }

    private boolean isOutOfScreen() {
        return position.x > com.badlogic.gdx.Gdx.graphics.getWidth() + 100;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public Vector2 getPosition() {
        return position;
    }
}
