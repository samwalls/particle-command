package engine.common.component;

import engine.common.event.CollisionEnterEvent;
import engine.common.event.CollisionExitEvent;
import engine.common.event.CollisionStayEvent;
import engine.common.physics.Contact;
import engine.common.physics.ContactState;

import java.util.*;

import static engine.common.component.GameManager.game;

public class ContactResolver {

    public void resolveAll() {
        for (GameObject g : game().all()) {
            resolveContacts(detectContacts(g));
            checkForCollisionExits(g);
        }
    }

    //******** PRIVATE METHODS ********//

    private void resolveContacts(List<Contact> contacts) {
        for (Contact c : contacts) {
            boolean bothNonTrigger =
                    c.A() != null && !c.A().collider.isTrigger() &&
                    c.B() != null && !c.B().collider.isTrigger();
            // only resolve contacts if both of the objects are non-trigger types
            if (bothNonTrigger)
                c.resolve();
            // emit relevant collision events for the contact
            handleActiveContact(c.A(), c);
        }
    }

    private ArrayList<Contact> detectContacts(GameObject g) {
        // TODO - there are many more efficient ways of doing this, rather than looping O(n^2) style
        ArrayList<Contact> contacts = new ArrayList<>();
        for (GameObject other : game().all()) {
            if (other == g)
                continue;
            Contact[] detected = ColliderComponent.areContacting(g, other);
            if (detected == null || detected.length <= 0)
                continue;
            Collections.addAll(contacts, detected);
        }
        return contacts;
    }

    private void handleActiveContact(GameObject g, Contact contact) {
        // TODO this method may generate multiple enter / stay events per frame, if multiple contacts are resolved for the same coincidence of objects (e.g. collision bounce + interpenetration resolution)
        GameObject other = contact.B();
        Map<GameObject, ContactState> contactStateMap = g.getContactStateMap();
        // add the other object to the map if it doesn't exist (initial contact state NONE)
        if (!contactStateMap.containsKey(other)) {
            contactStateMap.put(other, ContactState.NONE);
        }
        // emit collision events based on the contact state
        switch (contactStateMap.get(other)) {
            case NONE:
                // the objects were not colliding before
                contactStateMap.put(other, ContactState.ENTER);
                game().emit(new CollisionEnterEvent(contact));
                break;
            case ENTER:
                // the objects were colliding in the last update and are colliding now
                contactStateMap.put(other, ContactState.STAY);
                game().emit(new CollisionStayEvent(contact));
                break;
            case STAY:
                // the objects collided indefinitely before beforehand, and are still colliding
                game().emit(new CollisionStayEvent(contact));
                break;
        }
    }

    private void checkForCollisionExits(GameObject g) {
        Map<GameObject, ContactState> contactStateMap = g.getContactStateMap();
        for (Map.Entry<GameObject, ContactState> entry : contactStateMap.entrySet()) {
            ContactState contactState = entry.getValue();
            // if the object is reported to be involved in a collision, check for collision exit
            boolean contactingBefore = contactState == ContactState.ENTER || contactState == ContactState.STAY;
            // if the objects were contacting previously, and are no longer, emit a collisionExitEvent
            if (contactingBefore && ColliderComponent.areContacting(g, entry.getKey()) == null) {
                contactStateMap.put(entry.getKey(), ContactState.NONE);
                game().emit(new CollisionExitEvent(g, entry.getKey()));
            }
        }
    }
}
