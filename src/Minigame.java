import javax.swing.*; // lets me make a window
import java.awt.*; // helps with drawing on the window
import java.awt.event.*; // lets me use mouse events

public class Minigame extends JFrame implements MouseListener, MouseMotionListener {
    private final int WINDOW_SIZE; // the length of the window's sides (it's a square)

    // accounts for how the canvas is automatically drawn at slightly different coordinates to the window
    private final int X_OFFSET = 8;
    private final int Y_OFFSET = 31;

    private final int SCREEN_PIXEL_SIZE = 150;
    private Color[][][] screen;
    private String[][] screenType;

    private int mouseX;
    private int mouseY;
    private final MyGraphics M_G = new MyGraphics();
    private Kite player;
    private Kite startKite;
    private int game;
    private Kite[] buttons = new Kite[4];
    private final Color[] BUTTON_COLORS = {Color.YELLOW, Color.MAGENTA, Color.CYAN, Color.RED};
    private final String[] BUTTON_NAMES = {"yellow", "magenta", "cyan", "red"};
    private final int IMMUNE_TIME = 35;
    private Needle xNeedle;
    private Needle yNeedle;
    private Cloud cloud1;
    private Cloud cloud2;
    private Cloud cloud3;
    private boolean isStart = true;
    private int cookieSegment;
    private int cookieClick;
    private float cookieClickPower;
    private float[] cookieAutoPower;
    private int cookieMaxClick;
    private int cookieMaxTop;
    private int cookieMaxBottom;
    private String[] describeArray;

