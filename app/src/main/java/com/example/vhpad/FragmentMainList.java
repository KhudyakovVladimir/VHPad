package com.example.vhpad;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import com.example.vhpad.databinding.FragmentMainListBinding;

public class FragmentMainList extends Fragment {
    VHViewModel vhViewModel;
    MyEventHandler myEventHandler;
    static RecyclerViewAdapter recyclerViewAdapter;
    Note note;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_list,null);

        myEventHandler = new MyEventHandler();
        //vhViewModel = new VHViewModel(getActivity().getApplication());
        recyclerViewAdapter = new RecyclerViewAdapter(getActivity(), Note.noteList);
        note = Note.noteList.get(0);

        FragmentMainListBinding fragmentMainListBinding =
                DataBindingUtil.setContentView(getActivity(), R.layout.fragment_main_list);

        fragmentMainListBinding.setHandler(myEventHandler);
        //fragmentMainListBinding.setViewModel(vhViewModel);
        fragmentMainListBinding.recyclerView.setAdapter(recyclerViewAdapter);
        fragmentMainListBinding.setNote(note);

        return view;
    }
}
