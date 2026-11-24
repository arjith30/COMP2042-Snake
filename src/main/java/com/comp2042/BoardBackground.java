package com.comp2042;

import com.comp2042.model.Level;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/** Decorative level themes; these drawings never create collision cells. */
public final class BoardBackground {
    private BoardBackground() { }

    /** Draws a stable, readable grid with level-specific scenery and no moving distractions. */
    public static void draw(GraphicsContext g, Level level, int columns, int rows, double tile) {
        Color light = Color.web(switch (level) {
            case MEADOW -> "#a3d56b";
            case HEDGES -> "#efcd80";
            case RUINS -> "#bacbdf";
        });
        Color dark = Color.web(switch (level) {
            case MEADOW -> "#98cb60";
            case HEDGES -> "#e5bf72";
            case RUINS -> "#adbed5";
        });
        for (int y = 0; y < rows; y++) for (int x = 0; x < columns; x++) {
            g.setFill((x + y) % 2 == 0 ? light : dark);
            double left = Math.round(x * tile), top = Math.round(y * tile);
            g.fillRect(left, top, Math.round((x + 1) * tile) - left, Math.round((y + 1) * tile) - top);
            if ((x * 7 + y * 11) % 5 != 0) continue;
            double px = (x + .23) * tile, py = (y + .78) * tile;
            if (level == Level.MEADOW) {
                g.setStroke(Color.web("#6ba64b", .36)); g.setLineWidth(Math.max(1, tile * .035));
                g.strokeLine(px, py, px - tile * .07, py - tile * .12);
                g.strokeLine(px, py, px + tile * .05, py - tile * .16);
            } else if (level == Level.HEDGES) {
                g.setFill(Color.web("#bd8c47", .3));
                g.fillOval(px, py, tile * .15, tile * .07);
                g.setFill(Color.web("#fff0bb", .6));
                g.fillOval(px + tile * .08, py - tile * .09, tile * .09, tile * .06);
            } else {
                g.setStroke(Color.web("#dce6f2", .65)); g.setLineWidth(Math.max(1, tile * .025));
                g.strokeLine(px, py, px + tile * .16, py);
                g.strokeLine(px + tile * .08, py, px + tile * .13, py - tile * .08);
            }
        }
    }
}
