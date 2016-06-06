package edu.berkeley.biocode.utils;
// The example requires the Google GData Client library,
// which in turn requires the
// Google Collection library. These can be downloaded from
// http://code.google.com/p/gdata-java-client/downloads/list and
// http://code.google.com/p/google-collections/downloads/list.

import com.google.gdata.client.ClientLoginAccountType;
import com.google.gdata.client.GoogleService;
import com.google.gdata.client.Service.GDataRequest;
import com.google.gdata.client.Service.GDataRequest.RequestType;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ContentType;
import com.google.gdata.util.ServiceException;
import edu.berkeley.biocode.bioValidator.mainPage;
import edu.berkeley.biocode.validator.Rules;
import edu.berkeley.biocode.validator.bVWorksheet;
import org.jdesktop.swingworker.SwingWorker;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

/**
 * Java example using the Google Fusion Tables API
 * to query, insert, update, and delete.
 * Uses the Google GDATA core library.
 *
 * @author googletables-feedback@google.com (Google Fusion Tables Team)
 */
public class FusionTables {
    private static FusionTables fusiontable;
    private static final String SERVICE_URL =
            "https://www.google.com/fusiontables/api/query";


    private static final Pattern CSV_VALUE_PATTERN =
            Pattern.compile("([^,\\r\\n\"]*|\"(([^\"]*\"\")*[^\"]*)\")(,|\\r?\\n)");

    private static String username = null;
    //private static String tablename = null;
    private static String password = null;
    private GoogleService service;
    public boolean auth = false;
    private String tablename = null;
    public mainPage mp = null;
    FTloadTask task = null;
    //ButtonGroup radioGroup = new ButtonGroup();


    public FusionTables(String name, mainPage mp) throws AuthenticationException {
        this.mp = mp;
        // tablename = getFileNameWithoutExtension(name);
        auth();
        /*if (auth) {
            System.out.println("creating google service");
            //service = new GoogleService("fusiontables", "fusiontables.ApiExample");
            //service.setUserCredentials(username, password, ClientLoginAccountType.GOOGLE);
        }   else {
            System.out.println("authorization problems");

        }*/
    }

    /**
     * Authenticates the given account for {@code fusiontables} service using a
     * given email ID and password.
     *
     * @param email    Google account email. (For more information, see
     *                 http://www.google.com/support/accounts.)
     * @param password Password for the given Google account.
     *                 <p/>
     *                 This code instantiates the GoogleService class from the
     *                 Google GData APIs Client Library,
     *                 passing in Google Fusion Tables API-specific parameters.
     *                 It then goes back to the Google GData APIs Client Library for the
     *                 setUserCredentials() method.
     */
    public FusionTables(String email, String password, mainPage mp) throws AuthenticationException {
        this.mp = mp;
        service = new GoogleService("fusiontables", "fusiontables.ApiExample");
        service.setUserCredentials(email, password, ClientLoginAccountType.GOOGLE);
    }

    /**
     * Authenticates for {@code fusiontables} service using the auth token. The
     * auth token can be retrieved for an authenticated user by invoking
     * service.getAuthToken() on the email and password. The auth token can be
     * reused rather than specifying the user name and password repeatedly.
     *
     * @param authToken The auth token. (For more information, see
     *                  http://code.google.com/apis/gdata/auth.html#ClientLogin.)
     * @throws AuthenticationException This code instantiates the GoogleService class from the
     *                                 Google Data APIs Client Library,
     *                                 passing in Google Fusion Tables API-specific parameters.
     *                                 It then goes back to the Google Data APIs Client Library for the
     *                                 setUserToken() method.
     */
    /*public FusionTables(String authToken) throws AuthenticationException {
        service = new GoogleService("fusiontables", "fusiontables.ApiExample");
        service.setUserToken(authToken);
    }*/

