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
    private int screenPixelSize = 150;
    private MyGraphics mg = new MyGraphics(Color.BLACK);
    private Kite player;
    private int game;
    private Kite[] buttons = new Kite[4];
    private Color[] buttonColors = {Color.YELLOW, Color.MAGENTA, Color.CYAN, Color.RED};
    private int immuneTime = 35;
    private Needle xNeedle;
    private Needle yNeedle;
    private Cloud cloud1;
    private Cloud cloud2;
    private Cloud cloud3;
    private Color[][][] screen = new Color[2][screenPixelSize][screenPixelSize];
    private String[][] screenType = new String[screenPixelSize][screenPixelSize];
    private boolean isStart = true;
    private final int UPDATE_SPEED = 10; // milliseconds between screen updates
    private int cookieSegment;
    private int cookieClick;
    private float cookieClickPower;
    private float[] cookieAutoPower;
    private int cookieMaxClick;
    private int cookieMaxTop;
    private int cookieMaxBottom;

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

        if(screenSize.width > screenSize.height) setLocation((game%2)*(screenSize.width/2+windowWidth/2)+(screenSize.width-windowWidth-this.windowWidth*2)/4, ((3-game)/2)*(screenSize.height/2)+(screenSize.height-windowHeight)/4); // centres the window on the user's screen
        else if(screenSize.height > screenSize.width) setLocation((game%2)*(screenSize.width/2)+(screenSize.width-windowWidth)/4, ((3-game)/2)*(screenSize.height/2+windowHeight/2)+(screenSize.height-windowHeight-this.windowHeight*2)/4); // centres the window on the user's screen
        else setLocation((game%2)*(screenSize.width/2)+(screenSize.width-windowWidth)/4, ((3-game)/2)*(screenSize.height/2)+(screenSize.height-windowHeight)/4); // centres the window on the user's screen

        addMouseListener(this);
        addMouseMotionListener(this);

        this.pack();
        this.toFront();
        this.setVisible(true);

        if(game != 0) this.player = Main.player;
        else this.player = new Kite(screenPixelSize, Color.WHITE, 0, "player", immuneTime);
        switch(game) {
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

    public void actionPerformed(ActionEvent e) {

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
        mouseX = (e.getX() - xOffset)*screenPixelSize/windowWidth;
        mouseY = (e.getY() - yOffset)*screenPixelSize/windowWidth;
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
        for(int i = 0; i < screenPixelSize; i++) {
            for(int j = 0; j < screenPixelSize; j++) {
                screen[0][i][j] = null;
            }
        }
        System.out.println("cleared");
    }
    public void runGame(float updateXP) {
        clearScreen();
        while(this.player.isAlive()) {
            try{
                if(Main.isCookie && this.game != 0) Main.cookieMinigame.repaint();
                repaint();
                if(updateXP>0) Main.player.gainXP(updateXP, this.game, 255);
                Thread.sleep(UPDATE_SPEED);
            } catch(InterruptedException e) {
                System.out.println(e);
            }
        }
        if(this.game != 4) {
            Main.player.changeColor(new Color(255-((255-buttonColors[game].getRed())*Main.player.getXP(this.game))/255, 255-((255-buttonColors[game].getGreen())*Main.player.getXP(this.game))/255, 255-((255-buttonColors[game].getBlue())*Main.player.getXP(this.game))/255), this.game);
            System.out.println(Main.player.getXP(this.game));
        }
    }
    public void showButtons() {
        for(int i = 0; i < buttons.length; i++) {
            if(buttons[i].isAlive()) buttons[i].show(mg, screenPixelSize/4+(i%2)*screenPixelSize/2, screenPixelSize/4+((3-i)/2)*screenPixelSize/2);
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
        for(int i = 0; i < buttons.length; i++) buttons[i] = new Kite(screenPixelSize, buttonColors[i], 0, i+"", immuneTime*2);
    }
    public void kiteNeedle() {
        this.game = 2;
        xNeedle = new Needle(screenPixelSize, player.getHeight(), true);
        yNeedle = new Needle(screenPixelSize, player.getWidth(), false);

        runGame(0.03F);
    }
    public void kiteClassic() {
        this.game = 3;
        cloud1 = new Cloud(screenPixelSize);
        cloud2 = new Cloud(screenPixelSize);
        cloud3 = new Cloud(screenPixelSize);

        runGame(0.03F);
    }
    public void kiteSimon() {
        this.game = 1;
        for(int i = 0; i < buttons.length; i++) buttons[i] = new Kite(screenPixelSize, buttonColors[i], 0, i+"", immuneTime*2);
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

        for(int i = 0; i < screenPixelSize; i++) {
            for(int j = 0; j < screenPixelSize; j++) {
                this.screen[1][i][j] = Color.WHITE;
                this.screenType[i][j] = "background";
            }
        }

        switch(this.game) {
            case 0:
                this.player.makeImmune(1);

                if(this.player.getXP(this.cookieSegment)-255*(3-this.cookieClick) > 0 && this.player.getXP(this.cookieSegment)%255 == 0) {
                    if(this.cookieSegment > 0) {
                        this.cookieSegment--;
                    } else if(this.cookieClick > this.cookieMaxClick) {
                        this.cookieSegment = 3;
                        this.cookieClick--;
                    }
                }

                if((this.cookieClick >= this.cookieMaxTop && this.cookieSegment > 1) || (this.cookieClick >= this.cookieMaxBottom && this.cookieSegment < 2)) {
                    this.player.gainXP(this.cookieAutoPower[this.cookieSegment], this.cookieSegment, 255*(4-this.cookieClick));
                    cookieChangeColor();
                }

                if(this.cookieClick == 0 && this.cookieSegment == 0 && this.player.getXP(this.cookieSegment) == 255*buttons.length) {
                    Main.player.gainXP(255, this.game, 255);
                    this.player.kill();
                }

                showButtons();
                break;
            case 1:
                if(!Main.isWait) {
                    if (Main.simonCounter < Main.simonOrder.length) {
                        if (Main.simonOrder[Main.simonCounter] == 4) {
                            Main.simonTimer = 1 - Main.immuneTime;
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

        if(this.player != null) this.player.show(mg, mouseX, mouseY);

        for(int i = 0; i < screenPixelSize; i++) {
            for(int j = 0; j < screenPixelSize; j++) {
                if(this.screen[0][i][j] != this.screen[1][i][j] || isStart) {
                    g2.setColor(this.screen[1][i][j]);
                    g2.fillRect(i * windowWidth / screenPixelSize + xOffset, j * windowHeight / screenPixelSize + yOffset, windowWidth / screenPixelSize + 1, windowHeight / screenPixelSize + 1);
                    this.screen[0][i][j] = this.screen[1][i][j];
                }
            }
        }
        if(this.isStart) this.isStart = false;
    }
}
