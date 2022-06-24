package com.estankov.invoice.service;

import com.estankov.invoice.model.CalculateRequest;
import com.estankov.invoice.model.CalculateResponse;
import com.estankov.invoice.model.Customer;
import com.estankov.invoice.model.InvoiceHeader;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class InvoiceService {

    public CalculateResponse suminvoices(CalculateRequest calculateRequest) {
        List<CSVRecord> csvRecords = CsvParser.parseCsv(calculateRequest.getFile());

        List<Customer> customers = csvRecords.stream()
                .map(record -> Customer.builder()
                        .name(record.get(InvoiceHeader.CUSTOMER.getName()))
                        .balance(new BigDecimal(record.get(InvoiceHeader.TOTAL.getName())))
                        .build())
                .toList();

        return CalculateResponse.builder()
                .currency(calculateRequest.getOutputCurrency())
                .customers(customers)
                .build();
    }
}
