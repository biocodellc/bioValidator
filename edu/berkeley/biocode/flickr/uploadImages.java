package edu.berkeley.biocode.flickr;


import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.uploader.UploadMetaData;
import edu.berkeley.biocode.bioValidator.mainPage;
import edu.berkeley.biocode.photoMatcher.photoDetail;
import edu.berkeley.biocode.photoMatcher.photoListModel;
import edu.berkeley.biocode.photoMatcher.photoListPanel;
import edu.berkeley.biocode.utils.ClientHttpRequest;
import edu.berkeley.biocode.validator.Constants;
import edu.berkeley.biocode.validator.setProperties;
import org.jdesktop.swingworker.SwingWorker;
import org.xml.sax.SAXException;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.prefs.Preferences;

public class uploadImages implements PropertyChangeListener {

    public ProgressMonitor progressmonitor = null;
    uploadTask task;
    int progress = 0;


    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {

        setProperties sp = new setProperties();


/*

      uploadImages u = new uploadImages();
      // Just for testing!
      u.uploadImage(new File("/Users/biocode/testoutput/.bvthumbs/John Deck1+DSC_9146.JPG"), "John Deck1");
*/
    }

    public uploadImages(photoListPanel plp, String rootDirectory) {

        photoListModel plm = plp.photostablemodel;

        // Get the user selected Rows
        int selectedRows[] = plp.table  .getSelectedRows();

        // Count the total number of matched specimens
        String message = "";
        int totalrows = plm.getRowCount();
        int totalmatchedspecimens = 0;
        System.out.println("here");
        for (int i = 0; i < selectedRows.length; i++) {
            String specimen = plm.getValueAt(selectedRows[i], 1).toString();
            if (!specimen.equals(Constants.nomatch) &&
                    !specimen.equals("") &&
                    specimen != null) {
                totalmatchedspecimens++;
            }
        }

        // User input/feedback
        int result = Constants.CONTINUE;
        if (totalmatchedspecimens == 0) {

            JOptionPane.showMessageDialog(null, "Nothing to load!\n Either none of your selected images had a match,\n you did not select an option to Match Specimen Photos,\n or did not select any photos to load.");
            result = Constants.CANCEL;
        } else if (totalmatchedspecimens < selectedRows.length) {

            message = "Not all pictures have a specimen name match!  \n" +
                    "Click OK to continue loading to Flickr anyway";
            result = JOptionPane.showOptionDialog(null,
                    message,
                    "",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    Constants.OPTIONSOK,
                    Constants.OPTIONSOK[Constants.OK]);
        }

        // Run Upload as background process
        if (result == Constants.CONTINUE) {

            progressmonitor = new ProgressMonitor(null, "Flickr Uploader", "Count progress", 0, selectedRows.length + 1);
            progressmonitor.setMillisToPopup(0);
            progressmonitor.setMillisToDecideToPopup(0);

            task = new uploadTask(plp, rootDirectory);
            task.addPropertyChangeListener(this);
            task.execute();
        }


    }

    public uploadImages() {

    }

    private void uploadImage(File file, photoDetail pd) throws Exception, NoSuchAlgorithmException {
        AuthorizeFlickrUpload authorizedUpload = null;
        try {
            authorizedUpload = new AuthorizeFlickrUpload();

            //authorizedUpload.init();

        } catch (javax.xml.parsers.ParserConfigurationException e) {
            e.printStackTrace();
        } catch (org.xml.sax.SAXException e) {
            e.printStackTrace();
        }

        String title = file.getName();

        String photo = file.getAbsolutePath();
        String description = "Uploaded using bioValidator";

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.ENGLISH);
        java.util.Date date = new java.util.Date();
        String dateStr = dateFormat.format(date);
        try {
            Date date2 = dateFormat.parse(dateStr);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }

        if (System.getProperty("app.version") != null) {
            description += " version " + System.getProperty("app.version");
        }
        List<String> tags = new ArrayList<String>();
        tags.add("bioValidator:field=" + "\"" + pd.getSpecimen() + "\"");
        tags.add("bioValidator:file=" + "\"" + file.getName() + "\"");
        tags.add("bioValidator:date=" + dateStr);
        tags.add(pd.getTaxonomy_binomial());
        tags.add(pd.getGeo_lat());
        tags.add(pd.getGeo_lon());
        //tags.add("dwc:recordedby=\"Eric Chenin\"");


        // Get photo ready for loading
        InputStream in = new FileInputStream(photo);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int i;
        byte[] buffer = new byte[1024];
        while ((i = in.read(buffer)) != -1) {
            out.write(buffer, 0, i);
        }
        in.close();
        byte data[] = out.toByteArray();

