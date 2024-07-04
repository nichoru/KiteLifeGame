import java.awt.*;
import java.util.Objects;

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

    private boolean colorIn(int x, int y, Color color, String type) {
        if(x >= 0 && x < Main.screen[1].length && y >= 0 && y < Main.screen[1][0].length) {
            Main.screen[1][x][y] = color;
            if(Main.screenType[x][y].equals(type) || Main.screenType[x][y].equals("background")) {
                Main.screenType[x][y] = type;
            } else {
                Main.screenType[x][y] = type;
                return true;
            }
        }
        return false;
    }

    public void makeColoredCircle(int x, int y, int radius, Color f, Color o, String type) {
        for(int i=0; i < radius; i++) {
            for(int j=0; j < radius; j++) {
                if(i*i+j*j < radius*radius) {
                    colorIn(x+i, y+j, f, type);
                    colorIn(x-i, y+j, f, type);
                    colorIn(x+i, y-j, f, type);
                    colorIn(x-i, y-j, f, type);
                }
            }
        }
    }

    public boolean makeRATriangle(int x, int y, int w, int h, int xD, int yD, Color fill, Color outline, String type) {
        boolean collision = false;
        for(int i=0; i < w; i++) if(colorIn(x+i*xD,y,fill, type)) collision = true;
        for(int i=0; i < h; i++) if(colorIn(x,y+i*yD,fill, type)) collision = true;
        if(h<w) {
            for (int i = 0; i < w; i++) if (colorIn(x + i * xD, y - yD * (h + i * h / w), fill, type)) collision = true;
        } else for(int i=0; i < h; i++) if(colorIn(x+xD*(w-i*w/h),y+i*yD,fill, type)) collision = true;

        return collision;
    }

}
