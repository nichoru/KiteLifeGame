import java.awt.*;
import java.awt.geom.*;

public class Kite {
    private int x;
    private int y;
    private int width;
    private int lives;

    public Kite(int scale) {
        this.width = scale/96;
        this.lives = 4;
    }

    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;

        Line2D lin  = new Line2D.Float(0, 0, this.width*96, this.width*96);
    }
}
