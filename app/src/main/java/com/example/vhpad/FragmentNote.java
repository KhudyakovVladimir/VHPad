package com.example.vhpad;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import com.example.vhpad.databinding.FragmentNoteBindingImpl;

public class FragmentNote extends Fragment {
    VHViewModel vhViewModel;
    MyEventHandler myEventHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note, null);

        vhViewModel = new VHViewModel(getActivity().getApplication());
        myEventHandler = new MyEventHandler();

        FragmentNoteBindingImpl fragmentNoteBinding =
                DataBindingUtil.setContentView(getActivity(), R.layout.fragment_note);
        fragmentNoteBinding.setHandler(myEventHandler);
        fragmentNoteBinding.setViewModel(vhViewModel);

        return view;
    }
}
