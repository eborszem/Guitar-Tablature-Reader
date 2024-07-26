package project;
public class Composition {
    String key;
    int beat;  // the beat note: 4-whole note, 2-half note, 1-quarter note, .5-eighth note
    int beatsPerMeasure;
    Measure measure;
    Composition next;
    // note: each measure's number of beats must equal beat * beatsPerMeasure
    public Composition(String key, int beat, int beatsPerMeasure, Measure measure) {
        this.measure = measure;
        this.next = null;
    }

    // note to self: this function moves the heads of the lists, resulting in them becoming null after this is called
    public void printCompositon(Composition c) {
        // print measures, with each chord seperated by a bar 
        while (c.measure != null) {
            while (c.measure.chord != null) {
                int duration = c.measure.chord.note.duration;
                while (c.measure.chord.note != null) {
                    System.out.print(c.measure.chord.note.interval);
                    c.measure.chord.note = c.measure.chord.note.next;
                }
                if (c.measure.chord.next == null && c.measure.next == null)
                System.out.print(":" + duration);
                else if (c.measure.chord.next == null)
                    System.out.print(":" + duration + " | ");
                else
                    System.out.print(":" + duration + ", ");
                c.measure.chord = c.measure.chord.next;
            }
            c.measure = c.measure.next;
        }
        System.out.println();
        
    }

}
