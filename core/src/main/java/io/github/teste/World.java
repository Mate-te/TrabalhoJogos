package io.github.teste;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.ScreenUtils;
import jdk.javadoc.internal.doclets.formats.html.markup.Text;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.MathUtils;
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
    public World(AssetManager manager) {
        this.manager = manager;
    }

    private float alienSpawnTimer = 0.5f;
    private float alienSpawnInterval = 0.5f; // segundos
    private float cowboyY;

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
        alien.init(Gdx.graphics.getWidth(), cowboyY);
        activeAliens.add(alien);
    }

    public void shoot(float x, float y) {
        Bullet bullet = bulletPool.obtain();
        bullet.init(x, y);
        activeBullets.add(bullet);
        bullet.setSom(manager.get("data/PIU.wav", Sound.class));
        bullet.getSom().play();
    }

    private void checkCollisions() {
        for (int i = activeBullets.size; --i >= 0;) {
            Bullet bullet = activeBullets.get(i);
            for (int j = activeAliens.size; --j >= 0;) {
                Alien alien = activeAliens.get(j);
                if (bullet.getPosition().dst(alien.getPosition()) < 50) { // assume raio 50
                    bullet.setAlive(false);
                    alien.setAlive(false);
                }
            }
        }
    }

    public Array<Bullet> getActiveBullets() {
        return activeBullets;
    }

    public Array<Alien> getActiveAliens() {
        return activeAliens;
    }

    public void setCowboyY(float y) {
        cowboyY = y;
    }
}
