import java.awt.*; // lets me use colours

public class Kite {
    private int x; // x position of the centre of the kite
    private int y; // y position of the centre of the kite
    private final int WIDTH; // the horizontal distance from the centre of the kite to the corner
    private final int HEIGHT; // the vertical distance from the centre of the kite to the corner
    private int lives; // number of lives the kite currently has (maximum of 4)
    private final Color OUTLINE_COLOR; // colour of the outlines of the kite
    private final Color[] FILL_COLOR = new Color[4]; // colour of each segment of the kite
    private int immunity; // the number of frames until the kite can be hit again
    private final int MAX_IMMUNITY; // the maximum number of frames the kite can be immune for when it loses a life
    private final int SCREEN_SIZE; // the size of the screen the kite is on
    private final float[] XP = new float[4]; // the xp for each segment of the kite
    private final String TYPE; // the type of the kite (for collision)

    public Kite(int screenSize, Color color, float xp, String type, int maxImmunity) { // constructor for Kite - sets a bunch of variables to appropriate starting values
        this.SCREEN_SIZE = screenSize;
        this.WIDTH = this.SCREEN_SIZE/20;
        this.HEIGHT = this.WIDTH*5/3;
        this.lives = 4;
        this.immunity = 0;
        this.MAX_IMMUNITY = maxImmunity;
        this.OUTLINE_COLOR = Color.BLACK;
        this.x = this.SCREEN_SIZE/2;
        this.y = this.SCREEN_SIZE/2;
        for (int i = 0; i < 4; i++) this.FILL_COLOR[i] = color;
        for(int i = 0; i < this.XP.length; i++) this.XP[i] = xp;
        this.TYPE = type;
    }

    public void show(MyGraphics g, int mouseX, int mouseY) { // draws the kite on screen and does collision
        boolean collision = false;
        // set the central coordinates of the kite to the mouse's coordinates
        this.x = mouseX;
        this.y = mouseY;
        // makes sure the kite is always fully displayed on the screen to avoid cheating
        if(this.x < this.WIDTH) this.x = this.WIDTH;
        if(this.x > this.SCREEN_SIZE - this.WIDTH && this.lives >= 2) this.x = this.SCREEN_SIZE - this.WIDTH;
        if(this.y < this.HEIGHT && this.lives >= 3) this.y = this.HEIGHT;
        if(this.y > this.SCREEN_SIZE - this.HEIGHT) this.y = this.SCREEN_SIZE - this.HEIGHT;

        // draws the immunity circle around the kite (if applicable) and gives the player an immunity circle if in the simon minigame
        if(this.immunity > 0 && !this.TYPE.equals("player") && this.lives < 4 && !(this.MAX_IMMUNITY == Main.IMMUNE_TIME && Main.currentGame == 4 && this.lives == 0)) {
            if(this.TYPE.equals("start") || (this.MAX_IMMUNITY == Main.IMMUNE_TIME && Main.currentGame != 1)) g.makeColoredCircle(this.x, this.y, this.HEIGHT*3/2, new Color(((this.FILL_COLOR[0].getRed())*(255*this.immunity/this.MAX_IMMUNITY))/255, ((this.FILL_COLOR[0].getGreen())*(255*this.immunity/this.MAX_IMMUNITY))/255, ((this.FILL_COLOR[0].getBlue())*(255*this.immunity/this.MAX_IMMUNITY))/255), "background");
            else g.makeColoredCircle(this.x, this.y, this.HEIGHT*3/2, new Color(255-((255-this.FILL_COLOR[0].getRed())*(255*this.immunity/this.MAX_IMMUNITY))/255, 255-((255-this.FILL_COLOR[0].getGreen())*(255*this.immunity/this.MAX_IMMUNITY))/255, 255-((255-this.FILL_COLOR[0].getBlue())*(255*this.immunity/this.MAX_IMMUNITY))/255), "background");
        } else if(this.immunity > 0 && !Main.isInstructions && Main.currentGame == 1 && this.lives < 4) g.makeColoredCircle(this.x, this.y, this.HEIGHT*3/2, new Color(255-255*this.immunity/this.MAX_IMMUNITY, 255-255*this.immunity/this.MAX_IMMUNITY, 255-255*this.immunity/this.MAX_IMMUNITY), "background");

        // draws the kite based on how many lives are left, and if it's drawn on top of an obstacle, collision is set to true
        if(this.lives == 4) if(g.makeRATriangle(this.x, this.y, this.WIDTH, this.HEIGHT, 1, -1, this.FILL_COLOR[3], this.OUTLINE_COLOR, this.TYPE)) collision = true;
        if(this.lives >= 3) if(g.makeRATriangle(this.x, this.y, this.WIDTH, this.HEIGHT, -1, -1, this.FILL_COLOR[2], this.OUTLINE_COLOR, this.TYPE)) collision = true;
        if(this.lives >= 2) if(g.makeRATriangle(this.x, this.y, this.WIDTH, this.HEIGHT, 1, 1, this.FILL_COLOR[1], this.OUTLINE_COLOR,  this.TYPE)) collision = true;
        if(this.lives >= 1) if(g.makeRATriangle(this.x, this.y, this.WIDTH, this.HEIGHT, -1, 1, this.FILL_COLOR[0], this.OUTLINE_COLOR, this.TYPE)) collision = true;
        if(this.immunity > 0) this.immunity--; // if the kite recently lost a life (or has immunity for any other reason), decrease the timer on this
        else if(collision) this.loseLife(); // if the kite doesn't have immunity and is on top of an obstacle, lose a life
    }

    public void loseLife() { // what happens when the kite loses a life
        if(this.lives > 0) {
            this.lives--;
            this.immunity = this.MAX_IMMUNITY;
        }
    }

    public int getLives() { // returns how many lives the kite has left
        return this.lives;
    }
    public void setLives(int l) { // sets how many lives the kite has left
        this.lives = l;
    }

    public void kill() { // sets the kite's lives to 0
        this.lives = 0;
    }

    public void makeImmune(int time) { // sets the kite's immunity to a certain amount of frames
        this.immunity = time;
    }

    public boolean isImmune() { // returns whether the kite is immune
        return this.immunity > 0;
    }

    public boolean isAlive() { // returns whether the kite is alive
        return this.lives != 0;
    }

    public void resurrect() { // gives the kite full health and immunity
        this.immunity = this.MAX_IMMUNITY;
        this.lives = 4;
    }

    public void changeColor(Color color, int segment) { // changes the colour of a segment of the kite
        this.FILL_COLOR[segment] = color;
    }

    public void gainXP(float amountXP, int segment, int maxXP) { // increases the xp of a segment of the kite
        this.XP[segment] += amountXP;
        if(this.XP[segment]>maxXP) this.XP[segment] = maxXP;
    }

    public void loseXP(float amountXP, int segment) { // decreases the xp of a segment of the kite
        this.XP[segment] -= amountXP;
        if(this.XP[segment]<0) this.XP[segment] = 0;
    }

    public int getXP(int segment) { // returns the xp of a segment of the kite
        return (int) this.XP[segment];
    }

    public int getWidth() { // returns the width of the kite (this changes as lives decreases and segments are lost)
        if(this.lives > 1) return this.WIDTH*2-1;
        return this.WIDTH;
    }
    public int getHeight() { // returns the height of the kite (this changes as lives decreases and segments are lost)
        if(this.lives > 2) return this.HEIGHT*2-1;
        return this.HEIGHT;
    }
}