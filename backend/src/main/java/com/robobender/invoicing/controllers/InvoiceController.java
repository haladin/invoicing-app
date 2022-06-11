package com.robobender.invoicing.controllers;

import com.robobender.invoicing.model.TotalPerCustomer;
import com.robobender.invoicing.services.InvoiceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@ControllerAdvice
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @RequestMapping(value = "/api/invoices", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TotalPerCustomer> index(@RequestParam("file") MultipartFile multipartFile,
                                        @RequestParam("currencies") String currencies,
                                        @RequestParam("outputCurrency") String outputCurrency,
                                        @RequestParam(name = "filterByVat", required = false, defaultValue = "0") String filterByVat) throws IOException {

        try {
            var content = new String(multipartFile.getBytes(), StandardCharsets.UTF_8);
            return this.invoiceService.generateReport(content,currencies, Long.parseLong(filterByVat), outputCurrency);
        } catch (Exception ex){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }
    }
}
