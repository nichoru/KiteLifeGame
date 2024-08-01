import java.awt.*; // lets me use colours

public class MyGraphics {

    public MyGraphics() { // constructor for MyGraphics
    }

    private boolean colorIn(int x, int y, Color color, String type) { // colours in a pixel on the screen, and returns whether there was a collision
        if(x >= 0 && x < Main.currentScreen[1].length && y >= 0 && y < Main.currentScreen[1][0].length) { // if the pixel is on the screen
            Main.currentScreen[1][x][y] = color; // change the colour of the pixel on the screen
            if(Main.currentScreenType[x][y].equals(type) || Main.currentScreenType[x][y].equals("background")) { // if there isn't a collision
                Main.currentScreenType[x][y] = type;
            } else { // if there is a (potential) collision
                if(Main.currentScreenType[x][y].equals("obstacle")) { // if it's with an obstacle, return that there was a collision
                    Main.currentScreenType[x][y] = type;
                    return true;
                } else if(Main.isInCookie && (Main.isMiniWin && Main.player.getXP(0) == 255)) { // if in the cookie minigame
                    if(Main.isInstructions && Main.currentGame == 0) { // instructions menu - if the player hits the start kite (and it's not immune), the start kite loses a life
                        if(!Main.startKite.isImmune()) Main.startKite.loseLife();
                        Main.currentScreenType[x][y] = type;
                        return false;
                    }
                    if(Main.cookieMinigame.getButton(Integer.parseInt(Main.currentScreenType[x][y])).isAlive() && Main.cookieMinigame.getPlayer().isAlive()){ // checks if the player can buy something that it hits, and if so, buys it
                        if(Main.cookieMinigame.getPlayer().getXP(Main.cookieMinigame.getButton(Integer.parseInt(Main.currentScreenType[x][y])).getLives() - 1) >= 255 * (4 - Integer.parseInt(Main.currentScreenType[x][y])) && !Main.cookieMinigame.getButton(Integer.parseInt(Main.currentScreenType[x][y])).isImmune()) {
                            Main.cookieMinigame.getButton(Integer.parseInt(Main.currentScreenType[x][y])).loseLife();
                            Main.cookieMinigame.cookiePay(255 * (4 - Main.cookieMinigame.getButton(Integer.parseInt(Main.currentScreenType[x][y])).getLives()), Main.cookieMinigame.getButton(Integer.parseInt(Main.currentScreenType[x][y])).getLives(), Integer.parseInt(Main.currentScreenType[x][y]));
                            Main.cookieMinigame.clearScreen();
                        }
                    }
                    Main.currentScreenType[x][y] = type;
                    return false;
                } else if(Main.isInstructions) { // if in an instruction screen, makes the start kite/touched game button lose a life
                    if(!Main.startKite.isImmune()) Main.startKite.loseLife();
                    if(Main.currentGame == 4 && !Main.isMiniWin) if(!Main.buttons[Integer.parseInt(Main.currentScreenType[x][y])].isImmune()) {
                        Main.buttons[Integer.parseInt(Main.currentScreenType[x][y])].loseLife();
                        Main.nextGame = Integer.parseInt(Main.currentScreenType[x][y]);
                    }
                    Main.currentScreenType[x][y] = type;
                    return false;
                } else if(Main.currentGame == 1) { // if currently playing the simon minigame, checks if you got the button right (if so, it advances, and you don't lose a life)
                    if(Main.simonCounter < Main.simonOrder.length) {
                        if (Main.simonOrder[Main.simonCounter] == Integer.parseInt(Main.currentScreenType[x][y])) {
                            if (!Main.currentMinigame.getButton(Main.simonOrder[Main.simonCounter]).isImmune()) {
                                Main.currentMinigame.getButton(Main.simonOrder[Main.simonCounter]).loseLife();
                                Main.simonCounter++;
                            }

                            Main.currentScreenType[x][y] = type;
                            return false;
                        }
                    }
                }
                if(Main.simonCounter > 0 && Main.simonCounter < Main.simonOrder.length) if(Main.currentMinigame.getButton(Main.simonOrder[Main.simonCounter-1]).isImmune() && Main.simonOrder[Main.simonCounter-1] == Integer.parseInt(Main.currentScreenType[x][y])) {
                    Main.currentScreenType[x][y] = type;
                    return false;
                }
                Main.currentScreenType[x][y] = type;
                return true; // otherwise, return that there was a collision
            }
        }
        return false; // if the pixel isn't on screen, return no collision
    }

    public void makeColoredCircle(int x, int y, int radius, Color f, String type) { // draws a circle on the current screen with the given parameters
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

    public boolean makeRATriangle(int x, int y, int w, int h, int xD, int yD, Color fill, Color outline, String type) { // draws a right-angled triangle on screen with the given parameters, and returns true if there's a collision
        boolean collision = false;

        if(h<w) { // if the height is less than the width, fill in the triangle based on the width, otherwise do it based on the height
            for (int i = 0; i < w; i++) {
                for(int j = 0; j < w-i; j++) if(colorIn(x+i*xD, y+yD*((j+1)*h/w), fill, type)) collision = true;
            }
        } else for(int i=0; i < h; i++) {
            for(int j = 0; j < h-i; j++) if(colorIn(x+xD*((j+1)*w/h),y+i*yD,fill, type)) collision = true;
        }

        // outline of the triangle
        if(makeLine(x, y, w, h, xD, yD, outline, type)) collision = true;
        if(makeLine(x, y, w, 0, xD, yD, outline, type)) collision = true;
        if(makeLine(x, y, 0, h, xD, yD, outline, type)) collision = true;

        return collision;
    }

    public boolean makeLine(int x, int y, int w, int h, int xD, int yD, Color c, String type) { // draws a line on screen with the given parameters, and returns true if there's a collision
        boolean collision = false;

        if(h<w) { // if height is greater than width, draw the line based on the width, otherwise do it based on the height
            for(int i=0; i < w; i++) {
                if(colorIn(x+i*xD, y-yD*(h+i*h/w), c, type)) collision = true;
            }
        } else for(int i=0; i < h; i++) {
            if(colorIn(x+xD*(w-i*w/h), y+i*yD, c, type)) collision = true;
        }

        return collision;
    }
}