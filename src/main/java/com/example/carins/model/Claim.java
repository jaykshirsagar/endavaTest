package com.example.carins.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "claim")
public class Claim {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Car car;

    private LocalDate claimDate;

    private String description;

    private Long amount;

    public Claim() {}

    public Claim(Car car, LocalDate claimDate, String description, Long amount) {
        this.car = car;
        this.claimDate = claimDate;
        this.description = description;
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public Car getCar() {
        return car;
    }

    public String getDescription() {
        return description;
    }

    public Long getAmount() {
        return amount;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public LocalDate getClaimDate() {
        return claimDate;
    }

    public void setClaimDate(LocalDate claimDate) {
        this.claimDate = claimDate;
    }
}
