package io.github.teste;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.math.MathUtils;

public class Bullet extends GameEntity implements Pool.Poolable { // Changed to extend GameEntity

    private Sound som;
    private float angle; // Adicionado para controle de vetor
    private final float speed = 500f;

    public Bullet(Texture texture) {
        super(texture);
        setAlive(false);
        setSize(32, 32);
        setOriginCenter(); // Define o centro para rotação da sprite da bala
    }

    public void init(float posX, float posY, float angleDeg) {
        setPosition(
            posX - getWidth() / 2f,
            posY - getHeight() / 2f
        );
        this.angle = angleDeg;
        setRotation(angleDeg);
        setAlive(true); // Use setAlive from GameEntity
    }

    @Override
    public void reset() {
        setPosition(0, 0);
        setRotation(0);
        this.angle = 0f;
        setAlive(false); // Use setAlive from GameEntity
    }

    @Override // Mark as override since it's now in GameEntity
    public void update(float delta) {
        if (isOutOfScreen()) {
            setAlive(false); // Use setAlive from GameEntity
        } else {
            // Movimentação baseada no ângulo trigonométrico
            translateX(MathUtils.cosDeg(angle) * speed * delta);
            translateY(MathUtils.sinDeg(angle) * speed * delta);
        }
    }

    private boolean isOutOfScreen() {
        return getX() > Gdx.graphics.getWidth() + 100 || getX() < -100 ||
            getY() > Gdx.graphics.getHeight() + 100 || getY() < -100;
    }

    // isAlive() and setAlive() methods removed as they are inherited from GameEntity

    public Sound getSom() {
        return som;
    }

    public void setSom(Sound som) {
        this.som = som;
    }
}
