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
    ORANGE("Orange Pop"), SKELETON("Mr. Bones"),
    COSMIC("Cosmic"), CANDY("Candy Pop"), JADE("Jade Dragon");

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
        if (this == COSMIC || this == CANDY || this == JADE) {
            renderThemed(g, part);
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.TRANSPARENT);
            return canvas.snapshot(params, null);
        }
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

    private void renderThemed(GraphicsContext g, Part part) {
        Color outline = Color.web(this == COSMIC ? "#29204f" : this == CANDY ? "#713354" : "#164c43");
        Color light = Color.web(this == COSMIC ? "#af78ef" : this == CANDY ? "#ff9acb" : "#65e4a6");
        Color dark = Color.web(this == COSMIC ? "#5933a4" : this == CANDY ? "#f0529e" : "#169c75");
        var fill = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, light), new Stop(1, dark));
        if (part == Part.HEAD) {
            g.setFill(outline); g.fillRoundRect(7, 8, 86, 97, 54, 54);
            g.setFill(fill); g.fillRoundRect(12, 12, 76, 93, 48, 48);
            if (this == JADE) {
                // Rounded horns and short whiskers stay within the collision tile.
                g.setStroke(outline); g.setLineWidth(13);
                g.strokeLine(23, 23, 20, 10); g.strokeLine(77, 23, 80, 10);
                g.setStroke(Color.web("#ffdb76")); g.setLineWidth(7);
                g.strokeLine(23, 23, 20, 10); g.strokeLine(77, 23, 80, 10);
                g.setFill(Color.web("#ffe6a0")); g.fillOval(28, 64, 44, 29);
                g.setStroke(outline); g.setLineWidth(3);
                g.strokeLine(30, 72, 10, 64); g.strokeLine(70, 72, 90, 64);
            } else {
                g.setFill(this == COSMIC ? Color.web("#cba3ff") : Color.web("#fff0b8"));
                g.fillOval(29, 14, 42, 17);
                if (this == COSMIC) star(g, 50, 22, 7, Color.web("#b6fff3"));
                else {
                    g.setStroke(Color.web("#45d8dc")); g.setLineWidth(7);
                    g.strokeLine(39, 18, 44, 23); g.strokeLine(56, 18, 61, 23);
                }
            }
            for (int x : new int[]{28, 72}) {
                g.setFill(outline); g.fillOval(x - 17, 30, 34, 35);
                g.setFill(Color.WHITE); g.fillOval(x - 13, 33, 26, 28);
                g.setFill(Color.web(this == COSMIC ? "#42e0de" : this == CANDY ? "#b43b88" : "#e9ae39"));
                g.fillOval(x - 9, 34, 18, 23);
                g.setFill(outline); g.fillOval(x - 6, 34, 12, 18);
                if (this == COSMIC) star(g, x - 2, 41, 5, Color.WHITE);
                else { g.setFill(Color.WHITE); g.fillOval(x - 6, 35, 6, 7); }
            }
            g.setStroke(outline); g.setLineWidth(2.8);
            g.strokeArc(35, 67, 30, 17, 195, 150, javafx.scene.shape.ArcType.OPEN);
            if (this != COSMIC) {
                g.setFill(this == JADE ? Color.web("#237a5c") : Color.web("#ffcfdd"));
                g.fillOval(17, 66, 10, 7); g.fillOval(73, 66, 10, 7);
            }
        } else if (part == Part.TAIL) {
            g.setFill(outline); tail(g, 5, 95);
            g.setFill(fill); tail(g, 10, 90);
            if (this == COSMIC) {
                star(g, 48, 26, 12, Color.web("#94fff1"));
                star(g, 51, 60, 6, Color.web("#ffd6fd"));
            } else if (this == CANDY) {
                g.setStroke(Color.web("#80f5eb")); g.setLineWidth(12);
                g.strokeLine(25, 10, 73, 28); g.strokeLine(39, 48, 60, 56);
            } else {
                g.setStroke(Color.web("#ffda6c")); g.setLineWidth(8);
                g.strokeLine(50, 3, 50, 72);
                g.setStroke(Color.web("#147556")); g.setLineWidth(3);
                g.strokeArc(26, 20, 48, 23, 190, 160, javafx.scene.shape.ArcType.OPEN);
            }
        } else {
            g.setStroke(outline); g.setLineWidth(90); path(g, part);
            g.setStroke(fill); g.setLineWidth(80); path(g, part);
            if (part == Part.BODY) {
                motif(g, 50, 18, 0); motif(g, 50, 51, 0); motif(g, 50, 84, 0);
            } else {
                motif(g, 15, 51, -78); motif(g, 39, 64, -38); motif(g, 50, 90, -4);
            }
        }
    }

    private void motif(GraphicsContext g, double x, double y, double angle) {
        g.save(); g.translate(x, y); g.rotate(angle);
        if (this == COSMIC) {
            g.setStroke(Color.web("#d17eef")); g.setLineWidth(8);
            g.strokeArc(-31, -16, 60, 28, 195, 150, javafx.scene.shape.ArcType.OPEN);
            star(g, -14, -1, 8, Color.web("#a3fff4"));
            g.setFill(Color.web("#ffe6fc")); g.fillOval(19, -8, 6, 6);
        } else if (this == CANDY) {
            g.setStroke(Color.web("#76f1e5")); g.setLineWidth(12);
            g.strokeLine(-30, -9, 30, 8);
            g.setStroke(Color.web("#fff0b8")); g.setLineWidth(4);
            g.strokeLine(-30, 0, 30, 17);
        } else {
            g.setStroke(Color.web("#137654")); g.setLineWidth(3);
            for (int sx : new int[]{-20, 10})
                g.strokeArc(sx - 10, -14, 22, 20, 190, 160, javafx.scene.shape.ArcType.OPEN);
            g.setFill(Color.web("#ffda6c"));
            g.fillRoundRect(-5, -11, 10, 23, 7, 7);
        }
        g.restore();
    }

    private static void star(GraphicsContext g, double x, double y, double r, Color color) {
        g.setFill(color);
        g.fillPolygon(new double[]{x, x+r*.3, x+r, x+r*.3, x, x-r*.3, x-r, x-r*.3},
                new double[]{y-r, y-r*.3, y, y+r*.3, y+r, y+r*.3, y, y-r*.3}, 8);
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
