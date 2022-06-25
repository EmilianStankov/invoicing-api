package com.estankov.invoice.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockPart;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class InvoiceControllerTest {

    public static final String VALID_FILE_CONTENT =
            """
            Customer,Vat number,Document number,Type,Parent document,Currency,Total
            Customer1,123456789,1,1,,EUR,1000
            Customer1,123456789,2,2,1,USD,100
            Customer1,123456789,3,3,1,BGN,500
            """;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void testSumInvoices_success() throws Exception {
        performSumInvoices(VALID_FILE_CONTENT, "EUR")
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void testSumInvoices_badRequest_emptyFile() throws Exception {
        final String fileContent =
                """
                Customer,Vat number,Document number,Type,Parent document,Currency,Total
                """;

        performSumInvoices(fileContent, "BGN")
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message").value("Csv file does not contain invoice data"));
    }

    @Test
    void testSumInvoices_badRequest_unsupportedCsv() throws Exception {
        final String fileContent =
                """
                a,b,c,d
                1,2,3,4
                5,6,7,8
                """;

        performSumInvoices(fileContent, "USD")
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message").value("Csv does not contain data in the correct format"));
    }

    @Test
    void testSumInvoices_badRequest_incorrectData() throws Exception {
        final String fileContent =
                """
                Customer,Vat number,Document number,Type,Parent document,Currency,Total
                Customer1,123456789a,1,1,,EUR,1000
                Customer1,123456789,2,2,1,USD,100
                Customer1,123456789,3,3,1,BGN,500
                """;

        performSumInvoices(fileContent, "BGN")
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message").value("CSV data is not in the correct format: For input string: \"123456789a\""));
    }

    @Test
    void testSumInvoices_badRequest_unsupportedCurrency() throws Exception {
        final String fileContent =
                """
                Customer,Vat number,Document number,Type,Parent document,Currency,Total
                Customer1,123456789,1,1,,JPY,1000
                Customer1,123456789,2,2,1,USD,100
                Customer1,123456789,3,3,1,BGN,500
                """;

        performSumInvoices(fileContent, "USD")
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message").value("Currency JPY is not supported"));
    }

    @Test
    void testSumInvoices_badRequest_unsupportedOutputCurrency() throws Exception {
        performSumInvoices(VALID_FILE_CONTENT, "JPY")
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message").value("Currency: JPY not found in the supplied list of exchange rates"));
    }

    @Test
    void testSumInvoices_filtered() throws Exception {
        performSumInvoicesFiltered("123456789")
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void testSumInvoices_filtered_notFount() throws Exception {
        performSumInvoicesFiltered("234567891")
                .andExpect(status().isNotFound());
    }

    private ResultActions performSumInvoices(String fileContent, String outputCurrency) throws Exception {
        return mockMvc.perform(
                multipart("/sumInvoices")
                        .part(new MockPart("file", "data.csv", fileContent.getBytes(StandardCharsets.UTF_8)))
                        .part(new MockPart("exchangeRates", "EUR:1,USD:0.987,BGN:1.95".getBytes(StandardCharsets.UTF_8)))
                        .part(new MockPart("outputCurrency", outputCurrency.getBytes(StandardCharsets.UTF_8))));
    }


    private ResultActions performSumInvoicesFiltered(String customerVat) throws Exception {
        return mockMvc.perform(
                multipart("/sumInvoices")
                        .part(new MockPart("file", "data.csv", VALID_FILE_CONTENT.getBytes(StandardCharsets.UTF_8)))
                        .part(new MockPart("exchangeRates", "EUR:1,USD:0.987,BGN:1.95".getBytes(StandardCharsets.UTF_8)))
                        .part(new MockPart("outputCurrency", "EUR".getBytes(StandardCharsets.UTF_8)))
                        .part(new MockPart("customerVat", customerVat.getBytes(StandardCharsets.UTF_8))));
    }
}
