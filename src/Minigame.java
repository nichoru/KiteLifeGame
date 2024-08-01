import javax.swing.*; // lets me make a window
import java.awt.*; // helps with drawing on the window
import java.awt.event.*; // lets me use mouse events

public class Minigame extends JFrame implements MouseListener, MouseMotionListener {
    private final int WINDOW_SIZE; // the length of the window's sides (it's a square)

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

    private boolean isStart; // lets me only call super.paint(g) once, as calling it more caused flickering

    // all the variables for the cookie minigame
    private int cookieSegment; // the current segment being coloured in
    private int cookieClick; // the current colour being used
    private float cookieClickPower; // the amount of xp each click gives
    private final float[] COOKIE_AUTO_POWER = new float[] {0.5F, 0.5F, 0.5F, 0.5F}; // the amount of xp each segment automatically can gain
    private int cookieMaxClick; // the maximum colour that clicks can get you to
    private int cookieMaxTop; // the maximum colour that the top segments can automatically get to
    private int cookieMaxBottom; // the maximum colour that the bottom segments can automatically get to
    private String[] describeArray; // the array that holds and prints the descriptions of the buttons

    // the two needles for the needle minigame
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
        this.isStart = true;
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

    public void kiteCookie() { // sets up the cookie minigame (this isn't run by itself)
        this.cookieClick = 3;
        this.cookieClickPower = 5;
        this.cookieMaxClick = 3;
        this.cookieMaxTop = 4;
        this.cookieMaxBottom = 4;
        this.cookieSegment = this.BUTTONS.length-1;
        for(int i = 0; i < this.BUTTONS.length; i++) this.BUTTONS[i] = new Kite(this.SCREEN_PIXEL_SIZE, Main.BUTTON_COLORS[i], 0, i+"", this.IMMUNE_TIME*2); // making the buttons
    }
    public void kiteSimon() { // sets up the simon minigame
        for(int i = 0; i < this.BUTTONS.length; i++) this.BUTTONS[i] = new Kite(this.SCREEN_PIXEL_SIZE, Main.BUTTON_COLORS[i], 0, i+"", this.IMMUNE_TIME*2);
        this.clearScreen();
        this.repaint();
    }
    public void kiteNeedle() { // runs the needle minigame
        this.runGame(0.03F, this.PLAYER);
    }
    public void kiteClassic() { // sets up and runs the classic minigame
        for(int i = 0; i < 3; i++) this.CLOUDS[i] = new Cloud(this.SCREEN_PIXEL_SIZE);

        this.runGame(0.03F, this.PLAYER);
    }
    public void runGame(float updateXP, Kite kite) { // runs the game on the minigame screen updating the screen
        this.clearScreen(); // clears the screen so it can be redrawn

        while(kite.isAlive()) { // while the kite in question is alive, keep updating the screen
            try{
                if(Main.isCookie && this.GAME != 0) Main.cookieMinigame.repaint(); // runs the cookie minigame in the background if needed
                this.repaint();
                if(updateXP>0) Main.player.gainXP(updateXP, this.GAME, 255); // gain xp over time (depending on the game)
                Thread.sleep(Main.UPDATE_SPEED); // waits for a bit before updating the screen again
            } catch(InterruptedException e) {
                System.out.println(e);
            }
        }

        Main.player.changeColor(new Color(255-((255-Main.BUTTON_COLORS[this.GAME].getRed())*Main.player.getXP(this.GAME))/255, 255-((255-Main.BUTTON_COLORS[this.GAME].getGreen())*Main.player.getXP(this.GAME))/255, 255-((255-Main.BUTTON_COLORS[this.GAME].getBlue())*Main.player.getXP(this.GAME))/255), this.GAME); // changes the player's colour based on how well they did
    }

