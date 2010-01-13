package org.divenvrsk.happiness;

import org.divenvrsk.happiness.controllers.ViewController;

import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

public class Happiness extends MIDlet {

    public static Happiness instance;
    public Display display;

    protected void startApp() throws MIDletStateChangeException {
        if (instance == null) {
            instance = this;
            display = Display.getDisplay(this);
            new Thread() {
                public void run() {
                    ViewController.getInstance().start();
                }
            }.start();
        }
    }

    protected void pauseApp() {        
    }

    protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
    }

}
