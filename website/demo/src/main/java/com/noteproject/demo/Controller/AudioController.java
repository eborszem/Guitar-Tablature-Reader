package com.noteproject.demo.Controller;

import org.springframework.web.bind.annotation.RestController;

import com.noteproject.demo.Model.Measure;
import com.noteproject.demo.Model.Chord;
import com.noteproject.demo.Model.Composition;
import com.noteproject.demo.Model.Note;
import com.noteproject.demo.Repository.CompositionRepository;
import com.noteproject.demo.Service.CompositionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;

@RestController
public class AudioController {
    @Autowired
    CompositionRepository cr;
    @Autowired
    CompositionService cs;

    /*
     * Sends all of the composition's chords (comprised of notes) and their durations to be played by the javascript synth.
     * Returns an arraylist of chords. Chords are an arraylist of notes. Notes are stored as an integer
     * array containing the fretNumber, stringNumber, and duration.
     */
    @GetMapping("/play")
    public ArrayList<ArrayList<Note>> play(Model model) {
        Composition comp = cs.formatComposition(HomeController.globalCompositionId);
        ArrayList<ArrayList<Note>> allChords = new ArrayList<>();
        for (Measure m : comp.getMeasures()) {
            List<Chord> chords = m.getChords();
            for (Chord chord : chords) {
                List<Note> notes = chord.getNotes();
                ArrayList<Note> notesInChord = new ArrayList<>(); // one note per string
                for (Note note : notes) {
                    // read in fret number and string number to turn them into specific note
                    // read in duration to play for certain period of time
                    notesInChord.add(new Note(note.getFretNumber(), note.getStringNumber(), note.getDuration()));
                }
                allChords.add(notesInChord);
            }
        }
        System.out.println("chords arr=" + allChords);
        return allChords;
    }
}
