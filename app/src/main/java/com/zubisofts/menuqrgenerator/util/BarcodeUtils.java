package com.zubisofts.menuqrgenerator.util;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class BarcodeUtils {

//    public static Bitmap createBarCode (String uid, BarcodeFormat barcodeFormat, int codeHeight, int codeWidth) {
//
//        try {
//            Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<EncodeHintType, ErrorCorrectionLevel> ();
//            hintMap.put (EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
//
//            Writer codeWriter;
//            if (barcodeFormat == BarcodeFormat.QR_CODE) {
//                codeWriter = new QRCodeWriter();
//            } else if (barcodeFormat == BarcodeFormat.CODE_128) {
//                codeWriter = new Code128Writer();
//            } else {
//                throw new RuntimeException ("Format Not supported.");
//            }
//
//            BitMatrix byteMatrix = codeWriter.encode (
//                    uid,
//                    barcodeFormat,
//                    codeWidth,
//                    codeHeight,
//                    hintMap
//            );
//
//            int width   = byteMatrix.getWidth ();
//            int height  = byteMatrix.getHeight ();
//
//            Bitmap imageBitmap = Bitmap.createBitmap (width, height, Bitmap.Config.ARGB_8888);
//
//            for (int i = 0; i < width; i ++) {
//                for (int j = 0; j < height; j ++) {
//                    imageBitmap.setPixel (i, j, byteMatrix.get (i, j) ? Color.BLACK: Color.WHITE);
//                }
//            }
//
//            return imageBitmap;
//
//        } catch (WriterException e) {
//            e.printStackTrace ();
//            return null;
//        }
//    }


    public static void saveImage(Bitmap b, String id, Activity c) {

//        ActivityCompat.requestPermissions(c, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        File root = Environment.getExternalStorageDirectory();
        //Toast.makeText(c, root.getAbsolutePath(), Toast.LENGTH_LONG).show();
        File myDir = new File(root + "/QRMenu/qr/");
        myDir.mkdirs();
//        if (myDir.mkdirs()) {
//            Toast.makeText(c, root.getAbsolutePath(), Toast.LENGTH_LONG).show();
//
//        }
//        Random generator = new Random();
//        int n = 10000;
//        n = generator.nextInt(n);
        String fname = id + ".jpg";
        File file = new File(myDir, fname);
//            if (file.exists())
//                file.delete();
        try {
            OutputStream out = new FileOutputStream(file);
            b.compress(Bitmap.CompressFormat.JPEG, 100, out);
            Toast.makeText(c, "Image saved successfully",Toast.LENGTH_SHORT).show();
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


        // Tell the media scanner about the new file so that it is
        // immediately available to the user.
//        MediaScannerConnection.scanFile(c, new String[]{file.toString()}, null,
//                new MediaScannerConnection.OnScanCompletedListener() {
//                    public void onScanCompleted(String path, Uri uri) {
//                        Log.i("ExternalStorage", "Scanned " + path + ":");
//                        Log.i("ExternalStorage", "-> uri=" + uri);
//                    }
//                });


    }

    public static Bitmap createQRImage(String content, int widthPix, int heightPix, Bitmap logoBm) {
        try {
            if (content == null || "".equals(content)) {
                return null;
            }

                        // Configuration parameters
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
                        // fault tolerance level
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
                        // Set the width of the blank margin
//            hints.put(EncodeHintType.MARGIN, 2); //default is 4

            // Image data conversion, using matrix conversion
            BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, widthPix, heightPix, hints);
            int[] pixels = new int[widthPix * heightPix];
            // Here, according to the algorithm of the two-dimensional code, the images of the two-dimensional code are generated one by one.
            // Two for loops are the result of a picture scan
            for (int y = 0; y < heightPix; y++) {
                for (int x = 0; x < widthPix; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * widthPix + x] = 0xff000000;
                    } else {
                        pixels[y * widthPix + x] = 0xffffffff;
                    }
                }
            }

            // Generate the format of the QR code image, using ARGB_8888
            Bitmap bitmap = Bitmap.createBitmap(widthPix, heightPix, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, widthPix, 0, 0, widthPix, heightPix);

            if (logoBm != null) {
                bitmap = addLogo(bitmap, logoBm);
            }

            // You must use the compress method to save the bitmap to a file and then read it. The bitmap returned directly is without any compression, and the memory consumption is huge!
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Add a ic_logo pattern in the middle of the QR code
     */
    private static Bitmap addLogo(Bitmap src, Bitmap logo) {
        if (src == null) {
            return null;
        }

        if (logo == null) {
            return src;
        }

                // Get the width and height of the picture
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();
        int logoWidth = logo.getWidth();
        int logoHeight = logo.getHeight();

        if (srcWidth == 0 || srcHeight == 0) {
            return null;
        }

        if (logoWidth == 0 || logoHeight == 0) {
            return src;
        }

        //ic_logo size is 1/5 of the overall size of the QR code
        float scaleFactor = srcWidth * 1.0f / 5 / logoWidth;
        Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(src, 0, 0, null);
            canvas.scale(scaleFactor, scaleFactor, srcWidth / 2, srcHeight / 2);
            canvas.drawBitmap(logo, (srcWidth - logoWidth) / 2, (srcHeight - logoHeight) / 2, null);

//            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.save();
            canvas.restore();
        } catch (Exception e) {
            bitmap = null;
            e.getStackTrace();
        }

        return bitmap;
    }


}
