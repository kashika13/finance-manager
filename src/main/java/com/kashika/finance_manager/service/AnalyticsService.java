package com.kashika.finance_manager.service;

import com.kashika.finance_manager.model.Expense;
import com.kashika.finance_manager.model.User;
import com.kashika.finance_manager.repository.ExpenseRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;


@Service
public class AnalyticsService {

    @Autowired
    private ExpenseRepository expenseRepository;

    public Map<String, Integer> calculateMonthlyTotals(User user) {
        List<Expense> expenses = expenseRepository.findByUserOrderByExpenseDateDesc(user);
        Map<String, Integer> totals = new LinkedHashMap<>();
        for (Expense expense : expenses) {
            LocalDate date = expense.getExpenseDate().toLocalDate();
            String month = date.getMonth().name().substring(0, 1) + date.getMonth().name().substring(1).toLowerCase();

            totals.put(month, totals.getOrDefault(month, 0) + expense.getAmount()); // Accumulate total per month
        }
        return totals;
    }



    // Calculate total for the current month
    public int calculateTotalThisMonth(User user) {
        List<Expense> expenses = expenseRepository.findByUserOrderByExpenseDateDesc(user);
        int total = 0;
        LocalDate now = LocalDate.now();

        for (Expense expense : expenses) {
            LocalDate expenseDate = expense.getExpenseDate().toLocalDate();
            if (expenseDate.getMonth().equals(now.getMonth()) &&
                    expenseDate.getYear() == now.getYear()) {
                total += expense.getAmount();
            }
        }
        return total;
    }
}
