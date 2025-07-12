package com.noteproject.demo.Model;

public class Measure {
    Chord chord; // chords objects in the measure
    public Measure next;
	private int noteValue; // which note gets the beat. Quarter, half, etc.
	private int numNoteValuesPerMeasure;  // how many note beats are present in the measure
	// private int numBeats = noteValue * numNoteValuesPerMeasure;
    private int id;
    private int measureNumber;
    private int compositionId;

	// time signature is numBeats over noteBeat
	public Measure(Chord chord) {
		this.chord = chord;
        this.next = null;
	}
    
    public Measure() {
        
    }

    public Chord getChord() {
        return this.chord;
    }

    public void setChord(Chord chord) {
        this.chord = chord;
    }

    public Measure getNext() {
        return this.next;
    }

    public void setNext(Measure next) {
        this.next = next;
    }

    public int getNoteValue() {
        return this.noteValue;
    }

    public void setNoteValue(int noteValue) {
        this.noteValue = noteValue;
    }

    public int getNumNoteValuesPerMeasure() {
        return this.numNoteValuesPerMeasure;
    }

    public void setNumNoteValuesPerMeasure(int numNoteValuesPerMeasure) {
        this.numNoteValuesPerMeasure = numNoteValuesPerMeasure;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCompositionId() {
        return this.compositionId;
    }

    public void setCompositionId(int setCompositionId) {
        this.compositionId = setCompositionId;
    }

    public int getMeasureNumber() {
        return this.measureNumber;
    }

    public void setMeasureNumber(int measureNumber) {
        this.measureNumber = measureNumber;
    }

}
