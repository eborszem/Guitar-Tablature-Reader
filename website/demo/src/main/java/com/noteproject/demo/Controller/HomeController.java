package com.noteproject.demo.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Controller;
import com.noteproject.demo.Model.*;
import com.noteproject.demo.Repository.*;
import com.noteproject.demo.Service.*;

import org.springframework.ui.Model;

@Controller
public class HomeController {
    public static int globalCompositionId = 26; // composition 1 is chosen by default
    @Autowired
    CompositionRepository cr;
    @Autowired
    CompositionService cs;
    @Autowired
    MeasureRepository mr;
    @Autowired
    ChordRepository chr;

    @GetMapping("/")
    public String getHomePage(Model model) {
        model.addAttribute("allMeasures", mr.getMeasures(globalCompositionId));
        model.addAttribute("allCompositions", cr.getAllCompositions());
        Composition compInfo;
        boolean initialCompositionExists = true;
        try {
            compInfo = cr.getCompositionInfo(globalCompositionId);
        } catch  (EmptyResultDataAccessException e) { // composotions table is empty, so make an initial composition
            System.out.println("NO COMPOSITIONS EXIST: creating a new composition");
            globalCompositionId = cs.addNewComposition("initial composition", "new user");
            compInfo = cr.getCompositionInfo(globalCompositionId);
            initialCompositionExists = false;
        }

        // reload to give program time to generate an initial composition
        if (!initialCompositionExists) {
            return "redirect:/page";
        }
        
        model.addAttribute("compositionInfo", compInfo);

        // print for testing
        // List<Chord> chords = chr.findChordsByCompositionId(1);
        // for (Chord c : chords) {
        //     Note highE = c.getNote();
        //     Note b = highE.next;
        //     Note g = b.next;
        //     Note d = g.next;
        //     Note a = d.next;
        //     Note lowE = a.next;
        //     System.out.printf("E=%d, a=%d, d=%d, g=%d, b=%d, e=%d\n", lowE.getFretNumber(), a.getFretNumber(), d.getFretNumber(), g.getFretNumber(), b.getFretNumber(), highE.getFretNumber());
        //     System.out.println("duration=" + highE.getDuration()); // duration is accessed through notes
        //     System.out.println("id=" + c.getId() + ", measure id=" + c.getMeasureId());
        // }
        
        final String[][] FRETBOARD = {
            {"E", "F", "F#", "G", "G#", "A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A"},
            {"B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B", "C", "C#", "D", "D#", "E"},
            {"G", "G#", "A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B", "C"},
            {"D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G"},
            {"A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B", "C", "C#", "D"},
            {"E", "F", "F#", "G", "G#", "A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A"},
        };
        model.addAttribute("fretboard", FRETBOARD);
        return "homepage";
    }

}
