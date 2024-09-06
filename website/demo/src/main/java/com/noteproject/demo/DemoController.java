package com.noteproject.demo;

import java.util.Random;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import com.noteproject.demo.Model.Chord;
import com.noteproject.demo.Model.Composition;
import com.noteproject.demo.Model.Measure;
import com.noteproject.demo.Model.Note;
import com.noteproject.demo.Repositories.CompositionRepository;

import io.micrometer.core.ipc.http.HttpSender.Response;

import org.springframework.ui.Model;

@Controller
public class DemoController {
    int index = 0;
    @Autowired
    CompositionRepository cr;

    private final FileService fileService;
    public DemoController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping("/page")
    public String test(Model model) {
        cr.insertRecord(++index);
        /*Composition dummy = new Composition();
        Composition c = dummy.readComposition("/a.txt");
        System.out.println("start of getmapping");
        Measure m = c.getMeasure();
        model.addAttribute("measure", m);*/
        String compositionString = fileService.readFile("composition.txt");
        System.out.println("composition as a string="+compositionString);
        Composition composition = new Composition().readComposition(compositionString);
        System.out.println("START COMPOSITION PRINT");
        //composition.printComposition(composition);
        System.out.println("END COMPOSITION PRINT");
        model.addAttribute("measure", composition.getMeasure());
        System.out.println("1st measure object="+composition.getMeasure());
        List<Chord> chords = cr.findChordsByCompositionId(1);
        for (Chord c : chords) {
            Note highE = c.getNote();
            Note b = highE.next;
            Note g = b.next;
            Note d = g.next;
            Note a = d.next;
            Note lowE = a.next;
            System.out.printf("E=%d, a=%d, d=%d, g=%d, b=%d, e=%d\n", lowE.getFretNumber(), a.getFretNumber(), d.getFretNumber(), g.getFretNumber(), b.getFretNumber(), highE.getFretNumber());
            System.out.println("duration=" + highE.getDuration()); // duration is accessed through notes
            System.out.println("id=" + c.getId() + ", measure id=" + c.getMeasureId());
        }
        return "page";
    }


    @PostMapping("/createNewMeasure")
    public String createNewMeasure(Model model) {
        System.out.println("reached create new measure post mapping");
        //fileService.writeToFile("composition.txt");
        //public Note(int fretNumber, int stringNumber, int duration, boolean rest) {
        // we want to make a blank measure, with just one rest
        Note wholeRest = new Note(-1, -1, 4, true);
        Note wholeRest2 = new Note(-1, -1, 4, true);
        Note wholeRest3 = new Note(-1, -1, 4, true);
        Note wholeRest4 = new Note(-1, -1, 4, true);
        Note wholeRest5 = new Note(-1, -1, 4, true);
        Note wholeRest6 = new Note(-1, -1, 4, true);
        wholeRest.next = wholeRest2;
        wholeRest2.next = wholeRest3;
        wholeRest3.next = wholeRest4;
        wholeRest4.next = wholeRest5;
        wholeRest5.next = wholeRest6;
        Chord c = new Chord(wholeRest);
        Measure m = new Measure(c);
        cr.addMeasureToRepo(m);
        return "redirect:/page";
    }

    @RequestMapping(value = "/editChord", method = RequestMethod.POST)
    public ResponseEntity<String> editChord(@RequestParam("low_e_string") int low_e_string,
                                            @RequestParam("a_string") int a_string,
                                            @RequestParam("d_string") int d_string, 
                                            @RequestParam("g_string") int g_string,
                                            @RequestParam("b_string") int b_string,
                                            @RequestParam("high_e_string") int high_e_string,
                                            @RequestParam("measure") int measure,
                                            @RequestParam("chord") int chord,
                                            @RequestParam("original_l_e") int original_l_e,
                                            @RequestParam("original_a") int original_a,
                                            @RequestParam("original_d") int original_d,
                                            @RequestParam("original_g") int original_g,
                                            @RequestParam("original_b") int original_b,
                                            @RequestParam("original_h_e") int original_h_e) {
        System.out.println("changed string values=" + low_e_string + " " + a_string + " " + d_string + " " + g_string + " " + b_string + " " + high_e_string);
        System.out.println("original string values=" + original_l_e + " " + original_a + " " + original_d + " " + original_g + " " + original_b + " " + original_h_e);
        System.out.println("chord=" + chord + ", measure=" + measure);
        
        /* TODO: get composition that was created after the initial boot */
        /* Call it here, modify the values, and then update in the html */
        if (low_e_string != -1) {

        }
        if (a_string != -1) {

        }
        if (d_string != -1) {

        }
        if (g_string != -1) {

        }
        if (b_string != -1) {

        }
        if (high_e_string != -1) {

        }
        return new ResponseEntity<>("Chord updated", HttpStatus.OK);
    }
}
