package com.noteproject.demo.Mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import com.noteproject.demo.Model.*;

public class ChordRowMapper implements RowMapper<Chord> {
    @Override
    public Chord mapRow(ResultSet rs, int rowNum) throws SQLException {
        int dur = rs.getInt("duration");
        Note highE = new Note(rs.getInt("high_e_string"), 0, dur, false);
        Note b = new Note(rs.getInt("b_string"), 1, dur, false);
        Note g = new Note(rs.getInt("g_string"), 2, dur, false);
        Note d = new Note(rs.getInt("d_string"), 3, dur, false);
        Note a = new Note(rs.getInt("a_string"), 4, dur, false);
        Note lowE = new Note(rs.getInt("low_e_string"), 5, dur, false);
        List<Note> notes = new ArrayList<>();
        notes.add(highE);
        notes.add(b);
        notes.add(g);
        notes.add(d);
        notes.add(a);
        notes.add(lowE);
        Chord chord = new Chord(notes);
        chord.setId(rs.getInt("id"));
        chord.setChordNumber(rs.getInt("chord_number"));
        chord.setMeasureId(rs.getInt("measure_id"));
        return chord;
    }
}
