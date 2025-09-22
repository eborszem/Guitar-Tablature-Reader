package com.noteproject.demo.Model;
public class Note {
    private int duration;  // 1 beat, 2 beats, 4, 8, 16, etc.
    private int fretNumber;
    private int stringNumber;  // we use fretNumber and stringNumber to get note location on fretboard

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
}
