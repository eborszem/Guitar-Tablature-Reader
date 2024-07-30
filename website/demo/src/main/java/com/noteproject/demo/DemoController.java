package com.noteproject.demo;

import java.util.Random;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;

import com.noteproject.demo.Model.Chord;
import com.noteproject.demo.Model.Composition;
import com.noteproject.demo.Model.Measure;
import com.noteproject.demo.Model.Note;

import org.springframework.ui.Model;

@Controller
public class DemoController {
    //@RequestMapping("/hello")
    @GetMapping("/test")
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
        String s = c.printCompositon(c);
        model.addAttribute("composition", s);
        return "test";
    }

    @GetMapping("/page")
    public String test2(Model model) {
        System.out.println("start of getmapping");
        Note measure1chord1note1 = new Note(1, 1, false);
        Note measure1chord1note2 = new Note(3, 1, false);
        Note measure1chord1note3 = new Note(5, 1, false);
        measure1chord1note1.next = measure1chord1note2;
        measure1chord1note2.next = measure1chord1note3;
        Chord measure1chord1 = new Chord(measure1chord1note1);
        
        // measure 1 chord 2
        Note measure1chord2note1 = new Note(5, 3, false);
        Note measure1chord2note2 = new Note(7, 3, false);
        Note measure1chord2note3 = new Note(2, 3, false);
        measure1chord2note1.next = measure1chord2note2;
        measure1chord2note2.next = measure1chord2note3;
        Chord measure1chord2 = new Chord(measure1chord2note1);

        // measure 1 chord 3
        Note measure1chord3note1 = new Note(5, 3, false);
        Note measure1chord3note2 = new Note(7, 3, false);
        Note measure1chord3note3 = new Note(2, 3, false);
        measure1chord3note1.next = measure1chord3note2;
        measure1chord3note2.next = measure1chord3note3;
        Chord measure1chord3 = new Chord(measure1chord3note1);


        // measure 1 chord 4
        Note measure1chord4note1 = new Note(5, 3, false);
        Note measure1chord4note2 = new Note(7, 3, false);
        Note measure1chord4note3 = new Note(2, 3, false);
        measure1chord4note1.next = measure1chord4note2;
        measure1chord4note2.next = measure1chord4note3;
        Chord measure1chord4 = new Chord(measure1chord4note1);
        
        
        // measure 2 chord 1
        Note measure2chord1note1 = new Note(2, 2, false);
        Note measure2chord1note2 = new Note(4, 2, false);
        Note measure2chord1note3 = new Note(6, 2, false);
        measure2chord1note1.next = measure2chord1note2;
        measure2chord1note2.next = measure2chord1note3;
        Chord measure2chord1 = new Chord(measure2chord1note1);
       
        
        // measure 2 chord 2
        Note measure2chord2note1 = new Note(4, 2, false);
        Note measure2chord2note2 = new Note(6, 2, false);
        Note measure2chord2note3 = new Note(1, 2, false);
        measure2chord2note1.next = measure2chord2note2;
        measure2chord2note2.next = measure2chord2note3;
        Chord measure2chord2 = new Chord(measure2chord2note1);
        

        // measure 3 chord 1
        Note measure3chord1note1 = new Note(1, 4, false);
        Note measure3chord1note2 = new Note(3, 4, false);
        Note measure3chord1note3 = new Note(5, 4, false);
        measure3chord1note1.next = measure3chord1note2;
        measure3chord1note2.next = measure3chord1note3;
        Chord measure3chord1 = new Chord(measure3chord1note1);

        // Now linking the chords into measures.
        // The first chord of a measure points to all other chords in that measure
        measure1chord1.next = measure1chord2;
        measure1chord2.next = measure1chord3;
        measure1chord3.next = measure1chord4;
        Measure measure1 = new Measure(measure1chord1);

        
        measure2chord1.next = measure2chord2;
        Measure measure2 = new Measure(measure2chord1);

        Measure measure3 = new Measure(measure3chord1);


        
        measure1.next = measure2;
        measure2.next = measure3;
        
        //System.out.println(m + "...." + m.getAllChords() + "....");

        /*System.out.println("HELLO");
        System.out.println(m.getChord().getNote().getInterval());
        System.out.println(m.getChord().getNote().getNext().getInterval());
        System.out.println("BYE");*/
        Composition composition = new Composition("C", 4, 4, measure1);

        Composition composition2 = composition;

        // Prints out composition for testing
        System.out.println("ENTERING");
        for (Measure mm : composition2.getAllMeasures()) {
            for (Chord cc : mm.getAllChords()) {
                for (Note nn : cc.getAllNotes()) {
                    System.out.print(nn.getInterval());
                }
                System.out.println();
            }
            System.out.println("------------");
        }
        model.addAttribute("measure", measure1);
        System.out.println("end of getmapping");
        return "page";
    }

    /*@PostMapping("/posttest")
    public String createMeasure(Model model) {
        return "page";
    }*/
}
