package edu.berkeley.biocode.photoMatcher;

import edu.berkeley.biocode.bioValidator.mainPage;
import edu.berkeley.biocode.utils.directoryChooser;
import org.jdesktop.swingworker.SwingWorker;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;


/**
 * specimenThumbAction
 * panelImageBrowser is meant to be added as a component to the UI Designer.  It dynamically adds images
 */
public class panelImageBrowser extends JPanel implements PropertyChangeListener {

    // Model in a JList in a JScrollPane defines how to render thumbnails
    private final DefaultListModel model = new DefaultListModel();
    //private final myModel model = new myModel();
    imageRenderer renderer = new imageRenderer();
    public JList dataList = new JList();
    //public DataList dataList = new DataList();
    private JScrollPane scrollPane = new JScrollPane();// = new JScrollPane(this.dataList);
    public String imagedir = "";
    public int lastPhoto;
    public int firstPhoto = 1;
    public int allPhotos;
    //    private MissingIcon placeholderIcon = new MissingIcon();
    private ArrayList imageCaptions;// = {};
    public ArrayList imageFileNames;// = {};
    private JProgressBar progressBar = new JProgressBar();
    private JPanel pImageDisplay;
    private MouseListener mouseListener;
    private MouseListener largeImageMouseListener;

    // associated windows
    //private panelImageBrowser specimenThumbBrowser = null;
    //private panelSpecimenDisplay matcherSpecimenDisplay = null;
    private specimenDisplayController matcherSpecimenDisplayController = null;
    public ProgressMonitor progressMonitor;
    private addImageTask task;
    public int progress = 1;
    public int displayProgress = 1;
    public mainPage mainpage = null;
    Border selected = BorderFactory.createLineBorder(Color.black);
    Border empty = BorderFactory.createEmptyBorder();
    private JLabel oldSelectedLabel = null;
    private String browser = "";

    public panelImageBrowser() {
        // turn off tooltips to speed up performance
        ToolTipManager.sharedInstance().setEnabled(false);

    }


    /*
  Keep main from example that i took this from... interesting function invokeLater but i'm not sure what it does.
  public static void main(String args[]) {
      SwingUtilities.invokeLater(new Runnable() {
          public void run() {
              panelImageBrowser app = new panelImageBrowser();
              app.setVisible(true);
          }
      });
  }  */


