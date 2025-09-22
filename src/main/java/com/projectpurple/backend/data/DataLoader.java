package com.projectpurple.backend.data;

import com.projectpurple.backend.model.Expense;
import com.projectpurple.backend.model.SavingsGoal;
import com.projectpurple.backend.model.User;
import com.projectpurple.backend.repository.ExpenseRepository;
import com.projectpurple.backend.repository.SavingsGoalRepository;
import com.projectpurple.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;

@Component
public class DataLoader implements CommandLineRunner {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private SavingsGoalRepository savingsGoalRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Check if there are already users in the database
        if (userRepository.count() == 0) {
            // Create sample user
            User user1 = new User();
            user1.setName("John Doe");
            user1.setEmail("john.doe@example.com");
            user1.setPassword(passwordEncoder.encode("password123"));
            userRepository.save(user1);

            User user2 = new User();
            user2.setName("Jane Smith");
            user2.setEmail("jane.smith@example.com");
            user2.setPassword(passwordEncoder.encode("password456"));
            userRepository.save(user2);

            // Create sample expenses for user1
            Expense expense1 = new Expense();
            expense1.setAmount(45.50);
            expense1.setCategory("Food & Dining");
            expense1.setDate(LocalDateTime.of(2023, Month.APRIL, 10, 12, 30));
            expense1.setDescription("Lunch with colleagues");
            expense1.setUser(user1);

            Expense expense2 = new Expense();
            expense2.setAmount(120.00);
            expense2.setCategory("Bills & Utilities");
            expense2.setDate(LocalDateTime.of(2023, Month.APRIL, 5, 9, 0));
            expense2.setDescription("Electricity bill");
            expense2.setUser(user1);

            Expense expense3 = new Expense();
            expense3.setAmount(85.00);
            expense3.setCategory("Transportation");
            expense3.setDate(LocalDateTime.of(2023, Month.APRIL, 8, 18, 45));
            expense3.setDescription("Monthly bus pass");
            expense3.setUser(user1);

            expenseRepository.saveAll(Arrays.asList(expense1, expense2, expense3));

            // Create sample savings goals for user1
            SavingsGoal goal1 = new SavingsGoal();
            goal1.setName("Emergency Fund");
            goal1.setTargetAmount(5000.00);
            goal1.setCurrentAmount(1200.00);
            goal1.setDescription("Savings for unexpected expenses");
            goal1.setUser(user1);

            SavingsGoal goal2 = new SavingsGoal();
            goal2.setName("Vacation");
            goal2.setTargetAmount(2000.00);
            goal2.setCurrentAmount(800.00);
            goal2.setDescription("Summer vacation fund");
            goal2.setUser(user1);

            savingsGoalRepository.saveAll(Arrays.asList(goal1, goal2));

            System.out.println("Sample data has been loaded into the database.");
        } else {
            System.out.println("Database already contains data. Skipping sample data loading.");
        }
    }
}