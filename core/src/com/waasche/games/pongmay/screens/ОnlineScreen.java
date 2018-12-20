package com.waasche.games.pongmay.screens;

import com.badlogic.gdx.Gdx;
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
import com.waasche.games.pongmay.helpers.InputOnlineHandler;

public class ОnlineScreen implements Screen {
    private final int POSITIONITERATIONS = 3;
    private final int PUNTUACION = 5;
    private final float TIMESTEP = 0.016666668f;
    private final int VELOCITYITERATIONS = 8;
    private double angle;
    private Ball ball;
    private SpriteBatch batch;
    private Bounds bounds;
    private OrthographicCamera camera;
    private OrthographicCamera camera2;
    private Box2DDebugRenderer debugRenderer;
    private boolean end;
    private float force;
    private Pong game;
    private GlyphLayout layout;
    private double module;
    private Player player;
    private Player player2;
    private int scoreP1;
    private int scoreP2;
    private int scored;
    private boolean scored_me = false;
    private ShapeRenderer shapeRenderer;
    private Integer side = null;
    private boolean started = false;
    private World world;

    /* renamed from: ОnlineScreen$1 */
    class C04091 implements ContactListener {
        C04091() {
        }

        public void beginContact(Contact contact) {
            Fixture fixtureA = contact.getFixtureA();
            Fixture fixtureB = contact.getFixtureB();
            if (fixtureA == ОnlineScreen.this.player.getFixture() && fixtureB == ОnlineScreen.this.ball.getFixture()) {
                ОnlineScreen.this.angle = ((double) (((((ОnlineScreen.this.ball.getBody().getPosition().y - ОnlineScreen.this.player.getBody().getPosition().y) / ОnlineScreen.this.player.height) * 45.0f) / 360.0f) * 2.0f)) * Math.PI;
            }
            if (fixtureA == ОnlineScreen.this.player2.getFixture() && fixtureB == ОnlineScreen.this.ball.getFixture()) {
                ОnlineScreen.this.angle = ((double) (((180.0f - (((ОnlineScreen.this.ball.getBody().getPosition().y - ОnlineScreen.this.player2.getBody().getPosition().y) / ОnlineScreen.this.player.height) * 45.0f)) / 360.0f) * 2.0f)) * Math.PI;
            }
        }

        public void endContact(Contact contact) {
        }

        public void preSolve(Contact contact, Manifold oldManifold) {
        }

        public void postSolve(Contact contact, ContactImpulse impulse) {
        }
    }

    public ОnlineScreen(Pong game, int init_side) {
        this.game = game;
        float gameHeight = 400.0f / (800.0f / 203.0f);
        this.camera = new OrthographicCamera(203.0f / 10.0f, gameHeight / 10.0f);
        this.camera2 = new OrthographicCamera(203.0f * 1.5f, gameHeight * 1.5f);
        this.world = new World(new Vector2(0.0f, 0.0f), true);
        this.debugRenderer = new Box2DDebugRenderer();
        this.batch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();
        this.layout = new GlyphLayout();
        this.angle = 0.0d;
        this.force = 800.0f;
        this.scoreP1 = 0;
        this.scoreP2 = 0;
        this.scored = 0;
        this.end = false;
        this.side = Integer.valueOf(init_side);
        initObjects();
        initAssets();
        if (this.side.intValue() == 0 && !this.started) {
            this.ball.getBody().applyForce(800.0f, 100.0f, 0.0f, 0.0f, true);
            this.started = true;
        } else if (this.side.intValue() == 1 && !this.started) {
            this.ball.getBody().applyForce(-800.0f, 100.0f, 0.0f, 0.0f, true);
            this.started = true;
        }
        Gdx.input.setInputProcessor(new InputOnlineHandler(game, this, this.side.intValue(), 203.0f / 10.0f, gameHeight / 10.0f));
        createCollisionListener();
    }

