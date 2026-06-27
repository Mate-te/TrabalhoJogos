package io.github.teste;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

public class ParticleEmitter {
    private Array<Particle> particles;
    private float emissionRate; // Partículas por segundo
    private float timeSinceLastEmission = 0f;
    private float x, y; // Posição de emissão
    private boolean isEmitting = false;
    private float rotation = 0f; // Rotação da nave para direcionamento das partículas

    // Configurações das partículas
    private float particleLife = 1f;
    private float particleSpeedMin = 50f;
    private float particleSpeedMax = 150f;
    private float particleSize = 8f;
    private Color particleColor = new Color(1f, 0.6f, 0f, 1f); // Cor laranja/fogo
    private Texture particleTexture;

    public ParticleEmitter(float emissionRate, Texture texture) {
        this.particles = new Array<>();
        this.emissionRate = emissionRate;
        this.particleTexture = texture;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public void setEmitting(boolean emitting) {
        this.isEmitting = emitting;
    }

    public boolean isEmitting() {
        return isEmitting;
    }

    public void update(float delta) {
        // Emitir novas partículas se estiver ativo
        if (isEmitting) {
            timeSinceLastEmission += delta;
            float particlesPerFrame = emissionRate * delta;

            while (timeSinceLastEmission >= 1f / emissionRate) {
                emit();
                timeSinceLastEmission -= 1f / emissionRate;
            }
        }

        // Atualizar todas as partículas
        for (int i = particles.size - 1; i >= 0; i--) {
            Particle p = particles.get(i);
            p.update(delta);

            if (!p.isAlive()) {
                particles.removeIndex(i);
            }
        }
    }

    private void emit() {
        // Criar uma partícula com velocidade na direção oposta à nave (traseira)
        float rotationRad = rotation * MathUtils.degreesToRadians;
        
        // Emite na direção oposta à frente da nave (rotação + 180°)
        float angle = rotationRad + 3.14159f + MathUtils.random(-10f, 10f) * MathUtils.degreesToRadians;
        float speed = MathUtils.random(particleSpeedMin, particleSpeedMax);

        float vx = MathUtils.cos(angle) * speed;
        float vy = MathUtils.sin(angle) * speed;

        Particle p = new Particle(
            x + MathUtils.random(-2f, 2f), // Ligeira variação na posição
            y + MathUtils.random(-2f, 2f),
            vx,
            vy,
            particleLife,
            particleColor,
            particleSize,
            particleTexture
        );

        particles.add(p);
    }

    public void draw(SpriteBatch batch) {
        for (Particle p : particles) {
            p.draw(batch);
        }
    }

    public Array<Particle> getParticles() {
        return particles;
    }

    // Configuradores
    public void setParticleLife(float life) {
        this.particleLife = life;
    }

    public void setParticleSpeed(float min, float max) {
        this.particleSpeedMin = min;
        this.particleSpeedMax = max;
    }

    public void setParticleSize(float size) {
        this.particleSize = size;
    }

    public void setParticleColor(Color color) {
        this.particleColor = new Color(color);
    }
}
