package com.MyWorkingApp.C5V;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by softoz on 1/3/2016.
 */
public class C5VZipper {
    private static String XML_FILE = "c5vRecords.xml";
    private static String EXPORT_FILE = "c5vExport.zip";
    private static String EXPORT_DIRECTORY = "/c5vExportDir";
    private static String EXPORT_AUDIO = "/audio";
    private static String EXPORT_VIDEO = "/video";
    private static String EXPORT_PHOTO = "/photo";
    private static String EXPORT_PEN = "/pen";
    private static String REPORTDATE = "ReportDate";

    private static File sdCard = Environment.getExternalStorageDirectory();

    public String getRecordsXml(boolean forExport,ArrayList<C5VRecord> records) {
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", true);

            serializer.flush();
            writer.write("<?xml-stylesheet type=\"text/xsl\" href=\"c5vhtml.xsl\"?>");
            serializer.startTag("", C5VConfig.RECORDS);
            serializer.attribute("", "number", String.valueOf(records.size()));
            if (forExport) {
                serializer.startTag("", REPORTDATE);
                serializer.text(new SimpleDateFormat().format(new Date()));
                serializer.endTag("", REPORTDATE);
            }
            for (C5VRecord rec : records) {
                serializer.startTag("", C5VConfig.RECORD);

                serializer.startTag("", C5VConfig.DATE);
                serializer.text(rec.getMyRecordDate().toGMTString());
                serializer.endTag("", C5VConfig.DATE);

                serializer.startTag("", C5VConfig.GPS);
                serializer.text(rec.getGPSString());
                serializer.endTag("", C5VConfig.GPS);

                serializer.startTag("", C5VConfig.TITLE);
                serializer.text(rec.getMyRecordTitle());
                serializer.endTag("", C5VConfig.TITLE);

                serializer.startTag("", C5VConfig.TEXT);
                serializer.text(rec.getMyRecordText());
                serializer.endTag("", C5VConfig.TEXT);

                serializer.startTag("", C5VConfig.AUDIO);
                if (forExport) {
                    if (rec.getMyRecordAudio()!="")
                    serializer.text("." + EXPORT_AUDIO + "/" + getLastPathComponent(rec.getMyRecordAudio()));
                }
                else
                    serializer.text(rec.getMyRecordAudio());
                serializer.endTag("", C5VConfig.AUDIO);


                serializer.startTag("", C5VConfig.PHOTO);
                if (forExport) {
                    if (rec.getMyRecordPhoto()!="")
                        serializer.text("."+EXPORT_PHOTO + "/" + getLastPathComponent(rec.getMyRecordPhoto()));
                }
                else
                    serializer.text(rec.getMyRecordPhoto());
                serializer.endTag("", C5VConfig.PHOTO);

                serializer.startTag("", C5VConfig.VIDEO);
                if (forExport) {
                    if (rec.getMyRecordVideo()!="")
                    serializer.text("." + EXPORT_VIDEO + "/" + getLastPathComponent(rec.getMyRecordVideo()));
                }
                else
                    serializer.text(rec.getMyRecordVideo());
                serializer.endTag("", C5VConfig.VIDEO);


                serializer.startTag("", C5VConfig.PEN);
                if (forExport) {
                    if (rec.getMyRecordPen()!="")
                    serializer.text("." + EXPORT_PEN + "/" + getLastPathComponent(rec.getMyRecordPen()));
                }
                else
                    serializer.text(rec.getMyRecordPen());
                serializer.endTag("", C5VConfig.PEN);
                serializer.endTag("", C5VConfig.RECORD);
                if (BuildConfig.FREE && forExport) break;//only export one log
            }
            serializer.endTag("", C5VConfig.RECORDS);
            serializer.endDocument();
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /*
    Build and return a temporary directory with all the export data
     */
    private File buildExportDirectory(ArrayList<C5VRecord> records,Context ctx) {
        //build folder under EXPORT_DIRECTORY
        //1) Make directory
        String directoryString = sdCard.getAbsolutePath() + EXPORT_DIRECTORY;
        File dir = new File(directoryString);
        //delete any previous directory
        //and rebuild directory

        deleteDir(dir);
        if (!dir.mkdir()) {
            //todo toast error message
            return null;
        }
        File audio = new File(directoryString + EXPORT_AUDIO);
        audio.mkdir();
        File photo = new File(directoryString + EXPORT_PHOTO);
        photo.mkdir();
        File video = new File(directoryString + EXPORT_VIDEO);
        video.mkdir();
        File pen = new File(directoryString + EXPORT_PEN);
        pen.mkdir();
        for (C5VRecord rec : records) {
            String filePath = rec.getMyRecordAudio();
            if (filePath != "") {
                copyFile(filePath, directoryString + EXPORT_AUDIO + "/" + getLastPathComponent(filePath));
            }

            filePath = rec.getMyRecordPhoto();
            if (filePath != "") {
                copyFile(filePath, directoryString + EXPORT_PHOTO + "/" + getLastPathComponent(filePath));
            }

            filePath = rec.getMyRecordVideo();
            if (filePath != "") {
                copyFile(filePath, directoryString + EXPORT_VIDEO + "/" + getLastPathComponent(filePath));
            }
            filePath = rec.getMyRecordPen();
            if (filePath != "") {
                copyFile(filePath, directoryString + EXPORT_PEN + "/" + getLastPathComponent(filePath));
            }

            if (BuildConfig.FREE) break;//only one record saved for free version
        }
        //save the xml version of records

        try {
            String xmlFile =new SimpleDateFormat("yyyyMMddhhmmss").format(new Date())+XML_FILE;
            File f = new File(dir, xmlFile);
            f.createNewFile();
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(getRecordsXml(true, records).getBytes());
            fos.close();
            //copy xslt
            f = new File(dir, "c5vhtml.xsl");
            f.createNewFile();
            InputStream in = ctx.getResources().openRawResource(R.raw.c5vhtml);
            FileOutputStream out = new FileOutputStream(f);
            byte[] buff = new byte[1024];
            int read = 0;

            try {
                while ((read = in.read(buff)) > 0) {
                    out.write(buff, 0, read);
                }
            } finally {
                in.close();
                out.close();
            }

        } catch (IOException e) {

            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(ctx, e.getLocalizedMessage(), duration);
            toast.show();
        }
        //return the folder
        return dir;
    }

    /*
    Build and return a zip file with all the export data

    public File getExportZipFile() {
        File dir = buildExportDirectory();
        File res =  zipFileAtPath(dir.getAbsolutePath(), EXPORT_FILE);
        dir.delete();
        return res;
    }*/
    /*
    Build and return a zip file with all the export data
     */
    public File getExportZipFile(ArrayList<C5VRecord> records, Context ctx) {
        File dir = buildExportDirectory(records, ctx);
        try {
            //  zipDir(sdCard.getAbsolutePath()+"/"+EXPORT_FILE, dir.getAbsolutePath());
            zipDir(sdCard.getAbsolutePath() + "/" + EXPORT_FILE, dir.getPath());
        } catch (Exception e) {

            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(ctx, e.getLocalizedMessage(), duration);
            toast.show();
            return null;
        }
        File res = new File(sdCard.getAbsolutePath() + "/" + EXPORT_FILE);
        return res;
    }

    private void copyFile(String inputFullPath, String outputFullPath) {
        InputStream in = null;
        OutputStream out = null;
        try {

            in = new FileInputStream(inputFullPath);
            out = new FileOutputStream(outputFullPath);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file (You have now copied the file)
            out.flush();
            out.close();
            out = null;

        } catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        } catch (Exception e) {
            Log.e("tag", e.getMessage());
        }

    }

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        return dir.delete();
    }

    /*
     * gets the last path component
     *
     * Example: getLastPathComponent("downloads/example/fileToZip");
     * Result: "fileToZip"
     */
    public String getLastPathComponent(String filePath) {
        String[] segments = filePath.split("/");
        String lastPathComponent = segments[segments.length - 1];
        return lastPathComponent;
    }

    private static void zipDir(String zipFileName, String dir) throws Exception {
        try {
            File dirObj = new File(dir);
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName));
            System.out.println("Creating : " + zipFileName);
            addDir(dirObj, out);
            out.close();
        } catch (Exception e) {
            throw e;
        }
    }

    static void addDir(File dirObj, ZipOutputStream out) throws IOException {
        File[] files = dirObj.listFiles();
        byte[] tmpBuf = new byte[1024];
        try {
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    addDir(files[i], out);
                    continue;
                }
                String absPath = files[i].getAbsolutePath();
                int index = absPath.indexOf(EXPORT_DIRECTORY);
                if (index >= 0) {
                    FileInputStream in = new FileInputStream(absPath);
                    System.out.println(" Adding: " + absPath);
                    absPath = absPath.substring(index+1);
                    out.putNextEntry(new ZipEntry(absPath));
                    int len;
                    while ((len = in.read(tmpBuf)) > 0) {
                        out.write(tmpBuf, 0, len);
                    }
                    out.closeEntry();
                    in.close();
                }
            }
        } catch (Exception e) {
            throw e;
        }
    }
}
