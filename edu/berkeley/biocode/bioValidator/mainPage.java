package edu.berkeley.biocode.bioValidator;


import com.google.gdata.util.AuthenticationException;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import edu.berkeley.biocode.flickr.uploadImages;
import edu.berkeley.biocode.photoMatcher.*;
import edu.berkeley.biocode.utils.*;
import edu.berkeley.biocode.validator.*;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.*;
import java.net.URL;
import java.rmi.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.prefs.Preferences;

/**
 * Created by IntelliJ IDEA.
 * User: joyce gross 1234
 * Date: Nov 11, 2009
 * Time: 8:14:52 AM
 * To change this template use File | Settings | File Templates.
 * uinsert
 *  test
 */

public class mainPage {
    private JPanel mainPanel;
    public JButton buttonUpload;
    private JTabbedPane tabbedPane;
    private JTabbedPane tabbedPane1;
    public JButton buttonSpreadsheet;
    public scrollableEditorPane textPane1;
    //public JEditorPane textPane1;
    private JButton buttonGetSpecimenIDs;
    public JLabel labelValidateCE;
    public JLabel labelValidateSpecimen;
    private JLabel labelSpreadsheet;
    private JButton buttonGetCEIDs;
    private JTextArea textSystemMessages;
    private JLabel labelGetSpecimenIDs;
    private JLabel labelGetCEIDs;
    private JPanel topPanel;
    private JPanel bottomPanel;
    public JButton buttonRun;
    public JLabel labelXML;
    private JButton buttonImageDirectory;
    private JLabel labelImageDirectory;
    private panelImageBrowser displayImagesPanel;
    private JProgressBar progressBar;
    private JLabel labelNavigation;
    public validationFileFetcher refreshValidationCacheButton;
    private photoListPanel panelphotolist;
    private JButton loadPhotosButton;
    private JRadioButton radioButtonRow;
    private JRadioButton radioButtonMessage;
    private panelImageDisplay displayImages;
    private JPanel panelImageViewContainer;
    private panelImageDisplay panelImageView;
    private JPanel panelSpecimenViewContainer;
    public tinySpreadsheetViewer panelSpecimenView;
    private JLabel labelLoadPhotos;
    private JButton buttonUploadToFlickr;
    private JRadioButton radioButtonDatabase;
    private JRadioButton radioButtonSpreadsheet;
    private JButton buttonSpreadsheetLookup;
    private JButton buttonDBLookup;
    private panelImageBrowser pImageBrowser;
    public panelImageDisplay pImageDisplay;
    private JPanel panelSpecimenImageDrop;
    public panelImageBrowser pSpecimenImageBrowser;
    //public panelSpecimenDisplay matcherSpecimenDisplayController;
    public specimenDisplayController matcherSpecimenDisplayController;
    private JPanel specimenMatchPanel;
    public JButton buttonPrev;
    public JButton buttonNext;
    public JLabel labelImagesDisplayed;
    private JButton buttonSampleSpreadsheet;
    public JPasswordField passwordField;
    public JComboBox loginCombo;
    private JLabel labelTitle;
    private JButton buttonUploadToDB;
    public JCheckBox refreshThumbnailCacheCheckBox;
    public JButton buttonFillHigherTaxonomy;
    public JComboBox comboBoxFillHigherTaxonomy;
    private JButton loadTaxonomiesButton;
    public JButton buttonUploadFT;
    private JButton launchPhotoManagerButton;
    private JPanel labelPM;
    public JPanel resultsPanel;
    private JButton buttonValidation;
    private JButton buttonViewData;
    private JPanel buttonData;
    public JComboBox FieldHeadingsOnRow;
    public resultsSubPanel resultsSubPanel;
    private JButton viewDataButton;
    private JLabel XSDLabel;
    public static int lookupStatus;
    private String radioValueValidate = "row";
    private String radioValuePhotoList = "spreadsheet";
    public File fileWorkbook;
    public File fileXML;
    public static validationFileFetcher validationFile;
    public static String excelFile;
    public static bioValidator bv;
    public static Rules r;
    public static myFrame frame;


    //public final String XMLConfigurationFile =
    //            System.getProperty("app.bvFilesDirectory") +
    //           System.getProperty("app.validationFileName") +
    //           System.getProperty("app.cacheExtension");


