package com.estankov.invoice.service;

import com.estankov.invoice.model.CalculateRequest;
import com.estankov.invoice.model.CalculateResponse;
import com.estankov.invoice.model.ExchangeRate;
import com.estankov.invoice.model.InvoiceDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InvoiceServiceTest {

    private final InvoiceService objectUnderTest;

    private final String FILE_CONTENT = """
            Customer,Vat number,Document number,Type,Parent document,Currency,Total
            Customer1,123456789,1,1,,EUR,1000
            Customer1,123456789,2,2,1,USD,100
            Customer1,123456789,3,3,1,BGN,500
            """;

    private final MockMultipartFile FILE = new MockMultipartFile("data.csv", FILE_CONTENT.getBytes(StandardCharsets.UTF_8));

    List<ExchangeRate> exchangeRates;

    @Mock
    private DocumentConverter documentConverter = mock(DocumentConverter.class);

    @Mock
    private CurrencyConverter currencyConverter = mock(CurrencyConverter.class);

    public InvoiceServiceTest() {
        this.objectUnderTest = new InvoiceService(documentConverter, currencyConverter);
    }

    @BeforeEach
    void setUp() {
        exchangeRates = buildExchangeRates();
        when(documentConverter.convertToDocuments(any(CalculateRequest.class)))
                .thenReturn(buildInvoiceDocuments());
        when(currencyConverter.getExchangeRates(anyList(), anyString()))
                .thenReturn(exchangeRates);
    }

    @Test
    void testSumInvoices_outputCurrencySameAsBaseCurrency() {
        when(currencyConverter.getExchangeRate(anyList(), anyString()))
                .thenReturn(exchangeRates.get(0));

        CalculateResponse response = objectUnderTest.sumInvoices(
                new CalculateRequest(
                        FILE,
                        List.of("EUR:1", "BGN:1.95", "USD:0.987"),
                        "EUR",
                        null
                )
        );

        assertEquals("EUR", response.getCurrency());
        assertEquals(2, response.getCustomers().size());
        assertEquals(new BigDecimal("59355.00"), response.getCustomers().get(0).getBalance());
        assertEquals(new BigDecimal("134328.00"), response.getCustomers().get(1).getBalance());
    }

    @Test
    void testSumInvoices_outputCurrencyDifferentFromBaseCurrency() {
        when(currencyConverter.getExchangeRate(anyList(), anyString()))
                .thenReturn(exchangeRates.get(2));

        CalculateResponse response = objectUnderTest.sumInvoices(
                new CalculateRequest(
                        FILE,
                        List.of("EUR:1", "BGN:1.95", "USD:0.987"),
                        "USD",
                        null
                )
        );

        assertEquals("USD", response.getCurrency());
        assertEquals(2, response.getCustomers().size());
        assertEquals(new BigDecimal("58583.38"), response.getCustomers().get(0).getBalance());
        assertEquals(new BigDecimal("132581.74"), response.getCustomers().get(1).getBalance());
    }

    @Test
    void testSumInvoices_filtered() {
        when(currencyConverter.getExchangeRate(anyList(), anyString()))
                .thenReturn(exchangeRates.get(0));

        CalculateResponse response = objectUnderTest.sumInvoices(
                new CalculateRequest(
                        FILE,
                        List.of("EUR:1", "BGN:1.95", "USD:0.987"),
                        "EUR",
                        "123456789"
                )
        );

        assertEquals("EUR", response.getCurrency());
        assertEquals(1, response.getCustomers().size());
        assertEquals(new BigDecimal("134328.00"), response.getCustomers().get(0).getBalance());
    }

    private List<ExchangeRate> buildExchangeRates() {
        return List.of(
                ExchangeRate.builder()
                        .rate(BigDecimal.ONE)
                        .currency("EUR")
                        .build(),
                ExchangeRate.builder()
                        .rate(new BigDecimal("1.95"))
                        .currency("BGN")
                        .build(),
                ExchangeRate.builder()
                        .rate(new BigDecimal("0.987"))
                        .currency("USD")
                        .build()
        );
    }

    private List<InvoiceDocument> buildInvoiceDocuments() {
        return List.of(
                InvoiceDocument.builder()
                        .customer("Customer1")
                        .vatNumber(123456789)
                        .documentNumber(1)
                        .type(InvoiceDocument.Type.INVOICE)
                        .parentDocument(null)
                        .currency("EUR")
                        .total(new BigDecimal("100000"))
                        .build(),
                InvoiceDocument.builder()
                        .customer("Customer1")
                        .vatNumber(123456789)
                        .documentNumber(2)
                        .type(InvoiceDocument.Type.CREDIT_NOTE)
                        .parentDocument(1)
                        .currency("BGN")
                        .total(new BigDecimal("45672"))
                        .build(),
                InvoiceDocument.builder()
                        .customer("Customer1")
                        .vatNumber(123456789)
                        .documentNumber(3)
                        .type(InvoiceDocument.Type.DEBIT_NOTE)
                        .parentDocument(1)
                        .currency("USD")
                        .total(new BigDecimal("80000"))
                        .build(),
                InvoiceDocument.builder()
                        .customer("Customer2")
                        .vatNumber(234567891)
                        .documentNumber(4)
                        .type(InvoiceDocument.Type.INVOICE)
                        .parentDocument(null)
                        .currency("BGN")
                        .total(new BigDecimal("63452"))
                        .build(),
                InvoiceDocument.builder()
                        .customer("Customer2")
                        .vatNumber(234567891)
                        .documentNumber(5)
                        .type(InvoiceDocument.Type.CREDIT_NOTE)
                        .parentDocument(4)
                        .currency("USD")
                        .total(new BigDecimal("4097"))
                        .build()
        );
    }
}
