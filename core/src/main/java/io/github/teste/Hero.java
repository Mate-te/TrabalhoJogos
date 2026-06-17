package io.github.teste;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Hero extends GameEntity { // Changed to extend GameEntity

    private Sound deathSound;
    private HeroInputManager inputManager;
    private float velocityX = 0f;
    private float velocityY = 0f;
    private final float moveSpeed = 200f;

    private int lives = 5;
    private float lastHitTime = 0f;
    private final float hitCooldown = 1f; // 1 segundo entre danos

    // World bounds (em pixels). Se 0, usa as dimensões da tela como fallback.
    private float worldWidth = 0f;
    private float worldHeight = 0f;

    public Hero(Texture texture, Sound deathSound) {
        super(texture); // Call to GameEntity constructor
        this.deathSound = deathSound;
        this.lives = 5;
        setSize(64, 64);
    }

    public void init(float posX, float posY) {
        setPosition(
            posX - getWidth()/2f,
            posY - getHeight()/2f
        );
        setAlive(true); // Use setAlive from GameEntity
        lives = 5;
        lastHitTime = 0f;
        velocityX = 0f;
        velocityY = 0f;
    }

    @Override
    public void update(float delta) {
        if (lastHitTime > 0) {
            lastHitTime -= delta;
        }

        setPosition(getX() + velocityX * delta, getY() + velocityY * delta);

        float maxX = (worldWidth > 0f) ? worldWidth : Gdx.graphics.getWidth();
        float maxY = (worldHeight > 0f) ? worldHeight : Gdx.graphics.getHeight();

        if (getX() < 0) setX(0);
        if (getX() + getWidth() > maxX) {
            setX(maxX - getWidth());
        }
        if (getY() < 0) setY(0);
        if (getY() + getHeight() > maxY) {
            setY(maxY - getHeight());
        }
    }

    public void setWorldBounds(float width, float height) {
        this.worldWidth = width;
        this.worldHeight = height;
    }

    public void takeDamage() {
        if (lastHitTime <= 0 && alive) {
            lives--;
            lastHitTime = hitCooldown;

            if (lives <= 0) {
                die();
            }
        }
    }

    public void die() {
        if (isAlive()) { // Use isAlive from GameEntity
            setAlive(false); // Use setAlive from GameEntity
            if (deathSound != null) {
                deathSound.play();
            }
        }
    }

    // isAlive() method removed as it's inherited from GameEntity

    public int getLives() {
        return lives;
    }

    public void setInputManager(HeroInputManager inputManager) {
        this.inputManager = inputManager;
    }

    public HeroInputManager getInputManager() {
        return inputManager;
    }

    public void setVelocityX(float vx) {
        this.velocityX = vx;
    }

    public void setVelocityY(float vy) {
        this.velocityY = vy;
    }

    public float getMoveSpeed() {
        return moveSpeed;
    }
}
