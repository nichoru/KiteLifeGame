import javax.swing.*; // lets me make a window
import java.awt.*; // helps with drawing on the window
import java.awt.event.*; // lets me use mouse events

public class Minigame extends JFrame implements MouseListener, MouseMotionListener {
    private final int WINDOW_SIZE; // the length of the window's sides (it's a square)

    // accounts for how the canvas is automatically drawn at slightly different coordinates to the window
    private final int X_OFFSET = 8;
    private final int Y_OFFSET = 31;

    private final int SCREEN_PIXEL_SIZE = 150; // the length of the sides of the pixelated screen
    private final Color[][][] SCREEN; // the pixelated screen array - this holds both the previous screen (so it can check for changes) and the current screen, as well as the x and y of a colour
    private final String[][] SCREEN_TYPE; // tells me the type of each pixel in the screen array - this is used for collision detection in

    private int mouseX; // the x coordinate of the mouse within the pixelated screen array
    private int mouseY; // the y coordinate of the mouse within the pixelated screen array

    private final MyGraphics M_G = new MyGraphics(); // makes a new MyGraphics object, which is used for drawing on the screen and collision detection

    private final Kite PLAYER; // the player's kite that moves with their mouse
    private final Kite[] BUTTONS = new Kite[4]; // the coloured kites that show up in the simon and cookie minigames
    private final String[] BUTTON_NAMES = {"yellow", "magenta", "cyan", "red"}; // the names of the different colour kites for the cookie minigame
    private final int IMMUNE_TIME = 35; // the time in frames that kites are immune for after losing a life (this is less than in the home screen to make it faster)

    private final int GAME; // the type of game this minigame is

    private boolean isStart = true; // lets me only call super.paint(g) once, as calling it more caused flickering

    // all the variables for the cookie minigame
    private int cookieSegment;
    private int cookieClick;
    private float cookieClickPower;
    private final float[] COOKIE_AUTO_POWER = new float[] {0.5F, 0.5F, 0.5F, 0.5F};
    private int cookieMaxClick;
    private int cookieMaxTop;
    private int cookieMaxBottom;
    private String[] describeArray;

    // all the variables for the needle minigame
    private final Needle X_NEEDLE;
    private final Needle Y_NEEDLE;

    private final Cloud[] CLOUDS = new Cloud[3]; // the clouds for the classic minigame

