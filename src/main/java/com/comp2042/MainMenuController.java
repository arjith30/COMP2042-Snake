package com.comp2042;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ChoiceBox;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainMenuController {

    private static final int X_VALUE = 20;
    private static final int Y_VALUE = 20;
    private static final boolean POWER_UP = false;
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
        gc.save(); gc.translate(24, 10);
        skinPicker.getValue().drawConnected(gc, java.util.List.of(
                new java.awt.Point(0, 0), new java.awt.Point(1, 0),
                new java.awt.Point(2, 0), new java.awt.Point(3, 0)), 90, 52);
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
        gameScene = new Scene(gamePane, game.getWidth() * game.getMultiplier(), game.getHeight() * game.getMultiplier() + 56);

        gameScene.setOnKeyPressed(controller::handle);

        gameView.makeScene(gameScene, snek, coin);
        gameScene.getStylesheets().add("resources/views/styling.css");
        javafx.scene.layout.HBox hud = new javafx.scene.layout.HBox(20, gameView.scoreLabel, gameView.hintLabel);
        hud.setAlignment(Pos.CENTER); hud.setPrefHeight(56); hud.setId("gameHud");
        StackPane board = new StackPane(gameView.gridCanvas, gameView.endPanel);
        javafx.scene.layout.BorderPane layout = new javafx.scene.layout.BorderPane(board);
        layout.setTop(hud);
        gamePane.getChildren().add(layout);
        gameView.tryAgainButton.setOnAction(event -> controller.restart());
        gameView.menuButton.setOnAction(event -> controller.returnToMenu());
        gameStage.setOnHidden(event -> controller.stop());
        gameView.gridCanvas.setFocusTraversable(true);
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
