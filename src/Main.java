import javax.swing.*; // lets me make a window
import java.awt.*; // helps with drawing on the window
import java.awt.event.*; // lets me use mouse events

public class Main extends JFrame implements MouseMotionListener {

    public static final Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize(); // gets the user's actual screen size
    private final int WINDOW_SIZE; // the length of the window's sides (it's a square)

    // accounts for how the canvas is automatically drawn at slightly different coordinates to the window
    public final static int X_OFFSET = 8;
    private final int Y_OFFSET = 31;

    private final int SCREEN_PIXEL_SIZE = 150; // the length of the sides of the pixelated screen
    public static Color[][][] screen; // the pixelated screen array - this holds both the previous screen (so it can check for changes) and the current screen, as well as the x and y of a colour
    public static Color[][][] currentScreen; // this lets MyGraphics know which screen to draw on
    public static String[][] screenType; // tells me the type of each pixel in the screen array - this is used for collision detection in
    public static String[][] currentScreenType; // this lets MyGraphics know which screen to draw collision on

    private int mouseX; // the x coordinate of the mouse within the pixelated screen array
    private int mouseY; // the y coordinate of the mouse within the pixelated screen array

    private final MyGraphics M_G = new MyGraphics(); // makes a new MyGraphics object, which is used for drawing on the screen and collision detection

    public static Kite player; // the player's kite that moves with their mouse
    public static Kite startKite; // the kite that you kill to start the minigames during the instructions screen
    public static Kite[] buttons = new Kite[4]; // the coloured kites in the home screen and simon screen
    private final Color[] BUTTON_COLORS = {Color.YELLOW, Color.MAGENTA, Color.CYAN, Color.RED}; // the colours of the button kites
    public static final int IMMUNE_TIME = 50; // the time in frames that kites are immune for after losing a life

    public static int currentGame; // the type the current game is - 0 represents Kite Clicker (aka cookie), 1 represents Kite Says (aka simon), 2 represents Kite the Needle (aka needle), 3 represents Kite Flying 101 (aka classic), 4 represents the home screen
    public static int nextGame; // the type the next game will be - this changes depending on which button you last hovered over
    public static Minigame currentMinigame; // the current minigame object
    private boolean isWin; // whether the player has won the game (this triggers when the player has fully coloured in their kite)
    public static boolean isInstructions; // whether to show the instructions screen
    private String[] instructionsArray; // changes based on the game and contains the instructions, which are printed on the screen
    private boolean isStart; // lets me only call super.paint(g) once, as calling it more caused flickering
    public static final int UPDATE_SPEED = 10; // milliseconds between screen updates

    public static Minigame cookieMinigame; // the cookie minigame object (this enables it to be played at the same time as other games)
    public static boolean isCookie; // whether the cookie minigame is being played
    public static boolean isInCookie; // whether the cursor is in the cookie minigame

    public static boolean isWait; // whether the game is displaying the button order in the simon minigame
    public static int simonTimer; // the timer for the simon minigame to check when it's done displaying buttons
    public static int[] simonOrder; // the order of the buttons in the simon minigame
    public static int simonCounter; // the current position in the simonOrder[] array

    public static void main(String[] args) { // makes a new Main object when run
        new Main();
    }

