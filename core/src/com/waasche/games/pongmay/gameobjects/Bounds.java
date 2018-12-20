package com.waasche.games.pongmay.gameobjects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactFilter;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;

public class Bounds implements ContactFilter, ContactListener {
    private Body body;
    private Fixture fixture;

    public Bounds(World world) {
        BodyDef bodyDef = new BodyDef();
        FixtureDef fixtureDef = new FixtureDef();
        bodyDef.type = BodyType.StaticBody;
        bodyDef.position.set(0.0f, -2.5f);
        ChainShape groundShapeBottom = new ChainShape();
        ChainShape groundShapeTop = new ChainShape();
        groundShapeBottom.createChain(new Vector2[]{new Vector2(-10.0f, -2.5f), new Vector2(10.0f, -2.5f)});
        groundShapeTop.createChain(new Vector2[]{new Vector2(-10.0f, 7.5f), new Vector2(10.0f, 7.5f)});
        fixtureDef.shape = groundShapeBottom;
        this.body = world.createBody(bodyDef);
        this.fixture = this.body.createFixture(fixtureDef);
        fixtureDef.shape = groundShapeTop;
        this.body = world.createBody(bodyDef);
        this.fixture = this.body.createFixture(fixtureDef);
        groundShapeTop.dispose();
        groundShapeBottom.dispose();
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
