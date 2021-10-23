package com.santander.fxpricehandler.service;

import com.santander.fxpricehandler.cache.FxPriceCache;
import com.santander.fxpricehandler.exception.PriceNotAvailableException;
import com.santander.fxpricehandler.exception.PriceTransformationException;
import com.santander.fxpricehandler.model.Price;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class FxPriceHandlerService {

    private FxPriceTransformerService fxPriceTransformerService;

    private FxPriceCache fxPriceCache;

    private static final Logger log = LoggerFactory.getLogger(FxPriceHandlerService.class);

    @Autowired
    public FxPriceHandlerService(FxPriceTransformerService fxPriceTransformerService, FxPriceCache fxPriceCache) {
        this.fxPriceTransformerService = fxPriceTransformerService;
        this.fxPriceCache = fxPriceCache;
    }

    @JmsListener(destination = "fxPriceData.queue")
    public void onMessage(String priceData) {
        if (null != priceData && !priceData.isEmpty()) {
            String[] priceDataMessages = priceData.split("\\R");
            for (String aPriceData : priceDataMessages) {
                try {
                    fxPriceCache.cacheFxPrice(fxPriceTransformerService.transform(aPriceData));
                } catch (PriceTransformationException e) {
                    log.error("Error handling raw price data: " + aPriceData + " | Error is : " + e.getMessage());
                }
            }
        } else {
            log.error("Empty price data message received on " + LocalDateTime.now());
        }
    }

    public Price getPrice(String instrumentName) {
        Price price = fxPriceCache.getPrice(instrumentName);
        if (price == null) {
            throw new PriceNotAvailableException("Price not available for " + instrumentName);
        }
        return fxPriceCache.getPrice(instrumentName);
    }
}
