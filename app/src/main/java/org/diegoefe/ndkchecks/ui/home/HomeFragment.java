package org.diegoefe.ndkchecks.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import org.diegoefe.ndkchecks.Nativa;
import org.diegoefe.ndkchecks.R;

import java.util.Locale;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =  ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final Button btnHello = root.findViewById(R.id.idJniHello);
        btnHello.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Nativa nativa = Nativa.instance();
                Toast.makeText(HomeFragment.this.getContext(), String.format(Locale.getDefault(),
                        "Usando nativa:\nBuild on %s,\nLog tag is [%s]",
                                nativa.info(),
                                nativa.logTag()),
                        Toast.LENGTH_LONG).show();
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
}