package edu.berkeley.biocode.validator;

import javax.swing.*;
import java.io.File;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: biocode
 * Date: Feb 12, 2010
 * Time: 2:42:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class setProperties extends Properties {

    public setProperties() {

        System.setProperty("app.taxonomyIndex", "http://biocode.berkeley.edu/index/directory.xml");
        System.setProperty("app.taxonomyURL", "http://biocode.berkeley.edu/index/");


        // The version of this application should match version of validation schema
        System.setProperty("app.majorversion", "0.9");
        System.setProperty("app.subversion", "6");
        System.setProperty("app.version",System.getProperty("app.majorversion")+System.getProperty("app.subversion"));

        // bioValidator Component in User Home Directory where we store Cached items
        System.setProperty("app.bvFilesDirectory",
                System.getProperty("user.home") +
                System.getProperty("file.separator") +
                "bioValidatorCache" +
                System.getProperty("file.separator"));
        //the directory to save cached thumbnails to reccomended to use a . before
        // the directory name to hide it from file viewer so users don't accidentally load it.
        System.setProperty("app.thumbsDirectory", ".bvthumbs");
        System.setProperty("app.emailScriptNoticeFlickrUpload","http://biocode.berkeley.edu/cgi/flickrphoto_email.cgi");
        // db Connection Driver
        System.setProperty("app.dbDriver", "com.mysql.jdbc.Driver");
        // db Cxn
        System.setProperty("app.dbCxn", "jdbc:mysql://gall.bnhm.berkeley.edu/biocode");
        // db User
        System.setProperty("app.dbUser", "viewer");
        // db Password
        System.setProperty("app.dbPass", "viewonly");
        // Number of photos to display at a time, per panel in the image browser
        System.setProperty("app.maxPhotos", "6");
        //xmlValidatorFile=biocodeValidator.xml.cache
        // Upload base URL
        System.setProperty("app.uploadBaseURL", "http://biocode.berkeley.edu/");

        System.setProperty("app.bioValidatorUserPreferences","bioValidatorUserPreferences");

        // Validation Schemas XML
        //System.setProperty("app.validationSchemasBaseURL","http://biocode.berkeley.edu/");
        //System.setProperty("app.validationSchemasFileName", "validationSchemas-0.8.xml");
        System.setProperty("app.validationSchemasBaseURL","http://biovalidator.googlecode.com/svn/trunk/schemas/");
        System.setProperty("app.validationSchemasFileName", "validationSchemas-0.9.xml");

        // Validation Schemas XSD
        //System.setProperty("app.validationXSDSchemasBaseURL","http://biocode.berkeley.edu/");
        //System.setProperty("app.validationXSDSchemasFileName","validationSchemas-0.8.xsd");
        System.setProperty("app.validationXSDSchemasBaseURL","http://biovalidator.googlecode.com/svn/trunk/schemas/");
        System.setProperty("app.validationXSDSchemasFileName","validationSchemas.xsd");

        // Default Validation Schema
        System.setProperty("app.defaultValidationSchema","biocodeValidator-0.9.xml");


        // Base URL for finding validation rules file
        //System.setProperty("app.validationBaseURL", "http://biocode.berkeley.edu/");
        // Name of validation rules file
        //System.setProperty("app.validationFileName", "biocodeValidator-" +
        //        System.getProperty("app.majorversion") +
        //        ".xml");

        //System.setProperty("app.validationAbsoluteFileName", System.getProperty("app.bvFilesDirectory") +
         //       System.getProperty("app.validationFileName"));


        // Base URL for finding validation rules file
        //System.setProperty("app.xsdBaseURL", "http://biocode.berkeley.edu/");
        // Name of validation rules file
        //System.setProperty("app.xsdFileName", "bioValidator-" +
        //        System.getProperty("app.majorversion") +
        //        ".xsd");
        //System.setProperty("app.xsdAbsoluteFileName", System.getProperty("app.bvFilesDirectory") +
        //        System.getProperty("app.xsdFileName"));
        // Cache extension name to use (on local computer)
        System.setProperty("app.cacheExtension", ".cache");
        // Number of hours to wait by default until validation file expires
        System.setProperty("app.validationExpireHours", "24");
        // Set width & height of thumbnail images generated.
        System.setProperty("app.thumbnailHeight", "80");
        System.setProperty("app.thumbnailWidth", "80");

        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "bioValidator");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InstantiationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IllegalAccessException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        

        // Check that file exists for bioValidator
        File file = new File(System.getProperty("app.bvFilesDirectory"));
        boolean exists = file.exists();

        if (!exists) {
            // Create one directory
            boolean success = (new File(System.getProperty("app.bvFilesDirectory"))).mkdir();
            if (!success) {
                String message = "Application does not have write access in user's home directory, Fix this and restart application.";
                System.out.println(message);
                JOptionPane.showMessageDialog(null, message);
                System.exit(19);

            }
        }
    }
}
