package project;
public class Measure {
    Chord chord;  // chords in the measure
    Measure next;
	//Measure prev;
	int noteBeat;  // which note gets the beat. Quarter, half, etc.
	int numNoteBeatsPerMeasure;  // how many note beats are present in the measure
	int numBeats = noteBeat * numNoteBeatsPerMeasure;
	// time signature is numBeats over noteBeat
	public Measure(Chord chord) {
		this.chord = chord;
        this.next = null;
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
        /* 
        System.out.println("1="+m.chord.note.interval);
        System.out.println("3="+m.chord.note.next.interval);
        System.out.println("5="+m.chord.note.next.next.interval);
        System.out.println("5="+m.chord.next.note.interval);
        System.out.println("7="+m.chord.next.note.next.interval);
        System.out.println("2="+m.chord.next.note.next.next.interval);
        System.out.println("4="+m.chord.next.next.note.interval);
        System.out.println("6="+m.chord.next.next.note.next.interval);
        System.out.println("1="+m.chord.next.next.note.next.next.interval);
        */
    }

}