    public Main() { // constructor for Main
        setTitle("Kite Life"); // names the window "Kite Life"

        // makes the window a square that's based on the smaller out of the user's screen height and width
        if(SCREEN_SIZE.width < SCREEN_SIZE.height) WINDOW_SIZE = SCREEN_SIZE.width/2;
        else WINDOW_SIZE = SCREEN_SIZE.height/2;
        getContentPane().setPreferredSize(new Dimension(WINDOW_SIZE, WINDOW_SIZE));

        // creating the main window and canvas
        getContentPane().setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        JPanel window = new JPanel();
        window.setPreferredSize(new Dimension(WINDOW_SIZE, WINDOW_SIZE));
        Canvas canvas = new Canvas();
        window.add(canvas);

        setLocation((SCREEN_SIZE.width-WINDOW_SIZE)/2, (SCREEN_SIZE.height-WINDOW_SIZE)/2); // centres the window on the user's screen

        addMouseMotionListener(this); // lets me see if the mouse moves

        // finishes making the window
        this.pack();
        this.toFront();
        this.setVisible(true);

        // setting up a few variables
        screen = new Color[2][SCREEN_PIXEL_SIZE][SCREEN_PIXEL_SIZE];
        screenType = new String[SCREEN_PIXEL_SIZE][SCREEN_PIXEL_SIZE];
        isCookie = false;
        isInCookie = false;
        isStart = true;
        isWait = true;
        player = new Kite(SCREEN_PIXEL_SIZE, Color.WHITE, 0, "player", IMMUNE_TIME);
        startKite = new Kite(SCREEN_PIXEL_SIZE, Color.WHITE, 0, "start", IMMUNE_TIME);
        for(int i = 0; i < buttons.length; i++) buttons[i] = new Kite(SCREEN_PIXEL_SIZE, BUTTON_COLORS[i], 0, i+"", IMMUNE_TIME);

        kiteHome();
    }

    public void kiteHome() { // shows the home screen and sets up whatever minigames are being played
        currentGame = 4; // this represents the home screen as the main focus when repainting

        // makes kites have less health the more xp you have for that minigame and checks if the kite has full xp in all (and shows the win screen if so)
        isWin = true;
        for(int i = 0; i < buttons.length; i++) {
            buttons[i].setLives(4-(player.getXP(i)/63));
            if(!buttons[i].isAlive() && player.getXP(i) != 255) buttons[i].setLives(1);
            if(buttons[i].isAlive()) isWin = false;
        }

        // selecting the next game
        isInstructions = true;
        runGame();
        currentGame = nextGame;

        // puts it in instruction mode for the next game
        clearScreen();
        repaint();
        startKite.resurrect();

        // makes a new Minigame based on which one is selected (cookie aka Kite Clicker has a separate object, so it can be played at the same time as other games)
        if(currentGame!=0) currentMinigame = new Minigame(currentGame, WINDOW_SIZE);
        else if(!isCookie) {
            isCookie = true;
            cookieMinigame = new Minigame(currentGame, WINDOW_SIZE);
        }
        if(currentGame==1) kiteSimon(); // the simon minigame partially includes the main screen, so this is run here

        // after the minigame is done, shuts down the minigame and brings the player back to life, then runs through the selection screen again
        player.resurrect();
        if(currentGame != 0) currentMinigame.dispose();
        if(!isWin) kiteHome();
    }
    public void runGame() { // runs the game on the main screen by updating the screen
        clearScreen(); // clears the screen so it can be redrawn

        while(player.isAlive()) { // while the player is alive, keep updating the screen
            try{
                if(isCookie) { // runs the cookie minigame if it's being played (this allows it to be played at the same time as the home screen
                    cookieMinigame.repaint();
                    if(!cookieMinigame.getPlayer().isAlive()) { // closes the cookie minigame when the player finishes it
                        player.changeColor(BUTTON_COLORS[0], 0);
                        cookieMinigame.dispose();
                        isCookie = false;
                        isInCookie = false;

                        // checks if this was the last minigame to be won, and if so, triggers the win screen
                        isWin = true;
                        for(int i = 0; i < buttons.length; i++) if(player.getXP(i) < 255) isWin = false;
                        if(isWin) kiteHome();
                    }
                }
                if(currentGame == 1 && !isWait) currentMinigame.repaint(); // runs the simon minigame if it's time for the player to copy the buttons
                else repaint(); // otherwise, just repaint the home screen
                if(currentGame == 4) { // runs through all the buttons and checks if they're dead, and if so, kills the player (which makes the home screen stop running)
                    for(int i = 0; i < buttons.length; i++) {
                        if(!buttons[i].isAlive() && player.getXP(i) != 255) player.kill();
                    }
                }
                Thread.sleep(UPDATE_SPEED); // waits for a bit before updating the screen again
            } catch(InterruptedException e) {
                System.out.println(e); // if there's an error to do with Thread.sleep(), print it
            }
        }

        // when the player dies, bring it back to life and change the colour to represent new xp gained if that happens
        if(currentGame != 4) player.changeColor(new Color(255-((255-BUTTON_COLORS[currentGame].getRed())*player.getXP(currentGame))/255, 255-((255-BUTTON_COLORS[currentGame].getGreen())*player.getXP(currentGame))/255, 255-((255-BUTTON_COLORS[currentGame].getBlue())*player.getXP(currentGame))/255), currentGame);
        player.resurrect();
        for (Kite button : buttons) button.resurrect();
    }

