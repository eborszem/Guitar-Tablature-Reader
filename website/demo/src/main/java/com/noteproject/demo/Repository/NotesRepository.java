package com.noteproject.demo.Repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.noteproject.demo.Model.Note;

@Repository
public class NotesRepository {
    private final JdbcTemplate jdbcTemplate;
    public NotesRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Note> getNotes(int chordId, int measureId) {
        String sql = "SELECT low_e_string, a_string, d_string, g_string, b_string, high_e_string, duration FROM Chords WHERE id = ? AND measure_id = ?";
        return jdbcTemplate.query(sql, rs -> {
            List<Note> notes = new ArrayList<>();
            if (rs.next()) {
                notes.add(new Note(rs.getInt("high_e_string"), 0));
                notes.add(new Note(rs.getInt("b_string"), 1));
                notes.add(new Note(rs.getInt("g_string"), 2));
                notes.add(new Note(rs.getInt("d_string"), 3));
                notes.add(new Note(rs.getInt("a_string"), 4));
                notes.add(new Note(rs.getInt("low_e_string"), 5));
            }
            return notes;
        }, chordId, measureId);

    }
}
