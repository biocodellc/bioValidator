package edu.berkeley.biocode.utils;

import edu.berkeley.biocode.bioValidator.mainPage;
import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: biocode
 * Date: Apr 23, 2010
 * Time: 5:14:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class PopupList extends JButton {
    public PopupList(String text) {
        text = text.replaceAll(", ", ",");
        text = text.replaceAll(",", "\n");
        text = text.replaceAll("\\[", "");
        text = text.replaceAll("\\]", "");

        final String ftext = text;

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        textArea.setRows(20);

        textArea.setText(ftext);
        textArea.setCaretPosition(0); // set scrollbar at top

        JScrollPane s = new JScrollPane(textArea);
        s.setAutoscrolls(true);

        final JFrame frame = new JFrame("Acceptable Values");
        frame.setSize(new Dimension(300,200));
        frame.add(s);
        frame.setVisible(true);
    }
}
