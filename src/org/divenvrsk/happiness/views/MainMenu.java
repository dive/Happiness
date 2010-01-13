package org.divenvrsk.happiness.views;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import org.divenvrsk.happiness.Settings;

import javax.microedition.lcdui.Graphics;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import org.divenvrsk.happiness.Happiness;

public class MainMenu extends ViewInterface {

    private static MainMenu instance;
    public static Bubble currentBubble = null;
    private static boolean wasDragged = false;
    private static final Hashtable touchZones = new Hashtable();
    private static final String TOUCH_EXIT = "exit";
    private static final String TOUCH_CLEAR = "clear";
    private static final String TOUCH_BUBBLES = "bubbles";
    public static final int DO_NOTHING = 999;
    public static final int POINTER_PROCESSED = 0;
    public static final int POINTER_MISS = 1;
    public static Player p = null;

    private MainMenu() {
        touchZones.put(TOUCH_CLEAR, new int[]{0, Settings.SCREEN_HEIGHT - (Settings.font.getHeight() * 2),
                    Settings.HALF_SCREEN_WIDTH, Settings.SCREEN_HEIGHT});
        touchZones.put(TOUCH_EXIT, new int[]{Settings.HALF_SCREEN_WIDTH, Settings.SCREEN_HEIGHT - (Settings.font.getHeight() * 2),
                    Settings.SCREEN_WIDTH, Settings.SCREEN_HEIGHT});
        touchZones.put(TOUCH_BUBBLES, new int[]{0, 0, Settings.SCREEN_WIDTH, Settings.SCREEN_HEIGHT - (Settings.font.getHeight() * 2)});

        InputStream is = Happiness.instance.getClass().getResourceAsStream("/burst.mp3");
        try {
            p = Manager.createPlayer(is, "audio/mpeg");
            p.prefetch();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (MediaException ex) {
            ex.printStackTrace();
        }
    }

    public static ViewInterface getInstance() {
        if (instance == null) {
            instance = new MainMenu();
        }
        return instance;
    }

    public int keypressed(int key) {
        switch (key) {
            case 54:
            case Settings.RIGHT_KEY:
                if (MainMenu.currentBubble.x < Settings.BUBBLE_X - 1) {
                    Settings.bubbles[MainMenu.currentBubble.x + 1][MainMenu.currentBubble.y].active = true;
                } else {
                    Settings.bubbles[0][MainMenu.currentBubble.y].active = true;
                }
                MainMenu.currentBubble.active = false;
                return MainMenu.POINTER_PROCESSED;
            case 52:
            case Settings.LEFT_KEY:
                if (MainMenu.currentBubble.x == 0) {
                    Settings.bubbles[Settings.BUBBLE_X - 1][MainMenu.currentBubble.y].active = true;
                } else {
                    Settings.bubbles[MainMenu.currentBubble.x - 1][MainMenu.currentBubble.y].active = true;
                }
                MainMenu.currentBubble.active = false;
                return MainMenu.POINTER_PROCESSED;
            case 50:
            case Settings.UP_KEY:
                if (MainMenu.currentBubble.y == 0) {
                    Settings.bubbles[MainMenu.currentBubble.x][Settings.BUBBLE_Y - 1].active = true;
                } else {
                    Settings.bubbles[MainMenu.currentBubble.x][MainMenu.currentBubble.y - 1].active = true;
                }
                MainMenu.currentBubble.active = false;
                return MainMenu.POINTER_PROCESSED;
            case 56:
            case Settings.DOWN_KEY:
                if (MainMenu.currentBubble.y < Settings.BUBBLE_Y - 1) {
                    Settings.bubbles[MainMenu.currentBubble.x][MainMenu.currentBubble.y + 1].active = true;
                } else {
                    Settings.bubbles[MainMenu.currentBubble.x][0].active = true;
                }
                MainMenu.currentBubble.active = false;
                return MainMenu.POINTER_PROCESSED;
            case 53:
            case Settings.FIRE_KEY:
                if (!MainMenu.currentBubble.burst) {
                    new Thread() {

                        public void run() {
                            try {
                                if (p != null && p.getState() != Player.CLOSED) {
                                    p.stop();
                                }
                                p.start();
                            } catch (MediaException ignored) {
                                ignored.printStackTrace();
                            }
                        }
                    }.start();
                }
                MainMenu.currentBubble.burst = true;
                return MainMenu.POINTER_PROCESSED;
            case 42:
            case Settings.LEFT_SOFTKEY:
                for (int ycounter = 0; ycounter < (Settings.BUBBLE_Y); ycounter++) {
                    for (int xcounter = 0; xcounter < (Settings.BUBBLE_X); xcounter++) {
                        Settings.bubbles[xcounter][ycounter].burst = false;
                    }
                }
                return MainMenu.POINTER_PROCESSED;
            case 35:
            case Settings.RIGHT_SOFTKEY:
                Happiness.instance.notifyDestroyed();
                return MainMenu.POINTER_PROCESSED;
        }
        return MainMenu.POINTER_MISS;
    }

    public void paint(Graphics g) {
        g.setColor(0x8CB1CE);
        g.fillRect(0, 0, Settings.SCREEN_WIDTH, Settings.SCREEN_HEIGHT);
        g.setColor(0x000000);
        g.drawString("clear (*)", 5, Settings.SCREEN_HEIGHT, Graphics.LEFT | Graphics.BOTTOM);
        g.drawString("exit (#)", Settings.SCREEN_WIDTH - 5, Settings.SCREEN_HEIGHT, Graphics.RIGHT | Graphics.BOTTOM);
        for (int ycounter = 0; ycounter < (Settings.BUBBLE_Y); ycounter++) {
            for (int xcounter = 0; xcounter < (Settings.BUBBLE_X); xcounter++) {
                Settings.bubbles[xcounter][ycounter].paint(g,
                        ((Settings.SCREEN_WIDTH - ((Settings.BUBBLE_X) * Settings.BUBBLE_WIDTH)) / 2) + Settings.BUBBLE_WIDTH * xcounter,
                        Settings.BUBBLE_HEIGHT * ycounter);
                if (Settings.bubbles[xcounter][ycounter].active) {
                    currentBubble = Settings.bubbles[xcounter][ycounter];
                }
            }
        }
        if (!Settings.TOUCHSCREEN) {
            for (int ycounter = 0; ycounter < (Settings.BUBBLE_Y); ycounter++) {
                for (int xcounter = 0; xcounter < (Settings.BUBBLE_X); xcounter++) {
                    if (Settings.bubbles[xcounter][ycounter].active) {
                        g.drawImage(Settings.finger, ((Settings.SCREEN_WIDTH - ((Settings.SCREEN_WIDTH / Settings.BUBBLE_WIDTH) * Settings.BUBBLE_WIDTH)) / 2) + Settings.BUBBLE_WIDTH * xcounter + (Settings.BUBBLE_WIDTH / 4),
                                Settings.BUBBLE_HEIGHT * ycounter + (Settings.BUBBLE_HEIGHT / 2),
                                Graphics.LEFT | Graphics.TOP);
                        break;
                    }
                }
            }
        }
    }

    protected String checkRegion(int x, int y) {
        if (touchZones.size() == 0) {
            return "NONE";
        }
        int[] zoneRect;
        String zone;
        for (Enumeration en = touchZones.keys(); en.hasMoreElements();) {
            zone = (String) en.nextElement();
            zoneRect = (int[]) touchZones.get(zone);
            if (x >= zoneRect[0] && x <= zoneRect[2] && y >= zoneRect[1] && y <= zoneRect[3]) {
//                System.out.println("pointerReleased at: " + zone);
                return zone;
            }
        }
        return "NONE";
    }

    public int pointerPressed(int x, int y) {
        String region = checkRegion(x, y);
        if (touchZones.get(region) == null) {
            return MainMenu.POINTER_MISS;
        }
        return MainMenu.POINTER_PROCESSED;
    }

    public int pointerReleased(int x, int y) {
        if (wasDragged) {
            wasDragged = false;
            return MainMenu.DO_NOTHING;
        }
        String region = checkRegion(x, y);
        if (touchZones.get(region) == null) {
            return MainMenu.POINTER_MISS;
        }
        if (region.equals(TOUCH_CLEAR)) {
            keypressed(Settings.LEFT_SOFTKEY);
            return MainMenu.POINTER_PROCESSED;
        } else if (region.equals(TOUCH_EXIT)) {
            keypressed(Settings.RIGHT_SOFTKEY);
            return MainMenu.POINTER_PROCESSED;
        } else if (region.equals(TOUCH_BUBBLES)) {
            for (int ycounter = 0; ycounter < (Settings.BUBBLE_Y); ycounter++) {
                for (int xcounter = 0; xcounter < (Settings.BUBBLE_X); xcounter++) {
                    if (x > ((Settings.SCREEN_WIDTH - ((Settings.BUBBLE_X) * Settings.BUBBLE_WIDTH)) / 2) + Settings.BUBBLE_WIDTH * xcounter
                            && x < (((Settings.SCREEN_WIDTH - ((Settings.BUBBLE_X) * Settings.BUBBLE_WIDTH)) / 2) + Settings.BUBBLE_WIDTH * xcounter) + Settings.BUBBLE_WIDTH
                            && y > Settings.BUBBLE_HEIGHT * ycounter
                            && y < (Settings.BUBBLE_HEIGHT * ycounter + Settings.BUBBLE_HEIGHT)) {
                        currentBubble.active = false;
                        Settings.bubbles[xcounter][ycounter].active = true;
                        currentBubble = Settings.bubbles[xcounter][ycounter];
                        keypressed(Settings.FIRE_KEY);
                        return MainMenu.POINTER_PROCESSED;
                    }
                }
            }
        }
        return MainMenu.POINTER_MISS;
    }

    public int pointerDragged(int x, int y) {
        return MainMenu.POINTER_MISS;
    }

    public void regenTouchZones() {
        touchZones.put(TOUCH_CLEAR, new int[]{0, Settings.SCREEN_HEIGHT - (Settings.font.getHeight() * 2),
                    Settings.HALF_SCREEN_WIDTH, Settings.SCREEN_HEIGHT});
        touchZones.put(TOUCH_EXIT, new int[]{Settings.HALF_SCREEN_WIDTH, Settings.SCREEN_HEIGHT - (Settings.font.getHeight() * 2),
                    Settings.SCREEN_WIDTH, Settings.SCREEN_HEIGHT});
        touchZones.put(TOUCH_BUBBLES, new int[]{0, 0, Settings.SCREEN_WIDTH, Settings.SCREEN_HEIGHT - (Settings.font.getHeight() * 2)});
    }
}
