package edu.berkeley.biocode.validator;

/**
 * Created by IntelliJ IDEA.
 * User: biocode
 * Date: May 11, 2010
 * Time: 2:54:01 PM
 * To change this template use File | Settings | File Templates.
 */

public class Constants {
    public static final int SPREADSHEET = 0;
    public static final int DB = 1;

    public static int CANCEL = 0;
    public static int CONTINUE = 1;
    public static int OK = 1;

    public static Object[] OPTIONS = {"Cancel", "Continue"};
    public static Object[] OPTIONSOK = {"Cancel", "OK"};

    public static String nomatch = "Unable to find match";

private Constants (){
    //this prevents even the native class from
    //calling this ctor as well :
    throw new AssertionError();
  }

}
