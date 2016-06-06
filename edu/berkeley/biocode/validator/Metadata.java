package edu.berkeley.biocode.validator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Metadata {

    private List fields = new ArrayList();
    private String name;

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
        for (Iterator i = fields.iterator(); i.hasNext(); ) {
            String field = (String) i.next();
            System.out.println("  field data : " + field);
        }

    }
}

