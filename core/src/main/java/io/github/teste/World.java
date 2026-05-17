package io.github.teste;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.Gdx;

public class World {

    private final Array<Bullet> activeBullets = new Array<Bullet>();
    private final Array<Alien> activeAliens = new Array<Alien>();
    private final Pool<Bullet> bulletPool = new Pool<Bullet>() {
        @Override
        protected Bullet newObject() {
            return new Bullet();
        }
    };
    private final Pool<Alien> alienPool = new Pool<Alien>() {
        @Override
        protected Alien newObject() {
            return new Alien();
        }
    };

    private AssetManager manager;
    private Hero hero;

    public float alienSpawnTimer = 0.5f;
    public float alienSpawnInterval = 0.5f;

    public World(AssetManager manager, Hero hero) {
        this.manager = manager;
        this.hero = hero;
        // Posiciona na esquerda, altura central
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
        // Usa a posição Y do herói
        alien.init(Gdx.graphics.getWidth(), hero.getPosition().y);
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
        // Colisões bala vs alien
        for (int i = activeBullets.size; --i >= 0;) {
            Bullet bullet = activeBullets.get(i);
            for (int j = activeAliens.size; --j >= 0;) {
                Alien alien = activeAliens.get(j);
                if (bullet.getPosition().dst(alien.getPosition()) < 50) {
                    bullet.setAlive(false);
                    alien.setAlive(false);
                }
            }
        }

        // Colisões alien vs herói
        for (int j = activeAliens.size; --j >= 0;) {
            Alien alien = activeAliens.get(j);
            if (hero.isAlive() && hero.getPosition().dst(alien.getPosition()) < 50) {
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
