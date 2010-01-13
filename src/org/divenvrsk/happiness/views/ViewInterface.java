package org.divenvrsk.happiness.views;

import javax.microedition.lcdui.Graphics;

public abstract class ViewInterface {

    public abstract void paint(Graphics g);

    public abstract int keypressed(int key);

    public abstract int pointerPressed(int x, int y);

    public abstract int pointerReleased(int x, int y);

    public abstract int pointerDragged(int x, int y);

    protected abstract String checkRegion(int x, int y);

    public abstract void regenTouchZones();
}
