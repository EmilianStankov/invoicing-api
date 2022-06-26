package com.estankov.invoice.model;

/**
 * Enumerates the expected columns in an invoice .csv
 */
public enum InvoiceHeader {

    CUSTOMER("Customer"),
    VAT_NUMBER("Vat number"),
    DOCUMENT_NUMBER("Document number"),
    TYPE("Type"),
    PARENT_DOCUMENT("Parent document"),
    CURRENCY("Currency"),
    TOTAL("Total");

    private final String name;

    InvoiceHeader(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
