package io.github.teste;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture; // Import Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch; // Import SpriteBatch
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MenuScreen implements Screen {

    private final Main game;
    private Stage stage;
    private Viewport viewport;
    private Skin skin;
    private SpriteBatch batch; // Added SpriteBatch
    private Texture backgroundTexture; // Added background Texture

    public MenuScreen(Main game) {
        this.game = game;
        viewport = new FitViewport(800, 480, new OrthographicCamera());
        stage = new Stage(viewport);
        batch = new SpriteBatch(); // Initialize SpriteBatch

        skin = new Skin();
        skin.add("default-font", new BitmapFont());
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = skin.getFont("default-font");
        skin.add("default", textButtonStyle);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        backgroundTexture = Assets.manager.get(Assets.MENU_BACKGROUND, Texture.class); // Get background texture

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        TextButton playButton = new TextButton("Start Game", skin);
        table.add(playButton).width(200).height(50).pad(10).row();

        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreen(game));
                dispose();
            }
        });

        // Add other menu buttons here (e.g., Options, Exit)
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw background first
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        batch.draw(backgroundTexture, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        batch.end();

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        stage.getViewport().update(width, height, true); // Update stage viewport as well
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        batch.dispose(); // Dispose SpriteBatch
        // backgroundTexture is managed by AssetManager, so no need to dispose here
    }
}
