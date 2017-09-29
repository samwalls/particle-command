package engine.common.component;

import engine.common.event.CollisionEnterEvent;
import engine.common.event.RenderEvent;
import engine.common.event.UpdateEvent;
import engine.common.physics.Contact;

import static engine.common.component.GameManager.game;

public class Component implements Drawable, Updatable, Collidable {

    public Component() {
        // map game events to code supplied be the engine user
        game().on(UpdateEvent.class, event -> this.onUpdate());
        game().on(RenderEvent.class, event -> this.onRender());
        game().on(CollisionEnterEvent.class, event -> {
            CollisionEnterEvent e = (CollisionEnterEvent) event;
            // only do something if the event applies to this object
            if (e.contact.A() == this)
                this.onCollisionEnter(e.contact);
        });
    }

    @Override
    public void onRender() { }

    @Override
    public void onUpdate() { }

    @Override
    public void onCollisionEnter(Contact contact) { }
}
