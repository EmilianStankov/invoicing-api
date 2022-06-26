package com.estankov.invoice.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * An invoice calculation result.
 * Groups the final amounts in the target currency per customer based on their vatNumbers.
 */
@Data
@Builder
public class CalculateResponse {

    private String currency;
    private List<Customer> customers;
}
