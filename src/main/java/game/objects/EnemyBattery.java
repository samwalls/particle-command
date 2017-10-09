package game.objects;

import engine.common.component.GameObject;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

import static engine.common.component.GameManager.game;

public class EnemyBattery extends GameObject {

    private List<EnemyTurret> turrets;

    private int ammunition;

    public EnemyBattery(int ammunition) {
        turrets = new ArrayList<>();
        this.ammunition = ammunition;
    }

    public void addTurret(EnemyTurret turret) {
        turrets.add(turret);
    }

    public void setupInitialPositions() {
        for (int i = 0; i < turrets.size(); i++) {
            EnemyTurret turret = turrets.get(i);
            turret.setEnemyBattery(this);
            turret.setPosition(new PVector(0, -game().height / 2f - EnemyTurret.TURRET_HEIGHT / 2f));
            // equally distribute the phase
            // TODO
        }
    }

    public int getAmmunition() {
        return ammunition;
    }

    public void deductAmmunition() {
        ammunition--;
    }
}
