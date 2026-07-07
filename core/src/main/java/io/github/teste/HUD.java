package io.github.teste;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class HUD {
    private static final int MAX_LIVES = 5;
    private static final float HEART_SIZE = 32f;
    private static final float HEART_SPACING = 40f;
    private static final float PADDING_LEFT = 20f;
    private static final float PADDING_TOP = 20f;
    private static final float PADDING_RIGHT = 20f;

    private Texture heartFull;
    private Texture heartEmpty;
    private BitmapFont font;

    public HUD(Texture heartFull, Texture heartEmpty, BitmapFont font) {
        this.heartFull = heartFull;
        this.heartEmpty = heartEmpty;
        this.font = font;
    }

    public void draw(SpriteBatch batch, int lives, float elapsedTime, int currentWave, int maxWaves, float screenWidth, float screenHeight) {
        drawHearts(batch, lives, screenHeight);
        drawTimer(batch, elapsedTime, screenWidth, screenHeight);
        drawWave(batch, currentWave, maxWaves, screenWidth, screenHeight);
    }

    private void drawHearts(SpriteBatch batch, int currentLives, float screenHeight) {
        for (int i = 0; i < MAX_LIVES; i++) {
            float x = PADDING_LEFT + (i * HEART_SPACING);
            float y = screenHeight - PADDING_TOP - HEART_SIZE;

            if (i < currentLives) {
                batch.draw(heartFull, x, y, HEART_SIZE, HEART_SIZE);
            } else {
                batch.draw(heartEmpty, x, y, HEART_SIZE, HEART_SIZE);
            }
        }
    }

    private void drawTimer(SpriteBatch batch, float elapsedTime, float screenWidth, float screenHeight) {
        int minutes = (int) (elapsedTime / 60f);
        int seconds = (int) (elapsedTime % 60f);
        int milliseconds = (int) ((elapsedTime % 1f) * 1000f);
        String timeText = String.format("%02d:%02d:%03d", minutes, seconds, milliseconds);

        font.draw(batch, timeText, screenWidth - 150, screenHeight - PADDING_TOP);
    }

    private void drawWave(SpriteBatch batch, int currentWave, int maxWaves, float screenWidth, float screenHeight) {
        String waveText = String.format("Wave %d / %d", currentWave, maxWaves);

        font.draw(batch, waveText, screenWidth - 150, screenHeight - PADDING_TOP - 20f);
    }
}
