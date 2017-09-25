public abstract class Contact {
  
  protected GameObject contactA, contactB;
  
  protected PVector normal = new PVector();
  protected PVector relativeVelocity = new PVector();
  
  protected float penetration = 0;
  
  public Contact(PVector normal, float penetration, GameObject contactA, GameObject contactB) {
    this.normal = normal;
    this.penetration = penetration;
    this.contactA = contactA;
    this.contactB = contactB;
    relativeVelocity = new PVector(contactB.physics().getVelocity().x, contactB.physics().getVelocity().y);
    relativeVelocity.sub(contactA.physics().getVelocity());
  }
  
  public abstract void resolve();
  
  public GameObject contactA() {
    return contactA;
  }
  
  public GameObject contactB() {
    return contactB;
  }
}