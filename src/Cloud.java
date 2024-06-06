import java.awt.*;

public class Cloud {
    private int x;
    private int y;
    private int scale;
    private int gray1;
    private int gray2;
    private int gray3;
    private int gray4;

    public Cloud(int x, int scale) {
        this.x = 100;
        this.y = 100;
        this.scale = scale;
        this.colorGenerate();
    }

    public void show(MyGraphics g) {
        g.setFillColor(Color.GREEN);
        g.makeColoredCircle(this.x - 11*scale/288, this.y + scale/144, scale/36, new Color(gray1, gray1, gray1), new Color(gray1, gray1, gray1));
        g.makeColoredCircle(this.x + 5*scale/144, this.y + 3*scale/288, 7*scale/288, 7*scale/288);
        g.makeColoredCircle(this.x + 40, this.y, 50, 50);
        g.makeColoredCircle(this.x + 20, this.y + 10, 50, 50);
    }

    private void colorGenerate() {
        this.gray1 = (int) (Math.random()*255);
        this.gray2 = (int) (Math.random()*255);
        this.gray3 = (int) (Math.random()*255);
        this.gray4 = (int) (Math.random()*255);
    }
}
