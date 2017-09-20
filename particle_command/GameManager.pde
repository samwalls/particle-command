public class GameManager {
  
  ArrayList<GameObject> gameObjects = new ArrayList();
  
  public void add(GameObject go) {
    gameObjects.add(go);
  }
  
  public ArrayList<GameObject> gameObjects() {
    return gameObjects;
  }
}