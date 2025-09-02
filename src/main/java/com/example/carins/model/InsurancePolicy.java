package com.example.carins.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "insurancepolicy")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InsurancePolicy {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Car car;

    private String provider;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean logged = false;




}