    public Minigame(int game, int windowSize) { // constructor for minigame
        switch(game) { // changes the name of the window based on the game
            case 0:
                setTitle("Kite Clicker");
                break;
            case 1:
                setTitle("Kite Says");
                break;
            case 2:
                setTitle("Kite the Needle");
                break;
            case 3:
                setTitle("Kite Flying 101");
        }

        // makes the window half the size of the main window and positions it nicely on the screen
        if(Main.SCREEN_SIZE.width < Main.SCREEN_SIZE.height) this.WINDOW_SIZE = (Main.SCREEN_SIZE.width-windowSize)/2;
        else this.WINDOW_SIZE = (Main.SCREEN_SIZE.height-windowSize)/2;
        getContentPane().setPreferredSize(new Dimension(this.WINDOW_SIZE, this.WINDOW_SIZE));

        // creating the main window and canvas
        getContentPane().setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        JPanel window = new JPanel();
        window.setPreferredSize(new Dimension(this.WINDOW_SIZE, this.WINDOW_SIZE));
        Canvas canvas = new Canvas();
        window.add(canvas);

        // sets the location of the window
        if(Main.SCREEN_SIZE.width > Main.SCREEN_SIZE.height) setLocation((game%2)*(Main.SCREEN_SIZE.width/2+windowSize/2)+(Main.SCREEN_SIZE.width-windowSize-this.WINDOW_SIZE*2)/4, ((3-game)/2)*(Main.SCREEN_SIZE.height/2)+(Main.SCREEN_SIZE.height-windowSize)/4); // centres the window on the user's screen
        else if(Main.SCREEN_SIZE.height > Main.SCREEN_SIZE.width) setLocation((game%2)*(Main.SCREEN_SIZE.width/2)+(Main.SCREEN_SIZE.width-windowSize)/4, ((3-game)/2)*(Main.SCREEN_SIZE.height/2+windowSize/2)+(Main.SCREEN_SIZE.height-windowSize-this.WINDOW_SIZE*2)/4); // centres the window on the user's screen
        else setLocation((game%2)*(Main.SCREEN_SIZE.width/2)+(Main.SCREEN_SIZE.width-windowSize)/4, ((3-game)/2)*(Main.SCREEN_SIZE.height/2)+(Main.SCREEN_SIZE.height-windowSize)/4); // centres the window on the user's screen

        // lets me see if the mouse moves/clicks
        addMouseListener(this);
        addMouseMotionListener(this);

        // finishes making the window
        this.pack();
        this.toFront();
        this.setVisible(true);

        // sets up a few variables
        this.SCREEN = new Color[2][SCREEN_PIXEL_SIZE][SCREEN_PIXEL_SIZE];
        this.SCREEN_TYPE = new String[SCREEN_PIXEL_SIZE][SCREEN_PIXEL_SIZE];
        if(game != 0) this.PLAYER = Main.player;
        else this.PLAYER = new Kite(SCREEN_PIXEL_SIZE, Color.WHITE, 0, "player", IMMUNE_TIME);
        this.GAME = game;
        this.X_NEEDLE = new Needle(SCREEN_PIXEL_SIZE, PLAYER.getHeight(), true);
        this.Y_NEEDLE = new Needle(SCREEN_PIXEL_SIZE, PLAYER.getWidth(), false);

        // runs the minigame in instuction mode
        this.runGame(0F, Main.startKite);
        Main.isInstructions = false;

        switch(this.GAME) { // starts up the game based on the current game
            case 0:
                this.kiteCookie();
                break;
            case 1:
                this.kiteSimon();
                break;
            case 2:
                this.kiteNeedle();
                break;
            case 3:
                this.kiteClassic();
        }
    }

    public void mouseExited(MouseEvent e) {
        if(this.GAME == 0) Main.isInCookie = false;
    }
    public void mouseEntered(MouseEvent e) {
        if(this.GAME == 0) Main.isInCookie = true;
    }
    public void mouseReleased(MouseEvent e) {System.out.println("release");}
    public void mousePressed(MouseEvent e) {System.out.println("press");}
    public void mouseMoved(MouseEvent e) {
        mouseX = (e.getX() - X_OFFSET)*SCREEN_PIXEL_SIZE/this.WINDOW_SIZE;
        mouseY = (e.getY() - Y_OFFSET)*SCREEN_PIXEL_SIZE/this.WINDOW_SIZE;
    }
    public void mouseDragged(MouseEvent e) {System.out.println("drag");}
    public void mouseClicked(MouseEvent e) {
        System.out.println("click at "+e.getX()+", "+e.getY());
        if(this.GAME == 0) {
            this.PLAYER.gainXP(this.cookieClickPower, this.cookieSegment, 255*(4-this.cookieClick));
            cookieChangeColor();
        }
    }

