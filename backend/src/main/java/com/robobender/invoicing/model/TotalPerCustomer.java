package com.robobender.invoicing.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TotalPerCustomer {

    private String customerName;
    private long customerVat;
    private BigDecimal total = new BigDecimal("0.0");
    private String currency;
}
