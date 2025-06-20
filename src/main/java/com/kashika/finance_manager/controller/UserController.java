package com.kashika.finance_manager.controller;

import com.kashika.finance_manager.model.User;
import com.kashika.finance_manager.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Optional;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String password,
                           RedirectAttributes redirectAttributes) {

        // Check if the username already exists
        if (userRepository.findByUsername(username).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Username already exists.");
            return "redirect:/register";
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));

        userRepository.save(user);

        redirectAttributes.addFlashAttribute("msg", "Registration successful! Please login.");
        return "redirect:/login";
    }

    @PostMapping("/save-phone")
    public String savePhoneNumber(@RequestParam String phoneNumber, Principal principal, RedirectAttributes redirectAttributes) {
        Optional<User> userOpt = userRepository.findByUsername(principal.getName());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setPhoneNumber("whatsapp:"+phoneNumber);
            userRepository.save(user);
            redirectAttributes.addFlashAttribute("msg", "Phone number saved successfully!");
        }
        return "redirect:/whatsapp-integration";
    }




    // The login process is now automated by Spring Security
    // So we do not need this mapping manually.
    // Spring Security handles form submission with Username/Password.

    // The "/login" and "/logout" endpoints are provided by Spring Security by default
}
