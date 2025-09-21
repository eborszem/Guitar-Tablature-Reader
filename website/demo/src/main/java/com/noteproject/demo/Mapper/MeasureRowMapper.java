package com.noteproject.demo.Mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import com.noteproject.demo.Model.Measure;

public class MeasureRowMapper implements RowMapper<Measure> {
    @Override
    public Measure mapRow(ResultSet rs, int rowNum) throws SQLException {
        Measure measure = new Measure(
            rs.getInt("id"),
            rs.getInt("measure_number"),
            rs.getInt("composition_id")
        );
        return measure;
    }
}