    public void kiteSimon() { // special setup for the simon minigame display screen for the player to copy
        simonTimer = 1-IMMUNE_TIME; // waits a bit to display the first button
        simonOrder = new int[buttons.length*4]; // resets the button order if the game has been played before
        for(int i = 0; i < simonOrder.length; i++) { // sets the first three buttons to random colours and the remaining all to 4 (which means to stop)
            if(i<3) simonOrder[i] = (int) Math.floor(Math.random()*4);
            else simonOrder[i] = 4;
        }
        isWait = true; // the player has to wait and watch the colours when isWait is true

        runGame(); // simon is run from the main class, unlike the other minigames

        simonCounter = simonOrder.length;
    }

    public void mouseMoved(MouseEvent e) { // updates the mouse's position in the pixelated screen array when moved
        mouseX = (e.getX() - X_OFFSET)*SCREEN_PIXEL_SIZE/WINDOW_SIZE;
        mouseY = (e.getY() - Y_OFFSET)*SCREEN_PIXEL_SIZE/WINDOW_SIZE;
    }
    public void mouseDragged(MouseEvent e) { // also updates the mouse's position in the pixelated screen array when dragged
        mouseX = (e.getX() - X_OFFSET)*SCREEN_PIXEL_SIZE/WINDOW_SIZE;
        mouseY = (e.getY() - Y_OFFSET)*SCREEN_PIXEL_SIZE/WINDOW_SIZE;
    }

    public void clearScreen() { // clears the screen array so the next time it's drawn, everything is printed
        for(int i = 0; i < SCREEN_PIXEL_SIZE; i++) {
            for(int j = 0; j < SCREEN_PIXEL_SIZE; j++) {
                screen[0][i][j] = null;
            }
        }
    }
    public void showButtons() { // shows the buttons on the screen
        if(player.getXP(0) == 255 && currentGame != 1) { // if the cookie minigame is complete, kill the yellow kite, so it can't be played again
            buttons[0].kill();
        }
        for(int i = 0; i < buttons.length; i++) { // shows the buttons in the four corners of the screen
            buttons[i].show(M_G, SCREEN_PIXEL_SIZE/4+(i%2)*SCREEN_PIXEL_SIZE/2, SCREEN_PIXEL_SIZE/4+((3-i)/2)*SCREEN_PIXEL_SIZE/2);
        }
    }

