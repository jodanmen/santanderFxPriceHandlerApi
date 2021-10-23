package com.santander.fxpricehandler.cache;

import com.santander.fxpricehandler.exception.PriceNotAvailableException;
import com.santander.fxpricehandler.model.Price;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class FxPriceCacheIntegrationTest {

    @Autowired
    private FxPriceCache fxPriceCache;

    @Test
    void testCacheFxPrice() {
        LocalDateTime time = LocalDateTime.now();
        Price expectedPrice = new Price(123, "GBP/USD", 100, 200, time);
        fxPriceCache.cacheFxPrice(expectedPrice);
        assertEquals(expectedPrice, fxPriceCache.getPrice("GBP/USD"));
        fxPriceCache.emptyCache();
    }

    @Test
    void testGetUnavailablePrice() {
        Exception exception = assertThrows(PriceNotAvailableException.class, () -> fxPriceCache.getPrice("EUR/USD"));
        String expectedMessage = "Price not available for EUR/USD";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

}