    public void clearScreen() {
        for(int i = 0; i < SCREEN_PIXEL_SIZE; i++) {
            for(int j = 0; j < SCREEN_PIXEL_SIZE; j++) {
                SCREEN[0][i][j] = null;
            }
        }
        System.out.println("cleared");
    }
    public void runGame(float updateXP, Kite kite) { // runs the game on the minigame screen updating the screen
        this.clearScreen(); // clears the screen so it can be redrawn

        while(kite.isAlive()) { // while the kite in question is alive, keep updating the screen
            try{
                if(Main.isCookie && this.GAME != 0) Main.cookieMinigame.repaint();
                this.repaint();
                if(updateXP>0) Main.player.gainXP(updateXP, this.GAME, 255);
                Thread.sleep(Main.UPDATE_SPEED);
            } catch(InterruptedException e) {
                System.out.println(e);
            }
        }

        Main.player.changeColor(new Color(255-((255-Main.BUTTON_COLORS[GAME].getRed())*Main.player.getXP(this.GAME))/255, 255-((255-Main.BUTTON_COLORS[GAME].getGreen())*Main.player.getXP(this.GAME))/255, 255-((255-Main.BUTTON_COLORS[GAME].getBlue())*Main.player.getXP(this.GAME))/255), this.GAME); // changes the player's colour based on how well they did
    }
    public void showButtons() {
        for(int i = 0; i < BUTTONS.length; i++) {
            BUTTONS[i].show(M_G, SCREEN_PIXEL_SIZE/4+(i%2)*SCREEN_PIXEL_SIZE/2, SCREEN_PIXEL_SIZE/4+((3-i)/2)*SCREEN_PIXEL_SIZE/2);
        }
    }

    public void kiteCookie() {
        this.cookieClick = 3;
        this.cookieClickPower = 5;
        this.cookieMaxClick = 3;
        this.cookieMaxTop = 4;
        this.cookieMaxBottom = 4;
        this.cookieSegment = BUTTONS.length-1;
        for(int i = 0; i < BUTTONS.length; i++) BUTTONS[i] = new Kite(SCREEN_PIXEL_SIZE, Main.BUTTON_COLORS[i], 0, i+"", IMMUNE_TIME*2);
    }
    public void kiteNeedle() {
        runGame(0.03F, this.PLAYER);
    }
    public void kiteClassic() {
        for(int i = 0; i < 3; i++) CLOUDS[i] = new Cloud(SCREEN_PIXEL_SIZE);

        runGame(0.03F, this.PLAYER);
    }
    public void kiteSimon() {
        for(int i = 0; i < BUTTONS.length; i++) BUTTONS[i] = new Kite(SCREEN_PIXEL_SIZE, Main.BUTTON_COLORS[i], 0, i+"", IMMUNE_TIME*2);
        clearScreen();
        repaint();
    }

    public Kite getPlayer() {
        return this.PLAYER;
    }

    public Kite getButton(int i) {
        return this.BUTTONS[i];
    }

    public String getScreenType(int x, int y) {
        return this.SCREEN_TYPE[x][y];
    }

    public void setScreenType(int x, int y, String type) {
        this.SCREEN_TYPE[x][y] = type;
    }

    public void cookiePay(int price, int segment, int level) {
        while(price > 0) {
            if(price > this.PLAYER.getXP(this.cookieSegment)-(3-this.cookieClick)*255) {
                price -= this.PLAYER.getXP(this.cookieSegment)-(3-this.cookieClick)*255;
                this.PLAYER.loseXP(this.PLAYER.getXP(this.cookieSegment)-(3-this.cookieClick)*255, this.cookieSegment);
                cookieChangeColor();

                if(this.cookieSegment < 3) this.cookieSegment++;
                else {
                    this.cookieSegment = 0;
                    this.cookieClick++;
                }
            }
            else {
                this.PLAYER.loseXP(price, this.cookieSegment);
                cookieChangeColor();
                price = 0;
            }
        }

        switch(segment) {
            case 0:
                this.COOKIE_AUTO_POWER[level]*=5F/2F;
                break;
            case 1:
                this.cookieMaxClick = level-1;
                this.cookieClickPower*=3F/2F;
                break;
            case 2:
                this.cookieMaxBottom = level;
                break;
            case 3:
                this.cookieMaxTop = level;
        }
    }

