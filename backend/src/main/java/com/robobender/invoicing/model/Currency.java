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
public class Currency {

    private String name;
    private BigDecimal rate;

    public Currency(String currency){
        var split = currency.split(":");
        this.name = split[0];
        this.rate = new BigDecimal(split[1]);
    }
}
