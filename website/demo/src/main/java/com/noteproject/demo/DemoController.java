package com.noteproject.demo;

import java.util.Random;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import com.noteproject.demo.Model.Chord;
import com.noteproject.demo.Model.Composition;
import com.noteproject.demo.Model.Measure;
import com.noteproject.demo.Model.Note;
import com.noteproject.demo.Repositories.CompositionRepository;

import org.springframework.ui.Model;

@Controller
public class DemoController {

    public static int globalCompositionId = 2; // composition 1 is chosen by default
    @Autowired
    CompositionRepository cr;

    private final FileService fileService;
    public DemoController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping("/page")
    public List<Measure> test(Model model) {
        if (globalCompositionId == 1) {
            
        }
        //cr.insertRecord(++index);
        /*Composition dummy = new Composition();
        Composition c = dummy.readComposition("/a.txt");
        System.out.println("start of getmapping");
        Measure m = c.getMeasure();
        model.addAttribute("measure", m);*/
        String compositionString = fileService.readFile("composition.txt");
        //System.out.println("composition as a string="+compositionString);
        Composition composition = new Composition().readComposition(compositionString);
        System.out.println("START COMPOSITION PRINT");
        //composition.printComposition(composition);
        System.out.println("END COMPOSITION PRINT");
        model.addAttribute("allMeasures", cr.getMeasures(globalCompositionId));
        model.addAttribute("allCompositions", cr.getAllCompositions());
        Composition x = cr.getCompositionInfo(globalCompositionId);
        model.addAttribute("compositionInfo", x);
        //System.out.println("1st measure object="+composition.getMeasure());
        List<Chord> chords = cr.findChordsByCompositionId(1);
        for (Chord c : chords) {
            Note highE = c.getNote();
            Note b = highE.next;
            Note g = b.next;
            Note d = g.next;
            Note a = d.next;
            Note lowE = a.next;
            System.out.printf("E=%d, a=%d, d=%d, g=%d, b=%d, e=%d\n", lowE.getFretNumber(), a.getFretNumber(), d.getFretNumber(), g.getFretNumber(), b.getFretNumber(), highE.getFretNumber());
            System.out.println("duration=" + highE.getDuration()); // duration is accessed through notes
            System.out.println("id=" + c.getId() + ", measure id=" + c.getMeasureId());
        }
        
        // Measure obj = cr.getTimeSignature(globalCompositionId);
        // model.addAttribute("timeSig", obj);

        final String[][] FRETBOARD = {
            {"E", "F", "F#", "G", "G#", "A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A"},
            {"B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B", "C", "C#", "D", "D#", "E"},
            {"G", "G#", "A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B", "C"},
            {"D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G"},
            {"A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B", "C", "C#", "D"},
            {"E", "F", "F#", "G", "G#", "A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A"},
        };
        model.addAttribute("fretboard", FRETBOARD);
        
        //model.addAttribute("measure", cr.getFirstMeasure());
        //Composition temp = new Composition("C", 4, 4, cr.formatComposition());
        //temp.printComposition(temp);
        // List<Measure> list = cr.getMeasures();
        // System.out.println("-----TEST-----");
        // System.out.println(list.get(2).getNext().getNext().getChord().getNext().getNote().getFretNumber());
        // System.out.println(list.get(2).getNext().getNext().getChord().getNext().getNote().getNext().getFretNumber());
        // System.out.println(list.get(2).getNext().getNext().getChord().getNext().getNote().getNext().getNext().getFretNumber());
        // System.out.println(list.get(2).getNext().getNext().getChord().getNext().getNote().getNext().getNext().getNext().getFretNumber());
        // System.out.println(list.get(2).getNext().getNext().getChord().getNext().getNote().getNext().getNext().getNext().getNext().getFretNumber());
        // System.out.println(list.get(2).getNext().getNext().getChord().getNext().getNote().getNext().getNext().getNext().getNext().getNext().getFretNumber());
        // System.out.println("-----TEST END-----");
        return cr.getMeasures(globalCompositionId);
    }


    @PostMapping("/createNewMeasure")
    @ResponseBody
    public List<Map<String, Object>> createNewMeasure(Model model) {
        System.out.println("reached create new measure post mapping");
        //fileService.writeToFile("composition.txt");
        //public Note(int fretNumber, int stringNumber, int duration, boolean rest) {
        // we want to make a blank measure, with just one rest
        Note wholeRest = new Note(-1, 0, 4, true);
        Note wholeRest2 = new Note(-1, 1, 4, true);
        Note wholeRest3 = new Note(-1, 2, 4, true);
        Note wholeRest4 = new Note(-1, 3, 4, true);
        Note wholeRest5 = new Note(-1, 4, 4, true);
        Note wholeRest6 = new Note(-1, 5, 4, true);
        wholeRest.next = wholeRest2;
        wholeRest2.next = wholeRest3;
        wholeRest3.next = wholeRest4;
        wholeRest4.next = wholeRest5;
        wholeRest5.next = wholeRest6;
        
        Chord c = new Chord(wholeRest);
        Measure m = new Measure(c);
        
        int measureId = cr.addMeasureToRepo(m, globalCompositionId);

        List<Map<String, Object>> res = new ArrayList<>();
        Map<String, Object> chord = new HashMap<>();
        chord.put("fretNumbers", List.of(-1, -1, -1, -1, -1, -1)); // default rests
        chord.put("duration", "4"); // TODO: Whole note value currently
        chord.put("measureId", measureId);
        res.add(chord);

        return res;
    }

    @RequestMapping(value = "/deleteMeasure", method = RequestMethod.POST)
    public ResponseEntity<String> deleteMeasure(@RequestParam("measureId") int measureId) {
        cr.deleteMeasure(measureId);
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @RequestMapping(value = "/addMeasure", method = RequestMethod.POST)
    public ResponseEntity<String> addMeasure(@RequestParam("measureId") int measureId) {
        cr.addMeasure(measureId, globalCompositionId);
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
        // System.out.println("changed string values=" + updated_low_e_string + " " + updated_a_string + " " + updated_d_string + " " + updated_g_string + " " + updated_b_string + " " + updated_high_e_string);
        // System.out.println("original string values=" + original_low_e_string + " " + original_a_string + " " + original_d_string + " " + original_g_string + " " + original_b_string + " " + original_high_e_string);
        // System.out.println("measure=" + measure + ", chord=" + chord + ", duration=" + duration);
        System.out.println("*****measureLocation=" + measureLocation + ", measureId=" + measureId);
        System.out.println("*****chord=" + chordLocation);
        Measure measures = cr.formatComposition(globalCompositionId);
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
        if (newDuration != dur) {
            durUpdate = true;
            dur = newDuration;
        }
        final int UNCHANGED = -1;
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
            cr.updateDurations(newDuration, oldDur, updatedChord, measureId, chordLocation, globalCompositionId);
        } else {
            // was above if previously, moved here to avoid possible bugs
            cr.updateChord(updatedChord, measureId, chordLocation);
        }
        return new ResponseEntity<>("Chord updated", HttpStatus.OK);
    }

    @PostMapping("/changeComposition")
    @ResponseBody
    public void changeComposition(@RequestParam("selectedComposition") String composition) {
        System.out.println(composition + "!!!!!...");
        globalCompositionId = Integer.parseInt(composition);
    }

    @PostMapping("/newComposition")
    @ResponseBody
    public void newComposition(@RequestParam("title") String title, @RequestParam("composer") String composer) {
        System.out.println("NEW COMP BEING ADDED");
        globalCompositionId = cr.addNewComposition(title, composer); // adds new comp and measure to tables
    }
}
