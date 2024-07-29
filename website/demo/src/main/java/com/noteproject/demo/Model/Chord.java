package com.noteproject.demo.Model;

import java.util.ArrayList;
import java.util.List;

public class Chord {
    Note note;  // notes in the chord
    public Chord next;

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

        if (n != null) System.out.println("n.interval="+n.interval);
        else System.out.println("fail 1");
        if (n.next != null) System.out.println("n.next.interval="+n.next.interval);
        else System.out.println("fail 2");
        if (n.next.next != null) System.out.println("n.next.next.interval="+n.next.next.interval);
        else System.out.println("fail 3");
        
        while (n != null) {
            notes.add(n);
            System.out.println("interval="+n.interval);
            n = n.next;//this.note.next;
        }
        return notes;
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
