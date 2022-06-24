package com.estankov.invoice.service;

import com.estankov.invoice.exception.CsvParseException;
import com.estankov.invoice.model.InvoiceHeader;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class CsvParser {

    public static List<CSVRecord> parseCsv(MultipartFile file) {
        CSVParser csvParser;
        CSVFormat csvFormat = CSVFormat.DEFAULT
                .builder()
                .setHeader(
                        InvoiceHeader.CUSTOMER.getName(),
                        InvoiceHeader.VAT_NUMBER.getName(),
                        InvoiceHeader.DOCUMENT_NUMBER.getName(),
                        InvoiceHeader.TYPE.getName(),
                        InvoiceHeader.PARENT_DOCUMENT.getName(),
                        InvoiceHeader.CURRENCY.getName(),
                        InvoiceHeader.TOTAL.getName())
                .setIgnoreHeaderCase(true)
                .setSkipHeaderRecord(true)
                .setAllowDuplicateHeaderNames(false)
                .build();
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            csvParser = new CSVParser(fileReader, csvFormat);
            return csvParser.getRecords();
        } catch (IOException ex) {
            throw new CsvParseException("Failed to parse uploaded csv file!", ex);
        }
    }
}
