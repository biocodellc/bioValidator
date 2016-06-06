package edu.berkeley.biocode.photoMatcher;

import javax.swing.table.DefaultTableModel;
import java.util.*;

public class photoListModel extends DefaultTableModel {

    private Hashtable data;

    //private final String[] columnNames;
    private final photoListColumn[] columnNames = {
            new photoListColumn("File", String.class, false),
            new photoListColumn("Field Name", String.class, false),
            new photoListColumn("Flickr Status", String.class, false)
    };

    private int columns = columnNames.length;

    //public photoListModel(photoListColumn columnNames[]) {
    public photoListModel() {
        data = new Hashtable();
    }

    public String getValue(int Row, int Column) {
        return getValueAt(Row, Column).toString();
    }

    /**
     * Return an ArrayList of elements in the FileColumn
     *
     * @return ArrayList
     */
    public ArrayList getFileColumn() {
        Vector v = this.getDataVector();
        Iterator i = v.iterator();
        int count = 0;

        ArrayList list = new ArrayList();

        while (i.hasNext()) {
            Object[] o = ((Vector) i.next()).toArray();
            list.add(o[0].toString());
        }
        return list;
    }


    public void updateFilename(Object o, int row) {
        super.setValueAt(o, row, 0);
    }

    public void updateSpecimen(Object o, int row) {
        super.setValueAt(o, row, 1);
    }

    public void updateFlickrStatus(Object o, int row) {
        super.setValueAt(o, row, 2);
    }

    public Set getData() {
        return data.entrySet();
    }


    public void addRow(photoListRow t) {
        Object[] obj = {t.getFile(), t.getSpecimen()};
        super.addRow(obj);
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public Class<?> getColumnClass(int modelIndex) {
        return columnNames[modelIndex].columnClass;
    }


    public String getColumnName(int col) {
        return columnNames[col].columnName;
    }

    public boolean isCellEditable(int row, int col) {
        return false;
    }


}
