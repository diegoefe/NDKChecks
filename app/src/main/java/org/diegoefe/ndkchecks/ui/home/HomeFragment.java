package org.diegoefe.ndkchecks.ui.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.icu.text.UnicodeSetSpanner;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import org.diegoefe.ndkchecks.Consts;
import org.diegoefe.ndkchecks.MiThread;
import org.diegoefe.ndkchecks.Nativa;
import org.diegoefe.ndkchecks.R;

import java.util.Locale;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    private MiReceiver receiver = new HomeFragment.MiReceiver();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =  ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final Context ctx = getContext();
        final Button btnJavaT = root.findViewById(R.id.idJavaThread);
        ctx.registerReceiver(receiver, new IntentFilter(Consts.FilterName));
        btnJavaT.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeFragment.this.getContext(), "Creando Java thread!", Toast.LENGTH_SHORT).show();
                new Thread(new MiThread(ctx)).start();
            }
        });

        final Button btnCppT = root.findViewById(R.id.idCppThread);
        ctx.registerReceiver(receiver, new IntentFilter(Consts.FilterName));
        btnCppT.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeFragment.this.getContext(), "Creando C++ thread!", Toast.LENGTH_SHORT).show();
                //new Thread(new MiThread(ctx)).start();
            }
        });

        final Button btnHello = root.findViewById(R.id.idJniHello);
        btnHello.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Nativa nativa = Nativa.instance();
                Toast.makeText(HomeFragment.this.getContext(), String.format(Locale.getDefault(),
                        "Usando nativa:\nBuild on %s,\nLog tag is [%s]",
                        nativa.info(),
                        Consts.ltag),
                        Toast.LENGTH_SHORT).show();
            }
        });

        homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                btnHello.setText(s);
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        getContext().unregisterReceiver(receiver);
        super.onDestroyView();
    }

    static class MiReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(Consts.ltag, "MiReceiver.onReceive");
            Bundle extras = intent.getExtras();
            if(null != extras) {
                String type = extras.getString(Consts.Type, "nada de type");
                String payload = extras.getString(Consts.Payload, "nada de payload");
                final String msg = String.format(Locale.getDefault(), "Received message of type %s: %s", type, payload);
                Log.d(Consts.ltag, msg);
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        }
    }
}