package com.justsomeapp.bar.framework.impl;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

import java.io.IOException;
import java.io.InputStream;

import com.justsomeapp.bar.framework.Graphics;
import com.justsomeapp.bar.framework.Pixmap;

public class AndroidGraphics implements Graphics
{
    AssetManager assets;
    Bitmap frameBuffer;
    Canvas canvas;
    Paint paint;
    Rect srcRect = new Rect();
    Rect dstRect = new Rect();

    public AndroidGraphics(AssetManager assets, Bitmap frameBuffer)
    {
        this.assets = assets;
        this.frameBuffer = frameBuffer;
        this.canvas = new Canvas(frameBuffer);
        this.paint = new Paint();
    }
    public Pixmap newPixmap(String fileName, PixmapFormat format)
    {
        Bitmap.Config config = null;
        if (format == PixmapFormat.RGB565)
        {
            config = Bitmap.Config.RGB_565;
        }
        else if (format == PixmapFormat.ARGB4444)
        {
            config = Bitmap.Config.ARGB_8888;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = config;

        InputStream in = null;
        Bitmap bitmap = null;
        try
        {
            in = assets.open(fileName);
            bitmap = BitmapFactory.decodeStream(in);
            if (bitmap == null)
            {
                throw new RuntimeException("Couldn't load bitmap from asset '" + fileName + "'");
            }
        }
        catch(IOException e)
        {
            throw new RuntimeException("Couldn't load bitmap from asset '" + fileName + "'");
        }
        finally
        {
            if (in != null)
            {
                try
                {
                    in.close();
                }
                catch(IOException e)
                {}
            }
        }
        if (bitmap.getConfig() == Bitmap.Config.RGB_565)
        {
            format = PixmapFormat.RGB565;
        }
        else if (bitmap.getConfig() == Bitmap.Config.ARGB_4444)
        {
            format = PixmapFormat.ARGB4444;
        }
        else
        {
            format = PixmapFormat.ARGB8888;
        }
        return new AndroidPixmap(bitmap, format);
    }
    public void clear(int color)
    {
        canvas.drawRGB((color & 0xff0000) >> 16, (color & 0xff00) >> 8, (color & 0xff));
    }
    public void drawPixel(int x, int y, int color)
    {
        paint.setColor(color);
        canvas.drawPoint(x, y, paint);
    }
    public void drawLine(int x, int y, int x2, int y2, int color)
    {
        paint.setColor(color);
        canvas.drawLine(x, y, x2, y2, paint);
    }
    public void drawLine(int x, int y, int x2, int y2, int color, int width)
    {
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(width);
        canvas.drawLine(x, y, x2, y2, paint);
    }
    public void drawRect(int x, int y, int width, int height, int color)
    {
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(x, y, x+width-1, y+height-1, paint);
    }
    public void drawRect(int x, int y, int width, int height, int color, float rho)
    {
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);

        int center_x = width/2;
        int center_y = height/2;

        for(int i = 0; i < height; i++)
        {
            int b1_x = 0 - center_x;
            int b1_y = i - center_y;
            float mod = (float) (Math.sqrt(b1_x*b1_x + b1_y*b1_y));

            float alpha1 = (float) (Math.asin(b1_y/mod));
            float beta1  = alpha1 + rho;

            int c1_x = (int) (mod*Math.cos(beta1));
            int c1_y = (int) (mod*Math.sin(beta1));


            int b2_x = width - center_x;
            int b2_y = i - center_y;

            //float alpha2 = (float) (Math.asin(b2_y/mod));
            float beta2 = (float)Math.PI - alpha1 + rho;

            int c2_x = (int) (mod*Math.cos(beta2));
            int c2_y = (int) (mod*Math.sin(beta2));

            c1_x += x + center_x;
            c1_y += y + center_y;
            c2_x += x + center_x;
            c2_y += y + center_y;
            canvas.drawLine(c1_x, c1_y, c2_x, c2_y, paint);
        }
    }
    public void drawPixmap(Pixmap pixmap, int x, int y, int srcX, int srcY, int srcWidth, int srcHeight)
    {
        srcRect.left = srcX;
        srcRect.top = srcY;
        srcRect.right = srcX + srcWidth - 1;
        srcRect.bottom = srcY + srcHeight - 1;

        dstRect.left = x;
        dstRect.top = y;
        dstRect.right = x + srcWidth - 1;
        dstRect.bottom = y + srcHeight - 1;

        canvas.drawBitmap(((AndroidPixmap) pixmap).bitmap, srcRect, dstRect, null);
    }
    public void drawPixmap(Pixmap pixmap, int x, int y)
    {
        canvas.drawBitmap(((AndroidPixmap) pixmap).bitmap, x, y, null);
    }
    public void drawText(Typeface font, String text, String alignment, int size, int x, int y, int color)
    {
        paint.setColor(color);
        paint.setTypeface(font);
        paint.setTextSize(size);
        if (alignment.equals("RIGHT"))
        {
            paint.setTextAlign(Paint.Align.RIGHT);
        }
        else if (alignment.equals("CENTER"))
        {
            paint.setTextAlign(Paint.Align.CENTER);
        }
        else
        {
            paint.setTextAlign(Paint.Align.LEFT);
        }
        canvas.drawText(text, x, y, paint);
    }
    public int getWidth()
    {
        return frameBuffer.getWidth();
    }
    public int getHeight()
    {
        return frameBuffer.getHeight();
    }

    // Returns x, y, width and height of the smallest rectangle enclosing the text
    public int[] getTextBounds(Typeface font, String text, int size, int x, int y)
    {
        Rect rect = new Rect(0,0,0,0);
        paint.setTypeface(font);
        paint.setTextSize(size);
        paint.getTextBounds(text, 0, text.length(), rect);

        return new int[]{x+rect.centerX()-rect.width()/2, y+rect.centerY()-rect.height()/2, rect.width(), rect.height()};
    }
    // Returns x, y, width and height of the smallest+gap rectangle enclosing the text
    public int[] getTextBounds(Typeface font, String text, int size, int x, int y, int gap)
    {
        Rect rect = new Rect(0,0,0,0);
        paint.setTypeface(font);
        paint.setTextSize(size);
        paint.getTextBounds(text, 0, text.length(), rect);

        return new int[]{x-gap+rect.centerX()-rect.width()/2, y-gap+rect.centerY()-rect.height()/2, rect.width()+gap, rect.height()+gap};
    }
}
