package org.divenvrsk.happiness;

import java.io.IOException;
import java.io.InputStream;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import org.divenvrsk.happiness.controllers.ViewController;
import org.divenvrsk.happiness.views.Bubble;
import org.divenvrsk.happiness.views.MainMenu;

public class Settings {

    public static int SCREEN_WIDTH;
    public static int SCREEN_HEIGHT;
    public static int HALF_SCREEN_WIDTH;
    public static int HALF_SCREEN_HEIGHT;
    public static int BUBBLE_WIDTH = 38;
    public static int BUBBLE_HEIGHT = 37;
    public static int BUBBLE_X = 0;
    public static int BUBBLE_Y = 0;
    public static boolean done = false;
    public static boolean TOUCHSCREEN = false;
    public static Sprite bubble = null;
    public static Bubble[][] bubbles;
    public static Image finger = null;
    public static Font font = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);
    public final static int RIGHT_KEY = -4;
    public final static int LEFT_KEY = -3;
    public final static int UP_KEY = -1;
    public final static int DOWN_KEY = -2;
    public final static int RIGHT_SOFTKEY = -7;
    public final static int LEFT_SOFTKEY = -6;
    public final static int FIRE_KEY = -5;

    public static void init(Displayable displayable) {
        SCREEN_WIDTH = displayable.getWidth();
        SCREEN_HEIGHT = displayable.getHeight();
        HALF_SCREEN_WIDTH = SCREEN_WIDTH / 2;
        HALF_SCREEN_HEIGHT = SCREEN_HEIGHT / 2;
        try {
            Image tmp = Image.createImage("/bubble.png");
            bubble = new Sprite(tmp, BUBBLE_WIDTH, BUBBLE_HEIGHT);
            finger = Image.createImage("/finger.png");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        setBubbles();

        TOUCHSCREEN = ViewController.getInstance().hasPointerEvents();

        // fake interrupt
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ignored) {
        }
        done = true;
    }

    private static void setBubbles() {
        bubbles = new Bubble[Settings.SCREEN_WIDTH / Settings.BUBBLE_WIDTH][Settings.SCREEN_HEIGHT / Settings.BUBBLE_HEIGHT];
        for (int ycounter = 0; ycounter < (Settings.SCREEN_HEIGHT / Settings.BUBBLE_HEIGHT); ycounter++) {
            for (int xcounter = 0; xcounter < (Settings.SCREEN_WIDTH / Settings.BUBBLE_WIDTH); xcounter++) {
                bubbles[xcounter][ycounter] = new Bubble();
                bubbles[xcounter][ycounter].x = xcounter;
                bubbles[xcounter][ycounter].y = ycounter;
                if (ycounter == 0 && xcounter == 0) {
                    bubbles[xcounter][ycounter].active = true;
                }
            }
        }

        BUBBLE_Y = Settings.SCREEN_HEIGHT / Settings.BUBBLE_HEIGHT;
        BUBBLE_X = Settings.SCREEN_WIDTH / Settings.BUBBLE_WIDTH;
    }

    public static void initScreenSize(int w, int h) {
        SCREEN_WIDTH = w;
        SCREEN_HEIGHT = h;
        HALF_SCREEN_WIDTH = SCREEN_WIDTH / 2;
        HALF_SCREEN_HEIGHT = SCREEN_HEIGHT / 2;
        setBubbles();
    }
}
