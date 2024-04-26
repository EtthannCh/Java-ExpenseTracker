package com.example.expensetracker.expense;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

        @Query("SELECT exp Expense exp WHERE exp.transc_type = :type")
        Page<Expense> findByTranscType(@Param("type") String type, Pageable pageable);

        @Query("SELECT exp Expense exp where exp.transc_date >= :start_date and exp.transc_date <= :end_date")
        Page<Expense> findByTranscDateBetween(
                        @Param("start_date") LocalDate starDate,
                        @Param("end_date") LocalDate endDate,
                        Pageable pageable);

        @Query("SELECT exp from Expense exp inner join Category c ON e.cat_id = exp.cat_id  where c.cat_name = :category")
        Page<Expense> findByCategory(@Param("category") String category, Pageable pageable);

        @Query("SELECT exp Expense exp INNER JOIN Category c ON c.cat_id = exp.cat_id WHERE exp.transc_type = :type and c.cat_name = :category")
        Page<Expense> findByTranscTypeAndCategory(
                        @Param("type") String type,
                        @Param("category") String category,
                        Pageable pageable);

        @Query("SELECT exp Expense exp where exp.transc_type = :type AND exp.transc_date >= :start_date and exp.transc_date <= :end_date")
        Page<Expense> findByTranscTypeAndTranscDateBetween(
                        @Param("type") String type,
                        @Param("start_date") LocalDate startDate,
                        @Param("end_date") LocalDate endDate,
                        Pageable pageable);

        @Query("SELECT exp Expense exp INNER JOIN Category c ON c.cat_id = exp.cat_id WHERE exp.transc_date >= :start_date and exp.transc_date <= :end_date and c.cat_name = :category")
        Page<Expense> findByTranscDateAndCategory(
                        @Param("category") String category,
                        @Param("start_date") LocalDate startDate,
                        @Param("end_date") LocalDate endDate,
                        Pageable pageable);

        @Query("SELECT exp Expense exp INNER JOIN Category c ON c.cat_id = exp.cat_id where exp.transc_date >= :start_date and exp.transc_date <= :end_date AND c.cat_name = :category")
        Page<Expense> findByCategoryAndTranscDateBetween(
                        @Param("start_date") LocalDate starDate,
                        @Param("end_date") LocalDate endDate,
                        @Param("category") String category,
                        Pageable pageable);

        @Query("SELECT exp Expense exp INNER JOIN Category c ON c.cat_id = exp.cat_id where exp.transc_type = :type AND c.cat_name :category AND exp.transc_date >= :start_date and exp.transc_date <= :end_date")
        Page<Expense> findByTranscTypeAndCategoryAndTranscDateBetween(
                        @Param("type") String type,
                        @Param("category") String category,
                        @Param("start_date") LocalDate startDate,
                        @Param("end_date") LocalDate endDate,
                        Pageable pageable);

        // TOTAL TRANSACTION AMOUNT

}
