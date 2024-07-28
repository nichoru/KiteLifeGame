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
    public static Kite startKite;
    public static int currentGame;
    public static Minigame currentMinigame;
    public static Minigame cookieMinigame;
    public static boolean isCookie;
    public static boolean isInCookie;
    public static int nextGame;
    public static boolean isInstructions;
    public static Kite[] buttons = new Kite[4];
    private Color[] buttonColors = {Color.YELLOW, Color.MAGENTA, Color.CYAN, Color.RED};
    public static int immuneTime = 50;
    public static boolean isWait = true;
    public static int simonTimer;
    public static int[] simonOrder;
    public static int simonCounter;
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

        isCookie = false;
        isInCookie = false;
        isInstructions = false;

        player = new Kite(screenPixelSize, Color.WHITE, 0, "player", immuneTime);
        startKite = new Kite(screenPixelSize, Color.WHITE, 0, "start", immuneTime);
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
                if(isCookie) {
                    cookieMinigame.repaint();
                    if(!cookieMinigame.getPlayer().isAlive()) {
                        player.changeColor(buttonColors[0], 0);
                        System.out.println(player.getXP(0));
                        cookieMinigame.dispose();
                        isCookie = false;
                        isInCookie = false;
                    }
                }
                if(currentGame == 1 && !isWait) currentMinigame.repaint();
                else repaint();
                if(updateXP>0) player.gainXP(updateXP, currentGame, 255);
                Thread.sleep(UPDATE_SPEED);
            } catch(InterruptedException e) {
                System.out.println(e);
            }
        }
        if(currentGame != 4) {
            player.changeColor(new Color(255-((255-buttonColors[currentGame].getRed())*player.getXP(currentGame))/255, 255-((255-buttonColors[currentGame].getGreen())*player.getXP(currentGame))/255, 255-((255-buttonColors[currentGame].getBlue())*player.getXP(currentGame))/255), currentGame);
            System.out.println(currentGame);
            System.out.println(player.getXP(currentGame));
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
        isInstructions = true;
        clearScreen();
        repaint();
        if(currentGame!=0) currentMinigame = new Minigame(currentGame, windowWidth, windowHeight);
        else if(!isCookie) {
            isCookie = true;
            cookieMinigame = new Minigame(currentGame, windowWidth, windowHeight);
        }
        if(currentGame==1) kiteSimon();
        player.resurrect();
        if(currentGame != 0) currentMinigame.dispose();
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

        if(isInstructions) {
            for (int i = 0; i < screenPixelSize; i++) {
                for (int j = 0; j < screenPixelSize; j++) {
                    screen[1][i][j] = Color.BLACK;
                    screenType[i][j] = "background";
                }
            }
        } else {
            for (int i = 0; i < screenPixelSize; i++) {
                for (int j = 0; j < screenPixelSize; j++) {
                    screen[1][i][j] = Color.WHITE;
                    screenType[i][j] = "background";
                }
            }

            switch (currentGame) {
                case 0:
                    break;
                case 1:
                    if (simonTimer / immuneTime == simonOrder.length) {
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
                case 2:
                    break;
                case 3:
                    break;
                case 4:
                    showButtons();
            }

            if (currentGame != 1) player.show(mg, mouseX, mouseY);
        }

        for(int i = 0; i < screenPixelSize; i++) {
            for(int j = 0; j < screenPixelSize; j++) {
                if(screen[0][i][j] != screen[1][i][j] || isStart) {
                    g2.setColor(screen[1][i][j]);
                    g2.fillRect(i * windowWidth / screenPixelSize + xOffset, j * windowHeight / screenPixelSize + yOffset, windowWidth / screenPixelSize + 1, windowHeight / screenPixelSize + 1);
                    screen[0][i][j] = screen[1][i][j];
                }
            }
        }

        if(isInstructions) {
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.PLAIN, 20));
            System.out.println(windowWidth);
            switch(currentGame) {
                case 0:
                    g2.drawString("Welcome to Kite Clicker!", windowWidth/2-100, windowHeight/2-50);
                    g2.drawString("Click to gain more colour", windowWidth/2-150, windowHeight/2);
                    g2.drawString("When you have enough full segments of a colour", windowWidth/2-150, windowHeight/2+50);
                    g2.drawString("you can buy upgrades by hovering over that colour kite", windowWidth/2-150, windowHeight/2+100);
                    g2.drawString("The aim is to have a fully yellow kite", windowWidth/2-50, windowHeight/2+150);
                    break;
                case 1:
                    g2.drawString("Welcome to Kite Says!", windowWidth/2-100, windowHeight/2-50);
                    g2.drawString("Watch the buttons light up on the main screen", windowWidth/2-150, windowHeight/2);
                    g2.drawString("Afterwards, hover over them on the small screen", windowWidth/2-150, windowHeight/2+50);
                    g2.drawString("in the same order", windowWidth/2-50, windowHeight/2+100);
                    break;
                case 2:
                    g2.drawString("Welcome to Kite the Needle!", windowWidth/2-100, windowHeight/2-50);
                    g2.drawString("Thread the needle by avoiding the obstacles", windowWidth/2-150, windowHeight/2);
                    g2.drawString("for as long as you can", windowWidth/2-150, windowHeight/2+50);
                    g2.drawString("The gaps will get smaller and smaller", windowWidth/2-150, windowHeight/2+100);
                    break;
                case 3:
                    g2.drawString("Welcome to Kite Flying 101!", windowWidth/2-100, windowHeight/2-50);
                    g2.drawString("Dodge the clouds and try to stay alive", windowWidth/2-150, windowHeight/2);
                    g2.drawString("The clouds will get faster and faster", windowWidth/2-150, windowHeight/2+50);
                    break;
                case 4:
                    g2.drawString("Welcome to Kite Life!", windowWidth/2-100, windowHeight/2-50);
                    g2.drawString("Click on the kite to start the game", windowWidth/2-150, windowHeight/2);
                    g2.drawString("Use the mouse to move the kite", windowWidth/2-150, windowHeight/2+50);
                    g2.drawString("Avoid the obstacles and stay alive", windowWidth/2-150, windowHeight/2+100);
                    g2.drawString("Good luck!", windowWidth/2-50, windowHeight/2+150);
            }
        }
        if(isStart) isStart = false;
    }
}