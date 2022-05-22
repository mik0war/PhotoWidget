package ru.mik0war.photowidget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.mik0war.photowidget.Utils.Settings;

public class ImageFileExplorer {
    private final Context context;

    private String directoryName;
    private String fileName;
    private String fileFormat;
    private boolean external;


    public ImageFileExplorer(Context context) {
        this.context = context;
        this.setDirectoryName(Settings.DEFAULT_DIRECTORY.getValue());
        this.setFileFormat(Bitmap.CompressFormat.PNG);
    }

    public ImageFileExplorer setFileName(String ... fileName) {
        this.fileName = fileName.length != 0 ?  getFinalFileName(fileName[0], fileName[0]) :
                                                setDefaultFileName();
        return this;
    }

    @NonNull
    private String setDefaultFileName(){
        String fileName = new SimpleDateFormat("ddMMyyyy_HHmmss", Locale.ROOT)
                .format(new Date());

        if(!checkFileExist(fileName))
            return setFullFileName(fileName);
        else
            return setFullFileName(getFinalFileName(fileName, fileName));
    }

    private String getFinalFileName(String fileName, String finalFileName){
        while (true) {
            if (checkFileExist(finalFileName))
                finalFileName = incrementRepeatedFileName(fileName, finalFileName);
            else
                return finalFileName;
        }
    }

    private String setFullFileName(String fileName){
        return fileName + "." + fileFormat;
    }


    private String incrementRepeatedFileName(String fileName, String repeatedFileName){
        return fileName + "(" + getRepeatedFileNumber(repeatedFileName) + ")";
    }

    private boolean checkFileExist(String fileName){
        return createFile(setFullFileName(fileName)).exists();
    }

    private static int getRepeatedFileNumber(String filename){
        Pattern pattern = Pattern.compile("\\([0-9]+\\)");
        Matcher matcher = pattern.matcher(filename);
        if (matcher.find())
            return Integer.parseInt(
                    matcher.group(0).substring(1, matcher.group(0).length() - 1)) + 1;
        else
            return 1;
    }

    public ImageFileExplorer setFileFormat(Bitmap.CompressFormat fileFormat) {
        this.fileFormat = fileFormat.toString().toLowerCase(Locale.ROOT);
        return this;
    }

    public ImageFileExplorer setExternal(boolean external) {
        this.external = external;
        return this;
    }

    public ImageFileExplorer setDirectoryName(String ... directoryName) {
        this.directoryName = directoryName.length != 0 ?    directoryName[0] :
                                                            Settings.DEFAULT_DIRECTORY.getValue();
        return this;
    }

    public void save(Bitmap bitmapImage) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(createFile(fileName));
            bitmapImage.compress(Bitmap.CompressFormat.valueOf(fileFormat.toUpperCase(Locale.ROOT)), 100, fileOutputStream);
            fileOutputStream.flush();
        } catch (Exception e) {
            Log.e("ImageFileExplorer",e.getMessage());
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                Log.e("ImageFileExplorer",e.getMessage());
            }
        }
    }

    @NonNull
    private File createFile(String fileName) {
        File directory;
        if(external){
            directory = getAlbumStorageDir(directoryName);
        }
        else {
            directory = context.getDir(directoryName, Context.MODE_PRIVATE);
        }
        if(!directory.exists() && !directory.mkdirs()){
            Log.e("ImageFileExplorer","Error while creating directory " + directoryName);
        }

        return new File(directory, fileName);
    }

    private File getAlbumStorageDir(String albumName) {
        return new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    public Bitmap load() {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(createFile(fileName));
            return BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            Log.e("ImageFileExplorer",e.getMessage());
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                Log.e("ImageFileExplorer",e.getMessage());
            }
        }
        return null;
    }

}
