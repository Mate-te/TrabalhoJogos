package io.github.teste;

import com.badlogic.gdx.Game;

public class Main extends Game {

    @Override
    public void create() {
        setScreen(new LoadingScreen(this));

    }

    @Override
    public void dispose() {
        super.dispose();
        Assets.dispose();
    }
}
