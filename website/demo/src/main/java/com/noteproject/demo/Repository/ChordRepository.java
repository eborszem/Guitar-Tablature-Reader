package com.noteproject.demo.Repository;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import com.noteproject.demo.Model.*;
import com.noteproject.demo.Model.Chord.ChordDuration;

@Repository
public class ChordRepository {
    private final JdbcTemplate jdbcTemplate;
    public ChordRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Autowired
    NotesRepository nr;
    
    public void deleteChordsInMeasure(int measureId) {
        String sql = "DELETE FROM Chords WHERE measure_id = ?";
        jdbcTemplate.update(sql, measureId);
    }

    public void deleteChord(int measureId, int chordLocation) {
        String sql = "DELETE FROM Chords WHERE measure_id = ? AND chord_number = ?";
        jdbcTemplate.update(sql, measureId, chordLocation);
        String sql2 = "UPDATE Chords SET chord_number = chord_number - 1 WHERE measure_id = ? AND chord_number > ?";
        jdbcTemplate.update(sql2, measureId, chordLocation);
    }

    public List<Chord> findChordsByCompositionIdAndMeasureId(int compositionId, int measureId) {
        String sql = "SELECT c.* FROM chords c JOIN measures m ON c.measure_id = m.id WHERE m.composition_id = ? AND c.measure_id = ? ORDER BY chord_number ASC";
        
        List<Chord> chords = jdbcTemplate.query(
            sql,
            (rs, rowNum) -> new Chord(
                rs.getInt("id"),
                measureId,
                rs.getInt("chord_number"),
                nr.getNotes(rs.getInt("id"),measureId),
                ChordDuration.valueOf(rs.getString("duration"))
            ),
            measureId,
            compositionId
        );
        return chords;
    }

    public Chord findChordByMeasureIdAndChordId(int measureId, int chordId) {
        String sql = "SELECT * FROM chords WHERE measure_id = ? AND id = ?";
        
        Chord chord = jdbcTemplate.queryForObject(
            sql,
            (rs, rowNum) -> new Chord(
                chordId,
                measureId,
                rs.getInt("chord_number"),
                nr.getNotes(rs.getInt("id"),measureId),
                ChordDuration.valueOf(rs.getString("duration"))
            ),
            measureId,
            chordId
        );
        return chord;
    }

    public void updateChord(Chord c, int measureId, int chordId) {
        String sql = "UPDATE Chords SET low_e_string = ?, a_string = ?, d_string = ?, g_string = ?, b_string = ?, high_e_string = ?, duration = ? WHERE measure_id = ? AND id = ?";
        List<Note> notes = c.getNotes();
        for (Note n : notes) {
            System.out.println("{note} " + n.getFretNumber());
        }
        Note high_e = notes.get(0);
        Note b = notes.get(1);
        Note g = notes.get(2);
        Note d = notes.get(3);
        Note a = notes.get(4);
        Note low_e = notes.get(5);
        ChordDuration duration = c.getDuration();
        jdbcTemplate.update(sql, new PreparedStatementSetter() {
            @Override
            public void setValues(@NonNull java.sql.PreparedStatement ps) throws SQLException {
                ps.setObject(1, low_e.getFretNumber());
                ps.setObject(2, a.getFretNumber());
                ps.setObject(3, d.getFretNumber());
                ps.setObject(4, g.getFretNumber());
                ps.setObject(5, b.getFretNumber());
                ps.setObject(6, high_e.getFretNumber());
                ps.setObject(7, duration.name());
                ps.setInt(8, measureId);
                ps.setInt(9, chordId);
            }
        });
    }
}