package com.noteproject.demo.Controller;

import org.springframework.stereotype.Controller;
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

    @PostMapping("/delete")
    public ResponseEntity<String> deleteChord(@RequestParam("measureId") int measureId, @RequestParam("chordLocation") int chordLocation) {
        chs.deleteChord(measureId, chordLocation);
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    /* Basically, this code runs when the user confirms they want to change a chord via the virtual fretboard 
     * (which appears when the user clicks a chord). updateChord() takes in the updated and old string values. 
     * We also take in identifying information such as the unique measureId (as stored in database), measureLocation (relative
     * to the composition), and chordLocation (relative to the measureLocation).
     */
    @PostMapping("/update")
    public ResponseEntity<Chord> updateChord(@RequestBody Map<String, Object> payload, @RequestHeader("Authorization") String authHeader) {
        System.out.println("reached update chord post mapping");
        String token = authHeader.substring(7); // remove "Bearer "
        System.out.println("TOKEN="+token);
        String username = jwtService.extractUsername(token);
        Optional<User> user = ur.findByUsername(username);
        if (user.isEmpty()) {
            throw new IllegalStateException("User not found");
        }

        int compositionId = (Integer) payload.get("compositionId");
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

        Composition comp = cs.getCompositionById(compositionId); 
        Chord oldChord = chs.findChordByMeasureIdAndChordId(measureId, chordId);
        List<Note> oldNotes = oldChord.getNotes();
            
        Chord updatedChord = new Chord(chordId, measureId, oldChord.getChordNumber(), updatedNotes, newDuration);
        // if (durUpdate) {
        //     System.out.println("newDuration="+newDuration+", oldDur="+oldDur);
        //     chs.updateDurations(newDuration, oldDur, updatedChord, measureId, chordLocation, HomeController.globalCompositionId);
        // } else {
            // was above if previously, moved here to avoid possible bugs
        chs.updateChord(updatedChord, measureId, chordId);
        // }
        return ResponseEntity.ok(updatedChord);
    }

}