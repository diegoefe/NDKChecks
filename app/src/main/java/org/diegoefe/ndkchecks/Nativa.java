package org.diegoefe.ndkchecks;

import android.util.Log;

public class Nativa {
    final static private String ltag="ndk-checks";
    private Nativa() {}

    static {
        try {
            Log.i(ltag, "Loading nativa library...");
            System.loadLibrary("nativa");
            Log.i(ltag, "Loaded!");
        }
        catch(Throwable e) {
            Log.e(ltag, "Java loadLibrary error: " + e.getMessage());
        }
    }

    private static Nativa myself_ = null;
    public static Nativa instance() {
        if(null == myself_) {
            myself_ = new Nativa();
        }
        return myself_;
    }

    public native String logTag();
    public native String info();
}
