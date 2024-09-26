package com.noteproject.demo.Repositories;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;

import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.noteproject.demo.DemoController;
import com.noteproject.demo.Mapper.ChordRowMapper;
import com.noteproject.demo.Mapper.MeasureRowMapper;

import org.springframework.jdbc.core.RowMapper;
import com.noteproject.demo.Model.Chord;
import com.noteproject.demo.Model.Composition;
import com.noteproject.demo.Model.Measure;
import com.noteproject.demo.Model.Note;

import java.sql.Timestamp;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Date;


@Repository
public class CompositionRepository {
    private final JdbcTemplate jdbcTemplate;

    public CompositionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // returns measureId
    public int addMeasureToRepo(Measure m, int compositionId) {
        String sql = "SELECT MAX(measure_number) FROM Measures WHERE composition_id = ?";
        Integer val = jdbcTemplate.queryForObject(sql, new Object[]{compositionId}, Integer.class);
        int numMeasures;
        if (val != null) {
            numMeasures = val;
        } else {
            numMeasures = 0;
        }

        // Create a KeyHolder to capture the auto-generated key for the measure ID
        KeyHolder keyHolder = new GeneratedKeyHolder();

        // Insert measure and retrieve the auto-generated ID
        String sql2 = "INSERT INTO Measures (composition_id, measure_number) VALUES (?, ?)";
        jdbcTemplate.update(
            connection -> {
                PreparedStatement ps = connection.prepareStatement(sql2, new String[]{"id"});
                ps.setInt(1, compositionId);
                ps.setInt(2, numMeasures + 1);  // New measure number
                return ps;
            },
            keyHolder
        );

        // Get the generated measure ID
        Number measureId = keyHolder.getKey();
        if (measureId == null) {
            throw new RuntimeException("Failed to get id");
        }
        int measureIdInt = measureId.intValue();
        
        String sql3 = "INSERT INTO Chords (measure_id, low_e_string, a_string, d_string, g_string, b_string, high_e_string, duration, chord_number) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Note low_e_string = m.getChord().getNote();
        Note a_string = low_e_string.next;
        Note d_string = a_string.next;
        Note g_string = d_string.next;
        Note b_string = g_string.next;
        Note high_e_string = b_string.next;
        int duration = low_e_string.getDuration();
        jdbcTemplate.update(sql3, measureIdInt, low_e_string.getFretNumber(), a_string.getFretNumber(), d_string.getFretNumber(), g_string.getFretNumber(), b_string.getFretNumber(), high_e_string.getFretNumber(), duration, 0); // chord_number == 0, as a new measure will only have a single rest
        return measureIdInt;
    }

    public List<Chord> findChordsByCompositionId(int compositionId) {
        String sql = "SELECT c.* FROM chords c JOIN measures m ON c.measure_id = m.id WHERE m.composition_id = ?";
        
        PreparedStatementCreator psc = connection -> {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, compositionId);
            return ps;
        };
        
        RowMapper<Chord> rowMapper = new ChordRowMapper();
        
