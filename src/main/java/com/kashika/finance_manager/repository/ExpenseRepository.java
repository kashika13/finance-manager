package com.kashika.finance_manager.repository;

import com.kashika.finance_manager.model.Expense;
import com.kashika.finance_manager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Integer> {
    List<Expense> findByUser(User user);
    List<Expense> findByUserOrderByExpenseDateDesc(User user);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.user.id = :userId AND YEARWEEK(e.expenseDate) = YEARWEEK(CURRENT_DATE())")
    Integer findCurrentWeekTotal(@Param("userId") Integer userId);


}
