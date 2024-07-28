package com.noteproject.demo.Model;
public class Note {
    int duration;  // 1 beat, 2 beats, 4, 8, 16, etc.
    int fretNumber;
    int stringNumber;  // we use fretNumber and stringNumber to get note at 
    int interval;  // relation of note to root (e.g. in the key of C, G would be 5)
    boolean rest;
	String noteName;
    public Note next;
    public Note(int interval, int duration, boolean rest) {
        this.interval = interval;
        this.duration = duration;
        this.rest = rest;
        this.next = null;
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

    public int getInterval() {
        return this.interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
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

    public String getNoteName() {
        return this.noteName;
    }

    public void setNoteName(String noteName) {
        this.noteName = noteName;
    }

    public Note getNext() {
        return this.next;
    }

    public void setNext(Note next) {
        this.next = next;
    }

}
