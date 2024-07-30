import javax.swing.*; // lets me make a window
import java.awt.*; // helps with drawing on the window
import java.awt.event.*; // lets me use mouse events

public class Main extends JFrame implements MouseListener, MouseMotionListener {

    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); // gets the user's actual screen size
    private int windowSize; // the length of the window's sides (it's a square)

    private final int X_OFFSET = 8; // accounts for how the canvas is automatically drawn at slightly different coordinates to the window
    private final int Y_OFFSET = 31;

    private static final int SCREEN_PIXEL_SIZE = 150;
    public static Color[][][] screen = new Color[2][SCREEN_PIXEL_SIZE][SCREEN_PIXEL_SIZE];
    public static Color[][][] currentScreen = screen;
    public static String[][] screenType = new String[SCREEN_PIXEL_SIZE][SCREEN_PIXEL_SIZE];
    public static String[][] currentScreenType = screenType;

    private int mouseX; // the x coordinate of the mouse within the pixelated screen array
    private int mouseY; // the y coordinate of the mouse within the pixelated screen array

    private final MyGraphics M_G = new MyGraphics();

    public static Kite player;
    public static Kite startKite;
    public static Kite[] buttons = new Kite[4];
    private final Color[] BUTTON_COLORS = {Color.YELLOW, Color.MAGENTA, Color.CYAN, Color.RED};
    public static int immuneTime = 50;

    public static int currentGame;
    public static int nextGame;
    public static Minigame currentMinigame;
    private boolean isWin;
    public static boolean isInstructions;
    private String[] instructionsArray;
    private boolean isStart = true;
    private final int UPDATE_SPEED = 10; // milliseconds between screen updates

    public static Minigame cookieMinigame;
    public static boolean isCookie;
    public static boolean isInCookie;

    public static boolean isWait = true;
    public static int simonTimer;
    public static int[] simonOrder;
    public static int simonCounter;

    public static void main(String[] args) {
        new Main();
    } // makes a new Main object

    public Main() {
        String title = "Kite Life";
        setTitle(title);
        if(screenSize.width < screenSize.height) windowSize = screenSize.width/2; // makes the window a square that's based on the smaller out of the user's screen height and width
        else windowSize = screenSize.height/2;
        getContentPane().setPreferredSize(new Dimension(windowSize, windowSize));
        getContentPane().setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel window = new JPanel();
        window.setPreferredSize(new Dimension(windowSize, windowSize));
        Canvas canvas = new Canvas();
        window.add(canvas);

        setLocation((screenSize.width-windowSize)/2, (screenSize.height-windowSize)/2); // centres the window on the user's screen

        addMouseListener(this);
        addMouseMotionListener(this);

        this.pack();
        this.toFront();
        this.setVisible(true);

        isCookie = false;
        isInCookie = false;
        isInstructions = false;

        player = new Kite(SCREEN_PIXEL_SIZE, Color.WHITE, 0, "player", immuneTime);
        startKite = new Kite(SCREEN_PIXEL_SIZE, Color.WHITE, 0, "start", immuneTime);
        for(int i = 0; i < buttons.length; i++) buttons[i] = new Kite(SCREEN_PIXEL_SIZE, BUTTON_COLORS[i], 0, i+"", immuneTime);
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
        mouseX = (e.getX() - X_OFFSET)*SCREEN_PIXEL_SIZE/windowSize;
        mouseY = (e.getY() - Y_OFFSET)*SCREEN_PIXEL_SIZE/windowSize;
    }
    public void mouseDragged(MouseEvent e) {System.out.println("drag");}
    public void mouseClicked(MouseEvent e) {
        System.out.println("click at "+e.getX()+", "+e.getY());
    }

    public void clearScreen() {
        for(int i = 0; i < SCREEN_PIXEL_SIZE; i++) {
            for(int j = 0; j < SCREEN_PIXEL_SIZE; j++) {
                screen[0][i][j] = null;
            }
        }
    }
    public void runGame(float updateXP) {
        clearScreen();
        while(player.isAlive()) {
            try{
                if(isCookie) {
                    cookieMinigame.repaint();
                    if(!cookieMinigame.getPlayer().isAlive()) {
                        player.changeColor(BUTTON_COLORS[0], 0);
                        System.out.println(player.getXP(0));
                        cookieMinigame.dispose();
                        isCookie = false;
                        isInCookie = false;
                        isWin = true;
                        for(int i = 0; i < buttons.length; i++) if(player.getXP(i) < 255) isWin = false;
                        if(isWin) kiteHome();
                    }
                }
                if(currentGame == 1 && !isWait) currentMinigame.repaint();
                else repaint();
                if(currentGame == 4) {
                    for(int i = 0; i < buttons.length; i++) {
                        if(!buttons[i].isAlive() && player.getXP(i) != 255) player.kill();
                    }
                }
                if(updateXP>0) player.gainXP(updateXP, currentGame, 255);
                Thread.sleep(UPDATE_SPEED);
            } catch(InterruptedException e) {
                System.out.println(e);
            }
        }
        if(currentGame != 4) {
            player.changeColor(new Color(255-((255-BUTTON_COLORS[currentGame].getRed())*player.getXP(currentGame))/255, 255-((255-BUTTON_COLORS[currentGame].getGreen())*player.getXP(currentGame))/255, 255-((255-BUTTON_COLORS[currentGame].getBlue())*player.getXP(currentGame))/255), currentGame);
            System.out.println(currentGame);
            System.out.println(player.getXP(currentGame));
        }
        player.resurrect();
        for (Kite button : buttons) button.resurrect();
    }
    public void showButtons() {
        if(player.getXP(0) == 255) {
            buttons[0].kill();
        }
        for(int i = 0; i < buttons.length; i++) {
            buttons[i].show(M_G, SCREEN_PIXEL_SIZE/4+(i%2)*SCREEN_PIXEL_SIZE/2, SCREEN_PIXEL_SIZE/4+((3-i)/2)*SCREEN_PIXEL_SIZE/2);
        }
    }

    public void kiteHome() {
        currentGame = 4;
        isWin = true;
        for(int i = 0; i < buttons.length; i++) {
            buttons[i].setLives(4-(player.getXP(i)/63));
            if(!buttons[i].isAlive() && player.getXP(i) != 255) buttons[i].setLives(1);
            if(buttons[i].isAlive()) isWin = false;
        }
        isInstructions = true;
        runGame(0F);
        currentGame = nextGame;
        clearScreen();
        repaint();
        startKite.resurrect();
        if(currentGame!=0) currentMinigame = new Minigame(currentGame, windowSize, windowSize);
        else if(!isCookie) {
            isCookie = true;
            cookieMinigame = new Minigame(currentGame, windowSize, windowSize);
        }
        if(currentGame==1) kiteSimon();
        player.resurrect();
        if(currentGame != 0) currentMinigame.dispose();
        if(!isWin) kiteHome();
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
            clearScreen();
            for (int i = 0; i < SCREEN_PIXEL_SIZE; i++) {
                for (int j = 0; j < SCREEN_PIXEL_SIZE; j++) {
                    screen[1][i][j] = Color.BLACK;
                    screenType[i][j] = "background";
                }
            }
            if(currentGame == 4) {
                showButtons();
                player.show(M_G, mouseX, mouseY);
            }

        } else {
            for (int i = 0; i < SCREEN_PIXEL_SIZE; i++) {
                for (int j = 0; j < SCREEN_PIXEL_SIZE; j++) {
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
            }

            if (currentGame != 1) player.show(M_G, mouseX, mouseY);
        }

        for(int i = 0; i < SCREEN_PIXEL_SIZE; i++) {
            for(int j = 0; j < SCREEN_PIXEL_SIZE; j++) {
                if(screen[0][i][j] != screen[1][i][j] || isStart) {
                    g2.setColor(screen[1][i][j]);
                    g2.fillRect(i * windowSize / SCREEN_PIXEL_SIZE + X_OFFSET, j * windowSize / SCREEN_PIXEL_SIZE + Y_OFFSET, windowSize / SCREEN_PIXEL_SIZE + 1, windowSize / SCREEN_PIXEL_SIZE + 1);
                    screen[0][i][j] = screen[1][i][j];
                }
            }
        }

        if(isInstructions) {
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.PLAIN, windowSize/20));
            switch(currentGame) {
                case 0:
                    instructionsArray = new String[] {"Welcome to Kite Clicker!","Click to gain more colour","When you have enough full segments of", "a colour, you can buy upgrades by", "hovering over that colour kite","The aim is to have a fully yellow kite","YOU CAN PLAY OTHER GAMES STILL"};
                    break;
                case 1:
                    instructionsArray = new String[] {"Welcome to Kite Says!", "Watch the buttons light up", "on the main screen", "Afterwards, hover over them on the", "small screen in the same order"};
                    break;
                case 2:
                    instructionsArray = new String[] {"Welcome to Kite the Needle!", "Thread the needle by avoiding", "the obstacles for as long as you can", "The gaps will get smaller and smaller"};
                    break;
                case 3:
                    instructionsArray = new String[] {"Welcome to Kite Flying 101!", "Dodge the clouds and try to stay alive", "The clouds will get faster and faster"};
                    break;
                case 4:
                    if(isWin) instructionsArray = new String[] {"Congratulations!", "You have won the game!"};
                    else instructionsArray = new String[] {"Welcome to Kite Life!", "Hover over the kites to start each", "minigame (they will disappear ", "as you gain xp)", "", "The aim is to have a fully coloured kite"};
                    for(int i = 0; i < instructionsArray.length; i++) {
                        g2.drawString(instructionsArray[i], windowSize/20+X_OFFSET, (2*i+7)*windowSize/20+Y_OFFSET);
                    }
                    if(isCookie) g2.drawString("Tip: you can play multiple games at once!", windowSize/20+X_OFFSET, (instructionsArray.length*2+7)*windowSize/20+Y_OFFSET);
                    else if(player.getXP(0) == 0) g2.drawString("Tip: start with yellow!", windowSize/20+X_OFFSET, (instructionsArray.length*2+7)*windowSize/20+Y_OFFSET);
                    instructionsArray = new String[] {""};
                }

            for(int i = 0; i < instructionsArray.length; i++) {
                g2.drawString(instructionsArray[i], windowSize/10+X_OFFSET, (i+1)*windowSize/10+Y_OFFSET);
            }
            if(currentGame != 4 && currentGame != 0) {
                g2.drawString("Kill the other kite on the small screen", windowSize / 10 + X_OFFSET, (instructionsArray.length + 2) * windowSize / 10 + Y_OFFSET);
                g2.drawString("by hovering over it to start", windowSize / 10 + X_OFFSET, (instructionsArray.length + 3) * windowSize / 10 + Y_OFFSET);
            } else if(currentGame == 0) {
                g2.drawString("Kill the other kite on the small screen", windowSize / 10 + X_OFFSET, (instructionsArray.length + 1) * windowSize / 10 + Y_OFFSET);
                g2.drawString("by hovering over it to start", windowSize / 10 + X_OFFSET, (instructionsArray.length + 2) * windowSize / 10 + Y_OFFSET);
            }
        }
        if(isStart) isStart = false;
    }
}