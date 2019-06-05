package com.example.shivam_pc.gamemode;

import android.util.Log;

import java.io.File;

public class MyApplication{
    public void clearApplicationData(File appDir) {
        //File appDir = new File(getCacheDir().getParent());
        if (appDir.exists()) {
            for (String s : appDir.list()) {
                if (!s.equals("lib")) {
                    deleteDir(new File(appDir, s));
                    Log.i("TAG", "**************** File /data/data/APP_PACKAGE/" + s + " DELETED *******************");
                }
            }
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String file : children) {
                if (!deleteDir(new File(dir, file))) {
                    return false;
                }
            }
        }
        return dir.delete();
    }
}
