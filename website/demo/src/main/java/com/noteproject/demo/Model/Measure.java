package com.noteproject.demo.Model;

import java.util.ArrayList;
import java.util.List;

public class Measure {
    Chord chord; // chords objects in the measure
    public Measure next;
	private int noteValue; // which note gets the beat. Quarter, half, etc.
	private int numNoteValuesPerMeasure;  // how many note beats are present in the measure
	private int numBeats = noteValue * numNoteValuesPerMeasure;
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

    // note to self: this function moves the heads of the lists, resulting in them becoming null after this is called
    public void printMeasure(Measure m) {
        while (m.chord != null) {
            int duration = m.chord.note.duration;
            while (m.chord.note != null) {
                m.chord.note = m.chord.note.next;
            }
            System.out.print(":" + duration + "\n");
            m.chord = m.chord.next;
        }
    }

    public Measure(int noteValue, int numNoteValuesPerMeasure) {
        this.noteValue = noteValue;
        this.numNoteValuesPerMeasure = numNoteValuesPerMeasure;
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

    public List<Chord> getAllChords() {
        List<Chord> chords = new ArrayList<>();
        Chord c = this.chord;
        while (c != null) {
            chords.add(c);
            c = c.next;
        }
        return chords;
    }

    // All measures can be accessed linearly via the first measure in the composition
    public List<Measure> getAllMeasures() {
        List<Measure> measures = new ArrayList<>();
        Measure m = this;
        while (m != null) {
            measures.add(m);
            m = m.next;
        }
        System.out.println("measure object val="+this);
        return measures;
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
