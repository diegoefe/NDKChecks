package org.diegoefe.ndkchecks;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

public final class MiThread implements Runnable {

    private NHandler handler_;
    public MiThread(Context _ctx) {
        handler_ = new NHandler(_ctx);
    }

    @Override
    public void run() {
        SystemClock.sleep(4000);
        Message msg=new Message();
        Bundle bundle = new Bundle();
        bundle.putString(Consts.Type, Consts.TypeMyThread);
        bundle.putString(Consts.Payload, "NHandler is running");
        msg.setData(bundle);
        handler_.sendMessage(msg);

        SystemClock.sleep(7000);
        bundle.putString(Consts.Type, Consts.TypeMyThread);
        bundle.putString(Consts.Payload, "NHandler finished");
        Message msg2 =new Message();
        msg2.setData(bundle);
        handler_.sendMessage(msg2);
    }

    static class NHandler extends Handler {
        Context from_;
        NHandler(Context _from) {
            from_ =_from;
        }
        @Override
        public void handleMessage(android.os.Message msg) {
            Log.d(Consts.ltag, "NHandler.handleMessage");
            Bundle bundle = msg.getData();
            Intent i = new Intent(Consts.FilterName);
            i.putExtra(Consts.Type, bundle.getString(Consts.Type));
            i.putExtra(Consts.Payload, bundle.getString(Consts.Payload));
            Log.d(Consts.ltag, "NHandler.handleMessage DONE");
            from_.sendBroadcast(i);
        }
    }
}
