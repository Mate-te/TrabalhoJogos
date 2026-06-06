package io.github.teste;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.audio.Sound;

public abstract class Assets {
    public static final AssetManager manager = new AssetManager();

    public static final String FUNDO = "fundojpeg.jpeg";
    public static final String NAVE = "naveL.png";
    public static final String ALIEN = "demo.png";
    public static final String BULLET = "bullet.png";
    public static final String SOM_TIRO = "data/PIU.wav";
    public static final String SOM_MORTE = "data/morte.wav";
    public static final String DIALOG_BOX = "dialogbox.png";

    public static void loadAll() {
        manager.load(FUNDO, Texture.class);
        manager.load(NAVE, Texture.class);
        manager.load(ALIEN, Texture.class);
        manager.load(BULLET, Texture.class);
        manager.load(DIALOG_BOX, Texture.class);
        manager.load(SOM_TIRO, Sound.class);
        manager.load(SOM_MORTE, Sound.class);
    }

    // Liberta a memória de vídeo e áudio do sistema operativo
    public static void dispose() {
        manager.dispose();
    }
}

