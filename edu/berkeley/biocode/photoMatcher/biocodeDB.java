package edu.berkeley.biocode.photoMatcher;


import edu.berkeley.biocode.validator.Constants;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: biocode
 * Date: Jan 20, 2010
 * Time: 3:08:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class biocodeDB {

    public static Connection con = null;

    /**
     * creates a connection to a database object.
     *
     * @throws Exception Thrown if unable to connect for whatever reason
     */
    public biocodeDB() throws Exception {
        try {
            Class.forName(System.getProperty("app.dbDriver")).newInstance();

            con = DriverManager.getConnection(System.getProperty("app.dbCxn"),
                    System.getProperty("app.dbUser"),
                    System.getProperty("app.dbPass"));

            if (!con.isClosed())
                System.out.println("Successfully connected to MySQL server using TCP/IP...");

        } catch (Exception e) {
            throw new Exception();
            //System.err.println("Exception: " + e.getMessage());
        }
    }

    /**
     * close the connection
     */
    private static void close() {
        try {
            if (con != null)
                con.close();
        } catch (SQLException e) {
        }
    }

    /**
     * Return one Value
     *
     * @param sql
     * @return
     */
    public String getOneValue(String sql) {
        Statement st = null;
        ResultSet rs = null;
        String ret = "";
        try {
            st = con.createStatement();
            rs = st.executeQuery(sql);
            while (rs.next()) {
                ret = rs.getString(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return ret;
    }

    /**
     * MatchNames to a table & column from an ArrayList. useful for matching photoNames to Specimens
     *
     * @param table
     * @param field
     * @param list
     * @return
     */
    public HashMap MatchNames(String table, String field, ArrayList list) {
        PhotoNameParser p = new PhotoNameParser();//

        // Create new HashMap to hold columnValues
        HashMap<String, String> map = new HashMap<String, String>();

        // Loop values and create names that should match them
        String values = "";
        int count = 0;
        for (Object lookupValue : list) {
            String lSpecimen = p.parseName(p.getName((String) lookupValue));
            if (count > 0) values += ",";
            values += "'" + lSpecimen + "'";
            count++;
        }

        String sql = "SELECT specimen_num_collector " +
                "FROM " + table + " " +
                "WHERE " + field + " IN (" + values + ")";
        System.out.println(sql);
        // Create an arraylist from the resultset
        Statement st = null;
        ResultSet rs = null;
        String ret = "";
        ArrayList rowValues = new ArrayList();
        try {
            st = con.createStatement();
            rs = st.executeQuery(sql);
            while (rs.next()) {
                rowValues.add(rs.getString(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        // Loop values that were passed in to function
        for (Object lookupValue : list) {
            System.out.println(lookupValue);

            boolean booFound = false;
            String specimen_num_collector = "";
            //String lSpecimen=p.getName((String)lookupValue);
            // Loop rowValues

            for (int j = 0; j < rowValues.size(); j++) {

                String rowValue = (String) rowValues.get(j);
                // returning data like: rowValue=jg_10000:lSpecimenjg_10000+DSC_9155
                if (p.compareName(rowValue, (String) lookupValue)) {
                    booFound = true;
                    specimen_num_collector = rowValue;
                }
            }

            if (booFound) {
                map.put((String) lookupValue, specimen_num_collector);
            } else {
                map.put((String) lookupValue, Constants.nomatch);
            }

        }


        return map;
    }


    public static void main(String args[]) throws Exception {
        biocodeDB b = new biocodeDB();
        System.out.println(b.getOneValue("Select count(*) from biocode"));

        b.close();
    }
}

