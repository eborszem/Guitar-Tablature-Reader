package com.noteproject.demo.Model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Composition {
    String key;
    int beat;  // the beat note: 4-whole note, 2-half note, 1-quarter note, .5-eighth note
    int beatsPerMeasure;
    Measure measure;
    String title;
    int id;
    Timestamp timestamp;
    String composer;
    public Composition next;

    // note: each measure's number of beats must equal beat * beatsPerMeasure
    public Composition(String key, int beat, int beatsPerMeasure, Measure measure) {
        this.measure = measure;
        this.next = null;
    }
    public Composition(String key, int beat, int beatsPerMeasure, Measure measure, String title) {
        this.measure = measure;
        this.title = title;
        
        this.next = null;
    }

    public Composition(int id, String title) {
        this.id = id;
        this.title = title;
        this.next = null;
    }

    public Composition(String title, String composer, Timestamp timestamp) {
        this.title = title;
        this.composer = composer;
        this.timestamp = timestamp;
    }

    public Composition() {
        
    }

    // note to self: this function moves the heads of the lists, resulting in them becoming null after this is called
    // function to return composition as String
    public String printComposition(Composition composition) {
        Composition c = composition;  // dummy variable to avoid composition head becoming null
        String s = "";
        // print measures, with each chord separated by a bar
        while (c.measure != null) {
            while (c.measure.chord != null) {
                if (c.measure.chord.note == null)
                    break;
                int duration = c.measure.chord.note.duration;
                while (c.measure.chord.note != null) {
                    System.out.print(c.measure.chord.note.fretNumber + ",");
                    s = s.concat(String.valueOf("["+c.measure.chord.note.stringNumber + ", " + c.measure.chord.note.fretNumber + "]"));
                    c.measure.chord.note = c.measure.chord.note.next;
                }
                if (c.measure.chord.next == null && c.measure.next == null) {
                    System.out.print(":" + duration);
                    s = s.concat(":" + String.valueOf(duration));
                } else if (c.measure.chord.next == null) {
                    System.out.print(":" + duration + " | ");
                    s = s.concat(":" + String.valueOf(duration) + " | ");
                } else {
                    System.out.print(":" + duration + " ... ");
                    s = s.concat(":" + String.valueOf(duration) + ", ");
                }
                c.measure.chord = c.measure.chord.next;
            }
            c.measure = c.measure.next;
        }
        System.out.println();
        return s;
    }


    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getBeat() {
        return this.beat;
    }

    public void setBeat(int beat) {
        this.beat = beat;
    }

    public int getBeatsPerMeasure() {
        return this.beatsPerMeasure;
    }

    public void setBeatsPerMeasure(int beatsPerMeasure) {
        this.beatsPerMeasure = beatsPerMeasure;
    }

    public Measure getMeasure() {
        return this.measure;
    }

    public void setMeasure(Measure measure) {
        this.measure = measure;
    }

    public Composition getNext() {
        return this.next;
    }

    public void setNext(Composition next) {
        this.next = next;
    }

    public String getTitle() {
        return this.title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }

    public String getComposer() {
        return this.composer;
    }
    
    public Timestamp getTimestamp() {
        return this.timestamp;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Measure> getAllMeasures() {
        List<Measure> measures = new ArrayList<>();
        Measure m = this.measure;
        while (m != null) {
            measures.add(m);
            m = m.next;
        }
        return measures;
    }

    /* Function to read String compositionString (gotten from file) and format it into a Composition object

     * File is formatted like so:
     * 
     * 6/1,6/1,7/1,8/1,8/1,6/1
     * 2/1,12/1,3/1,4/1,4/1,2/1
     * 7/1,7/1,8/1,9/1,9/1,7/1
     * 9/1,9/1,10/1,11/1,11/1,9/1
     * (empty line)
     * 5/2,5/2,6/2,7/2,7/2,5/2
     * 8/2,8/2,9/2,10/2,10/2,8/2
     * (empty line)
     * 0/4,0/4,1/4,2/4,2/4,0/4
     * (empty line)
     * 
     * The first number is which fret the user selects. 1 = 1st fret, 2 = 2nd fret, etc.
     * The "/" separates the fret number and duration.
     * The second number is the duration.
     * .25 = 16th note, .5 = 8th note, 1 = quarter note, 2 = half note, 4 = whole note
     * The "," separates notes
     * New empty lines separate measures
     * 
     */
    public Composition readComposition(String compositionString) {
        String[] compositionArr = compositionString.split("\n");
        Note tempNote = new Note(1, 1, 1, true);
        Chord chord = new Chord(tempNote);
        Chord cDummy = chord;  // chordDummy is used to traverse note list without losing head of chord

        Measure measure = new Measure(chord);
        Measure mDummy = measure;  // chordDummy is used to traverse note list without losing head of measure

        // iterate one extra time so if statement occurs for last measure
        for (int i = 0; i <= compositionArr.length; i++) {
            // if line is empty or is the last line, then we know a measure will be created
            if (i == compositionArr.length || compositionArr[i].isEmpty()) {
                System.out.println("line" + i + " is empty");
                mDummy.next = new Measure(chord.next);
                mDummy = mDummy.next;
                chord = new Chord(tempNote);
                cDummy = chord;
                continue;
            }
            String[] durationAndNotesInChord = compositionArr[i].split(":");
            int duration = Integer.parseInt(durationAndNotesInChord[0]);
            String notesInChord = durationAndNotesInChord[1];
            String[] notesOnFrets = notesInChord.split(",");
            int fretNum = Integer.valueOf(notesOnFrets[0]);
            // String 6 = low E, String 1 = high E
            Note note = new Note(fretNum, 1, duration, false);  // get head of note list
            Note nDummy = note;  // noteDummy is used to traverse note list without losing head of note
            System.out.println("noteints="+compositionArr[i]);
            System.out.println("inter"+i);
            int stringNum = 1;  // Already added high E string (1). Start on B string (2), go to low E string (6)
            for (String n : notesOnFrets) {
                fretNum = Integer.valueOf(n);
                nDummy.next = new Note(fretNum, stringNum, duration, false);
                stringNum++;
                nDummy = nDummy.next;
            }
            cDummy.next = new Chord(note.next); // note.next avoids first empty note in measure
            cDummy = cDummy.next;
            // blank line found, implies measure ends

        }
        Composition composition = new Composition(compositionString, 1, 4, measure.next); // measure.next avoids duplicating measure 1
        return composition;
    }
    
    /*

    public Composition generateComposition() {
        Random rand = new Random();
        int r = rand.nextInt(10);
        Note measure1chord1note1 = new Note(r, 1, false);
        Note measure1chord1note2 = new Note(r, 1, false);
        Note measure1chord1note3 = new Note(r + 1, 1, false);
        Note measure1chord1note4 = new Note(r + 2, 1, false);
        Note measure1chord1note5 = new Note(r + 2, 1, false);
        Note measure1chord1note6 = new Note(r, 1, false);
        measure1chord1note1.next = measure1chord1note2;
        measure1chord1note2.next = measure1chord1note3;
        measure1chord1note3.next = measure1chord1note4;
        measure1chord1note4.next = measure1chord1note5;
        measure1chord1note5.next = measure1chord1note6;
        Chord measure1chord1 = new Chord(measure1chord1note1);
        
        r = rand.nextInt(10);
        // measure 1 chord 2
        Note measure1chord2note1 = new Note(r, 1, false);
        Note measure1chord2note2 = new Note(r, 1, false);
        Note measure1chord2note3 = new Note(r + 1, 1, false);
        Note measure1chord2note4 = new Note(r + 2, 1, false);
        Note measure1chord2note5 = new Note(r + 2, 1, false);
        Note measure1chord2note6 = new Note(r, 1, false);
        measure1chord2note1.next = measure1chord2note2;
        measure1chord2note2.next = measure1chord2note3;
        measure1chord2note3.next = measure1chord2note4;
        measure1chord2note4.next = measure1chord2note5;
        measure1chord2note5.next = measure1chord2note6;
        Chord measure1chord2 = new Chord(measure1chord2note1);


        r = rand.nextInt(10);
        // measure 1 chord 3
        Note measure1chord3note1 = new Note(r, 1, false);
        Note measure1chord3note2 = new Note(r, 1, false);
        Note measure1chord3note3 = new Note(r + 1, 1, false);
        Note measure1chord3note4 = new Note(r + 2, 1, false);
        Note measure1chord3note5 = new Note(r + 2, 1, false);
        Note measure1chord3note6 = new Note(r, 1, false);
        measure1chord3note1.next = measure1chord3note2;
        measure1chord3note2.next = measure1chord3note3;
        measure1chord3note3.next = measure1chord3note4;
        measure1chord3note4.next = measure1chord3note5;
        measure1chord3note5.next = measure1chord3note6;
        Chord measure1chord3 = new Chord(measure1chord3note1);


        r = rand.nextInt(10);
        // measure 1 chord 4
        Note measure1chord4note1 = new Note(r, 1, false);
        Note measure1chord4note2 = new Note(r, 1, false);
        Note measure1chord4note3 = new Note(r + 1, 1, false);
        Note measure1chord4note4 = new Note(r + 2, 1, false);
        Note measure1chord4note5 = new Note(r + 2, 1, false);
        Note measure1chord4note6 = new Note(r, 1, false);
        measure1chord4note1.next = measure1chord4note2;
        measure1chord4note2.next = measure1chord4note3;
        measure1chord4note3.next = measure1chord4note4;
        measure1chord4note4.next = measure1chord4note5;
        measure1chord4note5.next = measure1chord4note6;
        Chord measure1chord4 = new Chord(measure1chord4note1);
        
        
        r = rand.nextInt(10);
        // measure 2 chord 1
        Note measure2chord1note1 = new Note(r, 2, false);
        Note measure2chord1note2 = new Note(r, 2, false);
        Note measure2chord1note3 = new Note(r + 1, 2, false);
        Note measure2chord1note4 = new Note(r + 2, 2, false);
        Note measure2chord1note5 = new Note(r + 2, 2, false);
        Note measure2chord1note6 = new Note(r, 2, false);
        measure2chord1note1.next = measure2chord1note2;
        measure2chord1note2.next = measure2chord1note3;
        measure2chord1note3.next = measure2chord1note4;
        measure2chord1note4.next = measure2chord1note5;
        measure2chord1note5.next = measure2chord1note6;
        Chord measure2chord1 = new Chord(measure2chord1note1);
       
        
        r = rand.nextInt(10);
        // measure 2 chord 2
        Note measure2chord2note1 = new Note(r, 2, false);
        Note measure2chord2note2 = new Note(r, 2, false);
        Note measure2chord2note3 = new Note(r + 1, 2, false);
        Note measure2chord2note4 = new Note(r + 2, 2, false);
        Note measure2chord2note5 = new Note(r + 2, 2, false);
        Note measure2chord2note6 = new Note(r, 2, false);
        measure2chord2note1.next = measure2chord2note2;
        measure2chord2note2.next = measure2chord2note3;
        measure2chord2note3.next = measure2chord2note4;
        measure2chord2note4.next = measure2chord2note5;
        measure2chord2note5.next = measure2chord2note6;
        Chord measure2chord2 = new Chord(measure2chord2note1);
        

        r = rand.nextInt(10);
        // measure 3 chord 1
        Note measure3chord1note1 = new Note(r, 2, false);
        Note measure3chord1note2 = new Note(r, 2, false);
        Note measure3chord1note3 = new Note(r + 1, 2, false);
        Note measure3chord1note4 = new Note(r + 2, 2, false);
        Note measure3chord1note5 = new Note(r + 2, 2, false);
        Note measure3chord1note6 = new Note(r, 2, false);
        measure3chord1note1.next = measure3chord1note2;
        measure3chord1note2.next = measure3chord1note3;
        measure3chord1note3.next = measure3chord1note4;
        measure3chord1note4.next = measure3chord1note5;
        measure3chord1note5.next = measure3chord1note6;
        Chord measure3chord1 = new Chord(measure3chord1note1);

        // Now linking the chords into measures.
        // The first chord of a measure points to all other chords in that measure
        measure1chord1.next = measure1chord2;
        measure1chord2.next = measure1chord3;
        measure1chord3.next = measure1chord4;
        
        Measure measure1 = new Measure(measure1chord1);
        measure2chord1.next = measure2chord2;
        Measure measure2 = new Measure(measure2chord1);
        Measure measure3 = new Measure(measure3chord1);


        
        measure1.next = measure2;
        measure2.next = measure3;
        
        //System.out.println(m + "...." + m.getAllChords() + "....");

        Composition composition = new Composition("C", 4, 4, measure1);

        Composition composition2 = composition;

        // Prints out composition for testing
        System.out.println("ENTERING");
        for (Measure mm : composition2.getAllMeasures()) {
            for (Chord cc : mm.getAllChords()) {
                for (Note nn : cc.getAllNotes()) {
                    System.out.print(nn.getInterval());
                }
                System.out.println();
            }
            System.out.println("------------");
        }
        //model.addAttribute("measure", measure1);
        System.out.println("end of getmapping");
        return composition;
    }
    
    */
}
