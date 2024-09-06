package com.noteproject.demo.Mapper;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.noteproject.demo.Model.Chord;

import com.noteproject.demo.Model.Composition;
import com.noteproject.demo.Model.Measure;
import com.noteproject.demo.Model.Note;

public class MeasureRowMapper implements RowMapper<Measure> {
    @Override
    public Measure mapRow(ResultSet rs, int rowNum) throws SQLException {
        Measure measure = new Measure();
        measure.setId(rs.getInt("id"));
        measure.setCompositionId(rs.getInt("composition_id"));
        measure.setMeasureNumber(rs.getInt("measure_number"));
        return measure;
    }
}
