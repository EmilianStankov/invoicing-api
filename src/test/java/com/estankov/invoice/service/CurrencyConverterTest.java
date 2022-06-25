package com.estankov.invoice.service;

import com.estankov.invoice.exception.InconsistentDataException;
import com.estankov.invoice.model.ExchangeRate;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CurrencyConverterTest {

    private final CurrencyConverter objectUnderTest = new CurrencyConverter();

    private final static MathContext currencyConversionContext = new MathContext(5, RoundingMode.HALF_UP);

    private List<String> exchangeRatesInput = List.of("EUR:1", "USD:0.987", "GBP:0.878", "BGN:1.95");

    private String outputCurrency = "BGN";

    @Test
    void testConvertExchangeRates_outputCurrencySameAsBaseCurrency() {
        outputCurrency = "EUR";
        List<ExchangeRate> exchangeRates = objectUnderTest.getExchangeRates(exchangeRatesInput, outputCurrency);

        assertEquals(4, exchangeRates.size());

        assertEquals("EUR", exchangeRates.get(0).getCurrency());
        assertEquals(BigDecimal.ONE, exchangeRates.get(0).getRate());

        assertEquals("USD", exchangeRates.get(1).getCurrency());
        assertEquals(new BigDecimal("1.0132"), exchangeRates.get(1).getRate().round(currencyConversionContext));

        assertEquals("GBP", exchangeRates.get(2).getCurrency());
        assertEquals(new BigDecimal("1.1390"), exchangeRates.get(2).getRate().round(currencyConversionContext));

        assertEquals("BGN", exchangeRates.get(3).getCurrency());
        assertEquals(new BigDecimal("0.51282"), exchangeRates.get(3).getRate().round(currencyConversionContext));
    }

    @Test
    void testConvertExchangeRates_outputCurrencyDifferentFromBaseCurrency() {
        List<ExchangeRate> exchangeRates = objectUnderTest.getExchangeRates(exchangeRatesInput, outputCurrency);

        assertEquals(4, exchangeRates.size());

        assertEquals("EUR", exchangeRates.get(0).getCurrency());
        assertEquals(new BigDecimal("1.95"), exchangeRates.get(0).getRate().round(currencyConversionContext));

        assertEquals("USD", exchangeRates.get(1).getCurrency());
        assertEquals(new BigDecimal("1.9757"), exchangeRates.get(1).getRate().round(currencyConversionContext));

        assertEquals("GBP", exchangeRates.get(2).getCurrency());
        assertEquals(new BigDecimal("2.2210"), exchangeRates.get(2).getRate().round(currencyConversionContext));

        assertEquals("BGN", exchangeRates.get(3).getCurrency());
        assertEquals(BigDecimal.ONE, exchangeRates.get(3).getRate());
    }

    @Test
    void testConvertExchangeRates_missingOutputCurrency() {
        outputCurrency = "JPY";
        assertThrows(InconsistentDataException.class, () ->
                objectUnderTest.getExchangeRates(exchangeRatesInput, outputCurrency));
    }

    @Test
    void testConvertExchangeRates_missingBaseCurrency() {
        exchangeRatesInput = List.of("USD:0.987", "GBP:0.878", "BGN:1.95");
        assertThrows(InconsistentDataException.class, () ->
                objectUnderTest.getExchangeRates(exchangeRatesInput, outputCurrency));
    }

    @Test
    void testGetExchangeRate_currencyAvailable() {
        List<ExchangeRate> exchangeRates = objectUnderTest.getExchangeRates(exchangeRatesInput, outputCurrency);

        ExchangeRate exchangeRate = objectUnderTest.getExchangeRate(exchangeRates, "BGN");

        assertNotNull(exchangeRate);
        assertEquals("BGN", exchangeRate.getCurrency());
        assertEquals(BigDecimal.ONE, exchangeRate.getRate());
    }

    @Test
    void testGetExchangeRate_currencyNotAvailable() {
        List<ExchangeRate> exchangeRates = objectUnderTest.getExchangeRates(exchangeRatesInput, outputCurrency);

        assertThrows(InconsistentDataException.class, () ->
                objectUnderTest.getExchangeRate(exchangeRates, "JPY"));
    }
}
