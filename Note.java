package project;
public class Note {
    int duration;  // 1 beat, 2 beats, 4, 8, 16, etc.
    int fretNumber;
    int stringNumber;  // we use fretNumber and stringNumber to get note at 
    int interval;  // relation of note to root (e.g. in the key of C, G would be 5)
	String noteName;
    Note next;
    public Note(int interval, int duration) {
        this.interval = interval;
        this.duration = duration;
        this.next = null;
    }
}
