import java.awt.*;

public class MyGraphics {
    private Color fillColor = Color.WHITE;
    private Color outlineColor = Color.BLACK;
    private float pixelWidth;

    public MyGraphics(float pixelWidth) { this.pixelWidth = pixelWidth; }

    public void setFillColor(Color color) {
        this.fillColor = color;
    }
    public void setOutlineColor(Color color) {
        this.outlineColor = color;
    }

    public void changeColor(int x, int y, Color color) {

        System.out.println(x/pixelWidth+"! "+y/pixelWidth+", "+pixelWidth);
        Main.screen[1][(int) (x/pixelWidth)][(int) (y/pixelWidth)] = color;
    }

    public void makeColoredCircle(int x, int y, int radius, Color f, Color o) {
//        g2.setColor(f);
//        g2.fillOval(x-radius, y-radius, radius*2, radius*2);
//        g2.setColor(o);
//        g2.drawOval(x-radius, y-radius, radius*2, radius*2);
    }

    public void makeRATriangle(int x, int y, int w, int h, Color fill, Color outline) {
        System.out.println(w/pixelWidth);
        for(int i=0; i < w; i++) changeColor((int) (x+i*pixelWidth), y, fill);
        for(int i=0; i < h; i++) changeColor(x, (int) (y+i*pixelWidth), fill);
    }

}