    public void mouseExited(MouseEvent e) {
        if(this.GAME == 0 || this.GAME == 1) {
            if(this.GAME == 0) Main.isInCookie = false; // if the mouse leaves the cookie minigame, it stops registering the mouse as on that screen
            // moving the kite to the middle to avoid weird stuff when leaving the screen for button-based minigames
            this.mouseX = this.SCREEN_PIXEL_SIZE/2;
            this.mouseY = this.SCREEN_PIXEL_SIZE/2;
        }
    }
    public void mouseEntered(MouseEvent e) {
        if(this.GAME == 0) Main.isInCookie = true; // if the mouse enters the cookie minigame, it registers the mouse as on that screen
    }
    public void mouseMoved(MouseEvent e) { // updates the mouse's position in the pixelated screen array when moved
        this.mouseX = (e.getX() - Main.X_OFFSET)*this.SCREEN_PIXEL_SIZE/this.WINDOW_SIZE;
        this.mouseY = (e.getY() - Main.Y_OFFSET)*this.SCREEN_PIXEL_SIZE/this.WINDOW_SIZE;
    }
    public void mouseDragged(MouseEvent e) { // also updates the mouse's position in the pixelated screen array when dragged
        this.mouseX = (e.getX() - Main.X_OFFSET)*this.SCREEN_PIXEL_SIZE/this.WINDOW_SIZE;
        this.mouseY = (e.getY() - Main.Y_OFFSET)*this.SCREEN_PIXEL_SIZE/this.WINDOW_SIZE;
    }
    public void mouseClicked(MouseEvent e) { // updates the player's XP when the mouse is clicked in cookie
        if(this.GAME == 0 && !(Main.isInstructions && Main.currentGame == 0)) {
            this.PLAYER.gainXP(this.cookieClickPower, this.cookieSegment, 255*(4-this.cookieClick));
            this.cookieChangeColor();
        }
    }

    // need to be there as a part of MouseListener, but aren't used
    public void mouseReleased(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}

    public void clearScreen() { // clears the screen array so the next time it's drawn, everything is printed
        for(int i = 0; i < this.SCREEN_PIXEL_SIZE; i++) {
            for(int j = 0; j < this.SCREEN_PIXEL_SIZE; j++) {
                this.SCREEN[0][i][j] = null;
            }
        }
    }
    public void showButtons() { // shows the buttons on the screen
        for(int i = 0; i < this.BUTTONS.length; i++) {
            this.BUTTONS[i].show(this.M_G, this.SCREEN_PIXEL_SIZE/4+(i%2)*this.SCREEN_PIXEL_SIZE/2, this.SCREEN_PIXEL_SIZE/4+((3-i)/2)*this.SCREEN_PIXEL_SIZE/2);
        }
    }

