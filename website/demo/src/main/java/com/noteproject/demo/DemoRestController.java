package com.noteproject.demo;

import org.springframework.web.bind.annotation.RestController;

import com.noteproject.demo.Model.Composition;
import com.noteproject.demo.Model.Measure;
import com.noteproject.demo.Model.Chord;
import com.noteproject.demo.Model.Note;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
public class DemoRestController {
    private final FileService fileService;
    public DemoRestController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping("/play")
    public ArrayList<ArrayList<int[]>> play(Model model) {
        String compositionString = fileService.readFile("composition.txt");
        Composition composition = new Composition().readComposition(compositionString);
        Measure measure = composition.getMeasure();
        Measure m = measure;
        ArrayList<ArrayList<int[]>> allChords = new ArrayList<>();
        while (m != null) {
            Chord chord = m.getChord();
            while (chord != null) {
                Note note = chord.getNote();
                ArrayList<int[]> notesInChord = new ArrayList<>(); // one note per string
                while (note != null) {
                    // read in fret number and string number to turn them into specific note
                    // read in duration to play for certain period of time
                    notesInChord.add(new int[]{note.getFretNumber(), note.getStringNumber(), note.getDuration()});
                    note = note.next;
                }
                allChords.add(notesInChord);
                chord = chord.getNext();
            }
            m = m.next;
        }
        System.out.println(allChords.toString());
        return allChords;
    }
    
    @GetMapping("/editChord")
    public void changeChord(@RequestParam String param) {
        
    }
    
    
}
