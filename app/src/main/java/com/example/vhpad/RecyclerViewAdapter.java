package com.example.vhpad;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.example.vhpad.databinding.RecyclerviewItemBinding;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>{
    private LayoutInflater layoutInflater;
    private List<Note> noteList;

    public RecyclerViewAdapter(Context context, List<Note> noteList) {
        this.layoutInflater =LayoutInflater.from(context);
        this.noteList = noteList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //View view = layoutInflater.inflate(R.layout.recyclerview_item, parent, false);
        //return new MyViewHolder(view);
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        RecyclerviewItemBinding recyclerviewItemBinding =
                DataBindingUtil.inflate(layoutInflater, R.layout.recyclerview_item, parent, false);

        return new MyViewHolder(recyclerviewItemBinding);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        //Note note = noteList.get(position);
        //final int index = position;
        //holder.textViewNoteText.setText(note.getText());

        holder.bind(noteList.get(position));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = Note.noteList.get(position).getText();
                System.out.println("onItemClick : text = " + text);
            }
        });
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        //final TextView textViewNoteText;
        RecyclerviewItemBinding recyclerviewItemBinding;

        public MyViewHolder(RecyclerviewItemBinding recyclerviewItemBinding) {
            super(recyclerviewItemBinding.getRoot());
            this.recyclerviewItemBinding = recyclerviewItemBinding;

            //super(itemView);
            //textViewNoteText = itemView.findViewById(R.id.textViewNoteText);
        }

        public void bind(Note note){
            recyclerviewItemBinding.setNote(note);
            recyclerviewItemBinding.executePendingBindings();
        }
    }
}
