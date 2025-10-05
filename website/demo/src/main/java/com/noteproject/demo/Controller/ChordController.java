package com.noteproject.demo.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import com.noteproject.demo.Entity.User;
import com.noteproject.demo.Model.*;
import com.noteproject.demo.Model.Chord.ChordDuration;
import com.noteproject.demo.Repository.NotesRepository;
import com.noteproject.demo.Repository.UserRepository;
import com.noteproject.demo.Service.ChordService;
import com.noteproject.demo.Service.CompositionService;
import com.noteproject.demo.Service.JwtService;

import org.springframework.web.bind.annotation.RequestMapping;
@RequestMapping("/chord")
@Controller
public class ChordController {    
    @Autowired
    CompositionService cs;
    
    @Autowired
    ChordService chs;

    @Autowired
    JwtService jwtService;

    @Autowired
    UserRepository ur;

    final int NUM_STRINGS = 6;
    final int REST = -1;

    @PostMapping("/update")
    public ResponseEntity<Chord> updateChord(@RequestBody Map<String, Object> payload, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7); // remove "Bearer "
        String username = jwtService.extractUsername(token);
        Optional<User> user = ur.findByUsername(username);
        if (user.isEmpty()) {
            throw new IllegalStateException("User not found");
        }

        // int compositionId = (Integer) payload.get("compositionId");
        int measureId = (Integer) payload.get("measureId");
        int chordId = (Integer) payload.get("chordId");
        Object newDurationObj = payload.get("newDuration");
        String newDurationStr = newDurationObj.toString();
        ChordDuration newDuration = ChordDuration.valueOf(newDurationStr);

        @SuppressWarnings("unchecked")
        List<Integer> updatedNoteFrets = (List <Integer>) payload.get("updatedNotes");
        List<Note> updatedNotes = new ArrayList<>();
        for (int stringIdx = 0; stringIdx < NUM_STRINGS; stringIdx++) {
            updatedNotes.add(new Note(updatedNoteFrets.get(stringIdx), stringIdx));
        }

        Chord oldChord = chs.findChordByMeasureIdAndChordId(measureId, chordId);
        Chord updatedChord = new Chord(chordId, measureId, oldChord.getIndex(), updatedNotes, newDuration);
        chs.updateChord(updatedChord, measureId, chordId);
        return ResponseEntity.ok(updatedChord);
    }

    // get measureId, get chordId of chord we clicked "add" on. from these params, get the chord index
    // of that chord in the measure.
    // then, create a new chord (in respective measure) with that chord index + 1,
    // and push all other chords back by 1
    @PostMapping("/add")
    public ResponseEntity<Chord> addChord(@RequestBody Map<String, Object> payload, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String username = jwtService.extractUsername(token);
        Optional<User> user = ur.findByUsername(username);
        if (user.isEmpty()) {
            throw new IllegalStateException("User not found");
        }
        int measureId = Integer.parseInt((String) payload.get("measureId"));
        int chordId = Integer.parseInt((String) payload.get("chordId"));
        return ResponseEntity.ok(chs.addChord(measureId, chordId));
    }

    @PostMapping("/duplicate")
    public ResponseEntity<Chord> duplicateChord(@RequestBody Map<String, Object> payload, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String username = jwtService.extractUsername(token);
        Optional<User> user = ur.findByUsername(username);
        if (user.isEmpty()) {
            throw new IllegalStateException("User not found");
        }
        int measureId = Integer.parseInt((String) payload.get("measureId"));
        int chordId = Integer.parseInt((String) payload.get("chordId"));
        return ResponseEntity.ok(chs.duplicateChord(measureId, chordId));
    }

    @DeleteMapping()
    public ResponseEntity<Map<String, String>> deleteChord(@RequestBody Map<String, Object> payload, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String username = jwtService.extractUsername(token);
        Optional<User> user = ur.findByUsername(username);
        if (user.isEmpty()) {
            throw new IllegalStateException("User not found");
        }
        int measureId = Integer.parseInt((String) payload.get("measureId"));
        int chordId = Integer.parseInt((String) payload.get("chordId"));
        int chordIndex = Integer.parseInt((String) payload.get("chordIndex"));
        chs.deleteChord(measureId, chordId, chordIndex);
        return ResponseEntity.ok(Map.of(
            "message", "Chord deleted"
        ));
    }

    @PostMapping("/swap")
    public ResponseEntity<Map<String, String>> swapChord(@RequestBody Map<String, Object> payload, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String username = jwtService.extractUsername(token);
        Optional<User> user = ur.findByUsername(username);
        if (user.isEmpty()) {
            throw new IllegalStateException("User not found");
        }
        int measureId = Integer.parseInt((String) payload.get("measureId"));
        int chordId = Integer.parseInt((String) payload.get("chordId"));
        String direction = (String) payload.get("direction"); // left or right
        chs.swapChord(measureId, chordId, direction);
        return ResponseEntity.ok(Map.of(
            "message", "Chords swapped"
        ));
    }

}