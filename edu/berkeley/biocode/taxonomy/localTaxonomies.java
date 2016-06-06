package edu.berkeley.biocode.taxonomy;

import javax.swing.*;
import java.io.File;
import java.sql.Date;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: biocode
 * Date: Aug 5, 2010
 * Time: 5:12:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class localTaxonomies {
    private File INDEX_DIR = new File(System.getProperty("app.bvFilesDirectory")+"index");
    private static ArrayList taxonomies = new ArrayList();
    private static ArrayList taxonomydates = new ArrayList();

    /**
     * Lookup available taxonomies
     *
     * @return
     * @throws Exception
     */
    public localTaxonomies() throws Exception {

        //JComboBox cb = new JComboBox();
        // check to be sure index directory exists, if not, then create it
        if (!INDEX_DIR.exists()) {
            if (!INDEX_DIR.mkdir()) {
                throw new Exception("unable to create " + INDEX_DIR.getAbsolutePath());
            }
            ;
        }

        // lookup taxonomies in folder called "index"
        String[] chld = INDEX_DIR.list();

        for (int i = 0; i < chld.length; i++) {
            String fileName = chld[i];
            File file = new File(INDEX_DIR.getName() + File.separator + fileName);
            if (file.isDirectory()) {
                taxonomies.add(file.getName());
                taxonomydates.add(new Date(file.lastModified()));
            }
        }


    }

    public static void main(String args[]) {
        try {
            localTaxonomies lt = new localTaxonomies();
        } catch (Exception e) {
           e.printStackTrace();
        }
        for (int i = 0; i < taxonomies.size(); i++) {
            System.out.println(taxonomies.get(i));
        }
    }

    public ArrayList getTaxonomies() {
        return taxonomies;
    }

    public ArrayList getTaxonomydates() {
        return taxonomydates;
    }


}