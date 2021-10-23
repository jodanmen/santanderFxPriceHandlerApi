package com.santander.fxpricehandler.service;

import com.santander.fxpricehandler.cache.FxPriceCache;
import com.santander.fxpricehandler.model.Price;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FxPriceHandlerServiceTest {

    @Mock
    private FxPriceTransformerService fxPriceTransformerService;

    @Mock
    private FxPriceCache fxPriceCache;

    @InjectMocks
    private FxPriceHandlerService fxPriceHandlerService;

    @Test
    void testOnMessage() {
        Price mockPrice = new Price(106, "EUR/USD", 1.0990, 1.2010,
                LocalDateTime.parse("01-06-2020 12:01:01:001",
                        DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss:SSS")));

        when(fxPriceTransformerService.transform("106, EUR/USD, 1.1000, 1.2000, 01-06-2020 12:01:01:001"))
                .thenReturn(mockPrice);

        fxPriceHandlerService.onMessage("106, EUR/USD, 1.1000, 1.2000, 01-06-2020 12:01:01:001");
        verify(fxPriceCache, times(1)).cacheFxPrice(mockPrice);
    }

    @Test
    void testOnMessageMultipleLines() {
        Price mockPrice1 = new Price(106, "EUR/USD", 1.0990, 1.2010,
                LocalDateTime.parse("01-06-2020 12:01:01:001",
                        DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss:SSS")));

        Price mockPrice2 = new Price(107, "EUR/JPY", 119.4804, 120.0199,
                LocalDateTime.parse("01-06-2020 12:01:01:002",
                        DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss:SSS")));

        when(fxPriceTransformerService.transform("106, EUR/USD, 1.1000, 1.2000, 01-06-2020 12:01:01:001"))
                .thenReturn(mockPrice1);

        when(fxPriceTransformerService.transform("107, EUR/JPY, 119.60, 119.90, 01-06-2020 12:01:01:002"))
                .thenReturn(mockPrice2);

        fxPriceHandlerService.onMessage("106, EUR/USD, 1.1000, 1.2000, 01-06-2020 12:01:01:001\n" +
                "107, EUR/JPY, 119.60, 119.90, 01-06-2020 12:01:01:002");
        verify(fxPriceCache, times(1)).cacheFxPrice(mockPrice1);
        verify(fxPriceCache, times(1)).cacheFxPrice(mockPrice2);
    }

    @Test
    void testOnMessageMultipleUpdatesForCurrencyPair() {
        Price mockPrice1 = new Price(106, "EUR/USD", 1.0990, 1.2010,
                LocalDateTime.parse("01-06-2020 12:01:01:001",
                        DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss:SSS")));

        Price mockPrice2 = new Price(107, "EUR/JPY", 119.4804, 120.0199,
                LocalDateTime.parse("01-06-2020 12:01:01:002",
                        DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss:SSS")));

        when(fxPriceTransformerService.transform("106, EUR/USD, 1.1000, 1.2000, 01-06-2020 12:01:01:001"))
                .thenReturn(mockPrice1);

        when(fxPriceTransformerService.transform("107, EUR/JPY, 119.60, 119.90, 01-06-2020 12:01:01:002"))
                .thenReturn(mockPrice2);

        fxPriceHandlerService.onMessage("106, EUR/USD, 1.1000, 1.2000, 01-06-2020 12:01:01:001\n" +
                "107, EUR/JPY, 119.60, 119.90, 01-06-2020 12:01:01:002\n" +
                "108, GBP/USD, 1.2500,1.2560,01-06-2020 12:01:02:002\n" +
                "109, GBP/USD, 1.2499,1.2561,01-06-2020 12:01:02:100\n" +
                "110, EUR/JPY, 119.61,119.91,01-06-2020 12:01:02:110");
        verify(fxPriceCache, times(1)).cacheFxPrice(mockPrice1);
        verify(fxPriceCache, times(1)).cacheFxPrice(mockPrice2);
    }

    @Test
    void testGetPrice() {
        Price mockPrice = new Price(106, "EUR/USD", 1.0990, 1.2010,
                LocalDateTime.parse("01-06-2020 12:01:01:001",
                        DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss:SSS")));

        when(fxPriceCache.getPrice("EUR/USD")).thenReturn(mockPrice);

        assertEquals(mockPrice, fxPriceHandlerService.getPrice("EUR/USD"));
    }
}