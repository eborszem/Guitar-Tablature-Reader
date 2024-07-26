package project;
public class Chord {
    Note note;  // notes in the chord
    Chord next;
    Chord prev;
    //Chord prev;
    public Chord(Note note) {
        this.note = note;
        this.next = null;
        this.prev = null;
    }
    /*Chord(Note[] notes) {
        this.notes = notes;
        this.next = null;
        this.prev = null;
    }*/

}
