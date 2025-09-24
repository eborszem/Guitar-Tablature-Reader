package com.noteproject.demo.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.noteproject.demo.Model.Chord;
import com.noteproject.demo.Repository.ChordRepository;

@Service
public class ChordService {
    @Autowired
    ChordRepository chr;

    public Chord findChordByMeasureIdAndChordId(int measureId, int chordId) {
        return chr.findChordByMeasureIdAndChordId(measureId, chordId);
    }

    public void deleteChord(int measureId, int chordId, int chordNumber) {
        chr.deleteChord(measureId, chordId, chordNumber);
    }

    public void updateChord(Chord updatedChord, int measureId, int chordId) {
        chr.updateChord(updatedChord, measureId, chordId);
    }

	public Chord addChord(int measureId, int chordId) {
        Chord prev = findChordByMeasureIdAndChordId(measureId, chordId);
		return chr.addChord(measureId, chordId, prev);
	}

    public Chord duplicateChord(int measureId, int chordId) {
        Chord original = findChordByMeasureIdAndChordId(measureId, chordId);
        return chr.duplicateChord(measureId, chordId, original);
    }


}
