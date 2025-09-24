package com.noteproject.demo.Service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.noteproject.demo.Model.Chord;
import com.noteproject.demo.Model.Note;
import com.noteproject.demo.Repository.ChordRepository;
import com.noteproject.demo.Repository.MeasureRepository;

@Service
public class ChordService {
    @Autowired
    MeasureRepository mr;
    @Autowired
    ChordRepository chr;

    public Chord findChordByMeasureIdAndChordId(int measureId, int chordId) {
        return chr.findChordByMeasureIdAndChordId(measureId, chordId);
    }

    public void deleteChord(int measureId, int chordLocation) {
        chr.deleteChord(measureId, chordLocation);
    }

    public void updateChord(Chord updatedChord, int measureId, int chordId) {
        chr.updateChord(updatedChord, measureId, chordId);
    }


}
