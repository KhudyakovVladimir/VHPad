package com.example.vhpad;

import android.app.Application;
import androidx.databinding.BindingAdapter;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.AndroidViewModel;

import java.util.concurrent.TimeUnit;

public class VHViewModel extends AndroidViewModel {
    FragmentMainList fragmentMainList;
    FragmentNote fragmentNote;
    FragmentTest fragmentTest;
    FragmentTransaction fragmentTransaction;
    FragmentManager fragmentManager;

    public VHViewModel(Application application) {
        super(application);
    }

    public void setStartScreen(){
        if(Note.noteList != null){
            //FragmentMainList fragmentMainList = new FragmentMainList();
            //FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            //fragmentTransaction.add(R.id.frameLayout, fragmentMainList);
            //fragmentTransaction.commit();
        }
    }
    public void setSingleNoteScreen(){
        fragmentNote = new FragmentNote();
        fragmentManager = fragmentMainList.getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.frameLayout, fragmentNote);
        fragmentTransaction.commitNow();
    }

    public void setListNotesScreen(){
        fragmentTest = new FragmentTest();
        fragmentManager = fragmentMainList.getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.frameLayout, fragmentTest);
        fragmentTransaction.commitNow();
    }
}
