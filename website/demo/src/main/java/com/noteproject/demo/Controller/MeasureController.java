package com.noteproject.demo.Controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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

    @DeleteMapping()
    public ResponseEntity<String> deleteMeasure(@RequestParam("compositionId") int compositionId, @RequestParam("measureId") int measureId) {
        ms.deleteMeasure(compositionId, measureId);
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, String>> addMeasure(@RequestBody Map<String, String> payload, @RequestHeader("Authorization") String authHeader) {
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
        int compositionId = Integer.valueOf(payload.get("compositionId"));
        ms.addMeasure(measureId, compositionId);
        return ResponseEntity.ok(Map.of("status", "OK"));
    }

    @PostMapping("/duplicate")
    public ResponseEntity<String> duplicateMeasure(@RequestParam("compositionId") int compositionId, @RequestParam("measureId") int measureId) {
        ms.duplicateMeasure(measureId, compositionId);
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @PostMapping("/swap")
    public ResponseEntity<Map<String, String>> swapMeasure(@RequestBody Map<String, String> payload, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7); // remove "Bearer "
        System.out.println("TOKEN="+token);
        String username = jwtService.extractUsername(token);
        Optional<User> user = ur.findByUsername(username);
        if (user.isEmpty()) {
            throw new IllegalStateException("User not found");
        }
        int measureId = Integer.valueOf(payload.get("measureId"));
        int compositionId = Integer.valueOf(payload.get("compositionId"));
        String direction = (String) payload.get("direction"); // left or right
        ms.swapMeasure(measureId, compositionId, direction);
        return ResponseEntity.ok(Map.of("status", "OK"));
    }

}
