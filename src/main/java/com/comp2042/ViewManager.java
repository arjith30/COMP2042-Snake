package com.comp2042;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;

import java.io.IOException;

public class ViewManager {

    private Stage primaryStage = new Stage();
    private AnchorPane root;

    private Stage gameStage;

    private Image snakeLogo = new Image("resources/views/res/SnakeLogoWhite.png");

    public ViewManager() {
    }

    public void initalizeMainMenu() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("/resources/views/MainMenu.fxml"));
            root = loader.load();
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        Scene mainScene = new Scene(new StackPane(root), 550, 400);
        mainScene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.F11) {
                primaryStage.setFullScreen(!primaryStage.isFullScreen()); event.consume();
            } else if (event.getCode() == KeyCode.ESCAPE && primaryStage.isFullScreen()) {
                primaryStage.setFullScreen(false); event.consume();
            }
        });

        primaryStage.setScene(mainScene);
        primaryStage.setResizable(true);
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        primaryStage.setFullScreenExitHint("Press F11 or Esc to exit fullscreen");
        primaryStage.getIcons().add(snakeLogo);
        primaryStage.setTitle("Snake Game");
        primaryStage.show();
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void initGameStage() {
        gameStage = new Stage();
        gameStage.setTitle("Snake Game");
        gameStage.getIcons().add(snakeLogo);
        gameStage.setResizable(true);
        gameStage.setMinWidth(560); gameStage.setMinHeight(500);
        gameStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        gameStage.setFullScreenExitHint("Press F11 or Esc to exit fullscreen");
    }

    public Stage getGameStage() {
        return gameStage;
    }
}
