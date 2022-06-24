package com.estankov.invoice.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CalculateResponse {
    private String currency;
    private List<Customer> customers;
}
