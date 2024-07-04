import java.awt.*;

public class Cloud {
    private int x;
    private float y;
    private float speed;
    private int scale;
    private int gray1;
    private int gray2;
    private int gray3;
    private int gray4;

    public Cloud(int scale) {
        this.scale = scale;
        this.regenerate();
    }

    public void move() {
        this.y+=this.speed;
        if(this.y > this.scale) this.regenerate();
    }

    public void show(MyGraphics g) {
        g.makeColoredCircle(this.x - 11*scale/288, (int) this.y + scale/144, scale/36, new Color(gray1, gray1, gray1), new Color(gray1, gray1, gray1));
        g.makeColoredCircle(this.x + 5*scale/144, (int) this.y + 3*scale/288, 7*scale/288, new Color(gray2, gray2, gray2), new Color(gray2, gray2, gray2));
        g.makeColoredCircle(this.x, (int) this.y, 5*scale/144, new Color(gray3, gray3, gray3), new Color(gray3, gray3, gray3));
        g.makeColoredCircle(this.x - 7*scale/96, (int) this.y + 3*scale/144, scale/72, new Color(gray4, gray4, gray4), new Color(gray4, gray4, gray4));
    }

    public void regenerate() {
        this.x = (int) (Math.random()*scale);
        this.speed = (float) (Math.random());
        System.out.println(this.speed);
        this.y = 0;
        this.gray1 = (int) (Math.random()*75 + 125);
        this.gray2 = (int) (Math.random()*75 + 125);
        this.gray3 = (int) (Math.random()*75 + 125);
        this.gray4 = (int) (Math.random()*75 + 125);
    }
}
