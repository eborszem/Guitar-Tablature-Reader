package com.noteproject.demo.Controller;

import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Controller;

import com.noteproject.demo.Entity.User;
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
    @Autowired
    UserRepository ur;
    @Autowired
    JwtService jwtService;

    @GetMapping("/")
    public String getHomePage(Model model, @CookieValue("jwt") String token) {
        Composition compInfo;
        boolean initialCompositionExists = true;
        String username = jwtService.extractUsername(token);
        Optional<User> user = ur.findByUsername(username);
        if (user.isEmpty()) {
            throw new IllegalStateException("User not found");
        }
        Long userId = user.get().getId();
        try {
            compInfo = cr.getCompositionById(globalCompositionId);
        } catch  (EmptyResultDataAccessException e) { // compositions table is empty, so make an initial composition
            System.out.println("NO COMPOSITIONS EXIST: creating a new composition");
            globalCompositionId = cs.addNewComposition("initial composition", "new user", userId);
            compInfo = cr.getCompositionById(globalCompositionId);
            initialCompositionExists = false;
        }

        List<Measure> measures = mr.findMeasuresByCompositionId(globalCompositionId);
        for (Measure measure : measures) {
            System.out.println("measure id: " + measure.getId() + " measure number: " + measure.getMeasureNumber());
        }
        for (Measure measure : measures) {
            measure.setChords(chr.findChordsByCompositionIdAndMeasureId(compInfo.getId(), measure.getId()));
        }
        model.addAttribute("allMeasures", measures);
        model.addAttribute("allCompositions", cr.getAllCompositions(userId));

        // reload to give program time to generate an initial composition
        if (!initialCompositionExists) {
            return "redirect:/page";
        }
        
        model.addAttribute("compositionInfo", compInfo);

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
