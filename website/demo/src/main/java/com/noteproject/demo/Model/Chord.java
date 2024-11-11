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

    public void printAllNotes() {
        System.out.println("Chord print start");
        Note n = this.note;
        while (n != null) {
            System.out.println(n.interval);
            n = n.next;
        }
        System.out.println("Chord print end");
    }

    public List<Integer> getAllNoteIntervals() {
        List<Integer> notes = new ArrayList<>();
        while (this.note != null) {
            notes.add(this.note.interval);
            this.note = this.note.getNext();//this.note.next;
        }
        return notes;
    }

}
