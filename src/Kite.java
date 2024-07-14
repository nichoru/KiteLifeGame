import java.awt.*;

public class Kite {
    private int x;
    private int y;
    private int width;
    private int height;
    private int lives;
    private Color outlineColor = Color.BLACK;
    private Color fillColor;
    private int immunity = 0;

    public Kite(int scale, Color color) {
        this.width = scale/20;
        this.height = this.width*5/3;
        this.lives = 4;
        this.x = 50;
        this.y = 50;
        this.fillColor = color;
        this.immunity = 30;
    }

    public void show(MyGraphics g, int mouseX, int mouseY) {
        boolean collision = false;
        this.x = mouseX;
        this.y = mouseY;
        if(this.lives == 4) if(g.makeRATriangle(this.x, this.y, this.width, this.height, 1, -1, fillColor, outlineColor, "player")) collision = true;
        if(this.lives >= 3) if(g.makeRATriangle(this.x, this.y, this.width, this.height, -1, -1, fillColor, outlineColor, "player")) collision = true;
        if(this.lives >= 2) if(g.makeRATriangle(this.x, this.y, this.width, this.height, 1, 1, fillColor, outlineColor,  "player")) collision = true;
        if(g.makeRATriangle(this.x, this.y, this.width, this.height, -1, 1, fillColor, outlineColor, "player")) collision = true;
        if(immunity > 0) immunity--;
        else if(collision) loseLife();
    }

    private void loseLife() {
        this.lives--;
        this.immunity = 30;
    }
}