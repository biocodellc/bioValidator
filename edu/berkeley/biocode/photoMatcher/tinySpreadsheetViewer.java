package edu.berkeley.biocode.photoMatcher;

import edu.berkeley.biocode.bioValidator.mainPage;
import edu.berkeley.biocode.validator.Constants;
import edu.berkeley.biocode.validator.bVWorksheet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;


/**
 * panelSpecimenDisplay is meant to be added as a component to the UI Designer.
 * It dynamically adds specimen information from either the DB or the spreadsheet
 */
public class tinySpreadsheetViewer extends JPanel {

    public ArrayList arrayFieldNames = new ArrayList();
    private String specimen_num_collector = "Unassigned";

    private bVWorksheet bvworksheet = null;
    private int type = Constants.SPREADSHEET;

    /**
     *
     */
    public tinySpreadsheetViewer() {
        removeAll();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setAlignmentX(Component.LEFT_ALIGNMENT);
        setAlignmentY(Component.LEFT_ALIGNMENT);
    }

    public void init() {
         arrayFieldNames = new ArrayList();
    }

    public void update(String cellValue) {
        update(bvworksheet, cellValue);
    }

    /**
     * Update specimen information from the spreadsheet
     *
     * @param cellValue
     */
    public void update(bVWorksheet ws, String cellValue) {
        this.removeAll();
        this.specimen_num_collector = cellValue;

        bvworksheet = ws;
        type = Constants.SPREADSHEET;
        //System.out.println("arrayFieldNames " + arrayFieldNames);
        Map map = null;

        // Get the name of the field that is currently selected
        String selectedFieldName = "";
        try {
            try {
                selectedFieldName = mainPage.bv.mainpage.matcherSpecimenDisplayController.pmsSubPanel.cbColumns.getSelectedItem().toString();
            }   catch (NullPointerException e) {
                // if a field has not been selected yet then just set it to the first column in the sheet
                selectedFieldName = ws.getColNames().get(0);
            }
            map = ws.getColumnMap(selectedFieldName, cellValue, arrayFieldNames);
        } catch (Exception e) {
            System.err.println("minor exception in tinySpreadsheetViewer.update() = " + e.getMessage());
            //e.printStackTrace();
            //JOptionPane.showMessageDialog(null, "Exception Encountered ... " + e.getMessage());

        }

        if (map.size() <= 0) {
            this.add(new JLabel("No results from spreadsheet"));
        } else {
            Iterator it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry) it.next();
                String column = (String) pairs.getKey();
                String value = (String) pairs.getValue();
                // Create label for column information
                JLabel label = new JLabel(column + ": " + value);
                if (column.equals(selectedFieldName)) {
                    label.setFont(label.getFont().deriveFont(Font.BOLD));
                }

                // Create pane to hold symbols and row information
                JPanel pane = new JPanel();
                pane.setLayout(new BoxLayout(pane, BoxLayout.X_AXIS));
                pane.setAlignmentX(LEFT_ALIGNMENT);

                if (type == Constants.SPREADSHEET) {
                    // Minus label to take away this column
                    JLabel minusLabel = new JLabel(new ImageIcon("images/minus15.gif"));
                    minusLabel.setToolTipText(column);
                    minusLabel.addMouseListener(new minusListener());

                    // Populate pane
                    pane.add(minusLabel);
                }
                pane.add(label);

                this.add(pane);
            }

            JLabel addLabel = null;
            if (type == Constants.SPREADSHEET) {
                addLabel = new JLabel(new ImageIcon("images/plus15.gif"));
                addLabel.setToolTipText("Add a field to view");
                addLabel.addMouseListener(new addListener(ws.getColNames().toArray()));
                this.add(addLabel);

            } else {
                //addLabel.addMouseListener(new addListener(getDBColumns()));
            }

            this.updateUI();
        }
    }


    public class addListener extends MouseAdapter {
        Object[] optionList;

        public addListener(Object[] pOptionList) {
            optionList = pOptionList;
        }

        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() >= 1) {

                String s = (String) JOptionPane.showInputDialog(
                        null,
                        "Choose a field to add",
                        "Field Chooser Dialog",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        optionList,
                        "specimen_num_collector");

                if ((s != null) && (s.length() > 0)) {
                    addField((String)s);
                   // arrayFieldNames.add((String) s);                    
                    update(specimen_num_collector);
                }
            }
        }
    }

    public class minusListener extends MouseAdapter { //() {

        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() >= 1) {
                // don't want to remove if there is only 1 element left
                if (arrayFieldNames.size() > 1) {
                    JLabel clickedLabel = (JLabel) e.getComponent();
                    arrayFieldNames.remove(clickedLabel.getToolTipText());
                    update(specimen_num_collector);
                }
            }
        }
    }
    public void addField(String field) {
        boolean found = false;
        Iterator it = arrayFieldNames.iterator();
        while (it.hasNext()) {
            if (it.next().toString().equalsIgnoreCase(field)) {
                found = true;
            }
        }
        if (!found) {
            arrayFieldNames.add(field);
        }
    }
}
