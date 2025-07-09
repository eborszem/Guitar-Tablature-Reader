package com.noteproject.demo.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.noteproject.demo.Model.*;
import com.noteproject.demo.Repository.MeasureRepository;
import org.springframework.ui.Model;

@Controller
public class MeasureController {
    @Autowired
    MeasureRepository mr;

    /* test function */
    @PostMapping("/page")
    @ResponseBody
    public String testFunction(Model model) {
        System.out.println("test");
        return "success";
    }
    
    @PostMapping("/createNewMeasure")
    @ResponseBody
    public List<Map<String, Object>> createNewMeasure(Model model) {
        System.out.println("reached create new measure post mapping");
        //fileService.writeToFile("composition.txt");
        //public Note(int fretNumber, int stringNumber, int duration, boolean rest) {
        // we want to make a blank measure, with just one rest
        Note wholeRest = new Note(-1, 0, 4, true);
        Note wholeRest2 = new Note(-1, 1, 4, true);
        Note wholeRest3 = new Note(-1, 2, 4, true);
        Note wholeRest4 = new Note(-1, 3, 4, true);
        Note wholeRest5 = new Note(-1, 4, 4, true);
        Note wholeRest6 = new Note(-1, 5, 4, true);
        wholeRest.next = wholeRest2;
        wholeRest2.next = wholeRest3;
        wholeRest3.next = wholeRest4;
        wholeRest4.next = wholeRest5;
        wholeRest5.next = wholeRest6;
        
        Chord c = new Chord(wholeRest);
        Measure m = new Measure(c);
        
        int measureId = mr.addMeasureToRepo(m, HomeController.globalCompositionId);

        List<Map<String, Object>> res = new ArrayList<>();
        Map<String, Object> chord = new HashMap<>();
        chord.put("fretNumbers", List.of(-1, -1, -1, -1, -1, -1)); // default rests
        chord.put("duration", "4"); // TODO: Whole note value currently
        chord.put("measureId", measureId);
        res.add(chord);

        return res;
    }

    @RequestMapping(value = "/deleteMeasure", method = RequestMethod.POST)
    public ResponseEntity<String> deleteMeasure(@RequestParam("measureId") int measureId) {
        mr.deleteMeasure(measureId);
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @RequestMapping(value = "/addMeasure", method = RequestMethod.POST)
    public ResponseEntity<String> addMeasure(@RequestParam("measureId") int measureId) {
        mr.addMeasure(measureId, HomeController.globalCompositionId);
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @RequestMapping(value = "/duplicateMeasure", method = RequestMethod.POST)
    public ResponseEntity<String> duplicateMeasure(@RequestParam("measureId") int measureId) {
        mr.duplicateMeasure(measureId, HomeController.globalCompositionId);
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

}
