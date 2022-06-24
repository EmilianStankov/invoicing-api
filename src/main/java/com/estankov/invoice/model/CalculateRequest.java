package com.estankov.invoice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalculateRequest {
    @NotNull
    @NotBlank
    private String file;
    @Pattern(regexp = "^(\\w){3}:\\d*(.\\d+)*$", message = "Exchange rates not valid")
    private List<String> exchangeRates;
    @Pattern(regexp = "(\\w){3}$", message = "Currency not valid")
    private String currency;

    @Nullable
    private String customerVat;
}
