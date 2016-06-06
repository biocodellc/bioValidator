package edu.berkeley.biocode.utils;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: biocode
 * Date: Feb 25, 2010
 * Time: 4:39:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class scrollableEditorPane extends JScrollPane {

    public JEditorPane browser = null;
    public static AttributeSet currentAnchor;
    protected static BareBonesBrowserLaunch desktop;
    public String linkController = "javabrowser";

    public scrollableEditorPane() {
        init();

        browser.setSize(new Dimension(800,900));
        this.setSize(new Dimension(800,1200));
        this.addComponentListener(new ComponentAdapter() {
                        @Override
                        public void componentResized(ComponentEvent e) {
                                browser.setSize(new Dimension(
                                       browser.getWidth()-20,
                                       browser.getHeight()-20));
                        }
                }); 

        this.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_ALWAYS);
        this.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_ALWAYS);
    }

    protected void init() {
        browser = new JEditorPane();
        browser.setEditable(false);
        browser.setEditorKit(new HTMLEditorKit());
        this.getViewport().add(browser);
        LinkController linkController = new LinkController();
        browser.addMouseListener(linkController);
    }

    public void insertHTML(String html, Integer location) throws IOException, BadLocationException {
        //assumes editor is already set to "text/html" type
        init();
        HTMLEditorKit kit = (HTMLEditorKit) browser.getEditorKit();
        Document doc = browser.getDocument();
        //doc.createPosition(0); // attempting to set position at top
        StringReader reader = new StringReader(html);
        kit.read(reader, doc, location);       
    }

    static public String getContents(File aFile) {
        //...checks on aFile are elided
        StringBuilder contents = new StringBuilder();

        try {
            //use buffering, reading one line at a time
            //FileReader always assumes default encoding is OK!
            BufferedReader input = new BufferedReader(new FileReader(aFile));
            try {
                String line = null; //not declared within while loop
                /*
                * readLine is a bit quirky :
                * it returns the content of a line MINUS the newline.
                * it returns null only for the END of the stream.
                * it returns an empty String if two newlines appear in a row.
                */
                while ((line = input.readLine()) != null) {
                    contents.append(line);
                    contents.append(System.getProperty("line.separator"));
                }
            }
            finally {
                input.close();
            }
        }
        catch (FileNotFoundException ex) {
            System.out.println("can't find that file .." + aFile.getAbsolutePath());
        }

        catch (IOException ex) {
            ex.printStackTrace();
        }

        return contents.toString();
    }

    static public String getURLContents(URL aURL) throws IOException {
        String contents = "";
        String inputLine;

        BufferedReader in = new BufferedReader(new InputStreamReader(aURL.openStream()));

        while ((inputLine = in.readLine()) != null)
            contents += inputLine;

        in.close();

        return contents;
    }

    // the new patched link controller
    public class LinkController extends HTMLEditorKit.LinkController implements Serializable {
        public void mouseClicked(MouseEvent me) {
            JEditorPane jep = (JEditorPane) me.getSource();
            Document doc = jep.getDocument();

            if (doc instanceof HTMLDocument) {
                HTMLDocument hdoc = (HTMLDocument) doc;
                int pos = jep.viewToModel(me.getPoint());
                Element e = hdoc.getCharacterElement(pos);
                AttributeSet a = e.getAttributes();
                // all that work gets us the attribute set associated with
                // the current <a> tag, which we now store:
                currentAnchor = (AttributeSet) a.getAttribute(HTML.Tag.A);
                if (currentAnchor != null) {
                    try {
                        String target = "";
                        try {
                            target = currentAnchor.getAttribute(HTML.Attribute.TARGET).toString().replaceAll("target=", "").trim();
                        } catch (NullPointerException e2) {
                            target = "";
                        }
                        URL url = null;
                        String strURL = currentAnchor.getAttribute(HTML.Attribute.HREF).toString().replaceAll("href=", "").trim();
                        try {
                            url = new URL(strURL);
                        } catch (MalformedURLException e2) {
                            // Often times, references omit hostname-- append it here and try again
                            url = new URL(System.getProperty("app.uploadBaseURL") + strURL);
                        }
                        if (target.equals("javabrowser")) {
                            insertHTML(getURLContents(url), 0);
                        } else if (target.equals("popup")) {
                            System.out.println(strURL);

                            new PopupList(strURL);
                        } else {
                            desktop.openURL(url.toString());
                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    } catch (BadLocationException e1) {
                        e1.printStackTrace();
                    }
                }

                super.mouseClicked(me);
            }
        }

    }
}
