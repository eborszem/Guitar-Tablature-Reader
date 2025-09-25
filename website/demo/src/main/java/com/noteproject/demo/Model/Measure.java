package com.noteproject.demo.Model;

import java.util.List;

public class Measure {
    private int id;
    private int index; // placement of measure relative to other measures in composition
    private int compositionId;
    private List<Chord> chords;

	public Measure(List<Chord> chords) {
		this.chords = chords;
	}
    
    public Measure(int id, int index, int compositionId) {
        this.id = id;
        this.index = index;
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

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
