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

    public void show(MyGraphics g, int mouseX, int mouseY) {
        this.x = mouseX;
        this.y = mouseY;
        if(this.lives == 4) g.makeRATriangle(this.x, this.y, this.width, this.height, fillColor, outlineColor);
//        if(this.lives >= 3) g.makeRATriangle(this.x - this.width, this.x, this.x, this.y, this.y - this.height, this.y);
//        if(this.lives >= 2) g.makeRATriangle(this.x, this.x + this.width, this.x, this.y + this.height, this.y, this.y);
//        g.makeRATriangle(this.x, this.x - this.width, this.x, this.y + this.height, this.y, this.y);
    }
}
