package io.github.teste;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.math.MathUtils;

public class LoadingScreen implements Screen {
    private Game game;
    private AssetManager manager;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private float angle = 0;

    public LoadingScreen(Game game, AssetManager manager) {
        this.game = game;
        this.manager = manager;
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        loadAssets();
    }

    private void loadAssets() {
        manager.load("fundojpeg.jpeg", Texture.class);
        manager.load("batman.png", Texture.class);
        manager.load("demo.png", Texture.class);
        manager.load("bullet.png", Texture.class);
        manager.load("data/PIU.wav", com.badlogic.gdx.audio.Sound.class);
        manager.load("data/morte.wav", com.badlogic.gdx.audio.Sound.class);
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (manager.update()) {
            game.setScreen(new GameScreen(game, manager));
            return;
        }

        angle += delta * 360; // rotate 360 degrees per second

        batch.begin();
        String progressText = "Loading... " + (int)(manager.getProgress() * 100) + "%";
        font.draw(batch, progressText, Gdx.graphics.getWidth()/2 - 50, Gdx.graphics.getHeight()/2 + 50);
        batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1, 1, 1, 1);
        float centerX = Gdx.graphics.getWidth() / 2f;
        float centerY = Gdx.graphics.getHeight() / 2f;
        float radius = 30;
        shapeRenderer.line(centerX, centerY, centerX + radius * MathUtils.cosDeg(angle), centerY + radius * MathUtils.sinDeg(angle));
        shapeRenderer.end();
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
        shapeRenderer.dispose();
        font.dispose();
    }
}
