package com.comp2042;

import com.comp2042.model.Snake;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.awt.Point;

public class GameView {
    public Canvas gridCanvas;
    public GraphicsContext gc;
    public Label gameOverLabel, gameWonLabel, scoreLabel, hintLabel;
    public Button tryAgainButton, menuButton, fullscreenButton;
    public VBox endPanel;
    private LeaderboardView leaderboard;
    private Label endMessage;
    private final Game game;
    private final SnakeSkin skin;
    private double fullScale = 1;
    private Snake displayedSnake;
    private Coin displayedApple;
    private Obstacle displayedObstacle;

    public GameView(Game game) { this(game, SnakeSkin.ORANGE); }

    public GameView(Game game, SnakeSkin skin) { this.game = game; this.skin = skin; }

    public void makeScene(Scene scene, Snake snake, Coin apple) {
        gridCanvas = new Canvas(game.getWidth() * game.getMultiplier(), game.getHeight() * game.getMultiplier());
        gc = gridCanvas.getGraphicsContext2D();
        gameOverLabel = new Label("GAME OVER");
        gameWonLabel = new Label("YOU WON!");
        gameOverLabel.setId("gameOverLabel"); gameWonLabel.setId("gameWonLabel");
        gameOverLabel.setVisible(false); gameOverLabel.setManaged(false);
        gameWonLabel.setVisible(false); gameWonLabel.setManaged(false);
        scoreLabel = new Label(); scoreLabel.setId("scoreLabel");
        hintLabel = new Label("WASD / arrows  •  P pause  •  F11 fullscreen");
        hintLabel.setId("hintLabel");
        fullscreenButton = new Button("Fullscreen"); fullscreenButton.setId("fullscreenButton");
        fullscreenButton.setFocusTraversable(false);
        tryAgainButton = new Button("Try Again"); tryAgainButton.setId("tryAgainButton");
        menuButton = new Button("Choose Skin"); menuButton.setId("chooseSkinButton");
        // Buttons never steal the arrow keys needed to steer after restarting.
        tryAgainButton.setFocusTraversable(false); menuButton.setFocusTraversable(false);
        endMessage = new Label("Your score: 0"); endMessage.setId("endMessage");
        leaderboard = new LeaderboardView();
        endPanel = new VBox(10, gameOverLabel, gameWonLabel, endMessage, leaderboard, tryAgainButton, menuButton);
        endPanel.setAlignment(Pos.CENTER); endPanel.setMaxSize(360, javafx.scene.layout.Region.USE_PREF_SIZE);
        endPanel.setId("endPanel"); endPanel.setVisible(false); endPanel.setManaged(false);
        updateScoreLabel(); drawGrid(apple, snake, gc);
    }

    public void scaleToFullscreen(boolean fullscreen, Stage stage, Obstacle obstacle, Snake snake, Coin apple) {
        stage.setFullScreen(fullscreen);
    }

    public void resizeBoard(double availableWidth, double availableHeight) {
        if (availableWidth <= 0 || availableHeight <= 0) return;
        double tile = Math.min(availableWidth / game.getWidth(), availableHeight / game.getHeight());
        double width = tile * game.getWidth(), height = tile * game.getHeight();
        if (Math.abs(gridCanvas.getWidth() - width) < .01 && Math.abs(gridCanvas.getHeight() - height) < .01) return;
        fullScale = tile / game.getMultiplier();
        gridCanvas.setWidth(width); gridCanvas.setHeight(height);
        if (displayedApple != null && displayedSnake != null) {
            if (displayedObstacle == null) drawGrid(displayedApple, displayedSnake, gc);
            else drawGrid(displayedApple, displayedSnake, displayedObstacle, gc, false, 0);
        }
    }

    public void drawGrid(Coin apple, Snake snake, Obstacle obstacle, GraphicsContext context, boolean unused, int ignored) {
        displayedObstacle = obstacle;
        drawGrid(apple, snake, context);
        double tile = game.getMultiplier() * fullScale;
        context.setFill(Color.web("#486745"));
        for (Point point : obstacle.getObstacles())
            context.fillRoundRect(point.x * tile + 2, point.y * tile + 2, tile - 4, tile - 4, 10, 10);
    }

    private void drawGrid(Coin apple, Snake snake, GraphicsContext context) {
        displayedApple = apple; displayedSnake = snake;
        double tile = game.getMultiplier() * fullScale;
        drawGrass(context, game.getWidth(), game.getHeight(), tile);
        context.drawImage(apple.getImage(), apple.getPos().x * tile, apple.getPos().y * tile, tile, tile);
        skin.drawSnake(context, snake, tile);
    }

    public static void drawGrass(GraphicsContext g, int columns, int rows, double tile) {
        for (int y = 0; y < rows; y++) for (int x = 0; x < columns; x++) {
            g.setFill(Color.web((x + y) % 2 == 0 ? "#a3d56b" : "#98cb60"));
            double left = Math.round(x * tile), top = Math.round(y * tile);
            g.fillRect(left, top, Math.round((x + 1) * tile) - left, Math.round((y + 1) * tile) - top);
            // Deterministic sparse tufts stay still while the snake moves.
            if ((x * 7 + y * 11) % 5 == 0) {
                double px = (x + .23) * tile, py = (y + .78) * tile;
                g.setStroke(Color.web("#6ba64b", .36)); g.setLineWidth(Math.max(1, tile * .035));
                g.strokeLine(px, py, px - tile * .07, py - tile * .12);
                g.strokeLine(px, py, px + tile * .05, py - tile * .16);
            }
        }
    }

    public void gameOver(Snake snake) {
        gameOverLabel.setVisible(snake.isDead()); gameOverLabel.setManaged(snake.isDead());
        updateEndPanel();
    }
    public void gameWon() {
        gameWonLabel.setVisible(game.gameWon()); gameWonLabel.setManaged(game.gameWon());
        updateEndPanel();
    }
    private void updateEndPanel() {
        boolean show = gameOverLabel.isVisible() || gameWonLabel.isVisible();
        endPanel.setVisible(show); endPanel.setManaged(show);
        if (show) hintLabel.setText("Enter to try again  •  Esc for skins");
    }
    public void refreshLeaderboard() {
        leaderboard.refresh();
        endMessage.setText("Your score: " + game.getScorePoints() + "  •  Apples: " + game.getScore());
    }
    public void updateScoreLabel() {
        scoreLabel.setText("Apples: " + game.getScore() + "    Score: " + game.getScorePoints());
    }
    public void showReady() { hintLabel.setText("WASD / arrows  •  P pause  •  F11 fullscreen"); }
}
