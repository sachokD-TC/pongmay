package com.waasche.games.pongmay.gameobjects;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactFilter;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;

public class Ball implements ContactFilter, ContactListener {
    public final float RADIUS = 0.2f;
    private Body body;
    private Fixture fixture;
    private World world;

    public Ball(World world, float x, float y) {
        BodyDef bodyDef = new BodyDef();
        FixtureDef fixtureDef = new FixtureDef();
        this.world = world;
        bodyDef.type = BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        CircleShape ballShape = new CircleShape();
        ballShape.setRadius(0.2f);
        fixtureDef.shape = ballShape;
        fixtureDef.friction = 0.0f;
        fixtureDef.restitution = 1.0f;
        this.body = world.createBody(bodyDef);
        this.fixture = this.body.createFixture(fixtureDef);
        ballShape.dispose();
    }

    public Body getBody() {
        return this.body;
    }

    public Fixture getFixture() {
        return this.fixture;
    }

    public void beginContact(Contact contact) {
    }

    public void endContact(Contact contact) {
    }

    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    public void postSolve(Contact contact, ContactImpulse impulse) {
    }

    public boolean shouldCollide(Fixture fixtureA, Fixture fixtureB) {
        return false;
    }
}
