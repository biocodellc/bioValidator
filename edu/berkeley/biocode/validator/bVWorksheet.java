package edu.berkeley.biocode.validator;

import edu.berkeley.biocode.photoMatcher.PhotoNameParser;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: biocode
 * Date: Nov 4, 2009
 * Time: 3:38:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class bVWorksheet {

    private HSSFSheet wsh = null;
    private String sheetName = null;
    public Integer numRows = null;
    public boolean isValid = true;
    // private String photoNameField = null;
    private int numHeaderRows;

    public bVWorksheet(HSSFWorkbook wb, String sheetName) {
        numHeaderRows = 0;

        this.sheetName = sheetName;
        wsh = wb.getSheet(sheetName);

        if (wsh == null) {
            isValid = false;
        } else {
            numRows = this.getNumRows();
        }
    }

    public bVWorksheet(HSSFWorkbook wb, String sheetName, int rowColumnHeadingsAreOn) {
        numHeaderRows = rowColumnHeadingsAreOn - 1;

        this.sheetName = sheetName;
        wsh = wb.getSheet(sheetName);
        if (wsh == null) {
            isValid = false;
        } else {
            numRows = this.getNumRows();
        }
    }

    /* public String getPhotoNameField() {
        // TODO: set this in the configuration file
        return "Coll_EventID_collector";
        //return photoNameField;
    }*/

    public String getSheetName() {
        return this.sheetName;
    }

    public HSSFSheet getSheet() {
        return this.wsh;
    }

    public List<String> getColNames() {

        /*List<String> listColumnNames = new ArrayList<String>();
        List<String> listColumns = wsh.getColNames();
        for (int i = 0; i < listColumns.size(); i++) {
            listColumnNames.add(getStringValue(i, 0));
        }
        return listColumnNames.toArray();
        */
        List<String> listColumnNames = new ArrayList<String>();
        Iterator<Row> rows = wsh.rowIterator();
        int count = 0;
        while (rows.hasNext()) {
            if (count == numHeaderRows) {
                break;
            }
            rows.next();
            count++;
        }
        HSSFRow row = (HSSFRow) rows.next();

        Iterator<Cell> cells = row.cellIterator();
        while (cells.hasNext()) {
            HSSFCell cell = (HSSFCell) cells.next();
            if (cell.toString().trim() != "" && cell.toString() != null) {
                // System.out.println(cell.toString());
                listColumnNames.add(cell.toString());
            }
        }

        return listColumnNames;
    }

    public Integer getColumnPosition(String colName) throws Exception {
        List<String> listColumns = this.getColNames();
        for (int i = 0; i < listColumns.size(); i++) {
            if (this.getColNames().toArray()[i].toString().equals(colName)) {
                return i;
            }
        }
        // if not found then throw exception        
        return null;
    }


    public String getStringValue(String column, int row) throws Exception {
        String strValue = null;
        try {
            strValue = getStringValue(getColumnPosition(column), row);
        } catch (Exception e) {
            return null;
        }
        return strValue;
    }

    /**
     * Returns string values for all cells regardless of whether they are cast as numeric or
     * String.  Does not handle boolean cell types currently.
     *
     * @param col
     * @param row
     * @return
     */
    public String getStringValue(int col, int row) {
        row = row + this.numHeaderRows;

        HSSFRow hrow = wsh.getRow(row);
        HSSFCell cell = hrow.getCell(col);
        try {
            if (cell.getCellType() == Cell.CELL_TYPE_BLANK) {
                return null;
            } else if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
                if (cell.getStringCellValue().trim().equals("")) {
                    return null;
                } else {
                    return cell.toString();
                }
                // Handle Numeric Cell values--- this is a bit strange since we have no way of
                // knowing if the numeric cell value is integer or double.  Thus, "5.0" gets interpreted
                // as "5"... this is probably preferable to "5" getting displayed as "5.0", which is the
                // default HSSFCell value behaviour
            } else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                if (new Double(cell.getNumericCellValue()).toString().trim().equals("")) {
                    return null;
                } else {
                    String value = Double.toString(cell.getNumericCellValue()); //toString();

                    if (value.indexOf(".0") == value.length() - 2) {
                        Double D = (Double.parseDouble(value));
                        Integer I = D.intValue();
                        return I.toString();
                    } else {
                        return value;
                    }
                }
            } else {
                return null;
            }
        } catch (NullPointerException e) {
            return null;
            // case where this may be a numeric cell lets still try and return a string
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Double getDoubleValue(String column, int row) throws Exception {
        Double dblValue = null;
        try {
            dblValue = Double.parseDouble(getStringValue(column, row));
        } catch (Exception e) {
            return null;
        }
        return dblValue;
    }


    /**
     * Secure way to count number of rows in spreadsheet --- this method finds the first blank row and then returns the count--- this
     * means there can be no blank rows.
     *
     * @return
     */
    public int getNumRows() {
        Iterator it = wsh.rowIterator();
        int count = 0;
        while (it.hasNext()) {
            Row row = (Row) it.next();
            Iterator cellit = row.cellIterator();
            String rowContents = "";
            while (cellit.hasNext()) {
                Cell cell = (Cell) cellit.next();
                if (cell.getCellType() == Cell.CELL_TYPE_STRING || cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                    rowContents += cell.toString();
                }
            }

            if (rowContents.equals("")) {
                // The count to return should be minus 1 to account for title
                return count - 1 - numHeaderRows;
            }
            count++;
        }
        this.numRows = count - 1 - numHeaderRows;
        System.out.println("this is the number of rows!" + this.numRows);
        // The count to return should be minus 1 to account for title
        return count - 1 - numHeaderRows;
    }

    public int getNumCols() {
        List<String> cols = this.getColNames();
        return cols.size();
    }

    /**
     * Gets a HashMap of a particular column
     *
     * @param column
     * @param value
     * @param returnValues
     * @return map of column names and value for the specimen record
     */
    public HashMap getColumnMap(String column, String value, ArrayList returnValues) throws Exception {
        // Create new HashMap to hold return values
        HashMap<String, String> map = new HashMap<String, String>();

        boolean booFound = false;
        // Create a list of columnvalues
        // Loop rowValues
        for (int j = 1; j <= numRows; j++) {
            String rowValue = this.getStringValue(column, j);


            if (value.equals(rowValue)) {
                booFound = true;
                for (int k = 0; k < returnValues.size(); k++) {
                    // populate map
                    map.put((String) returnValues.get(k),
                            this.getStringValue((String) returnValues.get(k), j));

                }
            }
            if (booFound) {
                j = numRows;
            }

        }

        return map;
    }

    /**
     * Gets a row
     *
     * @param rowNum
     * @return map of column names and value for the specimen record
     */
    public HashMap getRow(int rowNum) {
        // Create new HashMap to hold return values
        HashMap<String, String> map = new HashMap<String, String>();
        return map;
    }

    /**
     * Creates an ArrayList of individual column values
     *
     * @param column
     * @return map of column names and value for the specimen record
     */
    public ArrayList specimenRecords(String column) throws Exception {
        ArrayList<String> arrRet = new ArrayList();

        // Loop rowValues
        for (Integer j = 1; j <= numRows; j++) {
            String rowValue = this.getStringValue(column, j);
            arrRet.add(rowValue);
        }

        return arrRet;
    }

    /**
     * This Method returns a Map of names pointing to values in a particular column.
     * Useful especially for looking up Photo Names to see if they match specimen_num_collector,
     * but can be used for any other values.
     *
     * @param column name of column to match to
     * @param list   a list of values to be matched
     * @return
     */
    public HashMap MatchNames(String column, ArrayList list) throws Exception {
        PhotoNameParser p = new PhotoNameParser();

        // Create new HashMap to hold columnValues
        HashMap<String, String> map = new HashMap<String, String>();

        // Loop values that were passed in to function
        for (Object lookupValue : list) {
            boolean booFound = false;
            String specimen_num_collector = "";

            // Loop rowValues
            for (int j = 1; j <= numRows; j++) {
                String rowValue = this.getStringValue(column, j);
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

    /**
     * Lookup the row that a value occurs at
     *
     * @param column name of column to match to
     * @param  value to be matched
     * @return
     */
    public Integer getRow(String column, String value) throws Exception {
        PhotoNameParser p = new PhotoNameParser();

        // Loop rowValues
        for (int j = 1; j <= numRows; j++) {
            String rowValue = this.getStringValue(column, j);
            //System.out.println(rowValue+"="+value + "?");
            if (rowValue.equals(value)) {
                return j;
            }
        }
        return null;
    }

    /**
     * This Method returns an ArrayList of names pointing to values in a particular column.
     * Useful especially for looking up Photo Names to see if they match specimen_num_collector,
     * but can be used for any other values.
     *
     * @param column name of column to match to
     * @return
     */
    public ArrayList getColumn(String column) throws Exception {
        PhotoNameParser p = new PhotoNameParser();

        // Create new HashMap to hold columnValues
        HashMap<String, String> map = new HashMap<String, String>();
        ArrayList values = new ArrayList();

        // Loop rowValues
        for (int j = 1; j <= numRows; j++) {
            String rowValue = this.getStringValue(column, j);
            // returning data like: rowValue=jg_10000:lSpecimenjg_10000+DSC_9155
            values.add(rowValue);
        }

        return values;
    }

    public ArrayList getColumnLimit(String column, int limit) throws Exception {
        PhotoNameParser p = new PhotoNameParser();

        // Create new HashMap to hold columnValues
        HashMap<String, String> map = new HashMap<String, String>();
        ArrayList values = new ArrayList();

        int thislimit = numRows;
        if (numRows > limit) {
            thislimit = limit;
        }
        // Loop rowValues
        for (int j = 1; j <= thislimit; j++) {
            String rowValue = this.getStringValue(column, j);
            // returning data like: rowValue=jg_10000:lSpecimenjg_10000+DSC_9155
            values.add(rowValue);
        }

        return values;
    }

    /**
     * Simple 2-d array to hold this worksheet contents
     *
     * @return
     */
    public String printAll() {
        String html = "";
        int numrows = this.getNumRows();
        // Limit out output of this command to 10 rows so we don't hog memory, resources
        if (numrows > 10) numrows = 10;
        int columns = this.getNumCols();
        String[][] dataArray = new String[numrows][columns];

        Iterator<Row> rows = this.wsh.rowIterator();
        html += "<table>";
        int rowCounter = 0;
        while (rows.hasNext()) {

            HSSFRow row = (HSSFRow) rows.next();
            Iterator<Cell> cells = row.cellIterator();

            int colCounter = 0;
            if (rowCounter <= numrows + numHeaderRows && rowCounter > numHeaderRows) {
                html += "<tr>";
                while (cells.hasNext()) {
                    String rowValue = "";
                    HSSFCell cell = (HSSFCell) cells.next();
                    if (cell.toString().trim() != "" && cell.toString() != null) {
                        rowValue = cell.toString();
                        html += "<td>" + rowValue + "</td>";

                    } else {
                        html += "<td></td>";
                    }
                    if (rowCounter < numRows) {
                        try {
                            dataArray[rowCounter][colCounter++] = rowValue;
                        } catch (ArrayIndexOutOfBoundsException e) {
                            //e.printStackTrace();
                            System.out.println("ArrayIndex Out of Bounds exception... common in worksheets that have formatting, etc.. .below the data");
                            break;
                        }
                    }
                }
                html += "</tr>";
            }
            rowCounter++;
        }
        html += "</table>";

        //return dataArray;
        return html;


    }
}


