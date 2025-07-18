package com.noteproject.demo.Mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
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
        highE.next = b;
        b.next = g;
        g.next = d;
        d.next = a;
        a.next = lowE;
        Chord chord = new Chord(highE);
        chord.setId(rs.getInt("id"));
        chord.setChordNumber(rs.getInt("chord_number"));
        chord.setMeasureId(rs.getInt("measure_id"));
        return chord;
    }
}
