package com.noteproject.demo.Controller;

import org.springframework.stereotype.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.noteproject.demo.Service.CompositionService;

@Controller
public class CompositionController {
    @Autowired
    CompositionService cs;
      
    @PostMapping("/changeComposition")
    @ResponseBody
    public void changeComposition(@RequestParam("selectedComposition") String composition) {
        System.out.println(composition + "!!!!!...");
        HomeController.globalCompositionId = Integer.parseInt(composition);
    }

    @PostMapping("/newComposition")
    @ResponseBody
    public void newComposition(@RequestBody Map<String, String> payload) {
        String title = payload.get("title");
        String composer = payload.get("composer");
        System.out.println("NEW COMP BEING ADDED");
        System.out.println("title=" + title + ", composer=" + composer);
        HomeController.globalCompositionId = cs.addNewComposition(title, composer); // adds new comp and measure to tables
    }
}
