package com.waasche.games.pongmay.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.waasche.games.pongmay.Pong;

public class MainMenu implements Screen {
    public static boolean hand;
    public static boolean sound;
    private SpriteBatch batch = new SpriteBatch();
    private OrthographicCamera camera = new OrthographicCamera(300.0f,  300.0f);
    private Preferences preferences = Gdx.app.getPreferences("pong");
    private Skin skin = new Skin();
    private Stage stage = new Stage(new FitViewport(800.0f, 400.0f));

    /* renamed from: MainMenu$4 */
    class C05684 extends ChangeListener {
        C05684() {
        }

        public void changed(ChangeEvent event, Actor actor) {
            Gdx.app.exit();
        }
    }

    /* renamed from: MainMenu$6 */
    class C05706 extends ChangeListener {
        C05706() {
        }

        public void changed(ChangeEvent event, Actor actor) {
            if (MainMenu.sound) {
                MainMenu.sound = false;
            } else {
                MainMenu.sound = true;
            }
            MainMenu.this.preferences.putBoolean("sound", MainMenu.sound);
            MainMenu.this.preferences.flush();
        }
    }

    /* renamed from: MainMenu$7 */
    class C05717 extends ChangeListener {
        C05717() {
        }

        public void changed(ChangeEvent event, Actor actor) {
            if (MainMenu.hand) {
                MainMenu.hand = false;
            } else {
                MainMenu.hand = true;
            }
            MainMenu.this.preferences.putBoolean("hand", MainMenu.hand);
            MainMenu.this.preferences.flush();
        }
    }

    public MainMenu(Pong g) {
        com.waasche.games.pongmay.helpers.AssetsLoader.load();
        Gdx.input.setInputProcessor(this.stage);
        Pixmap pixmap = new Pixmap(100, 100, Format.RGBA8888);
        pixmap.setColor(Color.GREEN);
        pixmap.fill();
        this.skin.add("white", new Texture(pixmap));
        pixmap.setColor(Color.RED);
        pixmap.fill();
        this.skin.add("red", new Texture(pixmap));
        this.skin.add("aldrich-rus", com.waasche.games.pongmay.helpers.AssetsLoader.font);
        TextButtonStyle textButtonStyle = new TextButtonStyle();
        textButtonStyle.up = this.skin.newDrawable("white", Color.DARK_GRAY);
        textButtonStyle.down = this.skin.newDrawable("white", Color.DARK_GRAY);
        textButtonStyle.checked = this.skin.newDrawable("white", Color.BLUE);
        textButtonStyle.over = this.skin.newDrawable("white", Color.LIGHT_GRAY);
        textButtonStyle.font = this.skin.getFont("aldrich-rus");
        textButtonStyle.font.getData().setScale(0.75f);
        this.skin.add("default", textButtonStyle);
        TextButtonStyle exitButtonStyle = new TextButtonStyle();
        exitButtonStyle.up = this.skin.newDrawable("red", Color.DARK_GRAY);
        exitButtonStyle.down = this.skin.newDrawable("red", Color.DARK_GRAY);
        exitButtonStyle.checked = this.skin.newDrawable("red", Color.BLUE);
        exitButtonStyle.over = this.skin.newDrawable("red", Color.LIGHT_GRAY);
        exitButtonStyle.font = this.skin.getFont("aldrich-rus");
        this.skin.add("exit", exitButtonStyle);
        new LabelStyle().font = this.skin.getFont("aldrich-rus");
        TextButton multiCreateButton = new TextButton( com.waasche.games.pongmay.resources.Text.get("Player1"), textButtonStyle);
        multiCreateButton.setBounds(450.0f, 310.0f, 250.0f, 80.0f);
        TextButton multiJoinButton = new TextButton(com.waasche.games.pongmay.resources.Text.get("Player2"), textButtonStyle);
        multiJoinButton.setBounds(450.0f, 210.0f, 250.0f, 80.0f);
        TextButton wallButton = new TextButton(com.waasche.games.pongmay.resources.Text.get("WALL"), textButtonStyle);
        wallButton.setBounds(450.0f, 110.0f, 250.0f, 80.0f);
        TextButton exitButton = new TextButton(com.waasche.games.pongmay.resources.Text.get("EXIT"), exitButtonStyle);
        exitButton.setBounds(450.0f, 10.0f, 250.0f, 80.0f);
        ImageButton rankingButtonWallMode = new ImageButton(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("trophy.png")))));
        rankingButtonWallMode.setBounds(720.0f, 110.0f, 50.0f, 80.0f);
        ImageButton soundButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("sound_on.png")))), null, new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("sound_off.png")))));
        sound = this.preferences.getBoolean("sound", true);
        if (sound) {
            soundButton.setChecked(false);
        } else {
            soundButton.setChecked(true);
        }
        soundButton.setBounds(50.0f, 330.0f, 50.0f, 50.0f);
        ImageButton handButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("left-hand.png")))), null, new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("right-hand.png")))));
        hand = this.preferences.getBoolean("hand", false);
        if (hand) {
            handButton.setChecked(true);
        } else {
            handButton.setChecked(false);
        }
        handButton.setBounds(50.0f, 20.0f, 50.0f, 50.0f);
        this.stage.addActor(multiCreateButton);
        this.stage.addActor(multiJoinButton);
        this.stage.addActor(wallButton);
        this.stage.addActor(exitButton);
        this.stage.addActor(rankingButtonWallMode);
        this.stage.addActor(soundButton);
        this.stage.addActor(handButton);
        final Pong pong = g;
        multiCreateButton.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                pong.startOnlineGame(0);
            }
        });
//        pong = g;
        multiJoinButton.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                pong.startOnlineGame(1);
            }
        });
//        pong = g;
        wallButton.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                pong.setScreen(new GameWallScreen(pong, false));
            }
        });
        exitButton.addListener(new C05684());
//        pong = g;
        rankingButtonWallMode.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
//                Pong pong = pong;
                Pong.actionResolver.submitScoreWallMode(MainMenu.this.preferences.getInteger("highscore", 0));
//                pong = pong;
                Pong.actionResolver.displayLeaderboardWallMode();
            }
        });
        soundButton.addListener(new C05706());
        handButton.addListener(new C05717());
        //Pong.actionResolver.automaticSignIn();
    }

    public void show() {
    }

    public void render(float delta) {
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        Gdx.gl.glClear(16384);
        this.stage.act(Math.min(Gdx.graphics.getDeltaTime(), 0.033333335f));
        this.stage.draw();
        this.camera.update();
        this.batch.setProjectionMatrix(this.camera.combined);
        this.batch.begin();
        com.waasche.games.pongmay.helpers.AssetsLoader.font.draw(this.batch, (CharSequence) "PONG", -90.0f, 10.0f);
        this.batch.end();
    }

    public void resize(int width, int height) {
        this.stage.getCamera().update();
        this.stage.getViewport().update(width, height, false);
    }

    public void pause() {
    }

    public void resume() {
    }

    public void hide() {
    }

    public void dispose() {
        com.waasche.games.pongmay.helpers.AssetsLoader.dispose();
        this.stage.dispose();
        this.skin.dispose();
    }

    public static boolean getSound() {
        return sound;
    }
}
