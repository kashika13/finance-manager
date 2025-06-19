package com.kashika.finance_manager.controller;

import com.kashika.finance_manager.model.Expense;
import com.kashika.finance_manager.model.User;
import com.kashika.finance_manager.repository.ExpenseRepository;
import com.kashika.finance_manager.repository.UserRepository;
import com.kashika.finance_manager.service.AnalyticsService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class PageController {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/add-expense")
    public String showExpenseForm(@AuthenticationPrincipal UserDetails principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        return "expense_form";
    }

    @GetMapping("/show-expense")
    public String showExpenses(Model model, @AuthenticationPrincipal UserDetails principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        User user = userRepository.findByUsername(principal.getUsername()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        List<Expense> expenses = expenseRepository.findByUser(user);
        model.addAttribute("data", expenses);
        return "expense_list";
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model, Authentication authentication) {

        if (authentication == null) {
            return "redirect:/login";
        }

        String username = authentication.getName();

        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null) {
            return "redirect:/login";
        }

        List<Expense> allExpenses = expenseRepository.findByUserOrderByExpenseDateDesc(user);
        List<Expense> recent = allExpenses.stream().limit(5).collect(Collectors.toList());

        int totalThisMonth = analyticsService.calculateTotalThisMonth(user);
        Map<String, Integer> monthlyTotals = analyticsService.calculateMonthlyTotals(user);
        int currentWeekTotal = expenseRepository.findCurrentWeekTotal(user.getId());

        model.addAttribute("username", username);
        model.addAttribute("totalThisMonth", totalThisMonth);
        model.addAttribute("recentExpenses", recent);
        model.addAttribute("currentWeekTotal", currentWeekTotal);
        model.addAttribute("monthlyTotals", monthlyTotals);

        return "dashboard";
    }

    @GetMapping("/whatsapp-integration")
    public String showWhatsAppIntegration(Model model, Principal principal) {
        String username = principal.getName();
        Optional<User> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getPhoneNumber() != null && !user.getPhoneNumber().isEmpty()) {
                model.addAttribute("alreadyIntegrated", true);
            }
        }
        return "whatsapp_integration"; // your HTML page name
    }


    @GetMapping("/register")
    public String showRegisterPage(){
        return "register";
    }

    @GetMapping("/login")
    public String showLoginPage(){
        return "login";
    }

    @GetMapping("/")
    public String showHomePage(){
        return "home";
    }

    @GetMapping("/home")
    public String redirectToHome() {
        return "redirect:/";
    }
}
