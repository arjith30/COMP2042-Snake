package com.comp2042;

import com.comp2042.audio.BackgroundMusic;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    private static ViewManager viewManager;
    private final BackgroundMusic music;

    public Main() { this(new BackgroundMusic()); }
    public Main(BackgroundMusic music) { this.music = java.util.Objects.requireNonNull(music); }

    @Override
    public void start(Stage primaryStage) {
        viewManager = new ViewManager(music);
        viewManager.initalizeMainMenu();
        viewManager.getMusic().start();
    }

    @Override
    public void stop() {
        if (viewManager != null) viewManager.getMusic().close();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static ViewManager getViewManager() {
        return viewManager;
    }
}