    public Minigame(int game, int windowSize) {
        String title = "Kite Life";
        setTitle(title);
        if(Main.SCREEN_SIZE.width < Main.SCREEN_SIZE.height) this.WINDOW_SIZE = (Main.SCREEN_SIZE.width-windowSize)/2; // makes the window a square that's based on the smaller out of the user's screen height and width
        else this.WINDOW_SIZE = (Main.SCREEN_SIZE.height-windowSize)/2;
        getContentPane().setPreferredSize(new Dimension(this.WINDOW_SIZE, this.WINDOW_SIZE));
        getContentPane().setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel window = new JPanel();
        window.setPreferredSize(new Dimension(this.WINDOW_SIZE, this.WINDOW_SIZE));
        Canvas canvas = new Canvas();
        window.add(canvas);

        if(Main.SCREEN_SIZE.width > Main.SCREEN_SIZE.height) setLocation((game%2)*(Main.SCREEN_SIZE.width/2+windowSize/2)+(Main.SCREEN_SIZE.width-windowSize-this.WINDOW_SIZE*2)/4, ((3-game)/2)*(Main.SCREEN_SIZE.height/2)+(Main.SCREEN_SIZE.height-windowSize)/4); // centres the window on the user's screen
        else if(Main.SCREEN_SIZE.height > Main.SCREEN_SIZE.width) setLocation((game%2)*(Main.SCREEN_SIZE.width/2)+(Main.SCREEN_SIZE.width-windowSize)/4, ((3-game)/2)*(Main.SCREEN_SIZE.height/2+windowSize/2)+(Main.SCREEN_SIZE.height-windowSize-this.WINDOW_SIZE*2)/4); // centres the window on the user's screen
        else setLocation((game%2)*(Main.SCREEN_SIZE.width/2)+(Main.SCREEN_SIZE.width-windowSize)/4, ((3-game)/2)*(Main.SCREEN_SIZE.height/2)+(Main.SCREEN_SIZE.height-windowSize)/4); // centres the window on the user's screen

        addMouseListener(this);
        addMouseMotionListener(this);

        this.pack();
        this.toFront();
        this.setVisible(true);

        if(game != 0) this.player = Main.player;
        else this.player = new Kite(SCREEN_PIXEL_SIZE, Color.WHITE, 0, "player", IMMUNE_TIME);
        this.game = game;

        this.screen = new Color[2][SCREEN_PIXEL_SIZE][SCREEN_PIXEL_SIZE];
        this.screenType = new String[SCREEN_PIXEL_SIZE][SCREEN_PIXEL_SIZE];
        this.startKite = Main.startKite;
        runGame(0F, this.startKite);
        Main.isInstructions = false;

        switch(this.game) {
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
        if(this.game == 0) Main.isInCookie = false;
    }
    public void mouseEntered(MouseEvent e) {
        if(this.game == 0) Main.isInCookie = true;
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
        if(this.game == 0) {
            this.player.gainXP(this.cookieClickPower, this.cookieSegment, 255*(4-this.cookieClick));
            cookieChangeColor();
        }
    }

    public void clearScreen() {
        for(int i = 0; i < SCREEN_PIXEL_SIZE; i++) {
            for(int j = 0; j < SCREEN_PIXEL_SIZE; j++) {
                screen[0][i][j] = null;
            }
        }
        System.out.println("cleared");
    }
    public void runGame(float updateXP, Kite kite) {
        clearScreen();
        while(kite.isAlive()) {
            try{
                if(Main.isCookie && this.game != 0) Main.cookieMinigame.repaint();
                repaint();
                if(updateXP>0) Main.player.gainXP(updateXP, this.game, 255);
                Thread.sleep(Main.UPDATE_SPEED);
            } catch(InterruptedException e) {
                System.out.println(e);
            }
        }
        if(this.game != 4) {
            Main.player.changeColor(new Color(255-((255-BUTTON_COLORS[game].getRed())*Main.player.getXP(this.game))/255, 255-((255-BUTTON_COLORS[game].getGreen())*Main.player.getXP(this.game))/255, 255-((255-BUTTON_COLORS[game].getBlue())*Main.player.getXP(this.game))/255), this.game);
            System.out.println(Main.player.getXP(this.game));
        }
    }
    public void showButtons() {
        for(int i = 0; i < buttons.length; i++) {
            buttons[i].show(M_G, SCREEN_PIXEL_SIZE/4+(i%2)*SCREEN_PIXEL_SIZE/2, SCREEN_PIXEL_SIZE/4+((3-i)/2)*SCREEN_PIXEL_SIZE/2);
        }
    }

    public void kiteCookie() {
        this.game = 0;
        this.cookieClick = 3;
        this.cookieAutoPower = new float[] {0.5F, 0.5F, 0.5F, 0.5F};
        this.cookieClickPower = 5;
        this.cookieMaxClick = 1;
        this.cookieMaxTop = 4;
        this.cookieMaxBottom = 4;
        this.cookieSegment = buttons.length-1;
        for(int i = 0; i < buttons.length; i++) buttons[i] = new Kite(SCREEN_PIXEL_SIZE, BUTTON_COLORS[i], 0, i+"", IMMUNE_TIME*2);
    }
    public void kiteNeedle() {
        this.game = 2;
        xNeedle = new Needle(SCREEN_PIXEL_SIZE, player.getHeight(), true);
        yNeedle = new Needle(SCREEN_PIXEL_SIZE, player.getWidth(), false);

        runGame(0.03F, this.player);
    }
    public void kiteClassic() {
        this.game = 3;
        cloud1 = new Cloud(SCREEN_PIXEL_SIZE);
        cloud2 = new Cloud(SCREEN_PIXEL_SIZE);
        cloud3 = new Cloud(SCREEN_PIXEL_SIZE);

        runGame(0.03F, this.player);
    }
    public void kiteSimon() {
        this.game = 1;
        for(int i = 0; i < buttons.length; i++) buttons[i] = new Kite(SCREEN_PIXEL_SIZE, BUTTON_COLORS[i], 0, i+"", IMMUNE_TIME*2);
        clearScreen();
    }

    public Kite getPlayer() {
        return this.player;
    }

    public Kite getButton(int i) {
        return this.buttons[i];
    }

    public String getScreenType(int x, int y) {
        return this.screenType[x][y];
    }

    public void setScreenType(int x, int y, String type) {
        this.screenType[x][y] = type;
    }

    public void cookiePay(int price, int segment, int level) {
        while(price > 0) {
            if(price > this.player.getXP(this.cookieSegment)-(3-this.cookieClick)*255) {
                price -= this.player.getXP(this.cookieSegment)-(3-this.cookieClick)*255;
                this.player.loseXP(this.player.getXP(this.cookieSegment)-(3-this.cookieClick)*255, this.cookieSegment);
                cookieChangeColor();

                if(this.cookieSegment < 3) this.cookieSegment++;
                else {
                    this.cookieSegment = 0;
                    this.cookieClick++;
                }
            }
            else {
                this.player.loseXP(price, this.cookieSegment);
                cookieChangeColor();
                price = 0;
            }
        }

        switch(segment) {
            case 0:
                this.cookieAutoPower[level]*=5F/2F;
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
                this.player.changeColor(new Color(255, this.player.getXP(this.cookieSegment)-255*3, 255-(this.player.getXP(this.cookieSegment)-255*3)), this.cookieSegment);
                break;
            case 1:
                this.player.changeColor(new Color(this.player.getXP(this.cookieSegment)-255*2, 255-(this.player.getXP(this.cookieSegment)-255*2), 255), this.cookieSegment);
                break;
            case 2:
                this.player.changeColor(new Color(255-(this.player.getXP(this.cookieSegment)-255), this.player.getXP(this.cookieSegment)-255, this.player.getXP(this.cookieSegment)-255), this.cookieSegment);
                break;
            case 3:
                this.player.changeColor(new Color(255, 255-this.player.getXP(this.cookieSegment), 255-this.player.getXP(this.cookieSegment)), this.cookieSegment);
        }
    }

    @Override
    public void paint(Graphics g) {
        Main.currentScreen = this.screen;
        Main.currentScreenType = this.screenType;

        if(this.isStart) {
            super.paint(g);
            System.out.println("start");
        }
        Graphics2D g2 = (Graphics2D) g;

        if(Main.isInstructions && Main.currentGame == this.game) {
            clearScreen();
            for(int i = 0; i < SCREEN_PIXEL_SIZE; i++) {
                for(int j = 0; j < SCREEN_PIXEL_SIZE; j++) {
                    this.screen[1][i][j] = Color.BLACK;
                    this.screenType[i][j] = "background";
                }
            }
            this.startKite.show(M_G, SCREEN_PIXEL_SIZE/2, SCREEN_PIXEL_SIZE/2);
        } else {

            for (int i = 0; i < SCREEN_PIXEL_SIZE; i++) {
                for (int j = 0; j < SCREEN_PIXEL_SIZE; j++) {
                    this.screen[1][i][j] = Color.WHITE;
                    this.screenType[i][j] = "background";
                }
            }

            switch (this.game) {
                case 0:
                    this.player.makeImmune(1);

                    if (this.player.getXP(this.cookieSegment) - 255 * (3 - this.cookieClick) > 0 && this.player.getXP(this.cookieSegment) % 255 == 0) {
                        if (this.cookieSegment > 0) {
                            this.cookieSegment--;
                        } else if (this.cookieClick > this.cookieMaxClick) {
                            this.cookieSegment = 3;
                            this.cookieClick--;
                        }
                    }

                    if ((this.cookieClick >= this.cookieMaxTop && this.cookieSegment > 1) || (this.cookieClick >= this.cookieMaxBottom && this.cookieSegment < 2)) {
                        this.player.gainXP(this.cookieAutoPower[this.cookieSegment], this.cookieSegment, 255 * (4 - this.cookieClick));
                        cookieChangeColor();
                    }

                    if (this.cookieClick == 0 && this.cookieSegment == 0 && this.player.getXP(this.cookieSegment) == 255 * buttons.length) {
                        Main.player.gainXP(255, this.game, 255);
                        this.player.kill();
                    }

                    showButtons();
                    break;
                case 1:
                    if (!Main.isWait) {
                        if (Main.simonCounter < Main.simonOrder.length) {
                            if (Main.simonOrder[Main.simonCounter] == 4) {
                                Main.simonTimer = 1 - Main.IMMUNE_TIME;
                                Main.player.gainXP(Main.simonCounter * 2, this.game, 255);
                                if (Main.simonOrder[Main.simonOrder.length - 1] != 4) Main.player.kill();
                                Main.simonOrder[Main.simonCounter] = (int) Math.floor(Math.random() * 4);
                                while (!Main.buttons[Main.simonOrder[Main.simonCounter]].isAlive())
                                    Main.simonOrder[Main.simonCounter] = (int) Math.floor(Math.random() * 4);
                                for (int i = 0; i < this.buttons.length; i++) {
                                    this.buttons[i].resurrect();
                                    Main.buttons[i].resurrect();
                                }
                                Main.isWait = true;
                            }
                            showButtons();
                        } else {
                            Main.isWait = true;
                            Main.player.gainXP(Main.simonCounter * 2, this.game, 255);
                            Main.player.kill();
                        }
                    }
                    break;
                case 2:
                    xNeedle.move(player.getHeight());
                    xNeedle.show(M_G);
                    yNeedle.move(player.getWidth());
                    yNeedle.show(M_G);
                    break;
                case 3:
                    cloud1.move();
                    cloud1.show(M_G);
                    cloud2.move();
                    cloud2.show(M_G);
                    cloud3.move();
                    cloud3.show(M_G);
                    break;
                case 4:
                    showButtons();
            }
        }

        this.player.show(M_G, mouseX, mouseY);

        for(int i = 0; i < SCREEN_PIXEL_SIZE; i++) {
            for(int j = 0; j < SCREEN_PIXEL_SIZE; j++) {
                if(this.screen[0][i][j] != this.screen[1][i][j] || isStart) {
                    g2.setColor(this.screen[1][i][j]);
                    g2.fillRect(i * this.WINDOW_SIZE / SCREEN_PIXEL_SIZE + X_OFFSET, j * this.WINDOW_SIZE / SCREEN_PIXEL_SIZE + Y_OFFSET, this.WINDOW_SIZE / SCREEN_PIXEL_SIZE + 1, this.WINDOW_SIZE / SCREEN_PIXEL_SIZE + 1);
                    this.screen[0][i][j] = this.screen[1][i][j];
                }
            }
        }

        if(this.game == 0 && !(Main.isInstructions && Main.currentGame == 0)) {
            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Arial", Font.PLAIN, this.WINDOW_SIZE/20));
            for(int i = 0; i < this.buttons.length; i++) {
                if(this.buttons[i].isAlive()) {
                    g2.drawString("Price: " + (5 - this.buttons[i].getLives()) + " " + this.BUTTON_NAMES[i], this.WINDOW_SIZE / 10 + (i % 2) * this.WINDOW_SIZE / 2 + X_OFFSET, 2 * this.WINDOW_SIZE / 3 - (i / 2) * this.WINDOW_SIZE / 2 + Y_OFFSET);
                    switch (this.buttons[i].getLives()) {
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
