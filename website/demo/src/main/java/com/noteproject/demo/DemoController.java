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
        // populating measures for testing
        // measure 1 chord 1
        Note n = new Note(1, 1, false);
        Note n2 = new Note(3, 1, false);
        Note n3 = new Note(5, 1, false);
        n.next = n2;
        n2.next = n3;
        n3.next = null;
        Chord c = new Chord(n);
        
        // measure 1 chord 2
        Note n4 = new Note(5, 3, false);
        Note n5 = new Note(7, 3, false);
        Note n6 = new Note(2, 3, false);
        n4.next = n5;
        n5.next = n6;
        n6.next = null;
        Chord c2 = new Chord(n4);
        
        // measure 2 chord 1
        Note n7 = new Note(2, 2, false);
        Note n8 = new Note(4, 2, false);
        Note n9 = new Note(6, 2, false);
        n7.next = n8;
        n8.next = n9;
        n9.next = null;
        Chord c3 = new Chord(n7);
       
        
        // measure 2 chord 2
        Note n10 = new Note(4, 2, false);
        Note n11 = new Note(6, 2, false);
        Note n12 = new Note(1, 2, false);
        n10.next = n11;
        n11.next = n12;
        n12.next = null;
        Chord c4 = new Chord(n10);
        

        // measure 3 chord 1
        Note n13 = new Note(1, 4, false);
        Note n14 = new Note(3, 4, false);
        Note n15 = new Note(5, 4, false);
        n13.next = n14;
        n14.next = n15;
        n15.next = null;
        Chord c5 = new Chord(n13);
        /*
        System.out.println(1);
        System.out.println(c.getAllNoteIntervals().toString());
        System.out.println(2);
        System.out.println(c2.getAllNoteIntervals().toString());
        System.out.println(3);
        System.out.println(c3.getAllNoteIntervals().toString());
        System.out.println(4);
        System.out.println(c4.getAllNoteIntervals().toString());
        System.out.println(5);
        System.out.println(c5.getAllNoteIntervals().toString());
        */

        c.next = c2;
        c2.next = null;
        c3.next = c4;
        c4.next = null;

        Measure m = new Measure(c);
        Measure m2 = new Measure(c3);
        Measure m3 = new Measure(c5);
        m.next = m2;
        m2.next = m3;
        
        //System.out.println(m + "...." + m.getAllChords() + "....");

        /*System.out.println("HELLO");
        System.out.println(m.getChord().getNote().getInterval());
        System.out.println(m.getChord().getNote().getNext().getInterval());
        System.out.println("BYE");*/
        Composition co = new Composition("C", 4, 4, m);

        Composition co2 = co;
        System.out.println("ENTERING");
        for (Measure mm : co2.getAllMeasures()) {
            System.out.println("\t->"+mm);
            System.out.println("\t\t->"+mm.chord);
            System.out.println("\t\t\t->"+mm.chord.next);
            for (Chord cc : mm.getAllChords()) {
                for (Note nn : cc.getAllNotes()) {
                    System.out.print(nn.getInterval());
                }
                System.out.println();
                System.out.println("test");
            }
            System.out.println("test2");
            System.out.println("------------");
        }
        System.out.println("EXITING");
        System.out.println("HELLO");
        model.addAttribute("measure", m);
        //System.out.println(m.getAllChords().toString());
        //co.printCompositon(co);
        System.out.println("BYE");
        System.out.println("end of getmapping");
        return "page";
    }

    /*@PostMapping("/posttest")
    public String createMeasure(Model model) {
        return "page";
    }*/
}
