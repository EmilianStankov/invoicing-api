package com.estankov.invoice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * A representation of a customer with their total balance after calculating the invoice amount.
 */
@Data
@Builder
public class Customer {

    @JsonIgnore
    private Integer vatNumber;
    private String name;
    private BigDecimal balance;
}
