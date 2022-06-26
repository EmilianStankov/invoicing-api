package com.estankov.invoice.service;

import com.estankov.invoice.model.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The main service that handles invoice procesing
 */
@Service
public class InvoiceService {

    private final DocumentConverter documentConverter;
    private final CurrencyConverter currencyConverter;

    /**
     * Construct an instance of the service by passing helper services
     * @param documentConverter a DocumentConverter helper service instance
     * @param currencyConverter a CurrencyConverter helper service instance
     */
    public InvoiceService(DocumentConverter documentConverter, CurrencyConverter currencyConverter) {
        this.documentConverter = documentConverter;
        this.currencyConverter = currencyConverter;
    }

    /**
     * Returns the invoice amounts for the passed customers, grouped by their vatNumbers.
     * If the CalculateRequest contains a customerVat the response is filtered only for the specific customer with that vatNumber.
     * @param calculateRequest  The input request for invoice processing
     * @return                  a CalculateResponse instance
     */
    public CalculateResponse sumInvoices(CalculateRequest calculateRequest) {
        List<InvoiceDocument> invoiceDocuments = documentConverter.convertToDocuments(calculateRequest);
        List<ExchangeRate> exchangeRates = currencyConverter.getExchangeRates(calculateRequest.getExchangeRates(),
                calculateRequest.getOutputCurrency());

        Map<Integer, List<InvoiceDocument>> documents = invoiceDocuments.stream()
                .map(document -> calculateBalance(document, exchangeRates))
                .collect(Collectors.groupingBy(InvoiceDocument::getVatNumber));

        List<Customer> customers = documents.values()
                .stream()
                .map(invoices -> Customer.builder()
                        .vatNumber(invoices.get(0).getVatNumber())
                        .name(invoices.get(0).getCustomer())
                        .balance(sumInvoices(invoices))
                        .build())
                .toList();

        return CalculateResponse.builder()
                .currency(calculateRequest.getOutputCurrency())
                .customers(filterCustomers(customers, calculateRequest.getCustomerVat()))
                .build();
    }

    private InvoiceDocument calculateBalance(InvoiceDocument document, List<ExchangeRate> exchangeRates) {
        ExchangeRate exchangeRate = currencyConverter.getExchangeRate(exchangeRates, document.getCurrency());
        BigDecimal total = document.getTotal();

        document.setTotal(total.multiply(exchangeRate.getRate())
                .setScale(2, RoundingMode.HALF_UP));
        return document;
    }

    private BigDecimal sumInvoices(List<InvoiceDocument> invoiceDocuments) {
        return invoiceDocuments.stream()
                .reduce(BigDecimal.ZERO, (total, invoiceDocument) -> switch (invoiceDocument.getType()) {
                    case INVOICE, DEBIT_NOTE -> total.add(invoiceDocument.getTotal());
                    case CREDIT_NOTE -> total.subtract(invoiceDocument.getTotal());
                }, BigDecimal::add);
    }

    private List<Customer> filterCustomers(List<Customer> customers, String vatNumber) {
        if (vatNumber == null) {
            return customers;
        }
        return customers.stream()
                .filter(customer -> customer.getVatNumber()
                        .equals(Integer.parseInt(vatNumber)))
                .toList();
    }
}
