package com.comp2042;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.StrokeLineCap;
import java.util.EnumMap;

/** Cached, transparent vector sprites. All parts use the original tile orientations. */
public enum SnakeSkin {
    ORANGE("Orange Pop"), SKELETON("Mr. Bones");

    public enum Part { HEAD, BODY, TURN, TAIL }
    private final String label;
    private final EnumMap<Part, Image> images = new EnumMap<>(Part.class);
    private static final Color INK = Color.web("#58341f");
    private static final Color BONE = Color.web("#fff4cf");

    SnakeSkin(String label) { this.label = label; }
    @Override public String toString() { return label; }

    /** Called on the JavaFX application thread; sprites are built only once per skin. */
    public Image image(Part part) {
        return images.computeIfAbsent(part, this::render);
    }

    private Image render(Part part) {
        Canvas canvas = new Canvas(128, 128);
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.scale(1.28, 1.28);
        g.setLineCap(StrokeLineCap.ROUND);
        boolean bones = this == SKELETON;
        Color outline = bones ? Color.web("#303048") : INK;
        var fill = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, bones ? Color.web("#55536d") : Color.web("#ffbd47")),
                new Stop(.5, bones ? Color.web("#3b3a54") : Color.web("#ff971f")),
                new Stop(1, bones ? Color.web("#303048") : Color.web("#f56b13")));
        if (part == Part.HEAD) {
            g.setFill(outline);
            g.fillRoundRect(7, 4, 86, 101, 58, 58);
            g.setFill(bones ? BONE : fill);
            g.fillRoundRect(12, 8, 76, 97, 50, 50);
            if (bones) {
                g.setFill(Color.web("#decda5"));
                g.fillRoundRect(24, 70, 52, 24, 14, 14);
                g.setStroke(outline); g.setLineWidth(2.8);
                for (int x = 35; x <= 65; x += 15) g.strokeLine(x, 79, x, 91);
                g.setFill(outline);
                g.fillPolygon(new double[]{50, 43, 57}, new double[]{53, 64, 64}, 3);
            } else {
                g.setFill(Color.web("#ffe1a0"));
                g.fillOval(26, 10, 48, 19);
                g.setFill(Color.web("#ffcf68"));
                g.fillOval(14, 59, 13, 10); g.fillOval(73, 59, 13, 10);
                g.setFill(INK);
                g.fillOval(38, 17, 5, 5); g.fillOval(57, 17, 5, 5);
                g.setStroke(INK); g.setLineWidth(2.5);
                g.strokeArc(31, 57, 38, 21, 195, 150, javafx.scene.shape.ArcType.OPEN);
            }
            // Large paired eyes with forward-looking pupils and a small catchlight.
            for (int x : new int[]{27, 73}) {
                g.setFill(outline); g.fillOval(x - 16, 29, 32, 32);
                g.setFill(Color.WHITE); g.fillOval(x - 12, 32, 24, 25);
                g.setFill(bones ? Color.web("#67ded2") : Color.web("#693519"));
                g.fillOval(x - 8, 33, 16, 19);
                g.setFill(Color.web("#242435")); g.fillOval(x - 5, 33, 10, 15);
                g.setFill(Color.WHITE); g.fillOval(x - 5, 34, 5, 6);
            }
        } else if (part == Part.TAIL) {
            g.setFill(outline); tail(g, 5, 95);
            g.setFill(fill); tail(g, 10, 90);
            if (bones) {
                g.setStroke(BONE); g.setLineWidth(8); g.strokeLine(50, 0, 50, 70);
                for (int y = 15; y < 65; y += 20) {
                    double w = 22 - y * .2;
                    g.setLineWidth(6); g.strokeLine(50 - w, y, 50 + w, y);
                }
            } else {
                g.setStroke(Color.web("#ffd273")); g.setLineWidth(6);
                g.strokeLine(30, 0, 46, 62);
            }
        } else {
            // The base corner joins the left and bottom edges, matching turnDir().
            g.setStroke(outline); g.setLineWidth(90); path(g, part);
            g.setStroke(fill); g.setLineWidth(80); path(g, part);
            if (bones) {
                g.setStroke(BONE); g.setLineWidth(9); path(g, part);
                if (part == Part.BODY) {
                    for (int y = 16; y < 100; y += 32) rib(g, 50, y, 0);
                } else {
                    rib(g, 14, 51, -80); rib(g, 38, 62, -42); rib(g, 49, 88, -5);
                }
            } else {
                g.setStroke(Color.web("#ffd273")); g.setLineWidth(7);
                if (part == Part.BODY) {
                    g.strokeLine(24, 0, 24, 100);
                    g.setFill(Color.web("#ed7116"));
                    g.fillOval(60, 25, 13, 10); g.fillOval(57, 66, 10, 8);
                } else {
                    g.beginPath(); g.moveTo(0, 25); g.quadraticCurveTo(75, 25, 75, 100); g.stroke();
                }
            }
        }
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        return canvas.snapshot(params, null);
    }

    private static void path(GraphicsContext g, Part part) {
        g.beginPath();
        if (part == Part.TURN) {
            g.moveTo(-5, 50); g.quadraticCurveTo(50, 50, 50, 105);
        } else { g.moveTo(50, -5); g.lineTo(50, 105); }
        g.stroke();
    }

    private static void tail(GraphicsContext g, double left, double right) {
        g.beginPath(); g.moveTo(left, -5); g.lineTo(right, -5);
        g.bezierCurveTo(right, 47, 63, 88, 50, 98);
        g.bezierCurveTo(37, 88, left, 47, left, -5); g.fill();
    }

    private static void rib(GraphicsContext g, double x, double y, double angle) {
        g.save(); g.translate(x, y); g.rotate(angle);
        g.setStroke(Color.web("#b4a88b")); g.setLineWidth(10);
        g.strokeLine(-24, 1, 24, 1);
        g.setStroke(BONE); g.setLineWidth(7); g.strokeLine(-24, -1, 24, -1);
        g.setFill(Color.WHITE); g.fillOval(-6, -6, 12, 12);
        g.restore();
    }
}
