package edu.berkeley.biocode.photoMatcher;


import edu.berkeley.biocode.utils.RegEx;

import java.io.File;

/**
 * Purpose of this class is to provide a set of functions to add in parsing meaningful information
 * from filepaths and specimens
 * <p/>
 * PhotoName can take the form of Part1 is specimen, separated by a "+"
 * and part2 is any metadata
 * User: biocode
 * Date: Jan 13, 2010
 * Time: 5:49:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class PhotoNameParser {

    public PhotoNameParser() {
    }

    /**
     * Returns a suggested output filename path
     *
     * @param pSpecimen
     * @param delimiter
     * @param photoPath
     * @return if return is null it means that there is no suggested output path, probably because the specimen is already matched to filename
     */
    public static String getOutputPath(String pSpecimen, String delimiter, String photoPath) {
        String parsedPhotoName = getName(new File(photoPath).getName());
        // do a match just against that which preceeds the first + sign for a filename
        if (pSpecimen.equals(parsedPhotoName.split("\\"+delimiter)[0])) {
            return null;
        } else {
            String lnewPhotoName = pSpecimen + delimiter + parsedPhotoName;
            return photoPath.replaceAll(parsedPhotoName.replace(delimiter,"\\"+delimiter),
                    lnewPhotoName.replace(delimiter,"\\"+delimiter));
        }
    }

    /**
     * Returns a suggested output filename
     * @param pSpecimen
     * @param delimiter
     * @param photoPath
     * @return
     */
    public static String getOutputFile(String pSpecimen, String delimiter, String photoPath) {
        String parsedPhotoName = new File(photoPath).getName();

        // do a match just against that which preceeds the first + sign for a filename
        // If there is a match then just return the parsedPhotoName
        if (pSpecimen.equals(parsedPhotoName.split("\\"+delimiter)[0])) {
            return parsedPhotoName;
        } else {
            return pSpecimen + delimiter + parsedPhotoName;
        }
    }

    /**
     * compare a name to a photo file path and see if they match.
     *
     * @param matchName
     * @param photoPath
     * @return
     */
    public static boolean compareName(String matchName, String photoPath) {
        // just match whatever is before the first + sign        
        String derivedName = getName(photoPath).split("\\+")[0];
        return derivedName.equals(matchName);
    }

    // Return just that portion before first plus sign
    public static String parseName(String name) {
         return name.split("\\+")[0];
    }
    /**
     * pass in a fullpath and filename and return just that portion that would match
     * after the last / and before the .ext
     *
     * @param photoName
     * @return
     */
    public static String getName(String photoName) {
        String name = new File(photoName).getName();

        // remove the filename extension
        // from the perl:
        // String name = field.fileparse($file, '\.[^\.]*');
        String lSpecimen = "";
        String str = RegEx.run("\\.[^\\.]*$", name);
        String results[] = str.split("\\+");
        // Following accounts for incoming names that happen to already have a + sign in them!
        for (int i = 0; i < results.length; i++) {
            if (i > 0) lSpecimen += "+";
            lSpecimen += results[i];
        }
        return lSpecimen;
    }
    

    public static void main(String[] args) {
        String example2a = "/Users/biocode/specimens.IndexFiles/BMOO-00001+IMG_6063.JPG";
        //getOutputPath("BMOO-00001", "+", example2a);
        System.out.println(parseName(getName(example2a)));
        //System.out.println(compareName("BMOO-00001", example2a));
        System.out.println(getOutputFile
                ("BMOO-00001","+","/Users/biocode/specimens.IndexFiles/BMOO-00001+IMG_6069.JPG"));
    }
}