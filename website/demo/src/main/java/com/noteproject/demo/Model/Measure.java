package com.noteproject.demo.Model;

import java.util.List;

public class Measure {
private List<Chord> chords;
private int id;
private int measureNumber;
private int compositionId;

	public Measure(List<Chord> chords) {
		this.chords = chords;
	}
    
    public Measure(int id, int measureNumber, int compositionId) {
        this.id = id;
        this.measureNumber = measureNumber;
        this.compositionId = compositionId;
	}

    public List<Chord> getChords() {
        return this.chords;
    }

    public void setChords(List<Chord> chords) {
        this.chords = chords;
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
