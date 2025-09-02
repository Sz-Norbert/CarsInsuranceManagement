package com.example.carins.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "insurancepolicy")
@Data
public class InsurancePolicy {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Car car;

    private String provider;
    private LocalDate startDate;
    private LocalDate endDate; // nullable == open-ended
    private boolean logged = false;

    public InsurancePolicy() {}
    public InsurancePolicy(Car car, String provider, LocalDate startDate, LocalDate endDate) {
        this.car = car; this.provider = provider; this.startDate = startDate; this.endDate = endDate;
    }


}
