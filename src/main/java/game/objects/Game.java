package game.objects;

import engine.common.component.GameObject;
import game.RenderLayer;
import processing.core.PVector;

import java.util.Arrays;
import java.util.stream.Collectors;

import static game.MainComponent.game;

/**
 * Responsible for setting up, re-setting and ending the game.
 */
public class Game extends GameObject {

    // parameters for difficulty configuration (with default values)

    private int remainingBatteryScoreBonus = 1000;
    private int remainingAmmunitionScoreBonus = 100;
    private int directHitScoreBonus = 100;
    private int topEvasionScoreBonus = 50;
    private int sideEvasionScoreBonus = 20;

    private int playerTurretCount = 4;
    private int enemyTurretCount = 1;
    private float enemyTurretFireProbability = 0.1f;

    private int playerAmmunition = 10;
    private int enemyAmmunition = 13;

    private static final float PLAYER_AMMUNITION_INCREASE = 5;
    private static final float ENEMY_AMMUNITION_INCREASE = 7;
    // 1% more likely to fire per firing opportunity than before
    private static final float ENEMY_FIRE_PROBABILITY_INCREASE = 0.01f;
    // i.e. increase the number of enemy turrets by 1 every 5 waves
    private static final int ENEMY_TURRET_COUNT_INCREASE_WAVES = 3;
    private static final int ENEMY_TURRET_COUNT_INCREASE = 1;

    // game objects making up the game

    private GameObject parent;
    private Universe universe;
    private PlayArea playArea;
    private Floor floor;
    private Battery playerBattery;
    private EnemyBattery enemyBattery;
    private Pointer mousePointer;
    private StarField starField;

    /**
     * The player's current score.
     */
    private int score = 0;

    /**
     * Miscellaneous bonuses applied from evasions etc.
     */
    private int miscBonus = 0;

    /**
     * The player's score in the previous wave.
     */
    private int scoreBefore = 0;

    private int remainingTurretsBefore = 0;

    private int remainingAmmunitionBefore = 0;

    /**
     * The current wave.
     */
    private int wave = 0;

    /**
     * True if the game is in play currently.
     */
    private boolean playing;

    /**
     * True if the player intends to start a wave.
     */
    private boolean shouldStart = false;

    /**
     * For use in the title screen and other stat screens.
     */
    private int infoScreenDelta;

    public Game() {
        // set rendering layers according to the order in the RenderLayer enum
        game().setRenderingLayers(Arrays.stream(RenderLayer.values()).map(RenderLayer::toString).collect(Collectors.toList()));
        reset();
        infoScreenDelta = 0;
    }

    @Override
    public void onUpdate() {
        // if we're waiting to play
        if (!playing) {
            // wait until shouldStart is true
            if (!shouldStart)
                return;
            playing = true;
            shouldStart = false;
            wave++;
        }
        if (gameOver()) {
            // TODO display game over screen
            playing = false;
        } else if (levelOver()) {
            scoreBefore = score;
            remainingAmmunitionBefore = playerBattery.getAmmunition();
            remainingTurretsBefore = playerBattery.size();
            applyScoreBonuses();
            increaseDifficultyConfiguration();
            playing = false;
            reset();
            // TODO show stats of last wave, and wait for button press to continue
        }
    }

    @Override
    public String renderLayer() {
        return RenderLayer.TEXT.toString();
    }

    @Override
    public void onRender() {
        if (!playing) {
            if (gameOver())
                renderGameOverScreen();
            else if(wave == 0)
                renderGameStartScreen();
            else
                renderRoundEndScreen();
        } else {
            renderStats();
        }
    }

    public void onClick() {
        if (playing) {
            playerBattery.fire();
        } else {
            if (gameOver())
                game().exit();
            // click counts as intention to start wave
            shouldStart = true;
        }
    }

    public void onKeyPress() {
        // any key press counts as intention to start wave (if not game over)
        if (!playing) {
            if (gameOver())
                game().exit();
            else
                shouldStart = true;
        }
    }

    public boolean isPlaying() {
        return playing;
    }

    public void addSideEvasionScore(boolean right, float y) {
        miscBonus += sideEvasionScoreBonus;
        float x = right ? game().width -20f : 20f;
        new Toast("side evasion! + " + sideEvasionScoreBonus, (int)(game().frameRate * 1f), new PVector(x, y), 20, right ? game().RIGHT : game().LEFT);
    }

