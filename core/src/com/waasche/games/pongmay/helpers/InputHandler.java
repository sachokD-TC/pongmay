package com.waasche.games.pongmay.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.waasche.games.pongmay.Pong;
import com.waasche.games.pongmay.gameobjects.Player;
import com.waasche.games.pongmay.screens.GameScreen;
import com.waasche.games.pongmay.screens.MainMenu;

public class InputHandler implements InputProcessor {
    private Pong game;
    private Player player;
    private Player player2;
    private GameScreen screen;
    private Vector3 target = new Vector3();

    public InputHandler(Pong g, GameScreen screen, float scaleFactorX, float scaleFactorY) {
        this.screen = screen;
        this.target.set(-10.0f, 0.0f, 0.0f);
        this.player = screen.getPlayer();
        this.player2 = screen.getPlayer2();
        this.game = g;
        Gdx.input.setCatchBackKey(true);
    }

    public boolean keyDown(int keycode) {
        if (keycode == 4) {
            if (this.screen.isPaused()) {
                this.game.setScreen(new MainMenu(this.game));
            } else {
                this.screen.pauseGame();
            }
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
        }
        if (this.screen.isEnded()) {
            this.game.setScreen(new MainMenu(this.game));
        }
        if (this.screen.isPaused()) {
            this.screen.resumeGame();
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
        if (!this.screen.isMultiplayer()) {
            this.player.getBody().setTransform(this.target.x, this.target.y, 0.0f);
            if (this.player.getBody().getPosition().y > 3.5f) {
                this.player.getBody().setTransform(this.target.x, 3.5f, 0.0f);
            } else if (this.player.getBody().getPosition().y < -3.5f) {
                this.player.getBody().setTransform(this.target.x, -3.5f, 0.0f);
            }
        } else if (screenX < Gdx.graphics.getWidth() / 2) {
            this.player.getBody().setTransform(this.target.x, this.target.y, 0.0f);
            if (this.player.getBody().getPosition().y > 3.5f) {
                this.player.getBody().setTransform(this.target.x, 3.5f, 0.0f);
            } else if (this.player.getBody().getPosition().y < -3.5f) {
                this.player.getBody().setTransform(this.target.x, -3.5f, 0.0f);
            }
        } else {
            this.player2.getBody().setTransform(this.target.x, this.target.y, 0.0f);
            if (this.player2.getBody().getPosition().y > 3.5f) {
                this.player2.getBody().setTransform(this.target.x, 3.5f, 0.0f);
            } else if (this.player2.getBody().getPosition().y < -3.5f) {
                this.player2.getBody().setTransform(this.target.x, -3.5f, 0.0f);
            }
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
