package org.divenvrsk.happiness.views;

import javax.microedition.lcdui.Graphics;
import org.divenvrsk.happiness.Settings;

public class Bubble {

    public boolean burst = false;
    public boolean active = false;
    public int x = 0;
    public int y = 0;

    public void paint(Graphics g, int x, int y) {
        Settings.bubble.setFrame(burst ? 1 : 0);
        Settings.bubble.setRefPixelPosition(x, y);
        Settings.bubble.paint(g);
    }
}
