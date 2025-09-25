package com.noteproject.demo.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
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

    private final int REST = -1;
    
    public List<Chord> findChordsByMeasureId(int measureId) {
        String sql = "SELECT * FROM Chords WHERE measure_id = ?";
        
        List<Chord> chords = jdbcTemplate.query(
            sql,
            (rs, rowNum) -> new Chord(
                rs.getInt("id"),
                measureId,
                rs.getInt("chord_number"),
                nr.getNotes(rs.getInt("id"),measureId),
                ChordDuration.valueOf(rs.getString("duration"))
            ),
            measureId
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

    public Chord findChordByMeasureIdAndChordIndex(int measureId, int chordIdx) {
        String sql = "SELECT * FROM chords WHERE measure_id = ? AND chord_number = ?";
        
        Chord chord = jdbcTemplate.queryForObject(
            sql,
            (rs, rowNum) -> new Chord(
                rs.getInt("id"),
                measureId,
                chordIdx,
                nr.getNotes(rs.getInt("id"),measureId),
                ChordDuration.valueOf(rs.getString("duration"))
            ),
            measureId,
            chordIdx
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

	public Chord addChord(int measureId, int chordId, Chord prev) {
        String incrementSql = "UPDATE Chords SET chord_number = chord_number + 1 WHERE measure_id = ? AND chord_number > ?";
        jdbcTemplate.update(incrementSql, measureId, prev.getIndex());
        String insertSql = "INSERT INTO Chords (measure_id, low_e_string, a_string, d_string, g_string, b_string, high_e_string, duration, chord_number) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(insertSql, new String[]{"id"});
            ps.setInt(1, measureId);
            ps.setInt(2, REST);
            ps.setInt(3, REST);
            ps.setInt(4, REST);
            ps.setInt(5, REST);
            ps.setInt(6, REST);
            ps.setInt(7, REST);
            ps.setString(8, ChordDuration.QUARTER.name());
            ps.setInt(9, prev.getIndex() + 1);
            return ps;
        }, keyHolder);
        try {
            int newChordId = keyHolder.getKey().intValue(); // id of newly inserted chord
            return findChordByMeasureIdAndChordId(measureId, newChordId); // return the newly added chord
        } catch (Exception e) {
            return new Chord();
        }
	}

    public Chord duplicateChord(int measureId, int chordId, Chord original) {
        String incrementSql = "UPDATE Chords SET chord_number = chord_number + 1 WHERE measure_id = ? AND chord_number > ?";
        jdbcTemplate.update(incrementSql, measureId, original.getIndex());
        String insertSql = "INSERT INTO Chords (measure_id, low_e_string, a_string, d_string, g_string, b_string, high_e_string, duration, chord_number) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(insertSql, new String[]{"id"});
            ps.setInt(1, measureId);
            ps.setInt(2, original.getNotes().get(5).getFretNumber());
            ps.setInt(3, original.getNotes().get(4).getFretNumber());
            ps.setInt(4, original.getNotes().get(3).getFretNumber());
            ps.setInt(5, original.getNotes().get(2).getFretNumber());
            ps.setInt(6, original.getNotes().get(1).getFretNumber());
            ps.setInt(7, original.getNotes().get(0).getFretNumber());
            ps.setString(8, original.getDuration().name());
            ps.setInt(9, original.getIndex() + 1);
            return ps;
        }, keyHolder);
        try {
            int newChordId = keyHolder.getKey().intValue(); // id of newly inserted chord
            return findChordByMeasureIdAndChordId(measureId, newChordId); // return the duplicatd chord
        } catch (Exception e) {
            return new Chord();
        }
    }

    public void deleteChord(int measureId, int chordId, int chordIndex) {
        String deleteSql = "DELETE FROM Chords WHERE measure_id = ? AND id = ?";
        jdbcTemplate.update(deleteSql, measureId, chordId);
        String updateSql = "UPDATE Chords SET chord_number = chord_number - 1 WHERE measure_id = ? AND chord_number > ?";
        jdbcTemplate.update(updateSql, measureId, chordIndex);
    }

    public void deleteChordsInMeasure(int measureId) {
        String sql = "DELETE FROM Chords WHERE measure_id = ?";
        jdbcTemplate.update(sql, measureId);
    }

    public void swapChord(Chord cur, Chord swap) {
        int curIdx = cur.getIndex();
        int swapIdx = swap.getIndex();
        int measureId = cur.getMeasureId();
        String sql =    "UPDATE Chords " +
                        "SET chord_number = CASE " +
                            "WHEN id = ? THEN ? " +
                            "WHEN id = ? THEN ? " +
                        "END " +
                        "WHERE measure_id = ? " +
                        "AND id IN (?, ?)";
        jdbcTemplate.update(sql,
            cur.getId(), swapIdx,
            swap.getId(), curIdx,
            measureId,
            cur.getId(), swap.getId()
        );
    }
}