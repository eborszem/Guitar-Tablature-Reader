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

    public Chord findChordByMeasureIdAndChordIndex(int measureId, int chordIdx) {
        return chr.findChordByMeasureIdAndChordIndex(measureId, chordIdx);
    }

    public void deleteChord(int measureId, int chordId, int chordIndex) {
        chr.deleteChord(measureId, chordId, chordIndex);
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

    public void swapChord(int measureId, int chordId, String direction) {
        Chord cur = findChordByMeasureIdAndChordId(measureId, chordId);
        int offset = direction.equals("LEFT") ? -1 : 1;
        Chord swap = findChordByMeasureIdAndChordIndex(measureId, cur.getIndex() + offset);
        if (swap == null) return;
        chr.swapChord(cur, swap);
    }


}
