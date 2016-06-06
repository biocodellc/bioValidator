package edu.berkeley.biocode.utils;

import java.io.*;

public class CopyFile {
    public static boolean run(String srFile, String dtFile) {
        try {
            File f1 = new File(srFile);
            File f2 = new File(dtFile);
            InputStream in = new FileInputStream(f1);

            //For Overwrite the file.
            OutputStream out = new FileOutputStream(f2);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            System.out.println("File copied.");
        }
        catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage() + " in the specified directory.");
            return false;
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }
}
