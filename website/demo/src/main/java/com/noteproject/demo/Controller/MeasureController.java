package com.noteproject.demo.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.ui.Model;
@Controller
public class MeasureController {
    @PostMapping("/page")
    public String createNewMeasure(Model model) {
        System.out.println("test");
        return "page";
    }
}
