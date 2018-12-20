package com.waasche.games.pongmay.gameobjects;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactFilter;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Player implements ContactFilter, ContactListener {
    private Body body;
    private Fixture fixture;
    public float height;
    public float width;

    public Player(World world, float x, float y, float width, float height) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.StaticBody;
        bodyDef.position.set(x, y);
        this.width = width;
        this.height = height;
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width, height);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        this.body = world.createBody(bodyDef);
        this.fixture = this.body.createFixture(fixtureDef);
        shape.dispose();
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
