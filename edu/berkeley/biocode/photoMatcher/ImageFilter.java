package edu.berkeley.biocode.photoMatcher;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;

/* ImageFilter.java is used by FileChooserDemo2.java. */
public class ImageFilter extends FileFilter {

    //Accept all directories and all gif, jpg, tiff, or png files.
    public boolean accept(File f) {
        //if (f.isDirectory()) {
        //    return true;
        //}

        String extension = Utils.getExtension(f);
        if (extension != null) {
            if (
                    //extension.equals(Utils.tiff) ||
                    //extension.equals(Utils.tif) ||
                    //extension.equals(Utils.gif) ||
                    extension.equals(Utils.jpeg) ||
                    extension.equals(Utils.jpg) 
                    //extension.equals(Utils.png)
                    ) {
                return true;
            } else {
                return false;
            }
        }
        // don't accept the thumb director
        if (f.getName().equals("bvthumbs")) {
            return false;
        }
        return false;
    }

    //The description of this filter
    public String getDescription() {
        return "Just Images";
    }
}

/* Utils.java is used by FileChooserDemo2.java. */
class Utils {
    public final static String jpeg = "jpeg";
    public final static String jpg = "jpg";
    //public final static String gif = "gif";
    //public final static String tiff = "tiff";
    //public final static String tif = "tif";
    //public final static String png = "png";

    /*
    * Get the extension of a file.
    */
    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }

    /**
     * Returns an ImageIcon, or null if the path was invalid.
     */
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = Utils.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
}

