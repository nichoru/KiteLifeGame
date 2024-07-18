import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Main extends JFrame implements ActionListener, MouseListener, MouseMotionListener {

    private JPanel window = new JPanel();
    private Canvas canvas = new Canvas();
    private String title = "Kite Life";
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private int windowWidth = screenSize.width/2;
    private int windowHeight = screenSize.height/2;
    private int mouseX;
    private int mouseY;
    private int xOffset = 8;
    private int yOffset = 31;
    private static int screenPixelSize = 150;
    private MyGraphics mg = new MyGraphics(Color.BLACK);
    private Kite player;
    public static int currentGame;
    public static int nextGame;
    private Kite[] buttons = new Kite[4];
    private Color[] buttonColors = {Color.YELLOW, Color.MAGENTA, Color.CYAN, Color.RED};
    private Cloud cloud1;
    private Cloud cloud2;
    private Cloud cloud3;
    public static Color[][][] screen = new Color[2][screenPixelSize][screenPixelSize];
    public static String[][] screenType = new String[screenPixelSize][screenPixelSize];
    private boolean isStart = true;
    private final int UPDATE_SPEED = 10; // milliseconds between screen updates

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

        player = new Kite(screenPixelSize, Color.WHITE, 0, "player");
        kiteHome();
    }

    public void actionPerformed(ActionEvent e) {

    }

    public void mouseExited(MouseEvent e) {System.out.println("exit");}
    public void mouseEntered(MouseEvent e) {System.out.println("enter");}
    public void mouseReleased(MouseEvent e) {System.out.println("release");}
    public void mousePressed(MouseEvent e) {System.out.println("press");}
    public void mouseMoved(MouseEvent e) {
        mouseX = (e.getX() - xOffset)*screenPixelSize/windowWidth;
        mouseY = (e.getY() - yOffset)*screenPixelSize/windowWidth;
    }
    public void mouseDragged(MouseEvent e) {System.out.println("drag");}
    public void mouseClicked(MouseEvent e) {
        System.out.println("click at "+e.getX()+", "+e.getY());
    }

    public void clearScreen() {
        for(int i = 0; i < screenPixelSize; i++) {
            for(int j = 0; j < screenPixelSize; j++) {
                screen[0][i][j] = null;
            }
        }
    }
    public void runGame(float updateXP) {
        while(player.isAlive()) {
            try{
                repaint();
                if(updateXP>0) player.gainXP(updateXP);
                Thread.sleep(UPDATE_SPEED);
            } catch(InterruptedException e) {
                System.out.println(e);
            }
        }
        if(currentGame != 4) {
            player.changeColor(new Color(255-((255-buttonColors[currentGame].getRed())*player.getXP())/255, 255-((255-buttonColors[currentGame].getGreen())*player.getXP())/255, 255-((255-buttonColors[currentGame].getBlue())*player.getXP())/255));
        }
        player.resurrect();
    }

    public void kiteHome() {
        currentGame = 4;
        for(int i = 0; i < buttons.length; i++) buttons[i] = new Kite(screenPixelSize, buttonColors[i], 0, i+"");
        runGame(0F);
        switch(nextGame) {
            case 0:
                kiteSimon();
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                kiteClassic();
        }
    }
    public void kiteClassic() {
        currentGame = 3;
        cloud1 = new Cloud(screenPixelSize);
        cloud2 = new Cloud(screenPixelSize);
        cloud3 = new Cloud(screenPixelSize);
        clearScreen();

        runGame(0.01F);

        kiteHome();
    }
    public void kiteSimon() {
        currentGame = 0;

        runGame(0F);

        kiteHome();
    }

    @Override
    public void paint(Graphics g) {
        if(isStart) {
            super.paint(g);
            isStart = false;
        }
        Graphics2D g2 = (Graphics2D) g;

        for(int i = 0; i < screenPixelSize; i++) {
            for(int j = 0; j < screenPixelSize; j++) {
                screen[1][i][j] = Color.WHITE;
                screenType[i][j] = "background";
            }
        }

        switch(currentGame) {
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                cloud1.move();
                cloud1.show(mg);
                cloud2.move();
                cloud2.show(mg);
                cloud3.move();
                cloud3.show(mg);
                break;
            case 4:
                for(int i = 0; i < buttons.length; i++) {
                    buttons[i].show(mg, screenPixelSize/4+(i%2)*screenPixelSize/2, screenPixelSize/4+((3-i)/2)*screenPixelSize/2);
                }
        }

        if(player != null) player.show(mg, mouseX, mouseY);

        for(int i = 0; i < screenPixelSize; i++) {
            for(int j = 0; j < screenPixelSize; j++) {
                if(screen[0][i][j] != screen[1][i][j]) {
                    g2.setColor(screen[1][i][j]);
                    g2.fillRect(i * windowWidth / screenPixelSize + xOffset, j * windowHeight / screenPixelSize + yOffset, windowWidth / screenPixelSize + 1, windowHeight / screenPixelSize + 1);
                    screen[0][i][j] = screen[1][i][j];
                }
            }
        }
    }
}