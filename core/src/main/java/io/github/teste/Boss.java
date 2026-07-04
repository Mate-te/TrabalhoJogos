package io.github.teste;

import com.badlogic.gdx.graphics.Texture;

public class Boss extends Alien {
    private final int maxHp = 100;
    private int hp;

    public Boss(Texture texture) {
        super(texture);
        setSize(128f, 128f);
        this.hp = maxHp;
    }

    @Override
    public void init(float posX, float posY) {
        super.init(posX, posY);
        // Garante o tamanho do boss (caso o pool reutilize o objeto)
        setSize(128f, 128f);
        this.hp = maxHp;
    }

    /**
     * Aplica dano ao boss. Se hp <= 0, marca como morto.
     */
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

    @Override
    public void reset() {
        super.reset();
        this.hp = maxHp;
        setSize(128f, 128f);
    }


    @Override
    public void update(float delta, Hero hero) {
        if (hero != null && hero.isAlive()) {
            // Centro do boss
            float bossCenterX = getX() + getWidth() / 2f;
            float bossCenterY = getY() + getHeight() / 2f;
            // Centro do herói
            float heroCenterX = hero.getX() + hero.getWidth() / 2f;
            float heroCenterY = hero.getY() + hero.getHeight() / 2f;

            float dx = heroCenterX - bossCenterX;
            float dy = heroCenterY - bossCenterY;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);

            if (distance > 0) {
                dx /= distance;
                dy /= distance;
                float speed = 60f; // velocidade bem menor que o Alien comum (ex: 150f)
                translateX(dx * speed * delta);
                translateY(dy * speed * delta);
            }
        }

        // garante que a animação do Alien ainda seja atualizada
        super.update(delta);
    }
}
