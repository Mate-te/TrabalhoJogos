package io.github.teste;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Game;

public class GameScreen implements Screen {
    private Game game;
    private AssetManager manager;
    private SpriteBatch batch;
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

        manager.load("fundojpeg.jpeg", Texture.class);
        manager.load("batman.png", Texture.class);
        manager.load("demo.png", Texture.class);
        manager.load("bullet.png", Texture.class);
        batch = new SpriteBatch();


        manager.load("data/PIU.wav", com.badlogic.gdx.audio.Sound.class);
        manager.load("data/morte.wav", com.badlogic.gdx.audio.Sound.class);
        manager.finishLoading();

        // Obtem sons
        fundo = manager.get("fundojpeg.jpeg", Texture.class);
        alien = manager.get("demo.png", Texture.class);
        heroIMG = manager.get("batman.png", Texture.class);
        bullet = manager.get("bullet.png", Texture.class);
        com.badlogic.gdx.audio.Sound shootSound = manager.get("data/PIU.wav", com.badlogic.gdx.audio.Sound.class);
        com.badlogic.gdx.audio.Sound deathSound = manager.get("data/morte.wav", com.badlogic.gdx.audio.Sound.class);

        // Cria o herói
        hero = new Hero(heroIMG,deathSound);

        // Cria o mundo
        world = new World(manager, hero);

        // Posiciona o herói
        hero.setPosition(50, (float) Gdx.graphics.getHeight() / 2.0f - hero.getHeight() / 2.0f);

        // Cria o gerenciador de input do herói
        heroInputManager = new HeroInputManager(hero, world, heroIMG);

        // Injeta o gerenciador no herói
        hero.setInputManager(heroInputManager);

        // Registra o gerenciador de input como InputProcessor
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
