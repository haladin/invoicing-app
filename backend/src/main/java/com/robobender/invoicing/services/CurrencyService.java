package com.robobender.invoicing.services;

import com.robobender.invoicing.model.Currencies;
import com.robobender.invoicing.model.Currency;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

@Service
public class CurrencyService {

    public Currencies parseCurrencies(String currenciesRaw) {

        var currencies = new Currencies();

        try {
            Arrays.asList(currenciesRaw.split(",")).forEach(
                    currency -> {
                        var parsed = new Currency(currency);
                        currencies.getCurrencies().add(parsed);
                        if (parsed.getRate().compareTo(new BigDecimal("1.0")) == 0) {
                            if (currencies.getBaseCurrency() == null) {
                                currencies.setBaseCurrency(parsed);
                            } else {
                                throw new RuntimeException("Two base currencies!");
                            }
                        }
                    }
            );
        } catch (Exception ex) {
            throw new RuntimeException("Unable to parse currencies rates list!");
        }

        if (currencies.getBaseCurrency() == null) {
            throw new RuntimeException("No base currency!");
        }
        return currencies;
    }

    public BigDecimal fromTo(String cFrom, String cTo, BigDecimal amount, Currencies currencies) {
        var currencyFrom = currencies.getCurrency(cFrom);
        var currencyTo = currencies.getCurrency(cTo);

        return (amount.multiply(currencyFrom.getRate())).divide(currencyTo.getRate(), 2, RoundingMode.HALF_UP);
    }
}
