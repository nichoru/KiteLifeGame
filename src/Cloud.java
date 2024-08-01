import java.awt.*; // lets me use colours

public class Cloud {
    private int x; // x position of the cloud
    private float y; // y position of the cloud
    private final int SCREEN_SIZE; // the size of the screen it's on, so it knows when to regenerate and sizing
    private final int SCALE; // helps with the size of the cloud
    private final int[] GRAY = new int[4]; // the colour of each part of the cloud (these are each repeated three times to make the right shade of grey)
    private float speed; // the (increasing) speed at which the cloud moves

    public Cloud(int screenSize) { // constructor for Cloud
        this.SCREEN_SIZE = screenSize;
        this.SCALE = this.SCREEN_SIZE*9/5;
        this.speed = 0.85F;
        this.regenerate();
    }

    public void move() { // moves the cloud downwards at the set speed (while increasing it), and if it goes off-screen, regenerates it
        this.y+=this.speed;
        this.speed+= 0.001F;
        if(this.y > this.SCREEN_SIZE + 5*this.SCALE/144) this.regenerate();
    }

    public void show(MyGraphics mg) { // draws the cloud on the current pixelated screen
        mg.makeColoredCircle(this.x - 11*this.SCALE/288, (int) this.y + this.SCALE/144, this.SCALE/36, new Color(this.GRAY[0], this.GRAY[0], this.GRAY[0]), "obstacle");
        mg.makeColoredCircle(this.x + 5*this.SCALE/144, (int) this.y + 3*this.SCALE/288, 7*this.SCALE/288, new Color(this.GRAY[1], this.GRAY[1], this.GRAY[1]), "obstacle");
        mg.makeColoredCircle(this.x, (int) this.y, 5*this.SCALE/144, new Color(this.GRAY[2], this.GRAY[2], this.GRAY[2]), "obstacle");
        mg.makeColoredCircle(this.x - 7*this.SCALE/96, (int) this.y + 5*this.SCALE/288, this.SCALE/63, new Color(this.GRAY[3], this.GRAY[3], this.GRAY[3]), "obstacle");
    }

    public void regenerate() { // regenerates the cloud with a new shade of grey, and gives it a random delay until it appears on screen (by changing starting position)
        this.x = (int) (Math.random()*this.SCREEN_SIZE);
        this.y = (int) (-Math.random()*this.SCREEN_SIZE - 5*this.SCALE/144);
        for(int i = 0; i < this.GRAY.length; i++) this.GRAY[i] = (int) (Math.random()*75 + 125);
    }
}