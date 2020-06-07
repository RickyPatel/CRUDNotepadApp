package com.example.android.yournotes1;

import java.util.Comparator;

public class Note {

    public String noteId;
    public String noteTitle;
    public String noteText;

    public Note(String noteId, String noteTitle, String noteText, long noteDate, int color) {
        this.noteId = noteId;
        this.noteTitle = noteTitle;
        this.noteText = noteText;
        this.noteDate = noteDate;
        this.color = color;
    }

    public long getNoteDate() {
        return noteDate;
    }

    public void setNoteDate(long noteDate) {
        this.noteDate = noteDate;
    }

    public long noteDate;

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    int color;


    public String getNoteId() {
        return noteId;
    }
    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public Note() { }



    public String getNoteText() {
        return noteText;
    }
    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }



    public String getNoteTitle() {
        return noteTitle;
    }
    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

}
