package io.github.teste;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

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

    public float alienSpawnTimer = 0.5f;
    public float alienSpawnInterval = 0.5f;

    public World(AssetManager manager, Hero hero) {
        this.manager = manager;
        this.hero = hero;
        this.alienTexture = manager.get("demo.png", Texture.class);
        this.bulletTexture = manager.get("bullet.png", Texture.class);
        this.hero.init(0, Gdx.graphics.getHeight() / 2f);
    }

    public void update(float delta) {
        alienSpawnTimer += delta;
        if (alienSpawnTimer >= alienSpawnInterval) {
            alienSpawnTimer = 0;
            spawnAlien();
        }

        // Atualiza balas
        for (Bullet bullet : activeBullets) {
            bullet.update(delta);
        }

        // Atualiza aliens
        for (Alien alien : activeAliens) {
            alien.update(delta);
        }

        // Atualiza o herói
        hero.update(delta);

        // Remove balas mortas
        for (int i = activeBullets.size; --i >= 0;) {
            Bullet bullet = activeBullets.get(i);
            if (!bullet.isAlive()) {
                activeBullets.removeIndex(i);
                bulletPool.free(bullet);
            }
        }

        // Remove aliens mortos
        for (int i = activeAliens.size; --i >= 0;) {
            Alien alien = activeAliens.get(i);
            if (!alien.isAlive()) {
                activeAliens.removeIndex(i);
                alienPool.free(alien);
            }
        }

        checkCollisions();
    }

    private void spawnAlien() {
        Alien alien = alienPool.obtain();
        float heroCenterY = hero.getY() + hero.getHeight() / 2f;
        alien.init(Gdx.graphics.getWidth(), heroCenterY);
        activeAliens.add(alien);
    }

    public void shoot(float x, float y) {
        Bullet bullet = bulletPool.obtain();
        bullet.init(x, y);
        activeBullets.add(bullet);

        // Verifica se o som foi carregado e toca
        if (manager.isLoaded("data/PIU.wav", com.badlogic.gdx.audio.Sound.class)) {
            com.badlogic.gdx.audio.Sound s = manager.get("data/PIU.wav", com.badlogic.gdx.audio.Sound.class);
            bullet.setSom(s);
            if (s != null) s.play();
        }
    }

    private void checkCollisions() {

        // bala vs alien
        for (int i = activeBullets.size; --i >= 0;) {

            Bullet bullet = activeBullets.get(i);

            for (int j = activeAliens.size; --j >= 0;) {

                Alien alien = activeAliens.get(j);

                if (bullet.getBoundingRectangle().overlaps(
                    alien.getBoundingRectangle())) {

                    bullet.setAlive(false);
                    alien.setAlive(false);
                }
            }
        }

        // alien vs herói
        for (int j = activeAliens.size; --j >= 0;) {

            Alien alien = activeAliens.get(j);

            if (hero.isAlive() &&
                hero.getBoundingRectangle().overlaps(
                    alien.getBoundingRectangle())) {

                hero.die();
            }
        }
    }
    public Array<Bullet> getActiveBullets() {
        return activeBullets;
    }

    public Array<Alien> getActiveAliens() {
        return activeAliens;
    }

    public Hero getHero() {
        return hero;
    }
}
