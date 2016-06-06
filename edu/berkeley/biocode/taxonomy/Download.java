package edu.berkeley.biocode.taxonomy;

import javax.swing.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Observable;

// This class downloads a file from a URL.
public class Download extends Observable {

    // Max size of download buffer.
    private static final int MAX_BUFFER_SIZE = 1024;

    // These are the status names.
    public static final String STATUSES[] = {"Downloading",
            "Paused", "Complete", "Cancelled", "Error"};

    // These are the status codes.
    public static final int DOWNLOADING = 0;
    public static final int PAUSED = 1;
    public static final int COMPLETE = 2;
    public static final int CANCELLED = 3;
    public static final int ERROR = 4;

    private URL url; // download URL
    private int size; // size of download in bytes
    private int downloaded; // number of bytes downloaded
    private int status; // current status of download
    public static ProgressMonitor pm;
    private ProgressMonitorInputStream pmis;

    //private ProgressMonitor p;
    // Constructor for Download.
    public Download(URL url) {
        pmis = null;
        pm = null;
        //  this.p = p;
        this.url = url;
        size = -1;
        downloaded = 0;
        //status = DOWNLOADING;

        // Begin the download.
        //download();
    }

    //public void setProgressMonitor(ProgressMonitor p) {
    //  this.p = p;
    //}// Get this download's URL.
    public String getUrl() {
        return url.toString();
    }

    // Get this download's size.
    public int getSize() {
        return size;
    }

    // Get this download's progress.
    public float getProgress() {
        return ((float) downloaded / size) * 100;
    }

    // Get this download's status.
    public int getStatus() {
        return status;
    }

    // Mark this download as having an error.
    private void error() {
        System.out.println("Error!");
        status = ERROR;
        stateChanged();
    }

    // Get file name portion of URL.
    private String getFileName(URL url) {
        String fileName = url.getFile();
        return fileName.substring(fileName.lastIndexOf('/') + 1);
    }

    // Download file.
    public void run() {
        File file = null;
        BufferedInputStream stream = null;
        BufferedOutputStream outStream = null;
        String filename = null;
        try {
            //System.out.println("open URL connection");
            // Open connection to URL.
            HttpURLConnection connection =
                    (HttpURLConnection) url.openConnection();

            // Specify what portion of file to download.
            connection.setRequestProperty("Range",
                    "bytes=" + downloaded + "-");

            // Connect to server.
            connection.connect();

            //System.out.println("checking response code");
            // Make sure response code is in the 200 range.
            if (connection.getResponseCode() / 100 != 2) {
                error();
            }

            //System.out.println("checking content length");
            // Check for valid content length.
            int contentLength = connection.getContentLength();
            if (contentLength < 1) {
                error();
            }

            //System.out.println("checking size");
            /* Set the size for this download if it
        hasn't been already set. */
            if (size == -1) {
              //  System.out.println("setting size");
                size = contentLength;
                //System.out.println("changing state from size");
                stateChanged();
            }
            //System.out.println("opening file");
            // Open file and seek to the end of it.
            filename = "index" + File.separatorChar + getFileName(url);
            file = new File(filename);

            pmis = new ProgressMonitorInputStream(
                    null,
                    "Downloading " + getFileName(url),
                    connection.getInputStream());
            pm = pmis.getProgressMonitor();
            pm.setMaximum(connection.getContentLength());
            pm.setMillisToDecideToPopup(0);
            pm.setMillisToPopup(0);
            

            status = DOWNLOADING;
            stream = new BufferedInputStream(pmis);
            outStream = new BufferedOutputStream(new FileOutputStream(file));
            int data;
            //System.out.println("reading/writing stream now");
            while ((data = stream.read()) >= 0) {
                outStream.write(data);
                downloaded += data;

                stateChanged();
            }
            /* Change status to complete if this point was
       reached because downloading has finished. */
            if (status == DOWNLOADING) {
                stateChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
            error();
        } finally {
            System.out.println("closing " + file);
            
            // Close file.
            if (file != null) {
                try {
                    outStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // Close connection to server.
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                System.out.println("closing pmis");
                pmis.close();
                System.out.println("closing pm");                
                pm.close();
                status = 100;
                
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }


        }
    }

    // Notify observers that this download's status has changed.
    private void stateChanged() {
        setChanged();
        notifyObservers();
    }
}