package com.noteproject.demo.Controller;

import org.springframework.web.bind.annotation.RestController;

import com.noteproject.demo.Model.Composition;
import com.noteproject.demo.Service.CompositionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class AudioController {
    @Autowired
    CompositionService cs;

    /**
     * Sends the current composition as a JSON response to the /play endpoint
     * JSON response is then used by the frontend to play the composition using Soundfont
     */
    @GetMapping("/play")
    public ResponseEntity<Composition> play(@RequestParam(name = "compositionId") int compositionId) {
        Composition comp = cs.getCompositionById(compositionId);
        return ResponseEntity.ok(comp);
    }
}