        return jdbcTemplate.query(psc, rowMapper);
    }

    public List<Chord> findChordsByCompositionAndMeasureIds(int compositionId, int measureId) {
        String sql = "SELECT c.* FROM chords c JOIN measures m ON c.measure_id = m.id WHERE m.composition_id = ? AND c.measure_id = ?";
        
        PreparedStatementCreator psc = connection -> {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, compositionId);
            ps.setInt(2, measureId);
            return ps;
        };
        
        RowMapper<Chord> rowMapper = new ChordRowMapper();
        System.out.println();
        return jdbcTemplate.query(psc, rowMapper);
    }

    public List<Measure> findMeasuresByCompositionId(int compositionId) {
        String sql = "SELECT * FROM Measures WHERE composition_id = ?";
        
        PreparedStatementCreator psc = connection -> {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, compositionId);
            return ps;
        };
        
        RowMapper<Measure> rowMapper = new MeasureRowMapper();
        
        return jdbcTemplate.query(psc, rowMapper);
    }

    // no compositions = exception will occur
    // gets a dummy measure which just tells us the composition's time signature (the note that gets the beat and how many beat notes are in a measure)
    // example:
    // 4 4 time: has a quarter note as its beat note and there is a duration of 4 quarter notes per measure
    // 2 4 time: beat note: quarter note, 2 quarter notes per measure
    public Measure getTimeSignature(int compositionId) {
        String sql = "SELECT note_value, num_note_values_per_measure FROM Compositions WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{compositionId}, (rs, rowNum) -> 
            new Measure(rs.getInt("note_value"), rs.getInt("num_note_values_per_measure"))
        );
    }

    public List<Composition> getAllCompositions() {
        String sql = "SELECT id, title FROM Compositions";
        return jdbcTemplate.query(sql, (rs, rowNum) -> 
            new Composition(rs.getInt("id"), rs.getString("title"))
        );
    }

    public Measure formatComposition(int compositionId) {
        List<Chord> chords = findChordsByCompositionId(compositionId);
        List<Measure> measures = findMeasuresByCompositionId(compositionId);
        // sort all chords by their measure id (relative position in measure)
        // sort all measures by their measure number (relative position in composition)
        // then add x chords to measure 0, then y chords to measure 1, etc.
        //chords.sort((c1, c2) -> Integer.compare(c1.getMeasureId(), c2.getMeasureId()));
        measures.sort((c1, c2) -> Integer.compare(c1.getMeasureNumber(), c2.getMeasureNumber()));
        HashMap<Integer, ArrayList<Chord>> measureIdToChords = new HashMap<>();
        // creates HashMap with measure ids as keys, and chords (that go into those measures) as values
        for (Chord c : chords) {
            int measureId = c.getMeasureId();
            if (!measureIdToChords.containsKey(measureId)) {
                ArrayList<Chord> arr = new ArrayList<>();
                arr.add(c);
                measureIdToChords.put(measureId, arr);
            } else {
                ArrayList<Chord> arr = measureIdToChords.get(measureId);
                arr.add(c);
                measureIdToChords.put(measureId, arr);
            }
            
        }
        Measure dummy = new Measure();
        Measure measure = dummy;
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

    public List<Measure> getMeasures(int compositionId) {
        List<Measure> measures = new ArrayList<>();
        Measure dummy = formatComposition(compositionId);
        Measure m = dummy;
        while (m != null) {
            measures.add(m);
            m = m.getNext();
        }
        System.out.println("MEASURES ARRAY="+measures.toString());
        return measures;
    }

    public void updateChord(Chord c, int measureId, int chordNum) {
        System.out.println("TEST UPDATE CHORD DUR");
        String sql = "UPDATE Chords SET low_e_string = ?, a_string = ?, d_string = ?, g_string = ?, b_string = ?, high_e_string = ?, duration = ? WHERE measure_id = ? AND chord_number = ?";
        Note high_e = c.getNote();
        Note b = high_e.getNext();
        Note g = b.getNext();
        Note d = g.getNext();
        Note a = d.getNext();
        Note low_e = a.getNext();
        int duration = high_e.getDuration();
        System.out.println("duration is" + duration);
        System.out.println("==============TEST UPDATE CHORD PRINTING NOTES E to e==============");
        System.out.println("dur=" + duration + ": " + low_e.getFretNumber() + ", " + a.getFretNumber() + ", " + d.getFretNumber() + ", " + g.getFretNumber() + ", " + b.getFretNumber() + ", " + high_e.getFretNumber());
        System.out.println("==============DONE PRINTING NOTES E to e==============");

        jdbcTemplate.update(sql, new PreparedStatementSetter() {
            @Override
            public void setValues(java.sql.PreparedStatement ps) throws SQLException {
                ps.setObject(1, low_e.getFretNumber());
                ps.setObject(2, a.getFretNumber());
                ps.setObject(3, d.getFretNumber());
                ps.setObject(4, g.getFretNumber());
                ps.setObject(5, b.getFretNumber());
                ps.setObject(6, high_e.getFretNumber());
                ps.setObject(7, duration);
                ps.setInt(8, measureId);
                ps.setInt(9, chordNum);
            }
        });
        
    }

    // newly added chord will go immediately after the (former) last chord in measure
    public void addChord(Chord c, int measureId) {
        System.out.println("TEST ADD CHORD");
        String str = "SELECT MAX(chord_number) FROM Chords WHERE measure_id = ?";
        Integer val = jdbcTemplate.queryForObject(str, new Object[]{measureId}, Integer.class);
        int chordNum;
        if (val != null) {
            chordNum = val + 1;
        } else {
            chordNum = 0;
        }
        String sql = "INSERT INTO Chords (measure_id, low_e_string, a_string, d_string, g_string, " +
                     "b_string, high_e_string, duration, chord_number) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Note high_e = c.getNote();
        Note b = high_e.getNext();
        Note g = b.getNext();
        Note d = g.getNext();
        Note a = d.getNext();
        Note low_e = a.getNext();
        int duration = high_e.getDuration();

        System.out.println("==============TEST ADD CHORD PRINTING NOTES E to e==============");
        System.out.println("dur=" + duration + ": " + low_e.getFretNumber() + ", " + a.getFretNumber() + ", " + d.getFretNumber() + ", " + g.getFretNumber() + ", " + b.getFretNumber() + ", " + high_e.getFretNumber());
        System.out.println("==============DONE PRINTING NOTES E to e==============");

        jdbcTemplate.update(sql, new PreparedStatementSetter() {
            @Override
            public void setValues(java.sql.PreparedStatement ps) throws SQLException {
                ps.setObject(1, measureId);
                ps.setObject(2, low_e.getFretNumber());
                ps.setObject(3, a.getFretNumber());
                ps.setObject(4, d.getFretNumber());
                ps.setObject(5, g.getFretNumber());
                ps.setObject(6, b.getFretNumber());
                ps.setObject(7, high_e.getFretNumber());
                ps.setObject(8, duration);
                ps.setInt(9, chordNum);
            }
        });
        
    }

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

    // newMeasure is a list of Chord objects (every chord points to its successor)
    public void editMeasure(Chord newMeasure, int measureId) {
        // debugging
        /*while (newMeasure != null) {
            for (Note n : newMeasure.getAllNotes()) {
                System.out.print(n.getFretNumber() + ".");
            }
            newMeasure = newMeasure.getNext();
            System.out.println("*");
        }*/
        //
        String delete = "DELETE FROM Chords WHERE measure_id = ?";
        jdbcTemplate.update(delete, measureId);
        String sql = "INSERT INTO Chords (measure_id, low_e_string, a_string, d_string, g_string, b_string, high_e_string, duration, chord_number) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        int i = 0;
        while (newMeasure != null) {
            Note low_e_string = newMeasure.getNote();
            Note a_string = low_e_string.next;
            Note d_string = a_string.next;
            Note g_string = d_string.next;
            Note b_string = g_string.next;
            Note high_e_string = b_string.next;
            int duration = low_e_string.getDuration();
            jdbcTemplate.update(sql, measureId, low_e_string.getFretNumber(), a_string.getFretNumber(), d_string.getFretNumber(), g_string.getFretNumber(), b_string.getFretNumber(), high_e_string.getFretNumber(), duration, i);
            newMeasure = newMeasure.getNext();
            i++;
            System.out.println(duration + ": " + low_e_string.getFretNumber() + " " + a_string.getFretNumber() + " " + d_string.getFretNumber() + " " + g_string.getFretNumber() + " " + b_string.getFretNumber() + " " + high_e_string.getFretNumber());
        }
    }

    // TODO: current bug for case #1: new notes will always be added after all preexisting chords, when in reality no chords should move around
    // caused by getting max chord number. need to use chordNum and readd every chord after it to the repo
    public void updateDurations(int newDuration, int oldDuration, Chord updatedChord, int measureId, int chordNum, int compositionId) {
        List<Chord> chords = findChordsByCompositionAndMeasureIds(compositionId, measureId);
        // maintain sorted order
        chords.sort((c1, c2) -> Integer.compare(c1.getChordNumber(), c2.getChordNumber()));

        double newDur = convertDur(newDuration);
        double oldDur = convertDur(oldDuration);
        // w: 4
        // h: 2
        // q: 1
        // 8th: .5 
        // 16th: .25
        int counter = 0;
        int totalBeatsUntilUpdatedChord = 0;
        Chord dummy = new Chord();
        Chord newMeasure = dummy; // a chord object points to multiple chords. essentially, it is a measure linked list
        System.out.println("DURATIONTEST");
        while (counter != chordNum) {
            //System.out.println(counter);
            int durationOfChord = chords.get(counter).getNote().getDuration();
            totalBeatsUntilUpdatedChord += durationOfChord;
            dummy.setNext(chords.get(counter));
            dummy = dummy.getNext();
            counter++;
        }
        System.out.println("AFTER WHILE LOOP");
        Measure timeSig = getTimeSignature(compositionId);
        int maxBeatsPerMeasure = timeSig.getNoteValue() * timeSig.getNumNoteValuesPerMeasure();
        int remainingBeats = maxBeatsPerMeasure - totalBeatsUntilUpdatedChord;
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
        updateChord(updatedChord, measureId, chordNum);
        if (newDur < oldDur) {
            // any chord/rest added here is guaranteed to be equal to the former chord
            // so no need to account for reallocation
            double remainder = oldDur - newDur;
            // 4 - 1 = 3
            // 16 - 1 = 15
            // 1
            // 16 
            while (remainder > 0) {
                if (remainder - 4 >= 0) {
                    remainders.add(4.0);
                    remainder -= 4;
                } else if (remainder - 2 >= 0) {
                    remainders.add(2.0);
                    remainder -= 2;
                } else if (remainder - 1 >= 0) {
                    remainders.add(1.0);
                    remainder -= 1;
                } else if (remainder - .5 >= 0) {
                    remainders.add(.5);
                    remainder -= .5;
                } else if (remainder - .25 >= 0) {
                    remainders.add(.25);
                    remainder -= .25;
                }
            }
            System.out.println("updateDurations reached100");

            //updateChord(updatedChord, measureId, chordNum);
            Chord dummy2 = new Chord();
            Chord rests = dummy2;
            for (double duration : remainders) {
                int r = styleDur(duration);
                System.out.println("remainder = " + r);
                Note high_e = new Note(-1, -1, r, true);
                Note b = new Note(-1, -1, r, true);
                Note g = new Note(-1, -1, r, true);
                Note d = new Note(-1, -1, r, true);
                Note a = new Note(-1, -1, r, true);
                Note low_e = new Note(-1, -1, r, true);
                high_e.setNext(b);
                b.setNext(g);
                g.setNext(d);
                d.setNext(a);
                a.setNext(low_e);
                dummy2.setNext(new Chord(high_e));
                dummy2 = dummy2.getNext();
                //addChord(c, measureId);
            }

            dummy.setNext(updatedChord);
            dummy = dummy.getNext();
            dummy.setNext(rests.getNext());
            dummy = dummy.getNext();

            editMeasure(newMeasure.getNext(), measureId);

            // need to update this chord in chord table, then
            // need to insert new rest chords into chords table
        }
        
    }

    public int addNewComposition() {
        Note wholeRest = new Note(-1, 0, 4, true);
        Note wholeRest2 = new Note(-1, 1, 4, true);
        Note wholeRest3 = new Note(-1, 2, 4, true);
        Note wholeRest4 = new Note(-1, 3, 4, true);
        Note wholeRest5 = new Note(-1, 4, 4, true);
        Note wholeRest6 = new Note(-1, 5, 4, true);
        wholeRest.next = wholeRest2;
        wholeRest2.next = wholeRest3;
        wholeRest3.next = wholeRest4;
        wholeRest4.next = wholeRest5;
        wholeRest5.next = wholeRest6;
        
        Chord c = new Chord(wholeRest);
        Measure m = new Measure(c);
        String sql = "INSERT INTO Compositions (title, composer, time, note_value, num_note_values_per_measure) VALUES (?, ?, ?, ?, ?)";

        /*  TODO: placeholder values   */
        String title = "New song";
        String composer = "Name"; 
        Timestamp timestamp = new Timestamp(new java.util.Date().getTime());

        int noteValue = 4;
        int numNotesPerMeasure = 4;
        /*                       */
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
            connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
                ps.setString(1, title);
                ps.setString(2, composer);
                ps.setTimestamp(3, timestamp);
                ps.setInt(4, noteValue);
                ps.setInt(5, numNotesPerMeasure);
                return ps;
            },
            keyHolder
        );
        Number compId = keyHolder.getKey();
        if (compId == null) {
            throw new RuntimeException("Failed to get id");
        }
        int compIdInt = compId.intValue();
        addMeasureToRepo(m, compIdInt);
        System.out.println("ENDING ADDING NEW COMP");
        return compIdInt;
    }
}