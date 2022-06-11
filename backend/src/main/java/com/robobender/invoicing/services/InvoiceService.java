package com.robobender.invoicing.services;

import com.robobender.invoicing.model.Currencies;
import com.robobender.invoicing.model.Invoice;
import com.robobender.invoicing.model.InvoiceType;
import com.robobender.invoicing.model.TotalPerCustomer;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
public class InvoiceService {

    private final CurrencyService currencyService;

    public InvoiceService(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    public List<TotalPerCustomer> generateReport(String content, String currenciesRaw, long filterByVat, String outputCurrency) throws IOException {
        var invoices = new HashSet<Invoice>();
        var customers = new HashMap<Long, String>();
        var currencies = this.currencyService.parseCurrencies(currenciesRaw);

        for (var line : content.split("\\R")) {
                this.handleLine(line, currencies, invoices, customers);
        }

        invoices.forEach(i -> {
            if (i.getParent() != -1 && !invoices.contains(new Invoice(i.getParent()))) {
                throw new RuntimeException("Parent of invoice is missing: " + i);
            }
        });

        return this.getTotal(filterByVat, outputCurrency, currencies, invoices, customers );
    }

    private void handleLine(String s, Currencies currencies, HashSet<Invoice> invoices, HashMap<Long, String> customers) {
        if (!s.startsWith("Customer")) {
            var invoice = new Invoice(s);
            invoice.setCurrency(currencies.getCurrency(invoice.getCurrencyName()));
            invoices.add(invoice);
            if (!customers.containsKey(invoice.getVatNumber())) {
                customers.put(invoice.getVatNumber(), invoice.getCustomerName());
            }
        }
    }

    private String getCustomerNameByVat(long vat, HashMap<Long, String> customers) {
        if (!customers.containsKey(vat)) {
            throw new RuntimeException("Customer with vat " + vat + " is missing!");
        }
        return customers.get(vat);
    }

    public List<TotalPerCustomer> getTotal(long vat, String currencyName, Currencies currencies, HashSet<Invoice> invoices, HashMap<Long, String> customers) {
        List<TotalPerCustomer> total = new ArrayList<>();
        if (vat == 0) {
            Map<Long, List<Invoice>> invoicesPerCustomer = invoices.stream()
                    .collect(groupingBy(Invoice::getVatNumber));

            for (var vatPerCustomer : invoicesPerCustomer.keySet()) {
                total.add(this.getTotalByVat(vatPerCustomer, currencyName, invoicesPerCustomer.get(vatPerCustomer), currencies, customers));
            }
        } else {
            total.add(this.getTotalByVat(
                    vat,
                    currencyName,
                    invoices.stream().filter(i -> i.getVatNumber() == vat).collect(Collectors.toList()),
                    currencies,
                    customers
            ));
        }

        return total;
    }

    private TotalPerCustomer getTotalByVat(long vat, String currencyName, List<Invoice> invoiceList, Currencies currencies, HashMap<Long, String> customers) {
        var inv = invoiceList.stream()
                .filter(i -> i.getInvoiceType() == InvoiceType.INVOICE).collect(Collectors.toList());
        var totalPerCustomer = new TotalPerCustomer();
        totalPerCustomer.setCustomerVat(vat);
        totalPerCustomer.setCustomerName(getCustomerNameByVat(vat, customers));
        totalPerCustomer.setCurrency(currencyName);
        for (var invoice : inv) {
            var amount = currencyService.fromTo(invoice.getCurrencyName(), currencyName, invoice.getTotal(), currencies);
            var children = invoiceList.stream()
                    .filter(i -> i.getParent() == invoice.getId()).collect(Collectors.toList());
            for (var c : children) {
                if (c.getInvoiceType() == InvoiceType.CREDIT_NOTE) {
                    amount = amount.subtract(currencyService.fromTo(c.getCurrencyName(), currencyName, c.getTotal(), currencies));
                } else if (c.getInvoiceType() == InvoiceType.DEBIT_NOTE) {
                    amount = amount.add(currencyService.fromTo(c.getCurrencyName(), currencyName, c.getTotal(), currencies));
                }
            }
            totalPerCustomer.setTotal(amount.add(totalPerCustomer.getTotal()));
        }

        return totalPerCustomer;
    }
}
