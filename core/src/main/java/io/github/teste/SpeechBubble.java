package io.github.teste;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;

/**
 * Balão de fala simples com NinePatch e BitmapFont.
 * - limita largura (wrap)
 * - pode aparecer centralizado em uma posição (ex: meio da tela)
 * - tem duração e pequena animação de subida
 */
public class SpeechBubble {
    private final NinePatch patch;
    private final BitmapFont font;
    private final GlyphLayout layout = new GlyphLayout();

    private float x, y;
    private float width, height;
    private float maxWidth = 400f; // ajuste padrão
    private float padding = 12f;   // padding interno
    private boolean visible = false;

    private float timer = 0f;      // tempo restante para ficar visível
    private float riseSpeed = 20f; // pixels por segundo que o balão sobe enquanto visível
    private Vector2 velocity = new Vector2(0, riseSpeed);

    public SpeechBubble(Texture dialogTexture, BitmapFont font) {
        // Ajuste os valores 10,10,10,10 conforme as bordas do seu dialogbox.png
        this.patch = new NinePatch(dialogTexture, 10, 10, 10, 10);
        this.font = font;
    }

    public void setMaxWidth(float maxWidth) {
        this.maxWidth = maxWidth;
    }

    public void setPadding(float padding) {
        this.padding = padding;
    }

    /**
     * Mostra o balão centralizado a partir de uma posição (px, py)
     * px,py são coordenadas do ponto "origem" (ex: centro da nave) — o balão aparecerá acima/centrado neste ponto
     */
    public void showCentered(String text, float durationSeconds, float centerX, float centerY) {
        // Cria o layout com wrap dentro do maxWidth menos padding
        float textMaxWidth = Math.max(1f, maxWidth - padding * 2f);
        layout.setText(font, text, Color.WHITE, textMaxWidth, Align.left, true);

        width = Math.min(layout.width + padding * 2f, maxWidth);
        height = layout.height + padding * 2f;

        // Posiciona o balão centrado em centerX e acima do centerY
        x = centerX - width / 2f;
        // coloca um pouco acima do centro (ajuste conforme precisar)
        y = centerY + 10f;

        timer = durationSeconds;
        visible = true;
    }

    public void hide() {
        visible = false;
        timer = 0f;
    }

    public boolean isVisible() {
        return visible;
    }

    public void update(float delta) {
        if (!visible) return;
        timer -= delta;
        if (timer <= 0f) {
            hide();
            return;
        }
        // animação de subida suave
        y += velocity.y * delta;
    }

    public void draw(SpriteBatch batch) {
        if (!visible) return;

        // desenha a 9patch por baixo
        patch.draw(batch, x, y, width, height);

        // desenha o texto dentro da área (BitmapFont.draw com GlyphLayout)
        float textX = x + padding;
        // BitmapFont.draw(layout) espera o Y do baseline — colocamos no topo do balão menos padding
        float textY = y + height - padding;
        font.draw(batch, layout, textX, textY);
    }

    public void updateFollowPosition(float centerX, float centerY) {
        if (!visible) return;

        // Atualiza X/Y para manter o balão centrado no alvo
        x = centerX - width / 2f;
        y = centerY + 10f; // mantém o mesmo offset vertical de quando apareceu
    }
}
