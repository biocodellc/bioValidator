package edu.berkeley.biocode.validator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by IntelliJ IDEA.
 * User: biocode
 * Date: Mar 18, 2010
 * Time: 3:53:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class MetadataElements {
    public LinkedList metadata = new LinkedList();
    public LinkedList loginNames = new LinkedList();
    public LinkedList list1 = new LinkedList();
    public LinkedList list2 = new LinkedList();
    public LinkedList list3 = new LinkedList();
    public LinkedList list4 = new LinkedList();
    public LinkedList list5 = new LinkedList();
    public LinkedList list6 = new LinkedList();
    public LinkedList list7 = new LinkedList();
    public LinkedList list8 = new LinkedList();
    public LinkedList list9 = new LinkedList();
    public LinkedList list10 = new LinkedList();





    public LinkedList displayFieldsSpreadsheet = new LinkedList();
    public LinkedList displayFieldsDB = new LinkedList();

    public MetadataElements() {
    }

    public void run() {
        // Loop through metadata elements
        for (Iterator i = metadata.iterator(); i.hasNext();) {
            Metadata m = (Metadata) i.next();

            // Invoke Methods in this class based on values in the XML validation file
            Method method = null;
            try {
                method = this.getClass().getMethod(m.getName(), Metadata.class);
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }

            if (method != null) {
                try {
                    method.invoke(this, m);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void addMetadata(Metadata m) {
        metadata.addLast(m);
    }

    public void loginNames(Metadata m) {
        Iterator itFields = m.getFields().iterator();
        while (itFields.hasNext()) loginNames.add(itFields.next().toString());
    }

    public void list1(Metadata m) {
        Iterator itFields = m.getFields().iterator();
        while (itFields.hasNext()) list1.add(itFields.next().toString());
    }

    public void list2(Metadata m) {
        Iterator itFields = m.getFields().iterator();
        while (itFields.hasNext()) list2.add(itFields.next().toString());
    }

    public void list3(Metadata m) {
        Iterator itFields = m.getFields().iterator();
        while (itFields.hasNext()) list3.add(itFields.next().toString());
    }

    public void list4(Metadata m) {
        Iterator itFields = m.getFields().iterator();
        while (itFields.hasNext()) list4.add(itFields.next().toString());
    }

    public void list5(Metadata m) {
        Iterator itFields = m.getFields().iterator();
        while (itFields.hasNext()) list5.add(itFields.next().toString());
    }
     public void list6(Metadata m) {
        Iterator itFields = m.getFields().iterator();
        while (itFields.hasNext()) list6.add(itFields.next().toString());
    }
     public void list7(Metadata m) {
        Iterator itFields = m.getFields().iterator();
        while (itFields.hasNext()) list7.add(itFields.next().toString());
    }
     public void list8(Metadata m) {
        Iterator itFields = m.getFields().iterator();
        while (itFields.hasNext()) list8.add(itFields.next().toString());
    }
     public void list9(Metadata m) {
        Iterator itFields = m.getFields().iterator();
        while (itFields.hasNext()) list9.add(itFields.next().toString());
    }
     public void list10(Metadata m) {
        Iterator itFields = m.getFields().iterator();
        while (itFields.hasNext()) list10.add(itFields.next().toString());
    }

    public void displayFieldsSpreadsheet(Metadata m) {
        Iterator itFields = m.getFields().iterator();
        while (itFields.hasNext()) displayFieldsSpreadsheet.add(itFields.next().toString());
    }

    public void displayFieldsDB(Metadata m) {
        Iterator itFields = m.getFields().iterator();
        while (itFields.hasNext()) displayFieldsDB.add(itFields.next().toString());
    }


}
