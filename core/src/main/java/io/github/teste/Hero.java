package io.github.teste;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Hero extends Sprite {

    private boolean alive;

    private Sound deathSound;

    private HeroInputManager inputManager;

    public Hero(Texture texture, Sound deathSound) {

        super(texture);

        this.alive = true;

        this.deathSound = deathSound;

        setSize(64, 64);
    }

    public void init(float posX, float posY) {

        setPosition(
            posX - getWidth()/2f,
            posY - getHeight()/2f
        );

        alive = true;
    }

    public void update(float delta) {

        // lógica futura de movimento
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

    public void setInputManager(HeroInputManager inputManager) {

        this.inputManager = inputManager;
    }

    public HeroInputManager getInputManager() {

        return inputManager;
    }
}
