package engine.common.component;

import engine.common.event.CollisionEnterEvent;
import engine.common.event.RenderEvent;
import engine.common.event.UpdateEvent;
import engine.common.physics.ColliderType;
import engine.common.physics.Contact;
import engine.common.physics.PhysicsComponent;
import processing.core.PVector;

import static engine.common.component.GameManager.game;

public class GameObject extends Component {

    private static final int MAX_SIZE = 100;

    protected Transform transform = new Transform();

    protected PhysicsComponent physics;

    protected ColliderType colliderType;

    public GameObject(ColliderType colliderType) {
        super();
        physics = new PhysicsComponent(transform);
        this.colliderType = colliderType;
        game().add(this);
    }

    public GameObject() {
        this(ColliderType.NONE);
    }

    /**
     * Called during every update step. Should be overriden if one wishes to implement extra update behaviour.
     */
    @Override
    public void onUpdate() {}

    /**
     * Called during every render step. Should be overriden if one wishes to implement extra rendering behaviour.
     */
    @Override
    public void onRender() {}

    /**
     * Called before a collision contact is resolved. Should be overriden if one wishes to implement extra behaviour upon
     * entering a collision.
     */
    @Override
    public void onCollisionEnter(Contact contact) {}

    //******** PUBLIC METHODS ********//

    // public property based on the mass of the object
    public float size() {
        return game().max(1, game().min(0.5f * physics().getMass(), MAX_SIZE));
    }

    // public access to this object's physics
    public PhysicsComponent physics() {
        return physics;
    }

    public ColliderType colliderType() {
        return colliderType;
    }

    //******** PROTECTED METHODS ********//

    protected void renderRelativeLine(PVector v) {
        game().line(physics().getPosition().x, physics().getPosition().y, physics().getPosition().x + v.x, physics().getPosition().y + v.y);
    }
}