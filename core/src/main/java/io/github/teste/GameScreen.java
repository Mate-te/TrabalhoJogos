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

public class GameScreen implements Screen {
    private Game game;
    private SpriteBatch batch;
    private BitmapFont font;
    private Texture fundo;
    private Texture alien;
    private Texture heroIMG;
    private Texture bullet;
    private World world;
    private Hero hero;
    private HeroInputManager heroInputManager;

    // Câmara ortográfica necessária para converter coordenadas do rato para o mundo
    private OrthographicCamera camera;
    private Vector3 mousePosTemp;

    public GameScreen(Game game) {
        this.game = game;

        batch = new SpriteBatch();
        font = new BitmapFont();

        // Inicialização da câmara com as dimensões virtuais da janela gráfica
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        mousePosTemp = new Vector3();

        // Recuperação dos assets carregados no LoadingScreen
        fundo = Assets.manager.get(Assets.FUNDO, Texture.class);
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

        world = new World(hero);

        heroInputManager = new HeroInputManager(hero, world, heroIMG);
        hero.setInputManager(heroInputManager);
        Gdx.input.setInputProcessor(heroInputManager);
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        // Limpa o ecrã com a cor de fundo
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        // Atualiza a lógica do mundo físico do jogo
        world.update(delta);

        // Lógica de rotação do herói em direção ao cursor do rato
        if (hero.isAlive()) {
            // Captura a posição do rato na tela
            mousePosTemp.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            // Inverte o eixo Y da tela para coincidir com o sistema de coordenadas do mundo (unproject)
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
        }

        // Atualiza as matrizes da câmara e vincula-as ao SpriteBatch antes do início do desenho
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        // Desenha o fundo adaptado ao tamanho da projeção
        batch.draw(fundo, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        if (world.getHero().isAlive()) {
            world.getHero().draw(batch);
        }

        for (Bullet bullet : world.getActiveBullets()) {
            bullet.draw(batch);
        }

        for (Alien alien : world.getActiveAliens()) {
            alien.draw(batch);
        }

        // Renderização do temporizador
        float elapsedTime = world.getElapsedTime();
        int minutes = (int)(elapsedTime / 60f);
        int seconds = (int)(elapsedTime % 60f);
        int milliseconds = (int)((elapsedTime % 1f) * 1000f);
        String timeText = String.format("%02d:%02d:%03d", minutes, seconds, milliseconds);
        font.draw(batch, timeText, Gdx.graphics.getWidth() - 100, Gdx.graphics.getHeight() - 20);

        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        // Atualiza o viewport da câmara caso a janela seja redimensionada
        camera.setToOrtho(false, width, height);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}
