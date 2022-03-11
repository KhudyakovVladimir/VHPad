package com.example.vhpad;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.fragment.app.Fragment;

public class FragmentChanger extends BaseObservable {
    public Fragment fragment;

    public FragmentChanger(Fragment fragment) {
        setFragment(fragment);
    }

    @Bindable
    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
        notifyPropertyChanged(BR.fragment);
    }

    public Fragment setMainListFragment(){
        return new FragmentMainList();
    }

    public Fragment setNoteFragment(){
        return new FragmentNote();
    }
}
