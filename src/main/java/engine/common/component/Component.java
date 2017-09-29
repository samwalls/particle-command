package engine.common.component;

import engine.common.event.CollisionEnterEvent;
import engine.common.event.RenderEvent;
import engine.common.event.UpdateEvent;
import engine.common.physics.Contact;

import static engine.common.component.GameManager.game;

public class Component implements Drawable, Updatable, Collidable {

    @Override
    public void onRender() { }

    @Override
    public void onUpdate() { }

    @Override
    public void onCollisionEnter(Contact contact) { }

    @Override
    public void onCollisionStay(Contact contact) { }

    @Override
    public void onCollisionExit(GameObject other) { }
}
