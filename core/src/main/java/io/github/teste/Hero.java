package io.github.teste;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Hero extends Sprite {

    private boolean alive;
    private Sound deathSound;
    private HeroInputManager inputManager;
    private float velocityX = 0f;
    private float velocityY = 0f;
    private final float moveSpeed = 200f;

    private int lives = 5;
    private float lastHitTime = 0f;
    private final float hitCooldown = 1f; // 1 segundo entre danos

    public Hero(Texture texture, Sound deathSound) {
        super(texture);
        this.alive = true;
        this.deathSound = deathSound;
        this.lives = 5;
        setSize(64, 64);
    }

    public void init(float posX, float posY) {
        setPosition(
            posX - getWidth()/2f,
            posY - getHeight()/2f
        );
        alive = true;
        lives = 5;
        lastHitTime = 0f;
        velocityX = 0f;
        velocityY = 0f;
    }

    public void update(float delta) {
        if (lastHitTime > 0) {
            lastHitTime -= delta;
        }

        setPosition(getX() + velocityX * delta, getY() + velocityY * delta);

        if (getX() < 0) setX(0);
        if (getX() + getWidth() > Gdx.graphics.getWidth()) {
            setX(Gdx.graphics.getWidth() - getWidth());
        }
        if (getY() < 0) setY(0);
        if (getY() + getHeight() > Gdx.graphics.getHeight()) {
            setY(Gdx.graphics.getHeight() - getHeight());
        }
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
        if (alive) {
            alive = false;
            if (deathSound != null) {
                deathSound.play();
            }
        }
    }

    public boolean isAlive() {
        return alive;
    }

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
