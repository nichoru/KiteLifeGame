import java.awt.*;

public class Needle {
    private float x;
    private float y;
    private int gap;
    private int screenSize;
    private int gray;
    private float speed;

    public Needle(int screenSize, int minGap) {
        this.screenSize = screenSize;
        this.speed = 0.85F;
        this.gap = this.screenSize;
        this.regenerate(minGap);
    }

    public void move(int minGap) {
        this.x-=this.speed;
        if(this.x < 0) this.regenerate(minGap);
    }

    public void show(MyGraphics g) {
        g.makeLine((int) this.x, (int) this.y, 0, (int) this.y, 1, -1, new Color(this.gray, this.gray, this.gray), "obstacle");
        g.makeLine((int) this.x, (int) this.y+this.gap, 0, (int) (this.screenSize - this.y - this.gap), 1, 1, new Color(this.gray, this.gray, this.gray), "obstacle");
    }

    public void regenerate(int minGap) {
        this.x = this.screenSize;
        this.gap -= (this.gap-minGap)/5;
        if(this.gap < minGap) this.gap = minGap;
        this.y = (int) (Math.random()*(this.screenSize-this.gap));
        this.gray = (int) (Math.random()*75 + 125);
    }
}
