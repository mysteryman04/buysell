package com.example.buysell.functions;

import com.example.buysell.models.Product;
import com.example.buysell.models.User;
import lombok.Data;

import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Entity
@Table(name = "product_interactions")
@Data
public class ProductInteraction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private double interactionValue; // You can use this to represent the user's interest or interaction strength

    // Getters and Setters
}
