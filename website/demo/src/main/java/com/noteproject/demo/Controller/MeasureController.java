package com.noteproject.demo.Controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.noteproject.demo.Entity.User;
import com.noteproject.demo.Repository.UserRepository;
import com.noteproject.demo.Service.JwtService;
import com.noteproject.demo.Service.MeasureService;

@RequestMapping("/measure")
@Controller
public class MeasureController {
    @Autowired
    JwtService jwtService;

    @Autowired
    MeasureService ms;

    @Autowired
    UserRepository ur;

    @PostMapping("/delete")
    public ResponseEntity<String> deleteMeasure(@RequestParam("measureId") int measureId) {
        ms.deleteMeasure(measureId);
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<String> addMeasure(@RequestBody Map<String, String> payload, @RequestHeader("Authorization") String authHeader) {
        System.out.println("reached create new measure post mapping");
        System.out.println("AUTH HEADER="+authHeader);
        String token = authHeader.substring(7); // remove "Bearer "
        System.out.println("TOKEN="+token);
        String username = jwtService.extractUsername(token);
        Optional<User> user = ur.findByUsername(username);
        if (user.isEmpty()) {
            throw new IllegalStateException("User not found");
        }
        int measureId = Integer.valueOf(payload.get("measureId"));
        ms.addMeasure(measureId, HomeController.globalCompositionId);
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @PostMapping("/duplicate")
    public ResponseEntity<String> duplicateMeasure(@RequestParam("measureId") int measureId) {
        ms.duplicateMeasure(measureId, HomeController.globalCompositionId);
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

}
