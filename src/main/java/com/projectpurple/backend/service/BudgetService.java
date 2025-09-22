package com.projectpurple.backend.service;

import com.projectpurple.backend.model.Expense;
import com.projectpurple.backend.model.SavingsGoal;
import com.projectpurple.backend.model.User;
import com.projectpurple.backend.repository.ExpenseRepository;
import com.projectpurple.backend.repository.SavingsGoalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class BudgetService {
    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private SavingsGoalRepository savingsGoalRepository;

    // Expense related methods
    public Expense addExpense(Expense expense, User user) {
        expense.setUser(user);
        expense.setDate(LocalDateTime.now());
        return expenseRepository.save(expense);
    }

    public List<Expense> getExpensesByUser(User user) {
        return expenseRepository.findByUserId(user.getId());
    }

    public Optional<Expense> getExpenseById(Long id) {
        return expenseRepository.findById(id);
    }

    public Expense updateExpense(Expense expense) {
        return expenseRepository.save(expense);
    }

    public void deleteExpense(Long id) {
        expenseRepository.deleteById(id);
    }

    // Savings Goal related methods
    public SavingsGoal addSavingsGoal(SavingsGoal goal, User user) {
        goal.setUser(user);
        goal.setCurrentAmount(0.0);
        return savingsGoalRepository.save(goal);
    }

    public List<SavingsGoal> getSavingsGoalsByUser(User user) {
        return savingsGoalRepository.findByUserId(user.getId());
    }

    public Optional<SavingsGoal> getSavingsGoalById(Long id) {
        return savingsGoalRepository.findById(id);
    }

    public SavingsGoal updateSavingsGoal(SavingsGoal goal) {
        return savingsGoalRepository.save(goal);
    }

    public void deleteSavingsGoal(Long id) {
        savingsGoalRepository.deleteById(id);
    }

    // Budget summary methods
    public Double getTotalExpenses(User user) {
        List<Expense> expenses = getExpensesByUser(user);
        return expenses.stream().mapToDouble(Expense::getAmount).sum();
    }

    public Double getTotalSavingsProgress(User user) {
        List<SavingsGoal> goals = getSavingsGoalsByUser(user);
        return goals.stream().mapToDouble(SavingsGoal::getCurrentAmount).sum();
    }

    public Map<String, Double> getExpensesByCategory(User user) {
        List<Expense> expenses = getExpensesByUser(user);
        Map<String, Double> categoryMap = new HashMap<>();
        
        for (Expense expense : expenses) {
            String category = expense.getCategory();
            categoryMap.put(category, categoryMap.getOrDefault(category, 0.0) + expense.getAmount());
        }
        
        return categoryMap;
    }
}