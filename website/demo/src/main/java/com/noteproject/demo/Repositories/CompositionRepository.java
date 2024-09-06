package com.noteproject.demo.Repositories;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.noteproject.demo.Mapper.ChordRowMapper;
import org.springframework.jdbc.core.RowMapper;
import com.noteproject.demo.Model.Chord;
import com.noteproject.demo.Model.Composition;
import com.noteproject.demo.Model.Measure;
import com.noteproject.demo.Model.Note;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;

@Repository
public class CompositionRepository {

    private final JdbcTemplate jdbcTemplate;

    public CompositionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> getAllRecords() {
        String sql = "SELECT * FROM test";
        return jdbcTemplate.queryForList(sql);
    }

    public int insertRecord(int val) {
        String sql = "INSERT INTO test (col) VALUES (?)";
        return jdbcTemplate.update(sql, val);
    }

    public void addMeasureToRepo(Measure m) {
        // Retrieve the current max measure number for composition_id 0
        String sql = "SELECT MAX(measure_number) FROM Measures WHERE composition_id = 1";
        Integer val = jdbcTemplate.queryForObject(sql, Integer.class);
        int numMeasures;
        if (val != null) {
            numMeasures = val;
        } else {
            numMeasures = 0;
        }

        // Create a KeyHolder to capture the auto-generated key for the measure ID
        KeyHolder keyHolder = new GeneratedKeyHolder();

        // Insert measure and retrieve the auto-generated ID
        String sql2 = "INSERT INTO Measures (composition_id, measure_number) VALUES (?, ?)";
        jdbcTemplate.update(
            connection -> {
                PreparedStatement ps = connection.prepareStatement(sql2, new String[]{"id"});
                ps.setInt(1, 1);  // New measure number
                ps.setInt(2, numMeasures + 1);  // New measure number
                return ps;
            },
            keyHolder
        );

        // Get the generated measure ID
        Number measureId = keyHolder.getKey();
        if (measureId == null) {
            throw new RuntimeException("Failed to retrieve generated measure ID");
        }
        int measureIdInt = measureId.intValue();
        
        String sql3 = "INSERT INTO Chords (measure_id, low_e_string, a_string, d_string, g_string, b_string, high_e_string) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Note low_e_string = m.getChord().getNote();
        Note a_string = low_e_string.next;
        Note d_string = a_string.next;
        Note g_string = d_string.next;
        Note b_string = g_string.next;
        Note high_e_string = b_string.next;
        jdbcTemplate.update(sql3, measureIdInt, low_e_string.getFretNumber(), a_string.getFretNumber(), d_string.getFretNumber(), g_string.getFretNumber(), b_string.getFretNumber(), high_e_string.getFretNumber());
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
}
