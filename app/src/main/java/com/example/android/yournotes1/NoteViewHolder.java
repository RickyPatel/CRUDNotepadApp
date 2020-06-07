package com.example.android.yournotes1;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NoteViewHolder extends RecyclerView.ViewHolder {

    View v;
    TextView noteText,noteDate,noteTitle;

    public NoteViewHolder(@NonNull View itemView) {
        super(itemView);

        v=itemView;
        noteTitle = v.findViewById(R.id.note_title);
        noteText = v.findViewById(R.id.note_text);
        noteDate = v.findViewById(R.id.note_date);
    }
   public void setNote(Note note)
   {
       noteTitle.setText(note.getNoteTitle());
       noteText.setText(note.getNoteText());
       noteDate.setText(NoteUtils.dateFromLong(note.getNoteDate()));

   }}
