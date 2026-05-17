package io.github.teste;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;

public class Hero {
    private Vector2 position;
    private boolean alive;
    private Sound deathSound;
    private HeroInputManager inputManager; // Variável para gerenciar input

    public Hero(Sound deathSound) {
        this.position = new Vector2();
        this.alive = true;
        this.deathSound = deathSound;
    }

    public void init(float posX, float posY) {
        position.set(posX, posY);
        alive = true;
    }

    public void update(float delta) {
        // Cowboy é estacionário até o momento
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

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(float x, float y) {
        position.set(x, y);
    }

    public void setInputManager(HeroInputManager inputManager) {
        this.inputManager = inputManager;
    }

    public HeroInputManager getInputManager() {
        return inputManager;
    }
}
