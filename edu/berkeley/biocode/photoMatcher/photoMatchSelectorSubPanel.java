package edu.berkeley.biocode.photoMatcher;

import edu.berkeley.biocode.validator.Rules;
import edu.berkeley.biocode.validator.bVWorksheet;

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
 * Date: Apr 28, 2011
 * Time: 1:52:05 PM
 * To change this template use File | Settings | File Templates.
 */


/**
 * Created by IntelliJ IDEA.
 * User: biocode
 * Date: Apr 22, 2011
 * Time: 4:01:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class photoMatchSelectorSubPanel extends JPanel {
    //public HashMap<String, JLabel> labelMap = new HashMap<String, JLabel>();
    public HashMap<String, JRadioButton> radioMap = new HashMap<String, JRadioButton>();
    specimenDisplayController sdc = null;
    public JComboBox cbColumns = new JComboBox();
    private bVWorksheet selectedWorksheet = null;

    public photoMatchSelectorSubPanel(specimenDisplayController sdc) {
        this.sdc = sdc;
        setAlignmentY(Component.TOP_ALIGNMENT);
        setAlignmentX(Component.LEFT_ALIGNMENT);

        // make a white background
        setBackground(new Color(255, 255, 255));
        JLabel jl = new JLabel("Waiting for spreadsheet to load ...");
        jl.setBackground(new Color(255, 255, 255));
        add(jl);
    }

    public bVWorksheet getSelectedWorksheet() {
        return selectedWorksheet;
    }

    private void setHeader() {
        // This is the header
        JLabel jlbW = new JLabel("Worksheet");
        jlbW.setFont(getFont(jlbW));
        jlbW.setAlignmentX(Component.LEFT_ALIGNMENT);
        jlbW.setAlignmentY(Component.TOP_ALIGNMENT);
        //JLabel jlbS = new JLabel("status");
        //jlbS.setFont(getFont(jlbS));
        add(jlbW);
        //add(jlbS);
    }

    private Font getFont(JLabel label) {
        return new Font(label.getFont().getName(), Font.BOLD, label.getFont().getSize());
    }

    public void init(ArrayList arrRules) {
        removeAll();
        ButtonGroup radioGroupResults = new ButtonGroup();

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        setHeader();

        int count = 1;
        Iterator<Rules> it = arrRules.iterator();
        while (it.hasNext()) {
            final Rules r = it.next();

            String worksheet = r.getWorksheet().getSheetName();

            // RadioButton Grouping
            JRadioButton jrb = new JRadioButton(worksheet);
            radioGroupResults.add(jrb);
            jrb.setName(worksheet);
            if (count == 1) {
                jrb.setSelected(true);
                selectedWorksheet = r.getWorksheet();
                addFields(r.getWorksheet().getColNames());

            }
            jrb.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    sdc.tinyspreadsheet.init();
                    selectedWorksheet = r.getWorksheet();
                    addFields(r.getWorksheet().getColNames());
                    updateUI();
                }
            });

            // Set Label name
            JLabel jlb = new JLabel("processing");
            jlb.setName("dynamicLabel" + worksheet);

            // Populate maps
            radioMap.put(worksheet, jrb);

            // Add to the panel
            add(jrb);

            count++;
        }


        // add the combobox
        cbColumns.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    sdc.browseSpreadsheet(selectedWorksheet, cbColumns.getSelectedItem().toString());
                } catch (Exception e1) {

                }
            }
        });
        cbColumns.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(cbColumns);

        updateUI();
    }

    /**
     * Add list of items to comboBox
     *
     * @param list
     */
    public void addFields(java.util.List<String> list) {
        cbColumns.removeAllItems();

        Iterator<String> it = list.iterator();

        while (it.hasNext()) {
            String value = it.next();
            cbColumns.addItem(value);
        }
        cbColumns.setSelectedIndex(0);

        // TODO: set a "default" photomatch field in XML configuration file
        // check if specimen_num_collector is in list and if so then select it.  This is a slight hack
        // to bias output towards Biocode
        Iterator<String> it2 = list.iterator();
        while (it2.hasNext()) {
            String value = it2.next();
            if (value.equalsIgnoreCase("specimen_num_collector")) {
                cbColumns.setSelectedItem(value);
            }
        }

        updateUI();
    }
}