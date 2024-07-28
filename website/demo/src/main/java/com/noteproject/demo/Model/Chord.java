package com.noteproject.demo.Model;

import java.util.ArrayList;
import java.util.List;

public class Chord {
    Note note;  // notes in the chord
    public Chord next;
    Chord prev;

    public Chord(Note note) {
        this.note = note;
        this.next = null;
        this.prev = null;
    }

    public Note getNote() {
        return this.note;
    }

    public void setNote(Note note) {
        this.note = note;
    }

    public Chord getNext() {
        return this.next;
    }

    public void setNext(Chord next) {
        this.next = next;
    }

    public Chord getPrev() {
        return this.prev;
    }

    public void setPrev(Chord prev) {
        this.prev = prev;
    }

    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<>();
        while (this.note != null) {
            notes.add(this.note);
            this.note = this.note.getNext();//this.note.next;
        }
        return notes;
    }

}