    public void cookiePay(int price, int segment, int level) { // buys an upgrade in the cookie minigame and updates abilities and appearance based on that
        while(price > 0) { // the payment, both in removing xp and visuals
            if(price > this.PLAYER.getXP(this.cookieSegment)-(3-this.cookieClick)*255) {
                price -= this.PLAYER.getXP(this.cookieSegment)-(3-this.cookieClick)*255;
                this.PLAYER.loseXP(this.PLAYER.getXP(this.cookieSegment)-(3-this.cookieClick)*255, this.cookieSegment);
                this.cookieChangeColor();

                if(this.cookieSegment < 3) this.cookieSegment++;
                else {
                    this.cookieSegment = 0;
                    this.cookieClick++;
                }
            }
            else {
                this.PLAYER.loseXP(price, this.cookieSegment);
                this.cookieChangeColor();
                price = 0;
            }
        }

        switch(segment) { // gaining the new ability
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
    private void cookieChangeColor() { // changes the colour of the kite in the cookie minigame so that it transitions smoothly between colours
        switch(this.cookieClick) {
            case 0: // magenta -> yellow
                this.PLAYER.changeColor(new Color(255, this.PLAYER.getXP(this.cookieSegment)-255*3, 255-(this.PLAYER.getXP(this.cookieSegment)-255*3)), this.cookieSegment);
                break;
            case 1: // cyan -> magenta
                this.PLAYER.changeColor(new Color(this.PLAYER.getXP(this.cookieSegment)-255*2, 255-(this.PLAYER.getXP(this.cookieSegment)-255*2), 255), this.cookieSegment);
                break;
            case 2: // red -> cyan
                this.PLAYER.changeColor(new Color(255-(this.PLAYER.getXP(this.cookieSegment)-255), this.PLAYER.getXP(this.cookieSegment)-255, this.PLAYER.getXP(this.cookieSegment)-255), this.cookieSegment);
                break;
            case 3: // white -> red
                this.PLAYER.changeColor(new Color(255, 255-this.PLAYER.getXP(this.cookieSegment), 255-this.PLAYER.getXP(this.cookieSegment)), this.cookieSegment);
        }
    }

    public Kite getPlayer() { // returns the player
        return this.PLAYER;
    }
    public Kite getButton(int i) { // returns a button
        return this.BUTTONS[i];
    }

    @Override
    public void paint(Graphics g) { // prints out the game on the screen
        // lets M_G know which screen to draw and do collision on
        Main.currentScreen = this.SCREEN;
        Main.currentScreenType = this.SCREEN_TYPE;

        if(this.isStart) { // initialises some things, but I don't want this running more than once
            super.paint(g);
            this.isStart = false;
        }

        Graphics2D g2 = (Graphics2D) g; // lets me actually draw on the canvas

        if(Main.isInstructions && Main.currentGame == this.GAME) { // makes the background black and displays the start kite when getting instructions for this game
            this.clearScreen();
            for(int i = 0; i < this.SCREEN_PIXEL_SIZE; i++) {
                for(int j = 0; j < this.SCREEN_PIXEL_SIZE; j++) {
                    this.SCREEN[1][i][j] = Color.BLACK;
                    this.SCREEN_TYPE[i][j] = "background";
                }
            }
            Main.startKite.show(this.M_G, this.SCREEN_PIXEL_SIZE/2, this.SCREEN_PIXEL_SIZE/2);
        } else { // runs the game after instructions are done
            for (int i = 0; i < this.SCREEN_PIXEL_SIZE; i++) { // makes the background white
                for (int j = 0; j < this.SCREEN_PIXEL_SIZE; j++) {
                    this.SCREEN[1][i][j] = Color.WHITE;
                    this.SCREEN_TYPE[i][j] = "background";
                }
            }

            switch (this.GAME) { // does different stuff depending on the game
                case 0: // cookie minigame
                    this.PLAYER.makeImmune(1); // the player can't die in this game (besides the end)

                    if (this.PLAYER.getXP(this.cookieSegment) - 255 * (3 - this.cookieClick) > 0 && this.PLAYER.getXP(this.cookieSegment) % 255 == 0) { // if you have the max xp in a segment, move to the next
                        if (this.cookieSegment > 0) {
                            this.cookieSegment--;
                        } else if (this.cookieClick > this.cookieMaxClick) {
                            this.cookieSegment = 3;
                            this.cookieClick--;
                        }
                    }

                    if((this.cookieClick >= this.cookieMaxTop && this.cookieSegment > 1) || (this.cookieClick >= this.cookieMaxBottom && this.cookieSegment < 2)) { // automatically colours in segments over time when appropriate
                        this.PLAYER.gainXP(this.COOKIE_AUTO_POWER[this.cookieSegment], this.cookieSegment, 255 * (4 - this.cookieClick));
                        this.cookieChangeColor();
                    }

                    if(this.cookieClick == 0 && this.cookieSegment == 0 && this.PLAYER.getXP(this.cookieSegment) == 255 * this.BUTTONS.length) { // if the kite is fully yellow, kill it and gain xp in the main game
                        Main.player.gainXP(255, this.GAME, 255);
                        this.PLAYER.kill();
                    }

                    this.showButtons();
                    break;
                case 1: // simon minigame
                    if (!Main.isWait) { // if the player isn't waiting for the main screen to show the colours, let the player move and play the game
                        if (Main.simonCounter < Main.simonOrder.length) {
                            if (Main.simonOrder[Main.simonCounter] == 4) { // if you get to the end of the current sequence (and it's still possible to keep going), add a new kite to the sequence, then go back to the main screen
                                Main.simonTimer = 1 - Main.IMMUNE_TIME;
                                Main.player.gainXP(Main.simonCounter * 2, this.GAME, 255);
                                if (Main.simonOrder[Main.simonOrder.length - 1] != 4) Main.player.kill();
                                Main.simonOrder[Main.simonCounter] = (int) Math.floor(Math.random() * 4);
                                while (!Main.buttons[Main.simonOrder[Main.simonCounter]].isAlive()) Main.simonOrder[Main.simonCounter] = (int) Math.floor(Math.random() * 4);
                                for (int i = 0; i < this.BUTTONS.length; i++) {
                                    this.BUTTONS[i].resurrect();
                                    Main.buttons[i].resurrect();
                                }
                                Main.isWait = true;
                            }
                        } else { // if you get to the end of the simon game, gain xp and kill the player to return to the home screen
                            Main.isWait = true;
                            Main.player.gainXP(Main.simonCounter * 2, this.GAME, 255);
                            Main.player.kill();
                        }
                    }
                    this.showButtons();
                    break;
                case 2: // needle minigame - moves and displays the needles on the screen
                    this.X_NEEDLE.move(this.PLAYER.getHeight());
                    this.X_NEEDLE.show(this.M_G);
                    this.Y_NEEDLE.move(this.PLAYER.getWidth());
                    this.Y_NEEDLE.show(this.M_G);
                    break;
                case 3: // classic minigame - moves and displays the clouds on the screen
                    for(Cloud cloud : this.CLOUDS) {
                        cloud.move();
                        cloud.show(this.M_G);
                    }
            }
        }
        this.PLAYER.show(this.M_G, this.mouseX, this.mouseY); // always shows the player

        for(int i = 0; i < this.SCREEN_PIXEL_SIZE; i++) { // prints out the screen array
            for(int j = 0; j < this.SCREEN_PIXEL_SIZE; j++) {
                if(this.SCREEN[0][i][j] != this.SCREEN[1][i][j]) {
                    g2.setColor(this.SCREEN[1][i][j]);
                    g2.fillRect(i * this.WINDOW_SIZE / this.SCREEN_PIXEL_SIZE + Main.X_OFFSET, j * this.WINDOW_SIZE / this.SCREEN_PIXEL_SIZE + Main.Y_OFFSET, this.WINDOW_SIZE / this.SCREEN_PIXEL_SIZE + 1, this.WINDOW_SIZE / this.SCREEN_PIXEL_SIZE + 1);
                    this.SCREEN[0][i][j] = this.SCREEN[1][i][j];
                }
            }
        }

        if(this.GAME == 0 && !(Main.isInstructions && Main.currentGame == 0)) { // displays the prices on the screen in the cookie minigame after the instructions are done
            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Arial", Font.PLAIN, this.WINDOW_SIZE/20));
            for(int i = 0; i < this.BUTTONS.length; i++) {
                if(this.BUTTONS[i].isAlive()) {
                    g2.drawString("Price: " + (5 - this.BUTTONS[i].getLives()) + " " + this.BUTTON_NAMES[i], this.WINDOW_SIZE / 10 + (i % 2) * this.WINDOW_SIZE / 2 + Main.X_OFFSET, 2 * this.WINDOW_SIZE / 3 - (i / 2) * this.WINDOW_SIZE / 2 + Main.Y_OFFSET); // prints out the prices for each button
                    switch (this.BUTTONS[i].getLives()) { // display a different upgrade description depending on what upgrade is next available
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
                            if (i > 0) this.describeArray = new String[]{"Buy for clicks", "to be able to", "turn the kite " + this.BUTTON_NAMES[i - 1]};
                            else this.describeArray = new String[]{"Buy for clicks", "to be better"};
                            break;
                        case 3:
                            this.describeArray = new String[]{"Buy for the lower two", "segments to auto", "turn " + this.BUTTON_NAMES[i]};
                            break;
                        case 4:
                            this.describeArray = new String[]{"Buy for the upper two", "segments to auto", "turn " + this.BUTTON_NAMES[i]};
                    }
                    for (int j = 0; j < this.describeArray.length; j++) { // prints out the upgrade descriptions
                        g2.drawString(this.describeArray[j], (i % 2) * this.WINDOW_SIZE / 2 + Main.X_OFFSET, 5 * this.WINDOW_SIZE / 6 + (j + 1) * this.WINDOW_SIZE / 20 - (i / 2) * this.WINDOW_SIZE / 2 + Main.Y_OFFSET);
                    }
                }
            }
        }
    }
}