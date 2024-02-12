package com.example.expensetracker.expense;

import java.math.BigInteger;
import java.time.LocalDate;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;

@Entity
public class Expense {

    
    @Id
    @SequenceGenerator(name = "expense_id_sequence", sequenceName = "expense_id_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "expense_id_sequence")
    private Long id;

    @NotEmpty(message = "Please enter the transaction name")
    private String transcName;
    private String transcDesc;

    private Long amount;

    private LocalDate transcDate;

    @Column(name = "transcType")
    private String transcType;

    private String category;

    public Expense() {
    }

    public Expense(Long id, String transcName, String transcDesc, Long amount, LocalDate transcDate,
            String transcType, String category) {
        this.id = id;
        this.transcName = transcName;
        this.transcDesc = transcDesc;
        this.amount = amount;
        this.transcDate = transcDate;
        this.transcType = transcType;
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public String getTranscName() {
        return transcName;
    }

    public String getTranscDesc() {
        return transcDesc;
    }

    public LocalDate getTranscDate() {
        return transcDate;
    }

    public String getCategory() {
        return category;
    }

    public String getTranscType() {
        return transcType;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setTranscDate(LocalDate transcDate) {
        this.transcDate = transcDate;
    }

    public void setTranscDesc(String transcDesc) {
        this.transcDesc = transcDesc;
    }

    public void setTranscName(String transcName) {
        this.transcName = transcName;
    }

    public void setTranscType(String transcType) {
        this.transcType = transcType;
    }
}
