package edu.berkeley.biocode.utils;

import edu.berkeley.biocode.photoMatcher.ImageFilter;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: biocode
 * Date: Feb 4, 2010
 * Time: 10:33:51 AM
 * To change this template use File | Settings | File Templates.
 */

public class directoryChooser {

    directoryChooserMac dcm = null;
    directoryChooserWin dcw = null;

    public directoryChooser(Frame frame, String defaultDirectory) {
        if (defaultDirectory == null ) {
            defaultDirectory = System.getProperty("user.home");
        }
        System.out.println("defaultDirectory:"  + defaultDirectory);
        
        if (OS.isMac() || OS.isUnix()) {
            dcm = new directoryChooserMac(frame, defaultDirectory);
        } else if (OS.isWindows()) {
            dcw = new directoryChooserWin(defaultDirectory);
        }
    }

    public String getDirectoryPath() {
        if (OS.isMac() || OS.isUnix()) {
            return dcm.getDirectoryPath();
        } else if (OS.isWindows()) {
            return dcw.getDirectoryPath();
        }
        return null;
    }

    public boolean isCancelled() {
        if (OS.isMac() || OS.isUnix()) {
            if (dcm.getFile() == null) {
                return true;
            } else {
                return false;
            }
        } else if (OS.isWindows()) {
            if (dcw.chosenFile == null) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    public ArrayList getImageList() {
        if (OS.isMac() || OS.isUnix()) {
            return dcm.getImageList();
        } else if (OS.isWindows()) {
            return dcw.getImageList();
        }
        return null;
    }

}

class directoryChooserMac extends FileDialog {

    public directoryChooserMac(Frame frame, String defaultDirectory) {
        super(frame);
        super.setDirectory(defaultDirectory);
        System.setProperty("apple.awt.fileDialogForDirectories", "true");
        setVisible(true);
        System.setProperty("apple.awt.fileDialogForDirectories", "false");
    }

    public String getDirectoryPath() {
        return getDirectory() + getFile() + System.getProperty("file.separator");
    }

    public ArrayList getImageList() {
        ArrayList imageList = new ArrayList();
        // Weed out all junk in directory and only go 1 directory deep (not recursive)
        ImageFilter imageFilter = new ImageFilter();
        File f = new File(getDirectoryPath());
        for (int i = 0; i < f.list().length; i++) {

            String filename = (f.list()[i]);
            if (imageFilter.accept(new File(filename))) {
                imageList.add(filename);//.add(filename);
            }
        }
        return imageList;
    }
}

class directoryChooserWin extends JFileChooser {
    File chosenFile = null;

    public directoryChooserWin(String defaultDirectory) {
        this.setCurrentDirectory(new java.io.File(System.getProperty("user.home")));
        this.setDialogTitle("Choose a Directory Containing Your Images");
        this.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        // disable the "All files" option.
        this.setAcceptAllFileFilterUsed(false);
        this.setCurrentDirectory(new File(defaultDirectory));
        int option = this.showOpenDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            chosenFile = this.getSelectedFile();
        }
    }

    public String getDirectoryPath() {
        return chosenFile.getAbsolutePath() + File.separator;
    }

    public ArrayList getImageList() {
        ArrayList imageList = new ArrayList();
        // Weed out all junk in directory and only go 1 directory deep (not recursive)
        ImageFilter imageFilter = new ImageFilter();
        int count = 0;
        for (int i = 0; i < getSelectedFile().list().length; i++) {
            String filename = ((getSelectedFile().list())[i]);

            if (imageFilter.accept(new File(filename))) {
                imageList.add(filename);//.add(filename);
            }
        }
        return imageList;
    }
}

class OS {
    public static boolean isWindows() {
        String os = System.getProperty("os.name").toLowerCase();
        //windows
        return (os.indexOf("win") >= 0);
    }

    public static boolean isMac() {
        String os = System.getProperty("os.name").toLowerCase();
        //Mac
        return (os.indexOf("mac") >= 0);
    }

    public static boolean isUnix() {
        String os = System.getProperty("os.name").toLowerCase();
        //linux or unix
        return (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0);
    }
}
