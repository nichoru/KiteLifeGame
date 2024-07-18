import java.awt.*;

public class Cloud {
    private int x;
    private float y;
    private int screenSize;
    private int scale;
    private int gray1;
    private int gray2;
    private int gray3;
    private int gray4;
    private float speed;

    public Cloud(int screenSize) {
        this.screenSize = screenSize;
        this.scale = this.screenSize*9/5;
        this.speed = 0.85F;
        this.regenerate();
    }

    public void move() {
        this.y+=this.speed;
        this.speed+= 0.001F;
        if(this.y > this.screenSize + 5*scale/144) this.regenerate();
    }

    public void show(MyGraphics g) {
        g.makeColoredCircle(this.x - 11*scale/288, (int) this.y + scale/144, scale/36, new Color(gray1, gray1, gray1), "obstacle");
        g.makeColoredCircle(this.x + 5*scale/144, (int) this.y + 3*scale/288, 7*scale/288, new Color(gray2, gray2, gray2), "obstacle");
        g.makeColoredCircle(this.x, (int) this.y, 5*scale/144, new Color(gray3, gray3, gray3), "obstacle");
        g.makeColoredCircle(this.x - 7*scale/96, (int) this.y + 5*scale/288, scale/63, new Color(gray4, gray4, gray4), "obstacle");
    }

    public void regenerate() {
        this.x = (int) (Math.random()*screenSize);
        this.y = (int) (-Math.random()*screenSize-5*scale/144);
        this.gray1 = (int) (Math.random()*75 + 125);
        this.gray2 = (int) (Math.random()*75 + 125);
        this.gray3 = (int) (Math.random()*75 + 125);
        this.gray4 = (int) (Math.random()*75 + 125);
    }
}