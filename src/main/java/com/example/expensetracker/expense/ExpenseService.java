package com.example.expensetracker.expense;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public ExpenseService(ExpenseRepository expenseRepository, CategoryRepository categoryRepository) {
        this.expenseRepository = expenseRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    public List<Category> getAllCategory() {
        return categoryRepository.findAll();
    }

    public Page<Expense> getExpensePage(Pageable pageable) {
        return expenseRepository.findAll(pageable);
    }

    public Expense getExpenseById(Long id) {
        return expenseRepository.findById(id).get();
    }

    public void addTransaction(Expense exp) {
        expenseRepository.save(exp);
    }

    public void addCategory(Category category) {
        categoryRepository.save(category);
    }

    public void deleteTransaction(Long id) {
        expenseRepository.deleteById(id);
    }

    public Expense updateTransaction(Long id, Expense expense) {
        return expenseRepository.findById(id).map(exp -> {
            exp.setTranscName(expense.getTranscName());
            exp.setTranscDesc(expense.getTranscDesc());
            exp.setAmount(expense.getAmount());
            exp.setCategory(expense.getCategory());
            exp.setTranscDate(expense.getTranscDate());
            exp.setTranscType(expense.getTranscType());
            return expenseRepository.save(exp);
        }).orElseThrow(() -> new IllegalStateException("Expense Not Found"));
    }

    public Page<Expense> findAll(Pageable page) {
        return expenseRepository.findAll(page);
    }

    public Page<Expense> filterExpenseByType(String type, Pageable page) {
        return expenseRepository.findByTranscType(type, page);
    }

    public Page<Expense> filterExpenseByCategory(String category, Pageable pageable) {
        return expenseRepository.findByCategory(category, pageable);
    }

    public Page<Expense> filterExpenseByTypeAndCategory(String type, String category, Pageable page) {
        return expenseRepository.findByTranscTypeAndCategory(type, category, page);
    }

    public Page<Expense> filterExpenseByTypeAndDate(String type, LocalDate startDate, LocalDate endDate,
            Pageable pageable) {
        return expenseRepository.findByTranscTypeAndTranscDateBetween(type, startDate, endDate,
                pageable);
    }

    public Page<Expense> filterExpenseByCategoryAndDate(String category, LocalDate startDate, LocalDate endDate,
            Pageable pageable) {
        return expenseRepository.findByTranscDateAndCategory(category, startDate, endDate,
                pageable);
    }

    public Page<Expense> filterExpenseByTypeAndCategoryAndDate(String type, String category, LocalDate starDate,
            LocalDate endDate, Pageable pageable) {
        return expenseRepository.findByTranscTypeAndCategoryAndTranscDateBetween(type, category, starDate, endDate,
                pageable);
    }

    public Page<Expense> filterExpenseByDate(LocalDate startDate, LocalDate endDate, Pageable page) {
        return expenseRepository.findByTranscDateBetween(startDate, endDate, page);
    }

    // TRANSCTION

    public Long getTotalAmountByType(String type) {
        return expenseRepository.findAll().stream().filter(exp -> exp.getTranscType().equals(type))
                .mapToLong(Expense::getAmount).sum();
    }

    public Long getTotalAmountByTypeAndCategory(String type, String category) {
        return expenseRepository.findAll().stream()
                .filter(exp -> exp.getTranscType().equals(type))
                .filter(exp -> exp.getCategory().equals(category))
                .mapToLong(Expense::getAmount).sum();
    }

    public Long getTotalAmountByTypeAndCategoryAndDate(String type, String category, LocalDate startDate,
            LocalDate endDate) {
        return expenseRepository.findAll().stream().filter(exp -> exp.getTranscType().equals(type))
                .filter(exp -> exp.getCategory().equals(category))
                .filter(exp -> exp.getTranscDate().isAfter(endDate) && exp.getTranscDate().isBefore(endDate))
                .mapToLong(Expense::getAmount).sum();
    }

    public Long getTotalIncome() {
        return expenseRepository.findAll().stream()
                .filter(exp -> exp.getTranscType().equals("Income"))
                .mapToLong(Expense::getAmount).sum();
    }

    public Long getTotalIncomeByCategory(String category) {
        return expenseRepository.findAll().stream()
                .filter(exp -> exp.getTranscType().equals("Income"))
                .filter(exp -> exp.getCategory().equals(category))
                .mapToLong(Expense::getAmount).sum();
    }

    public Long getTotalIncomeByDate(LocalDate starDate, LocalDate endDate) {
        return expenseRepository.findAll().stream()
                .filter(exp -> exp.getTranscType().equals("Income"))
                .filter(exp -> exp.getTranscDate().isAfter(starDate) && exp.getTranscDate().isBefore(endDate))
                .mapToLong(Expense::getAmount).sum();
    }

    public Long getTotalIncomeByCategoryAndDate(String category, LocalDate starDate, LocalDate endDate) {
        return expenseRepository.findAll().stream()
                .filter(exp -> exp.getTranscType().equals("Income"))
                .filter(exp -> exp.getCategory().equals(category))
                .filter(exp -> exp.getTranscDate().isAfter(starDate) && exp.getTranscDate().isBefore(endDate))
                .mapToLong(Expense::getAmount).sum();
    }

    public Long getTotalExpense() {
        return expenseRepository.findAll().stream()
                .filter(exp -> exp.getTranscType().equals("Expense"))
                .mapToLong(Expense::getAmount).sum();
    }

    public Long getTotalExpenseByCategory(String category) {
        return expenseRepository.findAll().stream()
                .filter(exp -> exp.getTranscType().equals("Expense"))
                .filter(exp -> exp.getCategory().equals(category))
                .mapToLong(Expense::getAmount).sum();
    }

    public Long getTotalExpenseByDate(LocalDate starDate, LocalDate endDate) {
        return expenseRepository.findAll().stream()
                .filter(exp -> exp.getTranscType().equals("Expense"))
                .filter(exp -> exp.getTranscDate().compareTo(starDate) >= 0
                        && exp.getTranscDate().compareTo(endDate) <= 0)
                .mapToLong(Expense::getAmount).sum();
    }

    public Long getTotalExpenseByCategoryAndDate(String category, LocalDate starDate, LocalDate endDate) {
        return expenseRepository.findAll().stream()
                .filter(exp -> exp.getTranscType().equals("Expense"))
                .filter(exp -> exp.getCategory().equals(category))
                .filter(exp -> exp.getTranscDate().isAfter(starDate) && exp.getTranscDate().isBefore(endDate))
                .mapToLong(Expense::getAmount).sum();
    }

}
