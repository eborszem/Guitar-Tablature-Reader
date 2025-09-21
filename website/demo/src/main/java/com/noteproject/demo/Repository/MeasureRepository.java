package com.noteproject.demo.Repository;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import com.noteproject.demo.Model.Note;
import com.noteproject.demo.Mapper.MeasureRowMapper;
import com.noteproject.demo.Model.Chord;
import com.noteproject.demo.Model.Composition;
import com.noteproject.demo.Model.Measure;

@Repository
public class MeasureRepository {
    private final JdbcTemplate jdbcTemplate;
    public MeasureRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Autowired
    ChordRepository chr;

    @Autowired
    CompositionRepository cr;

    // public int addMeasureToRepo(Measure m, int compositionId) {
    //     String sql = "SELECT MAX(measure_number) FROM Measures WHERE composition_id = ?";
    //     Integer val = jdbcTemplate.queryForObject(sql, new Object[]{compositionId}, Integer.class);
    //     final int numMeasures = (val != null) ? val : 0;
    //     // Create a KeyHolder to capture the auto-generated key for the measure ID
    //     KeyHolder keyHolder = new GeneratedKeyHolder();
    //     // Insert measure and retrieve the auto-generated ID
    //     String sql2 = "INSERT INTO Measures (composition_id, measure_number) VALUES (?, ?)";
    //     jdbcTemplate.update(
    //         connection -> {
    //             PreparedStatement ps = connection.prepareStatement(sql2, new String[]{"id"});
    //             ps.setInt(1, compositionId);
    //             ps.setInt(2, numMeasures + 1);  // New measure number
    //             return ps;
    //         },
    //         keyHolder
    //     );

    //     // Get the generated measure ID
    //     Number measureId = keyHolder.getKey();
    //     if (measureId == null) {
    //         throw new RuntimeException("Failed to get id");
    //     }
    //     int measureIdInt = measureId.intValue();
        
    //     String sql3 = "INSERT INTO Chords (measure_id, low_e_string, a_string, d_string, g_string, b_string, high_e_string, duration, chord_number) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    //     List<Chord> chords = m.getChords();
    //     Chord chord = chords.get(0);
    //     Note high_e_string = chord.getNotes().get(0);
    //     Note b_string = chord.getNotes().get(1);
    //     Note g_string = chord.getNotes().get(2);
    //     Note d_string = chord.getNotes().get(3);
    //     Note a_string = chord.getNotes().get(4);
    //     Note low_e_string = chord.getNotes().get(5);
    //     int duration = low_e_string.getDuration();
    //     jdbcTemplate.update(sql3, measureIdInt, low_e_string.getFretNumber(), a_string.getFretNumber(), d_string.getFretNumber(), g_string.getFretNumber(), b_string.getFretNumber(), high_e_string.getFretNumber(), duration, 0); // chord_number == 0, as a new measure will only have a single rest
    //     return measureIdInt;
    // }

    public int addMeasureToRepo(Measure m, int compositionId, int measureNumber, boolean duplicating) {
        // Create a KeyHolder to capture the auto-generated key for the measure ID
        KeyHolder keyHolder = new GeneratedKeyHolder();
        // Insert measure and retrieve the auto-generated ID
        String sql = "INSERT INTO Measures (composition_id, measure_number) VALUES (?, ?)";
        jdbcTemplate.update(
            connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
                ps.setInt(1, compositionId);
                ps.setInt(2, measureNumber);
                return ps;
            },
            keyHolder
        );

