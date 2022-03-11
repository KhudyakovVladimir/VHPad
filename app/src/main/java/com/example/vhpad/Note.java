package com.example.vhpad;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableArrayList;

import java.util.ArrayList;
import java.util.List;

public class Note extends BaseObservable {
    private String text;
    public static List<Note> noteList = getContent();

    public Note(String text) {
        //this.text = text;
        setText(text);
    }

    @Bindable
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
        notifyPropertyChanged(BR.text);
    }
    
    public static List<Note> getContent(){
        Note note1 = new Note("note_1");
        Note note2 = new Note("note_2");
        Note note3 = new Note("note_3");
        Note note4 = new Note("note_4");
        Note note5 = new Note("note_5");
        Note note6 = new Note("note_6");
        Note note7 = new Note("note_7");
        Note note8 = new Note("note_8");
        Note note9 = new Note("note_9");
        Note note10 = new Note("note_10");

        List<Note> list = new ArrayList<>();
        list.add(note1);
        list.add(note2);
        list.add(note3);
        list.add(note4);
        list.add(note5);
        list.add(note6);
        list.add(note7);
        list.add(note8);
        list.add(note9);
        list.add(note10);

        return list;
    }
}