    public mainPage() {
        final Preferences prefs = Preferences.userRoot().node(System.getProperty("app.bioValidatorUserPreferences"));

        // Set title
        labelTitle.setText("Biocode Validation and PhotoMatching Tool, version " + System.getProperty("app.version"));

        resultsSubPanel = new resultsSubPanel();

        resultsPanel.add(
                resultsSubPanel,
                new GridConstraints());


        // RadioButton Group for Validate
        ButtonGroup radioGroupValidate = new ButtonGroup();
        radioGroupValidate.add(radioButtonRow);
        radioGroupValidate.add(radioButtonMessage);

        radioButtonRow.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                radioValueValidate = "row";
                radioButtonSpreadsheet.setSelected(true);
            }
        });
        radioButtonMessage.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                radioValueValidate = "message";
            }
        });

        // RadioButton Group Lookup Method
        ButtonGroup radioGroupLookup = new ButtonGroup();
        radioGroupLookup.add(buttonSpreadsheetLookup);
        radioGroupLookup.add(buttonDBLookup);

        /*  buttonSpreadsheetLookup.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            ArrayList list = panelphotolist.photostablemodel.getFileColumn();

            if (bv.sWS != null) {
                Map map = bv.sWS.MatchNames("Specimen_Num_Collector", list);
                updateSpecimenPhotoLookup(map, list.size());
                panelSpecimenView.init(mainPage.bv.metadataElements.displayFieldsSpreadsheet);
                buttonSpreadsheetLookup.setForeground(new Color(0, 0, 255));
                buttonDBLookup.setForeground(new Color(0, 0, 0));
            } else {
                JOptionPane.showMessageDialog(mainPanel, "Must first select a spreadsheet in bioValidate Panel");
            }
            mainPage.this.lookupStatus = Constants.SPREADSHEET;
            panelphotolist.sort();

        }
    });

    buttonDBLookup.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            ArrayList list = panelphotolist.photostablemodel.getFileColumn();

            biocodeDB db = null;
            try {
                db = new biocodeDB();
            } catch (Exception e1) {
                JOptionPane.showMessageDialog(mainPanel, "Unable to make DB connection, check internet cxn or talk to Administrator.");
                e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            if (db != null) {
                Map map = db.MatchNames("biocode", "Specimen_Num_Collector", list);

                updateSpecimenPhotoLookup(map, list.size());
                panelSpecimenView.init(mainPage.bv.metadataElements.displayFieldsSpreadsheet);
                buttonSpreadsheetLookup.setForeground(new Color(0, 0, 0));
                buttonDBLookup.setForeground(new Color(0, 0, 255));
            }
            mainPage.this.lookupStatus = Constants.DB;
            panelphotolist.sort();


        }
    });
        */
        buttonUploadToFlickr.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                /*int selectedRows[] = panelphotolist.table.getSelectedRows();
                for (int i = 0; i < selectedRows.length; i++) {
                    System.out.println("Row Selection Number: " +
                            selectedRows[i] +
                            panelphotolist.photostablemodel.getValue(selectedRows[i], 1));
                }
                */
                //System.out.println(panelphotolist.photostablemodel.getValueAt().getSelectedRows());
                uploadImages u = new uploadImages(panelphotolist, labelLoadPhotos.getText());
            }
        });

        // PhotoLoader, load images from a directory
        loadPhotosButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                directoryChooser dc = new directoryChooser(
                        frame,
                        prefs.get("outputDirectory", System.getProperty("user.home"))
                );

                if (!dc.isCancelled()) {
                    prefs.put("outputDirectory", dc.getDirectoryPath());

                    // Set label showing Directory chosen on UI
                    labelLoadPhotos.setText(dc.getDirectoryPath());
                    panelphotolist.removeAll();
                    panelphotolist.init(mainPage.this.panelImageView, mainPage.this.panelSpecimenView, dc.getDirectoryPath());

                    ArrayList imageList = dc.getImageList();
                    PhotoNameParser pnP = new PhotoNameParser();

                    for (int i = 0; i < imageList.size(); i++) {
                        String filename = (String) imageList.get(i);

                        panelphotolist.photostablemodel.addRow(new photoListRow(filename, pnP.parseName(filename)));
                        panelphotolist.photostablemodel.updateFlickrStatus(prefs.get(filename, "not loaded"), i);
                    }
                    // Enable spreadsheet lookup values by default, but only if a spreadsheet has been selected
                    // TODO: specific specimen_numc_collector matching a lost art...
/*
                    if (bv.bVRules.size() > 0) {
                        //buttonSpreadsheetLookup.doClick();
                        // Spreadsheet lookup is done automatically
                        Map map = null;
                        try {
                            //map = ((Rules) bv.bVRules.get(0)).getWorksheet().MatchNames("Specimen_Num_Collector", imageList);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                            JOptionPane.showMessageDialog(mainPanel, "Exception Attempting match names: " + e1.getMessage());
                        }
                        updateSpecimenPhotoLookup(map, imageList.size());
                        //panelSpecimenView.init(mainPage.bv.metadataElements.displayFieldsSpreadsheet);
                        panelSpecimenView.init();
                        buttonSpreadsheetLookup.setForeground(new Color(0, 0, 255));
                        buttonDBLookup.setForeground(new Color(0, 0, 0));
                    }
                    */
                }
            }
        });
        buttonUpload.setVisible(false);  // by default do NOT make this visible -- only when we know this is biocode do we make it visible
        buttonUpload.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String passresult = "Unable to make connection to database or some other error occurred.";
                String msg = "This will upload your current spreadsheet to the Biocode DB.  \nYou may encounter errors or warnings in this section that have not already been caught. \n\nAre you sure you wish to continue?";

                insertMsg("");

                // Username combobox
                JComboBox cb = new JComboBox();
                for (Iterator i = bv.metadataElements.loginNames.iterator(); i.hasNext();) {
                    String name = (String) i.next();
                    cb.addItem(name);
                }

                // Password
                JPasswordField jp = new JPasswordField();

                // Project Code ComboBox
                JComboBox cbProjectCode = new JComboBox();
                for (Iterator i = bv.metadataElements.list1.iterator(); i.hasNext();) {
                    String name = (String) i.next();
                    cbProjectCode.addItem(name);
                }

                int result = JOptionPane.showConfirmDialog(null,
                        new Object[]{
                                new Label("Username:"), cb,
                                new Label("Password:"), jp,
                                new Label("Project Code:"), cbProjectCode,
                        },
                        "Uploading Credentials",
                        JOptionPane.OK_CANCEL_OPTION);


                String xml = "";
                if (result == 0) {
                    InputStream serverInput = null;
                    try {
                        serverInput = ClientHttpRequest.post(
                                new URL(System.getProperty("app.uploadBaseURL") + "cgi/biocode_checkpass"),
                                new Object[]{
                                        "entry_by", cb.getSelectedItem().toString(),
                                        "password", new String(jp.getPassword()),
                                        "format", "xml",
                                        "Submit", "Submit",
                                });
                    } catch (IOException e1) {
                        e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }

                    try {
                        xml = convertStreamToString(serverInput).toString();
                    } catch (IOException e1) {
                        e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }

                    passresult = parseCheckPassResponse(xml);

                }


                if (passresult.equals("1")) {
                    try {
                        ProgressMonitor progressmonitor = new ProgressMonitor(null, "Loading and processing file", "Waiting for Response to Download", 0, 100);
                        progressmonitor.setMillisToPopup(0);
                        progressmonitor.setMillisToDecideToPopup(0);

                        int i = 0;
                        while (i < 100) {
                            try {
                                Thread.sleep(10);
                                progressmonitor.setProgress(i);
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                            }
                            i++;
                        }

                        String specimenLoad = "true";
                        Iterator it = bv.bVRules.iterator();
                        while (it.hasNext()) {
                            Rules r = (Rules) it.next();

                            if (r.getWorksheet().numRows != null &&
                                    r.getWorksheet().numRows < 1) {
                                // Nothing??!?

                            }

                        }

                        System.out.println("version is " + System.getProperty("app.version"));

                        // Biocode specific cases to control uploader
                        Iterator<bVWorksheet> us = bv.getUploadSheets();
                        String specimen = "false";
                        String collecting_event = "false";
                        while (us.hasNext()) {
                            bVWorksheet bvw = ((bVWorksheet) us.next());
                            String worksheet = bvw.getSheetName();

                            System.out.println("worksheet = " + worksheet);
                            if (worksheet.equalsIgnoreCase("specimens") && (bvw.getNumRows() > 0)) {
                                specimen = "true";
                            }
                            if (worksheet.equalsIgnoreCase("collecting_events") && (bvw.getNumRows() > 0)) {
                                collecting_event = "true";
                            }
                        }

                        InputStream serverInput = ClientHttpRequest.post(
                                new URL(System.getProperty("app.uploadBaseURL") + "cgi/biocode_excel_load"),
                                new Object[]{
                                        "specimen", specimen,
                                        "collecting_event", collecting_event,
                                        "projectcode", cbProjectCode.getSelectedItem().toString(),
                                        "version", System.getProperty("app.version"),
                                        "Submit", "Submit",
                                        "excel_specimens", new File(bv.excelFile)
                                });

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        }

                        newMsg("<h3>SERVER RESPONSE FOR UPLOAD REQUEST ... </h3>" + convertStreamToString(serverInput));

                        progressmonitor.close();

                    } catch (UnknownHostException e2) {
                        JOptionPane.showMessageDialog(mainPanel, "Encountered an issue connecting to Biocode DB.  Do you have an internet connection?");
                    } catch (IOException e1) {
                        JOptionPane.showMessageDialog(mainPanel, "Error connecting to Biocode DB. May not have uploaded.");
                        e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        //} catch (BadLocationException e1) {
                        //    JOptionPane.showMessageDialog(mainPanel, "Error connecting to Biocode DB. May not have uploaded.");
                        //    e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                } else {
                    newMsg(passresult);
                }

            }
        }

        );

        buttonUploadFT.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                FusionTables ft = null;

                String error = "";
                String msg = "";

                newMsg("Fusion Tables upload function started");
                  /*
                try {
                    ft = new FusionTables("user", "pass", mainPage.this);
                } catch (AuthenticationException e1) {
                    e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                ft.loadDialog(bv.bVRules);
                */

                try {
                    // TODO: store authToken so user doesn't have to login all the time?
                    ft = new FusionTables(fileWorkbook.getName(),mainPage.this);

                } catch (AuthenticationException e1) {
                    error += e1.getMessage() + "<p> Did you enter your username and password correctly?";
                }

                if (!ft.auth) {
                    JOptionPane.showMessageDialog(mainPanel, "Warning: " + error);
                } else {
                    try {
                        ft.loadDialog(bv.bVRules);                        
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        JOptionPane.showMessageDialog(mainPanel, "Something happened while loading data: " + e1.getMessage());
                    }

                }
                insertMsg(msg);

            }
        }

        );

        launchPhotoManagerButton.addActionListener(new
                ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        BareBonesBrowserLaunch.openURL("http://biocode.berkeley.edu/photomanager/");
                    }
                }
        );

        // LoadSpreadsheet
        buttonSpreadsheet.addActionListener(new

                ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        JFileChooser fc = null;
                        if (prefs.get("spreadsheetFile", null) != null) {
                            fc = new JFileChooser(new File(prefs.get("spreadsheetFile", null)));
                        } else {
                            fc = new JFileChooser();
                        }
                        FileFilter filter = new FileNameExtensionFilter("Spreadsheet File", "xls");
                        fc.setFileFilter(filter);
                        fc.showOpenDialog(null);
                        try {
                            prefs.put("spreadsheetFile", fc.getSelectedFile().getAbsolutePath());
                        } catch (NullPointerException nulle) {
                            System.out.println("unable to write spreadsheetFile preference");
                        }
                        fileWorkbook = fc.getSelectedFile();
                        excelFile = fileWorkbook.getAbsolutePath();
                        labelSpreadsheet.setText(excelFile);

                        bv.run(excelFile);

                        // re-initialize grid on photolistpanel when new spreadsheet is chosen
                        panelphotolist.photostablemodel.setRowCount(0);
                        labelLoadPhotos.setText("");

                    }
                }
        );
