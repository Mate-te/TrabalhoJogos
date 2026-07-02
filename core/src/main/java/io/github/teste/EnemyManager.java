package io.github.teste;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class EnemyManager {

    private final Texture alienTexture;
    private final Array<Alien> activeAliens;
    private final Pool<Alien> alienPool;

    private float mapWidth;
    private float mapHeight;

    // Variáveis de controle de dificuldade e spawn
    private float alienSpawnTimer = 0f;
    private final float initialAlienSpawnInterval = 0.8f;
    private final float minAlienSpawnInterval = 0.5f;
    private final float timeToReachMin = 60f;
    private final float spawnDecreaseRate = (initialAlienSpawnInterval - minAlienSpawnInterval) / timeToReachMin;

    public EnemyManager(Texture alienTexture, float mapWidth, float mapHeight) {
        this.alienTexture = alienTexture;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.activeAliens = new Array<Alien>();

        // Configuração do pool de inimigos para otimização de memória
        this.alienPool = new Pool<Alien>() {
            @Override
            protected Alien newObject() {
                return new Alien(EnemyManager.this.alienTexture);
            }
        };
    }

    public void update(float delta, float elapsedTime, Hero hero) {
        // Calcula o intervalo de spawn atual baseado no tempo de jogo
        float currentSpawnInterval = Math.max(minAlienSpawnInterval, initialAlienSpawnInterval - (elapsedTime * spawnDecreaseRate));
        alienSpawnTimer += delta;

        // Spawna inimigos quando o timer atinge o intervalo
        if (alienSpawnTimer >= currentSpawnInterval) {
            spawnAlien(hero);
            alienSpawnTimer = 0f;
        }

        // Atualiza todos os aliens vivos e limpa os mortos
        for (int i = activeAliens.size; --i >= 0;) {
            Alien alien = activeAliens.get(i);
            alien.update(delta, hero);

            if (!alien.isAlive()) {
                activeAliens.removeIndex(i);
                alienPool.free(alien);
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

    public Array<Alien> getActiveAliens() {
        return activeAliens;
    }
}
