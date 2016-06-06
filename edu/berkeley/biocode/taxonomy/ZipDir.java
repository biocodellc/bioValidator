package edu.berkeley.biocode.taxonomy;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ZipDir {
    public static void zipDir(String zipFileName, String dir, String outputDir) {
        File dirObj = new File(dir);
        if (!dirObj.isDirectory()) {
            System.err.println(dir + " is not a directory");
            System.exit(1);
        }

        try {

            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outputDir + File.separator + zipFileName));
            System.out.println("Creating : " + zipFileName);

            addDir(dirObj, out);
            // Complete the ZIP file
            out.close();

            // magicSquare
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

    private static void addDir(File dirObj, ZipOutputStream out) throws IOException {
        File[] files = dirObj.listFiles();
        byte[] tmpBuf = new byte[1024];

        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                addDir(files[i], out);
                continue;
            }

            FileInputStream in = new FileInputStream(files[i].getAbsolutePath());
            System.out.println(" Adding: " + files[i].getPath());

            out.putNextEntry(new ZipEntry(files[i].getName()));

            // Transfer from the file to the ZIP file
            int len;
            while ((len = in.read(tmpBuf)) > 0) {
                out.write(tmpBuf, 0, len);
            }

            // Complete the entry
            out.closeEntry();
            in.close();
        }
    }

    public static void main(String[] args) {

        if (args.length < 2) {
            System.err.println("Usage: <zip file name> <Complete path to directory> <output directory to store zip file>");
        } else {
            zipDir(args[0], args[1], args[2]);
        }
    }

    public static void unzip(File zipfile, File directory) throws IOException {
        System.out.println(directory.getAbsolutePath() + ":" + zipfile.getAbsolutePath());
        ZipFile zfile = null;
            zfile = new ZipFile(zipfile);
        
        Enumeration<? extends ZipEntry> entries = zfile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            File file = new File(directory, entry.getName());
            if (entry.isDirectory()) {
                file.mkdirs();
            } else {
                file.getParentFile().mkdirs();
                InputStream in = zfile.getInputStream(entry);
                try {
                    copy(in, file);
                } finally {
                    in.close();
                }
            }
        }
    }

    private static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        while (true) {
            int readCount = in.read(buffer);
            if (readCount < 0) {
                break;
            }
            out.write(buffer, 0, readCount);
        }
    }

    private static void copy(File file, OutputStream out) throws IOException {
        InputStream in = new FileInputStream(file);
        try {
            copy(in, out);
        } finally {
            in.close();
        }
    }

    private static void copy(InputStream in, File file) throws IOException {
        OutputStream out = new FileOutputStream(file);
        try {
            copy(in, out);
        } finally {
            out.close();
        }
    }


}