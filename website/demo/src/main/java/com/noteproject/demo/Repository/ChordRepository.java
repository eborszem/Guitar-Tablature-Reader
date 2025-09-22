package com.noteproject.demo.Repository;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import com.noteproject.demo.Model.*;

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

    // public List<Chord> findChordsByCompositionId(int compositionId) {
    //     String sql = "SELECT c.* FROM chords c JOIN measures m ON c.measure_id = m.id WHERE m.composition_id = ?";
        
    //     List<Chord> chords = jdbcTemplate.query(
    //         sql,
    //         (rs, rowNum) -> new Chord(
    //             rs.getInt("id"),
    //             rs.getInt("measure_id"),
    //             rs.getInt("chord_number"),
    //             // fetch notes for this chord
    //             getNotes(rs.getInt("id"), rs.getInt("measure_id"))
    //         ),
    //         compositionId
    //     );
        
    //     return chords;
    // }

    public List<Chord> findChordsByCompositionIdAndMeasureId(int compositionId, int measureId) {
        String sql = "SELECT c.* FROM chords c JOIN measures m ON c.measure_id = m.id WHERE m.composition_id = ? AND c.measure_id = ? ORDER BY chord_number ASC";
        
        List<Chord> chords = jdbcTemplate.query(
            sql,
            (rs, rowNum) -> new Chord(
                rs.getInt("id"),
                measureId,
                rs.getInt("chord_number"),
                // fetch notes for this chord
                nr.getNotes(rs.getInt("id"),measureId)
            ),
            measureId,
            compositionId
        );
        return chords;
    }

    public void updateChord(Chord c, int measureId, int chordNum) {
        System.out.println("TEST UPDATE CHORD DUR");
        String sql = "UPDATE Chords SET low_e_string = ?, a_string = ?, d_string = ?, g_string = ?, b_string = ?, high_e_string = ?, duration = ? WHERE measure_id = ? AND chord_number = ?";
        List<Note> notes = c.getNotes();
        Note high_e = notes.get(0);
        Note b = notes.get(1);
        Note g = notes.get(2);
        Note d = notes.get(3);
        Note a = notes.get(4);
        Note low_e = notes.get(5);
        int duration = high_e.getDuration();

        jdbcTemplate.update(sql, new PreparedStatementSetter() {
            @Override
            public void setValues(@NonNull java.sql.PreparedStatement ps) throws SQLException {
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
}