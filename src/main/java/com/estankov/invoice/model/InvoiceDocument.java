package com.estankov.invoice.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Arrays;

@Data
@Builder
public class InvoiceDocument {

    private String customer;
    private Integer vatNumber;
    private Integer documentNumber;
    private Type type;
    private Integer parentDocument;
    private String currency;
    private BigDecimal total;

    public enum Type {
        INVOICE(1),
        CREDIT_NOTE(2),
        DEBIT_NOTE(3);

        private final int value;

        Type(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static Type getType(String value) {
            try {
                return Arrays.stream(Type.values())
                        .filter(type -> type.getValue() == Integer.parseInt(value))
                        .findAny()
                        .orElse(null);
            } catch (NumberFormatException e) {
                return null;
            }
        }

    }
}
