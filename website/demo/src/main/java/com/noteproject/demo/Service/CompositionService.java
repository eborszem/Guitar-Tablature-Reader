package com.noteproject.demo.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.noteproject.demo.Model.Chord;
import com.noteproject.demo.Model.Measure;
import com.noteproject.demo.Model.Note;
import com.noteproject.demo.Repository.ChordRepository;
import com.noteproject.demo.Repository.CompositionRepository;
import com.noteproject.demo.Repository.MeasureRepository;

@Service
public class CompositionService {
    @Autowired
    CompositionRepository cr;
    @Autowired
    MeasureRepository mr;
    @Autowired
    ChordRepository chr;

    public Measure formatComposition(int compositionId) {
        // 1. group chords by their measure ids (relative position in measure)
        // 2. sort all measures by their measure number (relative position in composition)
        // 3. then add x chords to measure 0, then y chords to measure 1, etc.
        
        // creates HashMap with measure ids as keys, and chords (that go into those measures) as values
        List<Chord> chords = chr.findChordsByCompositionId(compositionId);
        HashMap<Integer, ArrayList<Chord>> measureIdToChords = new HashMap<>();
        for (Chord c : chords) {
            int measureId = c.getMeasureId();
            ArrayList<Chord> arr;
            if (!measureIdToChords.containsKey(measureId)) {
                arr = new ArrayList<>();
            } else {
                arr = measureIdToChords.get(measureId);
            }
            arr.add(c);
            measureIdToChords.put(measureId, arr);
        }
        
        Measure dummy = new Measure();
        Measure measure = dummy;
        //chords.sort((c1, c2) -> Integer.compare(c1.getMeasureId(), c2.getMeasureId()));
        List<Measure> measures = mr.findMeasuresByCompositionId(compositionId);
        measures.sort((c1, c2) -> Integer.compare(c1.getMeasureNumber(), c2.getMeasureNumber()));
        for (Measure m : measures) {
            System.out.println("-----------measure-----------");
            int id = m.getId(); // can use this to get measure's chords
            ArrayList<Chord> chordsFromMeasureId = measureIdToChords.get(id);
            Chord cDummy = new Chord();
            Chord chord = cDummy;
            for (Chord c : chordsFromMeasureId) {
                System.out.print(c.getNote().getFretNumber());
                chord.setNext(c);
                chord = chord.getNext();
            }
            System.out.print(",dur="+chord.getNote().getDuration());
            System.out.println();
            measure.setNext(new Measure(cDummy.getNext()));
            //System.out.println("should print fret num of cur measure chord 1: " + measure.getNext().getChord().getNote().getFretNumber());
            measure = measure.getNext();
            
        }
        //System.out.println("1st measure 1st chord: " + dummy.getNext().getChord().toString());
        //System.out.println("1st measure 2nd chord: " + dummy.getNext().getChord().getNext().toString());
        //System.out.println("2nd measure 1st chord: " + dummy.getNext().getNext().getChord().toString());
        return dummy.getNext();
    }

    public int addNewComposition(String title, String composer) {
        int compId = cr.insertCompositionIntoDB(title, composer);
        // initialize a note spanning all 6 strings (0 - 5) of duration 4 (whole note)
        // TODO
        // currently, a rest is depicted by Note's 1st param (-1) and 4th param (true). this is redundant
        // only keep the -1 but also give it a variable name (like "final int REST_NOTE = -1")
        final int NUM_STRINGS = 6;
        final int WHOLE_NOTE_DURATION = 4;
        Note[] notes = new Note[NUM_STRINGS];
        for (int i = 0; i < NUM_STRINGS; i++) {
            notes[i] = new Note(-1, i, WHOLE_NOTE_DURATION, true);
        }
        for (int i = 0; i < NUM_STRINGS - 1; i++) {
            notes[i].next = notes[i + 1];
        }
        Chord c = new Chord(notes[0]); // linked list of chords begins at first note
        Measure m = new Measure(c);
        mr.addMeasureToRepo(m, compId);
        System.out.println("ENDING ADDING NEW COMP");
        return compId;
    }
    
}