package com.longkd.chatgpt_openai.base.util;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.CancellationSignal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AssetsProvider extends ContentProvider {

    @Override
    public AssetFileDescriptor openAssetFile(Uri uri, String mode) {
        AssetManager am = getContext().getAssets();
        String file_name = uri.getPath().substring(1);
        AssetFileDescriptor afd = null;

        try {
            afd = am.openFd(file_name);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return afd;
    }

    @Override
    public String getType(Uri p1) {
        return null;
    }

    @Override
    public int delete(Uri p1, String p2, String[] p3) {
        return 0;
    }

    @Override
    public Cursor query(Uri p1, String[] p2, String p3, String[] p4, String p5) {

        return null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, CancellationSignal cancellationSignal) {
        return super.query(uri, projection, selection, selectionArgs, sortOrder, cancellationSignal);
    }

    @Override
    public Uri insert(Uri p1, ContentValues p2) {
        return null;
    }

    @Override
    public boolean onCreate() {
        File f = new File(getContext().getFilesDir(), "bg_sticker_02.png");

        if (!f.exists()) {
            AssetManager assets = getContext().getAssets();

            try {
                copy(assets.open("bg_sticker_02.png"), f);
            } catch (IOException e) {
                return (false);
            }
        }

        return false;
    }

    static void copy(InputStream in, File dst)
            throws IOException {
        FileOutputStream out = new FileOutputStream(dst);
        byte[] buf = new byte[1024];
        int len;

        while ((len = in.read(buf)) >= 0) {
            out.write(buf, 0, len);
        }

        in.close();
        out.close();
    }

    @Override
    public int update(Uri p1, ContentValues p2, String p3, String[] p4) {
        return 0;
    }
}