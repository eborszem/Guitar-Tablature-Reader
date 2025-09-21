package com.noteproject.demo.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import com.noteproject.demo.Mapper.ChordRowMapper;
import com.noteproject.demo.Model.*;

@Repository
public class ChordRepository {
    private final JdbcTemplate jdbcTemplate;
    public ChordRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

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

    public List<Chord> findChordsByCompositionIdAndMeasureId(int compositionId, int measureId) {
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

    // newly added chord will go immediately after the (former) last chord in measure
    public void addChord(Chord c, int measureId) {
        System.out.println("TEST ADD CHORD");
        String str = "SELECT MAX(chord_number) FROM Chords WHERE measure_id = ?";
        Integer val = jdbcTemplate.queryForObject(str, new Object[]{measureId}, Integer.class);
        int chordNum = (val != null) ? val + 1 : 0;
        String sql = "INSERT INTO Chords (measure_id, low_e_string, a_string, d_string, g_string, " +
                    "b_string, high_e_string, duration, chord_number) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        List<Note> notes = c.getNotes();
        Note high_e = notes.get(0);
        Note b = notes.get(1);
        Note g = notes.get(2);
        Note d = notes.get(3);
        Note a = notes.get(4);
        Note low_e = notes.get(5);
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
}