package com.santander.fxpricehandler.controller;

import com.santander.fxpricehandler.exception.PriceNotAvailableException;
import com.santander.fxpricehandler.model.Price;
import com.santander.fxpricehandler.service.FxPriceHandlerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.jms.Queue;
import java.util.Objects;

@RestController
public class FxPriceController {

    private FxPriceHandlerService fxPriceHandlerService;

    private JmsTemplate jmsTemplate;

    private Queue fxPriceDataQueue;

    private static final Logger log = LoggerFactory.getLogger(FxPriceController.class);

    @Autowired
    public FxPriceController(FxPriceHandlerService fxPriceHandlerService,
                             JmsTemplate jmsTemplate, Queue fxPriceDataQueue) {
        this.fxPriceHandlerService = fxPriceHandlerService;
        this.jmsTemplate = jmsTemplate;
        this.fxPriceDataQueue = fxPriceDataQueue;
    }

    @GetMapping("/getPrice/{instrumentNameOne}/{instrumentNameTwo}")
    public ResponseEntity<Price> getPrice(@PathVariable String instrumentNameOne,
                                          @PathVariable String instrumentNameTwo) {
        try {
            return new ResponseEntity<>(fxPriceHandlerService.getPrice(instrumentNameOne +
                    "/" + instrumentNameTwo),
                    HttpStatus.OK);
        } catch (PriceNotAvailableException e) {
            log.error("Error retrieving price for currency pairs {} and {}. Cause: {}",
                    instrumentNameOne, instrumentNameTwo, e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/postPrice")
    public ResponseEntity<String> postPrice(@RequestBody String price) {
        Objects.requireNonNull(price);
        try {
            jmsTemplate.convertAndSend(fxPriceDataQueue, price);
            return new ResponseEntity<>("Price successfully processed", HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error occurred processing received price", e);
            return new ResponseEntity<>("Error occurred processing received price: " +
                    price, HttpStatus.BAD_REQUEST);
        }
    }
}
