import java.awt.*;

public class MyGraphics {
    private Color fillColor = Color.WHITE;
    private Color outlineColor = Color.BLACK;
    private float pixelWidth;
    private Graphics2D g2;

    public MyGraphics(float pixelWidth, Graphics2D g2) {
        this.pixelWidth = pixelWidth;
        this.g2 = g2;
    }

    public void setFillColor(Color color) {
        this.fillColor = color;
    }
    public void setOutlineColor(Color color) {
        this.outlineColor = color;
    }


    public void makeColoredCircle(int x, int y, int radius, Color f, Color o) {
        for(int i=0; i < radius; i++) {
            for(int j=0; j < radius; j++) {
                if(i*i+j*j < radius*radius) {
                    Main.screen[1][x+i][y+j] = f;
                    Main.screen[1][x-i][y+j] = f;
                    Main.screen[1][x+i][y-j] = f;
                    Main.screen[1][x-i][y-j] = f;
                }
            }
        }
    }

    public void makeRATriangle(int x, int y, int w, int h, int xD, int yD, Color fill, Color outline) {
        for(int i=0; i < w; i++) Main.screen[1][x+i*xD][y] = fill;
        for(int i=0; i < h; i++) Main.screen[1][x][y+i*yD] = fill;
        if(h<w) for(int i=0; i < w; i++) Main.screen[1][x+i*xD][y-yD*(h+i*h/w)] = fill;
        else for(int i=0; i < h; i++) Main.screen[1][x+xD*(w-i*w/h)][y+i*yD] = fill;
    }

}
