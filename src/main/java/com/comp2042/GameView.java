package com.comp2042;

import com.comp2042.model.Snake;
import com.comp2042.model.Level;

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
    public Label gameOverLabel, gameWonLabel, scoreLabel, hintLabel, levelLabel;
    public Button tryAgainButton, menuButton, fullscreenButton, nextLevelButton, musicButton;
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
        levelLabel = new Label(); levelLabel.setId("levelLabel");
        levelLabel.setMinWidth(javafx.scene.layout.Region.USE_PREF_SIZE);
        hintLabel = new Label("WASD / arrows  •  P pause");
        hintLabel.setId("hintLabel");
        musicButton = new Button(); musicButton.setId("musicButton");
        fullscreenButton = new Button("Fullscreen"); fullscreenButton.setId("fullscreenButton");
        fullscreenButton.setFocusTraversable(false);
        fullscreenButton.setMinWidth(javafx.scene.layout.Region.USE_PREF_SIZE);
        fullscreenButton.setTooltip(new javafx.scene.control.Tooltip("F11: toggle fullscreen; Esc: leave fullscreen"));
        tryAgainButton = new Button("Try Again"); tryAgainButton.setId("tryAgainButton");
        menuButton = new Button("Menu / Levels"); menuButton.setId("chooseSkinButton");
        nextLevelButton = new Button("Next Level"); nextLevelButton.setId("nextLevelButton");
        nextLevelButton.setFocusTraversable(false); nextLevelButton.setVisible(false); nextLevelButton.setManaged(false);
        // Buttons never steal the arrow keys needed to steer after restarting.
        tryAgainButton.setFocusTraversable(false); menuButton.setFocusTraversable(false);
        endMessage = new Label("Your score: 0"); endMessage.setId("endMessage");
        leaderboard = new LeaderboardView();
        endPanel = new VBox(10, gameOverLabel, gameWonLabel, endMessage, leaderboard, nextLevelButton, tryAgainButton, menuButton);
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
        for (Point point : obstacle.getObstacles()) {
            double x = point.x * tile, y = point.y * tile;
            boolean hedge = game.getLevel() == Level.HEDGES;
            context.setFill(Color.web(hedge ? "#234d32" : "#48544e"));
            context.fillRoundRect(x + 1, y + 1, tile - 2, tile - 2, tile * .3, tile * .3);
            context.setFill(Color.web(hedge ? "#3e8650" : "#929e8c"));
            context.fillRoundRect(x + 4, y + 3, tile - 8, tile - 8, tile * .25, tile * .25);
            if (hedge) {
                context.setFill(Color.web("#74b859"));
                context.fillOval(x + tile * .2, y + tile * .15, tile * .25, tile * .18);
                context.fillOval(x + tile * .58, y + tile * .54, tile * .2, tile * .14);
            } else {
                context.setStroke(Color.web("#c3ccad")); context.setLineWidth(Math.max(1, tile * .045));
                context.strokeLine(x + tile * .2, y + tile * .25, x + tile * .7, y + tile * .25);
                context.setFill(Color.web("#4d8149"));
                context.fillOval(x + tile * .6, y + tile * .65, tile * .25, tile * .15);
            }
        }
    }

    private void drawGrid(Coin apple, Snake snake, GraphicsContext context) {
        displayedApple = apple; displayedSnake = snake;
        double tile = game.getMultiplier() * fullScale;
        BoardBackground.draw(context, game.getLevel(), game.getWidth(), game.getHeight(), tile);
        context.drawImage(apple.getImage(), apple.getPos().x * tile, apple.getPos().y * tile, tile, tile);
        skin.drawSnake(context, snake, tile);
    }

    public static void drawGrass(GraphicsContext g, int columns, int rows, double tile) {
        BoardBackground.draw(g, Level.MEADOW, columns, rows, tile);
    }

    public void gameOver(Snake snake) {
        gameOverLabel.setVisible(snake.isDead()); gameOverLabel.setManaged(snake.isDead());
        updateEndPanel();
    }
    public void gameWon() {
        gameWonLabel.setText(game.gameWon() ? "ADVENTURE WON!" : "LEVEL COMPLETE");
        gameWonLabel.setVisible(game.isLevelComplete()); gameWonLabel.setManaged(game.isLevelComplete());
        updateEndPanel();
    }
    private void updateEndPanel() {
        boolean show = gameOverLabel.isVisible() || gameWonLabel.isVisible();
        endPanel.setVisible(show); endPanel.setManaged(show);
        boolean continuing = !gameOverLabel.isVisible() && game.hasNextLevel();
        nextLevelButton.setVisible(continuing); nextLevelButton.setManaged(continuing);
        leaderboard.setVisible(!continuing); leaderboard.setManaged(!continuing);
        tryAgainButton.setText(continuing ? "Restart Adventure" : "Try Again");
        if (show) {
            endMessage.setText("Score: " + game.getScorePoints() + "  •  Total apples: " + game.getScore());
            hintLabel.setText(continuing ? "Enter: next level" : "Enter: retry  •  Esc: menu");
        }
    }
    public void refreshLeaderboard() {
        leaderboard.refresh();
        endMessage.setText("Your score: " + game.getScorePoints() + "  •  Apples: " + game.getScore());
    }
    public void updateScoreLabel() {
        scoreLabel.setText("Score: " + game.getScorePoints());
        levelLabel.setText(game.getLevel() + "  •  " + game.getLevelApples() + "/" + game.getLevel().goal() + " apples");
    }
    public void showReady() { hintLabel.setText("WASD / arrows  •  P pause"); }
}
