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

    private float mapWidth;
    private float mapHeight;

    // --- Sistema de Waves ---
    private int currentWave = 1;
    private float waveTimer = 0f;
    private final float WAVE_DURATION = 30f;
    private final int MAX_WAVES = 5;
    private boolean bossSpawned = false;

    // Contadores de Spawn da Wave Atual
    private int aliensSpawnedThisWave = 0;
    private int jfSpawnedThisWave = 0;

    // Timers individuais de spawn
    private float alienSpawnTimer = 0f;
    private float jfSpawnTimer = 0f;

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
        if (currentWave <= MAX_WAVES) {
            waveTimer += delta;

            // Progressão de Wave (exceto na última, que dura até matar o boss)
            if (waveTimer >= WAVE_DURATION && currentWave < MAX_WAVES) {
                currentWave++;
                waveTimer = 0f;
                aliensSpawnedThisWave = 0;
                jfSpawnedThisWave = 0;
            }
            // --- Lógica da Wave 5 (Final) ---
            if (currentWave == MAX_WAVES && !bossSpawned) {
                spawnBoss(hero);
                bossSpawned = true;
            }
            // Cálculos dinâmicos dos limites da Wave Atual
            int maxOnScreenTotal = currentWave * 10;
            int maxOnScreenJF = (int) (maxOnScreenTotal * 0.3f);
            int maxOnScreenAlien = maxOnScreenTotal - maxOnScreenJF;
            int maxSpawnTotal = currentWave * 50;
            int maxSpawnJF = (int) (maxSpawnTotal * 0.3f);
            int maxSpawnAlien = maxSpawnTotal - maxSpawnJF;
            // Base de velocidade aumenta até a Wave 3 (ex: Wave 1 = 150f, Wave 2 = 170f, Wave 3 = 190f)
            float baseSpeed = 150f + (Math.min(currentWave, 3) - 1) * 20f;
            // --- Spawner de Aliens ---
            if (aliensSpawnedThisWave < maxSpawnAlien && getActiveNormalAliensCount() < maxOnScreenAlien) {
                float alienSpawnInterval = WAVE_DURATION / maxSpawnAlien;
                alienSpawnTimer += delta;

                if (alienSpawnTimer >= alienSpawnInterval) {
                    spawnAlien(hero, baseSpeed);
                    alienSpawnTimer -= alienSpawnInterval;
                    aliensSpawnedThisWave++;
                }
            }
            // --- Spawner de Jelly Fish (JF) ---
            if (jfSpawnedThisWave < maxSpawnJF && activeEnemyJF.size < maxOnScreenJF) {
                float jfSpawnInterval = WAVE_DURATION / maxSpawnJF;
                jfSpawnTimer += delta;

                if (jfSpawnTimer >= jfSpawnInterval) {
                    spawnEnemyShip(hero, baseSpeed);
                    jfSpawnTimer -= jfSpawnInterval;
                    jfSpawnedThisWave++;
                }
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
        // Atualiza todos os JF vivos e limpa os mortos
        for (int i = activeEnemyJF.size; --i >= 0;) {
            EnemyJF ship = activeEnemyJF.get(i);
            ship.update(delta, hero);
            if (!ship.isAlive()) {
                activeEnemyJF.removeIndex(i);
                enemyJFPool.free(ship);
            }
        }
    }
    // Auxiliar para não contar o Boss como "Alien Normal" nos limites da tela
    private int getActiveNormalAliensCount() {
        int count = 0;
        for (Alien a : activeAliens) {
            if (!(a instanceof Boss)) {
                count++;
            }
        }
        return count;
    }

    private void spawnAlien(Hero hero, float speed) {
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
        alien.setSpeed(speed);
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
        boss.setSpeed(60f);
        activeAliens.add(boss);
    }

    private void spawnEnemyShip(Hero hero, float speed) {
        EnemyJF ship = enemyJFPool.obtain();
        ship.setMapBounds(mapWidth, mapHeight);

        float spawnRadius = Math.max(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        float angle = MathUtils.random(0f, MathUtils.PI2);

        float heroX = hero != null ? hero.getX() + hero.getWidth() / 2f : mapWidth / 2f;
        float heroY = hero != null ? hero.getY() + hero.getHeight() / 2f : mapHeight / 2f;

        float x = heroX + MathUtils.cos(angle) * spawnRadius;
        float y = heroY + MathUtils.sin(angle) * spawnRadius;

        ship.init(x, y);
        ship.setSpeed(speed);
        activeEnemyJF.add(ship);
    }
    public Array<Alien> getActiveAliens() { return activeAliens; }
    public Array<EnemyJF> getActiveEnemyJF() { return activeEnemyJF; }
    public int getCurrentWave() { return currentWave; }
    public int getMaxWaves() { return MAX_WAVES; }
}
