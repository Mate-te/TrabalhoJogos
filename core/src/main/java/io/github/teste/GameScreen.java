package io.github.teste;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Game;

public class GameScreen implements Screen {
    private Game game;
    private AssetManager manager;
    private SpriteBatch batch;
    private BitmapFont font;
    private Texture fundo;
    private Texture alien;
    private Texture heroIMG;
    private Texture bullet;
    private World world;
    private Hero hero;
    private HeroInputManager heroInputManager;

    public GameScreen(Game game, AssetManager manager) {
        this.game = game;
        this.manager = manager;

        batch = new SpriteBatch();
        font = new BitmapFont(); // fonte padrão do libGDX

        fundo = manager.get("fundojpeg.jpeg", Texture.class);
        alien = manager.get("demo.png", Texture.class);
        heroIMG = manager.get("batman.png", Texture.class);
        bullet = manager.get("bullet.png", Texture.class);
        com.badlogic.gdx.audio.Sound shootSound = manager.get("data/PIU.wav", com.badlogic.gdx.audio.Sound.class);
        com.badlogic.gdx.audio.Sound deathSound = manager.get("data/morte.wav", com.badlogic.gdx.audio.Sound.class);

        hero = new Hero(heroIMG,deathSound);
        world = new World(manager, hero);

        hero.setPosition(50, (float) Gdx.graphics.getHeight() / 2.0f - hero.getHeight() / 2.0f);
        heroInputManager = new HeroInputManager(hero, world, heroIMG);
        hero.setInputManager(heroInputManager);
        Gdx.input.setInputProcessor(heroInputManager);
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        world.update(delta);

        batch.begin();
        batch.draw(fundo, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        if (world.getHero().isAlive()) {
            world.getHero().draw(batch);
        }

        for (Bullet bullet : world.getActiveBullets()) {
            bullet.draw(batch);
        }

        for (Alien alien : world.getActiveAliens()) {
            alien.draw(batch);
        }

        // renderiza temporizador no canto superior direito
        float elapsedTime = world.getElapsedTime();
        int minutes = (int)(elapsedTime / 60f);
        int seconds = (int)(elapsedTime % 60f);
        int milliseconds = (int)((elapsedTime % 1f) * 1000f);
        String timeText = String.format("%02d:%02d:%03d", minutes, seconds, milliseconds);
        font.draw(batch, timeText,
            Gdx.graphics.getWidth() - 100,
            Gdx.graphics.getHeight() - 20);

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
        font.dispose(); // importante: descartar a fonte
    }
}
