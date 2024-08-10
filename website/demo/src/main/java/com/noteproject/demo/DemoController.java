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

    @GetMapping("/page")
    public String test(Model model) {
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
        return "page";
    }


    @PostMapping("/createNewMeasure")
    public String createNewMeasure(Model model) {
        System.out.println("reached create new measure post mapping");
        fileService.writeToFile("composition.txt");
        return "redirect:/page";
    }
}
