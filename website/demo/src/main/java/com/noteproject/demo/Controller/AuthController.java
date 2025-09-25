package com.noteproject.demo.Controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.noteproject.demo.Entity.User;
import com.noteproject.demo.Repository.UserRepository;
import com.noteproject.demo.Service.JwtService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder,
                            JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/auth/register")
    @ResponseBody
    public ResponseEntity<?> register(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        try {
            userRepository.save(user);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Username '" + user.getUsername() + "' is already taken.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
        String token = jwtService.generateToken(user.getUsername());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/auth/login")
    @ResponseBody
    public ResponseEntity<?> login(@RequestBody User user, HttpServletResponse response) {
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser.isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Username does not exist.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
            );
        } catch (org.springframework.security.core.AuthenticationException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Incorrect password.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
        String token = jwtService.generateToken(user.getUsername());
        Cookie cookie = new Cookie("jwt", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60); // 1 hour
        response.addCookie(cookie);
        return ResponseEntity.ok(token);
    }


    @GetMapping("/auth/logout")
    public String logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("jwt", "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // Delete cookie immediately
        response.addCookie(cookie);
        return "redirect:/";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
}