/*        // LoadSpreadsheet
        buttonSampleSpreadsheet.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                excelFile = System.getProperty("user.dir") + System.getProperty("file.separator") + "sample.xls";
                labelSpreadsheet.setText(excelFile);

                bv.run(excelFile);
            }
        }

        );
        */
        // LoadSpreadsheet
        buttonRun.addActionListener(new
                ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (excelFile != null) {
                            bv.run(excelFile);
                        } else {
                            JOptionPane.showMessageDialog(mainPanel, "Please load spreadsheet before running.");
                        }
                    }
                }
        );

        // Load Images in photoMatcher
        buttonImageDirectory.addActionListener(new

                ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        // Null sWS means a spreadsheet has not been loaded yet
                        /*if (bv.sWS == null) {
                            JOptionPane.showMessageDialog(mainPanel, "Must first select a Specimen spreadsheet");
                            // Spreadsheet not validated
                        } else if (bv.sRules.hasErrors()) {
                            JOptionPane.showMessageDialog(mainPanel, "Must first fix outstanding errors in your spreadsheet before PhotoMatching");
                            // Spreadsheet loaded & validated, good to go
                        } else if (bv.sWS.numRows < 1) {
                            JOptionPane.showMessageDialog(mainPanel, "No Specimen records found in spreadsheet.  Add at lease one row to this sheet to continue.");
                        } else {
                        */
                        directoryChooser dc = new directoryChooser(frame, prefs.get("imageDirectory", System.getProperty("user.home")));
                        if (!dc.isCancelled()) {
                            prefs.put("imageDirectory", dc.getDirectoryPath());
                            pImageBrowser.initMainImageBrowser(dc, mainPage.this);
                            // Set the border for specimen matching
                            //specimenMatchPanel.setBorder(BorderFactory.createLineBorder(Color.black));
                            // Set the label of the directory just chosen
                            labelImageDirectory.setText(dc.getDirectoryPath());
                            //}
                        }
                    }
                }
        );


        refreshValidationCacheButton.addActionListener(new
                ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        //((validationFileFetcher)e.getSource()).choose();

                        bv.runValidationFileFetcher(0, (validationFileFetcher) e.getSource(), true);
                    }
                }
        );


        buttonViewData.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                newMsg("Loading spreadsheet data into frame");
                insertMsg(" ... ");
                String strResults = "";

                Iterator it = bv.bVRules.iterator();

                boolean found = false;
                HashMap<String, JRadioButton> map = resultsSubPanel.radioMap;

                while (it.hasNext()) {
                    Rules localRules = (Rules) it.next();
                    if (map.get(localRules.getWorksheet().getSheetName()).
                            isSelected()) {
                        found = true;
                        insertMsg(localRules.getWorksheet().printAll());

                        int count = localRules.getWorksheet().getNumRows() - 1;
                        strResults += "<p>Results only show up to 10 lines of data (" +
                                count +
                                " total)";
                    }
                }
                insertMsg(strResults);

                if (!found) {
                    JOptionPane.showMessageDialog(mainPanel, "select a worksheet");
                }
            }
        });

        buttonValidation.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {

                    Iterator it = bv.bVRules.iterator();

                    boolean found = false;
                    while (it.hasNext()) {
                        Rules lr = (Rules) it.next();
                        String sheetName = lr.getWorksheet().getSheetName();

                        HashMap<String, JRadioButton> map = resultsSubPanel.radioMap;
                        JRadioButton jrb = map.get(sheetName);

                        if (jrb.isSelected()) {
                            found = true;
                            String output = lr.printMessages(radioValueValidate);
                            if (output.equals("")) {
                                output += sheetName + " sheet validated";
                            }
                            newMsg(output);
                        }
                    }
                    if (!found) {
                        JOptionPane.showMessageDialog(mainPanel, "select a worksheet");
                    }
                } catch (Exception Exc) {
                    Exc.printStackTrace();
                    insertMsg("error initializing bioValidator -- Try selecting a spreadsheet first!\n" + Exc);
                }
            }
        });

        // Populate Fill Higher Taxonomy Combo Box
