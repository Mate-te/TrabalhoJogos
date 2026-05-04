package io.github.teste;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

public class Alien implements Pool.Poolable {
    private Vector2 position;
    private boolean alive;

    public Alien() {
        this.position = new Vector2();
        this.alive = false;
    }

    public void init(float posX, float posY) {
        position.set(posX, posY);
        alive = true;
    }

    @Override
    public void reset() {
        position.set(0, 0);
        alive = false;
    }

    public void update(float delta) {
        if (isOutOfScreen()) {
            alive = false;
        } else {
            // Move para a esquerda
            position.add(-400 * delta, 0);
        }
    }

    private boolean isOutOfScreen() {
        return position.x < -100; // Saiu da tela pela esquerda
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
