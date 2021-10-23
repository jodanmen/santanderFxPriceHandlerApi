package com.santander.fxpricehandler.service;

import com.santander.fxpricehandler.exception.PriceTransformationException;
import com.santander.fxpricehandler.model.Price;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

class FxPriceTransformerServiceTest {

    @Test
    void testTransformRawPriceDataToPriceObject() {
        Price expectedPrice = new Price(106, "EUR/USD", 1.0989, 1.2012,
                LocalDateTime.parse("01-06-2020 12:01:01:001",
                        DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss:SSS")));
        FxPriceTransformerService fxPriceTransformerService = new FxPriceTransformerService();
        Price actualPrice = fxPriceTransformerService.transform("106, EUR/USD, 1.1000, 1.2000, 01-06-2020 12:01:01:001");
        assertEquals(expectedPrice, actualPrice);
    }

    @Test
    void testTransformNullPriceData() {
        FxPriceTransformerService fxPriceTransformerService = new FxPriceTransformerService();
        Exception exception = assertThrows(NullPointerException.class, () ->
                fxPriceTransformerService.transform(null));
        String expectedMessage = "Price data is null and will not processed";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testTransformEmptyPriceData() {
        FxPriceTransformerService fxPriceTransformerService = new FxPriceTransformerService();
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                fxPriceTransformerService.transform(""));
        String expectedMessage = "Incorrect format. Price data received is: ";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testTransformIncorrectFormatPriceData() {
        FxPriceTransformerService fxPriceTransformerService = new FxPriceTransformerService();
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                fxPriceTransformerService.transform("106, EUR/USD"));
        String expectedMessage = "Incorrect format. Price data received is: 106, EUR/USD";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testTransformBadRawPriceData() {
        FxPriceTransformerService fxPriceTransformerService = new FxPriceTransformerService();
        Exception exception = assertThrows(PriceTransformationException.class, () ->
                fxPriceTransformerService.transform("106, EUR/USD, 1.1000, 1.2z00, 01-06-2020 12:01:01:001"));
        String expectedMessage = "An error occurred whilst transforming raw price data: java.lang.NumberFormatException";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

}