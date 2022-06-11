package com.robobender.invoicing;

import com.robobender.invoicing.services.CurrencyService;
import com.robobender.invoicing.services.InvoiceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class InvoicingApplicationTests {

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private CurrencyService currencyService;

    private static final String currencies = "EUR:1,USD:0.94,GBP:1.17,BGN:0.51";

    private static final String data = "Customer,Vat number,Document number,Type,Parent document,Currency,Total\n" +
            "Vendor 1,123456789,1000000257,1,,USD,400\n" +
            "Vendor 2,987654321,1000000258,1,,EUR,900\n" +
            "Vendor 3,123465123,1000000259,1,,GBP,1300\n" +
            "Vendor 1,123456789,1000000260,2,1000000257,EUR,100\n" +
            "Vendor 1,123456789,1000000261,3,1000000257,GBP,50\n" +
            "Vendor 2,987654321,1000000262,2,1000000258,USD,200\n" +
            "Vendor 3,123465123,1000000263,3,1000000259,EUR,100\n" +
            "Vendor 1,123456789,1000000264,1,,EUR,1600\n" +
            "vendor 4,1234,1,1,,USD,100\n" +
            "vendor 4,1234,2,2,1,EUR,20\n" +
            "vendor 4,1234,3,1,,USD,80";

    @Test
    void testGetCurrencyByName() {
        var currencies = currencyService.parseCurrencies(InvoicingApplicationTests.currencies);
        assertEquals("BGN", currencies.getCurrency("BGN").getName(), () -> "Currency not found");
        assertEquals(0, currencies.getCurrency("BGN").getRate().compareTo(new BigDecimal("0.51")), () -> "Currency rate is different");
    }

    @Test
    void testMissingCurrency() {
        var currencies = currencyService.parseCurrencies(InvoicingApplicationTests.currencies);
        var exceptionIsThrown = false;
        try {
            currencyService.fromTo("GBP", "NNN", new BigDecimal("100"), currencies);
        } catch (RuntimeException ex) {
            exceptionIsThrown = true;
            assertEquals("Currency NNN not found", ex.getMessage());
        }

        assertTrue(exceptionIsThrown);
    }

    @Test
    void testCurrencyConversion() {
        var currencies = currencyService.parseCurrencies(InvoicingApplicationTests.currencies);
        assertEquals(0, currencyService.fromTo("GBP", "BGN", new BigDecimal("100"), currencies).compareTo(new BigDecimal("229.41")));
    }

    @Test
    void testGetTotal() throws IOException {
        MultipartFile multipartFile = new MockMultipartFile("sourceFile.tmp", data.getBytes());
        var content = new String(multipartFile.getBytes(), StandardCharsets.UTF_8);

        var customers = this.invoiceService.generateReport(content, currencies, 0, "GBP");

        assertEquals(4, customers.size());
    }

    @Test
    void testGetTotalByVat() throws IOException {
        MultipartFile multipartFile = new MockMultipartFile("sourceFile.tmp", data.getBytes());
        var content = new String(multipartFile.getBytes(), StandardCharsets.UTF_8);

        var customers = this.invoiceService.generateReport(content, currencies, 1234, "GBP");

        assertEquals(1, customers.size());
        assertEquals(0, customers.get(0).getTotal().compareTo(new BigDecimal("127.52")));
    }

}
