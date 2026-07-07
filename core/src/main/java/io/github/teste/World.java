package io.github.teste;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.Texture;

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
        Texture enemyJFTexture = Assets.manager.get(Assets.ENEMY_JF_SPRITESHEET, Texture.class); // nova textura

        this.enemyManager = new EnemyManager(alienTexture, enemyJFTexture, mapWidth, mapHeight);
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
                    if (alien instanceof Boss) {
                        ((Boss) alien).takeDamage();
                        // Só ganha ponto se o boss morrer
                        if (!alien.isAlive() && hero != null) {
                            hero.addScore(alien.getScoreValue());
                        }
                    } else {
                        alien.setAlive(false);
                        if (hero != null) {
                            hero.addScore(alien.getScoreValue());
                        }
                    }
                }
            }
        }

        // Colisão: Tiros x Enemy JF (NOVO)
        for (int i = activeBullets.size; --i >= 0;) {
            Bullet bullet = activeBullets.get(i);
            for (int j = enemyManager.getActiveEnemyJF().size; --j >= 0;) {
                EnemyJF JF = enemyManager.getActiveEnemyJF().get(j);
                if (bullet.getBoundingRectangle().overlaps(JF.getBoundingRectangle())) {
                    bullet.setAlive(false);
                    JF.takeDamage(); // 3 tiros para matar

                    // Só ganha ponto se a água viva morrer (tem 3 de HP)
                    if (!JF.isAlive() && hero != null) {
                        hero.addScore(JF.getScoreValue());
                    }
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

        // Colisão: Hero x Enemy JF (NOVO)
        for (int j = enemyManager.getActiveEnemyJF().size; --j >= 0;) {
            EnemyJF JF = enemyManager.getActiveEnemyJF().get(j);
            if (hero != null && hero.isAlive() && hero.getBoundingRectangle().overlaps(JF.getBoundingRectangle())) {
                JF.setAlive(false);
                hero.takeDamage();
            }
        }
    }

    public Array<Bullet> getActiveBullets() { return activeBullets; }
    public Array<Alien> getActiveAliens() { return enemyManager.getActiveAliens(); }
    public Array<EnemyJF> getActiveEnemyJF() { return enemyManager.getActiveEnemyJF(); }
    public Hero getHero() { return hero; }
    public float getElapsedTime() { return elapsedTime; }
    public int getCurrentWave() { return enemyManager.getCurrentWave(); }
    public int getMaxWaves() { return enemyManager.getMaxWaves(); }
}
