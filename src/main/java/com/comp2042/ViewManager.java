package com.comp2042;

import com.comp2042.model.Snake;

import com.comp2042.audio.BackgroundMusic;
import javafx.beans.binding.Bindings;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
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
    private MainMenuController menuController;
    private final BackgroundMusic music;

    private Image snakeLogo = new Image("resources/views/res/SnakeLogoWhite.png");

    public ViewManager() { this(new BackgroundMusic()); }

    public ViewManager(BackgroundMusic music) { this.music = java.util.Objects.requireNonNull(music); }

    public void initalizeMainMenu() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("/resources/views/MainMenu.fxml"));
            root = loader.load();
            menuController = loader.getController();
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        Scene mainScene = new Scene(new StackPane(root), 850, 540);
        mainScene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.M) {
                music.toggleMuted(); event.consume();
            } else if (event.getCode() == KeyCode.F11) {
                primaryStage.setFullScreen(!primaryStage.isFullScreen()); event.consume();
            } else if (event.getCode() == KeyCode.ESCAPE && primaryStage.isFullScreen()) {
                primaryStage.setFullScreen(false); event.consume();
            }
        });

        primaryStage.setScene(mainScene);
        primaryStage.setOnShowing(event -> menuController.refreshLeaderboard());
        primaryStage.setResizable(true);
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        primaryStage.setFullScreenExitHint("Press F11 or Esc to exit fullscreen");
        primaryStage.getIcons().add(snakeLogo);
        primaryStage.setTitle("Snake Game");
        primaryStage.show();
    }

    public BackgroundMusic getMusic() { return music; }

    public void configureMusicButton(Button button) {
        button.getStyleClass().add("music-toggle");
        button.setFocusTraversable(false);
        button.setMinWidth(javafx.scene.layout.Region.USE_PREF_SIZE);
        button.setTooltip(new Tooltip("M: mute or unmute background music"));
        button.textProperty().bind(Bindings.when(music.availableProperty().not())
                .then("Music unavailable")
                .otherwise(Bindings.when(music.mutedProperty()).then("Music: Off (M)").otherwise("Music: On (M)")));
        button.disableProperty().bind(music.availableProperty().not());
        button.setOnAction(event -> music.toggleMuted());
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
