package com.noteproject.demo.Model;

import java.util.List;

public class Chord {
    private List<Note> notes;
    private int id;
    private int measureId;
    private int chordNumber;

    public Chord(List<Note> notes) {
        this.notes = notes;
    }
    
    public Chord() {}

    public List<Note> getNotes() {
        return this.notes;
    }

    public void setNote(List<Note> notes) {
        this.notes = notes;
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
