package io.github.teste;

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

    private Hero hero;

    // Dimensoes do mapa em pixels (usadas para limites e spawns)
    private float mapWidth = 0f;
    private float mapHeight = 0f;

    private float alienSpawnTimer = 0f;
    private float elapsedTime = 0f;

    private final float initialAlienSpawnInterval = 0.8f;
    private final float minAlienSpawnInterval = 0.5f;
    private final float timeToReachMin = 60f;
    private final float spawnDecreaseRate = (initialAlienSpawnInterval - minAlienSpawnInterval) / timeToReachMin;

    public World(Hero hero, float mapWidth, float mapHeight) {
        this.hero = hero;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.alienTexture = Assets.manager.get(Assets.ALIEN_SPRITESHEET, Texture.class);
        this.bulletTexture = Assets.manager.get(Assets.BULLET, Texture.class);
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
        alien.setMapBounds(mapWidth, mapHeight);
        float x = (mapWidth > 0f) ? mapWidth + 50f : Gdx.graphics.getWidth() + 50f;
        float yMax = (mapHeight > 100f) ? mapHeight - 50f : Math.max(Gdx.graphics.getHeight() - 50f, 50f);
        float y = MathUtils.random(50f, yMax);
        alien.init(x, y);
        activeAliens.add(alien);
    }

    public void shoot(float x, float y, float angleDeg) {
        Bullet bullet = bulletPool.obtain();
        bullet.setMapBounds(mapWidth, mapHeight);
        bullet.init(x, y, angleDeg);
        activeBullets.add(bullet);

        if (Assets.manager.isLoaded(Assets.SOM_TIRO, com.badlogic.gdx.audio.Sound.class)) {
            com.badlogic.gdx.audio.Sound s = Assets.manager.get(Assets.SOM_TIRO, com.badlogic.gdx.audio.Sound.class);
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
                alien.setAlive(false);
                hero.takeDamage();
            }
        }
    }

    public Array<Bullet> getActiveBullets() { return activeBullets; }
    public Array<Alien> getActiveAliens() { return activeAliens; }
    public Hero getHero() { return hero; }
    public float getElapsedTime() { return elapsedTime; }
}
