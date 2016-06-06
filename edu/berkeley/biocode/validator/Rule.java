package edu.berkeley.biocode.validator;

import edu.berkeley.biocode.bioValidator.mainPage;
import edu.berkeley.biocode.validator.MetadataElements;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Rule {

    // General values
    private String level;
    private String type;
    private String name;

    // values for DwCLatLngChecker (optional)
    private String decimalLatitude;
    private String decimalLongitude;
    private String maxErrorInMeters;
    private String horizontalDatum;
    private String plateName;
    private String wellNumber;

    // A list of values described in the metadata section (optional)
    private String list;

    private List fields = new ArrayList();

    /**
     * That which has an attribute of list in a ruletype we can look up a generic
     * list of names in the metadata section of the document.
     *
     * @param pList
     * @return
     */
    public List getListElements(String pList) {
        MetadataElements me;
        me = mainPage.bv.metadataElements;
        List<String> dynamicList = null;
        try {
            Field field = me.getClass().getField(pList);
            dynamicList = (List<String>) field.get(me);
        } catch (IllegalArgumentException e) {
            return null;
        } catch (IllegalAccessException e) {
            return null;
        } catch (NoSuchFieldException e) {
            return null;
        }
        return dynamicList;
    }

    public String getList() {
        return list;
    }

    public void setList(String list) {
        this.list = list;
    }

    public String getPlateName() {
        return plateName;
    }

    public void setPlateName(String PlateName) {
        this.plateName = PlateName;
    }

    public String getWellNumber() {
        return wellNumber;
    }

    public void setWellNumber(String WellNumber) {
        this.wellNumber = WellNumber;
    }

    public String getDecimalLatitude() {
        return decimalLatitude;
    }

    public void setDecimalLatitude(String decimalLatitude) {
        this.decimalLatitude = decimalLatitude;
    }

    public String getDecimalLongitude() {
        return decimalLongitude;
    }

    public void setDecimalLongitude(String decimalLongitude) {
        this.decimalLongitude = decimalLongitude;
    }

    public String getMaxErrorInMeters() {
        return maxErrorInMeters;
    }

    public void setMaxErrorInMeters(String maxErrorInMeters) {
        this.maxErrorInMeters = maxErrorInMeters;
    }

    public String getHorizontalDatum() {
        return horizontalDatum;
    }

    public void setHorizontalDatum(String horizontalDatum) {
        this.horizontalDatum = horizontalDatum;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addField(String field) {
        fields.add(field);
    }

    public List getFields() {
        return fields;
    }


    public void print() {
        System.out.println("  rule name=" + this.name);

        for (Iterator i = fields.iterator(); i.hasNext();) {
            String field = (String) i.next();
            System.out.println("  field data : " + field);
        }

    }
}
