package com.estankov.invoice.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * The exchange rate for a currency against the output currency
 */
@Data
@Builder
public class ExchangeRate {

    private String currency;
    private BigDecimal rate;
}
