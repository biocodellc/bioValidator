package edu.berkeley.biocode.photoMatcher;

import edu.berkeley.biocode.photoMatcher.ScaledImageIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by IntelliJ IDEA.
 * User: biocode
 * Date: Jan 11, 2010
 * Time: 5:33:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class labelImage extends JLabel {

    private String text;
    private Image image;
    private BufferedImage resizedImage;
    private BufferedImage scaledImage;

    public labelImage() {
        
    }
    public labelImage(String pathName, int width, int height) {
        // TODO: error catch in case image doesn't exist, cannot be read ,etc.

        // Setup display parameters (text below icon & centered)
        this.setVerticalTextPosition(JLabel.BOTTOM);
        this.setHorizontalTextPosition(JLabel.CENTER);

        // Make Label By Parsing URL for everything after the last /
        int slashIndex = pathName.toString().lastIndexOf('/');
        String filename = pathName.toString().substring(slashIndex + 1);
        this.setText(filename);

        // Make Icon
        this.setIcon(new ScaledImageIcon(pathName, width, height));

    }

}

