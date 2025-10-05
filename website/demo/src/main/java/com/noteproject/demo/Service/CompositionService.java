package com.noteproject.demo.Service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.noteproject.demo.Model.Chord;
import com.noteproject.demo.Model.Composition;
import com.noteproject.demo.Model.Measure;
import com.noteproject.demo.Model.Note;
import com.noteproject.demo.Model.Chord.ChordDuration;
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

    final int NUM_STRINGS = 6;
    final int WHOLE_NOTE_DURATION = 4;
    final int REST = -1;

    public int addNewComposition(String title, String composer, Long userId) {
        int compId = cr.insertCompositionIntoDB(title, composer, userId);
        List<Note> notes = new ArrayList<>();
        for (int i = 0; i < NUM_STRINGS; i++) {
            notes.add(new Note(REST, i));
        }
        Chord c = new Chord(notes, ChordDuration.WHOLE);
        List<Chord> chords = new ArrayList<>();
        chords.add(c);
        Measure m = new Measure(chords);
        mr.addMeasureToRepo(m, compId, 0, false);
        return compId;
    }

    public List<Composition> getUserCompositions(Long userId) {
        return cr.getUserCompositions(userId);
    }

    public List<Composition> getAllCompositions() {
        return cr.getAllCompositions();
    }

    public Composition getCompositionById(int id) {
        return cr.getCompositionById(id);
    }

    public boolean isOwner(long userId, int id) {
        return cr.checkCompositionOwnership(userId, id);
    }

    public int deleteComposition(int id) {
        return cr.deleteComposition(id);
    }
    
}