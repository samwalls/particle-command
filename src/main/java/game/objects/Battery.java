package game.objects;

import engine.common.component.GameObject;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

import static game.MainComponent.game;

public class Battery extends GameObject {

    private Game game;

    private List<PlayerTurret> turrets;

    private int ammunition;

    public Battery(Game game, List<PlayerTurret> turrets, int ammunition) {
        this.game = game;
        this.turrets = turrets;
        if (this.turrets == null)
            this.turrets = new ArrayList<>();
        this.ammunition = ammunition;
    }

    /**
     * @return the closest turret to the mouse pointer; null if there are no turrets available
     */
    public PlayerTurret closestTurret() {
        PlayerTurret closest = null;
        PVector mouse = new PVector(game().mouseX, game().mouseY);
        float minDistance = Float.POSITIVE_INFINITY;
        for (PlayerTurret t : turrets) {
            float distance = t.globalPosition().sub(mouse).mag();
            if (closest == null || distance < minDistance) {
                closest = t;
                minDistance = distance;
            }
        }
        return closest;
    }

    public void fire() {
        if (!game.isPlaying())
            return;
        PlayerTurret closest = closestTurret();
        if (closest != null && getAmmunition() > 0) {
            // deduct ammunition
            ammunition--;
            // create a projectile
            closest.fire(this, Projectile.DEFAULT_RADIUS + Turret.TURRET_HEIGHT / 2f + 5f, 30f);
        }
    }

    public void addTurret(PlayerTurret turret) {
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

    public int size() {
        return turrets.size();
    }

    public int getAmmunition() {
        return ammunition;
    }
}
