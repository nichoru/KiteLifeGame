import java.awt.*;

public class Cloud {
    private int x;
    private int y;
    private int scale;
    private  int yScale;
    private int gray1;
    private int gray2;
    private int gray3;
    private int gray4;

    public Cloud(int scale, int yScale) {
        this.scale = scale;
        this.yScale = yScale;
        this.regenerate();
    }

    public void move() {
        this.y++;
        if(this.y > this.yScale) this.regenerate();
    }

    public void show(MyGraphics g) {
        g.makeColoredCircle(this.x - 11*scale/288, this.y + scale/144, scale/36, new Color(gray1, gray1, gray1), new Color(gray1, gray1, gray1));
        g.makeColoredCircle(this.x + 5*scale/144, this.y + 3*scale/288, 7*scale/288, new Color(gray2, gray2, gray2), new Color(gray2, gray2, gray2));
        g.makeColoredCircle(this.x, this.y, 5*scale/144, new Color(gray3, gray3, gray3), new Color(gray3, gray3, gray3));
        g.makeColoredCircle(this.x - 7*scale/96, this.y + 3*scale/144, scale/72, new Color(gray4, gray4, gray4), new Color(gray4, gray4, gray4));
    }

    public void regenerate() {
        this.x = (int) (Math.random()*scale);
        this.y = (int) 50;
        this.gray1 = (int) (Math.random()*75 + 125);
        this.gray2 = (int) (Math.random()*75 + 125);
        this.gray3 = (int) (Math.random()*75 + 125);
        this.gray4 = (int) (Math.random()*75 + 125);
    }
}
