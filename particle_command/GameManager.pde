public class GameManager {
  
  ArrayList<GameObject> gameObjects = new ArrayList();
  
  public void add(GameObject go) {
    gameObjects.add(go);
  }
  
  public ArrayList<GameObject> gameObjects() {
    return gameObjects;
  }
  
  public void updateAll() {
    for (GameObject g : gameObjects) {
      g.update();
      for (Contact c : detectCollisions(g)) {
        // TODO implement onCollisionEnter and onCollisionExit - will require cache of collisions for each gameObject, somewhere...
        c.resolve();
      }
      g.physics().integrate();
      g.render();
    }
  }
  
  private ArrayList<Contact> detectCollisions(GameObject g) {
    // TODO - there are many ways of doing this
    ArrayList<Contact> collisions = new ArrayList();
    for (GameObject other : gameObjects) {
      if (other == g)
        continue;
      Contact contact = areContacting(g, other);
      if (contact != null) {
        collisions.add(new CircleContact(contact.normal, contact.penetration, contact.contactA, contact.contactB));
        collisions.add(contact);
      }
    }
    return collisions;
  }
}