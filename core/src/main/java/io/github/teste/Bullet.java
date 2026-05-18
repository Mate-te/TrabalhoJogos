package io.github.teste;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Pool;

public class Bullet extends Sprite implements Pool.Poolable {

    private boolean alive;
    private Sound som;

    public Bullet(Texture texture) {
        super(texture);

        this.alive = false;

        // tamanho da bala
        setSize(32, 32);
    }

    public void init(float posX, float posY) {

        // centraliza a sprite
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

            // move no eixo X
            translateX(500 * delta);
        }
    }

    private boolean isOutOfScreen() {
        return getX() > Gdx.graphics.getWidth() + 100;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public Sound getSom() {
        return som;
    }

    public void setSom(Sound som) {
        this.som = som;
    }
}
