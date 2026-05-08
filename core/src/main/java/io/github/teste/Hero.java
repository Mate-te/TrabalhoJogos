package io.github.teste;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;

public class Hero {
    private Vector2 position;
    private boolean alive;
    private Sound deathSound;

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
        // Cowboy é estacionário, então update pode ser vazio ou adicionar lógica futura (ex: animação)
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
}
