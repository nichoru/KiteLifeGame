import java.awt.*;

public class Needle {
    private float x;
    private float y;
    private int gap;
    private int screenSize;
    private int gray;
    private float speed;
    private boolean horizontal;

    public Needle(int screenSize, int minGap, boolean horizontal) {
        this.screenSize = screenSize;
        this.speed = 0.85F;
        this.gap = this.screenSize;
        this.horizontal = horizontal;
        this.regenerate(minGap);
    }

    public void move(int minGap) {
        if(this.horizontal) {
            this.x -= this.speed;
            if (this.x < 0) this.regenerate(minGap);
        } else {
            this.y -= this.speed;
            if (this.y < 0) this.regenerate(minGap);
        }
    }

    public void show(MyGraphics g) {
        if(this.horizontal) {
            g.makeLine((int) this.x, (int) this.y, 0, (int) this.y, 1, -1, new Color(this.gray, this.gray, this.gray), "obstacle");
            g.makeLine((int) this.x, (int) this.y + this.gap, 0, (int) (this.screenSize - this.y - this.gap), 1, 1, new Color(this.gray, this.gray, this.gray), "obstacle");
        } else {
            g.makeLine((int) this.x, (int) this.y, (int) this.x, 0, -1, 1, new Color(this.gray, this.gray, this.gray), "obstacle");
            System.out.println("hi");
            g.makeLine((int) this.x + this.gap, (int) this.y, (int) (this.screenSize - this.x - this.gap), 0, 1, 1, new Color(this.gray, this.gray, this.gray), "obstacle");
        }
    }

    public void regenerate(int minGap) {
        if(this.horizontal) this.x = (float) (this.screenSize*(1+Math.random()));
        else this.y = (float) (this.screenSize*(1+Math.random()));
        this.gap -= (this.gap-minGap)/5;
        if(this.gap < minGap) this.gap = minGap;
        if(this.horizontal) this.y = (int) (Math.random()*(this.screenSize-this.gap));
        else this.x = (int) (Math.random()*(this.screenSize-this.gap));
        this.gray = (int) (Math.random()*75 + 125);
    }
}
