package com.comp2042;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import java.awt.Point;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

/** Flat base colours and one continuous outline keep every turn and neck seamless. */
public enum SnakeSkin {
    ORANGE("Orange Pop", "#ff971f", "#713716", "#ffd372"),
    SKELETON("Mr. Bones", "#403b59", "#252337", "#fff0cd"),
    COSMIC("Cosmic", "#8050cb", "#302052", "#a2fff0"),
    CANDY("Candy Pop", "#f675b6", "#783652", "#81f2e5"),
    JADE("Jade Dragon", "#35be8d", "#165344", "#ffdb75");

    public enum Part { HEAD, BODY, TURN, TAIL }
    private final String label;
    private final Color base, outline, accent;
    private final EnumMap<Part, Image> images = new EnumMap<>(Part.class);

    SnakeSkin(String label, String base, String outline, String accent) {
        this.label = label;
        this.base = Color.web(base);
        this.outline = Color.web(outline);
        this.accent = Color.web(accent);
    }
    @Override public String toString() { return label; }

    public Image image(Part part) { return images.computeIfAbsent(part, this::render); }

    private Image render(Part part) {
        Canvas canvas = new Canvas(128, 128);
        var g = canvas.getGraphicsContext2D();
        g.scale(1.28, 1.28);
        g.setLineCap(StrokeLineCap.ROUND);
        if (part == Part.HEAD) {
            // Open neck: same width and colour as the straight body.
            g.setFill(outline); g.fillRoundRect(8, 6, 84, 100, 75, 75);
            g.fillRect(8, 50, 84, 50);
            g.setFill(base); g.fillRoundRect(14, 12, 72, 100, 68, 68);
            g.fillRect(14, 50, 72, 50);
            face(g);
        } else {
            g.setStroke(outline); g.setLineWidth(84); tilePath(g, part);
            g.setStroke(base); g.setLineWidth(72); tilePath(g, part);
            if (part == Part.TURN) {
                decoration(g, 17, 52, -70, .75);
                decoration(g, 46, 80, -15, .75);
            } else decoration(g, 50, 50, 0, part == Part.TAIL ? .65 : 1);
        }
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        return canvas.snapshot(params, null);
    }

    private static void tilePath(GraphicsContext g, Part part) {
        g.beginPath();
        if (part == Part.TURN) {
            g.moveTo(-5, 50); g.quadraticCurveTo(50, 50, 50, 105);
        } else if (part == Part.TAIL) {
            g.moveTo(50, -5); g.lineTo(50, 55);
        } else { g.moveTo(50, -5); g.lineTo(50, 105); }
        g.stroke();
    }

    /** Draw the whole snake as a single ribbon, then place markings and facial features. */
    public void drawSnake(GraphicsContext g, Snake snake, double tile) {
        ArrayList<Point> cells = snake.getBody();
        cells.add(snake.getHead());
        drawConnected(g, cells, snake.getSnakeDir(), tile);
    }

    public void drawConnected(GraphicsContext g, List<Point> cells, int headDirection, double tile) {
        if (cells.isEmpty()) return;
        g.save();
        g.scale(tile, tile);
        g.setLineCap(StrokeLineCap.ROUND); g.setLineJoin(StrokeLineJoin.ROUND);
        // Separate subpaths across wrap boundaries rather than crossing the whole board.
        g.setStroke(outline); g.setLineWidth(.84); ribbon(g, cells);
        g.setStroke(base); g.setLineWidth(.72); ribbon(g, cells);
        g.save(); clipBody(g, cells);
        for (int i = 0; i < cells.size() - 1; i++) {
            Point cell = cells.get(i), next = cells.get(i + 1);
            int dx = Integer.signum(next.x - cell.x), dy = Integer.signum(next.y - cell.y);
            if (Math.abs(next.x - cell.x) > 1) dx = -dx;
            if (Math.abs(next.y - cell.y) > 1) dy = -dy;
            double angle = Math.toDegrees(Math.atan2(dx, -dy));
            g.save(); g.translate(cell.x + .5, cell.y + .5); g.scale(.01, .01);
            decoration(g, 0, 0, angle, i == 0 ? .6 : .85);
            g.restore();
        }
        g.restore();
        Point head = cells.get(cells.size() - 1);
        g.translate(head.x + .5, head.y + .5); g.rotate(headDirection);
        g.scale(.01, .01); g.translate(-50, -50); face(g);
        g.restore();
    }

    /** Clip markings to the ribbon so ribs and stripes cannot protrude at elbows. */
    private static void clipBody(GraphicsContext g, List<Point> cells) {
        g.beginPath();
        double r = .36, k = r * .55228475;
        for (int i = 0; i < cells.size(); i++) {
            Point p = cells.get(i); double x = p.x + .5, y = p.y + .5;
            g.moveTo(x + r, y);
            g.bezierCurveTo(x + r, y + k, x + k, y + r, x, y + r);
            g.bezierCurveTo(x - k, y + r, x - r, y + k, x - r, y);
            g.bezierCurveTo(x - r, y - k, x - k, y - r, x, y - r);
            g.bezierCurveTo(x + k, y - r, x + r, y - k, x + r, y); g.closePath();
            if (i > 0) {
                Point previous = cells.get(i - 1);
                if (p.distance(previous) <= 1.1) {
                    if (p.x == previous.x) g.rect(x - r, Math.min(p.y, previous.y) + .5, r * 2, Math.abs(p.y - previous.y));
                    else g.rect(Math.min(p.x, previous.x) + .5, y - r, Math.abs(p.x - previous.x), r * 2);
                }
            }
        }
        g.clip();
    }

