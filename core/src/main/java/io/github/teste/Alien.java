package io.github.teste;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Pool;

public class Alien extends GameEntity implements Pool.Poolable {

    private float mapWidth = 0f;
    private float mapHeight = 0f;

    public Alien(Texture texture) {
        super(texture);
        setAlive(false);
        setSize(64, 64);
    }

    public void init(float posX, float posY) {
        setPosition(
            posX - getWidth() / 2f,
            posY - getHeight() / 2f
        );
        setAlive(true);
    }

    public void setMapBounds(float width, float height) {
        this.mapWidth = width;
        this.mapHeight = height;
    }

    @Override
    public void reset() {
        setPosition(0, 0);
        setAlive(false);
    }

    @Override
    public void update(float delta) {
        if (isOutOfBounds()) {
            setAlive(false);
        } else {
            translateX(-400 * delta);
        }
    }

    private boolean isOutOfBounds() {
        float maxX = (mapWidth > 0f) ? mapWidth : Gdx.graphics.getWidth();
        return getX() < -100 && getX() > maxX + 100;
    }
}
