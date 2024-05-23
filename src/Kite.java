import javax.swing.*;
import java.awt.*;
import java.util.Scanner;
import java.awt.event.*; // listener
import java.awt.geom.*;

public class Kite {
    private int x;
    private int y;
    private int width;
    private int height;
    private int lives;

    public Kite(int scale, int x1, int y1) {
        this.width = scale/96;
        this.height = this.width*5/3;
        this.lives = 4;
        this.x = 50;
        this.y = 50;
    }

    public void show(Graphics2D g2, int mouseX, int mouseY) {
        this.x = mouseX;
        this.y = mouseY;
        if(this.lives == 4) g2.drawPolygon(new int[]{this.x + this.width, this.x, this.x}, new int[]{this.y, this.y - this.height, this.y}, 3);
        if(this.lives >= 3) g2.drawPolygon(new int[]{this.x - this.width, this.x, this.x}, new int[]{this.y, this.y - this.height, this.y}, 3);
        if(this.lives >= 2) g2.drawPolygon(new int[]{this.x, this.x + this.width, this.x}, new int[]{this.y + this.height, this.y, this.y}, 3);
        g2.drawPolygon(new int[]{this.x, this.x - this.width, this.x}, new int[]{this.y + this.height, this.y, this.y}, 3);
    }
}