        // Get the generated measure ID
        Number measureId_Number = keyHolder.getKey();
        if (measureId_Number == null) {
            throw new RuntimeException("Failed to get id");
        }
        int measureId = measureId_Number.intValue();
        String sql2 = "INSERT INTO Chords (measure_id, low_e_string, a_string, d_string, g_string, b_string, high_e_string, duration, chord_number) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        // if duplicating, this will add all chords to the measure
        // if not, we are thus making a new measure and it will add only one (composed of 6 rests)
        List<Chord> chords = m.getChords();
        for (int i = 0; i < chords.size(); i++) {
            Chord c = chords.get(i);
            Note high_e_string = c.getNotes().get(0);
            Note b_string = c.getNotes().get(1);
            Note g_string = c.getNotes().get(2);
            Note d_string = c.getNotes().get(3);
            Note a_string = c.getNotes().get(4);
            Note low_e_string = c.getNotes().get(5);
            int duration = low_e_string.getDuration();
            jdbcTemplate.update(sql2, measureId, low_e_string.getFretNumber(), a_string.getFretNumber(), d_string.getFretNumber(), g_string.getFretNumber(), b_string.getFretNumber(), high_e_string.getFretNumber(), duration, i);
        }
        return measureId;
    }


    // Note: "Measure numbers" are the index of the measure in the composition. The lowest numbered measures are first, and the highest numbered measures are last
    // "Measure IDs" are different, being a unique identifier across all compositions (However, I still like to check the composition ID in order to be safe)
    public int getMeasureNumber(int compositionId, int measureId) {
        System.out.println("*******compositionID=" + compositionId + ", measureID=" + measureId);
        String sql = "SELECT measure_number FROM Measures WHERE id = ? AND composition_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{measureId, compositionId}, Integer.class);
    }

    public void incrementMeasureNumbers(int compositionId, int measureNum) {
        String sql = "UPDATE Measures SET measure_number = measure_number + 1 WHERE composition_id = ? AND measure_number > ?";
        jdbcTemplate.update(sql, compositionId, measureNum);
    }

    public void deleteMeasure(int measureId) {
        String sql = "DELETE FROM Measures WHERE id = ?";
        chr.deleteChordsInMeasure(measureId);
        jdbcTemplate.update(sql, measureId);
    }

    // Get all measures in a composition, sorted by measure number (aka their relative position in the composition)
    public List<Measure> findMeasuresByCompositionId(int compositionId) {
        String sql = "SELECT * FROM Measures WHERE composition_id = ? ORDER BY measure_number ASC";
        PreparedStatementCreator psc = connection -> {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, compositionId);
            return ps;
        };
        
        RowMapper<Measure> rowMapper = new MeasureRowMapper();
        
        return jdbcTemplate.query(psc, rowMapper);
    }

    // newMeasure is a list of Chord objects (every chord points to its successor)
    public void editMeasure(List<Chord> newMeasure, int measureId, List<Chord> chords, int index) {
        String delete = "DELETE FROM Chords WHERE measure_id = ?";
        jdbcTemplate.update(delete, measureId);
        String sql = "INSERT INTO Chords (measure_id, low_e_string, a_string, d_string, g_string, b_string, high_e_string, duration, chord_number) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        int i = 0;
        System.out.println("EDITED MEASURE:");
        // add measure (first chord up to last new rest)
        for (Chord chord : newMeasure) {
            List<Note> notes = chord.getNotes();
            Note high_e_string = notes.get(0);
            Note b_string = notes.get(1);
            Note g_string = notes.get(2);
            Note d_string = notes.get(3);
            Note a_string = notes.get(4);
            Note low_e_string = notes.get(5);
            int duration = low_e_string.getDuration();
            jdbcTemplate.update(sql, measureId, low_e_string.getFretNumber(), a_string.getFretNumber(), d_string.getFretNumber(), g_string.getFretNumber(), b_string.getFretNumber(), high_e_string.getFretNumber(), duration, i);
            // newMeasure = newMeasure.getNext();
            // System.out.println(duration + ": " + low_e_string.getFretNumber() + " " + a_string.getFretNumber() + " " + d_string.getFretNumber() + " " + g_string.getFretNumber() + " " + b_string.getFretNumber() + " " + high_e_string.getFretNumber());
            i++;
        }
        // now add remaining chords after the last new rest
        System.out.println("edited measure, remaining chords: ");
        while (index < chords.size()) {
            Chord chord = chords.get(index);
            List<Note> notes = chord.getNotes();
            Note high_e_string = notes.get(0);
            Note b_string = notes.get(1);
            Note g_string = notes.get(2);
            Note d_string = notes.get(3);
            Note a_string = notes.get(4);
            Note low_e_string = notes.get(5);
            int duration = low_e_string.getDuration();
            jdbcTemplate.update(sql, measureId, low_e_string.getFretNumber(), a_string.getFretNumber(), d_string.getFretNumber(), g_string.getFretNumber(), b_string.getFretNumber(), high_e_string.getFretNumber(), duration, i);
            System.out.println(duration + ": " + low_e_string.getFretNumber() + " " + a_string.getFretNumber() + " " + d_string.getFretNumber() + " " + g_string.getFretNumber() + " " + b_string.getFretNumber() + " " + high_e_string.getFretNumber());
            i++; // used to update the chordNum in database
            index++;
        }
    }
}