/*        localTaxonomies lt = null;
        try {
            lt = new localTaxonomies();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < lt.getTaxonomies().size(); i++) {
            this.comboBoxFillHigherTaxonomy.addItem(lt.getTaxonomies().get(i));
        }

        loadTaxonomiesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Username combobox
                lookupTaxonomies lt = new lookupTaxonomies();
                JComboBox cb = new JComboBox();

                for (int i = 0; i < lt.getRemoteTaxonomies().size(); i++) {
                    cb.addItem(lt.getRemoteTaxonomies().get(i));
                }

                int result = JOptionPane.showConfirmDialog(null,
                        new Object[]{new Label("Taxonomy:"), cb},
                        "Load Taxonomy",
                        JOptionPane.OK_CANCEL_OPTION);

                // OK Button pressed
                if (result == 0) {
                    String filetoDownload = (String) lt.getRemotetaxonomyfilenames().get(cb.getSelectedIndex());
                    try {
                        downloadTask task = new downloadTask(new URL(filetoDownload), comboBoxFillHigherTaxonomy);
                    } catch (MalformedURLException e1) {
                        e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }

            }
        });
  */
        /*
                viewSpecimenDataButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        newMsg("Loading spreadsheet data into frame");
                        insertMsg(" ... ");
                        String strResults = "";
                        insertMsg(bv.sWS.printAll());/*
                        String[][] dataArray = bv.sWS.printAll();
                        String strResults = "";
                        strResults += "<table>";
                        boolean limitreached = false;
                        for (int i = 0; i < dataArray.length; i++) {
                            strResults += "<tr>";
                            for (int j = 0; j < dataArray[i].length; j++) {
                                if (dataArray[i][j] != null) {
                                    strResults += "<td>" + dataArray[i][j] + "</td>";
                                } else {
                                    strResults += "<td></td>";
                                }
                            }
                            strResults += "</tr>";
                        }
                        strResults += "</table>";

                        if (bv.sWS.getNumRows() > 10) {
                            strResults += "<p>Results truncated to 10 lines from " + bv.sWS.getNumRows() + " lines";
                        }
                        insertMsg(strResults);

                    }
                });
        */
        /*
        viewCollEventDataButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                newMsg("Loading spreadsheet data into frame");
                insertMsg(" ... ");
                insertMsg(bv.ceWS.printAll());

                String strResults = "";

                if (bv.ceWS.getNumRows() > 10) {
                    strResults += "<p>Results truncated to 10 lines from " + bv.ceWS.getNumRows() + " lines";
                }
                insertMsg(strResults);

            }
        });
        */

