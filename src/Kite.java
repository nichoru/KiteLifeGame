import java.awt.*;

public class Kite {
    private int x;
    private int y;
    private int width;
    private int height;
    private int lives;
    private Color outlineColor = Color.BLACK;
    private Color[] fillColor = new Color[4];
    private int immunity = 0;
    private int maxImmunity;
    private int screenSize;
    private float[] xp = new float[4];
    private String type;

    public Kite(int screenSize, Color color, float xp, String type, int maxImmunity) {
        this.screenSize = screenSize;
        this.width = this.screenSize/20;
        this.height = this.width*5/3;
        this.lives = 4;
        this.maxImmunity = maxImmunity;
        this.x = this.screenSize/2;
        this.y = this.screenSize/2;
        for (int i = 0; i < 4; i++) this.fillColor[i] = color;
        for(int i = 0; i < this.xp.length; i++) this.xp[i] = xp;
        this.type = type;
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

        if(this.immunity > 0 && !this.type.equals("player") && this.isAlive()) g.makeColoredCircle(this.x, this.y, this.height*3/2, new Color(255-((255-this.fillColor[this.lives-1].getRed())*(255*this.immunity/this.maxImmunity))/255, 255-((255-this.fillColor[this.lives-1].getGreen())*(255*this.immunity/this.maxImmunity))/255, 255-((255-this.fillColor[this.lives-1].getBlue())*(255*this.immunity/this.maxImmunity))/255), "background");
        // draws the kite based on how many lives are left, and if it's drawn on top of an obstacle, collision is set to true
        if(this.lives == 4) if(g.makeRATriangle(this.x, this.y, this.width, this.height, 1, -1, this.fillColor[3], this.outlineColor, this.type)) collision = true;
        if(this.lives >= 3) if(g.makeRATriangle(this.x, this.y, this.width, this.height, -1, -1, this.fillColor[2], this.outlineColor, this.type)) collision = true;
        if(this.lives >= 2) if(g.makeRATriangle(this.x, this.y, this.width, this.height, 1, 1, this.fillColor[1], this.outlineColor,  this.type)) collision = true;
        if(g.makeRATriangle(this.x, this.y, this.width, this.height, -1, 1, this.fillColor[0], this.outlineColor, this.type)) collision = true;
        if(this.immunity > 0) this.immunity--; // if the kite recently lost a life (or has immunity for any other reason), decrease the timer on this
        else if(collision) this.loseLife(); // if the kite doesn't have immunity and is on top of an obstacle, lose a life
    }

    public void loseLife() { // what happens when the kite loses a life
        if(this.lives > 0) {
            this.lives--;
            System.out.println("hi");
            this.immunity = this.maxImmunity;
        }
    }

    public int getLives() {
        return this.lives;
    }
    public void setLives(int l) {
        this.lives = l;
    }

    public void kill() {this.lives = 0;}


    public void makeImmune(int time) {
        this.immunity = time;
    }

    public boolean isImmune() {
        return this.immunity > 0;
    }

    public boolean isAlive() {
        return this.lives != 0;
    }

    public void resurrect() {
        this.immunity = 0;
        this.lives = 4;
    }

    public void changeColor(Color color, int segment) {
        this.fillColor[segment] = color;
    }

    public void gainXP(float amountXP, int segment, int maxXP) {
        this.xp[segment] += amountXP;
        if(this.xp[segment]>maxXP) this.xp[segment] = maxXP;
    }

    public void loseXP(float amountXP, int segment) {
        this.xp[segment] -= amountXP;
    }

    public int getXP(int segment) {
        return (int) this.xp[segment];
    }

    public int getWidth() {
        if(this.lives > 1) return this.width*2-1;
        return this.width;
    }
    public int getHeight() {
        if(this.lives > 2) return this.height*2-1;
        return this.height;
    }
}