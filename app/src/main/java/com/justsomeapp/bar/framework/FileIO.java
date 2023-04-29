package com.justsomeapp.bar.framework;

import android.graphics.Typeface;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface FileIO
{
    public InputStream readAsset(String fileName) throws IOException;
    public Typeface readFont(String fileName) throws IOException;
    public InputStream readFile(String fileName) throws IOException;
    public OutputStream writeFile(String fileName) throws IOException;
}
