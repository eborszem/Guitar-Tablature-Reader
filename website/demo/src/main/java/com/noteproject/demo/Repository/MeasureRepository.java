package com.noteproject.demo.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import com.noteproject.demo.Model.Note;
import com.noteproject.demo.Model.Chord;
import com.noteproject.demo.Model.Chord.ChordDuration;
import com.noteproject.demo.Model.Measure;

@Repository
public class MeasureRepository {
    private final JdbcTemplate jdbcTemplate;
    public MeasureRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Autowired
    ChordRepository chr;

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
            ChordDuration duration = c.getDuration();
            Note high_e_string = c.getNotes().get(0);
            Note b_string = c.getNotes().get(1);
            Note g_string = c.getNotes().get(2);
            Note d_string = c.getNotes().get(3);
            Note a_string = c.getNotes().get(4);
            Note low_e_string = c.getNotes().get(5);
            jdbcTemplate.update(sql2,
                measureId, 
                low_e_string.getFretNumber(),
                a_string.getFretNumber(),
                d_string.getFretNumber(),
                g_string.getFretNumber(),
                b_string.getFretNumber(),
                high_e_string.getFretNumber(),
                duration.name(),
                i
            );
        }
        return measureId;
    }


    // Note: "Measure numbers" are the index of the measure in the composition. The lowest numbered measures are first, and the highest numbered measures are last
    // "Measure IDs" are different, being a unique identifier across all compositions (However, I still like to check the composition ID in order to be safe)
    public int getMeasureNumber(int compositionId, int measureId) {
        String sql = "SELECT measure_number FROM Measures WHERE id = ? AND composition_id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, measureId, compositionId);
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
    // public List<Measure> findMeasuresByCompositionId(int compositionId) {
    //     String sql = "SELECT * FROM Measures WHERE composition_id = ? ORDER BY measure_number ASC";
    //     PreparedStatementCreator psc = connection -> {
    //         PreparedStatement ps = connection.prepareStatement(sql);
    //         ps.setInt(1, compositionId);
    //         return ps;
    //     };
        
    //     RowMapper<Measure> rowMapper = new MeasureRowMapper();
        
    //     return jdbcTemplate.query(psc, rowMapper);
    // }

    /*
     * params: 
     * newMeasure (list of chords that will replace the current measure)
     * measureId (id of the measure in database)
     * chords (all chords in the composition, used to add remaining chords after newMeasure)
     * idx (the index in chords where we will start adding remaining chords after newMeasure)
     */
    public void editMeasure(List<Chord> newMeasure, int measureId, List<Chord> chords, int idx) {
        String delete = "DELETE FROM Chords WHERE measure_id = ?";
        jdbcTemplate.update(delete, measureId);
        String sql = "INSERT INTO Chords (measure_id, low_e_string, a_string, d_string, g_string, b_string, high_e_string, duration, chord_number) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        System.out.println("EDITED MEASURE:");
        int i = 0;
        // add measure (first chord up to last new rest)
        for (; i < newMeasure.size(); i++) {
            Chord chord = newMeasure.get(i);
            ChordDuration duration = chord.getDuration();
            List<Note> notes = chord.getNotes();
            Note high_e_string = notes.get(0);
            Note b_string = notes.get(1);
            Note g_string = notes.get(2);
            Note d_string = notes.get(3);
            Note a_string = notes.get(4);
            Note low_e_string = notes.get(5);
            jdbcTemplate.update(sql, measureId, low_e_string.getFretNumber(), a_string.getFretNumber(), d_string.getFretNumber(), g_string.getFretNumber(), b_string.getFretNumber(), high_e_string.getFretNumber(), duration.name(), i);
        }
        // now add remaining chords after the last new rest
        System.out.println("edited measure, remaining chords: ");
        while (idx < chords.size()) {
            Chord chord = chords.get(idx++);
            ChordDuration duration = chord.getDuration();
            List<Note> notes = chord.getNotes();
            Note high_e_string = notes.get(0);
            Note b_string = notes.get(1);
            Note g_string = notes.get(2);
            Note d_string = notes.get(3);
            Note a_string = notes.get(4);
            Note low_e_string = notes.get(5);
            jdbcTemplate.update(sql, measureId, low_e_string.getFretNumber(), a_string.getFretNumber(), d_string.getFretNumber(), g_string.getFretNumber(), b_string.getFretNumber(), high_e_string.getFretNumber(), duration.name(), i++);
            System.out.println(duration + ": " + low_e_string.getFretNumber() + " " + a_string.getFretNumber() + " " + d_string.getFretNumber() + " " + g_string.getFretNumber() + " " + b_string.getFretNumber() + " " + high_e_string.getFretNumber());
        }
    }
}