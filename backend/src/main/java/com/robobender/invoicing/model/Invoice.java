package com.robobender.invoicing.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
public class Invoice {
    private long id;
    private long vatNumber;
    private InvoiceType invoiceType;
    private String customerName;
    private Currency currency;
    private String currencyName;
    private BigDecimal total;
    private long parent = -1;

    public Invoice(long id){
        this.id = id;
    }

    public Invoice(String s){
        var parts = s.split(",");
        this.customerName = parts[0];
        this.vatNumber = Long.parseLong(parts[1]);
        this.id = Long.parseLong(parts[2]);
        this.invoiceType = InvoiceType.values()[Integer.parseInt(parts[3]) - 1];
        try {
            this.parent = Long.parseLong(parts[4]);
        } catch (NumberFormatException ex){

        }
        this.currencyName = parts[5];
        this.total = new BigDecimal(parts[6]);
    }

    @Override
    public String toString() {
        return "Invoice{" +
                "id=" + id +
                ", vatNumber=" + vatNumber +
                ", invoiceType=" + invoiceType +
                ", customerName='" + customerName + '\'' +
                ", currency=" + currency +
                ", currencyName='" + currencyName + '\'' +
                ", total=" + total +
                ", parent=" + parent +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Invoice invoice = (Invoice) o;

        return id == invoice.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
