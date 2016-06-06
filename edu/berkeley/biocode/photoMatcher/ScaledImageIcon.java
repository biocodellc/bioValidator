package edu.berkeley.biocode.photoMatcher;

import edu.berkeley.biocode.bioValidator.mainPage;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by IntelliJ IDEA.
 * User: biocode
 * Date: Jan 13, 2010
 * Time: 2:55:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class ScaledImageIcon extends ImageIcon {
    private BufferedImage scaledImage;
    private Image image;
    private int height;
    private int width;
    private ImageIcon imageicon;
    private String pathname;
    public boolean saveSuccess = true;

    ScaledImageIcon(ImageIcon imageicon) {
        this.imageicon = imageicon;
    }

    /**
     * Constructor to smartly open scaled images (relying on cached copies, if available)
     *
     * @param pathname
     * @param thumbDirName
     */
    ScaledImageIcon(String pathname, String thumbDirName) throws FileNotFoundException {
        // this scaledimageicon checks first for the existence of this file in the "bvthumbs" directory
        this.height = Integer.getInteger("app.thumbnailHeight");
        this.width = Integer.getInteger("app.thumbnailWidth");
        this.pathname = pathname;

        makeIcon();
        saveThumbImage();
    }


    ScaledImageIcon(String pathname, int width, int height) {
        this.height = height;
        this.width = width;
        this.pathname = pathname;
        makeIcon();
    }


    public void makeIcon() {
        image = Toolkit.getDefaultToolkit().createImage(pathname);
        System.out.println(pathname);
        MediaTracker tracker = new MediaTracker(new JPanel());
                tracker.addImage(image,0);
                try {
                    tracker.waitForAll();
                }
                catch(InterruptedException ex){}
        
        scaledImage = getScaledImage(width, height);
        ImageIcon scaledicon = new ImageIcon(scaledImage);
        cleanup();
        setImage(scaledicon.getImage());
    }

    /**
     * scales Image while maintaining proper width/height ratios
     *
     * @param p_image
     * @param w
     * @param h
     * @return
     */
    private BufferedImage getScaledImage(int w, int h) {
        int l_width = w;
        int l_height = h;

        // Make sure the aspect ratio is maintained, so the image is not skewed
        double thumbRatio = (double) l_width / (double) l_height;
        int imageWidth = image.getWidth(null);
        int imageHeight = image.getHeight(null);
        double imageRatio = (double) imageWidth / (double) imageHeight;
        if (thumbRatio < imageRatio) {
            l_height = (int) (l_width / imageRatio);
        } else {
            l_width = (int) (l_height * imageRatio);
        }

        // Draw the scaled image
        BufferedImage resizedImage = new BufferedImage(l_width, l_height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.drawImage(image, 0, 0, l_width, l_height, this.getImageObserver());
        graphics2D.dispose();
        return resizedImage;
    }

    public void cleanup() {

        if (scaledImage != null) {
            scaledImage.flush();
        }

        if (image != null) {
            image.flush();
        }

        System.gc();
        System.runFinalization();
        System.gc();
    }


    public boolean saveThumbImage() throws FileNotFoundException {
        try {
            File f = new File(pathname);
            String outputDirectory = f.getParent() + File.separator + System.getProperty("app.thumbsDirectory") + File.separator;
            String outputFile = outputDirectory + f.getName();
            File outputfile = new File(outputFile);
            saveSuccess = true;

            // Create thumbDirName if does not exist
            // a bit inefficient to check this everytime but far easier from a logic perspective as this
            // gets called from multiple spots
            if (!new File(outputDirectory).exists()) {
                if (!(new File(outputDirectory)).mkdir()) {
                    JOptionPane.showMessageDialog(mainPage.frame,
                            "Unable to make thumbnail cache directory, check write permissions!\n" +
                                    "If your photos are on a CD or DVD, you will need to copy\n"+
                                    "them to a local hard disk and try again."
                    );
                    saveSuccess = false;
                }
            }

            if (saveSuccess) {
                System.out.println("in saveThumbImage() function: " + outputfile);
                ImageIO.write(scaledImage, "jpg", outputfile);
            } else {
                return false;
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }

}


