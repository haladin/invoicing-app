package com.robobender.invoicing.model;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class Currencies {

    private Currency baseCurrency;
    private Set<Currency> currencies = new HashSet<Currency>();

    public Currency getCurrency(String name) {
        var currency = this.currencies.stream()
                .filter(c -> c.getName().equals(name))
                .findAny()
                .orElse(null);

        if (currency == null) {
            throw new RuntimeException("Currency " + name + " not found");
        }

        return currency;
    }

}
