package com.comp2042;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/** Shared cartoon apple artwork, cached for every new food spawn. */
public final class AppleArt {
    private static Image image;
    private AppleArt() { }

    public static Image image() {
        if (image != null) return image;
        Canvas canvas = new Canvas(128, 128);
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.scale(1.28, 1.28);
        g.setFill(Color.web("#264d29", .2)); g.fillOval(20, 82, 63, 12);
        g.setStroke(Color.web("#684126")); g.setLineWidth(7);
        g.strokeLine(50, 32, 55, 10);
        g.setFill(Color.web("#24542b"));
        g.fillOval(54, 9, 30, 17);
        g.setFill(Color.web("#8be052"));
        g.fillOval(57, 10, 23, 12);
        g.beginPath(); g.moveTo(50, 32);
        g.bezierCurveTo(20, 10, 5, 38, 16, 65);
        g.bezierCurveTo(23, 87, 36, 95, 50, 86);
        g.bezierCurveTo(64, 95, 79, 85, 86, 62);
        g.bezierCurveTo(96, 32, 73, 12, 50, 32);
        g.closePath();
        g.setFill(Color.web("#ed4544")); g.fill();
        g.setStroke(Color.web("#8e2838")); g.setLineWidth(4); g.stroke();
        g.setFill(Color.web("#ff7770")); g.fillOval(24, 30, 22, 36);
        g.setFill(Color.web("#ffe1b7")); g.fillOval(28, 34, 8, 16);
        SnapshotParameters params = new SnapshotParameters(); params.setFill(Color.TRANSPARENT);
        image = canvas.snapshot(params, null);
        return image;
    }
}
