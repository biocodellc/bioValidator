package edu.berkeley.biocode.utils;

import edu.berkeley.biocode.utils.scrollableEditorPane;

import javax.swing.text.BadLocationException;
import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: biocode
 * Date: Mar 1, 2010
 * Time: 4:30:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class scrollableEditorPaneDesktop extends scrollableEditorPane {

    public scrollableEditorPaneDesktop() {
        this.linkController = "desktop";
        String html = "";
        File file = new File("index.html");
        html = getContents(file);

        init();
        this.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);
        try {
            insertHTML(html, 0);
        } catch (FileNotFoundException e) {
           System.out.println("Default file not found ...");
            //e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (BadLocationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