/*        buttonFillHigherTaxonomy.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    if (bv.sWS.getNumRows() > 100 && bv.sWS.getNumRows() < 1000) {
                        JOptionPane.showMessageDialog(mainPanel, "Processing Higher Taxonomy is an experimental function\nand may take awhile for longer sets of data. \nBe patient if application does not appear to respond for up to 1 minute.");
                    } else if (bv.sWS.getNumRows() > 1000) {
                        JOptionPane.showMessageDialog(mainPanel, "Processing Higher Taxonomy is an experimental function\nand may take awhile for longer sets of data. \nYour data will be truncated to 1000 rows.\nBe patient if application does not appear to respond for up to 1 minute.");

                    }
                    // Limit to 1000 rows for sanity sake
                    SearchFiles t = new SearchFiles(
                            comboBoxFillHigherTaxonomy.getSelectedItem().toString(),
                            bv.sWS.getColumnLimit("Specimen_Num_Collector", 1000),
                            bv.sWS.getColumnLimit("LowestTaxon", 1000),
                            bv.sWS.getColumnLimit("LowestTaxonLevel", 1000),
                            "html"
                    );

                    if (bv.sWS.getNumRows() > 1000) {
                        t.output = "Number of rows truncated to 1000 from " + bv.sWS.getNumRows() + " ...<br>";
                    }
                    insertMsg(t.output);
                } catch (Exception Exc) {
                    insertMsg("Error filling higher taxonomy.  You must first load a spreadsheet.  If you are still having problems make sure that your sheet is validated.");
                    Exc.printStackTrace();
                }
            }
        });
  */
    }


    public void insertMsg(String msg) {
        writeMsg(msg, true);
    }

    public void newMsg(String msg) {
        writeMsg(msg, false);
    }

    private void writeMsg(String msg, boolean append) {
        try {
            HTMLEditorKit kit = new HTMLEditorKit();
            HTMLDocument htmldoc = (HTMLDocument) textPane1.browser.getDocument();


            if (append) {
                textPane1.browser.setEditorKit(kit);
                textPane1.browser.setDocument(htmldoc);
                //kit.insertHTML(htmldoc, htmldoc.getLength(), msg, 0, 0, HTML.getTag("div"));
                kit.insertHTML(htmldoc, htmldoc.getLength(), msg, 0, 0, null);
            } else {
                textPane1.browser.setDocument(htmldoc);
                textPane1.insertHTML(msg, 0);
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (NullPointerException e) {
            System.out.println("nullpointer?" + e.getMessage());
        } catch (BadLocationException exc) {
            exc.printStackTrace();
        }
    }


    public static void main(String[] args) {
        try {
            // this only needs to be called once as all application properties are set in System
            setProperties sp = new setProperties();
            frame = new myFrame("mainPage");
            mainPage mp = new mainPage();
            bv = new bioValidator(mp);
            frame.setContentPane(mp.mainPanel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        } catch (Exception e) {
            String message = "Problem Starting bioValidator.  Make sure you have write permissions at " +
                    System.getProperty("app.bvFilesDirectory");
            if (e.getMessage() != null) {
                message += "\nA useful message has been generated:\n" + e.getMessage();
            }
            JOptionPane.showMessageDialog(null, message);

            e.printStackTrace();
            System.exit(29);
        }
    }


    private void updateTextArea(final String text) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                textSystemMessages.append(text);
                //textArea.append(text);
            }
        });
    }

    private void redirectSystemStreams() {
        OutputStream out = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                updateTextArea(String.valueOf((char) b));
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                updateTextArea(new String(b, off, len));
            }

            @Override
            public void write(byte[] b) throws IOException {
                write(b, 0, b.length);
            }
        };

        System.setOut(new PrintStream(out, true));
        System.setErr(new PrintStream(out, true));
    }


    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    /**
     * Update the table showing specimen names next to photos.
     *
     * @param map
     */
    private void updateSpecimenPhotoLookup(Map map, int size) {
        int count = 0;
        final Preferences prefs = Preferences.userRoot().node(System.getProperty("app.bioValidatorUserPreferences"));


        Iterator it = map.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            String filename = (String) pairs.getKey();
            String specimen = (String) pairs.getValue();
            panelphotolist.photostablemodel.updateFilename(filename, count);
            panelphotolist.photostablemodel.updateSpecimen(specimen, count);
            panelphotolist.photostablemodel.updateFlickrStatus(prefs.get(filename, "not loaded"), count);

            count++;
        }
        /*
        if (map.size() == size) {
            //his.buttonUploadToFlickr.setEnabled(true);
//            this.buttonUploadToDB.setEnabled(true);
        } else {
            this.buttonUploadToFlickr.setEnabled(false);
            this.buttonUploadToDB.setEnabled(true);
        }
        */
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public String convertStreamToString(InputStream is) throws IOException {
        /*
        * To convert the InputStream to String we use the BufferedReader.readLine()
        * method. We iterate until the BufferedReader return null which means
        * there's no more data to read. Each line will appended to a StringBuilder
        * and returned as String.
        */
        if (is != null) {
            StringBuilder sb = new StringBuilder();
            String line;

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            } finally {
                is.close();
            }
            return sb.toString();
        } else {
            return "";
        }
    }

    private String parseCheckPassResponse(String xml) {
        // The parser chokes on preliminary line breaks so just remove them all
        xml = xml.replaceAll("\n", "");

        NodeList nodeLst = null;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = null;
            db = dbf.newDocumentBuilder();

            Reader reader = new CharArrayReader(xml.toCharArray());
            Document doc = db.parse(new InputSource(reader));

            doc.getDocumentElement().normalize();
            nodeLst = doc.getElementsByTagName("response");
        } catch (SAXException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ParserConfigurationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        finally {
            return nodeLst.item(0).getTextContent();//.getAttributes().getNamedItem("name").toString();
        }
    }

    public void clearInterface() {
        insertMsg("");

/*        labelValidateSpecimen.setText("Waiting");
        labelValidateSpecimen.setForeground(new Color(0, 0, 0));

        labelValidateCE.setText("Waiting");
        labelValidateCE.setForeground(new Color(0, 0, 0));
  */
        buttonRun.setEnabled(false);
        buttonSpreadsheet.setEnabled(false);


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
        tabbedPane = new JTabbedPane();
        tabbedPane.setMinimumSize(new Dimension(800, 600));
        tabbedPane.setPreferredSize(new Dimension(800, 600));
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(4, 4, new Insets(5, 5, 5, 5), -1, -1));
        mainPanel.setFocusCycleRoot(true);
        mainPanel.setMinimumSize(new Dimension(1000, 700));
        mainPanel.setPreferredSize(new Dimension(1000, 700));
        tabbedPane.addTab("Untitled", mainPanel);
        mainPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(), null));
        topPanel = new JPanel();
        topPanel.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(topPanel, new GridConstraints(0, 0, 1, 4, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, new Dimension(-1, 25), 0, false));
        labelTitle = new JLabel();
        labelTitle.setHorizontalTextPosition(0);
        labelTitle.setText("TITLE GOES HERE");
        topPanel.add(labelTitle, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(bottomPanel, new GridConstraints(3, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(800, 600), new Dimension(800, 600), null, 0, false));
        tabbedPane1 = new JTabbedPane();
        bottomPanel.add(tabbedPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(900, 600), null, 0, false));
        buttonData = new JPanel();
        buttonData.setLayout(new GridLayoutManager(18, 3, new Insets(0, 0, 0, 0), -1, -1));
        buttonData.setMinimumSize(new Dimension(1000, 736));
        buttonData.setPreferredSize(new Dimension(1000, 736));
        tabbedPane1.addTab("Validate", buttonData);
        labelXML = new JLabel();
        labelXML.setFont(new Font(labelXML.getFont().getName(), Font.ITALIC, labelXML.getFont().getSize()));
        labelXML.setText("...");
        buttonData.add(labelXML, new GridConstraints(1, 1, 1, 2, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, new Dimension(-1, 50), 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new BorderLayout(0, 0));
        buttonData.add(panel1, new GridConstraints(3, 2, 15, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(600, 400), new Dimension(600, 500), null, 0, false));
        textPane1 = new scrollableEditorPane();
        textPane1.setPreferredSize(new Dimension(590, 410));
        panel1.add(textPane1, BorderLayout.WEST);
        final JLabel label1 = new JLabel();
        label1.setFont(new Font(label1.getFont().getName(), Font.BOLD, label1.getFont().getSize()));
        label1.setText("Results");
        buttonData.add(label1, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonRun = new JButton();
        buttonRun.setText("Run");
        buttonData.add(buttonRun, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, 1, 1, null, null, null, 0, false));
        refreshValidationCacheButton = new validationFileFetcher();
        buttonData.add(refreshValidationCacheButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        buttonData.add(panel2, new GridConstraints(17, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(24, -1), null, 0, false));
        buttonUpload = new JButton();
        buttonUpload.setEnabled(false);
        buttonUpload.setText("Upload to Database");
        buttonData.add(buttonUpload, new GridConstraints(10, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonUploadFT = new JButton();
        buttonUploadFT.setEnabled(false);
        buttonUploadFT.setText("Upload to Fusion Table");
        buttonData.add(buttonUploadFT, new GridConstraints(11, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("");
        buttonData.add(label2, new GridConstraints(17, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(18, -1), null, 0, false));
        resultsPanel = new JPanel();
        resultsPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        resultsPanel.setBackground(new Color(-1));
        buttonData.add(resultsPanel, new GridConstraints(3, 0, 3, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        buttonValidation = new JButton();
        buttonValidation.setText("View Messages");
        buttonData.add(buttonValidation, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), new Dimension(200, -1), 0, false));
        buttonViewData = new JButton();
        buttonViewData.setText("View Data");
        buttonData.add(buttonViewData, new GridConstraints(9, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), new Dimension(150, -1), 0, false));
        radioButtonRow = new JRadioButton();
        radioButtonRow.setSelected(true);
        radioButtonRow.setText("Display (and sort by) all rows");
        buttonData.add(radioButtonRow, new GridConstraints(7, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 2, false));
        radioButtonMessage = new JRadioButton();
        radioButtonMessage.setText("Display unique messages only");
        buttonData.add(radioButtonMessage, new GridConstraints(8, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 2, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("PhotoMatcher", panel3);
        final JLabel label3 = new JLabel();
        label3.setText(" The PhotoMatcher enables you to rename photos by associating them with records from your validated spreadsheeet.");
        panel3.add(label3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(panel4, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JSplitPane splitPane1 = new JSplitPane();
        panel4.add(splitPane1, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(200, 200), null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(5, 5, new Insets(0, 0, 0, 0), -1, -1));
        splitPane1.setLeftComponent(panel5);
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel5.add(panel6, new GridConstraints(4, 0, 1, 5, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        pImageDisplay = new panelImageDisplay();
        panel6.add(pImageDisplay, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, new Dimension(480, 480), new Dimension(480, 480), new Dimension(480, 480), 0, false));
        final JLabel label4 = new JLabel();
        label4.setFont(new Font(label4.getFont().getName(), Font.BOLD, label4.getFont().getSize()));
        label4.setText("Ctrl-click on image to add it to the Specimen Image Browser");
        panel5.add(label4, new GridConstraints(1, 0, 1, 5, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonPrev = new JButton();
        buttonPrev.setText("Prev");
        panel5.add(buttonPrev, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(80, -1), new Dimension(80, -1), 0, false));
        pImageBrowser = new panelImageBrowser();
        panel5.add(pImageBrowser, new GridConstraints(3, 0, 1, 5, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(500, 85), null, null, 0, false));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel5.add(panel7, new GridConstraints(0, 0, 1, 5, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        labelImageDirectory = new JLabel();
        labelImageDirectory.setFont(new Font(labelImageDirectory.getFont().getName(), Font.ITALIC, labelImageDirectory.getFont().getSize()));
        labelImageDirectory.setText(" ...");
        panel7.add(labelImageDirectory, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(350, -1), null, 0, false));
        buttonImageDirectory = new JButton();
        buttonImageDirectory.setText("Input Directory");
        panel7.add(buttonImageDirectory, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(170, -1), new Dimension(170, -1), 0, false));
        refreshThumbnailCacheCheckBox = new JCheckBox();
        refreshThumbnailCacheCheckBox.setText("Force creation of new thumbnails on image load");
        panel7.add(refreshThumbnailCacheCheckBox, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        buttonNext = new JButton();
        buttonNext.setText("Next");
        panel5.add(buttonNext, new GridConstraints(2, 2, 1, 3, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, new Dimension(80, -1), 0, false));
        labelImagesDisplayed = new JLabel();
        labelImagesDisplayed.setText("...");
        panel5.add(labelImagesDisplayed, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        specimenMatchPanel = new JPanel();
        specimenMatchPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        specimenMatchPanel.setBackground(new Color(-1118482));
        splitPane1.setRightComponent(specimenMatchPanel);
        panelSpecimenImageDrop = new JPanel();
        panelSpecimenImageDrop.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panelSpecimenImageDrop.setBackground(new Color(-1118482));
        specimenMatchPanel.add(panelSpecimenImageDrop, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, 1, 1, null, null, null, 0, false));
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel8.setBackground(new Color(-1118482));
        panelSpecimenImageDrop.add(panel8, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, 1, 1, null, null, null, 0, false));
        matcherSpecimenDisplayController = new specimenDisplayController();
        panel8.add(matcherSpecimenDisplayController.$$$getRootComponent$$$(), new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        pSpecimenImageBrowser = new panelImageBrowser();
        pSpecimenImageBrowser.setBackground(new Color(-1118482));
        panelSpecimenImageDrop.add(pSpecimenImageBrowser, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(-1, 165), new Dimension(-1, 165), new Dimension(-1, 165), 0, false));
        labelPM = new JPanel();
        labelPM.setLayout(new GridLayoutManager(4, 3, new Insets(0, 0, 0, 0), -1, -1));
        labelPM.setEnabled(true);
        tabbedPane1.addTab("PhotoLoader", labelPM);
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        labelPM.add(panel9, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(400, -1), null, null, 0, false));
        panelImageViewContainer = new JPanel();
        panelImageViewContainer.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel9.add(panelImageViewContainer, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panelImageView = new panelImageDisplay();
        panelImageViewContainer.add(panelImageView, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(500, 500), new Dimension(500, 500), new Dimension(500, 500), 0, false));
        panelSpecimenViewContainer = new JPanel();
        panelSpecimenViewContainer.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel9.add(panelSpecimenViewContainer, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panelSpecimenView = new tinySpreadsheetViewer();
        panelSpecimenViewContainer.add(panelSpecimenView, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel10 = new JPanel();
        panel10.setLayout(new GridLayoutManager(6, 3, new Insets(0, 0, 0, 0), -1, -1));
        labelPM.add(panel10, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel10.add(spacer1, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel10.add(spacer2, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setFont(new Font(label5.getFont().getName(), Font.BOLD, label5.getFont().getSize()));
        label5.setText("The features below are designed to load images into Flickr but do not yet integrate with the Biocode Database");
        panel10.add(label5, new GridConstraints(2, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setFont(new Font(label6.getFont().getName(), Font.BOLD, label6.getFont().getSize()));
        label6.setText("The PhotoManager will load data into Biocode Database and post images to CalPhotos");
        panel10.add(label6, new GridConstraints(0, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        launchPhotoManagerButton = new JButton();
        launchPhotoManagerButton.setLabel("Biocode PhotoManager");
        launchPhotoManagerButton.setText("Biocode PhotoManager");
        panel10.add(launchPhotoManagerButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(60, -1), null, 0, false));
        loadPhotosButton = new JButton();
        loadPhotosButton.setText("Select Photo Directory");
        panel10.add(loadPhotosButton, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, 1, GridConstraints.SIZEPOLICY_FIXED, new Dimension(170, -1), new Dimension(170, -1), new Dimension(170, -1), 0, false));
        labelLoadPhotos = new JLabel();
        labelLoadPhotos.setFont(new Font(labelLoadPhotos.getFont().getName(), Font.ITALIC, labelLoadPhotos.getFont().getSize()));
        labelLoadPhotos.setText(" ...");
        panel10.add(labelLoadPhotos, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("Select one or more matched photos before uploading:");
        panel10.add(label7, new GridConstraints(4, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonUploadToFlickr = new JButton();
        buttonUploadToFlickr.setEnabled(true);
        buttonUploadToFlickr.setText("Upload To Flickr");
        panel10.add(buttonUploadToFlickr, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, 1, GridConstraints.SIZEPOLICY_FIXED, new Dimension(150, -1), new Dimension(150, -1), new Dimension(170, -1), 0, false));
        panelphotolist = new photoListPanel();
        labelPM.add(panelphotolist, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(450, 500), new Dimension(450, -1), 0, false));
        final JPanel panel11 = new JPanel();
        panel11.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Help", panel11);
        final scrollableEditorPaneDesktop scrollableEditorPaneDesktop1 = new scrollableEditorPaneDesktop();
        panel11.add(scrollableEditorPaneDesktop1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        labelSpreadsheet = new JLabel();
        labelSpreadsheet.setFont(new Font(labelSpreadsheet.getFont().getName(), Font.ITALIC, labelSpreadsheet.getFont().getSize()));
        labelSpreadsheet.setText("...");
        mainPanel.add(labelSpreadsheet, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(800, -1), new Dimension(800, -1), null, 0, false));
        buttonSpreadsheet = new JButton();
        buttonSpreadsheet.setText("Load Spreadsheet");
        mainPanel.add(buttonSpreadsheet, new GridConstraints(1, 0, 1, 3, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(160, -1), null, 0, false));
        final JPanel panel12 = new JPanel();
        panel12.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(panel12, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, 1, null, null, null, 0, false));
        FieldHeadingsOnRow = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("1");
        defaultComboBoxModel1.addElement("2");
        defaultComboBoxModel1.addElement("3");
        defaultComboBoxModel1.addElement("4");
        defaultComboBoxModel1.addElement("5");
        defaultComboBoxModel1.addElement("6");
        defaultComboBoxModel1.addElement("7");
        defaultComboBoxModel1.addElement("8");
        FieldHeadingsOnRow.setModel(defaultComboBoxModel1);
        panel12.add(FieldHeadingsOnRow, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(60, -1), new Dimension(60, -1), 0, false));
        final JLabel label8 = new JLabel();
        label8.setText("Row that column headings are on");
        panel12.add(label8, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return tabbedPane;
    }
}



 
