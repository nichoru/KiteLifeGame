import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class MyGraphics {
    private Color outlineColor;

    public MyGraphics(Color oc) {
        this.outlineColor = oc;
    }

    private boolean colorIn(int x, int y, Color color, String type) {
        if(x >= 0 && x < Main.currentScreen[1].length && y >= 0 && y < Main.currentScreen[1][0].length) {
            Main.currentScreen[1][x][y] = color;
            if(Main.currentScreenType[x][y].equals(type) || Main.currentScreenType[x][y].equals("background")) {
                Main.currentScreenType[x][y] = type;
            } else {
                if(Main.currentScreenType[x][y].equals("obstacle")) {
                    Main.currentScreenType[x][y] = type;
                    return true;
                } else if(Main.isInCookie) {
                    if(Main.isInstructions && Main.currentGame == 0) {
                        if(!Main.startKite.isImmune()) Main.startKite.loseLife();
                        Main.currentScreenType[x][y] = type;
                        return false;
                    }
                    if(Main.cookieMinigame.getButton(Integer.parseInt(Main.cookieMinigame.getScreenType(x, y))).isAlive() && Main.cookieMinigame.getPlayer().isAlive()){
                        if(Main.cookieMinigame.getPlayer().getXP(Main.cookieMinigame.getButton(Integer.parseInt(Main.cookieMinigame.getScreenType(x, y))).getLives() - 1) >= 255 * (4 - Integer.parseInt(Main.cookieMinigame.getScreenType(x, y))) && !Main.cookieMinigame.getButton(Integer.parseInt(Main.cookieMinigame.getScreenType(x, y))).isImmune()) {
                            Main.cookieMinigame.getButton(Integer.parseInt(Main.cookieMinigame.getScreenType(x, y))).loseLife();
                            Main.cookieMinigame.cookiePay(255 * (4 - Main.cookieMinigame.getButton(Integer.parseInt(Main.cookieMinigame.getScreenType(x, y))).getLives()), Main.cookieMinigame.getButton(Integer.parseInt(Main.cookieMinigame.getScreenType(x, y))).getLives(), Integer.parseInt(Main.cookieMinigame.getScreenType(x, y)));
                            Main.cookieMinigame.clearScreen();
                        }
                    }
                    Main.cookieMinigame.setScreenType(x, y, type);
                    return false;
                } else if(Main.isInstructions) {
                    if(!Main.startKite.isImmune()) Main.startKite.loseLife();
                    if(Main.currentGame == 4) if(!Main.buttons[Integer.parseInt(Main.screenType[x][y])].isImmune()) {
                        Main.buttons[Integer.parseInt(Main.screenType[x][y])].loseLife();
                        Main.nextGame = Integer.parseInt(Main.screenType[x][y]);
                    }
                    Main.currentScreenType[x][y] = type;
                    return false;
                } else if(!Main.isWait && Main.currentGame == 1) {
                    if(Main.simonCounter < Main.simonOrder.length) {
                        if (Main.simonOrder[Main.simonCounter] == Integer.parseInt(Main.currentMinigame.getScreenType(x, y))) {
                            if (!Main.currentMinigame.getButton(Main.simonOrder[Main.simonCounter]).isImmune()) {
                                Main.currentMinigame.getButton(Main.simonOrder[Main.simonCounter]).loseLife();
                                Main.simonCounter++;
                            }

                            Main.currentMinigame.setScreenType(x, y, type);
                            return false;
                        }
                    }
                }
                if(Main.simonCounter > 0 && Main.simonCounter < Main.simonOrder.length) if(Main.currentMinigame.getButton(Main.simonOrder[Main.simonCounter-1]).isImmune() && Main.simonOrder[Main.simonCounter-1] == Integer.parseInt(Main.currentMinigame.getScreenType(x, y))) {
                    Main.currentMinigame.setScreenType(x, y, type);
                    return false;
                }
                Main.currentScreenType[x][y] = type;
                return true;
            }
        }
        return false;
    }

    public void makeColoredCircle(int x, int y, int radius, Color f, String type) {
        for(int i=0; i < radius; i++) {
            for(int j=0; j < radius; j++) {
                if(i*i+j*j < radius*radius) {
                    colorIn(x+i, y+j, f, type);
                    colorIn(x-i, y+j, f, type);
                    colorIn(x+i, y-j, f, type);
                    colorIn(x-i, y-j, f, type);
                }
            }
        }
    }

    public boolean makeRATriangle(int x, int y, int w, int h, int xD, int yD, Color fill, Color outline, String type) {
        boolean collision = false;

        if(h<w) {
            for (int i = 0; i < w; i++) {
                for(int j = 0; j < w-i; j++) if(colorIn(x+i*xD, y+yD*((j+1)*h/w), fill, type)) collision = true;
            }
        } else for(int i=0; i < h; i++) {
            for(int j = 0; j < h-i; j++) if(colorIn(x+xD*((j+1)*w/h),y+i*yD,fill, type)) collision = true;
        }

        if(makeLine(x, y, w, h, xD, yD, outline, type)) collision = true;
        if(makeLine(x, y, w, 0, xD, yD, outline, type)) collision = true;
        if(makeLine(x, y, 0, h, xD, yD, outline, type)) collision = true;

        return collision;
    }

    public boolean makeLine(int x, int y, int w, int h, int xD, int yD, Color c, String type) {
        boolean collision = false;

        if(h<w) {
            for(int i=0; i < w; i++) {
                if(colorIn(x+i*xD, y-yD*(h+i*h/w), c, type)) collision = true;
            }
        } else for(int i=0; i < h; i++) {
            if(colorIn(x+xD*(w-i*w/h), y+i*yD, c, type)) collision = true;
        }

        return collision;
    }

//    public void drawFile(String fileName, int x, int y, String type, boolean isOutlineColor) {
//        File myFile = new File(fileName);
//        try {
//            Scanner fileReader = new Scanner(myFile);
//            for(int i=0; i < fileReader.nextInt(); i++) {
//                if(isOutlineColor) colorIn(x+fileReader.nextInt(), y+fileReader.nextInt(), this.outlineColor, type);
//                else colorIn(x+fileReader.nextInt(), y+fileReader.nextInt(), new Color(fileReader.nextInt(), fileReader.nextInt(), fileReader.nextInt()), type);
//            }
//        } catch(IOException e) {
//            System.out.println("File not found");
//        }
//    }
}