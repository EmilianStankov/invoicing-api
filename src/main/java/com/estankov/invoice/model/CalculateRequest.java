package com.estankov.invoice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.List;

/**
 * An invoice calculation multipart request
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalculateRequest {

    private static final String EXCHANGE_RATES_PATTERN = "^((\\w){3}:\\d*(.\\d*))$";
    private static final String CURRENCY_PATTERN = "^(\\w){3}$";

    private MultipartFile file;
    private List<@NotBlank @Pattern(regexp = EXCHANGE_RATES_PATTERN, message = "Exchange rates not valid") String> exchangeRates;
    @Pattern(regexp = CURRENCY_PATTERN, message = "Currency not valid")
    private String outputCurrency;

    @Nullable
    @Pattern(regexp = "^\\d+$")
    private String customerVat;
}
