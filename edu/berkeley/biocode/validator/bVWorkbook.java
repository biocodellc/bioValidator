package edu.berkeley.biocode.validator;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by IntelliJ IDEA.
 * User: biocode
 * Date: Apr 21, 2011
 * Time: 4:06:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class bVWorkbook {
    private HSSFWorkbook book = null;


    public HSSFWorkbook getBook() {
        return book;
    }

    public bVWorkbook(String excelFile) {

        InputStream inputStream = null;

        try {
            inputStream = new FileInputStream(excelFile);
        }
        catch (FileNotFoundException e) {
            System.out.println("File not found in the specified path.");
            e.printStackTrace();
        }

        POIFSFileSystem fileSystem = null;

        try {
            fileSystem = new POIFSFileSystem(inputStream);

            book = new HSSFWorkbook(fileSystem);
            /*
            Example of how to loop through a workbook
             HSSFSheet sheet = workBook.getSheetAt(1);
            Iterator<Row> rows = sheet.rowIterator();

            while (rows.hasNext()) {
                HSSFRow row = (HSSFRow)rows.next();

                // display row number in the console.
                //System.out.println("Row No.: " + row.getRowNum());

                // once get a row its time to iterate through cells.
                Iterator<Cell> cells = row.cellIterator();

                while (cells.hasNext()) {
                    HSSFCell cell = (HSSFCell)cells.next();
                    if (cell.toString().trim() != "" && cell.toString() != null) {
                               System.out.println(cell.toString());
                    }
                }
            }
            */
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * loadSheet loads any type of generic worksheet from a workbook
     *
     * @param sheet
     * @return
     */
    public bVWorksheet loadSheet(String sheet, int numHeaderRows) {        
        return new bVWorksheet(book,sheet,numHeaderRows);
    }
}