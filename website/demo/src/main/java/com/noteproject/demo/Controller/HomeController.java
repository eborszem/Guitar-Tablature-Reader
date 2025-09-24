package com.noteproject.demo.Controller;

import java.util.Optional;

import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Controller;

import com.noteproject.demo.Entity.User;
import com.noteproject.demo.Model.Composition;
import com.noteproject.demo.Service.CompositionService;
import com.noteproject.demo.Service.JwtService;
import com.noteproject.demo.Repository.UserRepository;


import org.springframework.ui.Model;

@Controller
public class HomeController {
    public static int globalCompositionId = 26; // composition 1 is chosen by default
    @Autowired
    CompositionService cs;
    @Autowired
    UserRepository ur;
    @Autowired
    JwtService jwtService;

    final String[][] FRETBOARD = {
        {"E", "F", "F#", "G", "G#", "A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A"},
        {"B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B", "C", "C#", "D", "D#", "E"},
        {"G", "G#", "A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B", "C"},
        {"D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G"},
        {"A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B", "C", "C#", "D"},
        {"E", "F", "F#", "G", "G#", "A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A"},
    };

    @GetMapping("/")
    public String getHomePage(Model model, @CookieValue("jwt") String token) {
        Composition comp;
        boolean initialCompositionExists = true;
        String username = jwtService.extractUsername(token);
        Optional<User> user = ur.findByUsername(username);
        if (user.isEmpty()) {
            throw new IllegalStateException("User not found");
        }
        Long userId = user.get().getId();
        try {
            comp = cs.getCompositionById(globalCompositionId);
        } catch  (EmptyResultDataAccessException e) { // compositions table is empty, so make an initial composition
            System.out.println("NO COMPOSITIONS EXIST: creating a new composition");
            globalCompositionId = cs.addNewComposition("initial composition", "new user", userId);
            comp = cs.getCompositionById(globalCompositionId);
            initialCompositionExists = false;
        }
        
        // reload to give program time to generate an initial composition
        if (!initialCompositionExists) {
            return "redirect:/";
        }
        
        model.addAttribute("composition", comp);
        model.addAttribute("allCompositions", cs.getAllCompositions(userId));

        model.addAttribute("fretboard", FRETBOARD);
        return "homepage"; // todo: CHANGE TO JSON?
    }

}