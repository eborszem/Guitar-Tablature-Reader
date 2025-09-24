package com.noteproject.demo.Repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.noteproject.demo.Model.Composition;
import com.noteproject.demo.Model.Measure;
import com.noteproject.demo.Model.Chord;
import com.noteproject.demo.Model.Chord.ChordDuration;

import java.sql.Timestamp;
import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class CompositionRepository {
    private final JdbcTemplate jdbcTemplate;
    public CompositionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Autowired
    NotesRepository nr;

    public List<Composition> getAllCompositions(Long userId) {
        String sql = "SELECT id, title, composer, time, user_id FROM Compositions WHERE user_id = ?";
        return jdbcTemplate.query(
            sql,
            (rs, rowNum) -> 
            new Composition(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("composer"),
                rs.getTimestamp("time"),
                rs.getLong("user_id")
            ),
            userId
        );
    }

    public int insertCompositionIntoDB(String title, String composer, Long userId) {
        String sql = "INSERT INTO Compositions (title, composer, time, user_id) VALUES (?, ?, ?, ?)";
        Timestamp timestamp = new Timestamp(new java.util.Date().getTime());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
            connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
                ps.setString(1, title);
                ps.setString(2, composer);
                ps.setTimestamp(3, timestamp);
                ps.setLong(4, userId);
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

    /**
     *  Fetches a Composition by its ID, including its associated Measures -> Chords -> Notes
     */
    public Composition getCompositionById(int compositionId) {
        try {
            // fetch composition details
            String compsql = "SELECT title, composer, time, user_id FROM Compositions WHERE id = ?";
            Composition composition =  jdbcTemplate.queryForObject(
                compsql,
                (rs, rowNum) -> new Composition(
                    compositionId,
                    rs.getString("title"),
                    rs.getString("composer"),
                    rs.getTimestamp("time"),
                    rs.getLong("user_id")
                ),
                compositionId
            );
            if (composition == null) throw new RuntimeException("Composition not found");
            // fetch measures for the composition
            String measuresql = "SELECT * FROM Measures WHERE composition_id = ? ORDER BY measure_number ASC";
            List<Measure> measures = jdbcTemplate.query(
                measuresql,
                (rs, rowNum) -> new Measure(
                    rs.getInt("id"),
                    rs.getInt("measure_number"),
                    compositionId
                ),
                compositionId
            );
            // fetch chords for each measure
            for (int i = 0; i < measures.size(); i++) {
                Measure measure = measures.get(i);
                String chordsql = "SELECT * FROM Chords WHERE measure_id = ? ORDER BY chord_number ASC";
                List<Chord> chords = jdbcTemplate.query(
                    chordsql,
                    (rs, rowNum) -> new Chord(
                        rs.getInt("id"),
                        measure.getId(),
                        rs.getInt("chord_number"),
                        // fetch notes for this chord
                        nr.getNotes(rs.getInt("id"), measure.getId()),
                        ChordDuration.valueOf(rs.getString("duration"))
                    ),
                    measure.getId()
                );
                
                measure.setChords(chords);
            }
            composition.setMeasures(measures);
            return composition;
        } catch (EmptyResultDataAccessException e) {
            System.out.println("Composition with ID " + compositionId + " does not exist.");
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving composition", e);
        }
    }
    
    
}
