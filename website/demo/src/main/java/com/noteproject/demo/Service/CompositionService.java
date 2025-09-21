package com.noteproject.demo.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.noteproject.demo.Model.Chord;
import com.noteproject.demo.Model.Composition;
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

    public Composition formatComposition(int compositionId) {
        // 1. group chords by their measure ids (relative position in measure)
        // 2. sort all measures by their measure number (relative position in composition)
        // 3. then add x chords to measure 0, then y chords to measure 1, etc.
        
        // creates HashMap with measure ids as keys, and chords (that go into those measures) as values
        List<Chord> chords = chr.findChordsByCompositionId(compositionId);
        HashMap<Integer, List<Chord>> measureIdToChords = new HashMap<>();
        for (Chord c : chords) {
            int measureId = c.getMeasureId();
            List<Chord> arr;
            if (!measureIdToChords.containsKey(measureId)) {
                arr = new ArrayList<>();
            } else {
                arr = measureIdToChords.get(measureId);
            }
            arr.add(c);
            measureIdToChords.put(measureId, arr);
        }
        //chords.sort((c1, c2) -> Integer.compare(c1.getMeasureId(), c2.getMeasureId()));
        List<Measure> measures = mr.findMeasuresByCompositionId(compositionId);
        measures.sort((c1, c2) -> Integer.compare(c1.getMeasureNumber(), c2.getMeasureNumber()));
        for (Measure m : measures) {
            m.setChords(measureIdToChords.getOrDefault(m.getId(), new ArrayList<>()));
        }
        //System.out.println("1st measure 1st chord: " + dummy.getNext().getChord().toString());
        //System.out.println("1st measure 2nd chord: " + dummy.getNext().getChord().getNext().toString());
        //System.out.println("2nd measure 1st chord: " + dummy.getNext().getNext().getChord().toString());
        Composition comp = cr.getCompositionById(compositionId);
        return new Composition(compositionId, comp.getTitle(), comp.getComposer(), measures, comp.getTimestamp(), comp.getUserId());
    }

    public int addNewComposition(String title, String composer, Long userId) {
        int compId = cr.insertCompositionIntoDB(title, composer, userId);
        // initialize a note spanning all 6 strings (0 - 5) of duration 4 (whole note)
        // TODO
        // currently, a rest is depicted by Note's 1st param (-1) and 4th param (true). this is redundant
        // only keep the -1 but also give it a variable name (like "final int REST_NOTE = -1")
        final int NUM_STRINGS = 6;
        final int WHOLE_NOTE_DURATION = 4;
        List<Note> notes = new ArrayList<>();
        for (int i = 0; i < NUM_STRINGS; i++) {
            notes.add(new Note(-1, i, WHOLE_NOTE_DURATION, true));
        }
        Chord c = new Chord(notes); // linked list of chords begins at first note
        List<Chord> chords = new ArrayList<>();
        chords.add(c);
        Measure m = new Measure(chords);
        mr.addMeasureToRepo(m, compId);
        System.out.println("ENDING ADDING NEW COMP");
        return compId;
    }
    
}