package com.example.vhpad;

import android.view.View;

public class MyEventHandler {

    public void addNewNote(View view){
        System.out.println("MyEventHandler : addNewNote");
        Note.noteList.remove(0);
        Note.noteList.add(0, new Note("text"));
        //FragmentMainList.recyclerViewAdapter.notifyDataSetChanged();
    }

    public void saveTheNote(View view, String text){
        System.out.println("MyEventHandler : saveTheNote");
        Note.noteList.remove(0);
        Note.noteList.add(0, new Note(text));
        FragmentMainList.recyclerViewAdapter.notifyDataSetChanged();
    }
}
