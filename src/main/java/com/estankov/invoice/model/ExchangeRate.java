package com.estankov.invoice.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ExchangeRate {

    private String currency;
    private BigDecimal rate;
}
