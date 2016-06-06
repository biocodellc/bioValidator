package edu.berkeley.biocode.validator;

import org.apache.commons.digester.Digester;
import org.jdesktop.swingworker.SwingWorker;
import org.xml.sax.SAXException;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


/**
 * bioValidator class is the main class for running validations (pass in excel file and validation XML file)
 */
public class bioValidator implements PropertyChangeListener {

    public MetadataElements metadataElements;
    public ArrayList bVRules = new ArrayList();
    public String excelFile;
    public validationFileFetcher validationFileFetcher;
    public validationFileFetcher xsdFile;

    biovalidatorTask task;
    public edu.berkeley.biocode.bioValidator.mainPage mainpage;
    public boolean console = false;

    private ArrayList uploadSheets = new ArrayList();

    public bioValidator(edu.berkeley.biocode.bioValidator.mainPage mp) {

        mainpage = mp;

        runValidationFileFetcher(
                Integer.getInteger("app.validationExpireHours"),
                mp.refreshValidationCacheButton,
                false
        );


    }

    public bioValidator() {
        console = true;

        runValidationFileFetcher(
                Integer.getInteger("app.validationExpireHours"),
                new validationFileFetcher(),
                false
        );
    }

    public static void main(String args[]) {
        setProperties sp = new setProperties();
        bioValidator bv = new bioValidator();
        bv.run("/Users/biocode/bioValidatorSpreadsheets/fishlarvae.xls");
    }

    /**
     * run the bioValidator
     *
     * @param pExcelFile The excel spreadsheet
     */
    public void run(String pExcelFile) {
        bVRules = new ArrayList();

        excelFile = pExcelFile;

        task = new biovalidatorTask();
        task.addPropertyChangeListener(this);
        task.execute();
    }

