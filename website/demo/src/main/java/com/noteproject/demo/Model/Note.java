package com.noteproject.demo.Model;
public class Note {
    private int fretNumber;
    private int stringNumber;  // we use fretNumber and stringNumber to get note location on fretboard

    public Note(int fretNumber, int stringNumber) {
        this.fretNumber = fretNumber;
        this.stringNumber = stringNumber;
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
