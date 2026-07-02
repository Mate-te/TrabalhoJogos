package io.github.teste;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;

public class World {

    private Texture bulletTexture;
    private final Array<Bullet> activeBullets = new Array<Bullet>();
    private final Pool<Bullet> bulletPool = new Pool<Bullet>() {
        @Override
        protected Bullet newObject() {
            return new Bullet(bulletTexture);
        }
    };

    private Hero hero;
    private EnemyManager enemyManager;

    // Dimensoes do mapa em pixels (usadas para limites e spawns)
    private float mapWidth = 0f;
    private float mapHeight = 0f;

    private float elapsedTime = 0f;

    public World(Hero hero, float mapWidth, float mapHeight) {
        this.hero = hero;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.bulletTexture = Assets.manager.get(Assets.BULLET, Texture.class);
        Texture alienTexture = Assets.manager.get(Assets.ALIEN_SPRITESHEET, Texture.class);

        this.enemyManager = new EnemyManager(alienTexture, mapWidth, mapHeight);

    }

    public void update(float delta) {
        elapsedTime += delta;

        if (hero != null && hero.isAlive()) {
            hero.update(delta);
        }

        enemyManager.update(delta, elapsedTime, hero);

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
        // Colisão: Tiros x Aliens
        for (int i = activeBullets.size; --i >= 0;) {
            Bullet bullet = activeBullets.get(i);
            for (int j = enemyManager.getActiveAliens().size; --j >= 0;) {
                Alien alien = enemyManager.getActiveAliens().get(j);
                if (bullet.getBoundingRectangle().overlaps(alien.getBoundingRectangle())) {
                    bullet.setAlive(false);
                    alien.setAlive(false);
                }
            }
        }

        // Colisão: Hero x Aliens
        for (int j = enemyManager.getActiveAliens().size; --j >= 0;) {
            Alien alien = enemyManager.getActiveAliens().get(j);
            if (hero != null && hero.isAlive() && hero.getBoundingRectangle().overlaps(alien.getBoundingRectangle())) {
                alien.setAlive(false);
                hero.takeDamage();
            }
        }
    }

    public Array<Bullet> getActiveBullets() { return activeBullets; }
    public Array<Alien> getActiveAliens() { return enemyManager.getActiveAliens(); }
    public Hero getHero() { return hero; }
    public float getElapsedTime() { return elapsedTime; }
}
