package com.noteproject.demo.Model;

import java.util.ArrayList;
import java.util.List;

public class Measure {
    Chord chord;  // chords in the measure
    public Measure next;
	//Measure prev;
	int noteBeat;  // which note gets the beat. Quarter, half, etc.
	int numNoteBeatsPerMeasure;  // how many note beats are present in the measure
	int numBeats = noteBeat * numNoteBeatsPerMeasure;
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
                System.out.print(m.chord.note.interval);
                m.chord.note = m.chord.note.next;
            }
            System.out.print(":" + duration + "\n");
            m.chord = m.chord.next;
        }
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

    public int getNoteBeat() {
        return this.noteBeat;
    }

    public void setNoteBeat(int noteBeat) {
        this.noteBeat = noteBeat;
    }

    public int getNumNoteBeatsPerMeasure() {
        return this.numNoteBeatsPerMeasure;
    }

    public void setNumNoteBeatsPerMeasure(int numNoteBeatsPerMeasure) {
        this.numNoteBeatsPerMeasure = numNoteBeatsPerMeasure;
    }

    public int getNumBeats() {
        return this.numBeats;
    }

    public void setNumBeats(int numBeats) {
        this.numBeats = numBeats;
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

}
