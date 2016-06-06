package edu.berkeley.biocode.validator;

import edu.berkeley.biocode.bioValidator.mainPage;

import javax.swing.*;
import java.util.LinkedList;
import java.util.prefs.Preferences;


/**
 * validationFiles is a LinkedList consisting of unique validationfile instances.
  */
public class validationFiles {

    public LinkedList validationfile = new LinkedList();

    validationFiles() {
    }

    public Object[] getURLs() {
        Object[] o = null;
        for (int i = 0; i < validationfile.size(); i++) {
            o[i] = ((validationFile) validationfile.get(i)).getUrl();
        }
        return o;
    }

    public validationFile getValidationFile(String name) {
        //Object[] o = null;
        for (int i = 0; i < validationfile.size(); i++) {
            if (((validationFile) validationfile.get(i)).getName().equals(name)) {
                return (validationFile) validationfile.get(i);
            }
        }
        return null;
    }

    public LinkedList getNames() {
        LinkedList al = new LinkedList();
        for (int i = 0; i < validationfile.size(); i++) {
            al.add(((validationFile) validationfile.get(i)).getName());
        }
        return al;
    }

    public Object[] getDescriptions() {
        Object[] o = null;
        for (int i = 0; i < validationfile.size(); i++) {
            o[i] = ((validationFile) validationfile.get(i)).getDescription();
        }
        return o;
    }


    /**
     * Choose a unique validationfile to use.
     * @return
     */
    public validationFile showDialog() {
        Preferences prefs = Preferences.userRoot().node(System.getProperty("app.bioValidatorUserPreferences"));

        // Get sensible possibilities
        String s = (String) JOptionPane.showInputDialog(
                mainPage.frame,
                "Choose validation schema\nto load or re-load",
                "Customized Dialog",
                JOptionPane.PLAIN_MESSAGE,
                null,
                this.getNames().toArray(),
                prefs.get("validationSchema", System.getProperty("app.defaultValidationSchema")));

        try {
            prefs.put("validationSchema", s);
        } catch (NullPointerException e) {
            return showDialog();
        }
        return getValidationFile(s);

    }

    public void addValidationFile(validationFile v) {
        validationfile.addLast(v);
    }


}
