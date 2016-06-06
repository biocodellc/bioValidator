package edu.berkeley.biocode.photoMatcher;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import edu.berkeley.biocode.bioValidator.mainPage;
import edu.berkeley.biocode.utils.CopyFile;
import edu.berkeley.biocode.utils.directoryChooser;
import edu.berkeley.biocode.validator.Constants;
import edu.berkeley.biocode.validator.Rules;
import edu.berkeley.biocode.validator.bVWorksheet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.prefs.Preferences;


/**
 * Created by IntelliJ IDEA.
 * User: biocode
 * Date: Jan 28, 2010
 * Time: 3:32:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class specimenDisplayController extends JPanel {
    private JPanel specimenDisplayController;
    private JLabel photonameLabel;
    private JLabel specimensLabel;

    private ArrayList listPhotoField = null;
    private String photoField;

    private int currentRow = 0;
    public Integer numRows;
    private JButton prev, next, rename = null;
    public tinySpreadsheetViewer tinyspreadsheet;
    private JTextPane renamePhotosTextPane;
    private JButton outputDirectoryButton;
    private JLabel directoryNameLabel;
    private JPanel resultsPanel;
    private panelImageBrowser specimenImages;
    private panelImageBrowser specimenThumbBrowser = null;
    private mainPage mainpage;
    public photoMatchSelectorSubPanel pmsSubPanel;
    private bVWorksheet activeSheet = null;

    public String outputDirectory = null;


    public specimenDisplayController() {
        final Preferences prefs = Preferences.userRoot().node(System.getProperty("app.bioValidatorUserPreferences"));

        pmsSubPanel = new photoMatchSelectorSubPanel(this);

        resultsPanel.add(
                pmsSubPanel,
                new GridConstraints());

        prev.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                prevRow();
            }
        });

        next.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                nextRow();
            }
        });

        rename.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                rename();
            }
        });

        // only set visible when run initRulesSchema
        rename.setEnabled(false);

        outputDirectoryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {

                Iterator it = mainpage.bv.bVRules.iterator();

                while (it.hasNext()) {
                    Rules lr = (Rules) it.next();
                    String sheetName = lr.getWorksheet().getSheetName();

                    HashMap<String, JRadioButton> map = pmsSubPanel.radioMap;
                    JRadioButton jrb = map.get(sheetName);

                    if (jrb.isSelected()) {

                        System.out.println("sheetName" + sheetName + " is selected");
                        if (lr.getWorksheet() == null) {
                            JOptionPane.showMessageDialog(null, "Must first select a Specimen spreadsheet");
                            // Spreadsheet not validated
                        } else if (lr.hasErrors()) {
                            JOptionPane.showMessageDialog(null, "Must first fix outstanding errors in your spreadsheet before PhotoMatching");
                            // Spreadsheet loaded & validated, good to go
                        } else if (lr.getWorksheet().numRows < 1) {
                            JOptionPane.showMessageDialog(null, "No Specimen records found in spreadsheet.  Add at lease one row to this sheet to continue.");
                        } else {

                            try {

                                directoryChooser dc = new directoryChooser(
                                        null,
                                        prefs.get("outputDirectory", System.getProperty("user.home"))
                                );

                                if (!dc.isCancelled()) {
                                    prefs.put("outputDirectory", dc.getDirectoryPath());

                                    directoryNameLabel.setText(dc.getDirectoryPath());
                                    outputDirectory = dc.getDirectoryPath();
                                    if (dc.getDirectoryPath() != null) {
                                        rename.setEnabled(true);
                                        // if we get here then presumably the images have been saved!
                                        // clean out thumbs in pane and add ones that have already been matched
                                        try {
                                            specimenImages.removeAllImages();
                                            if (outputDirectory != null) {
                                                specimenImages.addMatchingImages(photoField, outputDirectory);
                                            }
                                        } catch (NullPointerException e) {
                                            //e.printStackTrace();
                                        }


                                        browseSpreadsheet(lr.getWorksheet(), pmsSubPanel.cbColumns.getSelectedItem().toString());
                                    }
                                }
                            } catch (Exception e) {
                                JOptionPane.showMessageDialog(null, "Configuration schema has not set a field to match photos on.");

                                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                            }

                        }
                    }
                }

            }
        });
    }

    /*
    public void initRulesSchema() {
        worksheet w = mainPage.bv.sWS;
        listPhotoField = w.specimenRecords("Specimen_Num_Collector");
        numRows = w.numRows;
    }  */

    public void init(mainPage pMainpage, bVWorksheet w) {
        activeSheet = w;

        mainpage = pMainpage;

        // Logic for dealing with field Additions
        tinyspreadsheet.init();

        //tinyspreadsheet.init(mainPage.bv.metadataElements.displayFieldsSpreadsheet);

        // Initialize the specimen image browser (referenced from the mainPage.form)
        mainpage.pSpecimenImageBrowser.initSpecimenImageBrowser("", mainpage.pImageDisplay, this);

        this.specimenImages = mainpage.pSpecimenImageBrowser;

        numRows = w.numRows;

        currentRow = 0;

        updateDisplayMessages();

        /*
        // This was removed during refactoring, don't think i need this anymore.
        bVWorksheet w = mainPage.bv.sWS;
        // Allow the following to be set in XML Metadata description, like:
        // <photonamefield>Tree Tag No./ Accession No.</photonamefield>
        try {
            listPhotoField = w.specimenRecords("Specimen_Num_Collector");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Something happened while loading data: " + e.getMessage());

        }

        numRows = w.numRows;
        */
        //this.panelSpecimenDisplay.setVisible(true);
    }

    public String getPhotoField() {
        return photoField;
    }

    public void browseSpreadsheet(bVWorksheet sheet, String photoNameField) {
        activeSheet = sheet;
        numRows = sheet.numRows;

        tinyspreadsheet.addField(photoNameField);

        try {
            listPhotoField = sheet.specimenRecords(photoNameField);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        updateDisplayMessages();
        displayRow(currentRow);
    }

    public void specimenThumbAction(JList list) {
        Component[] listComponents = list.getComponents();
        if (listComponents.length == 0) {
            rename.setVisible(false);
        } else {
            rename.setVisible(true);
        }
    }


    public void prevRow() {
        int prevRow = currentRow - 1;
        if (prevRow < 0) {
            JOptionPane.showMessageDialog(specimenDisplayController, "No more rows to preceeding.");
        } else {
            displayRow(prevRow);
        }
    }

    public void nextRow() {
        int nextRow = currentRow + 1;
        if (nextRow >= numRows) {
            JOptionPane.showMessageDialog(specimenDisplayController, "No more rows to advance.");
        } else {
            displayRow(nextRow);
        }
    }

    /**
     * Handle the renaming of files
     */
    private void rename() {
        Set<String> inputFileNames = new HashSet<String>();
        ArrayList inputFiles = new ArrayList();
        ArrayList inputFileSource = new ArrayList();
        ArrayList outputFiles = new ArrayList();
        String msg = "";
        String notes = "";

        // Build listPhotoField of components
        Component[] listComponents = this.specimenImages.dataList.getComponents();

        // Catch if there are no images -- display message and quit
        if (listComponents.length == 0) {
            JOptionPane.showMessageDialog(specimenDisplayController, "Nothing to rename.");
        } else {

            // Use set to get only unique values
            for (int i = 0; i < listComponents.length; i++) {
                JLabel label = (JLabel) listComponents[i];
                PhotoNameParser p = new PhotoNameParser();
                String fileName = p.getName(label.getToolTipText());

                if (!inputFileNames.add(fileName)) {
                    notes += "(ignoring duplicate file " + fileName + ")\n";
                } else {
                    inputFiles.add(label.getToolTipText());
                }
            }

            // Generate display message and populate outputFiles format (the renamed files)
            PhotoNameParser p1 = null;
            for (int i = 0; i < inputFiles.size(); i++) {
                String input = (String) inputFiles.get(i);

                PhotoNameParser p = new PhotoNameParser();
                String lnewPhotoName = p.getOutputFile(photoField, "+", input);
                String lSpecimen = p.getName(input);

                if (lnewPhotoName != null) {
                    if (!input.equals(outputDirectory + lnewPhotoName)) {
                        outputFiles.add(outputDirectory + lnewPhotoName);
                        inputFileSource.add(input);
                        msg += "  " + lSpecimen + " -> " + outputDirectory + lnewPhotoName + "\n";
                    }
                }
            }
        }

        // If no msg was generated then it is because the images have already been all
        // renamed!
        if (msg.equals("")) {
            JOptionPane.showMessageDialog(specimenDisplayController, "All images already match current record.  Nothing to rename!");
            // Do the renaming
        } else {
            msg = "Rename the following images?\n" + msg;
            // Show dialog box asking to confirm rename operation
            int result = JOptionPane.showOptionDialog(specimenDisplayController,
                    msg + notes,
                    "",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    Constants.OPTIONSOK,
                    Constants.OPTIONSOK[Constants.OK]);

            // Do the rename on all selected files
            boolean success = true;
            if (result == Constants.CONTINUE) {
                for (int i = 0; i < inputFileSource.size(); i++) {
                    String in = (String) inputFileSource.get(i);
                    String out = (String) outputFiles.get(i);
                    System.out.println("renaming file " + in + " TO " + out);
                    //File file = new File(out);
                    try {
                        if (!CopyFile.run(in, out)) {
                            JOptionPane.showMessageDialog(specimenDisplayController,
                                    "Unable to rename file " + in);
                            success = false;
                        } else {
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(specimenDisplayController,
                                "Unable to rename file " + in);
                        success = false;
                    }
                }
                if (success) {
                    JOptionPane.showMessageDialog(specimenDisplayController, "Successfully renamed all files.");
                    // refresh images with all matching images for this specimen
                    this.specimenImages.removeAllImages();
                    this.specimenImages.addMatchingImages(photoField, outputDirectory);
                    this.specimenImages.updateUI();
                }
            }
        }
    }


    public void displayRow(int row) {
        // this is a bit of hack--- i'm not sure why application was not initializing properly..
        // this ensures that it actually happens.
        if (listPhotoField == null) {
            this.init(mainpage, activeSheet);
        }
        // only proceed to displaying this row if we pass the specimenImageChecker
        // to see if current rows and values are all ready to go.
        int result = Constants.CONTINUE;
        try {
            if (!specimenImageChecker()) {

                String msg = "One or more images you have selected have not been renamed. \n" +
                        "(If you select 'Continue' images you have selected will not be renamed.";
                result = JOptionPane.showOptionDialog(specimenDisplayController,
                        msg,
                        "",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        Constants.OPTIONS,
                        Constants.OPTIONS[Constants.CONTINUE]);
            }
        } catch (NullPointerException e) {
            // do nothing -- most likely there are no specimen images to check
        }
        if (result == Constants.CONTINUE) {
            // If we get to this point, we are ok

            currentRow = row;

            photoField = (String) listPhotoField.get(currentRow);
            try {
                tinyspreadsheet.removeAll();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            tinyspreadsheet.update(activeSheet, photoField);
            updateDisplayMessages();

            try {
                // if we get here then presumably the images have been saved!

                specimenImages.removeAllImages();
                if (outputDirectory != null) {
                    specimenImages.addMatchingImages(photoField, outputDirectory);

                }
            } catch (Exception e) {
                // do nothing -- most likely no images to bother with
            }
        }
    }

    /**
     * Check the listPhotoField of images against the current specimen
     *
     * @return return value of true indicates we can proceed safely while false means we need
     *         to rename something
     */
    public boolean specimenImageChecker() {
        PhotoNameParser p = new PhotoNameParser();
        // Check that we really want to move to the new row:
        // 1. Examine currentRow and compare against images in specimenImageBrowser
        // possible actions: save? move on? cancel?
        Component[] listComponents = this.specimenImages.dataList.getComponents();

        // 1. If there are no images in the specimen Image Browser then return true
        if (listComponents.length < 1) {
            return true;
        }

        // 2. If all the images in the specimen Image Browser match the photoField
        // in naming convention, then return true
        for (int i = 0; i < listComponents.length; i++) {
            JLabel label = (JLabel) listComponents[i];
            if (!p.compareName(photoField, label.getToolTipText())) {
                System.out.println(photoField + ":" + label.getToolTipText());
                return false;
            }
        }

        return true;
    }


    public void updateDisplayMessages() {
        int displayCurrentRow = currentRow + 1;
        specimensLabel.setText("displaying " + displayCurrentRow + " of " + numRows + " rows");
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        specimenDisplayController = new JPanel();
        specimenDisplayController.setLayout(new GridLayoutManager(7, 3, new Insets(0, 0, 0, 0), -1, -1));
        rename = new JButton();
        rename.setText("rename");
        specimenDisplayController.add(rename, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, 1, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, -1), null, 0, false));
        photonameLabel = new JLabel();
        photonameLabel.setText("...");
        specimenDisplayController.add(photonameLabel, new GridConstraints(5, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(300, -1), null, null, 0, false));
        outputDirectoryButton = new JButton();
        outputDirectoryButton.setText("Output Directory");
        specimenDisplayController.add(outputDirectoryButton, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), new Dimension(150, -1), 0, false));
        directoryNameLabel = new JLabel();
        directoryNameLabel.setFont(new Font(directoryNameLabel.getFont().getName(), Font.ITALIC, directoryNameLabel.getFont().getSize()));
        directoryNameLabel.setText("...");
        specimenDisplayController.add(directoryNameLabel, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(2, 3, new Insets(0, 0, 0, 0), -1, -1));
        specimenDisplayController.add(panel1, new GridConstraints(3, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        prev = new JButton();
        prev.setText("Prev");
        panel1.add(prev, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(60, -1), new Dimension(80, -1), 0, false));
        specimensLabel = new JLabel();
        specimensLabel.setText("...");
        panel1.add(specimensLabel, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, new Dimension(200, -1), 0, false));
        final JLabel label1 = new JLabel();
        label1.setEnabled(true);
        label1.setFont(new Font(label1.getFont().getName(), Font.BOLD, label1.getFont().getSize()));
        label1.setText("Scroll through rows on the spreadsheet that you have loaded ...");
        panel1.add(label1, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        next = new JButton();
        next.setText("Next");
        panel1.add(next, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(60, -1), new Dimension(80, -1), 0, false));
        renamePhotosTextPane = new JTextPane();
        renamePhotosTextPane.setBackground(new Color(-1121042));
        renamePhotosTextPane.setFont(new Font(renamePhotosTextPane.getFont().getName(), Font.ITALIC, renamePhotosTextPane.getFont().getSize()));
        renamePhotosTextPane.setText("Rename Photos.");
        specimenDisplayController.add(renamePhotosTextPane, new GridConstraints(6, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(100, 10), null, 0, false));
        tinyspreadsheet = new tinySpreadsheetViewer();
        specimenDisplayController.add(tinyspreadsheet, new GridConstraints(4, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(-1, 100), null, 0, false));
        resultsPanel = new JPanel();
        resultsPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        specimenDisplayController.add(resultsPanel, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(250, -1), null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setEnabled(true);
        label2.setFont(new Font(label2.getFont().getName(), Font.BOLD, label2.getFont().getSize()));
        label2.setText("Select which sheet/fieldname to match photos to:");
        specimenDisplayController.add(label2, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return specimenDisplayController;
    }
}



