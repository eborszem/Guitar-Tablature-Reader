package com.noteproject.demo.Model;
public class Chord {
    Note note;  // notes in the chord
    public Chord next;
    Chord prev;
    //Chord prev;
    public Chord(Note note) {
        this.note = note;
        this.next = null;
        this.prev = null;
    }
    /*Chord(Note[] notes) {
        this.notes = notes;
        this.next = null;
        this.prev = null;
    }*/

}
