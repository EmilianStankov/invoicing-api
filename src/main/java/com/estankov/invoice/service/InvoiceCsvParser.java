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
import java.util.Arrays;
import java.util.List;

@Service
public class InvoiceCsvParser {

    public List<CSVRecord> parseCsv(MultipartFile file) {
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
                .setSkipHeaderRecord(false)
                .setAllowMissingColumnNames(false)
                .setAllowDuplicateHeaderNames(false)
                .build();
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            csvParser = new CSVParser(fileReader, csvFormat);
            List<CSVRecord> records = csvParser.getRecords();
            validateRecords(csvFormat, records);
            return records.subList(1, records.size());
        } catch (IOException ex) {
            throw new CsvParseException("Failed to parse uploaded csv file!", ex);
        }
    }

    private void validateRecords(CSVFormat csvFormat, List<CSVRecord> records) {
        if (records.size() < 2) {
            throw new CsvParseException("Csv file does not contain invoice data");
        }
        CSVRecord headerRecord = records.get(0);
        if (!headerRecord.isConsistent()) {
            throw new CsvParseException("Csv does not contain data in the correct format");
        }
        Arrays.stream(csvFormat.getHeader()).forEach(header -> {
            if (!headerRecord.isMapped(header)) {
                throw new CsvParseException("Csv does not contain data in the correct format");
            }
        });
    }
}
