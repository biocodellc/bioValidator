package edu.berkeley.biocode.validator;



/**
 *
 * Special class to handle messages for reading Spreadsheet files
 */
public class Message {
    public String message;
    public Rule r;
    protected int row;

    public Integer getRow() {
        return this.row + 1;
    }

    private String getRowMessage() {
        String msg = "";
        if (this.row > 0) {
            Integer msgRow = this.row + 1;
            msg = "Row: " + (msgRow).toString() + ": ";
        }
        return msg;
    }

    /**
     * @return Message for this line
     */
    public String getLineMessage() {
        return getRowMessage() + " " + message;
    }
}

class valid extends Message {
    /**
     * @param r Rule
     */
    public valid(Rule r) {
        this.r = r;
        this.row = 0;
    }

    /**
     * @return return message
     */
    public String getLineMessage() {
        return r.getName();
    }
}

class warning extends Message {
    public warning(int row, Rule r, String message) {
        this.row = row;
        this.r = r;
        this.message = message;
    }

    public warning(Rule r, String message) {
        this.r = r;
        this.message = message;
        this.row = 0;
    }


}

class error extends Message {

    public error(Rule r, String message) {
        this.r = r;
        this.message = message;
        this.row = 0;
    }

    public error(int row, Rule r, String message) {
        this.row = row;
        this.message = message;
        this.r = r;
    }

}
