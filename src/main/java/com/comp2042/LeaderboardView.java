package com.comp2042;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import java.util.ArrayList;
import java.util.List;

public final class LeaderboardView extends VBox {
    private final List<Label> values = new ArrayList<>();
    private final Label status = new Label();

    public LeaderboardView() {
        super(5);
        getStyleClass().add("leaderboard");
        Label title = new Label("TOP 5 SCORES"); title.getStyleClass().add("leaderboard-title");
        getChildren().add(title);
        for (int rank = 1; rank <= 5; rank++) {
            Label position = new Label(String.format("%02d", rank));
            position.getStyleClass().add("leaderboard-rank");
            Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
            Label value = new Label(); values.add(value);
            HBox row = new HBox(16, position, spacer, value);
            row.setAlignment(Pos.CENTER_LEFT); row.getStyleClass().add("leaderboard-row");
            getChildren().add(row);
        }
        status.getStyleClass().add("leaderboard-status"); status.setWrapText(true);
        getChildren().add(status); refresh();
    }

    public void refresh() {
        List<Integer> scores = Leaderboard.local().scores();
        for (int index = 0; index < 5; index++)
            values.get(index).setText(index < scores.size() ? scores.get(index) + " pts" : "—");
        status.setText(Leaderboard.local().isPersistent() ? "Saved on this device" : "Scores kept for this session only");
    }
}