    @Override
    public void paint(Graphics g) { // prints out the game on the screen
        // lets M_G know which screen to draw and do collision on
        currentScreen = screen;
        currentScreenType = screenType;

        if(isStart) { // initialises some things, but I don't want this running more than once
            super.paint(g);
            isStart = false;
        }

        Graphics2D g2 = (Graphics2D) g; // lets me actually draw on the canvas

        if(isInstructions) { // makes the background black and displays the selection buttons if on the home screen
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

        } else { // otherwise, show the stuff for the simon minigame
            for (int i = 0; i < SCREEN_PIXEL_SIZE; i++) { // makes the background white
                for (int j = 0; j < SCREEN_PIXEL_SIZE; j++) {
                    screen[1][i][j] = Color.WHITE;
                    screenType[i][j] = "background";
                }
            }

            if (simonTimer / IMMUNE_TIME == simonOrder.length) { // if the simon minigame is done displaying the buttons (due to having completely killed all kites), the player can copy them
                simonCounter = 0;
                isWait = false;
            } else { // otherwise, it displays the buttons in the order they were set
                if (simonOrder[simonTimer / IMMUNE_TIME] != 4) {
                    simonCounter = simonOrder.length;
                    if (simonTimer % IMMUNE_TIME == 0) buttons[simonOrder[simonTimer / IMMUNE_TIME]].loseLife();
                    player.makeImmune(1);
                    simonTimer++;
                } else { // if the button is set to 4, the buttons stop displaying, as this is the end of what the player has to mimic for now
                    simonCounter = 0;
                    isWait = false;
                }
            }
            showButtons();
        }

        for(int i = 0; i < SCREEN_PIXEL_SIZE; i++) { // prints out the screen array
            for(int j = 0; j < SCREEN_PIXEL_SIZE; j++) {
                if(screen[0][i][j] != screen[1][i][j]) {
                    g2.setColor(screen[1][i][j]);
                    g2.fillRect(i * WINDOW_SIZE / SCREEN_PIXEL_SIZE + X_OFFSET, j * WINDOW_SIZE / SCREEN_PIXEL_SIZE + Y_OFFSET, WINDOW_SIZE / SCREEN_PIXEL_SIZE + 1, WINDOW_SIZE / SCREEN_PIXEL_SIZE + 1);
                    screen[0][i][j] = screen[1][i][j];
                }
            }
        }

        if(isInstructions) { // prints out the instructions on the screen
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.PLAIN, WINDOW_SIZE/20));
            switch(currentGame) { // changes the instructions based on what game is next
                case 0:
                    instructionsArray = new String[]{"Welcome to Kite Clicker!", "Click lots to gain more colour", "When you have enough full segments of", "a colour, you can buy upgrades by", "hovering over that colour kite", "The aim is to have a fully yellow kite", "YOU CAN PLAY OTHER GAMES STILL"};
                    break;
                case 1:
                    instructionsArray = new String[]{"Welcome to Kite Says!", "Watch the buttons light up", "on the main screen", "Afterwards, hover over them on the", "small screen in the same order"};
                    break;
                case 2:
                    instructionsArray = new String[]{"Welcome to Kite the Needle!", "Thread the needle by avoiding", "the obstacles for as long as you can", "The gaps will get smaller and smaller"};
                    break;
                case 3:
                    instructionsArray = new String[]{"Welcome to Kite Flying 101!", "Dodge the clouds and try to stay alive", "The clouds will get faster and faster"};
                    break;
                case 4:
                    if (isWin) instructionsArray = new String[]{"Congratulations!", "You have won the game!"};
                    else
                        instructionsArray = new String[]{"Welcome to Kite Life!", "Hover over the kites to start each", "minigame (they will disappear ", "as you gain xp)", "", "The aim is to have a fully coloured kite"};
                    for (int i = 0; i < instructionsArray.length; i++) {
                        g2.drawString(instructionsArray[i], WINDOW_SIZE / 20 + X_OFFSET, (2 * i + 7) * WINDOW_SIZE / 20 + Y_OFFSET);
                    }
                    if (isCookie)
                        g2.drawString("Tip: you can play multiple games at once!", WINDOW_SIZE / 20 + X_OFFSET, (instructionsArray.length * 2 + 7) * WINDOW_SIZE / 20 + Y_OFFSET);
                    else if (player.getXP(0) == 0)
                        g2.drawString("Tip: start with yellow!", WINDOW_SIZE / 20 + X_OFFSET, (instructionsArray.length * 2 + 7) * WINDOW_SIZE / 20 + Y_OFFSET);
                    instructionsArray = new String[]{""};
            }

            for(int i = 0; i < instructionsArray.length; i++) { // actually prints instructions
                g2.drawString(instructionsArray[i], WINDOW_SIZE/10+X_OFFSET, (i+1)*WINDOW_SIZE/10+Y_OFFSET);
            }
            if(currentGame != 4) g2.drawString("Hover over the other kite to start", WINDOW_SIZE / 10 + X_OFFSET, (instructionsArray.length + 2) * WINDOW_SIZE / 10 + Y_OFFSET); // this instruction is in all games except the home screen
        }
    }
}