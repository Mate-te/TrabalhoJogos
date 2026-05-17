package io.github.teste;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Texture;

public class HeroInputManager implements InputProcessor {
    private Hero hero;
    private World world;
    private Texture heroTexture;

    public HeroInputManager(Hero hero, World world, Texture heroTexture) {
        this.hero = hero;
        this.world = world;
        this.heroTexture = heroTexture;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (hero != null && hero.isAlive() && world != null) {
            float shootX = hero.getPosition().x + heroTexture.getWidth();
            float shootY = hero.getPosition().y + heroTexture.getHeight() / 2f;
            world.shoot(shootX, shootY);
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
