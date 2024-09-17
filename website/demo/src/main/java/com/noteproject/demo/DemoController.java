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

    static int globalCompositionId = 1; // composition 1 by default
    @Autowired
    CompositionRepository cr;

    private final FileService fileService;
    public DemoController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping("/page")
    public List<Measure> test(Model model) {
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
        Measure obj = cr.getTimeSignature(globalCompositionId);
        model.addAttribute("timeSig", obj);
        
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
        
        cr.addMeasureToRepo(m);

        List<Map<String, Object>> res = new ArrayList<>();
        Map<String, Object> chord = new HashMap<>();
        chord.put("fretNumbers", List.of(-1, -1, -1, -1, -1, -1)); // default rests
        chord.put("duration", "4");
        chord.put("measureId", cr.getMeasureId());
        res.add(chord);

        return res;
    }

    @RequestMapping(value = "/updateChord", method = RequestMethod.POST)
    public ResponseEntity<String> updateChord(@RequestParam("low_e_string") int low_e_string,
                                            @RequestParam("a_string") int a_string,
                                            @RequestParam("d_string") int d_string, 
                                            @RequestParam("g_string") int g_string,
                                            @RequestParam("b_string") int b_string,
                                            @RequestParam("high_e_string") int high_e_string,
                                            @RequestParam("measureId") int measureId,
                                            @RequestParam("measure") int measure,
                                            @RequestParam("chord") int chord,
                                            @RequestParam("chordNum") int chordNum,
                                            @RequestParam("duration") int duration,
                                            @RequestParam("newDuration") int newDuration,
                                            @RequestParam("newType") int newType,
                                            @RequestParam("original_l_e") int original_l_e,
                                            @RequestParam("original_a") int original_a,
                                            @RequestParam("original_d") int original_d,
                                            @RequestParam("original_g") int original_g,
                                            @RequestParam("original_b") int original_b,
                                            @RequestParam("original_h_e") int original_h_e) {
        System.out.println("changed string values=" + low_e_string + " " + a_string + " " + d_string + " " + g_string + " " + b_string + " " + high_e_string);
        System.out.println("original string values=" + original_l_e + " " + original_a + " " + original_d + " " + original_g + " " + original_b + " " + original_h_e);
        System.out.println("measure=" + measure + ", chord=" + chord + ", duration=" + duration);
        Measure measures = cr.formatComposition(globalCompositionId);
        // navigate to the chord in the composition
        for (int i = 0; i < measure; i++) {
            measures = measures.getNext();
        }
        Chord chords = measures.getChord();
        for (int i = 0; i < chord; i++) {
            chords = chords.getNext();
        }
        
        // duration must be accessed via a note
        int dur = chords.getNote().getDuration();
        if (newDuration != 16 && newDuration != 8 && newDuration != 4 && newDuration != 2 && newDuration != 1) {
            newDuration = dur; // default to current duration
        }
        // 0 = chord/note, 1 = rest
        if (newType != 0 && newType != 1) {
            newType = 0; // default to chord/note
        }
        System.out.println("TEST======="+chords.getNote().getDuration());
        System.out.println("new dur=" + newDuration);
        System.out.println("new type=" + newType);
        boolean durUpdate = false;
        // change duration, if it was changed
        if (newDuration != dur) {
            durUpdate = true;
            dur = newDuration;
        }
        /* Call it here, modify the values, and then update in the html */
        int val = high_e_string;
        if (high_e_string == -1)
            val = original_h_e;
        Note high_e = new Note(val, 0, dur, false);
        val = b_string;
        if (b_string == -1)
            val = original_b;
        Note b = new Note(val, 1, dur, false);
        val = g_string;
        if (g_string == -1) {
            val = original_g;
        }
        Note g = new Note(val, 2, dur, false);
        val = d_string;
        if (d_string == -1) {
            val = original_d;
        }
        Note d = new Note(val, 3, dur, false);
        val = a_string;
        if (a_string == -1) {
            val = original_a;
        }
        Note a = new Note(val, 4, dur, false);
        val = low_e_string;
        if (low_e_string == -1) {
            val = original_l_e;
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
        System.out.println("MEASURE ID ====" + measureId);
        System.out.println("CHORD NUM ====" + chordNum);

        cr.updateChord(updatedChord, measureId, chordNum);
        if (durUpdate) {
            cr.updateDurations(newDuration, dur, updatedChord, measureId, chordNum, globalCompositionId);
        }
        return new ResponseEntity<>("Chord updated", HttpStatus.OK);
    }

    @PostMapping("/changeComposition")
    @ResponseBody
    public void changeComposition(@RequestParam("selectedComposition") String composition) {
        System.out.println(composition + "!!!!!...");
        globalCompositionId = Integer.parseInt(composition);
    }
}
