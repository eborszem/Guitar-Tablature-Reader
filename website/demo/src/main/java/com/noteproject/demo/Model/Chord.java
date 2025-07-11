package com.noteproject.demo.Model;

import java.util.ArrayList;
import java.util.List;

public class Chord {
    Note note;  // notes in the chord
    Chord next;
    private int id;
    private int measureId;
    private int chordNumber;

    public Chord(Note note) {
        this.note = note;
        this.next = null;
    }
    
    public Chord() {

    }

    public Chord(String string, String string2, String string3, String string4, String string5, String string6,
            int int1, int int2) {
        //TODO Auto-generated constructor stub
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

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMeasureId() {
        return this.measureId;
    }

    public void setMeasureId(int measureId) {
        this.measureId = measureId;
    }

    public int getChordNumber() {
        return this.chordNumber;
    }

    public void setChordNumber(int chordNumber) {
        this.chordNumber = chordNumber;
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

    public boolean allRests() {
        Note n = this.note;
        while (n != null) {
            if (n.fretNumber != -1) {
                return false;
            }
            n = n.next;
        }
        return true;
    }
}