    /**
     * Called when ready to render images in this panel.  The constructor is called by the UI designer
     * but we don't know parameters at that time.
     *
     * @param dc
     * @param pMainpage
     */
    public void initMainImageBrowser(directoryChooser dc,
                                     mainPage pMainpage) {
        this.browser = "thumb";

        System.out.println("initializing master thumb browser");
        mainpage = pMainpage;
        dataList.setModel(model);

        // set variables
        this.imagedir = dc.getDirectoryPath();
        this.pImageDisplay = mainpage.pImageDisplay;
        // Convert ArrayList to String[]
        this.imageFileNames = dc.getImageList();
        this.imageCaptions = dc.getImageList();

        if (matcherSpecimenDisplayController == null) {
            matcherSpecimenDisplayController = mainpage.matcherSpecimenDisplayController;
        }
        // capture thumbs and send to Specimen thumbs window since this was initialized with
        // a panelImageBrowser
        /*if (specimenThumbBrowser == null) {
            this.specimenThumbBrowser = mainpage.pSpecimenImageBrowser;
            specimenThumbBrowser.initSpecimenImageBrowser(imagedir, this.pImageDisplay, this.matcherSpecimenDisplayController);
        }
        this.matcherSpecimenDisplayController.initRulesSchema(specimenThumbBrowser);
        //this.matcherSpecimenDisplayController.browseSpreadsheet();
        */

        // Number of photos to display
        allPhotos = imageFileNames.size();
        lastPhoto = imageFileNames.size();
        if (imageFileNames.size() > Integer.getInteger("app.maxPhotos")) {
            lastPhoto = Integer.getInteger("app.maxPhotos");
        }
        firstPhoto = 1;


        // Clear interface
        wipeGui();

        //progressMonitor = new ProgressMonitor(this, "Creating thumbnail cache", "", 0, allPhotos);
        setProgressMonitor();
        progressMonitor.setMillisToPopup(2000);
        progressMonitor.setMillisToDecideToPopup(2000);
        task = new addImageTask(true);
        task.addPropertyChangeListener(this);
        task.execute();

        // Set mouselistener
        mouseListener = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 1) {
                    JLabel label = (JLabel) (dataList.getSelectedValue());
                    displayLargeImage(label.getToolTipText());
                    if ((e.isControlDown())) {
                        if (matcherSpecimenDisplayController.outputDirectory == null) {
                            JOptionPane.showMessageDialog(mainpage.pSpecimenImageBrowser, "Set output directory before adding images");
                        } else {
                            String imageName = label.getToolTipText();
                            System.out.println("control down, adding image " + imageName);
                            mainpage.pSpecimenImageBrowser.addImage(imageName, true, label);
                        }
                    }

                    try {
                        if (oldSelectedLabel == null) {
                            oldSelectedLabel = label;
                        }
                        oldSelectedLabel.setBorder(empty);
                    } catch (NullPointerException enull) {
                        enull.printStackTrace();
                    }
                    ((JLabel) (dataList.getSelectedValue())).setBorder(selected);
                    oldSelectedLabel = label;
                }
            }
        };
        largeImageMouseListener = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 1) {
                    JLabel label = (JLabel) (dataList.getSelectedValue());

                    //JLabel label = (JLabel) pImageDisplay.getComponent(0);
                    if ((e.isControlDown())) {
                        if (mainpage.matcherSpecimenDisplayController.outputDirectory == null) {
                            JOptionPane.showMessageDialog(mainpage.pSpecimenImageBrowser, "Set output directory before adding images");
                        } else {
                            String imageName = label.getToolTipText();
                            System.out.println("control down, adding image " + imageName);
                            mainpage.pSpecimenImageBrowser.addImage(imageName, true, label);
                        }
                    }
                }
            }
        };
        dataList.addMouseListener(mouseListener);

        // only add if it has not already been added
        if (mainpage.buttonNext.getActionListeners().length < 1) {
            mainpage.buttonNext.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    System.out.println("next button pressed");
                    wipeGui();
                    //firstPhoto = Integer.getInteger("app.maxPhotos") + firstPhoto;
                    firstPhoto++;
                    if (firstPhoto > allPhotos - Integer.getInteger("app.maxPhotos")) {
                        firstPhoto = allPhotos - Integer.getInteger("app.maxPhotos");
                        if (firstPhoto < 1) firstPhoto = 1;
                    }
                    //lastPhoto = Integer.getInteger("app.maxPhotos") + lastPhoto;
                    lastPhoto++;
                    if (lastPhoto > allPhotos) {
                        lastPhoto = allPhotos;
                    }

                    prevNextController();
                }
            });
            mainpage.buttonPrev.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    System.out.println("prev button pressed");
                    wipeGui();
                    //firstPhoto = firstPhoto - Integer.getInteger("app.maxPhotos");
                    firstPhoto--;
                    if (firstPhoto < 1) {
                        firstPhoto = 1;
                    }
                    //lastPhoto = lastPhoto - Integer.getInteger("app.maxPhotos");
                    lastPhoto--;
                    if (lastPhoto < Integer.getInteger("app.maxPhotos")) {
                        lastPhoto = Integer.getInteger("app.maxPhotos");
                    }
                    if (lastPhoto > allPhotos) lastPhoto = allPhotos;
                    prevNextController();
                }
            });
        }

    }

    /**
     * Wipes interface, calls addImageTask and sets propertychangelistener for this event
     */
    private void prevNextController() {
        System.out.println("here in prevnextcontroller");
        wipeGui();
        dataList.addMouseListener(mouseListener);

        final addImageTask task = new addImageTask(false);
        //task.addPropertyChangeListener(this);
        task.execute();
    }

    /**
     * initialize to add images just one at a time
     *
     * @param imageDir
     * @param imageLargePanel
     */
    public void initSpecimenImageBrowser(String imageDir, JPanel imageLargePanel, specimenDisplayController pMatcherSpecimenDisplayController) {
        System.out.println("initializing specimen thumb browser");
        this.browser = "specimen";
        dataList.setModel(model);

        // set variables
        this.imagedir = imageDir;
        this.pImageDisplay = imageLargePanel;
        wipeGui();

        updateScrollPane();
        this.matcherSpecimenDisplayController = pMatcherSpecimenDisplayController;
        // set mouselistener
        mouseListener = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                System.out.println("mouse clicked on image");
                JLabel label = (JLabel) (dataList.getSelectedValue());
                if ((e.isControlDown())) {
                    removeImage(label, true);
                } else {
                    displayLargeImage(label.getToolTipText());
                }
            }
        };
        dataList.addMouseListener(mouseListener);
    }

    /**
     * Given a specimen_num_collector value, look in currently selected Image Directory
     * and call addImage to populate image thumbs
     *
     * @param specimen_num_collector
     * @param outputDir
     */
    public void addMatchingImages(String specimen_num_collector, String outputDir) throws NullPointerException {
        PhotoNameParser p = new PhotoNameParser();
        // Look in imagedir and get ALL imagepath names
        File f = new File(outputDir);

        // Weed out all junk in directory and only go 1 directory deep (not recursive)
        ImageFilter imageFilter = new ImageFilter();

        for (int i = 0; i < f.list().length; i++) {
            String filename = (f.list()[i]);
            if (imageFilter.accept(new File(filename))) {
                // If specimen_num_collector is found in the filename, then go ahead
                // and add the image to the pane
                if (p.compareName(specimen_num_collector, filename)) {
                    JLabel label = createImage(outputDir + filename);
                    if (!label.equals(null)) {
                        addImage(outputDir + filename, true, label);
                    } else {
                        i = f.list().length;
                    }
                }
            }
        }
    }

    public void addImage(String fullPath, boolean updatescrollpane, JLabel label) {
        // add to the dataList
        dataList.add(label);
        model.addElement(label);
        if (updatescrollpane) {
            updateScrollPane();
        }
    }

    public JLabel createImage(String fullPath) {
        ScaledImageIcon thumbnailIcon = null;
        try {
            thumbnailIcon = new ScaledImageIcon(fullPath, System.getProperty("app.thumbsDirectory"));
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Unable to save image " + fullPath);
        }

        if (!thumbnailIcon.saveSuccess) {
            return null;
        } else {
            JLabel label = new JLabel();
            label.setIcon(thumbnailIcon);
            label.setToolTipText(fullPath);

            return label;
        }
    }

    public void removeAllImages() {
        dataList.removeAll();
        model.removeAllElements();
    }

    private void removeImage(JLabel label, boolean updatescrollpane) {
        System.out.println("remove Image for " + label.getToolTipText());

        // only allow removing images that have NOT been renamed!
        PhotoNameParser p = new PhotoNameParser();
        if (!p.compareName(matcherSpecimenDisplayController.getPhotoField(), label.getToolTipText())) {
            dataList.remove(label);
            model.removeElement(label);

            if (updatescrollpane) {
                updateScrollPane();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Unable to remove an image that has already\n" +
                    "been named according to this specimen. \n" +
                    "You must rename the image manually in your computer\n" +
                    "and then return to this specimen record");
        }
    }

    /**

     */
    public void wipeGui() {
        model.removeAllElements();
        dataList.removeAll();
        dataList.removeMouseListener(mouseListener);
        if (dataList.isVisible()) {
            this.remove(dataList);
        }
        if (scrollPane.isVisible()) {
            this.remove(scrollPane);
        }
        if (pImageDisplay.isVisible()) {
            this.remove(pImageDisplay);
        }
        try {
            if (mainpage.pSpecimenImageBrowser != null) {
                if (mainpage.pSpecimenImageBrowser.isVisible()) {
                    this.remove(mainpage.pSpecimenImageBrowser);
                }
            }
        } catch (NullPointerException e) {

        }

    }

    public void displayLargeImage(final String imageName) {
        JLabel label = new JLabel();

        label.setVerticalTextPosition(JLabel.BOTTOM);
        label.setHorizontalTextPosition(JLabel.CENTER);
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

        label.setIcon(new ScaledImageIcon(imageName, 500, 450));
        label.setText(imageName);
        label.setToolTipText(imageName);

        pImageDisplay.removeAll();
        pImageDisplay.add(label);
        pImageDisplay.updateUI();

        label.addMouseListener(largeImageMouseListener);
    }


    private int setVisibleRows(int totalElements, int numCols) {
        int res = totalElements / numCols;
        return (int) Math.floor(res + 0.5f) + 1;
    }

    public void updateScrollPane() {
        // First remove the scrollpane
        this.remove(scrollPane);
        System.out.println("updating scrollpane");

        // Setup Viewing environment for our JList
        try {
            dataList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
            int visibleRowWidth = this.getWidth() / Integer.getInteger("app.thumbnailWidth");
            //dataList.setVisibleRowCount(setVisibleRows(dataList.getModel().getSize(), visibleRowWidth));
            if (this.browser.equals("specimen")) {
                dataList.setVisibleRowCount(2);
            } else {
                dataList.setVisibleRowCount(1);
            }

            dataList.setCellRenderer(renderer);

            scrollPane.setViewportView(dataList);
            scrollPane.setBackground(Color.WHITE);

            if (this.browser.equals("specimen")) {
                scrollPane.setPreferredSize(new Dimension(480, 240));
                scrollPane.setMinimumSize(new Dimension(480, 240));
            }

            scrollPane.setAutoscrolls(false);
            scrollPane.setHorizontalScrollBarPolicy(new JScrollPane().HORIZONTAL_SCROLLBAR_NEVER);
            scrollPane.setVerticalScrollBarPolicy(new JScrollPane().VERTICAL_SCROLLBAR_NEVER);

            scrollPane.setBackground(dataList.getBackground());
            scrollPane.invalidate();
            scrollPane.validate();
            scrollPane.revalidate();
            scrollPane.updateUI();

            // Last add scrollpane back to our Component
            this.add(scrollPane);
            this.updateUI();
        } catch (Exception e) {
            System.out.println("ERROR IN SCROLLPANE INITIALIZATION");
            e.printStackTrace();
        }

    }
    private void setProgressMonitor() {
        progressMonitor = new ProgressMonitor(this, "Creating thumbnail cache", "", 0, allPhotos);

    }

    public void propertyChange(PropertyChangeEvent evt) {
        System.out.println("propertyChanged!");
        if ("progress" == evt.getPropertyName()) {
            if (progressMonitor.isCanceled() || task.isDone()) {
                if (progressMonitor.isCanceled()) {
                    try {
                        mainpage.labelImagesDisplayed.setText("Cancel detected, finishing up ...");
                        Thread.currentThread().sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                    task.cancel(true);
                }
            }
        }
    }

    private class imageRenderer extends JLabel implements ListCellRenderer {
        /**
         * finds the image and text corresponding
         * to the selected value and returns the label, set up
         * to display the text and image.
         *
         * @param list
         * @param value
         * @param index
         * @param isSelected
         * @param cellHasFocus
         * @return
         */

        public Component getListCellRendererComponent(
                JList list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {
            this.setIcon(((JLabel) value).getIcon());

            //this.setToolTipText(((JLabel) value).getToolTipText());
            //System.out.println("painting image " + index + " (" + ((JLabel) value).getToolTipText() + ")");
            return this;
        }


        public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
        }

        public void firePropertyChange(String propertyName, byte oldValue, byte newValue) {
        }

        public void firePropertyChange(String propertyName, char oldValue, char newValue) {
        }

        public void firePropertyChange(String propertyName, double oldValue, double newValue) {
        }

        public void firePropertyChange(String propertyName, float oldValue, float newValue) {
        }

        public void firePropertyChange(String propertyName, int oldValue, int newValue) {
        }

        public void firePropertyChange(String propertyName, long oldValue, long newValue) {
        }

        protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        }

        public void firePropertyChange(String propertyName, short oldValue, short newValue) {
        }

        public void repaint(long tm, int x, int y, int width, int height) {
        }

        public void repaint(Rectangle r) {
        }

        public void revalidate() {
        }

        public void validate() {
        }
    }

    class addImageTask extends SwingWorker<Void, Void> {
        private boolean createImage = true;
        private final String prReallyDone = "ReallyDone";

        addImageTask(boolean pCreateImage) {
            createImage = pCreateImage;
            getPropertyChangeSupport().addPropertyChangeListener(prReallyDone,
                    new PropertyChangeListener() {
                        public void propertyChange(PropertyChangeEvent e) {
                            if (e.getNewValue().equals(true)) {
                                whenReallyDone();
                            }
                        }
                    });

        }

        @Override
        public Void doInBackground() {

            String outputDirectory = imagedir +
                    File.separator +
                    System.getProperty("app.thumbsDirectory") +
                    File.separator;
            String rootDirectory = imagedir;

            ListDataListener[] listeners = model.getListDataListeners();
            for (ListDataListener listener : listeners) {
                model.removeListDataListener(listener);
            }
            while (progress <= allPhotos && !progressMonitor.isCanceled()) {
                String filename = (String) imageFileNames.get(progress - 1);
                String outputFile = outputDirectory + filename;
                String inputFile = rootDirectory + filename;

                File outputfile = new File(outputFile);
                // Has image file already been written to?
                // If yes, just open it
                JLabel label = null;
                if (!createImage || (outputfile.exists() &&
                        !mainpage.refreshThumbnailCacheCheckBox.isSelected())) {
                    ImageIcon icon = new ImageIcon(outputFile);
                    label = new JLabel();
                    label.setIcon(icon);
                    // The input file gets set as the tooltip text here & NOT the thumbnail
                    // because this is the pointer to the real image
                    label.setToolTipText(inputFile);

                    addImageCaller(outputFile, label);

                    // If no, make icon & then save it
                } else {
                    label = createImage(inputFile);
                    addImageCaller(outputFile, label);
                    progressMonitor.setProgress(progress);
                    progressMonitor.setNote(progress + " of " + allPhotos);
                }
                progress++;
                


            }

            ListDataEvent evt = new ListDataEvent(model, ListDataEvent.INTERVAL_ADDED, 0, dataList.getModel().getSize() - 1);
            for (ListDataListener listener : listeners) {
                model.addListDataListener(listener);
                listener.intervalAdded(evt);
            }

            firePropertyChange(prReallyDone, false, true);

            return null;
        }

        // the done method for task has a bug in that it fires before the while loop in
        // doInBackground is actually done, leading to improperly closed files
        // This method is a workaround as it is called when the propertychange event is fired
        // after the while loop exits.
        private void whenReallyDone() {

            dataList.setVisible(true);
            setProgressMonitor();
            updateScrollPane();
            updateUI();
            progress = 1;
            mainpage.labelImagesDisplayed.setText(
                    "Displaying " +
                            firstPhoto +
                            " to " +
                            lastPhoto +
                            " of " +
                            allPhotos +
                            " images");
        }

        private void addImageCaller(String file, JLabel label) {
            if (progress >= firstPhoto && progress <= lastPhoto) {
                addImage(file, false, label);
            }
        }

    }
}

/*
class MissingIcon implements Icon {

    private int width = 32;
    private int height = 32;

    private BasicStroke stroke = new BasicStroke(4);

    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2d = (Graphics2D) g.create();

        g2d.setColor(Color.WHITE);
        g2d.fillRect(x + 1, y + 1, width - 2, height - 2);

        g2d.setColor(Color.BLACK);
        g2d.drawRect(x + 1, y + 1, width - 2, height - 2);

        g2d.setColor(Color.RED);

        g2d.setStroke(stroke);
        g2d.drawLine(x + 10, y + 10, x + width - 10, y + height - 10);
        g2d.drawLine(x + 10, y + height - 10, x + width - 10, y + 10);

        g2d.dispose();
    }

    public int getIconWidth() {
        return width;
    }

    public int getIconHeight() {
        return height;
    }

}
*/