    public void addTopEvasionScore(float x) {
        miscBonus += topEvasionScoreBonus;
        float y = 40f;
        new Toast("top evasion! + " + topEvasionScoreBonus, (int)(game().frameRate * 1f), new PVector(x, y), 20, game().CENTER);
    }

    public void addDirectHitScore(float x, float y) {
        miscBonus += directHitScoreBonus;
        new Toast("direct hit! + " + directHitScoreBonus, (int)(game().frameRate * 1f), new PVector(x, y), 20, game().CENTER);
    }

    private boolean levelOver() {
        return enemyBattery.getAmmunition() <= 0 && enemyBattery.destroyedProjectiles() >= enemyAmmunition;
    }

    private boolean gameOver() {
        return playerBattery.size() <= 0;
    }

    private void applyScoreBonuses() {
        // remaining ammunition awards points
        score += playerBattery.getAmmunition() * remainingAmmunitionScoreBonus;
        // remaining batteries award points
        score += playerBattery.size() * remainingBatteryScoreBonus;
        score += miscBonus;
    }

    private void increaseDifficultyConfiguration() {
        playerAmmunition += PLAYER_AMMUNITION_INCREASE;
        enemyAmmunition += ENEMY_AMMUNITION_INCREASE;
        enemyTurretFireProbability += ENEMY_FIRE_PROBABILITY_INCREASE;
        if (wave % ENEMY_TURRET_COUNT_INCREASE_WAVES == 0)
            enemyTurretCount += ENEMY_TURRET_COUNT_INCREASE;
        // carry on the number of batteries from before
        playerTurretCount = playerBattery.size();
    }

    /**
     * Reset using various difficulty parameters.
     */
    private void reset() {
        // remove all GameObjects that aren't this
        game().all().forEach(g -> {
            if (g != this)
                g.destroy();
        });
        // set up everything
        universe = new Universe();
        playArea = new PlayArea();
        floor = new Floor();
        floor.setPosition(new PVector(0, game().height / 2f));
        mousePointer = new Pointer();
        parent = new GameObject();
        parent.setPosition(new PVector(game().width / 2f, game().height / 2f));
        parent.addChild(universe);
        parent.addChild(playArea);
        parent.addChild(floor);
        parent.addChild(mousePointer);
        starField = new StarField();
        setupBatteries();
        playing = false;
        miscBonus = 0;
    }

    private void setupBatteries() {
        setupPlayerBatteries();
        setupEnemyBatteries();
    }

    /**
     * Set up arbitrary number of turrets - evenly distributed along the floor - and add them to the battery.
     */
    private void setupPlayerBatteries() {
        playerBattery = new Battery(this, null, playerAmmunition);
        parent.addChild(playerBattery);
        for (int i = 0; i < playerTurretCount; i++) {
            PlayerTurret turret = new PlayerTurret(playArea, playerBattery);
            // distribute the turrets evenly among the ground
            float x = -game().width / 2f + (i + 0.5f) * (game().width / playerTurretCount);
            float y = game().height / 2f - Floor.FLOOR_HEIGHT / 2f - Turret.TURRET_HEIGHT / 2f;
            turret.setPosition(new PVector(x, y));
            parent.addChild(turret);
            playerBattery.addTurret(turret);
        }
    }

    /**
     * Set up arbitrary number of enemy turrets - evenly distributed along the floor - and add them to the battery.
     */
    private void setupEnemyBatteries() {
        enemyBattery = new EnemyBattery(enemyAmmunition);
        parent.addChild(enemyBattery);
        for (int i = 0; i < enemyTurretCount; i++) {
            EnemyTurret turret = new EnemyTurret(this, playArea, playerBattery, enemyTurretFireProbability);
            parent.addChild(turret);
            enemyBattery.addTurret(turret);
        }
        enemyBattery.setupInitialPositions();
    }

    private void renderGameStartScreen() {
        game().pushMatrix();
        game().pushStyle();
        game().textAlign(game().CENTER);
        game().textSize(80f);
        float redPhase = infoScreenDelta * game().PI / (game().frameRate * 5);
        float greenPhase = infoScreenDelta * game().PI / (game().frameRate * 3);
        float bluePhase = infoScreenDelta * game().PI / (game().frameRate * 6);
        game().fill(255 * game().sin(redPhase) + 127, 255 * game().sin(greenPhase) + 127, 255 * game().sin(bluePhase) + 127);
        game().text("Particle Command", game().width / 2f, game().height / 2f - 100f);
        game().textSize(50f);
        game().fill(255, 255, 255);
        game().text("press almost any button to start the game!", game().width / 2f, game().height / 2f);
        game().popStyle();
        game().popMatrix();
        infoScreenDelta++;
    }

