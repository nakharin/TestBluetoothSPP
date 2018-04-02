package com.emcsthai.bluetooth.sppr210.testbluetoothspp.MyUtility;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

import java.io.UnsupportedEncodingException;

public class EMCSUtility {

    public static Bitmap convertBitmapToGrayScale(Bitmap bmpOriginal) {

        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayScale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayScale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayScale;
    }

    public static String getUTF8FromAsciiBytes(byte[] ascii_bytes) {
        String ascii = null;
        try {
            ascii = new String(ascii_bytes, "TIS620");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] utf8 = null;
        try {
            assert ascii != null;
            utf8 = ascii.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String result = null;
        try {
            assert utf8 != null;
            result = new String(utf8, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        assert result != null;
        return result.substring(0, result.length() - 2);
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
}
