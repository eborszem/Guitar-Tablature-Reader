package com.noteproject.demo.Repository;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import com.noteproject.demo.Model.Composition;
import java.sql.Timestamp;
import java.sql.PreparedStatement;
import java.util.List;


@Repository
public class CompositionRepository {
    private final JdbcTemplate jdbcTemplate;
    public CompositionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // no compositions = exception will occur
    // gets a dummy measure which just tells us the composition's time signature (the note that gets the beat and how many beat notes are in a measure)
    // example:
    // 4 4 time: has a quarter note as its beat note and there is a duration of 4 quarter notes per measure
    // 2 4 time: beat note: quarter note, 2 quarter notes per measure

    // public Measure getTimeSignature(int compositionId) {
    //     String sql = "SELECT note_value, num_note_values_per_measure FROM Compositions WHERE id = ?";
    //     return jdbcTemplate.queryForObject(sql, new Object[]{compositionId}, (rs, rowNum) -> 
    //         new Measure(rs.getInt("note_value"), rs.getInt("num_note_values_per_measure"))
    //     );
    // }

    public List<Composition> getAllCompositions() {
        String sql = "SELECT id, title FROM Compositions";
        return jdbcTemplate.query(sql, (rs, rowNum) -> 
            new Composition(rs.getInt("id"), rs.getString("title"))
        );
    }
   
    public int insertCompositionIntoDB(String title, String composer) {
        String sql = "INSERT INTO Compositions (title, composer, time, note_value, num_note_values_per_measure) VALUES (?, ?, ?, ?, ?)";
        Timestamp timestamp = new Timestamp(new java.util.Date().getTime());
        int noteValue = 4;
        int numNotesPerMeasure = 4;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
            connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
                ps.setString(1, title);
                ps.setString(2, composer);
                ps.setTimestamp(3, timestamp);
                ps.setInt(4, noteValue);
                ps.setInt(5, numNotesPerMeasure);
                return ps;
            },
            keyHolder
        );
        Number compId = keyHolder.getKey();
        if (compId == null) {
            throw new RuntimeException("Failed to get id");
        }
        int compIdInt = compId.intValue();
        return compIdInt;
    }

    public Composition getCompositionInfo(int compositionId) {
        String sql = "SELECT title, composer, time FROM Compositions WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{compositionId}, (rs, rowNum) -> 
            new Composition(rs.getString("title"), rs.getString("composer"), rs.getTimestamp("time"))
        );
    }

    public Composition getCompositionById(int compIdInt) {
        String sql = "SELECT title, composer, time FROM Compositions WHERE id = ?";
    
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{compIdInt}, (rs, rowNum) -> {
                // Extract only title, composer, and time
                return new Composition(
                    rs.getString("title"),
                    rs.getString("composer"),
                    rs.getTimestamp("time")
                );
            });
        } catch (EmptyResultDataAccessException e) {
            System.out.println("Composition with ID " + compIdInt + " does not exist.");
            return null;
        }
    }
    
    
}
