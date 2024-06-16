import javax.swing.*;
import java.awt.*;
import java.util.Scanner;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage; // reduces flickering by buffering the image

public class Main extends JFrame implements ActionListener, MouseListener, MouseMotionListener {

    private JPanel window = new JPanel();
    private Canvas canvas = new Canvas();
    private String title = "Kite Life";
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private int windowWidth = screenSize.width/2;
    private int windowHeight = screenSize.height/2;
    private int mouseX;
    private int mouseY;
    private BufferedImage offScreenImage;
    private int xOffset = 8;
    private int yOffset = 31;
    private int screenPixelSize = 150;
    private Kite player;
    private Cloud cloud1;
    private Color[][][] screen = new Color[2][screenPixelSize][screenPixelSize];

    public static void main(String[] args) {
        new Main();
    } // makes a new Main object

    public Main() {
        setTitle(title);
        if(windowWidth < windowHeight) windowHeight = windowWidth; // makes the window a square that's based on the smaller out of the user's screen height and width
        else windowWidth = windowHeight;
        getContentPane().setPreferredSize(new Dimension(windowWidth, windowHeight));
        getContentPane().setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        window.setPreferredSize(new Dimension(windowWidth, windowHeight));
        window.add(canvas);

        setLocation((screenSize.width-windowWidth)/2, (screenSize.height-windowHeight)/2); // centres the window on the user's screen

        addMouseListener(this);
        addMouseMotionListener(this);

        this.pack();
        this.toFront();
        this.setVisible(true);

        player = new Kite(windowWidth, Color.YELLOW);
        cloud1 = new Cloud(windowWidth, windowHeight);
        for(int i = 0; i < windowWidth; i++) {
            for(int j = 0; j < windowHeight; j++) {
                screen[1][i][j] = Color.WHITE;
                screen[2][i][j] = Color.WHITE;
            }
        }
    }

    public void actionPerformed(ActionEvent e) {

    }

    public void mouseExited(MouseEvent e) {System.out.println("exit");}
    public void mouseEntered(MouseEvent e) {System.out.println("enter");}
    public void mouseReleased(MouseEvent e) {System.out.println("release");}
    public void mousePressed(MouseEvent e) {System.out.println("press");}
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX() - xOffset;
        mouseY = e.getY() - yOffset;
        repaint();
    }
    public void mouseDragged(MouseEvent e) {System.out.println("drag");}
    public void mouseClicked(MouseEvent e) {
        System.out.println("click at "+e.getX()+", "+e.getY());
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if(offScreenImage == null) offScreenImage = new BufferedImage(windowWidth, windowHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D) offScreenImage.getGraphics();


        MyGraphics mg = new MyGraphics(g2);
        if(player != null) player.show(mg, mouseX, mouseY);
        if(cloud1 != null) {
            cloud1.move();
            cloud1.show(mg);
        }

        for(int i = 0; i < screenPixelSize; i++) {
            for(int j = 0; j < screenPixelSize; j++) {
                if(screen[1][i][j] != screen[2][i][j]) {
                    g2.setColor(screen[2][i][j]);
                    g2.fillRect(i * windowWidth / screenPixelSize, j * windowHeight / screenPixelSize, windowWidth / screenPixelSize + 1, windowHeight / screenPixelSize + 1);
                    screen[1][i][j] = screen[2][i][j];
                }
            }
        }
        g.drawImage(offScreenImage, xOffset, yOffset, null);
    }
}