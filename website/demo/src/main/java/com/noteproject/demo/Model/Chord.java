package com.noteproject.demo.Model;

import java.util.ArrayList;
import java.util.List;

public class Chord {
    Note note;  // notes in the chord
    Chord next;

    public Chord(Note note) {
        this.note = note;
        this.next = null;
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

    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<>();
        Note n = this.note;
        while (n != null) {
            notes.add(n);
            n = n.next;
        }
        return notes;
    }

    public void printAllNotes() {
        System.out.println("Chord print start");
        Note n = this.note;
        while (n != null) {
            System.out.println(n.interval);
            n = n.next;
        }
        System.out.println("Chord print end");
    }

    public List<Integer> getAllNoteIntervals() {
        List<Integer> notes = new ArrayList<>();
        while (this.note != null) {
            notes.add(this.note.interval);
            this.note = this.note.getNext();//this.note.next;
        }
        return notes;
    }

}
