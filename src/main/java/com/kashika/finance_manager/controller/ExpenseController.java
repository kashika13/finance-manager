package com.kashika.finance_manager.controller;

import com.kashika.finance_manager.model.Expense;
import com.kashika.finance_manager.model.User;
import com.kashika.finance_manager.repository.ExpenseRepository;
import com.kashika.finance_manager.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Date;
import java.util.List;

@Controller
@RequestMapping("/api/expense")
public class ExpenseController {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/add")
    public String addExpense(@RequestParam String item,
                             @RequestParam String date,
                             @RequestParam String mode,
                             @RequestParam int amount,
                             RedirectAttributes redirectAttributes,
                             @AuthenticationPrincipal UserDetails principal) {

        if (principal == null) {
            return "redirect:/login";
        }

        User user = userRepository.findByUsername(principal.getUsername()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        Expense expense = new Expense();
        expense.setExpenseItem(item);
        expense.setExpenseDate(Date.valueOf(date)); // parses "yyyy-MM-dd"
        expense.setMode(mode);
        expense.setAmount(amount);
        expense.setUser(user);
        expenseRepository.save(expense);

        // Store a flash message to show after redirect
        redirectAttributes.addFlashAttribute("msg", "Expense successfully added.");

        return "redirect:/dashboard";
    }

    @GetMapping("/all")
    @ResponseBody
    public List<Expense> getAllExpenses(@AuthenticationPrincipal UserDetails principal) {
        if (principal == null) {
            return List.of();
        }

        User user = userRepository.findByUsername(principal.getUsername()).orElse(null);
        if (user == null) {
            return List.of();
        }

        return expenseRepository.findByUser(user);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public String deleteExpense(@PathVariable int id) {
        expenseRepository.deleteById(id);
        return "Deleted expense with ID: " + id;
    }
}
