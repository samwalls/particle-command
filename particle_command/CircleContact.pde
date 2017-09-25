public class CircleContact extends Contact {
  
  public CircleContact(PVector normal, float penetration, GameObject a, GameObject b) {
    super(normal, penetration, a, b);
  }
  
  @Override
  public void resolve() {
    println("contact: " + penetration + " in direction | " + normal);
    // loosely based on "Game Engine Physiscs Development" - section 7.2.2
    PVector displacement = new PVector(contactA.physics().getPosition().x, contactA.physics().getPosition().y);
    displacement.sub(new PVector(contactB.physics().getPosition().x, contactB.physics().getPosition().y));
    float penetration = (contactA.size() + contactB.size()) - displacement.mag() * 2;
    float totalInverseMass = 0;
    totalInverseMass += contactA.physics().getInverseMass() + contactB.physics().getInverseMass();
    if (penetration > 0 && totalInverseMass > 0) {
      PVector movePerInverseMass = new PVector(normal.x, normal.y);
      movePerInverseMass.mult(-(penetration / totalInverseMass));
      
      // apply penetration resolution
      PVector displacementA = new PVector(movePerInverseMass.x, movePerInverseMass.y);
      displacementA.mult(contactA.physics().getInverseMass());
      
      PVector displacementB = new PVector(movePerInverseMass.x, movePerInverseMass.y);
      displacementB.mult(contactB.physics().getInverseMass());
      
      println("resetting positions due to penetration");
      if (contactA.physics().isKinematic())
        contactA.physics().setPosition(contactA.physics().getPosition().add(displacementA));
      if (contactB.physics().isKinematic())
        contactB.physics().setPosition(contactB.physics().getPosition().add(displacementB));
    }
  }
}