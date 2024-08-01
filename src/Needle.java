import java.awt.*; // lets me use colours

public class Needle {
    private float x; // x position of the needle
    private float y; // y position of the needle
    private int gap; // the (decreasing) size of the gap for the kite to fit through
    private final int SCREEN_SIZE; // the size of the screen it's on, so it knows when to regenerate and sizing
    private int gray; // the colour of the needle (this is repeated three times to make the right shade of grey)
    private final float SPEED; // the speed at which the needle moves
    private final boolean IS_HORIZONTAL; // whether the needle movement is horizontal or vertical

    public Needle(int screenSize, int minGap, boolean isHorizontal) { // constructor for Needle
        this.SCREEN_SIZE = screenSize;
        this.SPEED = 0.85F;
        this.gap = this.SCREEN_SIZE;
        this.IS_HORIZONTAL = isHorizontal;
        this.regenerate(minGap);
    }

    public void move(int minGap) { // moves the needle in the correct direction at the set speed, and if it goes off-screen, regenerates it
        if(this.IS_HORIZONTAL) {
            this.x -= this.SPEED;
            if (this.x < 0) this.regenerate(minGap);
        } else {
            this.y -= this.SPEED;
            if (this.y < 0) this.regenerate(minGap);
        }
    }

    public void show(MyGraphics mg) { // draws the needle on the current pixelated screen
        if(this.IS_HORIZONTAL) {
            mg.makeLine((int) this.x, (int) this.y, 0, (int) this.y, 1, -1, new Color(this.gray, this.gray, this.gray), "obstacle");
            mg.makeLine((int) this.x, (int) this.y + this.gap, 0, (int) (this.SCREEN_SIZE - this.y - this.gap), 1, 1, new Color(this.gray, this.gray, this.gray), "obstacle");
        } else {
            mg.makeLine((int) this.x, (int) this.y, (int) this.x, 0, -1, 1, new Color(this.gray, this.gray, this.gray), "obstacle");
            mg.makeLine((int) this.x + this.gap, (int) this.y, (int) (this.SCREEN_SIZE - this.x - this.gap), 0, 1, 1, new Color(this.gray, this.gray, this.gray), "obstacle");
        }
    }

    public void regenerate(int minGap) { // regenerates the needle with a smaller gap, and new shade of grey, and gives it a random delay until it appears on screen (by changing starting position)
        if(this.IS_HORIZONTAL) this.x = (float) (this.SCREEN_SIZE *(1+Math.random()));
        else this.y = (float) (this.SCREEN_SIZE *(1+Math.random()));
        this.gap -= (this.gap-minGap)/5;
        if(this.gap < minGap) this.gap = minGap;
        if(this.IS_HORIZONTAL) this.y = (int) (Math.random()*(this.SCREEN_SIZE -this.gap));
        else this.x = (int) (Math.random()*(this.SCREEN_SIZE -this.gap));
        this.gray = (int) (Math.random()*75 + 125);
    }
}