    void initObjects() {
        this.player = new Player(this.world, -9.0f, 0.0f, 0.2f, 1.5f);
        this.player2 = new Player(this.world, 9.0f, 0.0f, 0.2f, 1.5f);
        this.bounds = new Bounds(this.world);
        this.ball = new Ball(this.world, 0.0f, 0.0f);
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

    public int isScored() {
        return this.scored;
    }

    public boolean isEnded() {
        return this.end;
    }

    public void restartGame() {
        this.force = 800.0f;
        switch (this.scored) {
            case 1:
                this.ball.getBody().applyForce(-this.force, 100.0f, 0.0f, 0.0f, true);
                break;
            case 2:
                this.ball.getBody().applyForce(this.force, 100.0f, 0.0f, 0.0f, true);
                break;
        }
        this.scored = 0;
    }

    public void show() {
    }

    public void render(float delta) {
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        Gdx.gl.glClear(16384);
        this.world.step(0.016666668f, 8, 3);
        this.camera.update();
        this.camera2.update();
        this.batch.setProjectionMatrix(this.camera2.combined);
        this.shapeRenderer.setProjectionMatrix(this.camera.combined);
        if (this.ball.getBody().getPosition().x + 2.0f < this.player.getBody().getPosition().x && this.scored == 0) {
            this.scoreP2++;
            this.scored = 2;
        } else if (this.ball.getBody().getPosition().x - 2.0f > this.player2.getBody().getPosition().x && this.scored == 0) {
            this.scoreP1++;
            this.scored = 1;
        }
        if (this.scored != 0) {
            this.ball.getBody().setTransform(0.0f, 0.0f, 0.0f);
            this.player.getBody().setTransform(-9.0f, 0.0f, 0.0f);
            this.player2.getBody().setTransform(9.0f, 0.0f, 0.0f);
            this.ball.getBody().setLinearVelocity(0.0f, 0.0f);
        }
        if (this.angle != 0.0d) {
            this.ball.getBody().setLinearVelocity(0.0f, 0.0f);
            this.force += 50.0f;
            this.module = Math.sqrt((double) ((this.force * this.force) + 10000.0f));
            this.ball.getBody().applyForceToCenter((float) (this.module * Math.cos(this.angle)), (float) (this.module * Math.sin(this.angle)), true);
            this.angle = 0.0d;
            AssetsLoader.pong.play();
        }
        if ((this.scoreP1 >= 5 || this.scoreP2 >= 5) && Math.abs(this.scoreP1 - this.scoreP2) > 1) {
            this.end = true;
        }
        this.shapeRenderer.begin(ShapeType.Filled);
        if (this.side.intValue() == 0) {
            this.shapeRenderer.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        } else {
            this.shapeRenderer.setColor(1.0f, 0.0f, 0.0f, 1.0f);
        }
        this.shapeRenderer.rect(this.player.getBody().getPosition().x - this.player.width, this.player.getBody().getPosition().y - this.player.height, this.player.width * 2.0f, this.player.height * 2.0f);
        if (this.side.intValue() == 1) {
            this.shapeRenderer.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        } else {
            this.shapeRenderer.setColor(1.0f, 0.0f, 0.0f, 1.0f);
        }
        this.shapeRenderer.rect(this.player2.getBody().getPosition().x - this.player2.width, this.player2.getBody().getPosition().y - this.player2.height, this.player2.width * 2.0f, this.player2.height * 2.0f);
        Pong pong;
        if (this.side.intValue() == 0) {
            pong = this.game;
            Pong.actionResolver.sendPos(this.player.getBody().getPosition().y, 0.0f);
        } else {
            pong = this.game;
            Pong.actionResolver.sendPos(this.player2.getBody().getPosition().y, 0.0f);
        }
        this.shapeRenderer.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        ShapeRenderer shapeRenderer = this.shapeRenderer;
        float f = this.ball.getBody().getPosition().x;
        float f2 = this.ball.getBody().getPosition().y;
        this.ball.getClass();
        shapeRenderer.circle(f, f2, 0.2f, 32);
        this.shapeRenderer.rect(-0.05f, -50.0f, 0.1f, 100.0f);
        this.shapeRenderer.end();
        this.batch.begin();
        this.layout.setText(AssetsLoader.font, this.scoreP1 + "");
        float width = this.layout.width;
        AssetsLoader.font.draw(this.batch, this.scoreP1 + "", -25.0f - (width / 2.0f), 60.0f);
        AssetsLoader.font.draw(this.batch, this.scoreP2 + "", 25.0f - (width / 2.0f), 60.0f);
        if (!this.end || this.scoreP1 > this.scoreP2) {
            this.batch.end();
        } else {
            this.batch.end();
        }
    }

    private void createCollisionListener() {
        this.world.setContactListener(new C04091());
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

    public void updateGame(float y, float restart) {
        if (restart == 0.0f) {
            try {
                if (this.side.intValue() == 0) {
                    this.player2.getBody().setTransform(this.player2.getBody().getPosition().x, y, 0.0f);
                } else {
                    this.player.getBody().setTransform(this.player.getBody().getPosition().x, y, 0.0f);
                }
            } catch (Exception e) {
                return;
            }
        } else if (restart == 1.0f) {
            restartGame();
            this.scored = (int) y;
        } else if (restart == 9.0f) {
            this.end = true;
        }
        if (this.side.intValue() == 0 && !this.started) {
            this.ball.getBody().applyForce(800.0f, 100.0f, 0.0f, 0.0f, true);
            this.started = true;
        } else if (this.side.intValue() == 1 && !this.started) {
            this.ball.getBody().applyForce(-800.0f, 100.0f, 0.0f, 0.0f, true);
            this.started = true;
        }
        System.out.printf("POSICION RECIBIDA Y : " + String.valueOf(y), new Object[0]);
    }
}
