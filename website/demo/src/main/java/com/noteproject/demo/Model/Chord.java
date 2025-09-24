package com.noteproject.demo.Model;

import java.util.List;

public class Chord {
    private List<Note> notes;
    private int id;
    private int measureId;
    private int chordNumber;
    private ChordDuration duration;  // 1 beat, 2 beats, 4, 8 (.5 beats), 16 (.25 beats), etc.
    /*
     * idea: enum for duration. WHOLE, HALF, QUARTER, EIGHTH, SIXTEENTH
     * after moving duration to chord
     * 
     */



    public Chord(int id, int measureId, int chordNumber, List<Note> notes, ChordDuration duration) {
        this.id = id;
        this.measureId = measureId;
        this.chordNumber = chordNumber;
        this.notes = notes;
        this.duration = duration;
    }

    public Chord(List<Note> notes, ChordDuration duration) {
        this.notes = notes;
        this.duration = duration;
    }
    
    public Chord() {}

    public enum ChordDuration {
        WHOLE,
        HALF,
        QUARTER,
        EIGHTH,
        SIXTEENTH
    }

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

    public ChordDuration getDuration() {
        return this.duration;
    }

    public void setDuration(ChordDuration duration) {
        this.duration = duration;
    }
}
