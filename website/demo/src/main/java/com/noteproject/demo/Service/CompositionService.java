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
        // 2. then add x chords to measure 0, then y chords to measure 1, etc.
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
        List<Measure> measures = mr.findMeasuresByCompositionId(compositionId);
        for (Measure m : measures) {
            m.setChords(measureIdToChords.getOrDefault(m.getId(), new ArrayList<>()));
        }
        Composition comp = cr.getCompositionById(compositionId);
        return new Composition(compositionId, comp.getTitle(), comp.getComposer(), measures, comp.getTimestamp(), comp.getUserId());
    }

    public int addNewComposition(String title, String composer, Long userId) {
        int compId = cr.insertCompositionIntoDB(title, composer, userId);
        final int NUM_STRINGS = 6;
        final int WHOLE_NOTE_DURATION = 4;
        List<Note> notes = new ArrayList<>();
        for (int i = 0; i < NUM_STRINGS; i++) {
            notes.add(new Note(-1, i, WHOLE_NOTE_DURATION));
        }
        Chord c = new Chord(notes);
        List<Chord> chords = new ArrayList<>();
        chords.add(c);
        Measure m = new Measure(chords);
        mr.addMeasureToRepo(m, compId, 0, false);
        return compId;
    }
    
}