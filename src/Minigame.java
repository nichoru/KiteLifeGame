import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Minigame extends JFrame implements ActionListener, MouseListener, MouseMotionListener {
    private JPanel window = new JPanel();
    private Canvas canvas = new Canvas();
    private String title = "Kite Life";
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private int windowWidth;
    private int windowHeight;
    private float scale;
    private int mouseX;
    private int mouseY;
    private int xOffset = 8;
    private int yOffset = 31;
    private static int screenPixelSize = 150;
    private MyGraphics mg = new MyGraphics(Color.BLACK);
    private Kite player;
    public static int game;
    public static int nextGame;
    public static Kite[] buttons = new Kite[4];
    private Color[] buttonColors = {Color.YELLOW, Color.MAGENTA, Color.CYAN, Color.RED};
    public static int immuneTime = 50;
    public static int simonTimer;
    public static int[] simonOrder;
    public static int simonCounter;
    private Needle xNeedle;
    private Needle yNeedle;
    private Cloud cloud1;
    private Cloud cloud2;
    private Cloud cloud3;
    public static Color[][][] screen = new Color[2][screenPixelSize][screenPixelSize];
    public static String[][] screenType = new String[screenPixelSize][screenPixelSize];
    private boolean isStart = true;
    private final int UPDATE_SPEED = 10; // milliseconds between screen updates

    public Minigame(int game, int windowWidth, int windowHeight) {
        setTitle(title);
        this.scale = (float) 1/2;
        this.windowWidth = (int) ((screenSize.width-windowWidth)*this.scale);
        this.windowHeight = (int) ((screenSize.height-windowHeight)*this.scale);
        if(this.windowWidth < this.windowHeight) this.windowHeight = this.windowWidth; // makes the window a square that's based on the smaller out of the user's screen height and width
        else this.windowWidth = this.windowHeight;
        getContentPane().setPreferredSize(new Dimension(this.windowWidth, this.windowHeight));
        getContentPane().setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        window.setPreferredSize(new Dimension(this.windowWidth, this.windowHeight));
        window.add(canvas);

        setLocation((game%2)*(screenSize.width/2)+(screenSize.width-windowWidth-this.windowWidth*2)/4, ((3-game)/2)*(screenSize.height/2)+(screenSize.height-windowHeight)/4); // centres the window on the user's screen

        addMouseListener(this);
        addMouseMotionListener(this);

        this.pack();
        this.toFront();
        this.setVisible(true);

        player = Main.player;
        switch(game) {
            case 0:
                kiteSimon();
                break;
            case 1:
                break;
            case 2:
                kiteNeedle();
                break;
            case 3:
                kiteClassic();
        }
        this.setVisible(false);
    }

    public void actionPerformed(ActionEvent e) {

    }

    public void mouseExited(MouseEvent e) {System.out.println("exit");}
    public void mouseEntered(MouseEvent e) {
        System.out.println("enter");
        Main.currentScreen = this.screen;
        Main.currentScreenType = this.screenType;
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
                repaint();
                if(updateXP>0) player.gainXP(updateXP);
                Thread.sleep(UPDATE_SPEED);
            } catch(InterruptedException e) {
                System.out.println(e);
            }
        }
        if(game != 4) {
            player.changeColor(new Color(255-((255-buttonColors[game].getRed())*player.getXP())/255, 255-((255-buttonColors[game].getGreen())*player.getXP())/255, 255-((255-buttonColors[game].getBlue())*player.getXP())/255));
            System.out.println(player.getXP());
        }
    }
    public void showButtons() {
        for(int i = 0; i < buttons.length; i++) {
            if(buttons[i].isAlive()) buttons[i].show(mg, screenPixelSize/4+(i%2)*screenPixelSize/2, screenPixelSize/4+((3-i)/2)*screenPixelSize/2);
        }
    }

    public void kiteNeedle() {
        game = 2;
        xNeedle = new Needle(screenPixelSize, player.getHeight(), true);
        yNeedle = new Needle(screenPixelSize, player.getWidth(), false);

        runGame(0.01F);

        //kiteHome();
    }
    public void kiteClassic() {
        game = 3;
        cloud1 = new Cloud(screenPixelSize);
        cloud2 = new Cloud(screenPixelSize);
        cloud3 = new Cloud(screenPixelSize);

        runGame(0.01F);

        //kiteHome();
    }
    public void kiteSimon() {
        game = 0;
        simonTimer = 1-immuneTime;
        simonOrder = new int[16];
        for(int i = 0; i < simonOrder.length; i++) {
            if(i<3) simonOrder[i] = (int) Math.floor(Math.random()*4);
            else simonOrder[i] = 4;
        }

        runGame(0F);

        simonCounter = simonOrder.length;
        //kiteHome();
    }

    @Override
    public void paint(Graphics g) {
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

        switch(game) {
            case 0:
                if(simonCounter<simonOrder.length) {
                    if(simonOrder[simonCounter] == 4) {
                        simonOrder[simonCounter] = (int) Math.floor(Math.random() * 4);
                        while(!buttons[simonOrder[simonCounter]].isAlive()) simonOrder[simonCounter] = (int) Math.floor(Math.random() * 4);
                        simonTimer = 1 - immuneTime;
                        player.gainXP(simonCounter * 2);
                    }
                }

                if(simonOrder[simonTimer/immuneTime] != 4) {
                    simonCounter = simonOrder.length;
                    if(simonTimer%immuneTime == 0) {
                        buttons[simonOrder[simonTimer/immuneTime]].loseLife();
                    }
                    player.makeImmune(1);
                    simonTimer++;
                } else {
                    if(simonCounter == simonOrder.length) simonCounter = 0;
                }
                showButtons();
                break;
            case 1:
                break;
            case 2:
                xNeedle.move(player.getHeight());
                xNeedle.show(mg);
                yNeedle.move(player.getWidth());
                yNeedle.show(mg);
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
