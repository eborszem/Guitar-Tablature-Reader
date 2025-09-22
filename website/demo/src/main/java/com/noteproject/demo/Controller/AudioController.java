package com.noteproject.demo.Controller;

import org.springframework.web.bind.annotation.RestController;

import com.noteproject.demo.Model.Composition;
import com.noteproject.demo.Service.CompositionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class AudioController {
    @Autowired
    CompositionService cs;

    /**
     * Sends the current composition as a JSON response to the /play endpoint
     * JSON response is then used by the frontend to play the composition using Soundfont
     */
    @GetMapping("/play")
    public ResponseEntity<Composition> play(Model model) {
        Composition comp = cs.getCompositionById(HomeController.globalCompositionId); // TODO: REPLACE WITH PATH VARIABLE LATER
        return ResponseEntity.ok(comp); // replace globalCompositionId with param later?
    }
}
