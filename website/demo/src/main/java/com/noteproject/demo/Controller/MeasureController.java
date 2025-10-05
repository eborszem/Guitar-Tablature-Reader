package com.noteproject.demo.Controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    @PostMapping()
    public ResponseEntity<Map<String, String>> addMeasure(@RequestBody Map<String, String> payload, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String username = jwtService.getValidUsername(token);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid or expired token"));
        }
        int measureId = Integer.valueOf(payload.get("measureId"));
        int compositionId = Integer.valueOf(payload.get("compositionId"));
        ms.addMeasure(measureId, compositionId);
        return ResponseEntity.ok(Map.of("status", "OK"));
    }

    @PostMapping("/duplicate")
    public ResponseEntity<String> duplicateMeasure(@RequestParam("compositionId") int compositionId, @RequestParam("measureId") int measureId, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String username = jwtService.getValidUsername(token);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }
        ms.duplicateMeasure(measureId, compositionId);
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @PutMapping("/swap")
    public ResponseEntity<Map<String, String>> swapMeasure(@RequestBody Map<String, String> payload, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String username = jwtService.getValidUsername(token);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid or expired token"));
        }
        int measureId = Integer.valueOf(payload.get("measureId"));
        int compositionId = Integer.valueOf(payload.get("compositionId"));
        String direction = (String) payload.get("direction"); // left or right
        ms.swapMeasure(measureId, compositionId, direction);
        return ResponseEntity.ok(Map.of("status", "OK"));
    }

    @DeleteMapping()
    public ResponseEntity<String> deleteMeasure(@RequestParam("compositionId") int compositionId, @RequestParam("measureId") int measureId, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String username = jwtService.getValidUsername(token);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }
        ms.deleteMeasure(compositionId, measureId);
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

}
