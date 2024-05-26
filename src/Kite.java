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
    private Color outlineColor = Color.BLACK;
    private Color fillColor;

    public Kite(int scale, Color color) {
        this.width = scale/20;
        this.height = this.width*5/3;
        this.lives = 4;
        this.x = 50;
        this.y = 50;
        this.fillColor = color;
    }

    public void show(Graphics2D g2, int mouseX, int mouseY) {
        this.x = mouseX;
        this.y = mouseY;
        if(this.lives == 4) makeTriangle(g2, this.x + this.width, this.x, this.x, this.y, this.y - this.height, this.y);
        if(this.lives >= 3) makeTriangle(g2, this.x - this.width, this.x, this.x, this.y, this.y - this.height, this.y);
        if(this.lives >= 2) makeTriangle(g2, this.x, this.x + this.width, this.x, this.y + this.height, this.y, this.y);
        makeTriangle(g2, this.x, this.x - this.width, this.x, this.y + this.height, this.y, this.y);
    }

    private void makeTriangle(Graphics2D g2, int x1, int x2, int x3, int y1, int y2, int y3) {
        g2.setColor(this.fillColor);
        g2.fillPolygon(new int[]{x1, x2, x3}, new int[]{y1, y2, y3}, 3);
        g2.setColor(this.outlineColor);
        g2.drawPolygon(new int[]{x1, x2, x3}, new int[]{y1, y2, y3}, 3);
        }
}
