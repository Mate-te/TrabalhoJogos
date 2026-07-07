package io.github.teste;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.audio.Music;

public abstract class Assets {
    public static final AssetManager manager = new AssetManager();

    public static final String FUNDO = "fundojpeg.jpeg";
    public static final String NAVE = "naveL.png";
    public static final String NAVELFOGO = "NAVELFOGO.png";
    public static final String ALIEN = "demo.png";
    public static final String ALIEN_SPRITESHEET = "ufo_spritesheet.png";
    public static final String BULLET = "bullet.png";
    public static final String SOM_TIRO = "data/shoot.wav";
    public static final String SOM_MORTE = "data/morte.wav";
    public static final String MUSICA_FUNDO = "data/backgroundMusic.mp3";
    public static final String DIALOG_BOX = "dialogbox.png";
    public static final String MENU_BACKGROUND = "menu_background.jpeg"; // New background asset
    public static final String MAPA = "mapa.tmx";
    public static final String PARTICLE = "particles/particle.png";
    public static final String ENEMY_JF_SPRITESHEET = "jellyfish-large-Sheet.png"; // ou o nome do seu arquivo
    public static final String HEART_FULL = "heart.png";
    public static final String HEART_EMPTY = "background.png";

    public static void loadAll() {
        manager.load(FUNDO, Texture.class);
        manager.load(NAVE, Texture.class);
        manager.load(NAVELFOGO, Texture.class);
        manager.load(ALIEN, Texture.class);
        manager.load(ENEMY_JF_SPRITESHEET, Texture.class);
        manager.load(ALIEN_SPRITESHEET, Texture.class);
        manager.load(BULLET, Texture.class);
        manager.load(DIALOG_BOX, Texture.class);
        manager.load(SOM_TIRO, Sound.class);
        manager.load(SOM_MORTE, Sound.class);
        manager.load(MUSICA_FUNDO, com.badlogic.gdx.audio.Music.class);
        manager.setLoader(TiledMap.class,
            new com.badlogic.gdx.maps.tiled.TmxMapLoader(new com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver()));
        manager.load(MAPA, TiledMap.class);

        manager.load(MENU_BACKGROUND, Texture.class);
        manager.load(PARTICLE, Texture.class);
        manager.load(HEART_FULL, Texture.class);
        manager.load(HEART_EMPTY, Texture.class);
    }

    // Liberta a memória de vídeo e áudio do sistema operativo
    public static void dispose() {
        manager.dispose();
    }
}