    private static void ribbon(GraphicsContext g, List<Point> cells) {
        g.beginPath();
        Point first = cells.get(0);
        g.moveTo(first.x + .5, first.y + .5);
        for (int i = 1; i < cells.size(); i++) {
            Point previous = cells.get(i - 1), current = cells.get(i);
            if (previous.distance(current) > 1.1) {
                g.moveTo(current.x + .5, current.y + .5);
                // A tiny line also renders isolated wrap-side segments with a round cap.
                g.lineTo(current.x + .5001, current.y + .5);
            } else g.lineTo(current.x + .5, current.y + .5);
        }
        if (cells.size() == 1) g.lineTo(first.x + .5001, first.y + .5);
        g.stroke();
    }

    private void face(GraphicsContext g) {
        g.setLineCap(StrokeLineCap.ROUND);
        if (this == SKELETON) {
            g.setFill(accent); g.fillRoundRect(17, 14, 66, 70, 38, 38);
            g.setFill(Color.web("#dfcda5")); g.fillRoundRect(30, 64, 40, 23, 12, 12);
            g.setStroke(outline); g.setLineWidth(3);
            for (int x = 40; x <= 60; x += 10) g.strokeLine(x, 73, x, 83);
            g.setFill(outline); g.fillPolygon(new double[]{50, 44, 56}, new double[]{53, 64, 64}, 3);
        } else {
            if (this == JADE) {
                g.setStroke(outline); g.setLineWidth(12);
                g.strokeLine(24, 26, 21, 12); g.strokeLine(76, 26, 79, 12);
                g.setStroke(accent); g.setLineWidth(6);
                g.strokeLine(24, 26, 21, 12); g.strokeLine(76, 26, 79, 12);
                g.setFill(Color.web("#ffe7a0")); g.fillOval(30, 62, 40, 25);
                g.setStroke(outline); g.setLineWidth(3);
                g.strokeLine(30, 71, 13, 65); g.strokeLine(70, 71, 87, 65);
            } else if (this == COSMIC) star(g, 50, 24, 9, accent);
            else {
                g.setFill(this == ORANGE ? Color.web("#ffcf78") : Color.web("#fff0bc"));
                g.fillOval(32, 17, 36, 13);
            }
            g.setStroke(outline); g.setLineWidth(3);
            g.strokeArc(35, 63, 30, 20, 195, 150, javafx.scene.shape.ArcType.OPEN);
        }
        for (int x : new int[]{29, 71}) {
            g.setFill(outline); g.fillOval(x - 16, 30, 32, 34);
            g.setFill(Color.WHITE); g.fillOval(x - 12, 33, 24, 27);
            g.setFill(this == SKELETON || this == COSMIC ? Color.web("#55d9d3") : outline);
            g.fillOval(x - 8, 34, 16, 21);
            g.setFill(Color.web("#242334")); g.fillOval(x - 5, 34, 10, 17);
            if (this == COSMIC) star(g, x - 2, 41, 5, Color.WHITE);
            else { g.setFill(Color.WHITE); g.fillOval(x - 5, 35, 5, 6); }
        }
    }

    private void decoration(GraphicsContext g, double x, double y, double angle, double scale) {
        g.save(); g.translate(x, y); g.rotate(angle); g.scale(scale, scale);
        g.setLineCap(StrokeLineCap.ROUND);
        if (this == SKELETON) {
            g.setStroke(accent); g.setLineWidth(8); g.strokeLine(0, -48, 0, 48);
            for (int cy : new int[]{-24, 10, 36}) {
                g.setLineWidth(6); g.strokeLine(-25, cy, 25, cy);
                g.setFill(Color.WHITE); g.fillOval(-5, cy - 5, 10, 10);
            }
        } else if (this == COSMIC) {
            g.setStroke(Color.web("#cf89ef")); g.setLineWidth(7);
            g.strokeArc(-27, -30, 52, 44, 195, 150, javafx.scene.shape.ArcType.OPEN);
            star(g, -13, 16, 10, accent); star(g, 19, -17, 6, Color.web("#ffe6fc"));
        } else if (this == CANDY) {
            g.setStroke(accent); g.setLineWidth(11);
            g.strokeLine(-26, -27, 26, -10); g.strokeLine(-26, 10, 26, 27);
            g.setStroke(Color.web("#fff0bc")); g.setLineWidth(4);
            g.strokeLine(-25, -15, 25, 2);
        } else if (this == JADE) {
            g.setStroke(outline.deriveColor(0, 1, 1, .55)); g.setLineWidth(3);
            for (int cy : new int[]{-25, 10})
                for (int cx : new int[]{-19, 13})
                    g.strokeArc(cx - 10, cy, 20, 20, 190, 160, javafx.scene.shape.ArcType.OPEN);
            g.setFill(accent); g.fillRoundRect(-5, -29, 10, 58, 8, 8);
        } else {
            g.setFill(accent); g.fillOval(-24, -31, 12, 19);
            g.fillOval(12, 3, 10, 15); g.fillOval(-17, 26, 8, 10);
        }
        g.restore();
    }

    private static void star(GraphicsContext g, double x, double y, double r, Color color) {
        g.setFill(color);
        g.fillPolygon(new double[]{x, x+r*.3, x+r, x+r*.3, x, x-r*.3, x-r, x-r*.3},
                new double[]{y-r, y-r*.3, y, y+r*.3, y+r, y+r*.3, y, y-r*.3}, 8);
    }
}