    private void renderRoundEndScreen() {
        game().pushMatrix();
        game().pushStyle();
        game().textAlign(game().CENTER);
        game().textSize(80f);
        float phase = infoScreenDelta * game().PI / (game().frameRate * 5);
        game().fill(0, 64 * game().sin(phase) + 191, 64 * game().sin(phase) + 191);
        game().text("Round " + wave + " Complete!", game().width / 2f, 100f);
        // render score stats
        float middle = game().height / 2f - 100f;
        game().textSize(40f);
        game().fill(0, 255, 255);
        game().text("score before round: " + scoreBefore, game().width / 2f, middle - 100f);
        game().text("--------------------", game().width / 2f, middle - 60f);
        game().text("extra turrets " + remainingTurretsBefore + " (x " + remainingBatteryScoreBonus + "): " + remainingTurretsBefore * remainingBatteryScoreBonus, game().width / 2f, middle - 20f);
        game().text("extra ammunition " + remainingAmmunitionBefore + " (x " + remainingAmmunitionScoreBonus + "): " + remainingAmmunitionBefore * remainingAmmunitionScoreBonus, game().width / 2f, middle + 20f);
        int remaining = score - (scoreBefore + remainingTurretsBefore * remainingBatteryScoreBonus + remainingAmmunitionBefore * remainingAmmunitionScoreBonus);
        game().text("other bonuses: " + remaining, game().width / 2f, middle + 60f);
        game().text("--------------------", game().width / 2f, middle + 100f);
        game().text("current score: " + score, game().width / 2f, middle + 140f);
        // render help message
        game().textSize(40f);
        game().fill(255, 255, 255);
        game().text("press any button to move to the next round", game().width / 2f, game().height - 150f);
        game().popStyle();
        game().popMatrix();
        infoScreenDelta++;
    }

    private void renderGameOverScreen() {
        game().pushMatrix();
        game().pushStyle();
        game().textAlign(game().CENTER);
        game().textSize(80f);
        float phase = infoScreenDelta * game().PI / (game().frameRate * 5);
        game().fill(64 * game().sin(phase) + 191, 0, 0);
        game().text("GAME OVER", game().width / 2f, game().height / 2f - 80f);
        game().textSize(50f);
        game().fill(255, 255, 255);
        game().text("press any button to close", game().width / 2f, game().height / 2f);
        game().popStyle();
        game().popMatrix();
        infoScreenDelta++;
    }

    private void renderStats() {
        renderScore();
        renderAmmoStats();
        renderWaveProgress();
    }

    private void renderScore() {
        game().pushMatrix();
        game().pushStyle();
        game().textAlign(game().CENTER);
        game().textSize(60f);
        game().fill(255, 255, 255, 127);
        game().text("score: " + (score + miscBonus), game().width / 2f, 100f);
        game().popStyle();
        game().popMatrix();
    }

    private void renderAmmoStats() {
        float infoPaddingLeft = 20f;
        game().pushMatrix();
        game().pushStyle();
        // player's ammo
        game().textSize(30f);
        game().fill(0, 255, 255);
        game().text("player ammo: " + playerBattery.getAmmunition(), infoPaddingLeft, 30);
        // enemy's ammo
        game().fill(255, 0, 0);
        game().text("enemy ammo: " + enemyBattery.getAmmunition(), infoPaddingLeft, 70);
        game().popStyle();
        game().popMatrix();
    }

    private void renderWaveProgress() {
        game().pushMatrix();
        game().pushStyle();
        // draw a bar accross the bottom, showing the progress of the round
        game().fill(255, 255, 255);
        float infoZoneTop = game().height - Floor.FLOOR_HEIGHT / 2f + 25f;
        game().textSize(20f);
        game().textAlign(game().CENTER);
        game().text("wave " + wave + " progress", game().width / 2f, infoZoneTop + 60);
        game().fill(255, 0, 0);
        game().stroke(0, 0);
        game().rect(0, game().height - 20f, game().width, 10f);
        float progress = 1f - ((float)enemyBattery.getAmmunition()) / ((float)enemyAmmunition);
        game().fill(0, 255, 0, 255);
        game().rect(0, game().height - 20f, progress * game().width, 10f);
        game().popStyle();
        game().popMatrix();
    }
}
