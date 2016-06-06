package edu.berkeley.biocode.photoMatcher;



public class photoListRow {
        private String specimen;
        private String file;

        public photoListRow(String file, String specimen) {
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