    /**
     * Fetches the results for a select query. Prints them to standard
     * output, surrounding every field with (@code |}.
     * <p/>
     * This code uses the GDataRequest class and getRequestFactory() method
     * from the Google Data APIs Client Library.
     * The Google Fusion Tables API-specific part is in the construction
     * of the service URL. A Google Fusion Tables API SELECT statement
     * will be passed in to this method in the selectQuery parameter.
     */
    public void runSelect(String selectQuery) throws IOException,
            ServiceException {
        URL url = new URL(
                SERVICE_URL + "?sql=" + URLEncoder.encode(selectQuery, "UTF-8"));
        GDataRequest request = service.getRequestFactory().getRequest(
                RequestType.QUERY, url, ContentType.TEXT_PLAIN);

        request.execute();

        /* Prints the results of the query.                */
        /* No Google Fusion Tables API-specific code here. */

        Scanner scanner = new Scanner(request.getResponseStream(), "UTF-8");
        while (scanner.hasNextLine()) {
            scanner.findWithinHorizon(CSV_VALUE_PATTERN, 0);
            MatchResult match = scanner.match();
            String quotedString = match.group(2);
            String decoded = quotedString == null ? match.group(1)
                    : quotedString.replaceAll("\"\"", "\"");
            System.out.print("|" + decoded);
            if (!match.group(4).equals(",")) {
                System.out.println("|");
            }
        }
    }

    /**
     * populates a hashmap of table IDs and table names
     */
    public ArrayList getTables() throws IOException,
            ServiceException {
        URL url = new URL(
                SERVICE_URL + "?sql=" + URLEncoder.encode("show tables", "UTF-8"));
        GDataRequest request = service.getRequestFactory().getRequest(
                RequestType.QUERY, url, ContentType.TEXT_PLAIN);

        request.execute();
        ArrayList tables = new ArrayList();

        /* Prints the results of the query.                */
        /* No Google Fusion Tables API-specific code here. */

        Scanner scanner = new Scanner(request.getResponseStream(), "UTF-8");
        String id = "";
        String name = "";
        while (scanner.hasNextLine()) {
            scanner.findWithinHorizon(CSV_VALUE_PATTERN, 0);
            MatchResult match = scanner.match();

            String quotedString = match.group(2);
            String decoded = quotedString == null ? match.group(1)
                    : quotedString.replaceAll("\"\"", "\"");
            if (id.equals("")) {
                if (decoded.trim() != "table id") {
                    id = decoded;
                }
            } else {
                if (decoded.trim() != "name") {
                    name = decoded;
                }
            }
//            System.out.print("|" + decoded);
            if (!match.group(4).equals(",")) {
                if (!id.trim().equals("") && !name.trim().equals("")) {
                    tables.add(new tableID(id, name));
                }
                id = "";
                name = "";
            }
        }
        return tables;
    }

    /**
     * Executes insert, update, and delete statements.
     * Prints out results, if any.
     * <p/>
     * This code uses the GDataRequest class and getRequestFactory() method
     * from the Google Data APIs Client Library to construct a POST request.
     * The Google Fusion Tables API-specific part is in the use
     * of the service URL. A Google Fusion Tables API INSERT, UPDATE,
     * or DELETE statement will be passed into this method in the
     * updateQuery parameter.
     */
    public void runUpdate(String updateQuery) throws IOException,
            ServiceException {
        URL url = new URL(SERVICE_URL);
        GDataRequest request = service.getRequestFactory().getRequest(
                RequestType.INSERT, url,
                new ContentType("application/x-www-form-urlencoded"));
        OutputStreamWriter writer =
                new OutputStreamWriter(request.getRequestStream());
        writer.append("sql=" + URLEncoder.encode(updateQuery, "UTF-8"));
        writer.flush();

        request.execute();

        /* Prints the results of the statement.            */
        /* No Google Fusion Tables API-specific code here. */

        Scanner scanner = new Scanner(request.getResponseStream(), "UTF-8");
        while (scanner.hasNextLine()) {
            scanner.findWithinHorizon(CSV_VALUE_PATTERN, 0);
            MatchResult match = scanner.match();
            String quotedString = match.group(2);
            String decoded = quotedString == null ? match.group(1)
                    : quotedString.replaceAll("\"\"", "\"");
            //System.out.print("|" + decoded);
            //if (!match.group(4).equals(",")) {
            //    System.out.println("|");
            //}
        }
    }

