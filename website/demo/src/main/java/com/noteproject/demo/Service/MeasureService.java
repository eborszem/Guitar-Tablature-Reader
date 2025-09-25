package com.noteproject.demo.Service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.noteproject.demo.Model.Chord;
import com.noteproject.demo.Model.Measure;
import com.noteproject.demo.Model.Note;
import com.noteproject.demo.Model.Chord.ChordDuration;
import com.noteproject.demo.Repository.ChordRepository;
import com.noteproject.demo.Repository.MeasureRepository;

@Service
public class MeasureService {
    @Autowired
    MeasureRepository mr;

    @Autowired
    ChordRepository chr;
    /* Create new measure,
     * then increment all following measures by 1,
     * then add new measure to database with measure index (gotten from measureId) + 1.
     * This method retains the order of measures.
     */
    final int NUM_STRINGS = 6;
    public void addMeasure(int measureId, int compositionId) {
        List<Note> notes = new ArrayList<>();
        for (int i = 0; i < NUM_STRINGS; i++) {
            notes.add(new Note(-1, i));
        }
        int measureIndex = mr.getMeasureIndex(compositionId, measureId);
        mr.incrementMeasureIndices(compositionId, measureIndex); // increment all measures after the new measure to keep order
        List<Chord> chords = new ArrayList<>();
        chords.add(new Chord(notes, ChordDuration.WHOLE)); // a single chord of rests
        mr.addMeasureToRepo(new Measure(chords), compositionId, measureIndex + 1, false); // goes 1 after current measure
    }

    // Same as above, but measure retains the chords from its "parent".
    public void duplicateMeasure(int measureId, int compositionId) {
        List<Chord> chords = chr.findChordsByMeasureId(measureId);
        int measureIndex = mr.getMeasureIndex(compositionId, measureId);
        mr.incrementMeasureIndices(compositionId, measureIndex); // increment all measures after the new measure to keep order
        mr.addMeasureToRepo(new Measure(chords), compositionId, measureIndex + 1, true); // goes 1 after current measure
    }

    public void deleteMeasure(int compositionId, int measureId) {
        mr.deleteMeasure(compositionId, measureId);
    }

}
