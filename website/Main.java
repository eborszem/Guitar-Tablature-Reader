package project;
public class Main {
    public static void main(String[] args) {
        Note note = new Note(0, 0);
        Chord chord = new Chord(note);
        Measure m = new Measure(null);
        Measure mm = m;
        // creates 4 measures for the composition
        for (int i = 0; i < 4; i++) {
            Chord cc = chord;
            // creates 4 chords with 1 beat each
            for (int j = 1; j < 16; j += 4) {
                int k = j;
                // the three while loops keep us within valid musical intervals 1 through 7 when creating a new note
                while (k > 7) {
                    k -= 7;
                }
                Note root = new Note(i+k, 1);
                while ((k + 2) > 7) {
                    k -= 7;
                }
                Note third = new Note(i+k + 2, 1);
                while ((k + 4) > 7) {
                    k -= 7;
                }
                Note fifth = new Note(i+k + 4, 1);

                //System.out.println(root.interval + "," + third.interval + "," + fifth.interval);
                Note dummy = root;
                root.next = third;
                third.next = fifth;
                //should be equal to above
                //System.out.println(dummy.interval + "," + dummy.next.interval + "," + dummy.next.next.interval);
                Chord c = new Chord(dummy);
                chord.next = c;
                chord = chord.next;
                
            }
            // add new list of chords (which together comprise the measure) to linked list of measures
            Measure measure = new Measure(cc.next);
            m.next = measure;
            m = m.next;
            chord = new Chord(note);
        }


        //System.out.println(mm.next.chord.note.interval);
        //Measure m = new Measure(tmp);
        //m.printMeasure(m);
        
        Composition c = new Composition("C", mm);
        c.printCompositon(c);



        /*System.out.println("hello");
        Note note = new Note(1, 2);  // c
        Note note2 = new Note(3, 2);  // e
        Note note3 = new Note(5, 2);  // g
        Note c = note;
        note.next = note2;
        note = note.next;
        note.next = note3;
        note = note.next;

        Chord cMaj = new Chord(c);
        Chord tmp = cMaj;

        Note note4 = new Note(5, 1);  // g
        Note note5 = new Note(7, 1);  // b
        Note note6 = new Note(2, 1);  // d
        Note g = note4;
        note4.next = note5;
        note4 = note4.next;
        note4.next = note6;
        note4 = note4.next;

        Chord gMaj = new Chord(g);
        cMaj.next = gMaj;

        Note note7 = new Note(4, 1);  // f
        Note note8 = new Note(6, 1);  // a
        Note note9 = new Note(1, 1);  // c
        
        Note f = note7;
        note7.next = note8;
        note7 = note7.next;
        note7.next = note9;
        note7 = note7.next;

        Chord fMaj = new Chord(f);
        gMaj.next = fMaj;

        Measure m = new Measure(tmp);
        //m.printMeasure(m);

        Composition composition = new Composition("C", m);
        composition.printCompositon(composition);*/

    }
}
