package engine.common.event;

import engine.common.physics.Contact;

public class CollisionStayEvent extends Event {

    public Contact contact;

    public CollisionStayEvent(Contact contact) {
        this.contact = contact;
    }
}
