import javax.swing.Icon;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

/**
 * Utility class providing vector-rendered crisp icons for the UI.
 */
public final class ModernIcons {

    private ModernIcons() {}

    public static Icon createTimerIcon(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            int padding = 4;
            int r = Math.min(w, h) - padding * 2;
            int cx = w / 2;
            int cy = h / 2 + 1;

            g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(color);
            g2.drawOval(cx - r / 2, cy - r / 2, r, r);

            // Top stopwatch button
            g2.drawLine(cx - 3, cy - r / 2 - 3, cx + 3, cy - r / 2 - 3);
            g2.drawLine(cx, cy - r / 2 - 3, cx, cy - r / 2);

            // Clock hands
            g2.drawLine(cx, cy, cx, cy - r / 3);
            g2.drawLine(cx, cy, cx + r / 4, cy);
        });
    }

    public static Icon createMoveIcon(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(color);

            // Two curved swap arrows
            int p = 3;
            g2.drawArc(p, p, w - p * 2, h - p * 2, 30, 120);
            g2.drawArc(p, p, w - p * 2, h - p * 2, 210, 120);

            // Arrowheads
            Path2D a1 = new Path2D.Double();
            a1.moveTo(w * 0.70, h * 0.15);
            a1.lineTo(w * 0.85, h * 0.28);
            a1.lineTo(w * 0.65, h * 0.35);
            g2.fill(a1);

            Path2D a2 = new Path2D.Double();
            a2.moveTo(w * 0.30, h * 0.85);
            a2.lineTo(w * 0.15, h * 0.72);
            a2.lineTo(w * 0.35, h * 0.65);
            g2.fill(a2);
        });
    }

    public static Icon createScoreIcon(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            int cx = w / 2;
            int cy = h / 2;
            int outerRadius = Math.min(w, h) / 2 - 2;
            int innerRadius = outerRadius / 2;

            Path2D star = new Path2D.Double();
            for (int i = 0; i < 10; i++) {
                double angle = Math.PI / 2 + i * Math.PI / 5;
                double r = (i % 2 == 0) ? outerRadius : innerRadius;
                double x = cx + r * Math.cos(angle);
                double y = cy - r * Math.sin(angle);
                if (i == 0) {
                    star.moveTo(x, y);
                } else {
                    star.lineTo(x, y);
                }
            }
            star.closePath();

            g2.setColor(color);
            g2.fill(star);
        });
    }

    public static Icon createLockIcon(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(color);

            // Shackle
            g2.drawArc((int)(w * 0.30), (int)(h * 0.15), (int)(w * 0.40), (int)(h * 0.45), 0, 180);

            // Body
            int bodyX = (int)(w * 0.22);
            int bodyY = (int)(h * 0.42);
            int bodyW = (int)(w * 0.56);
            int bodyH = (int)(h * 0.45);
            g2.fillRoundRect(bodyX, bodyY, bodyW, bodyH, 6, 6);

            // Keyhole
            g2.setColor(ModernUI.BG_DARK_TOP);
            g2.fillOval(w / 2 - 3, bodyY + 6, 6, 6);
            g2.fillRect(w / 2 - 2, bodyY + 9, 4, 7);
        });
    }

    public static Icon createRestartIcon(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(color);

            int p = 4;
            g2.drawArc(p, p, w - p * 2, h - p * 2, 45, 270);

            // Arrow head
            int ax = (int)(w * 0.72);
            int ay = (int)(h * 0.18);
            Path2D arrow = new Path2D.Double();
            arrow.moveTo(ax - 6, ay);
            arrow.lineTo(ax + 4, ay);
            arrow.lineTo(ax, ay + 8);
            arrow.closePath();
            g2.fill(arrow);
        });
    }

    public static Icon createExitIcon(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(color);

            // Door frame
            g2.drawPolyline(new int[]{(int)(w * 0.55), (int)(w * 0.25), (int)(w * 0.25), (int)(w * 0.55)},
                            new int[]{(int)(h * 0.18), (int)(h * 0.18), (int)(h * 0.82), (int)(h * 0.82)}, 4);

            // Arrow out
            g2.drawLine((int)(w * 0.40), h / 2, (int)(w * 0.82), h / 2);
            Path2D head = new Path2D.Double();
            head.moveTo(w * 0.70, h * 0.35);
            head.lineTo(w * 0.85, h * 0.50);
            head.lineTo(w * 0.70, h * 0.65);
            g2.draw(head);
        });
    }

    public static Icon createMenuIcon(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            int s = (int)(size * 0.32);
            int gap = (int)(size * 0.14);
            int start = (int)(size * 0.11);

            g2.fillRoundRect(start, start, s, s, 4, 4);
            g2.fillRoundRect(start + s + gap, start, s, s, 4, 4);
            g2.fillRoundRect(start, start + s + gap, s, s, 4, 4);
            g2.fillRoundRect(start + s + gap, start + s + gap, s, s, 4, 4);
        });
    }

    public static Icon createPlayIcon(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            Path2D triangle = new Path2D.Double();
            triangle.moveTo(w * 0.28, h * 0.20);
            triangle.lineTo(w * 0.82, h * 0.50);
            triangle.lineTo(w * 0.28, h * 0.80);
            triangle.closePath();
            g2.fill(triangle);
        });
    }

    public static Icon createBackIcon(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setStroke(new BasicStroke(2.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(color);

            Path2D chevron = new Path2D.Double();
            chevron.moveTo(w * 0.65, h * 0.20);
            chevron.lineTo(w * 0.30, h * 0.50);
            chevron.lineTo(w * 0.65, h * 0.80);
            g2.draw(chevron);
        });
    }

    private interface IconPainter {
        void paint(Graphics2D g2, int width, int height);
    }

    private static class VectorIcon implements Icon {
        private final int width;
        private final int height;
        private final IconPainter painter;

        VectorIcon(int width, int height, IconPainter painter) {
            this.width = width;
            this.height = height;
            this.painter = painter;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            ModernUI.enableGraphicsAntialiasing(g2);
            g2.translate(x, y);
            painter.paint(g2, width, height);
            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return width;
        }

        @Override
        public int getIconHeight() {
            return height;
        }
    }
}
