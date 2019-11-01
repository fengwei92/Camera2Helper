package com.stanic.camerahelper;

import android.util.Size;

import java.util.Comparator;

public class CompareSizesByArea implements Comparator<Size> {
    @Override
    public int compare(Size size, Size t1) {
        return Long.signum((long)size.getWidth() * size.getHeight() - (long)t1.getWidth() * t1.getHeight());
    }
}
