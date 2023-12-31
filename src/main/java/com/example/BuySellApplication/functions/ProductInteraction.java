package com.example.BuySellApplication.functions;

import com.example.BuySellApplication.models.Product;
import com.example.BuySellApplication.models.User;
import lombok.Data;

import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;

import java.util.Date;

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

    private double interactionValue;

    @Column(name = "interaction_timestamp")
    private Date interactionTimestamp;

}

