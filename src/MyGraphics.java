import java.awt.*;

public class MyGraphics {
    private Graphics2D g2;
    private Color fillColor = Color.WHITE;
    private Color outlineColor = Color.BLACK;

    public MyGraphics(Graphics2D g2) {
        this.g2 = g2;
    }

    public void setFillColor(Color color) {
        this.fillColor = color;
    }
    public void setOutlineColor(Color color) {
        this.outlineColor = color;
    }

    public void makeColoredCircle(int x, int y, int radius, Color f, Color o) {
        g2.setColor(f);
        g2.fillOval(x-radius, y-radius, radius*2, radius*2);
        g2.setColor(o);
        g2.drawOval(x-radius, y-radius, radius*2, radius*2);
    }
    public void makeTriangle(int x1, int x2, int x3, int y1, int y2, int y3) {
        g2.setColor(this.fillColor);
        g2.fillPolygon(new int[]{x1, x2, x3}, new int[]{y1, y2, y3}, 3);
        g2.setColor(this.outlineColor);
        g2.drawPolygon(new int[]{x1, x2, x3}, new int[]{y1, y2, y3}, 3);
    }

}
