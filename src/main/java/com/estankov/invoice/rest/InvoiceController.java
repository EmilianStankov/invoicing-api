package com.estankov.invoice.rest;

import com.estankov.invoice.model.CalculateRequest;
import com.estankov.invoice.model.CalculateResponse;
import com.estankov.invoice.service.InvoiceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @CrossOrigin(origins = "*")
    @PostMapping(value = "sumInvoices", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CalculateResponse> sumInvoices(@Valid @ModelAttribute CalculateRequest calculateRequest) {
        CalculateResponse response = invoiceService.sumInvoices(calculateRequest);
        if (response.getCustomers().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
