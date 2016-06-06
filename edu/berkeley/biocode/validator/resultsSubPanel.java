package edu.berkeley.biocode.validator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: biocode
 * Date: Apr 22, 2011
 * Time: 4:01:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class resultsSubPanel extends JPanel {
    public HashMap<String, JLabel> labelMap = new HashMap<String, JLabel>();
    public HashMap<String, JRadioButton> radioMap = new HashMap<String, JRadioButton>();

    public resultsSubPanel() {

        setAlignmentY(Component.TOP_ALIGNMENT);
        setAlignmentX(Component.LEFT_ALIGNMENT);

        // make a white background
        setBackground(new Color(255, 255, 255));

        JLabel jl = new JLabel("Waiting for spreadsheet to load ...");
        jl.setBackground(new Color(255,255,255));
        add(jl);

    }

    private void setHeader() {
        // This is the header
        JLabel jlbW = new JLabel("Worksheet");
        jlbW.setFont(getFont(jlbW));
        jlbW.setAlignmentX(Component.LEFT_ALIGNMENT);
        jlbW.setAlignmentY(Component.TOP_ALIGNMENT);
        JLabel jlbS = new JLabel("Status");
        jlbS.setFont(getFont(jlbS));
        add(jlbW);
        add(jlbS);
    }

    private Font getFont(JLabel label) {
        return new Font(label.getFont().getName(), Font.BOLD, label.getFont().getSize());
    }

    public void init(ArrayList arrWorksheets) {
        removeAll();
        ButtonGroup radioGroupResults = new ButtonGroup();

        setLayout(new GridLayout(arrWorksheets.size() + 1, 2));

        setHeader();

        int count = 1;
        Iterator<String> it = arrWorksheets.iterator();
        while (it.hasNext()) {
            String worksheet = it.next();

            // RadioButton Grouping
            JRadioButton jrb = new JRadioButton(worksheet);
            radioGroupResults.add(jrb);
            jrb.setName(worksheet);
            if (count == 1) {
                jrb.setSelected(true);

            }

            // Set Label name
            JLabel jlb = new JLabel("processing");
            jlb.setName("dynamicLabel" + worksheet);

            // Populate maps
            labelMap.put(worksheet, jlb);
            radioMap.put(worksheet, jrb);

            // Add to the panel
            add(jrb);
            add(jlb);

            count++;
        }

        updateUI();
    }


}
