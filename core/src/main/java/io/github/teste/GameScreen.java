package io.github.teste;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;


public class GameScreen implements Screen {
    private Game game;
    private SpriteBatch batch;
    private BitmapFont font;
    private Texture alien;
    private Texture heroIMG;
    private Texture bullet;
    private World world;
    private Hero hero;
    private SpeechBubble speechBubble;
    private HeroInputManager heroInputManager;
    private OrthogonalTiledMapRenderer tmr;
    private TiledMap tiledMap;

    private OrthographicCamera camera;
    private OrthographicCamera uiCamera; // Câmera para a HUD (sem zoom)
    private Vector3 mousePosTemp;

    // Dimensões do mapa (em pixels) - defina aqui o tamanho maior que a tela
    private final float mapWidthPx = 3000f;  // 3x a tela
    private final float mapHeightPx = 3000f; // 3x a tela

    public GameScreen(Game game) {
        this.game = game;

        batch = new SpriteBatch();
        font = new BitmapFont();

        // Inicialização da câmara com as dimensões virtuais da janela gráfica
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Câmera UI que não sofre zoom (sempre 1:1)
        uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        mousePosTemp = new Vector3();

        // Recuperação dos assets carregados no
        tiledMap = Assets.manager.get(Assets.MAPA, TiledMap.class);
        tmr = new OrthogonalTiledMapRenderer(tiledMap);

        alien = Assets.manager.get(Assets.ALIEN, Texture.class);

        // ATENÇÃO: Verifique se no LoadingScreen mudou a string para "nave.png" ou manteve "batman.png"
        // Esta string DEVE ser idêntica à que está no Assets.manager.load() do LoadingScreen
        heroIMG = Assets.manager.get(Assets.NAVE, Texture.class);
        bullet = Assets.manager.get(Assets.BULLET, Texture.class);

        com.badlogic.gdx.audio.Sound shootSound = Assets.manager.get(Assets.SOM_TIRO, com.badlogic.gdx.audio.Sound.class);
        com.badlogic.gdx.audio.Sound deathSound = Assets.manager.get(Assets.SOM_MORTE, com.badlogic.gdx.audio.Sound.class);

        hero = new Hero(heroIMG, deathSound);
        // Define o centro do sprite como o ponto de origem para a rotação
        hero.setOriginCenter();
        hero.init(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f);


        Texture dialogTex = Assets.manager.get(Assets.DIALOG_BOX, com.badlogic.gdx.graphics.Texture.class);
        speechBubble = new SpeechBubble(dialogTex, font);
        speechBubble.setMaxWidth(350f); // limite de largura da caixa em pixels, ajuste conforme quiser
        speechBubble.setPadding(12f);

        float centerX = camera.position.x; // usa a posição da câmera (funciona se a câmera se mover)
        float centerY = camera.position.y;
        speechBubble.showCentered("Vamos salvar o mundo!", 3f, centerX, centerY);

        // informa os limites do mundo ao herói para que os clamps usem o tamanho do mapa
        hero.setWorldBounds(mapWidthPx, mapHeightPx);

        // Cria o mundo e passa as dimensões do mapa para controle de spawns e lógica
        world = new World(hero, mapWidthPx, mapHeightPx);

        heroInputManager = new HeroInputManager(hero, world, heroIMG);
        hero.setInputManager(heroInputManager);
        Gdx.input.setInputProcessor(heroInputManager);
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        // Atualiza a lógica do mundo físico do jogo
        world.update(delta);
        speechBubble.update(delta);
        if (speechBubble.isVisible()) {
            float heroX = hero.getX() + hero.getWidth() / 2f;
            float heroY = hero.getY() + hero.getHeight() / 2f;
            speechBubble.updateFollowPosition(heroX, heroY);
        }

        camera.zoom = heroInputManager.getZoomLevel();

        if (hero.isAlive()) {
            mousePosTemp.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(mousePosTemp);

            // Calcula o arco tangente diferencial entre o centro do herói e o vetor do rato
            float centroHeroiX = hero.getX() + hero.getWidth() / 2f;
            float centroHeroiY = hero.getY() + hero.getHeight() / 2f;

            float anguloRadianos = MathUtils.atan2(
                mousePosTemp.y - centroHeroiY,
                mousePosTemp.x - centroHeroiX
            );

            // Converte o radiano resultante para graus angulares exigidos pelo Sprite do LibGDX
            float anguloGraus = anguloRadianos * MathUtils.radDeg;
            hero.setRotation(anguloGraus);

            // Câmera segue o herói
            float heroCenterX = hero.getX() + hero.getWidth() / 2f;
            float heroCenterY = hero.getY() + hero.getHeight() / 2f;
            camera.position.x = heroCenterX;
            camera.position.y = heroCenterY;
            
            // Clamp da câmera aos limites do mapa
            float halfWidth = camera.viewportWidth / 2f;
            float halfHeight = camera.viewportHeight / 2f;
            camera.position.x = MathUtils.clamp(camera.position.x, halfWidth, mapWidthPx - halfWidth);
            camera.position.y = MathUtils.clamp(camera.position.y, halfHeight, mapHeightPx - halfHeight);
        }

        // ===== RENDERIZAR MUNDO (COM ZOOM) =====
        camera.update();

        tmr.setView(camera);
        tmr.render();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        if (world.getHero().isAlive()) {
            world.getHero().draw(batch);
        }

        for (Bullet bullet : world.getActiveBullets()) {
            bullet.draw(batch);
        }

        for (Alien alien : world.getActiveAliens()) {
            alien.draw(batch);
        }

        speechBubble.draw(batch);

        batch.end();

        // ===== RENDERIZAR HUD (SEM ZOOM) =====
        // Atualiza e aplica a câmera UI
        uiCamera.update();
        batch.setProjectionMatrix(uiCamera.combined);

        batch.begin();

        // Renderização do temporizador
        float elapsedTime = world.getElapsedTime();
        int minutes = (int)(elapsedTime / 60f);
        int seconds = (int)(elapsedTime % 60f);
        int milliseconds = (int)((elapsedTime % 1f) * 1000f);
        String timeText = String.format("%02d:%02d:%03d", minutes, seconds, milliseconds);
        font.draw(batch, timeText, Gdx.graphics.getWidth() - 150, Gdx.graphics.getHeight() - 20);

        int lives = world.getHero().getLives();
        String livesText = "Vidas: " + lives;
        font.draw(batch, livesText, 20, Gdx.graphics.getHeight() - 20);

        batch.end();
        // ===== FIM HUD =====
    }

    @Override
    public void resize(int width, int height) {
        // Atualiza o viewport da câmara de jogo
        camera.setToOrtho(false, width, height);

        // Atualiza o viewport da câmara UI
        uiCamera.setToOrtho(false, width, height);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        if (tmr != null) tmr.dispose();
        if (tiledMap != null) tiledMap.dispose();
        batch.dispose();
        font.dispose();
    }
}
