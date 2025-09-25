package com.noteproject.demo.Controller;

import org.springframework.stereotype.Controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.noteproject.demo.Entity.User;
import com.noteproject.demo.Model.Composition;
import com.noteproject.demo.Repository.UserRepository;
import com.noteproject.demo.Service.CompositionService;
import com.noteproject.demo.Service.JwtService;

@RequestMapping("/composition")
@Controller
public class CompositionController {
    @Autowired
    CompositionService cs;

    @Autowired
    UserRepository ur;

    @Autowired
    JwtService jwtService;

    @PostMapping("/change")
    @ResponseBody
    public ResponseEntity<Composition> changeComposition (
        @RequestBody Map<String, String> payload, 
        @RequestHeader("Authorization") String authHeader,
        @RequestParam(name = "compositionId") Integer compositionId
    ) {
        // int compositionId = Integer.valueOf(payload.get("selectedId"));
        String token = authHeader.substring(7); // remove "Bearer "
        String username = jwtService.extractUsername(token);
        Optional<User> user = ur.findByUsername(username);
        if (user.isEmpty()) {
            throw new IllegalStateException("User not found");
        }

        Composition comp = cs.getCompositionById(compositionId);
        return ResponseEntity.ok(comp);
    }

    @PostMapping("/new")
    @ResponseBody
    public ResponseEntity<Map<String, Integer>> newComposition(@RequestBody Map<String, String> payload, @RequestHeader("Authorization") String authHeader) {
        String title = payload.get("title");
        String composer = payload.get("composer");
        System.out.println("NEW COMP BEING ADDED");
        System.out.println("AUTH HEADER="+authHeader);
        String token = authHeader.substring(7); // remove "Bearer "
        System.out.println("TOKEN="+token);
        String username = jwtService.extractUsername(token);
        Optional<User> user = ur.findByUsername(username);
        if (user.isEmpty()) {
            throw new IllegalStateException("User not found");
        }
        Long userId = user.get().getId();
        int compositionId = cs.addNewComposition(title, composer, userId); // adds new comp and measure to tables
        return ResponseEntity.ok(Map.of("compositionId", compositionId));
    }
}
