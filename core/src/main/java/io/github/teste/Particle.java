package io.github.teste;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Particle {
    private float x, y;
    private float velocityX, velocityY;
    private float life; // Tempo de vida restante
    private float maxLife; // Tempo de vida total (para interpolação de alpha)
    private Color color;
    private float size;
    private Texture texture;

    public Particle(float x, float y, float vx, float vy, float life, Color color, float size, Texture texture) {
        this.x = x;
        this.y = y;
        this.velocityX = vx;
        this.velocityY = vy;
        this.life = life;
        this.maxLife = life;
        this.color = new Color(color);
        this.size = size;
        this.texture = texture;
    }

    public void update(float delta) {
        x += velocityX * delta;
        y += velocityY * delta;
        life -= delta;
    }

    public void draw(SpriteBatch batch) {
        if (life > 0) {
            float alpha = life / maxLife; // Fade out gradualmente
            Color drawColor = new Color(color);
            drawColor.a = alpha;

            batch.setColor(drawColor);
            batch.draw(texture, x - size / 2f, y - size / 2f, size, size);
            batch.setColor(Color.WHITE);
        }
    }

    public boolean isAlive() {
        return life > 0;
    }
}
