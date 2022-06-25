package com.estankov.invoice.service;

import com.estankov.invoice.exception.DataFormatException;
import com.estankov.invoice.exception.InconsistentDataException;
import com.estankov.invoice.model.CalculateRequest;
import com.estankov.invoice.model.InvoiceDocument;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DocumentConverterTest {

    private final DocumentConverter objectUnderTest;

    DocumentConverterTest() {
        InvoiceCsvParser csvParser = new InvoiceCsvParser();
        objectUnderTest = new DocumentConverter(csvParser);
    }

    @Test
    void testConvertToDocuments_validFile() {
        final String fileContent = """
                Customer,Vat number,Document number,Type,Parent document,Currency,Total
                Customer1,123456789,1,1,,EUR,1000
                Customer1,123456789,2,2,1,USD,100
                Customer1,123456789,3,3,1,BGN,500
                """;
        final MockMultipartFile file = new MockMultipartFile("data.csv", fileContent.getBytes(StandardCharsets.UTF_8));

        final List<InvoiceDocument> documents = objectUnderTest.convertToDocuments(
                new CalculateRequest(
                        file,
                        List.of("EUR:1", "BGN:1.95", "USD:0.987"),
                        "EUR",
                        null
                )
        );

        assertEquals(3, documents.size());

        assertEquals("Customer1", documents.get(0).getCustomer());
        assertEquals(123456789, documents.get(0).getVatNumber());
        assertEquals(1, documents.get(0).getDocumentNumber());
        assertEquals(InvoiceDocument.Type.INVOICE, documents.get(0).getType());
        assertNull(documents.get(0).getParentDocument());
        assertEquals("EUR", documents.get(0).getCurrency());
        assertEquals(new BigDecimal("1000"), documents.get(0).getTotal());

        assertEquals("Customer1", documents.get(1).getCustomer());
        assertEquals(123456789, documents.get(1).getVatNumber());
        assertEquals(2, documents.get(1).getDocumentNumber());
        assertEquals(InvoiceDocument.Type.CREDIT_NOTE, documents.get(1).getType());
        assertEquals(1, documents.get(1).getParentDocument());
        assertEquals("USD", documents.get(1).getCurrency());
        assertEquals(new BigDecimal("100"), documents.get(1).getTotal());

        assertEquals("Customer1", documents.get(2).getCustomer());
        assertEquals(123456789, documents.get(2).getVatNumber());
        assertEquals(3, documents.get(2).getDocumentNumber());
        assertEquals(InvoiceDocument.Type.DEBIT_NOTE, documents.get(2).getType());
        assertEquals(1, documents.get(2).getParentDocument());
        assertEquals("BGN", documents.get(2).getCurrency());
        assertEquals(new BigDecimal("500"), documents.get(2).getTotal());
    }

    @Test
    void testConvertToDocuments_invalidDataFormat() {
        final String fileContent = """
                Customer,Vat number,Document number,Type,Parent document,Currency,Total
                Customer1,123456789a,1,1,,EUR,1000
                Customer1,123456789,2,2,1,USD,100
                Customer1,123456789,3,3,1,BGN,500
                """;
        final MockMultipartFile file = new MockMultipartFile("data.csv", fileContent.getBytes(StandardCharsets.UTF_8));

        assertThrows(DataFormatException.class, () -> objectUnderTest.convertToDocuments(
                new CalculateRequest(
                        file,
                        List.of("EUR:1", "BGN:1.95", "USD:0.987"),
                        "EUR",
                        null
                )
        ));
    }

    @Test
    void testConvertToDocuments_invalidParentDocument() {
        final String fileContent = """
                Customer,Vat number,Document number,Type,Parent document,Currency,Total
                Customer1,123456789,1,1,,EUR,1000
                Customer1,123456789,2,2,8,USD,100
                Customer1,123456789,3,3,1,BGN,500
                """;
        final MockMultipartFile file = new MockMultipartFile("data.csv", fileContent.getBytes(StandardCharsets.UTF_8));

        assertThrows(InconsistentDataException.class, () -> objectUnderTest.convertToDocuments(
                new CalculateRequest(
                        file,
                        List.of("EUR:1", "BGN:1.95", "USD:0.987"),
                        "EUR",
                        null
                )
        ));
    }

    @Test
    void testConvertToDocuments_duplicateDocumentNumber() {
        final String fileContent = """
                Customer,Vat number,Document number,Type,Parent document,Currency,Total
                Customer1,123456789,1,1,,EUR,1000
                Customer1,123456789,1,2,1,USD,100
                Customer1,123456789,1,3,1,BGN,500
                """;
        final MockMultipartFile file = new MockMultipartFile("data.csv", fileContent.getBytes(StandardCharsets.UTF_8));

        assertThrows(InconsistentDataException.class, () -> objectUnderTest.convertToDocuments(
                new CalculateRequest(
                        file,
                        List.of("EUR:1", "BGN:1.95", "USD:0.987"),
                        "EUR",
                        null
                )
        ));
    }
}
