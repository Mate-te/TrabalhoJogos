package io.github.teste;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Pool;

public class Alien extends GameEntity implements Pool.Poolable { // Changed to extend GameEntity

    public Alien(Texture texture) {
        super(texture);
        setAlive(false);
        setSize(64, 64);
    }

    public void init(float posX, float posY) {

        // centraliza o sprite
        setPosition(
            posX - getWidth() / 2f,
            posY - getHeight() / 2f
        );

        setAlive(true); // Use setAlive from GameEntity
    }

    @Override
    public void reset() {

        setPosition(0, 0);

        setAlive(false); // Use setAlive from GameEntity
    }

    @Override // Mark as override since it's now in GameEntity
    public void update(float delta) {

        if (isOutOfScreen()) {

            setAlive(false); // Use setAlive from GameEntity

        } else {

            // move para esquerda
            translateX(-400 * delta);
        }
    }

    private boolean isOutOfScreen() {

        return getX() < -100;
    }

    // isAlive() and setAlive() methods removed as they are inherited from GameEntity
}
