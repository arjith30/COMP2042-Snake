package com.comp2042;

import com.comp2042.model.Snake;
import com.comp2042.model.Direction;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Duration;

public class GameController {
    private Snake snek;
    private Coin coin;
    private final GameView gameView;
    private final Game game;
    private final Stage stage;
    private final Obstacle obstacle;
    public static KeyCode key = KeyCode.LEFT;
    public static KeyCode lastKey = KeyCode.LEFT;
    private boolean keyPressed;
    private boolean scoreRecorded;
    private final Timeline animation;

    public GameController(Snake snake, Coin apple, Game game, GameView view, Obstacle obstacle, Stage stage) {
        this.snek = snake; this.coin = apple; this.game = game;
        this.gameView = view; this.obstacle = obstacle; this.stage = stage;
        key = lastKey = KeyCode.LEFT;
        animation = new Timeline(new KeyFrame(Duration.seconds(1), event -> tick()));
        animation.setCycleCount(Animation.INDEFINITE); animation.setRate(game.getSpeed());
    }

    private void tick() {
        if (snek.isDead() || game.gameWon()) { animation.stop(); return; }
        if (snek.advance(directionFor(key), coin.getPos(), obstacle.getObstacles())) {
            game.incScore(); game.addPoints(coin.getPoints());
            // Do not search for a free spawn cell after filling the entire board.
            if (!game.gameWon()) coin = new Coin(game.getWidth(), game.getHeight(), snek, obstacle);
        }
        keyPressed = false;
        gameView.drawGrid(coin, snek, obstacle, gameView.gc, false, 0);
        if ((snek.isDead() || game.gameWon()) && !scoreRecorded) {
            scoreRecorded = true;
            Leaderboard.local().record(game.getScorePoints());
            gameView.refreshLeaderboard();
        }
        gameView.updateScoreLabel(); gameView.gameOver(snek); gameView.gameWon();
        if (snek.isDead() || game.gameWon()) animation.stop();
    }

    public void handle(KeyEvent event) {
        KeyCode pressed = switch (event.getCode()) {
            case W -> KeyCode.UP;
            case A -> KeyCode.LEFT;
            case S -> KeyCode.DOWN;
            case D -> KeyCode.RIGHT;
            default -> event.getCode();
        };
        if (pressed == KeyCode.ENTER && (snek.isDead() || game.gameWon())) {
            restart(); event.consume(); return;
        }
        if (pressed == KeyCode.F11) {
            toggleFullscreen(); event.consume(); return;
        }
        if (pressed == KeyCode.ESCAPE) {
            if (stage.isFullScreen()) stage.setFullScreen(false);
            else returnToMenu();
            event.consume(); return;
        }
        if (pressed == KeyCode.TAB || pressed == KeyCode.R) { restart(); event.consume(); return; }
        if (pressed == KeyCode.PLUS || pressed == KeyCode.ADD) {
            gameView.scaleToFullscreen(true, stage, obstacle, snek, coin); event.consume(); return;
        }
        if (pressed == KeyCode.MINUS || pressed == KeyCode.SUBTRACT) {
            gameView.scaleToFullscreen(false, stage, obstacle, snek, coin); event.consume(); return;
        }
        if (snek.isDead() || game.gameWon()) return;
        if (pressed == KeyCode.P) {
            animation.pause(); gameView.hintLabel.setText("Paused  •  WASD or arrows to resume");
        }
        if (pressed.isArrowKey()) {
            if (!keyPressed && validDirection(pressed)) {
                lastKey = key; key = pressed; keyPressed = true;
            }
            animation.play(); gameView.hintLabel.setText("Collect apples!  •  P to pause  •  R to restart");
            event.consume();
        }
    }

    private static Direction directionFor(KeyCode code) {
        return switch (code) {
            case UP -> Direction.UP;
            case RIGHT -> Direction.RIGHT;
            case DOWN -> Direction.DOWN;
            default -> Direction.LEFT;
        };
    }

    private boolean validDirection(KeyCode pressed) {
        return !snek.getDirection().isOppositeTo(directionFor(pressed));
    }

    public void restart() {
        animation.stop(); animation.jumpTo(Duration.ZERO);
        obstacle.reset(); game.resetScore();
        snek = new Snake(game.getWidth(), game.getHeight(), game.isNoBorder());
        coin = new Coin(game.getWidth(), game.getHeight(), snek, obstacle);
        key = lastKey = KeyCode.LEFT; keyPressed = false; scoreRecorded = false;
        animation.setRate(game.getSpeed());
        gameView.gameOver(snek); gameView.gameWon(); gameView.updateScoreLabel(); gameView.showReady();
        gameView.drawGrid(coin, snek, obstacle, gameView.gc, false, 0);
        gameView.gridCanvas.requestFocus();
    }

    public void toggleFullscreen() { stage.setFullScreen(!stage.isFullScreen()); }

    public void stop() { animation.stop(); }
    public void returnToMenu() {
        stop();
        if (Main.getViewManager() != null) Main.getViewManager().getPrimaryStage().show();
        stage.close();
    }
}
