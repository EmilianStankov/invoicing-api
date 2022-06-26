package com.estankov.invoice.service;

import com.estankov.invoice.exception.InconsistentDataException;
import com.estankov.invoice.model.ExchangeRate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

/**
 * Helper service that handles exchange rate conversions.
 */
@Service
public class CurrencyConverter {

    /**
     * Converts the list of strings into a List of ExchangeRate after validating its compliance to the requirements.
     * Sets the values for each currency based on the output currency.
     * @param exchangeRates  Strings following the pattern "^((\w){3}:\d*(.\d*))$"
     * @param outputCurrency A string following the pattern "^(\w){3}$", must be contained in the exchangeRates list
     * @return               List of ExchangeRate
     */
    public List<ExchangeRate> getExchangeRates(List<String> exchangeRates, String outputCurrency) {
        List<ExchangeRate> rates = exchangeRates.stream()
                .map(exchangeRate -> exchangeRate.split(":"))
                .map(parts -> ExchangeRate.builder()
                        .currency(parts[0])
                        .rate(new BigDecimal(parts[1]))
                        .build())
                .toList();

        validateExchangeRates(rates, outputCurrency);
        convertToOutputRate(rates, outputCurrency);
        return rates;
    }

    /**
     * Returns a specific ExchangeRate from a list based on its name.
     * @param exchangeRates List of ExchangeRate supported exchange rates
     * @param currency      The name of the currency we need the exchange rate of
     * @return              ExchangeRate
     */
    public ExchangeRate getExchangeRate(List<ExchangeRate> exchangeRates, String currency) {
        return exchangeRates.stream()
                .filter(exchangeRate -> exchangeRate.getCurrency().equalsIgnoreCase(currency))
                .findFirst()
                .orElseThrow(() -> new InconsistentDataException("Currency " + currency + " is not supported"));
    }

    private void convertToOutputRate(List<ExchangeRate> exchangeRates, String outputCurrency) {
        ExchangeRate outputRate = getExchangeRate(exchangeRates, outputCurrency);

        exchangeRates.forEach(rate -> rate.setRate(outputRate.getRate()
                .divide(rate.getRate(), MathContext.DECIMAL128)));
    }

    private void validateExchangeRates(List<ExchangeRate> exchangeRates, String outputCurrency) {
        validateBaseCurrency(exchangeRates);
        validateOutputCurrency(exchangeRates, outputCurrency);
    }

    private void validateBaseCurrency(List<ExchangeRate> exchangeRates) {
        List<ExchangeRate> baseExchangeRate = exchangeRates.stream()
                .filter(rate -> rate.getRate().equals(BigDecimal.ONE))
                .toList();
        if (baseExchangeRate.isEmpty()) {
            throw new InconsistentDataException(
                    "Base currency not provided in the supplied list of exchange rates");
        }
    }

    private void validateOutputCurrency(List<ExchangeRate> exchangeRates, String outputCurrency) {
        List<ExchangeRate> outputExchangeRate = exchangeRates.stream()
                .filter(rate -> rate.getCurrency().equalsIgnoreCase(outputCurrency))
                .toList();
        if (outputExchangeRate.isEmpty()) {
            throw new InconsistentDataException(
                    "Currency: " + outputCurrency + " not found in the supplied list of exchange rates");
        }
    }
}
