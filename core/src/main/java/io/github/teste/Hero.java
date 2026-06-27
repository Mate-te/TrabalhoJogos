package io.github.teste;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Hero extends GameEntity { // Changed to extend GameEntity

    private Sound deathSound;
    private HeroInputManager inputManager;
    private float velocityX = 0f;
    private float velocityY = 0f;
    private final float moveSpeed = 200f;

    private int lives = 5;
    private float lastHitTime = 0f;
    private final float hitCooldown = 1f; // 1 segundo entre danos

    // World bounds (em pixels). Se 0, usa as dimensões da tela como fallback.
    private float worldWidth = 0f;
    private float worldHeight = 0f;

    // Partículas de fogo
    private ParticleEmitter fireEmitter;
    private final float VELOCITY_THRESHOLD = 10f; // Velocidade mínima para emitir partículas

    public Hero(Texture texture, Sound deathSound, Texture particleTexture) {
        super(texture); // Call to GameEntity constructor
        this.deathSound = deathSound;
        this.lives = 5;
        setSize(64, 64);

        // Inicializa o emitter de partículas de fogo
        this.fireEmitter = new ParticleEmitter(50f, particleTexture); // 50 partículas por segundo
        this.fireEmitter.setParticleLife(0.8f);
        this.fireEmitter.setParticleSpeed(80f, 180f);
        this.fireEmitter.setParticleSize(10f);
    }

    public void init(float posX, float posY) {
        setPosition(
            posX - getWidth()/2f,
            posY - getHeight()/2f
        );
        setAlive(true); // Use setAlive from GameEntity
        lives = 5;
        lastHitTime = 0f;
        velocityX = 0f;
        velocityY = 0f;
    }

    @Override
    public void update(float delta) {
        if (lastHitTime > 0) {
            lastHitTime -= delta;
        }

        setPosition(getX() + velocityX * delta, getY() + velocityY * delta);

        float maxX = (worldWidth > 0f) ? worldWidth : Gdx.graphics.getWidth();
        float maxY = (worldHeight > 0f) ? worldHeight : Gdx.graphics.getHeight();

        if (getX() < 0) setX(0);
        if (getX() + getWidth() > maxX) {
            setX(maxX - getWidth());
        }
        if (getY() < 0) setY(0);
        if (getY() + getHeight() > maxY) {
            setY(maxY - getHeight());
        }

        // Atualiza posição do emitter (parte traseira da nave, meio-esquerda)
        float heroCenterX = getX() + getWidth() / 2f;
        float heroCenterY = getY() + getHeight() / 2f;
        
        // Calcula o offset baseado na rotação da nave
        float rotationRad = getRotation() * com.badlogic.gdx.math.MathUtils.degreesToRadians;
        float offsetDistance = getWidth() / 2.5f; // Distância do centro até a traseira
        
        // Offset na direção oposta à frente da nave (rotação + 180°)
        float emitterX = heroCenterX + com.badlogic.gdx.math.MathUtils.cos(rotationRad + 3.14159f) * offsetDistance;
        float emitterY = heroCenterY + com.badlogic.gdx.math.MathUtils.sin(rotationRad + 3.14159f) * offsetDistance;
        
        fireEmitter.setPosition(emitterX, emitterY);
        fireEmitter.setRotation(getRotation());

        // Verifica se está se movendo
        float currentVelocity = (float) Math.sqrt(velocityX * velocityX + velocityY * velocityY);
        fireEmitter.setEmitting(currentVelocity > VELOCITY_THRESHOLD);

        // Atualiza as partículas
        fireEmitter.update(delta);
    }

    public void setWorldBounds(float width, float height) {
        this.worldWidth = width;
        this.worldHeight = height;
    }

    public void takeDamage() {
        if (lastHitTime <= 0 && alive) {
            lives--;
            lastHitTime = hitCooldown;

            if (lives <= 0) {
                die();
            }
        }
    }

    public void die() {
        if (isAlive()) { // Use isAlive from GameEntity
            setAlive(false); // Use setAlive from GameEntity
            if (deathSound != null) {
                deathSound.play();
            }
        }
    }

    // isAlive() method removed as it's inherited from GameEntity

    public int getLives() {
        return lives;
    }

    public void setInputManager(HeroInputManager inputManager) {
        this.inputManager = inputManager;
    }

    public HeroInputManager getInputManager() {
        return inputManager;
    }

    public void setVelocityX(float vx) {
        this.velocityX = vx;
    }

    public void setVelocityY(float vy) {
        this.velocityY = vy;
    }

    public float getMoveSpeed() {
        return moveSpeed;
    }

    public ParticleEmitter getFireEmitter() {
        return fireEmitter;
    }
}
