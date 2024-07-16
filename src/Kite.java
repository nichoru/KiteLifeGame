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
    private int screenSize;
    private float xp;

    public Kite(int screenSize, Color color, float xp) {
        this.screenSize = screenSize;
        this.width = this.screenSize/20;
        this.height = this.width*5/3;
        this.lives = 4;
        this.x = this.screenSize/2;
        this.y = this.screenSize/2;
        this.fillColor = color;
        this.xp = xp;
    }

    public void show(MyGraphics g, int mouseX, int mouseY) { // draws the kite on screen and does collision
        boolean collision = false;
        // set the central coordinates of the kite to the mouse's coordinates
        this.x = mouseX;
        this.y = mouseY;
        // makes sure the kite is always fully displayed on the screen to avoid cheating
        if(this.x < this.width) this.x = this.width;
        if(this.x > this.screenSize - this.width && this.lives >= 2) this.x = this.screenSize - this.width;
        if(this.y < this.height && this.lives >= 3) this.y = this.height;
        if(this.y > this.screenSize - this.height) this.y = this.screenSize - this.height;

        // draws the kite based on how many lives are left, and if it's drawn on top of an obstacle, collision is set to true
        if(this.lives == 4) if(g.makeRATriangle(this.x, this.y, this.width, this.height, 1, -1, fillColor, outlineColor, "player")) collision = true;
        if(this.lives >= 3) if(g.makeRATriangle(this.x, this.y, this.width, this.height, -1, -1, fillColor, outlineColor, "player")) collision = true;
        if(this.lives >= 2) if(g.makeRATriangle(this.x, this.y, this.width, this.height, 1, 1, fillColor, outlineColor,  "player")) collision = true;
        if(g.makeRATriangle(this.x, this.y, this.width, this.height, -1, 1, fillColor, outlineColor, "player")) collision = true;
        if(immunity > 0) immunity--; // if the kite recently lost a life (or has immunity for any other reason), decrease the timer on this
        else if(collision) loseLife(); // if the kite doesn't have immunity and is on top of an obstacle, lose a life
    }

    private void loseLife() { // what happens when the kite loses a life
        if(this.lives > 0) {
            this.lives--;
            this.immunity = 30;
        }
    }

    public boolean isAlive() {
        return this.lives != 0;
    }

    public void gainXP(float amountXP) {
        this.xp += amountXP;
    }

    public int getXP() {
        return (int) this.xp;
    }
}