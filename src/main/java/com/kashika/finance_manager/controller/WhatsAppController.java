package com.kashika.finance_manager.controller;

import com.kashika.finance_manager.model.Expense;
import com.kashika.finance_manager.model.User;
import com.kashika.finance_manager.repository.ExpenseRepository;
import com.kashika.finance_manager.repository.UserRepository;
import com.kashika.finance_manager.service.AnalyticsService;
import com.twilio.twiml.MessagingResponse;
import com.twilio.twiml.messaging.Body;
import com.twilio.twiml.messaging.Message;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;
import java.util.Map;

@RestController
public class WhatsAppController {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final AnalyticsService analyticsService;

    public WhatsAppController(ExpenseRepository expenseRepository, UserRepository userRepository,AnalyticsService analyticsService) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
        this.analyticsService = analyticsService;
    }

    @PostMapping(value = "/whatsapp", produces = "application/xml")
    public String receiveMessage(HttpServletRequest request) {
        String body = request.getParameter("Body").trim().toLowerCase();
        String from = request.getParameter("From");
        System.out.println(from);

        // For now: fallback hardcoded user (replace with phone-based mapping later)
        User user = userRepository.findByPhoneNumber(from).orElse(null);
        if (user == null) {
            return twilioResponse("❌ User not found.");
        }

        try {
            // 🔷 Total for this month
            if (body.equals("monthly")) {
                int total = analyticsService.calculateTotalThisMonth(user);
                return twilioResponse("This month's total: ₹" + total);
            }

            // 🔷 Total for this week
            if (body.equals("weekly")) {
                int total = expenseRepository.findCurrentWeekTotal(user.getId());
                return twilioResponse("This week's total: ₹" + total);
            }

            // 🔷 Last 5 expenses
            if (body.equals("recent")) {
                List<Expense> recent = expenseRepository.findByUserOrderByExpenseDateDesc(user)
                        .stream().limit(5).toList();

                if (recent.isEmpty()) {
                    return twilioResponse("No recent expenses found.");
                }

                StringBuilder sb = new StringBuilder("🧾 Last 5 Expenses:\n");
                for (Expense e : recent) {
                    sb.append("- ").append(e.getExpenseItem())
                            .append(": ₹").append(e.getAmount())
                            .append(" on ").append(e.getExpenseDate()).append("\n");
                }
                return twilioResponse(sb.toString());
            }

            // 🔷 Monthly breakdown (like bar chart labels)
            if (body.equals("breakdown")) {
                Map<String, Integer> monthlyTotals = analyticsService.calculateMonthlyTotals(user);

                if (monthlyTotals.isEmpty()) {
                    return twilioResponse("📊 No data available for monthly breakdown.");
                }

                StringBuilder sb = new StringBuilder("📊 Monthly Breakdown:\n");
                monthlyTotals.forEach((month, total) ->
                        sb.append(month).append(": ₹").append(total).append("\n")
                );
                return twilioResponse(sb.toString());
            }

            // 🔷 Expense Entry Format: Item, Amount, Mode, Date
            String[] parts = body.split(",");
            if (parts.length < 4) {
                return twilioResponse("❌ Invalid format.\nSend:\n1. `Item, Amount, Mode, Date (yyyy-MM-dd)`\n2. `monthly`, `weekly`, `recent`, `breakdown`");
            }

            String item = parts[0].trim();
            int amount = (int) Double.parseDouble(parts[1].trim());
            String mode = parts[2].trim();
            String date = parts[3].trim();

            Expense expense = new Expense();
            expense.setExpenseItem(item);
            expense.setAmount(amount);
            expense.setMode(mode);
            expense.setExpenseDate(Date.valueOf(date));
            expense.setUser(user);

            expenseRepository.save(expense);
            return twilioResponse("✅ Saved: " + item + " ₹" + amount + " on " + date + " via " + mode);

        } catch (Exception e) {
            e.printStackTrace();
            return twilioResponse("❌ Error. Please try again.");
        }
    }

    private String twilioResponse(String messageText) {
        Body body = new Body.Builder(messageText).build();
        Message sms = new Message.Builder().body(body).build();
        MessagingResponse twiml = new MessagingResponse.Builder().message(sms).build();

        return twiml.toXml();
    }


}
