package com.justsomeapp.bar.framework.impl;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.justsomeapp.bar.framework.FileIO;

// The Game interface implementation will hold an instance of this class and return it via Game.getFileIO().
public class AndroidFileIO implements FileIO
{
    Context context;
    AssetManager assets;
    String externalStoragePath;

    String FILENAME = "barprefs";


    public AndroidFileIO(Context context)
    {
        this.context = context;
        this.assets = context.getAssets();
        this.externalStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator;
    }
    public InputStream readAsset(String fileName) throws IOException
    {
        return assets.open(fileName);
    }
    public Typeface readFont(String fileName) throws IOException
    {
        return Typeface.createFromAsset(context.getAssets(), fileName+".ttf");
    }
    public InputStream readFile(String fileName) throws IOException
    {
        return context.openFileInput(FILENAME);

        //fos.write("asd".getBytes());
        //fos.close();

        //return new FileInputStream(externalStoragePath + fileName);
    }
    public OutputStream writeFile(String fileName) throws IOException
    {
        return context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
        //return new FileOutputStream(externalStoragePath + fileName);
    }
}
