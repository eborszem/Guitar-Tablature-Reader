package com.noteproject.demo.Model;

import java.util.ArrayList;
import java.util.List;

public class Composition {
    String key;
    int beat;  // the beat note: 4-whole note, 2-half note, 1-quarter note, .5-eighth note
    int beatsPerMeasure;
    Measure measure;
    public Composition next;
    // note: each measure's number of beats must equal beat * beatsPerMeasure
    public Composition(String key, int beat, int beatsPerMeasure, Measure measure) {
        this.measure = measure;
        this.next = null;
    }

    // note to self: this function moves the heads of the lists, resulting in them becoming null after this is called
    // function to return composition as String
    public String printCompositon(Composition c) {
        String s = "";
        // print measures, with each chord seperated by a bar 
        while (c.measure != null) {
            while (c.measure.chord != null) {
                int duration = c.measure.chord.note.duration;
                while (c.measure.chord.note != null) {
                    System.out.print(c.measure.chord.note.interval);
                    s = s.concat(String.valueOf(c.measure.chord.note.interval));
                    c.measure.chord.note = c.measure.chord.note.next;
                }
                if (c.measure.chord.next == null && c.measure.next == null) {
                    System.out.print(":" + duration);
                    s = s.concat(":" + String.valueOf(duration));
                } else if (c.measure.chord.next == null) {
                    System.out.print(":" + duration + " | ");
                    s = s.concat(":" + String.valueOf(duration) + " | ");
                } else {
                    System.out.print(":" + duration + ", ");
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

    public List<Measure> getAllMeasures() {
        List<Measure> measures = new ArrayList<>();
        Measure m = this.measure;
        System.out.println("measures start");
        System.out.println(m);
        System.out.println(m.next);
        while (m != null) {
            measures.add(m);
            m = m.next;
        }
        System.out.println("measures end");
        return measures;
    }

}
