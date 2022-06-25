package com.estankov.invoice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class Customer {

    @JsonIgnore
    private Integer vatNumber;
    private String name;
    private BigDecimal balance;
}
