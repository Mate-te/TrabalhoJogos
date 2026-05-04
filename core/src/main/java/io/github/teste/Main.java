package io.github.teste;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;

public class Main extends Game {
    private AssetManager manager;

    @Override
    public void create() {
        manager = new AssetManager();
        setScreen(new LoadingScreen(this, manager));
    }

    @Override
    public void dispose() {
        super.dispose();
        manager.dispose();
    }
}
