package com.noteproject.demo.Model;

import java.util.List;

public class Chord {
    private int id;
    private int measureId;
    private int index; // placement of chord relative to other chords in measure
    private List<Note> notes;
    private ChordDuration duration;
    
    public enum ChordDuration { WHOLE, HALF, QUARTER, EIGHTH, SIXTEENTH }
    
    public Chord(int id, int measureId, int index, List<Note> notes, ChordDuration duration) {
        this.id = id;
        this.measureId = measureId;
        this.index = index;
        this.notes = notes;
        this.duration = duration;
    }

    public Chord(List<Note> notes, ChordDuration duration) {
        this.notes = notes;
        this.duration = duration;
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

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public ChordDuration getDuration() {
        return this.duration;
    }

    public void setDuration(ChordDuration duration) {
        this.duration = duration;
    }
}
