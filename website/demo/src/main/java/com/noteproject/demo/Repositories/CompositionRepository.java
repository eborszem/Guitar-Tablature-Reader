package com.noteproject.demo.Repositories;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;

import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.noteproject.demo.Mapper.ChordRowMapper;
import com.noteproject.demo.Mapper.MeasureRowMapper;

import org.springframework.jdbc.core.RowMapper;
import com.noteproject.demo.Model.Chord;
import com.noteproject.demo.Model.Composition;
import com.noteproject.demo.Model.Measure;
import com.noteproject.demo.Model.Note;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

@Repository
public class CompositionRepository {
    private int measureId;
    private final JdbcTemplate jdbcTemplate;

    public CompositionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /*public List<Map<String, Object>> getAllRecords() {
        String sql = "SELECT * FROM test";
        return jdbcTemplate.queryForList(sql);
    }

    public int insertRecord(int val) {
        String sql = "INSERT INTO test (col) VALUES (?)";
        return jdbcTemplate.update(sql, val);
    }*/

    public int getMeasureId() {
        return this.measureId;
    }

    public void addMeasureToRepo(Measure m) {
        // Retrieve the current max measure number for composition_id 0
        String sql = "SELECT MAX(measure_number) FROM Measures WHERE composition_id = 1"; // TODO: uses 1 as value for composition id
        Integer val = jdbcTemplate.queryForObject(sql, Integer.class);
        int numMeasures;
        if (val != null) {
            numMeasures = val;
        } else {
            numMeasures = 0;
        }
        measureId = val;

        // Create a KeyHolder to capture the auto-generated key for the measure ID
        KeyHolder keyHolder = new GeneratedKeyHolder();

        // Insert measure and retrieve the auto-generated ID
        String sql2 = "INSERT INTO Measures (composition_id, measure_number) VALUES (?, ?)";
        jdbcTemplate.update(
            connection -> {
                PreparedStatement ps = connection.prepareStatement(sql2, new String[]{"id"});
                ps.setInt(1, 1);  //TODO: Implement support for displaying multiple compositions
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
        
        String sql3 = "INSERT INTO Chords (measure_id, low_e_string, a_string, d_string, g_string, b_string, high_e_string, duration, chord_number) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Note low_e_string = m.getChord().getNote();
        Note a_string = low_e_string.next;
        Note d_string = a_string.next;
        Note g_string = d_string.next;
        Note b_string = g_string.next;
        Note high_e_string = b_string.next;
        int duration = low_e_string.getDuration();
        jdbcTemplate.update(sql3, measureIdInt, low_e_string.getFretNumber(), a_string.getFretNumber(), d_string.getFretNumber(), g_string.getFretNumber(), b_string.getFretNumber(), high_e_string.getFretNumber(), duration, 0);
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

    public List<Measure> findMeasuresByCompositionId(int compositionId) {
        String sql = "SELECT * FROM Measures WHERE composition_id = ?";
        
        PreparedStatementCreator psc = connection -> {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, compositionId);
            return ps;
        };
        
        RowMapper<Measure> rowMapper = new MeasureRowMapper();
        
        return jdbcTemplate.query(psc, rowMapper);
    }

    public List<Composition> getAllCompositions() {
        String sql = "SELECT id, title FROM Compositions";
        return jdbcTemplate.query(sql, (rs, rowNum) -> 
            new Composition(rs.getInt("id"), rs.getString("title"))
        );
    }

    public Measure formatComposition(int compositionId) {
        List<Chord> chords = findChordsByCompositionId(compositionId);
        List<Measure> measures = findMeasuresByCompositionId(compositionId);
        // sort all chords by their measure id (relative position in measure)
        // sort all measures by their measure number (relative position in composition)
        // then add x chords to measure 0, then y chords to measure 1, etc.
        //chords.sort((c1, c2) -> Integer.compare(c1.getMeasureId(), c2.getMeasureId()));
        measures.sort((c1, c2) -> Integer.compare(c1.getMeasureNumber(), c2.getMeasureNumber()));
        HashMap<Integer, ArrayList<Chord>> measureIdToChords = new HashMap<>();
        // creates HashMap with measure ids as keys, and chords (that go into those measures) as values
        for (Chord c : chords) {
            int measureId = c.getMeasureId();
            if (!measureIdToChords.containsKey(measureId)) {
                ArrayList<Chord> arr = new ArrayList<>();
                arr.add(c);
                measureIdToChords.put(measureId, arr);
            } else {
                ArrayList<Chord> arr = measureIdToChords.get(measureId);
                arr.add(c);
                measureIdToChords.put(measureId, arr);
            }
            
        }
        Measure dummy = new Measure();
        Measure measure = dummy;
        for (Measure m : measures) {
            System.out.println("-----------measure-----------");
            int id = m.getId(); // can use this to get measure's chords
            ArrayList<Chord> chordsFromMeasureId = measureIdToChords.get(id);
            Chord cDummy = new Chord();
            Chord chord = cDummy;
            for (Chord c : chordsFromMeasureId) {
                System.out.print(c.getNote().getFretNumber());
                chord.setNext(c);
                chord = chord.getNext();
            }
            System.out.print(",dur="+chord.getNote().getDuration());
            System.out.println();
            measure.setNext(new Measure(cDummy.getNext()));
            //System.out.println("should print fret num of cur measure chord 1: " + measure.getNext().getChord().getNote().getFretNumber());
            measure = measure.getNext();
            
        }
        // return measure
        System.out.println("1st measure 1st chord: " + dummy.getNext().getChord().toString());
        System.out.println("1st measure 2nd chord: " + dummy.getNext().getChord().getNext().toString());
        System.out.println("2nd measure 1st chord: " + dummy.getNext().getNext().getChord().toString());
        return dummy.getNext();
    }

    public List<Measure> getMeasures(int compositionId) {
        List<Measure> measures = new ArrayList<>();
        Measure dummy = formatComposition(compositionId);
        Measure m = dummy;
        while (m != null) {
            measures.add(m);
            m = m.getNext();
        }
        System.out.println("MEASURES ARRAY="+measures.toString());
        return measures;
    }

    public void updateChord(Chord c, int measureId, int chordNum) {
        String sql = "UPDATE Chords SET low_e_string = ?, a_string = ?, d_string = ?, g_string = ?, b_string = ?, high_e_string = ?, duration = ? WHERE measure_id = ? AND chord_number = ?";
        Note high_e = c.getNote();
        Note b = high_e.getNext();
        Note g = b.getNext();
        Note d = g.getNext();
        Note a = d.getNext();
        Note low_e = a.getNext();
        int duration = high_e.getDuration();
        System.out.println("==============PRINTING NOTES E to e==============");
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

    public void changeComposition(String composition) {

    }
}
