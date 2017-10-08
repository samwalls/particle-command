package game.objects;

import engine.common.component.GameObject;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

import static game.MainComponent.game;

public class Battery extends GameObject {

    private List<Turret> turrets;

    public Battery(List<Turret> turrets) {
        this.turrets = turrets;
        if (this.turrets == null)
            this.turrets = new ArrayList<>();
    }

    /**
     * @return the closest turret to the mouse pointer; null if there are no turrets available
     */
    public Turret closestTurret() {
        Turret closest = null;
        PVector mouse = new PVector(game().mouseX, game().mouseY);
        float minDistance = Float.POSITIVE_INFINITY;
        for (Turret t : turrets) {
            float distance = t.globalPosition().sub(mouse).mag();
            if (closest == null || distance < minDistance) {
                closest = t;
                minDistance = distance;
            }
        }
        return closest;
    }

    public void addTurret(Turret turret) {
        turrets.add(turret);
    }

    public void removeTurret(Turret turret) {
        turrets.remove(turret);
    }

    public void clear() {
        turrets.clear();
    }

    public boolean contains(GameObject turret) {
        return turrets.contains(turret);
    }
}
