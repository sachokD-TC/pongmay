package com.waasche.games.pongmay.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.waasche.games.pongmay.Pong;
import com.waasche.games.pongmay.gameobjects.Ball;
import com.waasche.games.pongmay.gameobjects.Bounds;
import com.waasche.games.pongmay.gameobjects.Player;
import com.waasche.games.pongmay.helpers.AssetsLoader;
import com.waasche.games.pongmay.helpers.InputHandlerWall;

public class GameWallScreen implements Screen {
    private final int POSITIONITERATIONS = 3;
    private final float TIMESTEP = 0.016666668f;
    private final int VELOCITYITERATIONS = 8;
    private double angle;
    private Ball ball;
    private SpriteBatch batch;
    private SpriteBatch batch2;
    private Bounds bounds;
    private OrthographicCamera camera;
    private OrthographicCamera camera2;
    private OrthographicCamera camera3;
    private Box2DDebugRenderer debugRenderer;
    private boolean end;
    private float force;
    private boolean hand;
    private int highscore;
    private GlyphLayout layout;
    private double module;
    private boolean new_highscore = false;
    private boolean paused;
    private Player player;
    private Player player2;
    private Preferences preferences;
    private int score;
    private int scored;
    private ShapeRenderer shapeRenderer;
    private World world;

    /* renamed from: GameWallScreen$1 */
    class C04081 implements ContactListener {
        C04081() {
        }

        public void beginContact(Contact contact) {
            Fixture fixtureA = contact.getFixtureA();
            Fixture fixtureB = contact.getFixtureB();
            if (GameWallScreen.this.hand) {
                if (fixtureA == GameWallScreen.this.player2.getFixture() && fixtureB == GameWallScreen.this.ball.getFixture()) {
                    GameWallScreen.this.angle = ((double) (((((GameWallScreen.this.ball.getBody().getPosition().y - GameWallScreen.this.player2.getBody().getPosition().y) / GameWallScreen.this.player2.height) * 45.0f) / 360.0f) * 2.0f)) * Math.PI;
                }
                if (fixtureA == GameWallScreen.this.player.getFixture() && fixtureB == GameWallScreen.this.ball.getFixture()) {
                    GameWallScreen.this.angle = 9000.0d;
                }
            } else {
                if (fixtureA == GameWallScreen.this.player.getFixture() && fixtureB == GameWallScreen.this.ball.getFixture()) {
                    GameWallScreen.this.angle = ((double) (((((GameWallScreen.this.ball.getBody().getPosition().y - GameWallScreen.this.player.getBody().getPosition().y) / GameWallScreen.this.player.height) * 45.0f) / 360.0f) * 2.0f)) * Math.PI;
                }
                if (fixtureA == GameWallScreen.this.player2.getFixture() && fixtureB == GameWallScreen.this.ball.getFixture()) {
                    GameWallScreen.this.angle = 9000.0d;
                }
            }
            if (fixtureA == GameWallScreen.this.player2.getFixture() && fixtureB == GameWallScreen.this.ball.getFixture()) {
                GameWallScreen.this.score = GameWallScreen.this.score + 1;
            }
        }

        public void endContact(Contact contact) {
        }

        public void preSolve(Contact contact, Manifold oldManifold) {
        }

        public void postSolve(Contact contact, ContactImpulse impulse) {
        }
    }

    public GameWallScreen(Pong g, boolean multiplayer) {
        float gameHeight = 400.0f / (800.0f / 203.0f);
        this.camera = new OrthographicCamera(203.0f / 10.0f, gameHeight / 10.0f);
        this.camera2 = new OrthographicCamera(203.0f * 1.5f, gameHeight * 1.5f);
        this.camera3 = new OrthographicCamera(203.0f * 3.0f, gameHeight * 3.0f);
        this.world = new World(new Vector2(0.0f, 0.0f), true);
        this.debugRenderer = new Box2DDebugRenderer();
        this.batch = new SpriteBatch();
        this.batch2 = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();
        this.layout = new GlyphLayout();
        this.angle = 0.0d;
        this.force = 800.0f;
        this.score = 0;
        this.scored = 0;
        this.end = false;
        this.paused = false;
        this.preferences = Gdx.app.getPreferences("pong");
        this.highscore = this.preferences.getInteger("highscore", 0);
        initObjects();
        initAssets();
        Gdx.input.setInputProcessor(new InputHandlerWall(g, this, 203.0f / 10.0f, gameHeight / 10.0f));
        createCollisionListener();
    }

    void initObjects() {
        this.hand = this.preferences.getBoolean("hand", true);
        if (this.hand) {
            this.player = new Player(this.world, 9.0f, 0.0f, 0.2f, 1.5f);
            this.player2 = new Player(this.world, -10.0f, 0.0f, 0.2f, 20.0f);
        } else {
            this.player = new Player(this.world, -9.0f, 0.0f, 0.2f, 1.5f);
            this.player2 = new Player(this.world, 10.0f, 0.0f, 0.2f, 20.0f);
        }
        this.bounds = new Bounds(this.world);
        this.ball = new Ball(this.world, 0.0f, 0.0f);
        if (this.hand) {
            this.ball.getBody().applyForce(800.0f, 100.0f, 0.0f, 0.0f, true);
        } else {
            this.ball.getBody().applyForce(-800.0f, 100.0f, 0.0f, 0.0f, true);
        }
        this.module = Math.sqrt((double) ((this.force * this.force) + 10000.0f));
    }

