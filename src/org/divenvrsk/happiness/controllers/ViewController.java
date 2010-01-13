package org.divenvrsk.happiness.controllers;

import org.divenvrsk.happiness.Settings;
import org.divenvrsk.happiness.Happiness;
import org.divenvrsk.happiness.views.MainMenu;
import org.divenvrsk.happiness.views.ViewInterface;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import java.util.Vector;

public class ViewController extends Canvas {

    private static ViewController instance;
    public static boolean RUN = true;
    public ViewInterface currentView;
    private static Vector previousStage = new Vector();
    public static final int STAGE_LOADING = 0;
    public static final int STAGE_MAINMENU = 1;
    public static int currentStage = STAGE_LOADING;
    private static boolean initOnce = false;

    public ViewController() {
        setFullScreenMode(true);
        Happiness.instance.display.setCurrent(this);
    }

    public static ViewController getInstance() {
        if (instance == null) {
            instance = new ViewController();
        }
        return instance;
    }

    public void setStage(int stage) {
        previousStage.addElement(new Integer(currentStage));
        currentStage = stage;
    }

    public void setPreviousStage() {
        if (previousStage.size() > 0) {
            int last = ((Integer) previousStage.lastElement()).intValue();
            currentStage = last;
            previousStage.removeElementAt(last);
        } else {
            setStage(STAGE_MAINMENU);
        }
    }

    public void start() {
        while (RUN) {
            switch (currentStage) {
                case STAGE_LOADING:
                    if (!initOnce && Happiness.instance.display.getCurrent() != null) {
                        initOnce = true;
                        new Thread() {

                            public void run() {
                                Settings.init(Happiness.instance.display.getCurrent());
                            }
                        }.start();
                    }
                    if (Settings.done) {
                        setStage(STAGE_MAINMENU);
                    }
                    break;
                case STAGE_MAINMENU:
                    currentView = MainMenu.getInstance();
                    break;
                default:
                    break;
            }
            repaint();
            try {
                Thread.sleep(1);
            } catch (InterruptedException ignored) {
            }
        }
        Happiness.instance.notifyDestroyed();
    }

    protected void paint(Graphics g) {
        g.setColor(0x000000);
        g.fillRect(0, 0, Settings.SCREEN_WIDTH, Settings.SCREEN_HEIGHT);
        g.setFont(Settings.font);
        if (currentStage == STAGE_LOADING) {
            g.setColor(0xFFFFFF);
            g.drawString("@divenvrsk", getWidth() / 2, getHeight() / 2, Graphics.HCENTER | Graphics.TOP);
        } else {
            if (currentView != null) {
                currentView.paint(g);
            }
        }
    }

    protected void keyPressed(int key) {
        if (currentView != null) {
            currentView.keypressed(key);
        }
    }

    protected void pointerPressed(int x, int y) {
        if (currentView != null) {
            currentView.pointerPressed(x, y);
        }
    }

    protected void pointerReleased(int x, int y) {
        if (currentView != null) {
            int pointerStatus = currentView.pointerReleased(x, y);
//            keyPressed(pointerStatus);
        }
    }

    protected void pointerDragged(int x, int y) {
        currentView.pointerDragged(x, y);
    }

    protected void sizeChanged(int w, int h) {
        Settings.initScreenSize(w, h);
        if (currentView != null) {
            currentView.regenTouchZones();
        }
    }
}
