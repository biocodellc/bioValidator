package edu.berkeley.biocode.bioValidator;

import javax.swing.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

/**
 * Created by IntelliJ IDEA.
 * User: jdeck
 * Date: 3/6/12
 * Time: 6:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class myFrame extends JFrame implements ComponentListener {

    myFrame(String a) {
        super(a);
        System.out.println("hello" + a);

    }

    public void componentResized(ComponentEvent componentEvent) {
        System.out.println("frame resized!");
    }

    public void componentMoved(ComponentEvent componentEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void componentShown(ComponentEvent componentEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void componentHidden(ComponentEvent componentEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}

