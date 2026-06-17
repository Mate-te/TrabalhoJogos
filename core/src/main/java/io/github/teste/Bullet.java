package io.github.teste;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.math.MathUtils;

public class Bullet extends GameEntity implements Pool.Poolable { // Changed to extend GameEntity

    private Sound som;
    private float angle;
    private final float speed = 500f;
    private float mapWidth = 0f;
    private float mapHeight = 0f;

    public Bullet(Texture texture) {
        super(texture);
        setAlive(false);
        setSize(64, 64);
        setOriginCenter();
    }

    public void init(float posX, float posY, float angleDeg) {
        setPosition(
            posX - getWidth() / 2f,
            posY - getHeight() / 2f
        );
        this.angle = angleDeg;
        setRotation(angleDeg);
        setAlive(true);
    }

    public void setMapBounds(float width, float height) {
        this.mapWidth = width;
        this.mapHeight = height;
    }

    @Override
    public void reset() {
        setPosition(0, 0);
        setRotation(0);
        this.angle = 0f;
        setAlive(false);
    }

    @Override
    public void update(float delta) {
        if (isOutOfBounds()) {
            setAlive(false);
        } else {
            translateX(MathUtils.cosDeg(angle) * speed * delta);
            translateY(MathUtils.sinDeg(angle) * speed * delta);
        }
    }

    private boolean isOutOfBounds() {
        float maxX = (mapWidth > 0f) ? mapWidth : Gdx.graphics.getWidth();
        float maxY = (mapHeight > 0f) ? mapHeight : Gdx.graphics.getHeight();
        return getX() > maxX + 100 || getX() < -100 ||
               getY() > maxY + 100 || getY() < -100;
    }

    public Sound getSom() {
        return som;
    }

    public void setSom(Sound som) {
        this.som = som;
    }
}
