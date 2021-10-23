package com.santander.fxpricehandler.service;

import com.santander.fxpricehandler.exception.PriceTransformationException;
import com.santander.fxpricehandler.model.Price;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

@Service
public class FxPriceTransformerService {

    Price transform(String priceData) {
        String[] priceDataCells = validateAndSplitPriceData(priceData);
        try {
            return new Price(Long.parseLong(priceDataCells[0]), priceDataCells[1].trim(),
                    getBidWithCommissionApplied(priceDataCells[2].trim()),
                    getAskWithCommissionApplied(priceDataCells[3].trim()),
                    LocalDateTime.parse(priceDataCells[4].trim(), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss:SSS")));
        } catch (NumberFormatException | DateTimeParseException e) {
            throw new PriceTransformationException("An error occurred whilst transforming raw price data: " + e);
        }
    }

    private String[] validateAndSplitPriceData(String priceData) {
        Objects.requireNonNull(priceData, "Price data is null and will not processed");
        String[] priceDataCells = priceData.split(",");
        if (priceDataCells.length != 5) {
            throw new IllegalArgumentException("Incorrect format. Price data received is: " + priceData);
        }
        return priceDataCells;
    }

    private double getAskWithCommissionApplied(String askPrice) {
        double ask = Double.parseDouble(askPrice);
        return ask + (ask * 0.001d);
    }

    private double getBidWithCommissionApplied(String bidPrice) {
        double bid = Double.parseDouble(bidPrice);
        return bid - (bid * 0.001d);
    }

}