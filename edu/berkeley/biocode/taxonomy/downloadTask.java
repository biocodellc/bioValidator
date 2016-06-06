package edu.berkeley.biocode.taxonomy;

import org.jdesktop.swingworker.SwingWorker;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class downloadTask {
    Download d;
    //ProgressMonitor p;
    ProgressMonitor pmzip;
    JComboBox taskCombo;

    public downloadTask(URL url, JComboBox taskCombo) {
        this.d = null;
        this.taskCombo = taskCombo;
        d = new Download(url);

        downloadTaskExecute task = new downloadTaskExecute();
        task.execute();

        int count = 0;
        while (d.getProgress() < 100) {
            try {
                if (d.pm.isCanceled()) {
                    break;
                } else {
                    d.pm.setProgress((int) d.getProgress());
                }
            } catch (NullPointerException e) {
            }
        }

        task.done();

    }

    class zipTaskExecute extends SwingWorker<Void, Void> {
        String shortname = "";

        @Override
        public Void doInBackground() {
            String filename = "";
            try {
                shortname = getName(new URL(d.getUrl()).getFile());
            } catch (MalformedURLException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            System.out.println("shortname minus zip="+removeZip(shortname));
            filename = "index" + File.separatorChar + shortname;

            ZipDir zd = new ZipDir();
            try {
                System.out.println("index" + File.separatorChar + removeZip(shortname));
                zd.unzip(new File(filename), new File("index" + File.separatorChar + removeZip(shortname)) );
            } catch (IOException e1) {
                JOptionPane.showMessageDialog(null, "Problem unzipping " + filename + ":\n" + e1.toString());
                //e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            return null;
        }

        @Override
        public void done() {
            pmzip.close();
            JOptionPane.showMessageDialog(null, "finished unzipping " + shortname);
            taskCombo.addItem(removeZip(shortname));
        }
    }

    class downloadTaskExecute extends SwingWorker<Void, Void> {
        @Override
        public Void doInBackground() {
            try {
                d.run();
            } finally {
                pmzip = new ProgressMonitor(null, "", "", 0, 5);
                pmzip.setMillisToPopup(0);
                pmzip.setMillisToDecideToPopup(0);
                pmzip.setNote("unzipping file");
                pmzip.setProgress(1);

                zipTaskExecute ziptask = new zipTaskExecute();
                ziptask.execute();
            }
            return null;
        }

        @Override
        public void done() {
            //d.pm.close();


        }


    }

    private String getName(String url) {
        int slashIndex = url.lastIndexOf('/');
        return url.substring(slashIndex + 1);
    }
    private String removeZip(String name) {
        int zipIndex = name.lastIndexOf(".zip");
        return name.substring(0,zipIndex);
    }
}
