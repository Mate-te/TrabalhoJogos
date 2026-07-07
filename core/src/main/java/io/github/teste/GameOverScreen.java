package io.github.teste;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameOverScreen implements Screen {

    private final Main game;
    private Stage stage;
    private Viewport viewport;
    private Skin skin;
    private SpriteBatch batch;
    private Texture backgroundTexture;
    private int finalScore;

    public GameOverScreen(Main game, int finalScore) {
        this.game = game;
        this.finalScore = finalScore;

        viewport = new FitViewport(800, 480, new OrthographicCamera());
        stage = new Stage(viewport);
        batch = new SpriteBatch();

        skin = new Skin();

        // Criando a fonte
        BitmapFont font = new BitmapFont();
        font.getData().setScale(2f); // Aumentando a fonte para ficar mais visível
        skin.add("default-font", font);

        // Estilo do botão
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = skin.getFont("default-font");
        textButtonStyle.fontColor = Color.WHITE;
        textButtonStyle.overFontColor = Color.YELLOW;
        textButtonStyle.downFontColor = Color.RED;
        skin.add("default", textButtonStyle);

        // Estilo do texto (Label)
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = skin.getFont("default-font");
        labelStyle.fontColor = Color.WHITE;
        skin.add("default", labelStyle);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        // Por enquanto usando o fundo do Menu Inicial
        backgroundTexture = Assets.manager.get(Assets.RETRY_BACKGROUND, Texture.class);

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);



        // Pontuação Final
        Label scoreLabel = new Label("Score Final: " + finalScore, skin);
        scoreLabel.setAlignment(Align.center);
        table.add(scoreLabel).padBottom(40).row();

        // Botão de Tentar Novamente
        TextButton retryButton = new TextButton("Tentar Novamente", skin);
        table.add(retryButton).width(300).height(60).row();

        retryButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Inicia um novo jogo recriando o GameScreen
                game.setScreen(new GameScreen(game));
                dispose();
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        // Desenha o background escurecido
        batch.setColor(0.5f, 0.5f, 0.5f, 1f);
        batch.draw(backgroundTexture, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        batch.setColor(1f, 1f, 1f, 1f); // Reset na cor
        batch.end();

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        batch.dispose();
    }
}