    public String runCreate(String updateQuery) throws IOException,
            ServiceException {
        URL url = new URL(SERVICE_URL);
        GDataRequest request = service.getRequestFactory().getRequest(
                RequestType.INSERT, url,
                new ContentType("application/x-www-form-urlencoded"));
        OutputStreamWriter writer =
                new OutputStreamWriter(request.getRequestStream());
        writer.append("sql=" + URLEncoder.encode(updateQuery, "UTF-8"));
        writer.flush();

        request.execute();

        /* Prints the results of the statement.            */
        /* No Google Fusion Tables API-specific code here. */

        Scanner scanner = new Scanner(request.getResponseStream(), "UTF-8");
        String decoded = "";
        while (scanner.hasNextLine()) {
            scanner.findWithinHorizon(CSV_VALUE_PATTERN, 0);
            MatchResult match = scanner.match();
            String quotedString = match.group(2);
            decoded = quotedString == null ? match.group(1)
                    : quotedString.replaceAll("\"\"", "\"");
        }
        return decoded;
    }

    private String getFileNameWithoutExtension(String name) {
        int index = name.lastIndexOf('.');
        if (index > 0 && index <= name.length() - 2) {
            return name.substring(0, index).trim();
        }
        return name.trim();
    }


    // Test to see that breakPoint is reached (setting limits of 500 inserts at a time)
    private boolean breakPoint(int count, int cutoff) {
        if (count == 0) {
            return false;
        }
        Double test = null;

        test = count / new Double(cutoff);
        if (Math.floor(test) == test) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * authorization
     */
    public boolean auth() {
        JPasswordField jp = new JPasswordField();
        JTextField jtUserName = new JTextField();
        //JTextField jtTableName = new JTextField();

        int result = JOptionPane.showConfirmDialog(null,
                new Object[]{
                        //new Label("Loading " + tablename),
                        new Label("Google login/email:"), jtUserName,
                        new Label("Password:"), jp
                },
                "Enter your Google Credentials",
                JOptionPane.OK_CANCEL_OPTION);

        if (result == 0) {
            username = jtUserName.getText();
            //tablename = jtTableName.getText();
            password = new String(jp.getPassword().toString());
            if (username.equals("") || password.equals("")) {
                auth();
            }
            try {
                service = new GoogleService("fusiontables", "fusiontables.ApiExample");
                service.setUserCredentials(username, new String(jp.getPassword()), ClientLoginAccountType.GOOGLE);
            } catch (AuthenticationException e) {
                auth();
                return false;
            }
        } else {
            return false;
        }

        auth = true;
        return true;
    }

    /**
     * load dialog
     */
    public void loadDialog(ArrayList arrRules) {


        //
        // Welcome googleID!
        //
        // Worksheet            Select Table Name or ID
        // [X] worksheet name   drop-down fusion list (editable w/ tableID or New Table Name)
        // [X] worksheet name   drop-down fusion list (editable w/ tableID or New Table Name)
        //
        // [upload]
        //
        // [progress bar]

        // list of checkboxes showing sheets to load

        ArrayList dialogItems = new ArrayList();
        dialogItems.add(new tableLoader()); // set header
        Iterator it = arrRules.iterator();
        int confirmResult = 1;

        // Loop through sheets
        ArrayList tlItems = new ArrayList();
        int count = 0;
        while (it.hasNext()) {
            Rules r = (Rules) it.next();
            tableLoader tl = new tableLoader(r.getWorksheet());
            tlItems.add(tl);
            dialogItems.add(tl);
            //radioGroup.add(tl.radioFT);
            // set the first button to be selected
            if (count == 0) {
                tl.radioFT.setSelected(true);
            }
            count++;
        }

        // User instructions        
        dialogItems.add(new JLabel("Enter a new table name or, select existing table-name to over-write that table"));
        //dialogItems.add(new JLabel("Or, select a name to over-write that table"));

        // populate confirm dialog
        int result = JOptionPane.showConfirmDialog(null,
                dialogItems.toArray(),
                "Fusion Table Load Options",
                JOptionPane.OK_CANCEL_OPTION);

        // Process the results of the dialog box
        if (result == 0) {
            // Loop the tableItems ArrayList and process
            Iterator tlIT = tlItems.iterator();
            while (tlIT.hasNext()) {
                tableLoader tl = (tableLoader) tlIT.next();
                bVWorksheet w = tl.ws;
                if (tl.radioFT.isSelected()) {

                    try {
                        String name = "";
                        String id = null;
                        Object selectedItem = tl.comboboxFT.getSelectedItem();
                        try {
                            id = ((tableID) selectedItem).getId();
                        } catch (ClassCastException e) {
                            name = selectedItem.toString();

                        }
                        // check to see if this has an ID, then if so we know to overwrite table.
                        if (id != null) {
                            System.out.println("you want to upload and replace table id = " + id);
                            confirmResult = JOptionPane.showConfirmDialog(null,
                                    new Object[]{
                                            new Label("Are you sure you want to replace content for " + selectedItem.toString() + "?")
                                    },
                                    "Confirm Replace Contents",
                                    JOptionPane.OK_CANCEL_OPTION);


                            if (confirmResult == 0) {
                                task = new FTloadTask(w, (tableID) selectedItem);
                            }
                            // otherwise use what the user typed
                        } else {
                            System.out.println("you want to make a new table of " + name);
                            if (name == null || name.trim().equals("")) {
                                JOptionPane.showMessageDialog(null, "You need to select a table or type a name");
                                //loadDialog(arrRules);
                                return;
                            }
                            task = new FTloadTask(w, name);
                        }

                    } catch (IOException e3) {
                        e3.printStackTrace();
                    } catch (ServiceException e3) {
                        e3.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (task != null) {
                        task.execute();
                    } else {
                        mp.insertMsg("Fusion Table upload task cancelled");
                    }
                }
            }
        } else {
            mp.insertMsg("Fusion Table upload task cancelled");
        }
    }

    /**
     * Authorizes the user with either a Google Account email and password
     * or auth token, then exercises runSelect() and runUpdate() with some
     * hard-coded Google Fusion Tables API statements.
     */
    public static void main(String[] args) throws ServiceException, IOException {
    }

    private class tableID {
        String id;
        String name;

        private tableID(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String toString() {
            return name + " (id = " + id + ")";
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    private class tableLoader extends JPanel {
        bVWorksheet ws = null;
        JComboBox comboboxFT = new JComboBox();
        //JRadioButton radioFT = new JRadioButton();
        JCheckBox radioFT = new JCheckBox();


        //JCheckBox checkboxFT = new JCheckBox();
        int column1PreferredWidth = 200;
        int column1PreferredHeight = 15;

        tableLoader(bVWorksheet ws) {
            this.ws = ws;
            ArrayList tables = null;
            try {
                tables = getTables();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (ServiceException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            // Populate ComboBox with tables
            comboboxFT.setEditable(true);

            comboboxFT.addItem("");
            Iterator it = tables.iterator();
            while (it.hasNext()) {
                comboboxFT.addItem(it.next());
            }
            //comboboxFT.addItem("sometable");
            //comboboxFT.addItem("sometable2");
            //comboboxFT.addItem("sometable3");

            radioFT.setText(ws.getSheetName());
            radioFT.setPreferredSize(new Dimension(column1PreferredWidth, column1PreferredHeight));

            this.add(radioFT);
            this.add(comboboxFT);
        }

        tableLoader() {
            //JLabel l1 = new JLabel("Worksheets");
            //l1.setPreferredSize(new Dimension(column1PreferredWidth, column1PreferredHeight));
            //JLabel l2 = new JLabel("Table Name");
            //this.add(l1);
            //this.add(l2);
            this.add(new JLabel("Select a worksheet to load and specify table name"));
        }

    }


    class FTloadTask extends SwingWorker<Void, Void> {
        bVWorksheet w;
        String tablename;
        String tableid;
        tableID tid;


        FTloadTask(bVWorksheet w, String tablename) throws IOException, ServiceException {
            this.w = w;
            this.tablename = tablename;
        }

        FTloadTask(bVWorksheet w, tableID tid) throws IOException, ServiceException {
            this.w = w;
            this.tid = tid;
            this.tableid = tid.getId();
            this.tablename = tid.getName();
        }

        @Override
        protected void done() {

        }

        public int getLengthofTask() {
            return w.getNumRows();
        }

        @Override
        public Void doInBackground() throws IOException, ServiceException {
            //public String load(bVWorksheet w, mainPage mp) throws Exception {
            String create = "";
            String cols = "";
            //String thistablename = w.getSheetName().trim() + "_" + tablename;
            // Check if table exists at Fusion Tables

            // Create table statement
            String lat = "";
            String lng = "";

            if (tableid != null) {
                mp.insertMsg("Dropping contents from table id= " + tableid);
                runUpdate("DELETE FROM " + tableid);
            } else {
                mp.insertMsg("Create table " + tablename);
            }
            if (w != null) {
                create += "CREATE TABLE '" + tablename + "' (";
                cols += "(";
                for (int i = 0; i < w.getColNames().toArray().length; i++) {
                    if (w.getColNames().toArray()[i] != null) {
                        create += "'" + w.getColNames().toArray()[i] + "': String,";
                        cols += "'" + w.getColNames().toArray()[i] + "',";
                        try {
                            if (w.getColNames().toArray()[i].equals("DecimalLatitude")) {
                                lat = w.getColNames().toArray()[i].toString();
                            }
                            if (w.getColNames().toArray()[i].equals("DecimalLongitude")) {
                                lng = w.getColNames().toArray()[i].toString();
                            }
                        } catch (NullPointerException e) {
                            // do nothing
                        }
                    }
                }
                // remove last comma
                create = create.substring(0, create.lastIndexOf(',', create.length() - 1));
                cols = cols.substring(0, cols.lastIndexOf(',', cols.length() - 1));


                if (!lat.equals("") && !lng.equals("")) {
                    cols += ",decimallatlng";
                    create += ",decimallatlng: LOCATION";
                }
                cols += ")";
                create += ")\n";
            }

            System.out.println("CREATE STATEMENT: " + create);
           // create = "CREATE TABLE 'systemtrythis' ('tag': String,'Sample Number': String,'Genus': String,'Species': String,'Below Species Rank': String,'Below Species Epithet': String,'Author': String,'Family': String,'Collector Number': String,'Collector': String,'Collector (Team)': String,'Collection Date': String,'Species Field Code': String,'Order': String,'Genus Hybrid': String,'Species Hybrid': String,'Voucher Specimen?': String,'Herbarium Accession No.': String,'Herbarium Acronym': String,'Other ID No.': String,'Qualified Identification?': String,'Taxonomic Determiner': String,'Date Determined': String,'Taxonomic Determiner e-mail': String,'Common Name': String,'Cultivated?': String,'Cultivated Name': String,'Habitat': String,'Habit': String,'Plant Description 1': String,'Plant Description 2': String,'Taxonomic Notes/Other Specimen Details': String,'Plate No.': String,'Row Letter': String,'Column No.': String,'Tissue Type': String,'Tissue Sample Notes': String,'Locality': String,'Plot Elevation': String,'Plot Latitude': String,'Plot Longitude': String,'State/Province': String,'Country': String,'Plot Latitude (Decimal)': String,'Plot Longitude (Decimal)': String,'Locality/Geographic Notes': String)";

            //create = "CREATE TABLE 'ddd' (Tree Tag No. Accession No.: String,Sample Number: String,Genus: String,Species: String,Below Species Rank: String,Below Species Epithet: String,Author: String,Family: String,Collector Number: String,Collector: String,Collector (Team): String,Collection Date: String,Species Field Code: String,Order: String,Genus Hybrid: String,Species Hybrid: String,Voucher Specimen?: String,Herbarium Accession No.: String,Herbarium Acronym: String,Other ID No.: String,Qualified Identification?: String,Taxonomic Determiner: String,Date Determined: String,Taxonomic Determiner e-mail: String,Common Name: String,Cultivated?: String,Cultivated Name: String,Habitat: String,Habit: String,Plant Description 1: String,Plant Description 2: String,Taxonomic Notes/Other Specimen Details: String,Plate No.: String,Row Letter: String,Column No.: String,Tissue Type: String,Tissue Sample Notes: String,Locality: String,Plot Elevation: String,Plot Latitude: String,Plot Longitude: String,State/Province: String,Country: String,Plot Latitude (Decimal): String,Plot Longitude (Decimal): String,Locality/Geographic Notes: String)";
            // don't run this part if we have a table id
            if (tableid == null) {
                try {
                    tableid = runCreate(create);
                } catch (Exception e) {
                    mp.insertMsg("Exception occurred in creating table.  Check for unusual characters in column names?");
                    e.printStackTrace();
                    return null;
                }
            }

            // Construct insert statements
            // NOTE: cannot use prepared statements with fusion tables
            String insert = "";
            int count = 0;
            if (w != null && !tableid.equals("")) {
                int total = w.getNumRows();
                // Start with i = 1 since don't want to load first row
                for (int i = 1; i <= w.getNumRows(); i++) {
                    lat = "";
                    lng = "";
                    insert += "INSERT INTO " + tableid + " " + cols + " VALUES (";

                    for (int j = 0; j < w.getColNames().toArray().length; j++) {

                        if (w.getColNames().toArray()[j] != null) {

                            String value = null;
                            try {
                                value = w.getStringValue(j, i);
                            } catch (Exception e) {
                                e.printStackTrace();
                                // do nothing here-- just an empty cell
                            }

                            if (value != null) {
                                value = value.replaceAll("'", "\\\\'");
                            } else {
                                value = "";
                            }

                            insert += "'" + value + "',";

                            try {
                                if (w.getColNames().toArray()[j].equals("DecimalLatitude")) {
                                    lat = value + " ";
                                }
                                if (w.getColNames().toArray()[j].equals("DecimalLongitude")) {
                                    lng = value;
                                }
                            } catch (NullPointerException e) {
                                // do nothing
                            }
                        }
                    }

                    // remove last comma
                    insert = insert.substring(0, insert.lastIndexOf(',', insert.length() - 1));

                    if ((!lat.equals("") && !lng.equals("") || lat.equals(" "))) {
                        insert += ",'" + lat + lng + "'";
                    }
                    insert += ");\n";
                    count++;
                    // Fusion Tables accepts only 500 at a time... i upload 10 at a time to make the process
                    // more visible to user
                    if (breakPoint(count, 5)) {
                        try {
                            System.out.println("INSERT= " + insert);
                            runUpdate(insert);
                        } catch (Exception e) {
                            mp.insertMsg("Exception encountered when inserting data.  This is typically caused when re-loading data into an existing spreadsheet and you have changed columns.  If this is the case, you will need to create a new table.");
                            count = total;
                            e.printStackTrace();
                        }
                        insert = "";
                        mp.insertMsg(count + " of " + total + " records sent ...");
                    }
                }
            }
            if (!insert.equals("")) {
                runUpdate(insert);
            }

            if (tablename == null) {
                tablename = tid.getName();
            }
            String ret = "";
            ret += w.getSheetName().trim() + " fusion table data is named <b>" + tablename + "</b> and is visible at:<br>";
            ret += "<a href='http://www.google.com/fusiontables/DataSource?docid=" + tableid + "'>";
            ret += "http://www.google.com/fusiontables/DataSource?docid=" + tableid;
            ret += "</a>";
            ret += "<p>Note that each time you load a spreadsheet it will create a new table with that name and a unique ID.  ";
            ret += "You will want to follow the link above to check your table data and paste that link into the LIMS system ";
            ret += "to transfer your data.";
            ret += "<p>";

            mp.insertMsg(ret);

            return null;
        }
    }

}
