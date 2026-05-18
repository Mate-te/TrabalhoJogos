package io.github.teste;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Pool;

public class Alien extends Sprite implements Pool.Poolable {

    private boolean alive;

    public Alien(Texture texture) {
        super(texture);
        this.alive = false;
        setSize(64, 64);
    }

    public void init(float posX, float posY) {

        // centraliza o sprite
        setPosition(
            posX - getWidth() / 2f,
            posY - getHeight() / 2f
        );

        alive = true;
    }

    @Override
    public void reset() {

        setPosition(0, 0);

        alive = false;
    }

    public void update(float delta) {

        if (isOutOfScreen()) {

            alive = false;

        } else {

            // move para esquerda
            translateX(-400 * delta);
        }
    }

    private boolean isOutOfScreen() {

        return getX() < -100;
    }

    public boolean isAlive() {

        return alive;
    }

    public void setAlive(boolean alive) {

        this.alive = alive;
    }
}
