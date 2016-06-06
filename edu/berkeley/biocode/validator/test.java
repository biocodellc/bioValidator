package edu.berkeley.biocode.validator;

import java.util.prefs.Preferences;

/**
 * Created by jdeck on 8/21/15.
 */
public class test {
    public static void main(String[] args) {
        System.out.println(System.getProperty("app.bioValidatorUserPreferences"));
        System.out.println(Preferences.userRoot().node(System.getProperty("app.bioValidatorUserPreferences").toString()));
    }
}
