package com.waasche.games.pongmay;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.I18NBundle;
import com.waasche.games.pongmay.helpers.ActionResolver;
import com.waasche.games.pongmay.multiplayer.Multiplayer;
import com.waasche.games.pongmay.screens.MainMenu;
import com.waasche.games.pongmay.screens.ОnlineScreen;

import java.util.Locale;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;

public class Pong extends Game implements ApplicationListener {
    public static final int HOST_ID = 0;
    public static ActionResolver actionResolver;
    public static final String PONG_BUNDLE_FILE_NAME = "i18n/pongbundle";
    private ОnlineScreen ОnlineScreen;
    public I18NBundle pongBundle;
    public AssetManager assetManager;
    private static Pong pongInstance;
    private Multiplayer multiplayer;

    public Pong(ActionResolver actionResolver, Multiplayer multiplayer) {
        this.multiplayer = multiplayer;
        this.actionResolver = actionResolver;
        pongInstance = this;
    }

    public static Pong getInstance() {
        return pongInstance;
    }

    public void create() {
        assetManager = new AssetManager();
        onLoad();
        setScreen(new MainMenu(this));
    }

    public void onLoad() {
        pongBundle = loadTexts();
    }

    public Multiplayer getMultiplayer() {
        return multiplayer;
    }

    private I18NBundle loadTexts() {
        String language = Locale.getDefault().getLanguage();
        if (language.isEmpty()) return assetManager.get(PONG_BUNDLE_FILE_NAME);
        else return I18NBundle.createBundle(Gdx.files.internal(PONG_BUNDLE_FILE_NAME), new Locale(language));
    }

    public void dispose() {
        super.dispose();
    }

    public void render() {
        super.render();
    }

    public void resize(int width, int height) {
        super.resize(width, height);
    }

    public void pause() {
        super.pause();
    }

    public void resume() {
        super.resume();
    }

    public void startOnlineGame(final int side) {
        final Pong pong = this;
        Gdx.app.postRunnable(new Runnable() {
            public void run() {
                if (side == 0) {
                    multiplayer.createRoom();
                    delay(20000);
                    multiplayer.joinRoom();
                }
                Pong.this.ОnlineScreen = new ОnlineScreen(pong, side);
                Pong.this.setScreen(Pong.this.ОnlineScreen);

            }
        });
    }

    public ОnlineScreen getOnlineGame() {
        return this.ОnlineScreen;
    }
}
