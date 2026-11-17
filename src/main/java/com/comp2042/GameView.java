package com.comp2042;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import java.awt.Point;

public class GameView {
    public Canvas gridCanvas;
    public GraphicsContext gc;
    public Label gameOverLabel, gameWonLabel, scoreLabel, hintLabel;
    public Button tryAgainButton, menuButton;
    public VBox endPanel;
    private final Game game;
    private double fullScale = 1;

    public GameView(Game game) { this.game = game; }

    public void makeScene(Scene scene, Snake snake, Coin apple) {
        gridCanvas = new Canvas(game.getWidth() * game.getMultiplier(), game.getHeight() * game.getMultiplier());
        gc = gridCanvas.getGraphicsContext2D();
        gameOverLabel = new Label("GAME OVER");
        gameWonLabel = new Label("YOU WON!");
        gameOverLabel.setId("gameOverLabel"); gameWonLabel.setId("gameWonLabel");
        gameOverLabel.setVisible(false); gameOverLabel.setManaged(false);
        gameWonLabel.setVisible(false); gameWonLabel.setManaged(false);
        scoreLabel = new Label(); scoreLabel.setId("scoreLabel");
        hintLabel = new Label("WASD / arrows  •  P to pause  •  Esc for skins");
        hintLabel.setId("hintLabel");
        tryAgainButton = new Button("Try Again"); tryAgainButton.setId("tryAgainButton");
        menuButton = new Button("Choose Skin"); menuButton.setId("chooseSkinButton");
        // Buttons never steal the arrow keys needed to steer after restarting.
        tryAgainButton.setFocusTraversable(false); menuButton.setFocusTraversable(false);
        Label message = new Label("Another apple adventure?"); message.setId("endMessage");
        endPanel = new VBox(16, gameOverLabel, gameWonLabel, message, tryAgainButton, menuButton);
        endPanel.setAlignment(Pos.CENTER); endPanel.setMaxSize(330, 300);
        endPanel.setId("endPanel"); endPanel.setVisible(false); endPanel.setManaged(false);
        updateScoreLabel(); drawGrid(apple, snake, gc);
    }

    public void scaleToFullscreen(boolean fullscreen, Stage stage, Obstacle obstacle, Snake snake, Coin apple) {
        stage.setFullScreen(fullscreen);
        fullScale = fullscreen ? Math.min(
                (Screen.getPrimary().getVisualBounds().getWidth() - 32) / (game.getWidth() * game.getMultiplier()),
                (Screen.getPrimary().getVisualBounds().getHeight() - 100) / (game.getHeight() * game.getMultiplier())) : 1;
        gridCanvas.setWidth(game.getWidth() * game.getMultiplier() * fullScale);
        gridCanvas.setHeight(game.getHeight() * game.getMultiplier() * fullScale);
        drawGrid(apple, snake, obstacle, gc, false, 0);
    }

    public void drawGrid(Coin apple, Snake snake, Obstacle obstacle, GraphicsContext context, boolean unused, int ignored) {
        drawGrid(apple, snake, context);
        double tile = game.getMultiplier() * fullScale;
        context.setFill(Color.web("#486745"));
        for (Point point : obstacle.getObstacles())
            context.fillRoundRect(point.x * tile + 2, point.y * tile + 2, tile - 4, tile - 4, 10, 10);
    }

    private void drawGrid(Coin apple, Snake snake, GraphicsContext context) {
        double tile = game.getMultiplier() * fullScale;
        drawGrass(context, game.getWidth(), game.getHeight(), tile);
        context.drawImage(apple.getImage(), apple.getPos().x * tile, apple.getPos().y * tile, tile, tile);
        snake.getSkin().drawSnake(context, snake, tile);
    }

    public static void drawGrass(GraphicsContext g, int columns, int rows, double tile) {
        for (int y = 0; y < rows; y++) for (int x = 0; x < columns; x++) {
            g.setFill(Color.web((x + y) % 2 == 0 ? "#a3d56b" : "#98cb60"));
            g.fillRect(x * tile, y * tile, tile, tile);
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
        gameOverLabel.setVisible(snake.getDead()); gameOverLabel.setManaged(snake.getDead());
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
    public void updateScoreLabel() {
        scoreLabel.setText("Apples: " + game.getScore() + "    Score: " + game.getScorePoints());
    }
    public void showReady() { hintLabel.setText("WASD / arrows  •  P to pause  •  Esc for skins"); }
}
