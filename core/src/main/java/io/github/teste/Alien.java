package io.github.teste;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Pool;

public class Alien extends GameEntity implements Pool.Poolable {

    private float mapWidth = 0f;
    private float mapHeight = 0f;

    // Animação
    private TextureRegion[] frames;
    private float animationTimer = 0f;
    private final float frameDuration = 0.15f; // Tempo por frame em segundos
    private int currentFrameIndex = 0;

    public Alien(Texture texture) {
        super(texture);
        setAlive(false);
        setSize(64, 64);

        // Inicializa os frames da animação (3 frames horizontais)
        frames = new TextureRegion[3];
        int frameWidth = texture.getWidth() / 3;
        int frameHeight = texture.getHeight();

        for (int i = 0; i < 3; i++) {
            frames[i] = new TextureRegion(texture, i * frameWidth, 0, frameWidth, frameHeight);
        }
    }

    public void init(float posX, float posY) {
        setPosition(
            posX - getWidth() / 2f,
            posY - getHeight() / 2f
        );
        setAlive(true);
        animationTimer = 0f;
        currentFrameIndex = 0;
    }

    public void setMapBounds(float width, float height) {
        this.mapWidth = width;
        this.mapHeight = height;
    }

    @Override
    public void reset() {
        setPosition(0, 0);
        setAlive(false);
        animationTimer = 0f;
        currentFrameIndex = 0;
    }

    @Override
    public void update(float delta) {
        if (isOutOfBounds()) {
            setAlive(false);
        } else {
            translateX(-400 * delta);

            // Atualiza a animação
            animationTimer += delta;
            if (animationTimer >= frameDuration) {
                animationTimer -= frameDuration;
                currentFrameIndex = (currentFrameIndex + 1) % 3;
            }
        }
    }

    public void draw(SpriteBatch batch) {
        if (isAlive()) {
            batch.draw(frames[currentFrameIndex],
                getX(), getY(),
                getOriginX(), getOriginY(),
                getWidth(), getHeight(),
                getScaleX(), getScaleY(),
                getRotation());
        }
    }

    private boolean isOutOfBounds() {
        float maxX = (mapWidth > 0f) ? mapWidth : Gdx.graphics.getWidth();
        return getX() < -100 && getX() > maxX + 100;
    }
}