        // Perform upload
        try {
            UploadMetaData uploadMetaData = new UploadMetaData();
            uploadMetaData.setTitle(title);
            uploadMetaData.setDescription(description);
            uploadMetaData.setTags(tags);
            uploadMetaData.setAsync(false);
            uploadMetaData.setPublicFlag(true);
            uploadMetaData.setHidden(true);


            String ID = authorizedUpload.up.upload(data, uploadMetaData);
            // TODO: Add the Flickr ID to the PhotoDetail (so i can look it up later if needed)
            System.out.println("Flicker ID=" + ID);


        } catch (FlickrException e) {
            e.printStackTrace();
        }


    }

    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    class uploadTask extends SwingWorker<Void, Void> {
        photoListModel plm = null;
        photoListPanel plp = null;
        String rootDirectory = null;
        // List of photoDetails for uploaded photos
        ArrayList pdArr = new ArrayList();


        int numLoaded = 0;
        int totalAttempted = 0;

        uploadTask(photoListPanel pPLP, String pRootDirectory) {
            plp = pPLP;
            plm = plp.photostablemodel;
            rootDirectory = pRootDirectory;
        }

        @Override
        public Void doInBackground() {
            final Preferences prefs = Preferences.userRoot().node(System.getProperty("app.bioValidatorUserPreferences"));

            progressmonitor.setNote("initializing flickr uploader");
            progressmonitor.setProgress(progress++);

            int rowstoLoad = 0;
            int selectedRows[] = plp.table.getSelectedRows();

            // Figure out number of rows we're actually loading
            for (int i = 0; i < selectedRows.length; i++) {
                String specimen = plm.getValueAt(selectedRows[i], 1).toString();
                if (!specimen.equals(Constants.nomatch) &&
                        !specimen.equals("") &&
                        specimen != null) {
                    rowstoLoad++;
                }
            }
            int i = 0;
            int count = selectedRows.length;
            // Loop rows and load to Flickr
            while (!progressmonitor.isCanceled() && i < count) {

                String filename = plm.getValueAt(selectedRows[i], 0).toString();
                String absolutefilename = rootDirectory + filename;
                String specimen = plm.getValueAt(selectedRows[i], 1).toString();

                String binomial = "",  lat = "", lon = "";
                if (!specimen.equals(Constants.nomatch) &&
                        !specimen.equals("") &&
                        specimen != null) {

                    progressmonitor.setNote("Loading " + filename + "\n(" + progress + " of " + rowstoLoad + ")");
                    progressmonitor.setProgress(progress++);

                    totalAttempted++;
                    System.out.println("Loading this to flickr: " + absolutefilename + ":" + specimen);

                    // Upload image
                    boolean success = true;
                    try {

                        photoDetail pd = new photoDetail();
                        pd.setAbsoluteFileName(absolutefilename);
                        pd.setSpecimen(specimen);
                        pd.setGeo_lat(lat);
                        pd.setGeo_lon(lon);
                        pd.setTaxonomy_binomial(binomial);

                        pdArr.add(pd);

                        uploadImage(new File(absolutefilename), pd);


                    } catch (Exception e) {
                        success = false;
                        e.printStackTrace();
                    }

                    // Write out that this image has been upload to Flickr already
                    if (success) {

                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);
                        java.util.Date date = new java.util.Date();
                        String dateStr = dateFormat.format(date);
                        // TODO: column #2 is hard-coded... maybe put this as constant?
                        plm.setValueAt("loaded " + dateStr, selectedRows[i], 2);
                        prefs.put(filename, "loaded " + dateStr);

                        numLoaded++;
                    }

                    // TODO: detect over-write on Flickr Side??
                }
                i++;
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    done();
                }
                if (progressmonitor.isCanceled()) {
                    done();
                    return null;
                }
            }
            return null;
        }

        /**
         * done is called when the spreadsheet has finished loading
         */
        @Override
        public void done() {

            String message = "";

            if (progressmonitor.isCanceled()) {
                message = "Upload cancelled, not all images may have been loaded.";
                JOptionPane.showMessageDialog(null, message);
            } else {
                progress = 0;
                progressmonitor.setProgress(progress);
                progressmonitor.close();

                if (numLoaded == 0) {
                    message = "Did not load any images!";
                } else if (numLoaded == totalAttempted) {
                    message = "Succesfully loaded all images! \nPlease double check images at your Flickr Account";
                } else if (numLoaded == 0 && totalAttempted > 0) {
                    message = "Unable to load any images!\n Are you connected to the internet?";
                } else if (numLoaded == 0 && totalAttempted == 0) {
                    message = "No images to load?";
                } else {
                    message = "Attempted loading " + totalAttempted + " but only loaded " + numLoaded + " images";
                }
                JOptionPane.showMessageDialog(null, message);
            }

            // Send email message that this file was loaded
            for (int i = 0; i < pdArr.size(); i++) {
                photoDetail pd = (photoDetail) pdArr.get(i);
                message += "\n****************************\n";
                message += "Absolute Filename = " + pd.getAbsoluteFileName() + "  (specimen = " + pd.getSpecimen() + ")\n";
            }
            String premessage = "**************************\n";
            premessage += "This is just an informational message that a bioValidator user has loaded,\n";
            premessage += "or attempted to load image(s) using the Flickr load function.\n";
            premessage += "These photos will be harvested using an online script as well with full metadata\n";
            premessage += "**************************\n";

            InputStream serverInput = null;
            String response = "";
            try {
                serverInput = ClientHttpRequest.post(
                        new URL(System.getProperty("app.emailScriptNoticeFlickrUpload")),
                        new Object[]{
                                "message", premessage + message
                        });
            } catch (IOException e1) {
                e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            try {
                response = convertStreamToString(serverInput).toString();
            } catch (IOException e1) {
                e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            System.out.println("Message = " + premessage + message);
            System.out.println("Response = " + response);

        }
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

}


