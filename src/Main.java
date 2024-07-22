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
    public static Kite player;
    public static int currentGame;
    public static Minigame currentMinigame;
    public static int nextGame;
    public static Kite[] buttons = new Kite[4];
    private Color[] buttonColors = {Color.YELLOW, Color.MAGENTA, Color.CYAN, Color.RED};
    public static int immuneTime = 10;
    public static boolean isWait = true;
    public static int simonTimer;
    public static int[] simonOrder;
    public static int simonCounter;
    private Needle xNeedle;
    private Needle yNeedle;
    private Cloud cloud1;
    private Cloud cloud2;
    private Cloud cloud3;
    public static Color[][][] screen = new Color[2][screenPixelSize][screenPixelSize];
    public static Color[][][] currentScreen = screen;
    public static String[][] screenType = new String[screenPixelSize][screenPixelSize];
    public static String[][] currentScreenType = screenType;
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

        player = new Kite(screenPixelSize, Color.WHITE, 0, "player", immuneTime);
        for(int i = 0; i < buttons.length; i++) buttons[i] = new Kite(screenPixelSize, buttonColors[i], 0, i+"", immuneTime);
        kiteHome();
    }

    public void actionPerformed(ActionEvent e) {

    }

    public void mouseExited(MouseEvent e) {System.out.println("exit");}
    public void mouseEntered(MouseEvent e) {
        System.out.println("enter");

    }
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
        System.out.println("cleared");
    }
    public void runGame(float updateXP) {
        clearScreen();
        while(player.isAlive()) {
            try{
                if(currentGame == 0 && !isWait) currentMinigame.repaint();
                else repaint();
                if(updateXP>0) player.gainXP(updateXP);
                Thread.sleep(UPDATE_SPEED);
            } catch(InterruptedException e) {
                System.out.println(e);
            }
        }
        if(currentGame != 4) {
            player.changeColor(new Color(255-((255-buttonColors[currentGame].getRed())*player.getXP())/255, 255-((255-buttonColors[currentGame].getGreen())*player.getXP())/255, 255-((255-buttonColors[currentGame].getBlue())*player.getXP())/255));
            System.out.println(player.getXP());
        }
        player.resurrect();
        for(int i = 0; i < buttons.length; i++) buttons[i].resurrect();
    }
    public void showButtons() {
        for(int i = 0; i < buttons.length; i++) {
            if(buttons[i].isAlive()) buttons[i].show(mg, screenPixelSize/4+(i%2)*screenPixelSize/2, screenPixelSize/4+((3-i)/2)*screenPixelSize/2);
        }
    }

    public void kiteHome() {
        currentGame = 4;
        runGame(0F);
        currentGame = nextGame;
        currentMinigame = new Minigame(currentGame, windowWidth, windowHeight);
        if(currentGame==0) kiteSimon();
        player.resurrect();
        currentMinigame.dispose();
        kiteHome();
    }
    public void kiteNeedle() {
        currentGame = 2;
        xNeedle = new Needle(screenPixelSize, player.getHeight(), true);
        yNeedle = new Needle(screenPixelSize, player.getWidth(), false);

        runGame(0.01F);

        kiteHome();
    }
    public void kiteClassic() {
        currentGame = 3;
        cloud1 = new Cloud(screenPixelSize);
        cloud2 = new Cloud(screenPixelSize);
        cloud3 = new Cloud(screenPixelSize);

        runGame(0.01F);

        kiteHome();
    }
    public void kiteSimon() {
        simonTimer = 1-immuneTime;
        simonOrder = new int[16];
        for(int i = 0; i < simonOrder.length; i++) {
            if(i<3) simonOrder[i] = (int) Math.floor(Math.random()*4);
            else simonOrder[i] = 4;
            System.out.println(simonOrder[i]);
        }
        isWait = true;

        runGame(0F);

        simonCounter = simonOrder.length;
    }

    @Override
    public void paint(Graphics g) {
        currentScreen = screen;
        currentScreenType = screenType;
        if(isStart) {
            super.paint(g);
            System.out.println("start");
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
                if(simonTimer/immuneTime == simonOrder.length) {
                    System.out.println(simonTimer);
                    simonCounter = 0;

                    isWait = false;
                } else {
                    if (simonOrder[simonTimer / immuneTime] != 4) {
                        simonCounter = simonOrder.length;
                        if (simonTimer % immuneTime == 0) buttons[simonOrder[simonTimer / immuneTime]].loseLife();
                        player.makeImmune(1);
                        simonTimer++;
                    } else {
                        simonCounter = 0;

                        isWait = false;
                        //if(simonCounter == simonOrder.length) simonCounter = 0;
                    }
                }
                showButtons();
                //if(simonTimer/immuneTime==simonOrder.length) isWait = false;
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                showButtons();
        }

        if(player != null) player.show(mg, mouseX, mouseY);

        for(int i = 0; i < screenPixelSize; i++) {
            for(int j = 0; j < screenPixelSize; j++) {
                if(screen[0][i][j] != screen[1][i][j] || isStart) {
                    g2.setColor(screen[1][i][j]);
                    g2.fillRect(i * windowWidth / screenPixelSize + xOffset, j * windowHeight / screenPixelSize + yOffset, windowWidth / screenPixelSize + 1, windowHeight / screenPixelSize + 1);
                    screen[0][i][j] = screen[1][i][j];
                }
            }
        }
        if(isStart) isStart = false;
    }
}