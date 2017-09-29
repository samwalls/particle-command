package engine.common.event;

import engine.common.physics.Contact;

public class CollisionEnterEvent extends Event {

    public Contact contact;

    public CollisionEnterEvent(Contact contact) {
        this.contact = contact;
    }
}
