package edu.berkeley.biocode.photoMatcher;

public class photoListColumn {
        public final String columnName;
        public final Class columnClass;
        public final boolean isEditable;

        public photoListColumn(String columnName, Class columnClass, boolean isEditable) {
            this.columnName = columnName;
            this.columnClass = columnClass;
            this.isEditable = isEditable;
        }
}
