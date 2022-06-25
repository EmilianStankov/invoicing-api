package com.estankov.invoice.service;

import com.estankov.invoice.model.ExchangeRate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CurrencyConverter {

    public List<ExchangeRate> getExchangeRates(List<String> exchangeRates) {
        return exchangeRates.stream()
                .map(exchangeRate -> exchangeRate.split(":"))
                .map(parts -> ExchangeRate.builder()
                            .currency(parts[0])
                            .rate(new BigDecimal(parts[1]))
                            .build())
                .toList();
    }

    public ExchangeRate getBaseExchangeRate(List<ExchangeRate> exchangeRates) {
        return exchangeRates.stream()
                .filter(exchangeRate -> exchangeRate.getRate().equals(BigDecimal.ONE))
                .findFirst()
                .orElseThrow();
    }

}
