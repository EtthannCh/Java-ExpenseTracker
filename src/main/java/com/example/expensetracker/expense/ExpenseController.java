package com.example.expensetracker.expense;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping
public class ExpenseController {

    private final ExpenseService expenseService;
    private static final int PAGE_SIZE = 2;
    NumberFormat idrFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping("/home")
    public String home(@RequestParam(defaultValue = "0") int page, Model model) {
        PageRequest pageable = PageRequest.of(page, PAGE_SIZE);
        Page<Expense> expensePage = expenseService.getExpensePage(pageable);
        Long totalIncome = expenseService.getTotalIncome();
        Long totalExpense = expenseService.getTotalExpense();
        model.addAttribute("expense", expensePage);
        model.addAttribute("category", expenseService.getAllCategory());
        model.addAttribute("total", idrFormat.format(totalIncome - totalExpense));
        model.addAttribute("totalIncome", idrFormat.format(totalIncome));
        model.addAttribute("totalExpense", idrFormat.format(totalExpense));

        return "home";
    }

    @GetMapping("/add_transaction")
    public String addTransactionView(Model model) {
        model.addAttribute("category", expenseService.getAllCategory());
        return "add_transaction";
    }

    @GetMapping("/add_category")
    public String addCategory() {
        return "add_category";
    }

    @PostMapping("/home/save")
    public String saveTransaction(@ModelAttribute Expense exp) {
        expenseService.addTransaction(exp);
        return "redirect:/home";
    }

    @PostMapping("/home/saveCat")
    public String saveCategory(@ModelAttribute Category category) {
        expenseService.addCategory(category);
        return "redirect:/home";
    }

    @RequestMapping("/delete/{id}")
    public String deleteTransaction(@PathVariable("id") Long id) {
        expenseService.deleteTransaction(id);
        return "redirect:/home";
    }

    @GetMapping("/edit/{id}")
    public ModelAndView editTransactionView(@PathVariable("id") Long id, ModelAndView model) {
        Expense exp = expenseService.getExpenseById(id);
        return new ModelAndView("edit_transaction", "expense", exp);
    }

    @PostMapping("/edit/{id}")
    public String editTransaction(@PathVariable("id") Long id, @ModelAttribute Expense exp) {
        expenseService.updateTransaction(id, exp);
        return "redirect:/home";
    }

