package com.santander.fxpricehandler.service;

import com.santander.fxpricehandler.cache.FxPriceCache;
import com.santander.fxpricehandler.model.Price;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class FxPriceHandlerServiceIntegrationTest {

    @Autowired
    private FxPriceHandlerService fxPriceHandlerService;

    @Autowired
    private FxPriceCache fxPriceCache;

    @Test
    void testGetPriceForMultipleUpdatesOnCurrencyPair() {
        Price expectedPrice = new Price(109, "GBP/USD", 1.2486501, 1.2573561,
                LocalDateTime.parse("01-06-2020 12:01:02:100",
                        DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss:SSS")));
        fxPriceHandlerService.onMessage("106, EUR/USD, 1.1000, 1.2000, 01-06-2020 12:01:01:001\n" +
                "107, EUR/JPY, 119.60, 119.90, 01-06-2020 12:01:01:002\n" +
                "109, GBP/USD, 1.2499,1.2561,01-06-2020 12:01:02:100\n" +
                "110, EUR/JPY, 119.61,119.91,01-06-2020 12:01:02:110");
        assertEquals(expectedPrice, fxPriceHandlerService.getPrice("GBP/USD"));
        fxPriceCache.emptyCache();
    }

    @Test
    void testGeLatestPriceForMultipleUpdatesOnCurrencyPair() {
        Price expectedPrice = new Price(109, "GBP/USD", 1.2486501, 1.2573561,
                LocalDateTime.parse("01-06-2020 12:01:02:100",
                        DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss:SSS")));
        fxPriceHandlerService.onMessage("106, EUR/USD, 1.1000, 1.2000, 01-06-2020 12:01:01:001\n" +
                "107, EUR/JPY, 119.60, 119.90, 01-06-2020 12:01:01:002\n" +
                "109, GBP/USD, 1.2499,1.2561,01-06-2020 12:01:02:100\n" +
                "110, EUR/JPY, 119.61,119.91,01-06-2020 12:01:02:110\n" +
                "108, GBP/USD, 1.2500,1.2560,01-06-2020 12:01:02:002\n");
        assertEquals(expectedPrice, fxPriceHandlerService.getPrice("GBP/USD"));
        fxPriceCache.emptyCache();
    }
}
