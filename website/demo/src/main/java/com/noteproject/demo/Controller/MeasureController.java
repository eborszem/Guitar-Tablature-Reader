package com.noteproject.demo.Controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ui.Model;
/* testing file */
@Controller
public class MeasureController {
    @PostMapping("/page")
    @ResponseBody
    public String createNewMeasure(Model model) {
        System.out.println("test");
        return "success";
    }
}
