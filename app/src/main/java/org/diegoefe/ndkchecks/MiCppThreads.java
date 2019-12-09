package org.diegoefe.ndkchecks;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.util.Locale;

public class MiCppThreads {
    private CBHandler handler_;

    public MiCppThreads(Callbacks _callbacks) {
        handler_ = new CBHandler(_callbacks);
    }
    public void start() {
        Nativa.instance().startNativeThreads(handler_);
    }
    public void stop() {
        Nativa.instance().stopNativeThreads();
    }

    public interface Callbacks {
        void healthMessage(String msg);
    }

    static class CBHandler extends Handler {
        private Callbacks callbacks_;
        CBHandler(Callbacks _cbs)
        {
            callbacks_ = _cbs;
        }
        @Override
        public void handleMessage(android.os.Message msg) {
            Log.d(Consts.ltag, "CBHandler.handleMessage");
            String s = msg.obj.toString();
            Log.d(Consts.ltag, String.format(Locale.getDefault(), "CBHandler.handleMessage (%s)", s));
            callbacks_.healthMessage(s);
            Log.d(Consts.ltag, "CBHandler.handleMessage DONE");
        }
    }
}