    @RequestMapping("/expense/filter")
    public String filterTransaction(
            @RequestParam(name = "type", required = false, defaultValue = "") String type,
            @RequestParam(name = "startDate", required = false, defaultValue = "") String startDate,
            @RequestParam(name = "endDate", required = false, defaultValue = "") String endDate,
            @RequestParam(name = "category", required = false, defaultValue = "") String category,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        PageRequest pageable = PageRequest.of(page, PAGE_SIZE);
        Page<Expense> filterExpensePage;

        // type is not empty and others are empty
        if (!type.isEmpty() && startDate.isEmpty() && endDate.isEmpty() && category.isEmpty()) {
            try {
                filterExpensePage = expenseService.filterExpenseByType(type, pageable);
                Long totalIncome = expenseService.getTotalIncome();
                Long totalExpense = expenseService.getTotalExpense();
                model.addAttribute("expense", filterExpensePage);
                model.addAttribute("type", type);
                model.addAttribute("totalIncome", totalIncome);
                model.addAttribute("totalExpense", totalExpense);
                model.addAttribute("category", expenseService.getAllCategory());
                // return "<h1>Date and Category empty</h1>";
                return "home";
            } catch (Exception e) {
                System.err.println("Error parsing date: " + e.getMessage());
                return "abc";
            }
        }

        // category is not empty but the other fields are empty
        else if (!category.isEmpty() && type.isEmpty() && startDate.isEmpty() && endDate.isEmpty()) {
            Page<Expense> expense = expenseService.filterExpenseByCategory(category, pageable);
            Long totalIncome = expenseService.getTotalIncomeByCategory(category);
            Long totalExpense = expenseService.getTotalExpenseByCategory(category);
            model.addAttribute("totalIncome", totalIncome);
            model.addAttribute("totalExpense", totalExpense);
            model.addAttribute("expense", expense);
            model.addAttribute("category", expenseService.getAllCategory());
            // return "type and date are empty";
            return "home";
        }

        // startDate and endDate is not empty, but the other fields are empty
        else if (!startDate.isEmpty() && !endDate.isEmpty() && type.isEmpty() && category.isEmpty()) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate newStartDate = LocalDate.parse(startDate, formatter);
                LocalDate newEndDate = LocalDate.parse(endDate, formatter);
                Long totalIncome = expenseService.getTotalIncomeByDate(newStartDate, newEndDate);
                Long totalExpense = expenseService.getTotalExpenseByDate(newStartDate, newEndDate);
                if (newStartDate.isBefore(newEndDate)) {
                    filterExpensePage = expenseService.filterExpenseByDate(newStartDate,
                            newEndDate, pageable);
                    model.addAttribute("expense", filterExpensePage);
                    model.addAttribute("totalIncome", totalIncome);
                    model.addAttribute("totalExpense", totalExpense);
                    model.addAttribute("type", type);
                    model.addAttribute("category", expenseService.getAllCategory());
                    // return "<h1>type and category empty</h1>";
                    return "home";
                }
            } catch (DateTimeParseException e) {
                // Handle parsing exception
                System.err.println("Error parsing date: " + e.getMessage());
            }
        }

        // type and category are not empty and the rest are empty
        else if (!type.isEmpty() && startDate.isEmpty() && endDate.isEmpty() && !category.isEmpty()) {
            try {
                filterExpensePage = expenseService.filterExpenseByTypeAndCategory(type, category, pageable);
                model.addAttribute("expense", filterExpensePage);
                model.addAttribute("type", type);
                model.addAttribute("category", expenseService.getAllCategory());
                // return "<h1>Date empty</h1>";
                return "home";
            } catch (Exception e) {
                System.err.println("Error parsing date: " + e.getMessage());
                return "abc";
            }
        }

        // type and date are not empty, category is empty
        else if (!type.isEmpty() && !startDate.isEmpty() && !endDate.isEmpty() && category.isEmpty()) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate newStartDate = LocalDate.parse(startDate, formatter);
                LocalDate newEndDate = LocalDate.parse(endDate, formatter);
                filterExpensePage = expenseService.filterExpenseByTypeAndDate(type, newStartDate, newEndDate, pageable);
                model.addAttribute("expense", filterExpensePage);
                model.addAttribute("type", type);
                model.addAttribute("category", expenseService.getAllCategory());
                // return "<h1>category empty</h1>";
                return "home";
            } catch (Exception e) {
                System.err.println("Error parsing date: " + e.getMessage());
                return "abc";
            }
        }

        // category and date are not empty, type is empty
        else if (!category.isEmpty() && !startDate.isEmpty() && !endDate.isEmpty() && type.isEmpty()) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate newStartDate = LocalDate.parse(startDate, formatter);
                LocalDate newEndDate = LocalDate.parse(endDate, formatter);
                filterExpensePage = expenseService.filterExpenseByCategoryAndDate(type, newStartDate, newEndDate,
                        pageable);
                model.addAttribute("expense", filterExpensePage);
                model.addAttribute("type", type);
                model.addAttribute("category", expenseService.getAllCategory());
                // return "<h1>Type empty</h1>";
                return "home";
            } catch (Exception e) {
                System.err.println("Error parsing date: " + e.getMessage());
            }
        }

        // all of the fields are not empty
        else if (!type.isEmpty() && !startDate.isEmpty() && !endDate.isEmpty() && !category.isEmpty()) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate newStartDate = LocalDate.parse(startDate, formatter);
                LocalDate newEndDate = LocalDate.parse(endDate, formatter);
                if (newStartDate.isBefore(newEndDate)) {
                    filterExpensePage = expenseService.filterExpenseByTypeAndDate(type, newStartDate,
                            newEndDate, pageable);
                    model.addAttribute("expense", filterExpensePage);
                    model.addAttribute("type", type);
                    model.addAttribute("category", expenseService.getAllCategory());
                    // return "<h1>ALL EXISTS</h1>";
                    return "home";
                }
            } catch (DateTimeParseException e) {
                // Handle parsing exception
                System.err.println("Error parsing date: " + e.getMessage());
            }

        }

        // all of the fields are empty
        else if (type.isEmpty() && startDate.isEmpty() && endDate.isEmpty() && category.isEmpty()) {
            Page<Expense> expense = expenseService.findAll(pageable);
            model.addAttribute("expense", expense);
            model.addAttribute("totalIncome", expenseService.getTotalAmountByType("Income"));
            model.addAttribute("totalExpense", expenseService.getTotalAmountByType("Expense"));
            model.addAttribute("category", expenseService.getAllCategory());
            return "home";
            // return "<h1>ALL EMPTY</h1>";
        }
        model.addAttribute("category", expenseService.getAllCategory());

        return "redirect:/expense/filter";
    }
}
