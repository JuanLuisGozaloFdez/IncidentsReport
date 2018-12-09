/*
 * Copyright (c) 2018. HuanLu Robotics. Todos los derechos reservados / All rigths reserved.
 */

package com.huanlurobotics.incidentsreport;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.ExifInterface;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class PictureUtils {
    public static Bitmap getScaledBitmap ( String path, int destWidth, int destHeight) {
        Bitmap finalImage = null;
        // read image dimensions from storage to calculate scaled size
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        // find how much size must be scaled
        int inSampleSize = 1;
        if (srcHeight > destHeight || srcWidth > destWidth) {
            if (srcWidth > srcHeight) {
                inSampleSize = Math.round(srcHeight / destHeight);
            } else
                inSampleSize = Math.round(srcWidth / destWidth);
        }
        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        // Read again creating the final bitmap, adjusting orientation
        finalImage = BitmapFactory.decodeFile(path, options);
        return adjustPicture(path, finalImage);
    }

    // Conservative estimation of Bitmap size
    // (to be used in first cycle of Layout like OnCreate(), onStart() and onResume()
    public static Bitmap getScaledBitmap (String path, Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return getScaledBitmap(path, size.x, size.y);
    }

    public static Bitmap adjustPicture(String path, Bitmap realImage ) {

        try {
            File pictureFile = new File(path);
            ExifInterface exif=new ExifInterface(pictureFile.toString());

            if (exif.getAttribute(ExifInterface.TAG_ORIENTATION)
                    .equalsIgnoreCase("6")){
                realImage= rotate(realImage, 90);
            } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION)
                    .equalsIgnoreCase("8")){
                realImage= rotate(realImage, 270);
            } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION)
                    .equalsIgnoreCase("3")){
                realImage= rotate(realImage, 180);
            } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION)
                    .equalsIgnoreCase("0")){
                realImage= rotate(realImage, 90);
            }
        } catch (FileNotFoundException e) {
            Log.d("Info", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d("TAG", "Error accessing file: " + e.getMessage());
        }
        return realImage;
    }

    public static Bitmap rotate(Bitmap bitmap, int degree) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix mtx = new Matrix();
        mtx.setRotate(degree);

        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }
}
