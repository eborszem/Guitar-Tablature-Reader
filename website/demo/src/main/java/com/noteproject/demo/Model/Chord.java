package com.noteproject.demo.Model;

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
}
