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

    // eighth and sixteenth notes are easier to store as integers, but we must convert them back for math operations
    public double convertDur(int dur) {
        if (dur == 8) return .5;
        else if (dur == 16) return .25;
        return dur;
    }

    // turn eighth and sixteenth notes back into their storage values
    public int styleDur(double dur) {
        // Note: switch statements only work for discrete values
        if (dur == .5) return 8;
        else if (dur == .25) return 16;
        return (int) dur;
    }

    public void updateDurations(int newDuration, int oldDuration, Chord updatedChord, int measureId, int chordNum, int compositionId) {
        List<Chord> chords = chr.findChordsByCompositionIdAndMeasureId(compositionId, measureId);
        double newDur = convertDur(newDuration), oldDur = convertDur(oldDuration);
        // w: 4
        // h: 2
        // q: 1
        // 8th: .5 
        // 16th: .25
        int counter = 0;
        List<Chord> dummy = new ArrayList<>();
        System.out.println("DURATIONTEST");
        while (counter != chordNum) {
            // System.out.println("chord " + counter + ". high e string is " + chords.get(counter).getNotes().get(0).getFretNumber());
            dummy.add(counter, (chords.get(counter)));
            counter++;
        }

        /*
         * Cases:
         * newDuration < dur: chord will play for newDuration time, remaining dur - newDuration time becomes rests
         * newDuration > dur, newDuration does NOT exceed measure: user is warned chord will overwrite future chords/rests.
         * newDuration > dur, newDuration DOES exceed measure: user is warned chord will overwrite future chords/rests FOR EACH MEASURE.
         *                    popup appears every measure that is being affected
         */

        // must calculate rests to be put after shorter chord
        ArrayList<Double> remainders = new ArrayList<>();
        System.out.println("updateDurations reached50");
        System.out.println("new " + newDuration + " => " + newDur);
        System.out.println("old " + oldDuration + " => " + oldDur);
        // the duration 4 is shorter than 1, as 4 represents a quarter note while 1 repreents a whole note
        // however, because here we are going by integer value, the boolean logic flips
        // for example: if the newDuration of updatedChord is shorter (aka larger integer), then we split the chord into the shorter chord and rests
        // if the newDuration is longer (aka shorter integer), then we need to overwrite chords after updatedChord
        chr.updateChord(updatedChord, measureId, chordNum);
        if (newDur < oldDur) {
            System.out.println("CASE #1");
            // any chord/rest added here is guaranteed to be equal to the former chord
            // so no need to account for reallocation
            double remainder = oldDur - newDur;
            // 4 - 1 = 3
            // 16 - 1 = 15
            // 1
            // 16
            while (remainder > 0) {
                if (remainder - 4 >= 0) { // whole note
                    remainders.add(4.0);
                    remainder -= 4;
                } else if (remainder - 2 >= 0) { // half note
                    remainders.add(2.0);
                    remainder -= 2;
                } else if (remainder - 1 >= 0) { // quarter note
                    remainders.add(1.0);
                    remainder -= 1;
                } else if (remainder - .5 >= 0) { // eighth note
                    remainders.add(.5);
                    remainder -= .5;
                } else if (remainder - .25 >= 0) { // sixteenth note
                    remainders.add(.25);
                    remainder -= .25;
                }
            }
            dummy.add(updatedChord);
            // now add rests too
            List<Chord> rests = new ArrayList<>();
            for (double duration : remainders) {
                int r = styleDur(duration);
                System.out.println("remainder = " + r);
                List<Note> notes = new ArrayList<>();
                notes.add(new Note(-1, -1, r));
                notes.add(new Note(-1, -1, r));
                notes.add(new Note(-1, -1, r));
                notes.add(new Note(-1, -1, r));
                notes.add(new Note(-1, -1, r));
                notes.add(new Note(-1, -1, r));
                rests.add(new Chord(notes));
            }
            mr.editMeasure(dummy, measureId, chords, chordNum + 1); // chordNum + 1 is the index where the remaining chords start, as the count'th measure is the chord who is being changed
        } else { // if newDur > oldDur

        }
        System.out.println("RETURNING CHANGE DURATION");
        
    }

    public void deleteChord(int measureId, int chordLocation) {
        chr.deleteChord(measureId, chordLocation);
    }

    public void updateChord(Chord chord, int measureId, int chordNum) {
        chr.updateChord(chord, measureId, chordNum);
    }


}
