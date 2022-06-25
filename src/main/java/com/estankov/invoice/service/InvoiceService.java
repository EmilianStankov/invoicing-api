package com.estankov.invoice.service;

import com.estankov.invoice.exception.DataFormatException;
import com.estankov.invoice.exception.InconsistentDataException;
import com.estankov.invoice.model.*;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class InvoiceService {

    private final InvoiceCsvParser csvParser;

    public InvoiceService(InvoiceCsvParser csvParser) {
        this.csvParser = csvParser;
    }

    public CalculateResponse sumInvoices(CalculateRequest calculateRequest) {
        List<InvoiceDocument> invoiceDocuments = convertToDocuments(calculateRequest);

        List<Customer> customers = invoiceDocuments.stream()
                .map(document -> Customer.builder()
                        .name(document.getCustomer())
                        .balance(document.getTotal())
                        .build())
                .toList();

        return CalculateResponse.builder()
                .currency(calculateRequest.getOutputCurrency())
                .customers(customers)
                .build();
    }

    private List<InvoiceDocument> convertToDocuments(CalculateRequest calculateRequest) {
        List<CSVRecord> csvRecords = csvParser.parseCsv(calculateRequest.getFile());
        List<InvoiceDocument> documents;

        try {
            documents = csvRecords.stream()
                    .map(record -> InvoiceDocument.builder()
                            .customer(record.get(InvoiceHeader.CUSTOMER.getName()))
                            .vatNumber(Integer.parseInt(record.get(InvoiceHeader.VAT_NUMBER.getName())))
                            .documentNumber(Integer.parseInt(record.get(InvoiceHeader.DOCUMENT_NUMBER.getName())))
                            .type(InvoiceDocument.Type.getType(record.get(InvoiceHeader.TYPE.getName())))
                            .parentDocument(optionalOfInteger(record.get(InvoiceHeader.PARENT_DOCUMENT.getName()))
                                    .orElse(null))
                            .currency(record.get(InvoiceHeader.CURRENCY.getName()))
                            .total(new BigDecimal(record.get(InvoiceHeader.TOTAL.getName())))
                            .build())
                    .toList();
        } catch (NumberFormatException e) {
            throw new DataFormatException("CSV data is not in the correct format: " + e.getMessage(), e);
        }
        validateInvoiceDocuments(documents);
        return documents;
    }

    private void validateInvoiceDocuments(List<InvoiceDocument> invoiceDocuments) {
        invoiceDocuments.stream()
                .filter(document -> document.getParentDocument() != null)
                .forEach(document -> invoiceDocuments.stream()
                        .filter(invoiceDocument -> invoiceDocument.getDocumentNumber().equals(document.getParentDocument()))
                        .findAny()
                        .orElseThrow(() -> new InconsistentDataException("Parent document with number: " + document.getParentDocument() + " not found")));
    }

    private Optional<Integer> optionalOfInteger(final String value) {
        try {
            return Optional.of(Integer.parseInt(value));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}