    public boolean runValidationFileFetcher(int hours,
                                            validationFileFetcher pValidationFileFetcher,
                                            boolean clickButton) {

        mainpage.newMsg("Loading validation file");


        validationFileFetcher = null;
        validationFileFetcher = pValidationFileFetcher;

        // TODO: check to see if default validation is set, otherwise, popup the dialog to choose 
        validationFile validationFile = validationFileFetcher.choose(clickButton);

        // null validation file, we don't want to continue
        if (validationFile == null) {
            return false;
        }

        // Clear interface
        mainpage.clearInterface();

        // Handle XML File
        validationFileFetcher.initRulesSchema(hours);
        Color red = new Color(255, 0, 0);
        Color black = new Color(0, 0, 0);
        Color displayColor = red;

        // validationFileFetcher Not There!
        if (validationFileFetcher.getBody() == null) {
            if (console) {
                System.out.println(validationFileFetcher.getDescriptiveText());
            } else {
                String message = "Encountered a problem finding validation file!\n  " +
                        "If this is the first time you have run this application, you must have an internet connection\n" +
                        "Start your internet connection and then select 'Refresh Validation Cache'";

                JOptionPane.showMessageDialog(null, message);
                mainpage.labelXML.setForeground(displayColor);
                mainpage.labelXML.setText(validationFileFetcher.getDescriptiveText());
                return false;
            }
        } else {
            // GOOD SCHEMA
            if (validationFileFetcher.schemaValidated) {
                displayColor = black;
                mainpage.buttonRun.setEnabled(true);
                mainpage.buttonSpreadsheet.setEnabled(true);


            }

            // Messages
            if (console) {
                System.out.println(validationFileFetcher.getDescriptiveText());
            } else {
                mainpage.labelXML.setForeground(displayColor);
                mainpage.labelXML.setText(validationFileFetcher.getDescriptiveText());
            }

            // BAD SCHEMA
            if (!validationFileFetcher.schemaValidated) {
                mainpage.insertMsg("Trouble loading validation file:");
                mainpage.insertMsg(validationFileFetcher.getDescriptiveText());
                return false;
            }
        }

        // Loading Metadata
        Digester mD = new Digester();
        metadataElements = new MetadataElements();
        mD.push(metadataElements);
        addMetadata(mD);
        try {
            mD.parse(new StringReader(validationFileFetcher.getBody()));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        metadataElements.run();


        enableUpdate(false);

        return true;
    }

    class biovalidatorTask extends SwingWorker<Void, Void> {
        @Override
        public Void doInBackground() {
            mainpage.newMsg("Initializing workbook ");
            bVWorkbook myBook = null;

            try {
                // Load Workbook
                mainpage.insertMsg("Loading " + excelFile);
                myBook = new bVWorkbook(excelFile);
            } catch (Exception e) {
                mainpage.insertMsg("Trouble loading " + excelFile + "?<br>" + e);
            }

            // Loop each worksheet defined by the configuration file
            ArrayList arrWorksheets = validationFileFetcher.getWorksheets();

            // update results Panel
            mainpage.resultsSubPanel.init(arrWorksheets);


            Iterator<String> worksheets = arrWorksheets.iterator();

            while (worksheets.hasNext()) {
                String worksheet = worksheets.next();

                mainpage.insertMsg("Attempting to load " + worksheet);


                bVWorksheet ws = myBook.loadSheet(
                        worksheet,
                        Integer.parseInt(mainpage.FieldHeadingsOnRow.getSelectedItem().toString()));
                if (!ws.isValid) {
                    HashMap<String, JLabel> map = mainpage.resultsSubPanel.labelMap;
                    JLabel jlb = map.get(worksheet);
                    jlb.setForeground(new Color(255, 255, 0));
                    jlb.setText("Not Found");
                    mainpage.insertMsg("<p>Worksheet named " + worksheet + " is not found!  <br>Your ruleset requires " + worksheet + " to be in your workbook. Fix this and re-run validation.");
                } else {

                    mainpage.insertMsg("Creating digester instance for  " + worksheet);
                    Digester d = new Digester();
                    Rules r = new Rules(ws);

                    bVRules.add(r);
                    d.push(r);

                    try {
                        // Define how to parse XML files
                        if (ws != null) addRules(d, ws.getSheetName());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // Process the input file.
                    try {

                        // Construct XML to parse that contains just individual sheets and
                        // the metadata.... this is a HACK to workaround the fact that digester
                        // cannot parse attributes in cases like:
                        // <Worksheet sheetname="Collecting Events" />
                        String body = validationFileFetcher.getBody();
                        mainpage.insertMsg("Parsing " + worksheet);
                        String mXML = validationFileFetcher.getBody(body,"Metadata");
                        String wXML = validationFileFetcher.getBody(body,worksheet);
                        String xmltoParse = "<Validate>" + mXML + wXML + "</Validate>";

                        // JBD -- to help SI with Debugging
                        System.out.println("XML that is being parsed :" + xmltoParse);

                        if (ws != null) d.parse(new StringReader(xmltoParse));

                    } catch (IOException e) {
                        e.printStackTrace();
                        System.exit(-1);
                    } catch (org.xml.sax.SAXException se) {
                        se.printStackTrace();
                        // JBD -- to help SI with Debugging
                        mainpage.insertMsg("Possible error parsing XML Validation file:" + se.getMessage());
                        //System.exit(-1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // Run rules
                    mainpage.insertMsg("Running rules for " + worksheet);
                    if (ws != null) r.run(mainpage);
                    // Add user labels
                    HashMap<String, JLabel> map = mainpage.resultsSubPanel.labelMap;

                    JLabel jlb = map.get(worksheet);

                    if (r.hasErrors()) {
                        enableUpdate(false);
                        jlb.setForeground(new Color(255, 0, 0));
                        jlb.setText("errors");
                    } else if (r.hasWarnings()) {
                        addUploadSheet(ws);
                        enableUpdate(true);
                        mainpage.matcherSpecimenDisplayController.init(mainpage, ws);
                        jlb.setForeground(new Color(255, 165, 0));
                        jlb.setText("warnings");
                    } else {
                        addUploadSheet(ws);
                        enableUpdate(true);
                        mainpage.matcherSpecimenDisplayController.init(mainpage, ws);
                        jlb.setForeground(new Color(0, 255, 0));
                        jlb.setText("valid");
                    }

                }

                // Add the PhotoMatchSelectorSubPanel which lets user select sheet and values
                // to assign to photos in a drop-down box
                mainpage.matcherSpecimenDisplayController.pmsSubPanel.init(bVRules);

            }

            return null;
        }

        /**
         * done is called when the spreadsheet has finished loading
         */
        @Override
        public void done() {

        }
    }

    private void enableUpdate() {
        enableUpdate(true);
    }

    /**
     * enable the update button and login name/password fields
     */
    private void enableUpdate(boolean enable) {
        if (!console) {
            mainpage.buttonUpload.setEnabled(enable);
            mainpage.buttonUploadFT.setEnabled(enable);
        } else {
            System.out.println("update to db function not enabled here");
        }

        // Set this up for biocode/non-biocode
        if (!validationFileFetcher.isBiocode()) {
            mainpage.buttonUpload.setVisible(false);
        } else {
            mainpage.buttonUpload.setVisible(true);

        }
    }


    /**
     * Method that utilizes Commons digester to parse XML file and assigns functions to appropriate elements
     *
     * @param d
     * @param name
     */
    private static void addRules(Digester d, String name) {
        /*d.addObjectCreate("Validate/" + name + "/rule", Rule.class);
        d.addSetProperties("Validate/" + name + "/rule");
        d.addSetNext("Validate/" + name + "/rule", "addRule");
        d.addCallMethod("Validate/" + name + "/rule/field", "addField", 0);
        */
        d.addObjectCreate("Validate/Worksheet/rule", Rule.class);
        d.addSetProperties("Validate/Worksheet/rule");
        d.addSetNext("Validate/Worksheet/rule", "addRule");
        d.addCallMethod("Validate/Worksheet/rule/field", "addField", 0);
    }

    private static void addMetadata(Digester d) {
        d.addObjectCreate("Validate/Metadata/metadata", Metadata.class);
        d.addSetProperties("Validate/Metadata/metadata");
        d.addSetNext("Validate/Metadata/metadata", "addMetadata");
        d.addCallMethod("Validate/Metadata/metadata/field", "addField", 0);
    }


    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {

    }
    private void addUploadSheet(bVWorksheet ws) {
        uploadSheets.add(ws);
    }
    public Iterator getUploadSheets() {
        return uploadSheets.iterator();        
    }

}
