package com.comp2042;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ChoiceBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainMenuController {

    private static final int X_VALUE = 20;
    private static final int Y_VALUE = 20;
    private static final boolean POWER_UP = true;
    private static final char DIFFICULTY = 'N';

    @FXML private ChoiceBox<SnakeSkin> skinPicker;
    @FXML private Canvas skinPreview;

    @FXML
    private void initialize() {
        skinPicker.getItems().setAll(SnakeSkin.values());
        skinPicker.setValue(SnakeSkin.ORANGE);
        skinPicker.setOnAction(event -> drawSkinPreview());
        drawSkinPreview();
    }

    private void drawSkinPreview() {
        var gc = skinPreview.getGraphicsContext2D();
        gc.clearRect(0, 0, skinPreview.getWidth(), skinPreview.getHeight());
        SnakeSkin skin = skinPicker.getValue();
        double size = 48;
        gc.save();
        gc.translate(34, 18);
        gc.rotate(90);
        gc.drawImage(skin.image(SnakeSkin.Part.TAIL), 0, -size, size, size);
        gc.drawImage(skin.image(SnakeSkin.Part.BODY), 0, -size * 2, size, size);
        gc.drawImage(skin.image(SnakeSkin.Part.BODY), 0, -size * 3, size, size);
        gc.drawImage(skin.image(SnakeSkin.Part.HEAD), 0, -size * 4, size, size);
        gc.restore();
    }

    private Scene gameScene;

    private ViewManager viewManager = Main.getViewManager();

    public void handleStart() {
        viewManager.initGameStage();

        Stage gameStage = viewManager.getGameStage();
        launchSnake(gameStage);
        gameStage.setScene(gameScene);
        viewManager.getPrimaryStage().close();
        gameStage.show();
    }

    private void launchSnake(Stage gameStage) {
        Game game = new Game(X_VALUE, Y_VALUE, POWER_UP, DIFFICULTY);
        Obstacle obstacle = new Obstacle(game.getWidth(), game.getHeight());
        Snake snek = new Snake(game.getWidth(), game.getHeight(), game.isNoBorder(), obstacle, skinPicker.getValue());
        Coin coin = new Coin(game.getWidth(), game.getHeight(), snek, obstacle);

        GameView gameView = new GameView(game);
        GameController controller = new GameController(snek, coin, game, gameView, obstacle, gameStage);

        StackPane gamePane = new StackPane();
        gameScene = new Scene(gamePane, game.getWidth() * game.getMultiplier(), game.getHeight() * game.getMultiplier());

        gameScene.setOnKeyPressed(controller::handle);

        gameView.makeScene(gameScene, snek, coin);
        gamePane.getChildren().addAll(gameView.gridCanvas, gameView.gameOverLabel, gameView.gameWonLabel, gameView.scoreLabel);
        gamePane.setPadding(new Insets(20));
        gamePane.setAlignment(gameView.gameOverLabel, Pos.TOP_CENTER);
        gamePane.setAlignment(gameView.gameWonLabel, Pos.TOP_CENTER);
        gamePane.setAlignment(gameView.scoreLabel, Pos.BOTTOM_CENTER);
    }

    public ViewManager getViewManager() {
        return viewManager;
    }

    public static int getxValue() {
        return X_VALUE;
    }

    public static int getyValue() {
        return Y_VALUE;
    }
}
