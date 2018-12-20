package com.waasche.games.pongmay.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.waasche.games.pongmay.Pong;
import com.waasche.games.pongmay.gameobjects.Player;
import com.waasche.games.pongmay.screens.MainMenu;
import com.waasche.games.pongmay.screens.ОnlineScreen;

public class InputOnlineHandler implements InputProcessor {
    private Pong game;
    private float maxBot = -3.5f;
    private float maxTop = 3.5f;
    private Player player;
    private Player player2;
    private ОnlineScreen screen;
    private int side;
    private Vector3 target;

    public InputOnlineHandler(Pong g, ОnlineScreen screen, int side, float scaleFactorX, float scaleFactorY) {
        this.screen = screen;
        this.target = new Vector3();
        this.side = side;
        this.target.set(-10.0f, 0.0f, 0.0f);
        this.player = screen.getPlayer();
        this.player2 = screen.getPlayer2();
        this.game = g;
        Gdx.input.setCatchBackKey(true);
    }

    public boolean keyDown(int keycode) {
        if (keycode == 4) {
            this.game.setScreen(new MainMenu(this.game));
        }
        return false;
    }

    public boolean keyUp(int keycode) {
        return false;
    }

    public boolean keyTyped(char character) {
        return false;
    }

    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (!(this.screen.isScored() == 0 || this.screen.isEnded())) {
            this.screen.restartGame();
            Pong pong = this.game;
            Pong.actionResolver.sendPos((float) this.screen.isScored(), 1.0f);
        }
        if (this.screen.isEnded()) {
            this.game.setScreen(new MainMenu(this.game));
        }
        return true;
    }

    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    public boolean touchDragged(int screenX, int screenY, int pointer) {
        OrthographicCamera cam = this.screen.getCamera();
        cam.unproject(this.target.set((float) screenX, (float) screenY, 0.0f));
        this.screen.setCamera(cam);
        float value = this.target.y;
        if (this.side == 0) {
            if (this.player.getBody().getPosition().y > this.maxTop) {
                value = this.maxTop;
            } else if (this.player.getBody().getPosition().y < this.maxBot) {
                value = this.maxBot;
            }
            this.player.getBody().setTransform(this.player.getBody().getPosition().x, value, 0.0f);
        } else {
            if (this.player2.getBody().getPosition().y > this.maxTop) {
                value = this.maxTop;
            } else if (this.player2.getBody().getPosition().y < this.maxBot) {
                value = this.maxBot;
            }
            this.player2.getBody().setTransform(this.player2.getBody().getPosition().x, value, 0.0f);
        }
        return true;
    }

    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    public boolean scrolled(int amount) {
        return false;
    }
}
