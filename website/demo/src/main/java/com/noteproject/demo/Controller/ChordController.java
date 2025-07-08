package com.noteproject.demo.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.noteproject.demo.Model.*;
import com.noteproject.demo.Repository.ChordRepository;
import com.noteproject.demo.Service.ChordService;
import com.noteproject.demo.Service.CompositionService;

@Controller
public class ChordController {    
    @Autowired
    CompositionService cs;
    @Autowired
    ChordRepository chr;
    @Autowired
    ChordService chs;

    @RequestMapping(value = "/deleteChord", method = RequestMethod.POST)
    public ResponseEntity<String> deleteChord(@RequestParam("measureId") int measureId, @RequestParam("chordLocation") int chordLocation) {
        chr.deleteChord(measureId, chordLocation);
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    /* Basically, this code runs when the user confirms they want to change a chord via the virtual fretboard 
     * (which appears when the user clicks a chord). updateChord() takes in the updated and old string values. 
     * We also take in identifying information such as the unique measureId (as stored in database), measureLocation (relative
     * to the composition), and chordLocation (relative to the measureLocation).
     */
    @RequestMapping(value = "/updateChord", method = RequestMethod.POST)
    public ResponseEntity<String> updateChord(@RequestParam("updated_low_e_string") int updated_low_e_string,
                                            @RequestParam("updated_a_string") int updated_a_string,
                                            @RequestParam("updated_d_string") int updated_d_string, 
                                            @RequestParam("updated_g_string") int updated_g_string,
                                            @RequestParam("updated_b_string") int updated_b_string,
                                            @RequestParam("updated_high_e_string") int updated_high_e_string,
                                            @RequestParam("measureId") int measureId, // id of the measure in database
                                            @RequestParam("measure") int measureLocation, // the location of the measure in the composition (0 = first measure in composition, 1 = second measure in composition, etc.)
                                            @RequestParam("chordLocation") int chordLocation, // the location of the chord in the measure (0 = first chord in measure, 1 = second chord in measure, etc.)
                                            @RequestParam("newDuration") int newDuration,
                                            @RequestParam("original_low_e_string") int original_low_e_string,
                                            @RequestParam("original_a_string") int original_a_string,
                                            @RequestParam("original_d_string") int original_d_string,
                                            @RequestParam("original_g_string") int original_g_string,
                                            @RequestParam("original_b_string") int original_b_string,
                                            @RequestParam("original_high_e_string") int original_high_e_string) {
        final int UNCHANGED = -1;
        // System.out.println("changed string values=" + updated_low_e_string + " " + updated_a_string + " " + updated_d_string + " " + updated_g_string + " " + updated_b_string + " " + updated_high_e_string);
        // System.out.println("original string values=" + original_low_e_string + " " + original_a_string + " " + original_d_string + " " + original_g_string + " " + original_b_string + " " + original_high_e_string);
        // System.out.println("measure=" + measure + ", chord=" + chord + ", duration=" + duration);
        System.out.println("*****measureLocation=" + measureLocation + ", measureId=" + measureId);
        System.out.println("*****chord=" + chordLocation);
        Measure measures = cs.formatComposition(HomeController.globalCompositionId);
        // navigate to the chord in the composition
        for (int i = 0; i < measureLocation; i++) {
            measures = measures.getNext();
        }
        Chord chords = measures.getChord();
        for (int i = 0; i < chordLocation; i++) {
            chords = chords.getNext();
        }
        
        // duration must be accessed via a note
        int dur = chords.getNote().getDuration();
        if (newDuration != 16 && newDuration != 8 && newDuration != 4 && newDuration != 2 && newDuration != 1) {
            newDuration = dur; // default to current duration
        }

        System.out.println("TEST======="+chords.getNote().getDuration());
        System.out.println("new dur=" + newDuration);
        boolean durUpdate = false;
        // change duration, if it was changed
        int oldDur = dur;
        if (newDuration != dur && newDuration != UNCHANGED) {
            durUpdate = true;
            dur = newDuration;
        }
        /* Call it here, modify the values, and then update in the html */
        int val = updated_high_e_string;
        if (updated_high_e_string == UNCHANGED)
            val = original_high_e_string;
        Note high_e = new Note(val, 0, dur, false);
        val = updated_b_string;
        if (updated_b_string == UNCHANGED)
            val = original_b_string;
        Note b = new Note(val, 1, dur, false);
        val = updated_g_string;
        if (updated_g_string == UNCHANGED) {
            val = original_g_string;
        }
        Note g = new Note(val, 2, dur, false);
        val = updated_d_string;
        if (updated_d_string == UNCHANGED) {
            val = original_d_string;
        }
        Note d = new Note(val, 3, dur, false);
        val = updated_a_string;
        if (updated_a_string == UNCHANGED) {
            val = original_a_string;
        }
        Note a = new Note(val, 4, dur, false);
        val = updated_low_e_string;
        if (updated_low_e_string == UNCHANGED) {
            val = original_low_e_string;
        }
        Note low_e = new Note(val, 5, dur, false);

        high_e.next = b;
        b.next = g;
        g.next = d;
        d.next = a;
        a.next = low_e;
        System.out.println("==============CONTROLLER PRINTING NOTES E to e==============");
        System.out.println(low_e.getFretNumber() + ", " + a.getFretNumber() + ", " + d.getFretNumber() + ", " + g.getFretNumber() + ", " + b.getFretNumber() + ", " + high_e.getFretNumber());
        System.out.println("==============CONTROLLER DONE PRINTING NOTES E to e==============");

        Chord updatedChord = new Chord(high_e);
        System.out.println("durUpdate="+durUpdate);

        if (durUpdate) {
            System.out.println("newDuration="+newDuration+", oldDur="+oldDur);
            chs.updateDurations(newDuration, oldDur, updatedChord, measureId, chordLocation, HomeController.globalCompositionId);
        } else {
            // was above if previously, moved here to avoid possible bugs
            chr.updateChord(updatedChord, measureId, chordLocation);
        }
        return new ResponseEntity<>("Chord updated", HttpStatus.OK);
    }

}
