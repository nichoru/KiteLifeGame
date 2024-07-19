import java.awt.*;

public class Needle {
    private int x;
    private float y;
    private int screenSize;
    private int scale;
    private int gray;
    private float speed;

    public Needle(int screenSize) {
        this.screenSize = screenSize;
        this.scale = this.screenSize*9/5;
        this.speed = 0.85F;
        this.regenerate();
    }

    public void move() {
        this.x+=this.speed;
        this.speed+= 0.001F;
        if(this.x < 0) this.regenerate();
    }

    public void show(MyGraphics g) {
        g.makeLine(this.x, (int) this.y, 0, (int) this.y, 1, -1, new Color(this.gray, this.gray, this.gray), "obstacle");
    }

    public void regenerate() {
        this.x = screenSize;
        this.y = (int) (Math.random()*screenSize);
        this.gray = (int) (Math.random()*75 + 125);
    }
}