    void initAssets() {
        AssetsLoader.load();
    }

    public OrthographicCamera getCamera() {
        return this.camera;
    }

    public void setCamera(OrthographicCamera cam) {
        this.camera = cam;
    }

    public Player getPlayer() {
        return this.player;
    }

    public Player getPlayer2() {
        return this.player2;
    }

    public boolean isEnded() {
        return this.end;
    }

    public boolean isPaused() {
        return this.paused;
    }

    public void pauseGame() {
        this.paused = true;
    }

    public void resumeGame() {
        this.paused = false;
    }

    public void show() {
    }

    public void render(float delta) {
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        Gdx.gl.glClear(16384);
        if (this.paused) {
            this.world.step(0.0f, 8, 3);
        } else {
            this.world.step(0.016666668f, 8, 3);
        }
        this.camera.update();
        this.camera2.update();
        this.camera3.update();
        this.batch.setProjectionMatrix(this.camera2.combined);
        this.batch2.setProjectionMatrix(this.camera3.combined);
        this.shapeRenderer.setProjectionMatrix(this.camera.combined);
        if (this.hand) {
            if (this.ball.getBody().getPosition().x - 2.0f > this.player.getBody().getPosition().x && this.scored == 0) {
                this.end = true;
                if (this.score > this.highscore) {
                    this.preferences.putInteger("highscore", this.score);
                    this.preferences.flush();
                    this.highscore = this.score;
                    this.new_highscore = true;
                }
            }
        } else if (this.ball.getBody().getPosition().x + 2.0f < this.player.getBody().getPosition().x && this.scored == 0) {
            this.end = true;
            if (this.score > this.highscore) {
                this.preferences.putInteger("highscore", this.score);
                this.preferences.flush();
                this.highscore = this.score;
                this.new_highscore = true;
            }
        }
        if (this.angle == 9000.0d) {
            if (com.waasche.games.pongmay.screens.MainMenu.getSound()) {
                AssetsLoader.pong.play();
            }
            this.angle = 0.0d;
        }
        if (this.angle != 0.0d) {
            this.ball.getBody().setLinearVelocity(0.0f, 0.0f);
            this.force += 50.0f;
            this.module = Math.sqrt((double) ((this.force * this.force) + 10000.0f));
            this.ball.getBody().applyForceToCenter((float) (this.module * Math.cos(this.angle)), (float) (this.module * Math.sin(this.angle)), true);
            this.angle = 0.0d;
            if (com.waasche.games.pongmay.screens.MainMenu.getSound()) {
                AssetsLoader.pong.play();
            }
        }
        this.shapeRenderer.begin(ShapeType.Filled);
        this.shapeRenderer.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        this.shapeRenderer.rect(this.player.getBody().getPosition().x - this.player.width, this.player.getBody().getPosition().y - this.player.height, this.player.width * 2.0f, this.player.height * 2.0f);
        this.shapeRenderer.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        this.shapeRenderer.rect(this.player2.getBody().getPosition().x - this.player2.width, this.player2.getBody().getPosition().y - this.player2.height, this.player2.width * 2.0f, this.player2.height * 2.0f);
        this.shapeRenderer.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        ShapeRenderer shapeRenderer = this.shapeRenderer;
        float f = this.ball.getBody().getPosition().x;
        float f2 = this.ball.getBody().getPosition().y;
        this.ball.getClass();
        shapeRenderer.circle(f, f2, 0.2f, 32);
        this.shapeRenderer.rect(-0.05f, -50.0f, 0.1f, 100.0f);
        this.shapeRenderer.end();
        this.batch.begin();
        this.layout.setText(AssetsLoader.font, this.score + "");
        AssetsLoader.font.draw(this.batch, this.score + "", (-this.layout.width) / 2.0f, 60.0f);
        if (this.paused) {
            AssetsLoader.font.draw(this.batch, (CharSequence) com.waasche.games.pongmay.resources.Text.get("PAUSE"), -45.0f, 0.0f);
        }
        this.batch.end();
        this.batch2.begin();
        if (this.end) {
            if (this.new_highscore) {
                AssetsLoader.font.draw(this.batch2, com.waasche.games.pongmay.resources.Text.get("HighScore") + this.highscore, -150.0f, 5.0f);
            } else {
                AssetsLoader.font.draw(this.batch2, "GAME  FINISHED \n Highscore   " + this.highscore, -100.0f, 5.0f);
            }
        }
        this.batch2.end();
    }

    private void createCollisionListener() {
        this.world.setContactListener(new C04081());
    }

    public void resize(int width, int height) {
    }

    public void pause() {
    }

    public void resume() {
    }

    public void hide() {
    }

    public void dispose() {
        AssetsLoader.dispose();
        this.world.dispose();
        this.debugRenderer.dispose();
        this.shapeRenderer.dispose();
        this.batch.dispose();
    }
}
