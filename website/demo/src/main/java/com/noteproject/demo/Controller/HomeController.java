package com.noteproject.demo.Controller;

import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.noteproject.demo.Entity.User;
import com.noteproject.demo.Model.Composition;
import com.noteproject.demo.Service.CompositionService;
import com.noteproject.demo.Service.JwtService;
import com.noteproject.demo.Repository.UserRepository;


import org.springframework.ui.Model;

@Controller
public class HomeController {
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
    public String getHomePage(Model model, @CookieValue(value = "jwt", required = false) String token) {
        List<Composition> allCompositions = cs.getAllCompositions();
        model.addAttribute("noCompositions", allCompositions.isEmpty());
        model.addAttribute("allCompositions", allCompositions);
        
        if (token != null && !token.isEmpty()) {
            String username = jwtService.extractUsername(token);
            Optional<User> user = ur.findByUsername(username);
            if (user.isPresent()) {
                Long userId = user.get().getId();
                model.addAttribute("userCompositions", cs.getUserCompositions(userId));
                model.addAttribute("loggedIn", true);
            } else {
                model.addAttribute("loggedIn", false);
            }
        } else {
            model.addAttribute("loggedIn", false);
        }
        return "homepage";
    }

    @GetMapping("/song")
    public String getSongPage(Model model, @CookieValue(value = "jwt", required = false) String token, @RequestParam(name = "compositionId") Integer compositionId) {
        Composition comp = cs.getCompositionById(compositionId);
        model.addAttribute("composition", comp);
        model.addAttribute("fretboard", FRETBOARD);
        if (token != null && !token.isEmpty()) {
            String username = jwtService.extractUsername(token);
            Optional<User> user = ur.findByUsername(username);
            if (user.isPresent()) {
                User u = user.get();
                model.addAttribute("loggedIn", true);
                model.addAttribute("isOwner", cs.isOwner(u.getId(), compositionId));
            } else {
                model.addAttribute("loggedIn", false);
            }
        } else {
            model.addAttribute("loggedIn", false);
            model.addAttribute("isOwner", false);
        }
        return "songpage";
    }

}