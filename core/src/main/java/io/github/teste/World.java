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

    // timers
    private float alienSpawnTimer = 0f;
    private float elapsedTime = 0f;

    // dificuldade
    private final float initialAlienSpawnInterval = 0.8f; // intervalo inicial (s) — ajuste aqui
    private final float minAlienSpawnInterval = 0.5f;    // intervalo mínimo (s) — ajuste se quiser
    private final float timeToReachMin = 60f;             // segundos para diminuir até o mínimo
    private final float spawnDecreaseRate = (initialAlienSpawnInterval - minAlienSpawnInterval) / timeToReachMin;

    public World(AssetManager manager, Hero hero) {
        this.manager = manager;
        this.hero = hero;
        this.alienTexture = manager.get("demo.png", Texture.class);
        this.bulletTexture = manager.get("bullet.png", Texture.class);
        // Não é obrigatório inicializar a posição do hero aqui se depois o GameScreen posiciona.
        // this.hero.init(0, Gdx.graphics.getHeight() / 2f);
    }

    public void update(float delta) {
        // tempo total de jogo
        elapsedTime += delta;

        // calcula intervalo atual (diminui linearmente até o mínimo)
        float currentInterval = Math.max(minAlienSpawnInterval,
            initialAlienSpawnInterval - elapsedTime * spawnDecreaseRate);

        // atualiza timer e faz spawn quando atingir intervalo atual
        alienSpawnTimer += delta;
        if (alienSpawnTimer >= currentInterval) {
            alienSpawnTimer = 0f;
            spawnAlien(); // sempre um por vez
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
        if (hero != null) hero.update(delta);

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

        // calcula Y central do herói para alinhar verticalmente
        float heroCenterY = hero.getY() + hero.getHeight() / 2f;

        // spawn do alien vindo da direita, na mesma altura (centro) do herói
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

            if (hero != null && hero.isAlive() &&
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

    public float getElapsedTime() {
        return elapsedTime;
    }
}
