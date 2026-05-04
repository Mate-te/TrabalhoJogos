package io.github.teste;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Game;

public class GameScreen implements Screen {
    private Game game;
    private AssetManager manager;
    private SpriteBatch batch;
    private Texture fundo;
    private Texture alien;
    private Texture cowboy;
    private Texture bullet;
    private World world;

    public GameScreen(Game game, AssetManager manager) {
        this.game = game;
        this.manager = manager;
        batch = new SpriteBatch();
        fundo = manager.get("fundojpeg.jpeg", Texture.class);
        cowboy = manager.get("batman.png", Texture.class);
        alien = manager.get("demo.png", Texture.class);
        bullet = manager.get("bullet.png", Texture.class);
        manager.load("data/PIU.wav", com.badlogic.gdx.audio.Sound.class);
        manager.finishLoading();
        world = new World(manager);
        world.setCowboyY((float) Gdx.graphics.getHeight() / 2.0f - (float) cowboy.getHeight() / 2.0f);
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        world.update(delta);

        // Detecta clique
        if (Gdx.input.justTouched()) {
            float cowboyX = 50;
            float cowboyY = (float) Gdx.graphics.getHeight() / 2.0f - (float) cowboy.getHeight() / 2.0f;
            world.shoot(cowboyX + cowboy.getWidth(), cowboyY + (float) cowboy.getHeight() / 2.0f);
        }

        batch.begin();
        batch.draw(fundo, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        float cowboyX = 50;
        float cowboyY = (float) Gdx.graphics.getHeight() / 2.0f - (float) cowboy.getHeight() / 2.0f;
        batch.draw(cowboy, cowboyX, cowboyY);

        for (Bullet b : world.getActiveBullets()) {
            batch.draw(bullet, b.getPosition().x, b.getPosition().y);
        }

        for (Alien a : world.getActiveAliens()) {
            batch.draw(alien, a.getPosition().x, a.getPosition().y);
        }

        batch.end();
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
    }
}
