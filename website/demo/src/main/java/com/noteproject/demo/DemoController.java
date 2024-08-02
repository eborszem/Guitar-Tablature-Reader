package com.noteproject.demo;

import java.util.Random;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.stereotype.Controller;

import com.noteproject.demo.Model.Chord;
import com.noteproject.demo.Model.Composition;
import com.noteproject.demo.Model.Measure;
import com.noteproject.demo.Model.Note;

import org.springframework.ui.Model;

@Controller
public class DemoController {
    private final FileService fileService;
    public DemoController(FileService fileService) {
        this.fileService = fileService;
    }
    // temporary variable
    @GetMapping("/test2")
    public String randomComposition(Model model) {
        Note note = new Note(0, 0, false);
        Chord chord = new Chord(note);
        Measure m = new Measure(null);
        Measure mm = m;
        // creates 4 measures with random chords for the composition
        for (int i = 0; i < 4; i++) {
            Chord cc = chord;
            // creates 4 random chords with 1 beat each
            for (int j = 1; j < 16; j += 4) {
                Random rand = new Random();
                int k = rand.nextInt(8) + 1;
                if (k == -1 || k == 0)
                    k = 1;
                //int k = j;
                // the three while loops keep us within valid musical intervals 1 through 7 when creating a new note
                while (k > 7) {
                    k -= 7;
                }
                Note root = new Note(k, 1, false);
                while ((k + 2) > 7) {
                    k -= 7;
                }
                Note third = new Note(k + 2, 1, false);
                while ((k + 4) > 7) {
                    k -= 7;
                }
                Note fifth = new Note(k + 4, 1, false);

                //System.out.println(root.interval + "," + third.interval + "," + fifth.interval);
                Note dummy = root;
                root.next = third;
                third.next = fifth;
                //should be equal to above
                //System.out.println(dummy.interval + "," + dummy.next.interval + "," + dummy.next.next.interval);
                Chord c = new Chord(dummy);
                chord.next = c;
                chord = chord.next;
                
            }
            // add new list of chords (which together comprise the measure) to linked list of measures
            Measure measure = new Measure(cc.next);
            m.next = measure;
            m = m.next;
            chord = new Chord(note);
        }


        //System.out.println(mm.next.chord.note.interval);
        //Measure m = new Measure(tmp);
        //m.printMeasure(m);
        
        Composition c = new Composition("C", 1, 4, mm);
        String s = c.printComposition(c);
        model.addAttribute("composition", s);
        return "test";
    }

    @GetMapping("/page")
    public String test(Model model) {
        /*Composition dummy = new Composition();
        Composition c = dummy.readComposition("/a.txt");
        System.out.println("start of getmapping");
        Measure m = c.getMeasure();
        model.addAttribute("measure", m);*/
        String compositionString = fileService.readFile("composition.txt");
        System.out.println("composition as a string="+compositionString);
        Composition comp = new Composition();
        Composition composition = comp.readComposition(compositionString);
        System.out.println("START COMPOSITION PRINT");
        //composition.printComposition(composition);
        System.out.println("END COMPOSITION PRINT");
        model.addAttribute("measure", composition.getMeasure());
        System.out.println("1st measure object="+composition.getMeasure());
        return "page";
    }


    @PostMapping("/createNewMeasure")
    public String createNewMeasure(Model model) {
        System.out.println("reached create new measure post mapping");
        fileService.writeToFile("composition.txt");
        return "redirect:/page";
    }
}
