package com.example.expensetracker.expense;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;

@Entity
public class Category {

    @Id
    @SequenceGenerator(name = "cat_id_sequence", sequenceName = "cat_id_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cat_id_sequence")
    private Long catId;
    private String catName;

    public Category() {
    }

    public Category(Long catId, String catName) {
        this.catId = catId;
        this.catName = catName;
    }

    public Long getCatId() {
        return catId;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }
}
