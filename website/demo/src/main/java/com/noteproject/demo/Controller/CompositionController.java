package com.noteproject.demo.Controller;

import org.springframework.stereotype.Controller;

import java.util.Map;
import java.util.Optional;

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
import org.springframework.web.bind.annotation.ResponseBody;

import com.noteproject.demo.Entity.User;
import com.noteproject.demo.Model.Composition;
import com.noteproject.demo.Repository.UserRepository;
import com.noteproject.demo.Service.CompositionService;
import com.noteproject.demo.Service.JwtService;

@RequestMapping("/composition")
@Controller
public class CompositionController {
    @Autowired
    CompositionService cs;

    @Autowired
    UserRepository ur;

    @Autowired
    JwtService jwtService;

    @PutMapping()
    @ResponseBody
    public ResponseEntity<Map<String, ?>> newComposition(@RequestBody Map<String, String> payload, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String username = jwtService.getValidUsername(token);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid or expired token"));
        }
        String title = payload.get("title");
        String composer = payload.get("composer");
        Optional<User> user = ur.findByUsername(username);
        Long userId = user.get().getId();
        return ResponseEntity.ok(Map.of("compositionId", cs.addNewComposition(title, composer, userId)));
    }

    @DeleteMapping()
    @ResponseBody
    public ResponseEntity<Map<String, ?>> deleteComposition(@RequestBody Map<String, String> payload, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String username = jwtService.getValidUsername(token);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid or expired token"));
        }
        int id = Integer.valueOf(payload.get("id"));
        Optional<User> user = ur.findByUsername(username);
        Long userId = user.get().getId();
        if (!cs.isOwner(userId, id)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Cannot delete other users' compositions"));
        };
        return ResponseEntity.ok(Map.of("compositionId", cs.deleteComposition(id)));
    }
}
