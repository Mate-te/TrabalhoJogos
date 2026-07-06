package io.github.teste;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class EnemyManager {

    private final Texture alienTexture;
    private final Texture enemyJFTexture;
    private final Array<Alien> activeAliens;
    private final Pool<Alien> alienPool;
    private final Pool<Boss> bossPool;
    private final Pool<EnemyJF> enemyJFPool;
    private final Array<EnemyJF> activeEnemyJF;
    private float enemyJFSpawnTimer = 0f;
    private final float enemyJFSpawnInterval = 2f; // spawn a cada 2 segundos (após 30s)

    private float mapWidth;
    private float mapHeight;


    // Variáveis de controle de dificuldade e spawn
    private float bossSpawnTimer = 0f;
    private final float bossSpawnInterval = 60f; // 60 segundos
    private float alienSpawnTimer = 0f;
    private final float initialAlienSpawnInterval = 0.8f;
    private final float minAlienSpawnInterval = 0.5f;
    private final float timeToReachMin = 60f;
    private final float spawnDecreaseRate = (initialAlienSpawnInterval - minAlienSpawnInterval) / timeToReachMin;

    public EnemyManager(Texture alienTexture, Texture enemyShipTexture,float mapWidth, float mapHeight) {
        this.alienTexture = alienTexture;
        this.enemyJFTexture = enemyShipTexture;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.activeAliens = new Array<Alien>();
        this.activeEnemyJF = new Array<EnemyJF>();

        // Configuração do pool de inimigos para otimização de memória
        this.alienPool = new Pool<Alien>() {
            @Override
            protected Alien newObject() {
                return new Alien(EnemyManager.this.alienTexture);
            }
        };
        this.bossPool = new Pool<Boss>() {
            @Override
            protected Boss newObject() {
                // usa a mesma textura por enquanto; idealmente tenha uma textura específica para boss
                return new Boss(EnemyManager.this.alienTexture);
            }
        };

        this.enemyJFPool = new Pool<EnemyJF>() {
            @Override
            protected EnemyJF newObject() {
                return new EnemyJF(EnemyManager.this.enemyJFTexture);
            }
        };
    }

    public void update(float delta, float elapsedTime, Hero hero) {
        // Spawn normal de aliens (já existente)
        float currentSpawnInterval = Math.max(minAlienSpawnInterval, initialAlienSpawnInterval - (elapsedTime * spawnDecreaseRate));
        alienSpawnTimer += delta;

        if (alienSpawnTimer >= currentSpawnInterval) {
            spawnAlien(hero);
            alienSpawnTimer = alienSpawnTimer -currentSpawnInterval;
        }

        // --- boss spawn a cada bossSpawnInterval segundos ---
        bossSpawnTimer += delta;
        if (bossSpawnTimer >= bossSpawnInterval) {
            spawnBoss(hero);
            bossSpawnTimer = bossSpawnTimer-bossSpawnInterval;
        }

        // --- enemy ship spawn a partir de 30 segundos ---
        if (elapsedTime >= 30f) {
            enemyJFSpawnTimer += delta;
            if (enemyJFSpawnTimer >= enemyJFSpawnInterval) {
                spawnEnemyShip(hero);
                enemyJFSpawnTimer = enemyJFSpawnTimer-enemyJFSpawnInterval;
            }
        }

        // Atualiza todos os aliens vivos e limpa os mortos
        for (int i = activeAliens.size; --i >= 0;) {
            Alien alien = activeAliens.get(i);
            alien.update(delta, hero);

            if (!alien.isAlive()) {
                activeAliens.removeIndex(i);
                if (alien instanceof Boss) {
                    bossPool.free((Boss) alien);
                } else {
                    alienPool.free(alien);
                }
            }
        }

        // Atualiza todos os enemy ships vivos e limpa os mortos
        for (int i = activeEnemyJF.size; --i >= 0;) {
            EnemyJF ship = activeEnemyJF.get(i);
            ship.update(delta, hero);

            if (!ship.isAlive()) {
                activeEnemyJF.removeIndex(i);
                enemyJFPool.free(ship);
            }
        }
    }

    private void spawnAlien(Hero hero) {
        Alien alien = alienPool.obtain();
        alien.setMapBounds(mapWidth, mapHeight);

        // Raio e ângulo do spawn estilo Vampire Survivors
        float spawnRadius = Math.max(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        float angle = MathUtils.random(0f, MathUtils.PI2);

        // Define a posição baseada no Herói
        float heroX = hero != null ? hero.getX() + hero.getWidth() / 2f : mapWidth / 2f;
        float heroY = hero != null ? hero.getY() + hero.getHeight() / 2f : mapHeight / 2f;

        float x = heroX + MathUtils.cos(angle) * spawnRadius;
        float y = heroY + MathUtils.sin(angle) * spawnRadius;

        alien.init(x, y);
        activeAliens.add(alien);
    }

    private void spawnBoss(Hero hero) {
        Boss boss = bossPool.obtain();
        boss.setMapBounds(mapWidth, mapHeight);

        float spawnRadius = Math.max(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        float angle = MathUtils.random(0f, MathUtils.PI2);
        float heroX = hero != null ? hero.getX() + hero.getWidth() / 2f : mapWidth / 2f;
        float heroY = hero != null ? hero.getY() + hero.getHeight() / 2f : mapHeight / 2f;
        float x = heroX + MathUtils.cos(angle) * spawnRadius;
        float y = heroY + MathUtils.sin(angle) * spawnRadius;

        boss.init(x, y);
        activeAliens.add(boss);
    }

    private void spawnEnemyShip(Hero hero) {
        EnemyJF ship = enemyJFPool.obtain();
        ship.setMapBounds(mapWidth, mapHeight);

        float spawnRadius = Math.max(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        float angle = MathUtils.random(0f, MathUtils.PI2);

        float heroX = hero != null ? hero.getX() + hero.getWidth() / 2f : mapWidth / 2f;
        float heroY = hero != null ? hero.getY() + hero.getHeight() / 2f : mapHeight / 2f;

        float x = heroX + MathUtils.cos(angle) * spawnRadius;
        float y = heroY + MathUtils.sin(angle) * spawnRadius;

        ship.init(x, y);
        activeEnemyJF.add(ship);
    }
    public Array<Alien> getActiveAliens() {
        return activeAliens;
    }

    public Array<EnemyJF> getActiveEnemyJF() {
        return activeEnemyJF;
    }
}