    private void cookieChangeColor() {
        switch(this.cookieClick) {
            case 0:
                this.PLAYER.changeColor(new Color(255, this.PLAYER.getXP(this.cookieSegment)-255*3, 255-(this.PLAYER.getXP(this.cookieSegment)-255*3)), this.cookieSegment);
                break;
            case 1:
                this.PLAYER.changeColor(new Color(this.PLAYER.getXP(this.cookieSegment)-255*2, 255-(this.PLAYER.getXP(this.cookieSegment)-255*2), 255), this.cookieSegment);
                break;
            case 2:
                this.PLAYER.changeColor(new Color(255-(this.PLAYER.getXP(this.cookieSegment)-255), this.PLAYER.getXP(this.cookieSegment)-255, this.PLAYER.getXP(this.cookieSegment)-255), this.cookieSegment);
                break;
            case 3:
                this.PLAYER.changeColor(new Color(255, 255-this.PLAYER.getXP(this.cookieSegment), 255-this.PLAYER.getXP(this.cookieSegment)), this.cookieSegment);
        }
    }

    @Override
    public void paint(Graphics g) {
        Main.currentScreen = this.SCREEN;
        Main.currentScreenType = this.SCREEN_TYPE;

        if(this.isStart) {
            super.paint(g);
            System.out.println("start");
        }
        Graphics2D g2 = (Graphics2D) g;

        if(Main.isInstructions && Main.currentGame == this.GAME) {
            clearScreen();
            for(int i = 0; i < SCREEN_PIXEL_SIZE; i++) {
                for(int j = 0; j < SCREEN_PIXEL_SIZE; j++) {
                    this.SCREEN[1][i][j] = Color.BLACK;
                    this.SCREEN_TYPE[i][j] = "background";
                }
            }
            Main.startKite.show(M_G, SCREEN_PIXEL_SIZE/2, SCREEN_PIXEL_SIZE/2);
        } else {

            for (int i = 0; i < SCREEN_PIXEL_SIZE; i++) {
                for (int j = 0; j < SCREEN_PIXEL_SIZE; j++) {
                    this.SCREEN[1][i][j] = Color.WHITE;
                    this.SCREEN_TYPE[i][j] = "background";
                }
            }

            switch (this.GAME) {
                case 0:
                    this.PLAYER.makeImmune(1);

                    if (this.PLAYER.getXP(this.cookieSegment) - 255 * (3 - this.cookieClick) > 0 && this.PLAYER.getXP(this.cookieSegment) % 255 == 0) {
                        if (this.cookieSegment > 0) {
                            this.cookieSegment--;
                        } else if (this.cookieClick > this.cookieMaxClick) {
                            this.cookieSegment = 3;
                            this.cookieClick--;
                        }
                    }

                    if ((this.cookieClick >= this.cookieMaxTop && this.cookieSegment > 1) || (this.cookieClick >= this.cookieMaxBottom && this.cookieSegment < 2)) {
                        this.PLAYER.gainXP(this.COOKIE_AUTO_POWER[this.cookieSegment], this.cookieSegment, 255 * (4 - this.cookieClick));
                        cookieChangeColor();
                    }

                    if (this.cookieClick == 0 && this.cookieSegment == 0 && this.PLAYER.getXP(this.cookieSegment) == 255 * BUTTONS.length) {
                        Main.player.gainXP(255, this.GAME, 255);
                        this.PLAYER.kill();
                    }

                    showButtons();
                    break;
                case 1:
                    if (!Main.isWait) {
                        if (Main.simonCounter < Main.simonOrder.length) {
                            if (Main.simonOrder[Main.simonCounter] == 4) {
                                Main.simonTimer = 1 - Main.IMMUNE_TIME;
                                Main.player.gainXP(Main.simonCounter * 2, this.GAME, 255);
                                if (Main.simonOrder[Main.simonOrder.length - 1] != 4) Main.player.kill();
                                Main.simonOrder[Main.simonCounter] = (int) Math.floor(Math.random() * 4);
                                while (!Main.buttons[Main.simonOrder[Main.simonCounter]].isAlive())
                                    Main.simonOrder[Main.simonCounter] = (int) Math.floor(Math.random() * 4);
                                for (int i = 0; i < this.BUTTONS.length; i++) {
                                    this.BUTTONS[i].resurrect();
                                    Main.buttons[i].resurrect();
                                }
                                Main.isWait = true;
                            }
                        } else {
                            Main.isWait = true;
                            Main.player.gainXP(Main.simonCounter * 2, this.GAME, 255);
                            Main.player.kill();
                        }
                    }
                    showButtons();
                    break;
                case 2:
                    X_NEEDLE.move(PLAYER.getHeight());
                    X_NEEDLE.show(M_G);
                    Y_NEEDLE.move(PLAYER.getWidth());
                    Y_NEEDLE.show(M_G);
                    break;
                case 3:
                    for(Cloud cloud : CLOUDS) {
                        cloud.move();
                        cloud.show(M_G);
                    }
                    break;
                case 4:
                    showButtons();
            }
        }

        this.PLAYER.show(M_G, mouseX, mouseY);

        for(int i = 0; i < SCREEN_PIXEL_SIZE; i++) {
            for(int j = 0; j < SCREEN_PIXEL_SIZE; j++) {
                if(this.SCREEN[0][i][j] != this.SCREEN[1][i][j] || isStart) {
                    g2.setColor(this.SCREEN[1][i][j]);
                    g2.fillRect(i * this.WINDOW_SIZE / SCREEN_PIXEL_SIZE + X_OFFSET, j * this.WINDOW_SIZE / SCREEN_PIXEL_SIZE + Y_OFFSET, this.WINDOW_SIZE / SCREEN_PIXEL_SIZE + 1, this.WINDOW_SIZE / SCREEN_PIXEL_SIZE + 1);
                    this.SCREEN[0][i][j] = this.SCREEN[1][i][j];
                }
            }
        }

        if(this.GAME == 0 && !(Main.isInstructions && Main.currentGame == 0)) {
            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Arial", Font.PLAIN, this.WINDOW_SIZE/20));
            for(int i = 0; i < this.BUTTONS.length; i++) {
                if(this.BUTTONS[i].isAlive()) {
                    g2.drawString("Price: " + (5 - this.BUTTONS[i].getLives()) + " " + this.BUTTON_NAMES[i], this.WINDOW_SIZE / 10 + (i % 2) * this.WINDOW_SIZE / 2 + X_OFFSET, 2 * this.WINDOW_SIZE / 3 - (i / 2) * this.WINDOW_SIZE / 2 + Y_OFFSET);
                    switch (this.BUTTONS[i].getLives()) {
                        case 0:
                            this.describeArray = new String[]{""};
                        case 1:
                            this.describeArray = new String[]{"", "segment to change", "colour faster"};
                            switch (i) {
                                case 0:
                                    this.describeArray[0] = "Buy for the lower left";
                                    break;
                                case 1:
                                    this.describeArray[0] = "Buy for the lower right";
                                    break;
                                case 2:
                                    this.describeArray[0] = "Buy for the upper left";
                                    break;
                                case 3:
                                    this.describeArray[0] = "Buy for the upper right";
                            }
                            break;
                        case 2:
                            if (i > 0)
                                this.describeArray = new String[]{"Buy for clicks", "to be able to", "turn the kite " + BUTTON_NAMES[i - 1]};
                            else this.describeArray = new String[]{"Buy for clicks", "to be better"};
                            break;
                        case 3:
                            this.describeArray = new String[]{"Buy for the lower two", "segments to auto", "turn " + BUTTON_NAMES[i]};
                            break;
                        case 4:
                            this.describeArray = new String[]{"Buy for the upper two", "segments to auto", "turn " + BUTTON_NAMES[i]};
                    }
                    for (int j = 0; j < this.describeArray.length; j++) {
                        g2.drawString(this.describeArray[j], (i % 2) * this.WINDOW_SIZE / 2 + X_OFFSET, 5 * this.WINDOW_SIZE / 6 + (j + 1) * this.WINDOW_SIZE / 20 - (i / 2) * this.WINDOW_SIZE / 2 + Y_OFFSET);
                    }
                }
            }
        }
        if(this.isStart) this.isStart = false;
    }
}
