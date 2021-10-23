package com.santander.fxpricehandler.cache;

import com.santander.fxpricehandler.exception.PriceNotAvailableException;
import com.santander.fxpricehandler.model.Price;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class FxPriceCache {

    private Map<String, Price> fxPriceCache = new ConcurrentHashMap<>();

    @Value("${fxPriceCache.ttl}")
    private int dataExpiryPeriod = 1;

    private static final Logger log = LoggerFactory.getLogger(FxPriceCache.class);

    public void cacheFxPrice(Price price) {
        if (fxPriceCache.containsKey(price.getInstrumentName())
                && fxPriceCache.get(price.getInstrumentName()).getId() < price.getId()) {
            fxPriceCache.put(price.getInstrumentName(), price);
            return;
        } else if (fxPriceCache.containsKey(price.getInstrumentName())){
            log.info("Received a price out of date for instrument {}. Ignoring price for ID: {}",
                    price.getInstrumentName() ,price.getId());
            return;
        }
        fxPriceCache.put(price.getInstrumentName(), price);
    }

    public Price getPrice(String instrumentName) {
        if (null == fxPriceCache.get(instrumentName)) {
            throw new PriceNotAvailableException("Price not available for " + instrumentName);
        }
        return fxPriceCache.get(instrumentName);
    }

    public void emptyCache() {
        fxPriceCache.clear();
    }

    @Scheduled(cron = "${fxPriceCache.expiry.refreshRate}")
    public void evictExpiredPrices() {
        fxPriceCache.forEach((key, value) -> {
            if (ChronoUnit.DAYS.between(value.getTimestamp(), LocalDateTime.now()) > dataExpiryPeriod) {
                log.info("Removing expired price data for {} from cache", value.getInstrumentName());
                fxPriceCache.remove(key);
            }
        });
    }

}
