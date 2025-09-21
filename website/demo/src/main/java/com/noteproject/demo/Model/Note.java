package com.noteproject.demo.Model;
public class Note {
    private int duration;  // 1 beat, 2 beats, 4, 8, 16, etc.
    private int fretNumber;
    private int stringNumber;  // we use fretNumber and stringNumber to get note location on fretboard
    private boolean rest;

    public Note(int fretNumber, int stringNumber, int duration, boolean rest) {
        this.stringNumber = stringNumber;
        this.fretNumber = fretNumber;
        this.duration = duration;
        this.rest = rest;
    }

    public Note(int fretNumber, int stringNumber, int duration) {
        this.stringNumber = stringNumber;
        this.fretNumber = fretNumber;
        this.duration = duration;
    }

    public int getDuration() {
        return this.duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getFretNumber() {
        return this.fretNumber;
    }

    public void setFretNumber(int fretNumber) {
        this.fretNumber = fretNumber;
    }

    public int getStringNumber() {
        return this.stringNumber;
    }

    public void setStringNumber(int stringNumber) {
        this.stringNumber = stringNumber;
    }

    public boolean isRest() {
        return this.rest;
    }

    public boolean getRest() {
        return this.rest;
    }

    public void setRest(boolean rest) {
        this.rest = rest;
    }
}
