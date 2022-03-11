package com.example.vhpad;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.databinding.DataBindingUtil;
import com.example.vhpad.databinding.VhViewBinding;

public class VHView extends AppCompatActivity {
    VHViewModel vhViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vh_view);

        VhViewBinding vhViewBinding = DataBindingUtil.setContentView(this, R.layout.vh_view);

        vhViewModel = new VHViewModel(getApplication());
        vhViewModel.setListNotesScreen();
        vhViewBinding.setViewModel(vhViewModel);



        //vhViewModel.setStartScreen();
        //FragmentMainList fragmentMainList = new FragmentMainList();
        //Fragment fragment = new FragmentMainList();
        //FragmentChanger fragmentChanger = new FragmentChanger();
        //FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        //fragmentTransaction.add(R.id.frameLayout, fragment);
        //fragmentTransaction.commit();
    }
}