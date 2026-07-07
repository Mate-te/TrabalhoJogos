package io.github.teste;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class EnemyJF extends GameEntity {
    private float mapWidth = 0f;
    private float mapHeight = 0f;

    private float speed = 150f;

    // Animação (usa apenas a primeira linha da spritesheet)
    private TextureRegion[] frames;
    private float animationTimer = 0f;
    private final float frameDuration = 0.15f;
    private int currentFrameIndex = 0;

    // Vida
    private final int maxHp = 3;
    private int hp;

    public EnemyJF(Texture texture) {
        super(texture);
        setAlive(false);
        setSize(64, 64);
        this.hp = maxHp;

        // Inicializa frames apenas da primeira linha
        // Assumindo que a spritesheet tem 3 colunas e N linhas
        frames = new TextureRegion[3];
        int frameWidth = texture.getWidth() / 4;
        int frameHeight = texture.getHeight(); // Usa toda a altura ou pode ser ajustado

        // Extrai apenas os 3 frames da primeira linha (y=0)
        for (int i = 0; i < 3; i++) {
            frames[i] = new TextureRegion(texture, i * frameWidth, 0, frameWidth, frameHeight);
        }
    }

    public void init(float posX, float posY) {
        setPosition(
            posX - getWidth() / 2f,
            posY - getHeight() / 2f
        );
        setAlive(true);
        animationTimer = 0f;
        currentFrameIndex = 0;
        this.hp = maxHp;
    }

    public void setMapBounds(float width, float height) {
        this.mapWidth = width;
        this.mapHeight = height;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void takeDamage(int amount) {
        if (!isAlive()) return;
        hp -= amount;
        if (hp <= 0) {
            setAlive(false);
        }
    }

    public void takeDamage() {
        takeDamage(1);
    }

    public void reset() {
        setPosition(0, 0);
        setAlive(false);
        animationTimer = 0f;
        currentFrameIndex = 0;
        this.hp = maxHp;
    }

    @Override
    public void update(float delta) {
        // Atualiza a animação
        animationTimer += delta;
        if (animationTimer >= frameDuration) {
            animationTimer -= frameDuration;
            currentFrameIndex = (currentFrameIndex + 1) % 3;
        }
    }

    public void update(float delta, Hero hero) {
        if (hero != null && hero.isAlive()) {
            // Centro do navio inimigo
            float shipCenterX = getX() + getWidth() / 2f;
            float shipCenterY = getY() + getHeight() / 2f;
            // Centro do herói
            float heroCenterX = hero.getX() + hero.getWidth() / 2f;
            float heroCenterY = hero.getY() + hero.getHeight() / 2f;

            float dx = heroCenterX - shipCenterX;
            float dy = heroCenterY - shipCenterY;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);

            if (distance > 0) {
                dx /= distance;
                dy /= distance;

                // Velocidade similar ao Alien (150f) ou um pouco diferente
                float speed = 150f;
                translateX(dx * speed * delta);
                translateY(dy * speed * delta);
            }
        }

        // Atualiza animação
        update(delta);
    }

    public void draw(SpriteBatch batch) {
        if (isAlive()) {
            batch.draw(frames[currentFrameIndex],
                getX(), getY(),
                getOriginX(), getOriginY(),
                getWidth(), getHeight(),
                getScaleX(), getScaleY(),
                getRotation());
        }
    }
}
