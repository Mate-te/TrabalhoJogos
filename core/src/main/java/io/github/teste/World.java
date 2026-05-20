package io.github.teste;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;

public class World {

    private Texture bulletTexture;
    private Texture alienTexture;
    private final Array<Bullet> activeBullets = new Array<Bullet>();
    private final Array<Alien> activeAliens = new Array<Alien>();

    private final Pool<Bullet> bulletPool = new Pool<Bullet>() {
        @Override
        protected Bullet newObject() {
            return new Bullet(bulletTexture);
        }
    };
    private final Pool<Alien> alienPool = new Pool<Alien>() {
        @Override
        protected Alien newObject() {
            return new Alien(alienTexture);
        }
    };

    private AssetManager manager;
    private Hero hero;

    private float alienSpawnTimer = 0f;
    private float elapsedTime = 0f;

    private final float initialAlienSpawnInterval = 0.8f;
    private final float minAlienSpawnInterval = 0.5f;
    private final float timeToReachMin = 60f;
    private final float spawnDecreaseRate = (initialAlienSpawnInterval - minAlienSpawnInterval) / timeToReachMin;

    public World(AssetManager manager, Hero hero) {
        this.manager = manager;
        this.hero = hero;
        this.alienTexture = manager.get("demo.png", Texture.class);
        this.bulletTexture = manager.get("bullet.png", Texture.class);
    }

    public void update(float delta) {
        elapsedTime += delta;

        if (hero != null && hero.isAlive()) {
            hero.update(delta);
        }

        float currentSpawnInterval = Math.max(minAlienSpawnInterval, initialAlienSpawnInterval - (elapsedTime * spawnDecreaseRate));
        alienSpawnTimer += delta;
        if (alienSpawnTimer >= currentSpawnInterval) {
            spawnAlien();
            alienSpawnTimer = 0f;
        }

        for (int i = activeAliens.size; --i >= 0;) {
            Alien alien = activeAliens.get(i);
            alien.update(delta);
            if (!alien.isAlive()) {
                activeAliens.removeIndex(i);
                alienPool.free(alien);
            }
        }

        for (int i = activeBullets.size; --i >= 0;) {
            Bullet bullet = activeBullets.get(i);
            bullet.update(delta);
            if (!bullet.isAlive()) {
                activeBullets.removeIndex(i);
                bulletPool.free(bullet);
            }
        }

        checkCollisions();
    }

    private void spawnAlien() {
        Alien alien = alienPool.obtain();
        float x = Gdx.graphics.getWidth() + 50;
        float y = MathUtils.random(50, Gdx.graphics.getHeight() - 50);
        alien.init(x, y);
        activeAliens.add(alien);
    }

    public void shoot(float x, float y, float angleDeg) {
        Bullet bullet = bulletPool.obtain();
        bullet.init(x, y, angleDeg);
        activeBullets.add(bullet);

        if (manager.isLoaded("data/PIU.wav", com.badlogic.gdx.audio.Sound.class)) {
            com.badlogic.gdx.audio.Sound s = manager.get("data/PIU.wav", com.badlogic.gdx.audio.Sound.class);
            bullet.setSom(s);
            if (s != null) s.play();
        }
    }

    private void checkCollisions() {
        for (int i = activeBullets.size; --i >= 0;) {
            Bullet bullet = activeBullets.get(i);
            for (int j = activeAliens.size; --j >= 0;) {
                Alien alien = activeAliens.get(j);
                if (bullet.getBoundingRectangle().overlaps(alien.getBoundingRectangle())) {
                    bullet.setAlive(false);
                    alien.setAlive(false);
                }
            }
        }

        for (int j = activeAliens.size; --j >= 0;) {
            Alien alien = activeAliens.get(j);
            if (hero != null && hero.isAlive() && hero.getBoundingRectangle().overlaps(alien.getBoundingRectangle())) {
                hero.die();
            }
        }
    }

    public Array<Bullet> getActiveBullets() { return activeBullets; }
    public Array<Alien> getActiveAliens() { return activeAliens; }
    public Hero getHero() { return hero; }
    public float getElapsedTime() { return elapsedTime; }
}
