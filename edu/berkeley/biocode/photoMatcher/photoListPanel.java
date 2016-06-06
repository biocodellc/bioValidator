package edu.berkeley.biocode.photoMatcher;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;


public class photoListPanel extends JPanel {
    public JTable table;
    public photoListModel photostablemodel = new photoListModel();
    private panelImageDisplay panelImageView;
    private tinySpreadsheetViewer panelSpecimenView;
    private labelImage labelForImageDisplay;
    private String directory;


    public photoListPanel() {
        super(new GridLayout(1, 0));
    }

    public void init(panelImageDisplay panelImageView, tinySpreadsheetViewer panelSpecimenView, String directory) {
        this.panelImageView = panelImageView;
       // this.panelSpecimenView = panelSpecimenView;
        this.directory = directory;

        // clear all rows from table model
        photostablemodel.getDataVector().removeAllElements();

        //table = new (photostablemodel);
        table = new JTable(photostablemodel) {
            @Override
            public Component prepareRenderer(TableCellRenderer tcr, int row, int column) {
                Component c = super.prepareRenderer(tcr, row, column);
                c.setForeground(getForeground());
                return c;
            }
        };


        table.setAutoCreateColumnsFromModel(false);

        table.setRowSelectionAllowed(true);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.getSelectionModel().addListSelectionListener(new RowListener());
        table.setIntercellSpacing(new Dimension());
        table.setShowGrid(false);
        table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        table.setPreferredScrollableViewportSize(new Dimension(500, 70));
        //Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setAutoscrolls(true);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        //Add the scroll pane to this panel.
        add(scrollPane);
        this.updateUI();
    }

    private void displaySpecimen() {
        int row = table.getSelectedRow();
        int column = 1;
        final String cellValue = (String) table.getValueAt(row, column);

        panelSpecimenView.removeAll();
        panelSpecimenView.update(cellValue);
    }

    private void displayImage() {
        int row = table.getSelectedRow();
        int column = 0;

        String pathName = directory + "/" + table.getValueAt(row, column);

        // Display image we just clicked on in the display window
        labelForImageDisplay = new labelImage(pathName, 500, 500);
        panelImageView.removeAll();
        panelImageView.add(labelForImageDisplay, 0);
        panelImageView.updateUI();

    }

    public void sort() {
        sortAllRowsBy(photostablemodel, 0, true);
    }

// Regardless of sort order (ascending or descending), null values always appear last.

    // colIndex specifies a column in model.
    public void sortAllRowsBy(photoListModel model, int colIndex, boolean ascending) {
        Vector data = model.getDataVector();
        Collections.sort(data, (Comparator) (new ColumnSorter(colIndex, ascending)));
        model.fireTableStructureChanged();
        ;
    }

    // This comparator is used to sort vectors of data
    public class ColumnSorter implements Comparator {
        int colIndex;
        boolean ascending;

        ColumnSorter(int colIndex, boolean ascending) {
            this.colIndex = colIndex;
            this.ascending = ascending;
        }

        public int compare(Object a, Object b) {
            Vector v1 = (Vector) a;
            Vector v2 = (Vector) b;
            Object o1 = v1.get(colIndex);
            Object o2 = v2.get(colIndex);

            // Treat empty strains like nulls
            if (o1 instanceof String && ((String) o1).length() == 0) {
                o1 = null;
            }
            if (o2 instanceof String && ((String) o2).length() == 0) {
                o2 = null;
            }

            // Sort nulls so they appear last, regardless
            // of sort order
            if (o1 == null && o2 == null) {
                return 0;
            } else if (o1 == null) {
                return 1;
            } else if (o2 == null) {
                return -1;
            } else if (o1 instanceof Comparable) {
                if (ascending) {
                    return o1.toString().compareToIgnoreCase(o2.toString());

                    //return ((Comparable) o1).compareTo(o2);
                } else {
                    return o2.toString().compareToIgnoreCase(o1.toString());

                    //return ((Comparable) o2).compareTo(o1);
                }
            } else {
                if (ascending) {
                    return o1.toString().compareToIgnoreCase(o2.toString());
                } else {
                    return o2.toString().compareToIgnoreCase(o1.toString());
                }
            }
        }
    }


    private class RowListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent event) {
            if (event.getValueIsAdjusting()) {
                return;
            }
            // only display image & specimen information if just one is selected
            if (table.getSelectedRows().length == 1) {
                displayImage();
                // TODO: re-enable this--- for now just turning this off since conversion to dynamic photonaming complicated things
                //displaySpecimen();
            } else {
                panelImageView.removeAll();
                panelImageView.updateUI();
                //panelSpecimenView.removeAll();
                //panelSpecimenView.updateUI();
            }

        }

    }

    class rowPhotoList {
        private String specimen;
        private String file;

        public rowPhotoList(String file, String specimen) {
            this.specimen = specimen;
            this.file = file;
        }

        public void setName(String str) {
            this.specimen = str;
        }

        public void setFile(String file) {
            this.file = file;
        }

        public String getSpecimen() {
            return specimen;
        }

        public String getFile() {
            return file;
        }

    }
}


